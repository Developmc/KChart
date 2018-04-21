package com.example.kchart.test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: Clement
 * Create: 2018/4/21
 * Desc: btc的数据模型
 */

public class Btc {

    private String symbol;
    private List<List<Object>> data;
    private String moneyType;

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public List<BtcData> getData() {
        //解析数据
        List<BtcData> btcDataList = new ArrayList<>();
        if (!data.isEmpty()) {
            for (int i = 0; i < data.size(); i++) {
                List<Object> objects = data.get(i);
                if (objects.size() == 6) {
                    String time = String.valueOf(objects.get(0));
                    BigDecimal bigDecimal = new BigDecimal(time);
                    float open = Float.valueOf(String.valueOf(objects.get(1)));
                    float high = Float.valueOf(String.valueOf(objects.get(2)));
                    float low = Float.valueOf(String.valueOf(objects.get(3)));
                    float close = Float.valueOf(String.valueOf(objects.get(4)));
                    float volume = Float.valueOf(String.valueOf(objects.get(5)));
                    BtcData btcData = new BtcData(bigDecimal.longValue(), open, high,
                            low, close, volume);
                    btcDataList.add(btcData);
                }

            }
        }
        return btcDataList;
    }

    public void setData(List<List<Object>> data) {
        this.data = data;
    }

    public String getMoneyType() {
        return moneyType;
    }

    public void setMoneyType(String moneyType) {
        this.moneyType = moneyType;
    }
}
