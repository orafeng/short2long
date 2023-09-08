package com.base.short2long.netty;

import com.base.short2long.request.RequestDealer;
import com.base.short2long.utils.CommonFunction;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@Component
@ChannelHandler.Sharable
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    RequestDealer requestDealer;

    ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(100);

    /**
     * 客户端连接会触发
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("channel {} channelActive......", ctx.channel());
    }

    /**
     * 客户端发消息会触发
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            logger.info("channel {} channelRead......", ctx.channel());
            ByteBuf byteBufIn = (ByteBuf) msg;
            byte[] bytesIn = new byte[byteBufIn.readableBytes()];
            byteBufIn.readBytes(bytesIn);

            logger.info("messageIn: " + CommonFunction.printMsg(bytesIn));
            if (bytesIn.length > 0) {
                NettyMessage nettyMessage = new NettyMessage();
                nettyMessage.setBytesIn(bytesIn);
                nettyMessage.setCtx(ctx);
                executor.execute(() -> {
                    requestDealer.handleMessage(nettyMessage);
                });
                logger.info("executor: {}", executor);
            }
        } catch (Exception e) {
            e.printStackTrace();
            ctx.close();
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    /**
     * 发生异常触发
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.info("channel {} exceptionCaught......", ctx.channel());
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.info("channel {} channelInactive......", ctx.channel());
        ctx.fireChannelInactive();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            if (idleStateEvent.state() == IdleState.READER_IDLE) {
                // 60s未收到数据就会关闭连接
                logger.info("channel {} idle......", ctx.channel());
                ctx.close();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}