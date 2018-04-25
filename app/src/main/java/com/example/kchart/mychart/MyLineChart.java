package com.example.kchart.mychart;

import android.content.Context;
import android.util.AttributeSet;
import com.github.mikephil.charting.charts.LineChart;

/**
 * <pre>
 *     author : Clement
 *     time   : 2018/04/25
 *     desc   : 修改X轴第一个和最后一个文本显示不全的问题
 *     version: 1.0
 * </pre>
 */
public class MyLineChart extends LineChart {

    public MyLineChart(Context context) {
        super(context);
    }

    public MyLineChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyLineChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override protected void init() {
        super.init();
        mXAxis = new MyXAxis();
        mXAxisRenderer = new MyXAxisRenderer(mViewPortHandler, (MyXAxis) mXAxis, mLeftAxisTransformer, this);
    }
}
