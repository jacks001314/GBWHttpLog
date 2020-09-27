package com.gbw.httplog.server.weblogic;

public class GBWWeblogicCoherencePayloadEntry {

    private String version;
    private String fpath;

    public GBWWeblogicCoherencePayloadEntry(String version,String fpath){

        this.version = version;
        this.fpath = fpath;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getFpath() {
        return fpath;
    }

    public void setFpath(String fpath) {
        this.fpath = fpath;
    }
}
