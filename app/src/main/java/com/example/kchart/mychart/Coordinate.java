package com.example.kchart.mychart;

import android.content.Context;
import android.widget.TextView;
import com.example.kchart.R;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

/**
 * <pre>
 *     author : Clement
 *     time   : 2018/04/18
 *     desc   : 在坐标轴上绘制的markerView
 *     https://blog.csdn.net/u014769864/article/details/71545588
 *     version: 1.0
 * </pre>
 */
public class Coordinate extends MarkerView {

    private TextView tvContent;

    public Coordinate(Context context) {
        super(context, R.layout.layout_coordinate_marker);
        tvContent = findViewById(R.id.tv_content);
    }

    // callbacks everytime the MarkerView is redrawn, can be used to update the
    // content (user-interface) 每次 MarkerView 重绘此方法都会被调用，并为您提供更新它显示的内容的机会
    @Override public void refreshContent(Entry e, Highlight highlight) {
        //这里就设置你想显示到makerView上的数据，Entry可以得到X、Y轴坐标，也可以e.getData()获取其他你设置的数据
        tvContent.setText(String.valueOf(e.getY()));
        super.refreshContent(e, highlight);
    }

    /**
     * offset 是以點到的那個點作為 (0,0) 中心然後往右下角畫出來 该方法是让markerView现实到坐标的上方
     * 所以如果要顯示在點的上方
     * X=寬度的一半，負數
     * Y=高度的負數
     */
    @Override public MPPointF getOffset() {
        // Log.e("ddd", "width:" + (-(getWidth() / 2)) + "height:" + (-getHeight()));
        //return new MPPointF(-(getWidth() / 2), -getHeight());
        //居中显示（在highLine的交叉点显示）
        return new MPPointF(-getWidth() / 2, -getHeight() / 2);
    }
}
