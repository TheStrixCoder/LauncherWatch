package com.bid.launcherwatch.view;

import android.graphics.drawable.Drawable;
import java.util.List;

public class ClockInfo {
    private String angle;
    private String arraytype;
    private String centerX;
    private String centerY;
    private String cls;
    private String color;
    private String colorarray;
    private String direction;
    private String mulrotate;
    private String name;
    private Drawable namepng;
    private List<Num> nums;
    private String pkg;
    private String radius;
    private String range;
    private String rotate;
    private String rotatemode;
    private String startAngle;
    private String textcolor;
    private String textsize;
    private String width;

    static public class Num {
        private Drawable numDrawable;

        public Num() {
        }

        public Drawable getNumDrawable() {
            return this.numDrawable;
        }

        public void setNumDrawable(Drawable numDrawable2) {
            this.numDrawable = numDrawable2;
        }
    }

    public String getRange() {
        return this.range;
    }

    public void setRange(String range2) {
        this.range = range2;
    }

    public String getCls() {
        return this.cls;
    }

    public void setCls(String cls2) {
        this.cls = cls2;
    }

    public String getPkg() {
        return this.pkg;
    }

    public void setPkg(String pkg2) {
        this.pkg = pkg2;
    }

    public String getRotatemode() {
        return this.rotatemode;
    }

    public void setRotatemode(String rotatemode2) {
        this.rotatemode = rotatemode2;
    }

    public String getRadius() {
        return this.radius;
    }

    public void setRadius(String radius2) {
        this.radius = radius2;
    }

    public String getWidth() {
        return this.width;
    }

    public void setWidth(String width2) {
        this.width = width2;
    }

    public Drawable getNamepng() {
        return this.namepng;
    }

    public void setNamepng(Drawable namepng2) {
        this.namepng = namepng2;
    }

    public String getRotate() {
        return this.rotate;
    }

    public void setRotate(String rotate2) {
        this.rotate = rotate2;
    }

    public String getAngle() {
        return this.angle;
    }

    public void setAngle(String angle2) {
        this.angle = angle2;
    }

    public String getMulrotate() {
        return this.mulrotate;
    }

    public void setMulrotate(String mulrotate2) {
        this.mulrotate = mulrotate2;
    }

    public String getColor() {
        return this.color;
    }

    public void setColor(String color2) {
        this.color = color2;
    }

    public String getStartAngle() {
        return this.startAngle;
    }

    public void setStartAngle(String startAngle2) {
        this.startAngle = startAngle2;
    }

    public String getDirection() {
        return this.direction;
    }

    public void setDirection(String direction2) {
        this.direction = direction2;
    }

    public String getTextsize() {
        return this.textsize;
    }

    public void setTextsize(String textsize2) {
        this.textsize = textsize2;
    }

    public void setTextcolor(String textcolor2) {
        this.textcolor = textcolor2;
    }

    public List<Num> getNums() {
        return this.nums;
    }

    public void setNums(List<Num> nums2) {
        this.nums = nums2;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name2) {
        this.name = name2;
    }

    public String getCenterX() {
        return this.centerX;
    }

    public void setCenterX(String centerX2) {
        this.centerX = centerX2;
    }

    public String getCenterY() {
        return this.centerY;
    }

    public void setCenterY(String centerY2) {
        this.centerY = centerY2;
    }

    public String getArraytype() {
        return this.arraytype;
    }

    public void setArraytype(String arraytype2) {
        this.arraytype = arraytype2;
    }

    public String getColorArray() {
        return this.colorarray;
    }

    public void setColorArray(String colorarray2) {
        this.colorarray = colorarray2;
    }
}
