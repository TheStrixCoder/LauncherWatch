package com.bid.launcherwatch.cleananimation;

import java.util.Random;

public class Dot {
    public static int SPEED = 0;
    public static int WIDTH = 0;
    public static int sMaxDotRadius;
    public static int sMinDotRadius;
    private float mRadius;

    /* renamed from: mX */
    private float f7mX;

    /* renamed from: mY */
    private float f8mY;
    public Random sRandom;

    public Dot() {
        setRightPosition();
    }

    public Dot(float x, float y, float radius) {
        this.f7mX = x;
        this.f8mY = y;
        this.mRadius = radius;
    }

    public float getX() {
        return this.f7mX;
    }

    public float getY() {
        return this.f8mY;
    }

    public float getRadius() {
        return this.mRadius;
    }

    public double getZ() {
        return Math.sqrt((double) ((this.f7mX * this.f7mX) + (this.f8mY * this.f8mY)));
    }

    public void checkAndChange() {
        if (getZ() + ((double) getRadius()) < ((double) (WIDTH / 4))) {
            setRightPosition();
        } else {
            adjustPosition();
        }
    }

    public void adjustPosition() {
        this.f7mX -= (float) (((double) (this.f7mX * ((float) SPEED))) / getZ());
        this.f8mY -= (float) (((double) (this.f8mY * ((float) SPEED))) / getZ());
    }

    public void setRightPosition() {
        double tan;
        int i;
        double d;
        int i2;
        double tan2;
        int i3;
        double tan3;
        int i4;
        if (this.sRandom == null) {
            this.sRandom = new Random();
        }
        this.mRadius = (float) ((int) ((this.sRandom.nextFloat() * ((float) (sMaxDotRadius - sMinDotRadius))) + ((float) sMinDotRadius)));
        int angle = this.sRandom.nextInt(360);
        if (angle < 90) {
            this.f8mY = (((float) ((-WIDTH) / 2)) - this.mRadius) - ((float) this.sRandom.nextInt(50));
            int angle2 = angle - 45;
            if (angle2 < 0) {
                tan3 = -Math.tan((((double) angle2) * 3.141592653589793d) / 360.0d);
                i4 = WIDTH;
            } else {
                tan3 = Math.tan((((double) angle2) * 3.141592653589793d) / 180.0d);
                i4 = WIDTH;
            }
            this.f7mX = (float) ((int) ((tan3 * ((double) i4)) / 2.0d));
        } else if (angle < 180) {
            this.f7mX = ((float) (WIDTH / 2)) + this.mRadius + ((float) this.sRandom.nextInt(50));
            int angle3 = angle - 135;
            if (angle3 < 0) {
                tan2 = -Math.tan((((double) angle3) * 3.141592653589793d) / 180.0d);
                i3 = WIDTH;
            } else {
                tan2 = Math.tan((((double) angle3) * 3.141592653589793d) / 180.0d);
                i3 = WIDTH;
            }
            this.f8mY = (float) ((int) ((tan2 * ((double) i3)) / 2.0d));
        } else if (angle < 270) {
            this.f8mY = ((float) (WIDTH / 2)) + this.mRadius + ((float) this.sRandom.nextInt(50));
            int angle4 = angle - 225;
            if (angle4 < 0) {
                d = Math.tan((((double) angle4) * 3.141592653589793d) / 180.0d);
                i2 = WIDTH;
            } else {
                d = -Math.tan((((double) angle4) * 3.141592653589793d) / 180.0d);
                i2 = WIDTH;
            }
            this.f7mX = (float) ((int) ((d * ((double) i2)) / 2.0d));
        } else {
            this.f7mX = (((float) ((-WIDTH) / 2)) - this.mRadius) - ((float) this.sRandom.nextInt(50));
            int angle5 = angle - 315;
            if (angle5 > 0) {
                tan = -Math.tan((((double) angle5) * 3.141592653589793d) / 180.0d);
                i = WIDTH;
            } else {
                tan = Math.tan((((double) angle5) * 3.141592653589793d) / 180.0d);
                i = WIDTH;
            }
            this.f8mY = (float) ((int) ((tan * ((double) i)) / 2.0d));
        }
    }
}
