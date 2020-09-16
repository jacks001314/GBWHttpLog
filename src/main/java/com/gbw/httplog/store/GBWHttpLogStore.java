package com.gbw.httplog.store;

public interface GBWHttpLogStore {

    void open(GBWHttpLogStoreConfig config) throws GBWHttpLogStoreException;

    void store(String id,String json);

    GBWHttpLogSearchResult search(String id);

    void remove(String id);

    void close();
}
