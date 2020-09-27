package com.gbw.httplog.server.weblogic;

public class GBWWeblogicCoherenceServerConfig {

    private boolean isSSL;
    private String ip;
    private int port;
    private String payloadDir;

    public String getPayloadDir() {
        return payloadDir;
    }

    public void setPayloadDir(String payloadDir) {
        this.payloadDir = payloadDir;
    }

    public boolean isSSL() {
        return isSSL;
    }

    public void setSSL(boolean SSL) {
        isSSL = SSL;
    }

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
}
