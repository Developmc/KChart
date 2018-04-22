package com.example.kchart.mychart;

import android.content.Context;
import android.widget.TextView;

import com.example.kchart.R;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;

/**
 * <pre>
 *     author : Clement
 *     time   : 2018/04/18
 *     desc   : 在坐标轴上绘制的最新entry的markerView
 *     https://blog.csdn.net/u014769864/article/details/71545588
 *     version: 1.0
 * </pre>
 */
public class YLastMarkerView extends MarkerView {

    private TextView tvContent;

    public YLastMarkerView(Context context) {
        super(context, R.layout.layout_last_marker);
        tvContent = findViewById(R.id.tv_k_ma);
    }

    // callbacks everytime the MarkerView is redrawn, can be used to update the
    // content (user-interface) 每次 MarkerView 重绘此方法都会被调用，并为您提供更新它显示的内容的机会
    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        float content = 0.f;
        //这里有可能是蜡烛图，也有可能是柱状图
        if (e instanceof CandleEntry) {
            CandleEntry candleEntry = (CandleEntry) e;
            //这里取蜡烛图的最高点
            content = candleEntry.getHigh();
        } else if (e instanceof BarEntry) {
            BarEntry barEntry = (BarEntry) e;
            //这里取柱状图的Y坐标
            content = barEntry.getY();
        }
        tvContent.setText(String.valueOf(content));
        super.refreshContent(e, highlight);
    }

}
