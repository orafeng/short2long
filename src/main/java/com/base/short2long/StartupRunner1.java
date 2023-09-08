package com.base.short2long;

import com.base.short2long.config.SysConfig;
import com.base.short2long.netty.NettyClient;
import com.base.short2long.netty.NettyServer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author java author
 * @version 1.0
 * @Description: StartupRunner1
 * @date 2018/11/22 12:27
 */
@Component
@Order(value = 1)
public class StartupRunner1 implements CommandLineRunner {

    @Resource
    NettyServer nettyServer;

    @Resource
    NettyClient nettyClient;

    @Override
    public void run(String... args) {
        SysConfig.executor.execute(() -> {
            try {
                nettyServer.start(SysConfig.portList);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        SysConfig.executor.execute(() -> {
            nettyClient.start();
        });
    }
}
