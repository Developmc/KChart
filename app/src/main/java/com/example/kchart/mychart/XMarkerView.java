package com.example.kchart.mychart;

import android.content.Context;
import android.widget.TextView;
import com.example.kchart.R;
import com.example.kchart.util.TimeUtil;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import java.util.List;

/**
 * <pre>
 *     author : Clement
 *     time   : 2018/04/18
 *     desc   : 在坐标轴上绘制的markerView
 *     https://blog.csdn.net/u014769864/article/details/71545588
 *     version: 1.0
 * </pre>
 */
public class XMarkerView extends MarkerView {

    private TextView tvContent;
    private List<Long> mDates;

    public XMarkerView(Context context) {
        super(context, R.layout.layout_coordinate_marker);
        tvContent = findViewById(R.id.tv_k_ma);
    }

    // callbacks everytime the MarkerView is redrawn, can be used to update the
    // content (user-interface) 每次 MarkerView 重绘此方法都会被调用，并为您提供更新它显示的内容的机会
    @Override public void refreshContent(Entry e, Highlight highlight) {
        int index = (int) highlight.getX();
        if (mDates != null && !mDates.isEmpty() && mDates.size() > index) {
            String tempData = TimeUtil.long2String(mDates.get(index), TimeUtil.CHART_FORMAT);
            tvContent.setText(tempData);
        }
        super.refreshContent(e, highlight);
    }

    public void setDates(List<Long> dates) {
        this.mDates = dates;
    }
}
