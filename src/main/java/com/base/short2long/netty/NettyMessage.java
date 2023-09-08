package com.base.short2long.netty;

import io.netty.channel.ChannelHandlerContext;

public class NettyMessage {
    private ChannelHandlerContext ctx;
    private byte[] bytesIn;

    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    public void setCtx(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    public byte[] getBytesIn() {
        return bytesIn;
    }

    public void setBytesIn(byte[] bytesIn) {
        this.bytesIn = bytesIn;
    }
}
