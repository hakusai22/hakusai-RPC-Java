package com.hakusai.rpc.transport.netty.client;

import com.hakusai.rpc.entity.RpcRequest;
import com.hakusai.rpc.entity.RpcResponse;
import com.hakusai.rpc.enumeration.RpcError;
import com.hakusai.rpc.exception.RpcException;
import com.hakusai.rpc.factory.SingletonFactory;
import com.hakusai.rpc.loadbanlancer.LoadBalancer;
import com.hakusai.rpc.loadbanlancer.RandomLoadBalancer;
import com.hakusai.rpc.registry.NacosServiceDiscovery;
import com.hakusai.rpc.registry.ServiceDiscovery;
import com.hakusai.rpc.serializer.CommonSerializer;
import com.hakusai.rpc.transport.RpcClient;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.net.InetSocketAddress;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * NIO方式消费侧 客户端类
 *
 * @author hakusai22@qq.com
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
        if (Objects.isNull(serializer)) {
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
            assert channel != null;
            if (!channel.isActive()) {
                group.shutdownGracefully();
                return null;
            }
            // 存放到未处理的请求
            unprocessedRequests.put(rpcRequest.getRequestId(), resultFuture);
            //监听通道发送的数据
            channel.writeAndFlush(rpcRequest).addListener((ChannelFutureListener) future1 -> {
                if (future1.isSuccess()) {
                    logger.info(String.format("客户端成功发送消息: %s", rpcRequest));
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
