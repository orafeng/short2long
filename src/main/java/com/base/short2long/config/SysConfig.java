package com.base.short2long.config;

import com.base.short2long.constants.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
public class SysConfig {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    public static String node;
    public static Integer headLen;
    public static Integer timeout;
    public static List<String> portList;
    public static List<HostParams> hostList;
    public static ChannelMap channelMap;
    public static AtomicInteger atomicInt = new AtomicInteger(new Random(Constants.MAX_INTEGER).nextInt());
    public static ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);

    @Resource
    HostConfig hostConfig;

    @Resource
    PortConfig portConfig;

    @Value("${sys-config.node}")
    public void setNode(String node) {
        SysConfig.node = node;
        logger.info("node: " + node);
    }

    @Value("${sys-config.headLen}")
    public void setHeadLen(Integer headLen) {
        SysConfig.headLen = headLen;
        logger.info("headLen: " + headLen);
    }

    @Value("${sys-config.timeout}")
    public void setTimeout(Integer timeout) {
        SysConfig.timeout = timeout;
        logger.info("timeout: " + timeout);
    }

    @PostConstruct
    public void init() {
        SysConfig.portList = portConfig.getPortList();
        SysConfig.hostList = hostConfig.getHostList();
        SysConfig.channelMap = new ChannelMap(SysConfig.timeout, TimeUnit.SECONDS);
    }

}
