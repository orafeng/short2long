package com.base.short2long.netty;

import com.base.short2long.response.ResponseDealer;
import com.base.short2long.utils.CommonFunction;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
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

/**
 * @author java author
 * @version 1.0
 * @Description: NettyClientHandler
 * @date 2018/11/22 12:31
 */
@Component
@ChannelHandler.Sharable
public class NettyClientHandler extends ChannelInboundHandlerAdapter {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    ResponseDealer responseDealer;

    ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(100);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //定时发送心跳包
        logger.info("channel {} channelActive......", ctx.channel());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        logger.info("channel {} channelRead......", ctx.channel());
        try {
            ByteBuf byteBufIn = (ByteBuf) msg;
            byte[] bytesIn = new byte[byteBufIn.readableBytes()];
            byteBufIn.readBytes(bytesIn);

            if (bytesIn.length > 0) {
                logger.info("messageIn: " + CommonFunction.printMsg(bytesIn));
                NettyMessage nettyMessage = new NettyMessage();
                nettyMessage.setCtx(ctx);
                nettyMessage.setBytesIn(bytesIn);
                executor.execute(() -> {
                    responseDealer.handleMessage(nettyMessage);
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
            if (idleStateEvent.state() == IdleState.WRITER_IDLE) {
                //发送空闲报文
                logger.info("channel {} idle......", ctx.channel());
                ctx.writeAndFlush(Unpooled.EMPTY_BUFFER);
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}