package com.gbw.httplog.store;

public class GBWHttpLogSearchResult {

    private String id;
    private String json;
    private String msg;
    private int status;

    public GBWHttpLogSearchResult(String id,String json,int status,String msg){

        this.id = id;
        this.json = json;
        this.status = status;
        this.msg = msg;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
