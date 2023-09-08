package com.base.short2long.netty;

import com.base.short2long.utils.CommonFunction;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author java author
 * @version 1.0
 * @Description: NettyServer
 * @date 2018/11/22 12:31
 */
@Component
public class NettyServer {

    protected static Logger logger = LoggerFactory.getLogger(NettyServer.class);

    @Autowired
    private ServerChannelInitializer serverChannelInitializer;

    @SneakyThrows
    public void start(List<String> portList) throws InterruptedException {
        ChannelFuture[] ChannelFutures = null;
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup(40);
        ChannelFuture futureTcp;

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childHandler(serverChannelInitializer);
            //绑定端口，同步等待成功
            if (!CollectionUtils.isEmpty(portList)) {
                if (ChannelFutures == null) {
                    ChannelFutures = new ChannelFuture[portList.size()];
                }
                int i = 0;
                for (String port : portList) {
                    logger.info("Start listening on port: {}", port);
                    futureTcp = bootstrap.bind(Integer.parseInt(port)).sync();
                    ChannelFutures[i++] = futureTcp;
                    futureTcp.addListener(future -> {
                        if (future.isSuccess()) {
                            logger.info("netty server port: {} Start successful!", port);
                        } else {
                            logger.info("netty server port: {} Start failed!", port);
                        }
                    });
                }
                if (ChannelFutures.length == portList.size()) {
                    for (ChannelFuture futureTcp1 : ChannelFutures) {
                        futureTcp1.channel().closeFuture().sync().channel();
                    }
                }
            }
        } finally {
            //退出，释放线程资源
            if (ChannelFutures.length == portList.size()) {
                workerGroup.shutdownGracefully();
                bossGroup.shutdownGracefully();
            }
        }
    }

    public static void sendMessage(ChannelHandlerContext ctx, byte[] bytesOut) {
        logger.info("messageOut: " + CommonFunction.printMsg(bytesOut));
        ByteBuf byteBufOut = Unpooled.buffer(bytesOut.length);
        ctx.writeAndFlush(byteBufOut.writeBytes(bytesOut));
        return;
    }
}