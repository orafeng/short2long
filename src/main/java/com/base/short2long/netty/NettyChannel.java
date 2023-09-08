package com.base.short2long.netty;

import io.netty.channel.Channel;
import lombok.Data;

@Data
public class NettyChannel {
    private String host;
    private String port;
    private Channel channel;

    public NettyChannel(String host, String port, Channel channel) {
        this.host = host;
        this.port = port;
        this.channel = channel;
    }
}
