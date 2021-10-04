package com.xizi.rpc.transport.netty.server;

import com.xizi.rpc.codec.CommonDecoder;
import com.xizi.rpc.codec.CommonEncoder;
import com.xizi.rpc.hook.ShutdownHook;
import com.xizi.rpc.provider.ServiceProviderImpl;
import com.xizi.rpc.registry.NacosServiceRegistry;
import com.xizi.rpc.serializer.CommonSerializer;
import com.xizi.rpc.transport.AbstractRpcServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;


import java.util.concurrent.TimeUnit;

/**
 * NIO方式服务提供端
 * 在创建 RpcServer 对象时，传入一个 ServiceRegistry 作为这个服务的注册表。
 * @author xizizzz
 */

//继承AbstractRpcServer
public class NettyServer extends AbstractRpcServer {

    private final CommonSerializer serializer;

    //使用默认序列化协议 kryo
    public NettyServer(String host, int port) {
        this(host, port, DEFAULT_SERIALIZER);
    }

    //设置主机+端口号+自定义序列化协议
    public NettyServer(String host, int port, Integer serializer) {
        this.host = host;
        this.port = port;
        serviceRegistry = new NacosServiceRegistry(); // Nacos服务注册
        serviceProvider = new ServiceProviderImpl();  // 默认的服务注册表，保存服务端本地服务
        this.serializer = CommonSerializer.getByCode(serializer); //获取序列化器的自定义数字
        scanServices(); //扫描使用自定义注解的服务
    }

    //开启netty服务端
    @Override
    public void start() {
        // 这样在 RpcServer 启动之前，只需要调用 addClearAllHook，就可以注册这个钩子了。
        // 使用钩子 关闭后将清除Nacos中全部的服务和关闭线程池
        // 启动服务端后再关闭，就会发现 Nacos 中的注册信息都被注销了。
        ShutdownHook.getShutdownHook().addClearAllHook();
        // 1.bossGroup 用于接收连接，workerGroup 用于具体的处理
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            //2.创建服务端启动引导/辅助类：ServerBootstrap
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            //3.给引导类配置两大线程组,确定了线程模型
            serverBootstrap.group(bossGroup, workerGroup)
                    // 4.指定 IO 模型
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO)) // 打印日志
                    .option(ChannelOption.SO_BACKLOG, 256) //标识当服务器请求处理线程全满时，用于临时存放已完成三次握手的请求的队列的最大长度
                    .option(ChannelOption.SO_KEEPALIVE, true) //开启心跳
                    .childOption(ChannelOption.TCP_NODELAY, true) //如果要求高实时性，有数据发送时就马上发送，就将该选项设置为true关闭Nagle算法；
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            //5.可以自定义客户端消息的业务处理逻辑
                            //服务端心跳机制，每30秒检查一下读事件是否发生
                            pipeline.addLast(new IdleStateHandler(30, 0, 0, TimeUnit.SECONDS))
                                    .addLast(new CommonEncoder(serializer)) //编码器
                                    .addLast(new CommonDecoder()) //解码器
                                    .addLast(new NettyServerHandler());  //Netty中处理RpcRequest的Handler处理
                        }
                    });
            // 6.绑定端口+IP,调用 sync 方法阻塞知道绑定完成
            ChannelFuture future = serverBootstrap.bind(host, port).sync();
            // 7.阻塞等待直到服务器Channel关闭
            future.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            logger.error("启动服务器时有错误发生: ", e);
        } finally {
            //8.优雅关闭相关线程组资源
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

}
