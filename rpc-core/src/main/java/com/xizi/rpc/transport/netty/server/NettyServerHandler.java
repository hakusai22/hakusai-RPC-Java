package com.xizi.rpc.transport.netty.server;

import com.xizi.rpc.entity.RpcRequest;
import com.xizi.rpc.entity.RpcResponse;
import com.xizi.rpc.factory.SingletonFactory;
import com.xizi.rpc.handler.RequestHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Netty中处理RpcRequest的Handler
 * NettyServerhandler 用于接收 RpcRequest，并且执行调用，将调用结果返回封装成 RpcResponse 发送出去。
 *
 * @author xizizzz
 */
public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> {
    private static final Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);

    //服务端的请求处理器(封装了方法的执行)
    private final RequestHandler requestHandler;

    public NettyServerHandler() {
        //使用自定义单例工厂创建请求处理对象
        this.requestHandler = SingletonFactory.getInstance(RequestHandler.class);
    }

    /**
        1. ChannelHandlerContext 上下文对象 含有管道pipline 通道channel 地址
        2. msg 客户端发送的数据
     */
    //当服务端接收到RpcRequest请求就会触发
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest msg) throws Exception {
        try {
            //判断请求是否是心跳包
            if(msg.getHeartBeat()) {
                logger.info("接收到客户端心跳包...");
                return;
            }
            logger.info("服务器接收到请求: {}", msg);
            //RpcRequest请求的接口名称获取服务 进行调用目标方法invoke()
            Object result = requestHandler.handle(msg);
            logger.info("结果: {}", result);
            //判断通道是否可用
            if (ctx.channel().isActive() && ctx.channel().isWritable()) {
                // 将result结果数据写到通道中去
                ctx.writeAndFlush(RpcResponse.success(result, msg.getRequestId()));
            } else {
                logger.error("通道不可写");
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    //异常处理
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("处理过程调用时有错误发生:");
        cause.printStackTrace();
        ctx.close();
    }

    //心跳机制 用户事件触发
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        // 事件是空闲状态事件
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            // 读取空闲
            if (state == IdleState.READER_IDLE) {
                logger.info("长时间未收到心跳包，断开连接...");
                ctx.close();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }


}
