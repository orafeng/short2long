package com.base.short2long.netty;

import com.base.short2long.config.HostParams;
import com.base.short2long.config.SysConfig;
import com.base.short2long.utils.CommonFunction;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author java author
 * @version 1.0
 * @Description: NettyClient
 * @date 2018/11/22 12:31
 */
@Component
public class NettyClient {

    protected static Logger logger = LoggerFactory.getLogger(NettyClient.class);

    @Resource
    ClientChannelInitializer clientChannelInitializer;

    //设置一个多线程循环器
    private EventLoopGroup workerGroup = new NioEventLoopGroup();
    //启动附注类x
    public static Bootstrap bootstrap = new Bootstrap();

    private static AtomicInteger index = new AtomicInteger();

    private static CopyOnWriteArrayList<NettyChannel> channelList = new CopyOnWriteArrayList<>();

    public void start() {
        bootstrap.group(workerGroup);
        //指定所使用的NIO传输channel
        bootstrap.channel(NioSocketChannel.class);
        //指定客户端初始化处理
        bootstrap.handler(clientChannelInitializer);

        for (HostParams hostParams : SysConfig.hostList) {
            NettyChannel nettyChannel = new NettyChannel(hostParams.getHost(), hostParams.getPort(), null);
            channelList.add(nettyChannel);
            connect(nettyChannel);
        }
    }

    private void connect(NettyChannel nettyChannel) {
        logger.info("Connection host: {}, port: {}", nettyChannel.getHost(), nettyChannel.getPort());
        ChannelFuture future = bootstrap.connect(nettyChannel.getHost(), Integer.parseInt(nettyChannel.getPort()));
        nettyChannel.setChannel(future.channel());
        future.addListener(f -> {
            if (f.isSuccess()) {
                Channel channel = future.channel();
                channel.closeFuture().addListener(closeFuture -> {
                    // 如果连接正常断开，触发重连
                    logger.warn("Connection lost. Reconnecting in 5 seconds....");
                    channel.eventLoop().schedule(() -> connect(nettyChannel), 5, TimeUnit.SECONDS);
                });
                logger.info("Connection successful!");
            } else {
                logger.warn("Connection failed. Retry in 5 seconds....");
                // 连接失败，触发重连
                future.channel().eventLoop().schedule(() -> connect(nettyChannel), 5, TimeUnit.SECONDS);
            }
        });
    }

    public static Channel next() {
        //获取可用的channel
        return getChannel(0);
    }

    public static boolean isActive() {
        for (int i = 0; i < channelList.size(); i++) {
            if (channelList.get(i).getChannel().isActive()) {
                return true;
            }
        }
        return false;
    }

    private static Channel getChannel(int count) {
        NettyChannel nettyChannel = channelList.get(Math.abs(index.getAndIncrement() % channelList.size()));
        if (count >= channelList.size()) {
            return null;
        }

        if (!nettyChannel.getChannel().isActive()) {
            //尝试获取下一个channel
            return getChannel(++count);
        }
        return nettyChannel.getChannel();
    }

    public static void sendMessage(byte[] bytesOut) {
        Channel channel = next();
        if (channel != null) {
            logger.info("messageOut: " + CommonFunction.printMsg(bytesOut));
            ByteBuf byteBufOut = Unpooled.buffer(bytesOut.length);
            channel.writeAndFlush(byteBufOut.writeBytes(bytesOut));
        }
        return;
    }
}
