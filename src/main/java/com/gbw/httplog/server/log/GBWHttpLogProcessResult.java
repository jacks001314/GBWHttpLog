package com.gbw.httplog.server.log;

public class GBWHttpLogProcessResult {

    private  String op;
    private  int status;
    private  String msg;
    private  String result;


    public GBWHttpLogProcessResult(String op, int status, String msg, String result) {
        this.op = op;
        this.status = status;
        this.msg = msg;
        this.result = result;
    }


    public String getOp() {
        return op;
    }

    public void setOp(String op) {
        this.op = op;
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

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
