package com.bid.launcherwatch;

public class Data {
    String date;
    String distance;
    int steps;

    public Data(String paramString1, int paramInt, String paramString2) {
        this.date = paramString1;
        this.steps = paramInt;
        this.distance = paramString2;
    }

    public String getDate() {
        return this.date;
    }

    public String getDistance() {
        return this.distance;
    }

    public String setDistance(String dis) {
        this.distance = dis;
        return dis;
    }

    public int getSteps() {
        return this.steps;
    }

    public void setSteps(int paramInt) {
        this.steps = paramInt;
    }
}

