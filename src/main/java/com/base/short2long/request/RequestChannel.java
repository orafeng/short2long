package com.base.short2long.request;

import io.netty.channel.ChannelHandlerContext;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RequestChannel {
    private ChannelHandlerContext ctx;
    private LocalDateTime reqTime;
}
