package com.base.short2long.request;

import com.base.short2long.config.SysConfig;
import com.base.short2long.constants.Constants;
import com.base.short2long.netty.NettyClient;
import com.base.short2long.netty.NettyMessage;
import com.base.short2long.netty.NettyServer;
import com.base.short2long.utils.ConvStackTrace;
import com.base.short2long.utils.LoUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @author java author
 * @version 1.0
 * @Description: RequestDealerImpl
 * @date 2018/11/22 12:46
 */
@Service
public class RequestDealerImpl implements RequestDealer {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    public static byte[] bytes = {0x00};

    @Override
    public void handleMessage(NettyMessage nettyMessage) {
        try {
            logger.info("handle request message:");
            nettyMessage.getBytesIn();
            if (NettyClient.isActive()) {
                //增加channelIdx和ip
                byte[] bytes = new byte[nettyMessage.getBytesIn().length + SysConfig.headLen];
                RequestChannel requestChannel = new RequestChannel();
                requestChannel.setReqTime(LocalDateTime.now());
                requestChannel.setCtx(nettyMessage.getCtx());
                Integer channelIdx = getChannelIdx();
                //add 2bytes channelIdx
                logger.info("Use channel map idle index: {}", channelIdx);
                System.arraycopy(LoUtils.hexStr2Bytes(String.format("%04x", channelIdx)), 0, bytes, 0, Constants.CHANNEL_IDX_LEN);
                String ipAddress = getIpAddress(nettyMessage.getCtx().channel().remoteAddress().toString());

                //add ip address
                logger.info("remoteAddress: {}", ipAddress);
                System.arraycopy(ipAddress.getBytes(), 0, bytes, Constants.CHANNEL_IDX_LEN, ipAddress.getBytes().length);

                //复制原消息
                System.arraycopy(nettyMessage.getBytesIn(), 0, bytes,  SysConfig.headLen, nettyMessage.getBytesIn().length);
                //缓存通讯channel，应答时通过channelIdx查找channel
                SysConfig.channelMap.put(channelIdx, requestChannel);
                logger.info("current cache size: {}, {}", SysConfig.channelMap.getCache().size(), SysConfig.channelMap.getCache().stats());
                NettyClient.sendMessage(bytes);
            } else {
                logger.warn("not found valid communication connection.");
                NettyServer.sendMessage(nettyMessage.getCtx(), bytes);
            }
        } catch (Exception e) {
            logger.error(ConvStackTrace.getExceptionStackTrace(e));
        }

        return;
    }

    public static String getIpAddress(String remoteAddress) {
        int index = remoteAddress.indexOf(":");
        return remoteAddress.substring(1, index);
    }

    public static Integer getChannelIdx() {
        Integer channelIdx = 0;
        while (true) {
            //获取可用的缓存空间
            SysConfig.atomicInt.compareAndSet(Integer.MAX_VALUE, 0);
            channelIdx = SysConfig.atomicInt.getAndIncrement() % Constants.MAX_INTEGER;
            if (Objects.isNull(SysConfig.channelMap.get(channelIdx))) {
                break;
            }
        }
        return channelIdx;
    }
}