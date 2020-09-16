package com.gbw.httplog.store.redis;

import com.gbw.httplog.store.GBWHttpLogSearchResult;
import com.gbw.httplog.store.GBWHttpLogStore;
import com.gbw.httplog.store.GBWHttpLogStoreConfig;
import com.gbw.httplog.store.GBWHttpLogStoreException;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class GBWHttpLogStoreRedis implements GBWHttpLogStore {

    private GBWHttpLogStoreRedisConfig config;
    private JedisPool jedisPool;

    @Override
    public void open(GBWHttpLogStoreConfig conf) throws GBWHttpLogStoreException {

        this.config = (GBWHttpLogStoreRedisConfig) conf;

        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(config.getMaxTotal());
        poolConfig.setMaxIdle(config.getMaxIdle());
        poolConfig.setMaxWaitMillis(config.getMaxWaitMs());
        poolConfig.setTestOnBorrow(config.isPing());

        this.jedisPool = new JedisPool(poolConfig,
                config.getIp(),
                config.getPort(),
                config.getTimeout(),
                config.getAuth(),
                config.getDatabase());

    }

    @Override
    public void store(String id, String json) {

        Jedis jedis = null;

        try {
            jedis = jedisPool.getResource();
            jedis.set(id,json);

        }catch (Exception e){

        }finally {
            if(jedis!=null)
                jedis.close();
        }
    }

    @Override
    public GBWHttpLogSearchResult search(String id) {

        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String json = jedis.get(id);

            if(json!=null&&json.length()>0)
                return new GBWHttpLogSearchResult(id,json,200,"OK");

        }catch (Exception e){
            return new GBWHttpLogSearchResult(id,"{}",500,"Error:"+e.getMessage());
        }finally {
            if(jedis!=null)
                jedis.close();
        }

        return new GBWHttpLogSearchResult(id,"{}",404,"Error:Not Found");
    }

    @Override
    public void remove(String id) {

        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.del(id);
        }catch (Exception e){

        }finally {
            if(jedis!=null)
                jedis.close();
        }
    }

    @Override
    public void close() {

        jedisPool.close();
    }

}
