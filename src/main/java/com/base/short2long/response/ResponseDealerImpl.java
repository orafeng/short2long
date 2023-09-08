package com.base.short2long.response;

import com.base.short2long.config.SysConfig;
import com.base.short2long.constants.Constants;
import com.base.short2long.netty.NettyMessage;
import com.base.short2long.netty.NettyServer;
import com.base.short2long.request.RequestChannel;
import com.base.short2long.utils.ConvStackTrace;
import com.base.short2long.utils.LoUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @author java author
 * @version 1.0
 * @Description: ResponseDealerImpl
 * @date 2018/11/22 12:46
 */
@Service
public class ResponseDealerImpl implements ResponseDealer {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void handleMessage(NettyMessage nettyMessage) {
        try {
            logger.info("handle response message:");
            if (nettyMessage.getBytesIn().length > 2) {
                byte[] byteIdx = new byte[2];
                System.arraycopy(nettyMessage.getBytesIn(), 0, byteIdx, 0, Constants.CHANNEL_IDX_LEN);
                Integer channelIdx = Integer.parseInt(LoUtils.byte2HexStr(byteIdx), 16);
                logger.info("channel map index: {} from response message.", channelIdx);
                RequestChannel requestChannel = (RequestChannel) SysConfig.channelMap.get(channelIdx);
                SysConfig.channelMap.remove(channelIdx);
                logger.info("current cache size: {}, {}", SysConfig.channelMap.getCache().size(), SysConfig.channelMap.getCache().stats());
                if (!Objects.isNull(requestChannel)) {
                    Duration duration = Duration.between(requestChannel.getReqTime(), LocalDateTime.now());
                    logger.info("channelIdx: {}, duration: {}, ctx: {}",  channelIdx, duration.toMillis(), requestChannel.getCtx());
                    byte[] byteOut = new byte[nettyMessage.getBytesIn().length];
                    //返回
                    System.arraycopy(nettyMessage.getBytesIn(), Constants.CHANNEL_IDX_LEN, byteOut, 0, nettyMessage.getBytesIn().length - 2);
                    NettyServer.sendMessage(requestChannel.getCtx(), byteOut);
                } else {
                    logger.info("not found channelIdx: {}", channelIdx);
                }
            }
        } catch (Exception e) {
            logger.error(ConvStackTrace.getExceptionStackTrace(e));
        }

        return;
    }
}