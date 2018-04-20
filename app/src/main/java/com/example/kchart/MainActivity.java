package com.example.kchart;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import com.example.kchart.mychart.CoupleChartGestureListener;
import com.example.kchart.mychart.CrossMarkerView;
import com.example.kchart.mychart.MyCombinedChart;
import com.example.kchart.mychart.XMarkerView;
import com.example.kchart.mychart.YMarkerView;
import com.example.kchart.test.Model;
import com.example.kchart.test.StockListBean;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Utils;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private MyCombinedChart mKLineChart;
    private MyCombinedChart mVolumeChart;

    private int itemcount;
    private List<CandleEntry> candleEntries = new ArrayList<>();
    private ArrayList<String> xVals;
    private LineData lineData;
    private CandleData candleData;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        //有测试数据的组合图
        loadTestChartData();
    }

    //https://github.com/colin-phang/KLineChartDemo/blob/master/app/src/main/java/android/colin/democandlechart/MainActivity.java

    /***** 从github上找到的测试数据 *****/

    private void loadTestChartData() {
        mKLineChart = findViewById(R.id.k_line_chart);
        int color = ContextCompat.getColor(this, R.color.dark);

        // scaling can now only be done on x- and y-axis separately
        mKLineChart.setPinchZoom(false);
        mKLineChart.setBackgroundColor(color);
        mKLineChart.setDrawGridBackground(false);
        mKLineChart.setAutoScaleMinMaxEnabled(true);
        mKLineChart.setDescription(null);//右下角对图表的描述信息

        XAxis xAxis = mKLineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        //将X坐标转换显示
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override public String getFormattedValue(float value, AxisBase axis) {
                int index = (int) value;
                return xVals.get(index);
            }
        });

        YAxis leftAxis = mKLineChart.getAxisLeft();
        leftAxis.setEnabled(false);

        YAxis rightAxis = mKLineChart.getAxisRight();
        rightAxis.setLabelCount(8, false);
        rightAxis.setDrawGridLines(false); //显示横线
        rightAxis.setDrawAxisLine(true);  //绘制坐标轴线
        //设置Y坐标的最大值和最小值，避免图表滚动时，Y坐标自适应范围
        rightAxis.setAxisMaximum(25f);
        rightAxis.setAxisMinimum(5f);
        rightAxis.setEnabled(true);  //显示右坐标
        rightAxis.setTextColor(Color.WHITE);  //设置坐标字体颜色
        rightAxis.setMinWidth(40);  //设置宽度，可能是要显示小数点后几位
        rightAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);  //设置坐标上值显示的位置
        //rightAxis.setStartAtZero(false);

        /*****************************************************/
        mVolumeChart = findViewById(R.id.volume_chart);
        mVolumeChart.setBackgroundColor(color);
        mVolumeChart.setDrawValueAboveBar(true);
        mVolumeChart.getDescription().setEnabled(false);
        mVolumeChart.setDragEnabled(true);
        mVolumeChart.setScaleYEnabled(false);
        mVolumeChart.setAutoScaleMinMaxEnabled(true);

        Legend barChartLegend = mVolumeChart.getLegend();
        barChartLegend.setEnabled(false);
        //bar x y轴
        XAxis xAxisBar = mVolumeChart.getXAxis();
        xAxisBar.setDrawGridLines(false);
        xAxisBar.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxisBar.setValueFormatter(new IAxisValueFormatter() {
            @Override public String getFormattedValue(float value, AxisBase axis) {
                int index = (int) value;
                return xVals.get(index);
            }
        });

        YAxis axisLeftBar = mVolumeChart.getAxisLeft();
        axisLeftBar.setEnabled(false);

        YAxis axisRightBar = mVolumeChart.getAxisRight();
        axisRightBar.setLabelCount(4, false);
        axisRightBar.setDrawGridLines(false); //显示横线
        axisRightBar.setDrawAxisLine(true);  //绘制坐标轴线
        axisRightBar.setEnabled(true);  //显示右坐标
        axisRightBar.setTextColor(Color.WHITE);  //设置坐标字体颜色
        axisRightBar.setMinWidth(40);  //设置宽度，可能是要显示小数点后几位
        axisRightBar.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);  //设置坐标上值显示的位置

        /*****************************************************/

        // 设置比例图标示
        mKLineChart.getLegend().setEnabled(true);
        mKLineChart.resetTracking();
        //设置绘制顺序，避免遮掩 (要在setData之前设置)
        mKLineChart.setDrawOrder(new CombinedChart.DrawOrder[] {
            CombinedChart.DrawOrder.CANDLE, CombinedChart.DrawOrder.LINE
        });

        //初始化数据
        candleEntries = Model.getCandleEntries();
        itemcount = candleEntries.size();
        List<StockListBean.StockBean> stockBeans = Model.getData();
        xVals = new ArrayList<>();
        for (int i = 0; i < itemcount; i++) {
            xVals.add(stockBeans.get(i).getDate());
        }

        CombinedData kLineData = new CombinedData();
        /*k line*/
        candleData = generateCandleData();
        kLineData.setData(candleData);
        /*ma5*/
        ArrayList<Entry> ma5Entries = new ArrayList<Entry>();
        for (int index = 0; index < itemcount; index++) {
            ma5Entries.add(new Entry(index, stockBeans.get(index).getMa5()));
        }
        /*ma10*/
        ArrayList<Entry> ma10Entries = new ArrayList<Entry>();
        for (int index = 0; index < itemcount; index++) {
            ma10Entries.add(new Entry(index, stockBeans.get(index).getMa10()));
        }
        /*ma20*/
        ArrayList<Entry> ma20Entries = new ArrayList<Entry>();
        for (int index = 0; index < itemcount; index++) {
            ma20Entries.add(new Entry(index, stockBeans.get(index).getMa20()));
        }

        lineData = new LineData(generateLineDataSet(ma5Entries, Color.BLUE, "ma5"),
            generateLineDataSet(ma10Entries, Color.WHITE, "ma10"),
            generateLineDataSet(ma20Entries, Color.YELLOW, "ma20"));

        /****************************/
        CombinedData volumeData = new CombinedData();
        volumeData.setData(generateBarData());

        /*成交量曲线*/
        ArrayList<Entry> tempEntries = new ArrayList<Entry>();
        for (int index = 0; index < itemcount; index++) {
            tempEntries.add(new Entry(index, 40000000F));
        }
        LineData volumeLineData =
            new LineData(generateLineDataSet(tempEntries, Color.BLUE, "Temp"));
        volumeData.setData(volumeLineData);

        mVolumeChart.setData(volumeData);
        //设置缩放（X坐标最多显示40个单位）
        mVolumeChart.setVisibleXRangeMaximum(40);
        //移动当前的X到最后
        mVolumeChart.moveViewToX(itemcount);
        mVolumeChart.invalidate();
        /****************************/

        kLineData.setData(lineData);
        mKLineChart.setData(kLineData);//当前屏幕会显示所有的数据
        //设置缩放（X坐标最多显示40个单位）
        mKLineChart.setVisibleXRangeMaximum(40);
        //移动当前的X到最后
        mKLineChart.moveViewToX(itemcount);
        mKLineChart.invalidate();

        //设置Left坐标轴上的markerView
        //mCombinedChart.setLeftMarkerView(new CoordinateMarkerView(this));
        mKLineChart.setRightMarkerView(new YMarkerView(this));
        mKLineChart.setBottomMarkerView(new XMarkerView(this, xVals));
        //设置交叉点的markerView
        mKLineChart.setCrossMarkerView(new CrossMarkerView(this));

        //设置两表对齐
        setOffset();
        //绑定手势事件
        setChartListener();

    }

    private LineDataSet generateLineDataSet(List<Entry> entries, int color, String label) {
        LineDataSet set = new LineDataSet(entries, label);
        set.setColor(color);
        set.setLineWidth(1f);
        set.setCubicIntensity(0.5f);//圆滑曲线
        set.setDrawCircles(false);
        set.setDrawCircleHole(false);
        set.setDrawValues(false);
        set.setHighlightEnabled(false);
        set.setAxisDependency(YAxis.AxisDependency.RIGHT);

        return set;
    }

    private CandleData generateCandleData() {

        CandleDataSet set = new CandleDataSet(candleEntries, "");
        set.setAxisDependency(YAxis.AxisDependency.RIGHT);
        set.setShadowWidth(0.7f);
        set.setDecreasingColor(Color.RED);
        set.setDecreasingPaintStyle(Paint.Style.FILL);
        set.setIncreasingColor(Color.GREEN);
        set.setIncreasingPaintStyle(Paint.Style.STROKE);
        set.setNeutralColor(Color.RED);
        set.setShadowColorSameAsCandle(true);
        set.setHighlightLineWidth(0.5f);  //选中蜡烛时的高亮线的宽度
        set.setHighLightColor(Color.WHITE);  //线的颜色

        CandleData candleData = new CandleData();
        candleData.addDataSet(set);

        return candleData;
    }

    private BarData generateBarData() {
        List<BarEntry> barEntries = Model.getBarEntries();
        BarDataSet barDataSet = new BarDataSet(barEntries, "成交量");
        barDataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);
        barDataSet.setHighLightColor(Color.WHITE);   //设置选中高亮的条形颜色
        barDataSet.setHighLightAlpha(255);
        barDataSet.setDrawValues(false);
        barDataSet.setHighlightEnabled(true);
        //barDataSet.setColor(Color.RED);
        barDataSet.setColors(Color.GREEN, Color.RED);

        BarData barData = new BarData();
        barData.addDataSet(barDataSet);
        return barData;
    }

    /*设置两表对齐*/
    private void setOffset() {
        float firstLeft = mKLineChart.getViewPortHandler().offsetLeft();
        float secondLeft = mVolumeChart.getViewPortHandler().offsetLeft();
        float firstRight = mKLineChart.getViewPortHandler().offsetRight();
        float secondRight = mVolumeChart.getViewPortHandler().offsetRight();
        float offsetLeft, offsetRight;
        //设置左对齐：函数是针对图表相对位置计算，比如A表offLeftA=20dp,B表offLeftB=30dp,则A.setExtraLeftOffset(10),并不是30，还有注意单位转换
        if (secondLeft < firstLeft) {
            offsetLeft = Utils.convertPixelsToDp(firstLeft - secondLeft);
            mVolumeChart.setExtraLeftOffset(offsetLeft);
        } else {
            offsetLeft = Utils.convertPixelsToDp(secondLeft - firstLeft);
            mKLineChart.setExtraLeftOffset(offsetLeft);
        }
        //设置右对齐：函数是针对图表绝对位置计算，比如A表offRightA=20dp,B表offRightB=30dp,则A.setExtraLeftOffset(30),并不是10，还有注意单位转换
        if (secondRight < firstRight) {
            offsetRight = Utils.convertPixelsToDp(firstRight - secondRight);
            mVolumeChart.setExtraRightOffset(offsetRight);
        } else {
            offsetRight = Utils.convertPixelsToDp(secondRight - firstRight);
            mKLineChart.setExtraRightOffset(offsetRight);
        }
    }

    /**
     * 设置监听器
     */
    private void setChartListener() {
        //将K线图的滑动事件传递给交易量图
        mKLineChart.setOnChartGestureListener(
            new CoupleChartGestureListener(mKLineChart, new Chart[] { mVolumeChart }));
        //将交易量图的滑动事件传递给K线图
        mVolumeChart.setOnChartGestureListener(
            new CoupleChartGestureListener(mVolumeChart, new Chart[] { mKLineChart }));
        //滑动后，手指离开，不会有惯性滚动
        mKLineChart.setDragDecelerationEnabled(false);
        mVolumeChart.setDragDecelerationEnabled(false);

        //设置选中的监听器
        mKLineChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override public void onValueSelected(Entry e, Highlight h) {
                //同步图表的高亮线
                mVolumeChart.highlightValues(new Highlight[] { h });
            }

            @Override public void onNothingSelected() {
                //清空高亮线
                mKLineChart.highlightValues(null);
                mVolumeChart.highlightValue(null);
            }
        });
        mVolumeChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override public void onValueSelected(Entry e, Highlight h) {
                //同步图表的高亮线
                mKLineChart.highlightValues(new Highlight[] { h });
            }

            @Override public void onNothingSelected() {
                //清空高亮线
                mKLineChart.highlightValues(null);
                mVolumeChart.highlightValue(null);
            }
        });
    }
}
