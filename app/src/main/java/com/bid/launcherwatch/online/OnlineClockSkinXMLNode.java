package com.bid.launcherwatch.online;

public class OnlineClockSkinXMLNode {
    private String customer;
    private String file;
    private String name;
    private String preview;
    private String skinid;
    private String type;

    public String getName() {
        return this.name;
    }

    public void setName(String name2) {
        this.name = name2;
    }

    public String getSkinId() {
        return this.skinid;
    }

    public void setSkinId(String skinid2) {
        this.skinid = skinid2;
    }

    public String getPreview() {
        return this.preview;
    }

    public void setPreview(String preview2) {
        this.preview = preview2;
    }

    public String getCustomer() {
        return this.customer;
    }

    public void setCustomer(String customer2) {
        this.customer = customer2;
    }

    public String getClockTpye() {
        return this.type;
    }

    public void setClockTpye(String type2) {
        this.type = type2;
    }

    public String getFilePath() {
        return this.file;
    }

    public void setFilePath(String filePath) {
        this.file = filePath;
    }
}
