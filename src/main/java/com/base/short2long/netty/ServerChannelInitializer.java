package com.base.short2long.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Component
public class ServerChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Resource
    private NettyServerHandler nettyServerHandler;

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        //添加编解码
        socketChannel.pipeline().addLast("frameDecoder", new LengthFieldBasedFrameDecoder(8192, 0, 2, 0, 2));
        socketChannel.pipeline().addLast("frameEncoder", new LengthFieldPrepender(2));
        socketChannel.pipeline().addLast("loggingHandler", new LoggingHandler(LogLevel.INFO));
        socketChannel.pipeline().addLast("idleCheckHandler", new IdleStateHandler(300,
                0, 0, TimeUnit.SECONDS));
        socketChannel.pipeline().addLast(nettyServerHandler);
    }
}