package com.example.kchart.test;

/**
 * <pre>
 *     author : Clement
 *     time   : 2018/04/25
 *     desc   : 深度图的数据
 *     version: 1.0
 * </pre>
 */
public class Depth {
    private float price;
    private float amount;
    private float total;
    private String type;

    public Depth(float price, float amount, float total, String type) {
        this.price = price;
        this.amount = amount;
        this.total = total;
        this.type = type;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
