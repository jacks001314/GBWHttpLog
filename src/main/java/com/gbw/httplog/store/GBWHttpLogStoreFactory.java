package com.gbw.httplog.store;

import com.gbw.httplog.server.log.GBWHttpLogServerConfig;
import com.gbw.httplog.store.redis.GBWHttpLogStoreRedis;

public class GBWHttpLogStoreFactory {

    private GBWHttpLogStoreFactory(){

    }

    public static final GBWHttpLogStore make(GBWHttpLogServerConfig serverConfig){

        String type = serverConfig.getStoreType();
        GBWHttpLogStore store = null;

        if(type.equals("redis")){
            store =  new GBWHttpLogStoreRedis();
            try {
                store.open(serverConfig.getRedisStoreConfig());
            } catch (Exception e) {

                store = null;
            }
        }

        return store;
    }

}
