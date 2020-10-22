package com.gbw.httplog.server.dnslog;

import com.gbw.httplog.store.redis.GBWHttpLogStoreRedisConfig;

import java.util.Map;

public class GBWDNSLogConfig {

    private String domain;
    private String defaultResponseIP;
    private Map<String,String> bindMap;

    private GBWHttpLogStoreRedisConfig redisConfig;


    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public Map<String, String> getBindMap() {
        return bindMap;
    }

    public void setBindMap(Map<String, String> bindMap) {
        this.bindMap = bindMap;
    }

    public String getDefaultResponseIP() {
        return defaultResponseIP;
    }

    public void setDefaultResponseIP(String defaultResponseIP) {
        this.defaultResponseIP = defaultResponseIP;
    }

    public GBWHttpLogStoreRedisConfig getRedisConfig() {
        return redisConfig;
    }

    public void setRedisConfig(GBWHttpLogStoreRedisConfig redisConfig) {
        this.redisConfig = redisConfig;
    }
}
