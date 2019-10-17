package com.bid.launcherwatch.view;


public class User {
    private double height;
    private int sex;
    private double weight;

    public User(int paramInt, double paramDouble1, double paramDouble2) {
        this.sex = paramInt;
        this.weight = paramDouble1;
        this.height = paramDouble2;
    }

    public double getHeight() {
        return this.height;
    }

    public int getSex() {
        return this.sex;
    }

    public String toString() {
        return "User [height=" + this.height + ", sex=" + this.sex + ", weight=" + this.weight + "]";
    }
}

