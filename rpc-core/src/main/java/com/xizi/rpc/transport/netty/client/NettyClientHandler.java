package com.xizi.rpc.transport.netty.client;

import com.xizi.rpc.entity.RpcRequest;
import com.xizi.rpc.entity.RpcResponse;
import com.xizi.rpc.factory.SingletonFactory;
import com.xizi.rpc.serializer.CommonSerializer;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.net.InetSocketAddress;
import java.util.Objects;

/**
 * Netty客户端侧处理器
 * @author xizizzz
 */
public class NettyClientHandler extends SimpleChannelInboundHandler<RpcResponse> {

    private static final Logger logger = LoggerFactory.getLogger(NettyClientHandler.class);
    //单例工厂创建未处理的请求
    private final UnprocessedRequests unprocessedRequests;

    public NettyClientHandler() {
        this.unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequests.class);
    }

   // 当通道channel有读取事件 会触发
   // channel 将 RpcRequest 对象写出 并且等待服务端返回的结果RpcResponse。
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse msg) {
        try {
            logger.info(String.format("客户端接收到消息: %s", msg));
            //本地map根据request_id删除未处理的请求,并且从map中拿到CompletableFuture<RpcResponse> future进行执行complete(msg)将客户端接收到的消息返回
            unprocessedRequests.complete(msg);
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    //异常发送ChannelHandler
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("过程调用时有错误发生:");
        cause.printStackTrace();
        ctx.close();
    }

    //发送心跳包
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.WRITER_IDLE) {
                logger.info("客户端发送心跳包 [{}]", ctx.channel().remoteAddress());
                Channel channel = ChannelProvider.get((InetSocketAddress) ctx.channel().remoteAddress(), Objects.requireNonNull(CommonSerializer.getByCode(CommonSerializer.DEFAULT_SERIALIZER)));
                RpcRequest rpcRequest = new RpcRequest();
                //设置心跳包为true
                rpcRequest.setHeartBeat(true);
                assert channel != null;
                channel.writeAndFlush(rpcRequest).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }


}
