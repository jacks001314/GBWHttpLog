package com.gbw.httplog.store.redis;

import com.gbw.httplog.store.GBWHttpLogStoreConfig;

public class GBWHttpLogStoreRedisConfig extends GBWHttpLogStoreConfig {

    private String auth;
    private int database;
    private int timeout;
    private int maxTotal;
    private int maxIdle;
    private int maxWaitMs;
    private boolean ping;

    public int getMaxTotal() {
        return maxTotal;
    }

    public void setMaxTotal(int maxTotal) {
        this.maxTotal = maxTotal;
    }

    public int getMaxIdle() {
        return maxIdle;
    }

    public void setMaxIdle(int maxIdle) {
        this.maxIdle = maxIdle;
    }

    public int getMaxWaitMs() {
        return maxWaitMs;
    }

    public void setMaxWaitMs(int maxWaitMs) {
        this.maxWaitMs = maxWaitMs;
    }

    public boolean isPing() {
        return ping;
    }

    public void setPing(boolean ping) {
        this.ping = ping;
    }


    public String getAuth() {
        return auth;
    }


    public void setAuth(String auth) {
        this.auth = auth;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getDatabase() {
        return database;
    }

    public void setDatabase(int database) {
        this.database = database;
    }
}
