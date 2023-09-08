package com.base.short2long.response;

import com.base.short2long.netty.NettyMessage;

/**
 * @author  java author
 * @version 1.0
 * @Description: ResponseDealer
 * @date  2018/11/22 12:44
 */
public interface ResponseDealer {
    /**
     * 消息处理接口
     */
    void handleMessage(NettyMessage nettyMessage);
}