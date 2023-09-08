package com.base.short2long.request;

import com.base.short2long.netty.NettyMessage;

/**
 * @author  java author
 * @version 1.0
 * @Description: RequestDealer
 * @date  2018/11/22 12:44
 */
public interface RequestDealer {
    /**
     * 消息处理接口
     */
    void handleMessage(NettyMessage nettyMessage);
}