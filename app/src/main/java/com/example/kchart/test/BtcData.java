package com.example.kchart.test;

/**
 * Author: Clement
 * Create: 2018/4/21
 * Desc:
 */

public class BtcData {
    private long date;
    private float open;
    private float high;
    private float low;
    private float close;
    private float volume;

    private float ma7 = 8250f;
    private float ma30 = 8000f;

    public BtcData(long date, float open, float high, float low, float close, float volume) {
        this.date = date;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.volume = volume;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public float getOpen() {
        return open;
    }

    public void setOpen(float open) {
        this.open = open;
    }

    public float getHigh() {
        return high;
    }

    public void setHigh(float high) {
        this.high = high;
    }

    public float getLow() {
        return low;
    }

    public void setLow(float low) {
        this.low = low;
    }

    public float getClose() {
        return close;
    }

    public void setClose(float close) {
        this.close = close;
    }

    public float getVolume() {
        return volume;
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }

    public float getMa7() {
        return ma7;
    }

    public void setMa7(float ma7) {
        this.ma7 = ma7;
    }

    public float getMa30() {
        return ma30;
    }

    public void setMa30(float ma30) {
        this.ma30 = ma30;
    }
}
