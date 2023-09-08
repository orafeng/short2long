package com.base.short2long.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Data
@ConfigurationProperties(prefix = "hosts")
public class HostConfig {
    private List<HostParams> hostList;
}