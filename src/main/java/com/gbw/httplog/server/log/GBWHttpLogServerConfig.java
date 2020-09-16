package com.gbw.httplog.server.log;

import com.gbw.httplog.store.redis.GBWHttpLogStoreRedisConfig;

public class GBWHttpLogServerConfig {

    private boolean isSSL;
    private String ip;
    private int port;
    private String storeUri;
    private String searchUri;
    private String removeUri;

    private String storeType;
    private GBWHttpLogStoreRedisConfig redisStoreConfig;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getStoreUri() {
        return storeUri;
    }

    public void setStoreUri(String storeUri) {
        this.storeUri = storeUri;
    }

    public String getSearchUri() {
        return searchUri;
    }

    public void setSearchUri(String searchUri) {
        this.searchUri = searchUri;
    }

    public String getRemoveUri() {
        return removeUri;
    }

    public void setRemoveUri(String removeUri) {
        this.removeUri = removeUri;
    }

    public boolean isSSL() {
        return isSSL;
    }

    public void setSSL(boolean SSL) {
        isSSL = SSL;
    }

    public GBWHttpLogStoreRedisConfig getRedisStoreConfig() {
        return redisStoreConfig;
    }

    public void setRedisStoreConfig(GBWHttpLogStoreRedisConfig redisStoreConfig) {
        this.redisStoreConfig = redisStoreConfig;
    }

    public String getStoreType() {
        return storeType;
    }

    public void setStoreType(String storeType) {
        this.storeType = storeType;
    }
}
