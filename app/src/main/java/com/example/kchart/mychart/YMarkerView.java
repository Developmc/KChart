package com.example.kchart.mychart;

import android.content.Context;
import android.widget.TextView;
import com.example.kchart.R;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;

/**
 * <pre>
 *     author : Clement
 *     time   : 2018/04/18
 *     desc   : 在坐标轴上绘制的markerView
 *     https://blog.csdn.net/u014769864/article/details/71545588
 *     version: 1.0
 * </pre>
 */
public class YMarkerView extends MarkerView {

    private TextView tvContent;

    public YMarkerView(Context context) {
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
}
