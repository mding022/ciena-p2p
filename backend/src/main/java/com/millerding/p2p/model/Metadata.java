package com.millerding.p2p.model;

//Spring bean pojo
public class Metadata {
    private String filename;
    private int totalChunks;
    private String uuid;

    public Metadata(String filename, int totalChunks, String uuid) {
        this.filename = filename;
        this.totalChunks = totalChunks;
        this.uuid = uuid;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public int getTotalChunks() {
        return totalChunks;
    }

    public void setTotalChunks(int totalChunks) {
        this.totalChunks = totalChunks;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
