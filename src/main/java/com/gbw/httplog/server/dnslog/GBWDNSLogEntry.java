package com.gbw.httplog.server.dnslog;

public class GBWDNSLogEntry {

    private String srcIP;
    private String resIP;
    private long time;

    public GBWDNSLogEntry(String srcIP,String resIP,long time){

        this.srcIP = srcIP;
        this.resIP = resIP;
        this.time = time;
    }

    public String getSrcIP() {
        return srcIP;
    }

    public void setSrcIP(String srcIP) {
        this.srcIP = srcIP;
    }

    public String getResIP() {
        return resIP;
    }

    public void setResIP(String resIP) {
        this.resIP = resIP;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
