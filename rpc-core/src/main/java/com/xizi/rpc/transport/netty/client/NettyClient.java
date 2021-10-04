package com.xizi.rpc.transport.netty.client;

import com.xizi.rpc.entity.RpcRequest;
import com.xizi.rpc.entity.RpcResponse;
import com.xizi.rpc.enumeration.RpcError;
import com.xizi.rpc.exception.RpcException;
import com.xizi.rpc.factory.SingletonFactory;
import com.xizi.rpc.loadbanlancer.LoadBalancer;
import com.xizi.rpc.loadbanlancer.RandomLoadBalancer;
import com.xizi.rpc.registry.NacosServiceDiscovery;
import com.xizi.rpc.registry.ServiceDiscovery;
import com.xizi.rpc.serializer.CommonSerializer;
import com.xizi.rpc.transport.RpcClient;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;

/**
 * NIO方式消费侧 客户端类
 * @author xizizzz
 */
public class NettyClient implements RpcClient {

    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);
    private static final EventLoopGroup group;
    private static final Bootstrap bootstrap;

    //在静态代码块中就直接配置好了 Netty 客户端，等待发送数据时启动
    static {
        //1.创建一个 NioEventLoopGroup 对象实例
        group = new NioEventLoopGroup();
        //2.创建客户端启动引导/辅助类：Bootstrap
        bootstrap = new Bootstrap();
        //3.指定线程组
        bootstrap.group(group)
                //4.指定 IO 模型
                .channel(NioSocketChannel.class);
    }

    //服务发现接口
    private final ServiceDiscovery serviceDiscovery;
    //序列化协议
    private final CommonSerializer serializer;
    //未处理的请求
    private final UnprocessedRequests unprocessedRequests;

    public NettyClient() {
        this(DEFAULT_SERIALIZER, new RandomLoadBalancer());
    }

    public NettyClient(LoadBalancer loadBalancer) {
        this(DEFAULT_SERIALIZER, loadBalancer);
    }

    public NettyClient(Integer serializer) {
        this(serializer, new RandomLoadBalancer());
    }

    public NettyClient(Integer serializer, LoadBalancer loadBalancer) {
        this.serviceDiscovery = new NacosServiceDiscovery(loadBalancer);
        this.serializer = CommonSerializer.getByCode(serializer);
        this.unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequests.class);
    }

    //客户端根据请求参数发送请求
    @Override
    public CompletableFuture<RpcResponse> sendRequest(RpcRequest rpcRequest) {
        if (serializer == null) {
            logger.error("未设置序列化器");
            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
        }
        //CompletableFuture实现异步获取结果并且等待所有异步任务完成
        CompletableFuture<RpcResponse> resultFuture = new CompletableFuture<>();
        try {
            // 根据服务名称查找服务实体
            InetSocketAddress inetSocketAddress = serviceDiscovery.lookupService(rpcRequest.getInterfaceName());
            // 根据InetSocketAddress和序列化调用get()获取Channel
            Channel channel = ChannelProvider.get(inetSocketAddress, serializer);
            //判读channel通道是否活跃
            if (!channel.isActive()) {
                group.shutdownGracefully();
                return null;
            }
            // 存放到未处理的请求
            unprocessedRequests.put(rpcRequest.getRequestId(), resultFuture);
            //监听通道发送的数据
            channel.writeAndFlush(rpcRequest).addListener((ChannelFutureListener) future1 -> {
                if (future1.isSuccess()) {
                    logger.info(String.format("客户端发送消息: %s", rpcRequest.toString()));
                } else {
                    future1.channel().close();
                    resultFuture.completeExceptionally(future1.cause());
                    logger.error("发送消息时有错误发生: ", future1.cause());
                }
            });
        } catch (InterruptedException e) {
            unprocessedRequests.remove(rpcRequest.getRequestId());
            logger.error(e.getMessage(), e);
            Thread.currentThread().interrupt();
        }
        return resultFuture;
    }
}
