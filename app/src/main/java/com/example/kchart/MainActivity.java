package com.example.kchart;

import android.graphics.Paint;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import com.example.kchart.mychart.CoupleChartGestureListener;
import com.example.kchart.mychart.CrossMarkerView;
import com.example.kchart.mychart.HighestXDrawListener;
import com.example.kchart.mychart.MyCombinedChart;
import com.example.kchart.mychart.MyLineChart;
import com.example.kchart.mychart.XMarkerView;
import com.example.kchart.mychart.YLastMarkerView;
import com.example.kchart.mychart.YMarkerView;
import com.example.kchart.test.BtcData;
import com.example.kchart.test.KLineType;
import com.example.kchart.test.Model;
import com.example.kchart.util.TimeUtil;
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
    private MyLineChart mDepthChart;

    private List<Long> xVals;
    private List<BtcData> btcDataList;

    //折线
    private List<Entry> ma7Entries;
    private List<Entry> ma30Entries;
    private List<Entry> ma7SecondEntries;
    private List<Entry> ma30VolumeEntries;

    private CombinedData mKLineData;
    private CombinedData mVolumeData;
    private LineData mDepthData;

    private KLineType mType = KLineType.MINUTE;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        TabLayout tlType = findViewById(R.id.tl_type);
        tlType.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        mType = KLineType.MINUTE;
                        break;
                    case 1:
                        mType = KLineType.HOUR;
                        break;
                    case 2:
                        mType = KLineType.DAY;
                        break;
                }
                loadChart();
            }

            @Override public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        initKLineChart();
        initVolumeChart();
        initDepthChart();
        //有测试数据的组合图
        loadChart();
    }

    //https://github.com/colin-phang/KLineChartDemo/blob/master/app/src/main/java/android/colin/democandlechart/MainActivity.java

    /***** 从github上找到的测试数据 *****/

    private void loadChart() {
        initChartData();

        mKLineChart.setData(mKLineData);
        mKLineChart.getXAxis().setAxisMinimum(-0.5f);
        mKLineChart.getXAxis().setAxisMaximum(mKLineData.getXMax() + 0.5f);
        //设置缩放（X坐标最多显示48个单位）,需要在设置数据后调用才有效
        mKLineChart.setVisibleXRangeMaximum(48);
        mKLineChart.post(new Runnable() {
            @Override public void run() {
                //控制缩放范围
                mKLineChart.setVisibleXRangeMinimum(10);
                mKLineChart.setVisibleXRangeMaximum(1000);
            }
        });
        //设置x轴的marker显示
        mKLineChart.getBottomMarkerView().setDates(xVals);
        //移动当前的X到最后
        mKLineChart.moveViewToX(xVals.size());
        mKLineChart.invalidate();

        /****************************/
        //成交量图
        mVolumeChart.setData(mVolumeData);
        mVolumeChart.getXAxis().setAxisMinimum(-0.5f);
        mVolumeChart.getXAxis().setAxisMaximum(mVolumeData.getXMax() + 0.5f);
        //设置缩放（X坐标最多显示48个单位）,需要在设置数据后调用才有效
        mVolumeChart.setVisibleXRangeMaximum(48);
        mVolumeChart.post(new Runnable() {
            @Override public void run() {
                //控制缩放范围
                mVolumeChart.setVisibleXRangeMinimum(10);
                mVolumeChart.setVisibleXRangeMaximum(1000);
            }
        });
        //设置x轴的marker显示
        mVolumeChart.getBottomMarkerView().setDates(xVals);
        //移动当前的X到最后
        mVolumeChart.moveViewToX(xVals.size());
        mVolumeChart.invalidate();

        /****************************/
        //深度图
        mDepthChart.setData(mDepthData);
        mDepthChart.invalidate();

        //设置两表对齐
        setOffset();
        //绑定手势事件
        setChartListener();
    }

    private void initChartData() {
        int ma7Color = ContextCompat.getColor(this, R.color.ma7_color);
        int ma30Color = ContextCompat.getColor(this, R.color.ma30_color);

        //初始化数据
        btcDataList = Model.getBtcData(mType);
        List<CandleEntry> candleEntries = Model.getCandleEntries(btcDataList);
        int itemCount = candleEntries.size();
        xVals = new ArrayList<>();
        for (int i = 0; i < itemCount; i++) {
            xVals.add(btcDataList.get(i).getDate());
        }

        mKLineData = new CombinedData();
        /*k line*/
        CandleData candleData = generateCandleData(candleEntries);
        mKLineData.setData(candleData);
        /*ma7*/
        ma7Entries = Model.getCloseMaEntries(btcDataList, 7);
        /*ma30*/
        ma30Entries = Model.getCloseMaEntries(btcDataList, 30);

        LineData lineData = new LineData(generateLineDataSet(ma7Entries, ma7Color, "ma7"),
            generateLineDataSet(ma30Entries, ma30Color, "ma30"));
        mKLineData.setData(lineData);

        //成交量图
        mVolumeData = new CombinedData();
        mVolumeData.setData(generateBarData());
        /*成交量平均线曲线*/
        /*ma7*/
        ma7SecondEntries = Model.getMaVolumeEntries(btcDataList, 7);
        /*ma30*/
        ma30VolumeEntries = Model.getMaVolumeEntries(btcDataList, 30);
        LineData volumeLineData =
            new LineData(generateLineDataSet(ma7SecondEntries, ma7Color, "ma7"),
                generateLineDataSet(ma30VolumeEntries, ma30Color, "ma30"));
        mVolumeData.setData(volumeLineData);

        //深度图
        int sellColor = ContextCompat.getColor(this, R.color.increasing_color);
        int buyColor = ContextCompat.getColor(this, R.color.decreasing_color);
        List<Entry> buyEntries = Model.getBuyDepths();
        List<Entry> sellEntries = Model.getSellDepths();
        mDepthData = new LineData(generateDepthLineDataSet(buyEntries, buyColor, "买"),
            generateDepthLineDataSet(sellEntries, sellColor, "卖"));
    }

    private void initKLineChart() {
        mKLineChart = findViewById(R.id.k_line_chart);
        int chartBgColor = ContextCompat.getColor(this, R.color.chart_bg_color);
        int coordinateBgColor = ContextCompat.getColor(this, R.color.coordinate_bg_color);
        int coordinateTextColor = ContextCompat.getColor(this, R.color.coordinate_text_color);

        // scaling can now only be done on x- and y-axis separately
        mKLineChart.setPinchZoom(true);
        mKLineChart.setScaleXEnabled(true);   //允许X轴缩放
        mKLineChart.setScaleYEnabled(false);   //不允许Y轴缩放
        mKLineChart.setBackgroundColor(chartBgColor);
        mKLineChart.setDrawGridBackground(false);
        mKLineChart.setAutoScaleMinMaxEnabled(true);
        mKLineChart.setDescription(null);//右下角对图表的描述信息
        mKLineChart.setMinOffset(0f);//移除内边距
        mKLineChart.setExtraBottomOffset(10f); //添加Bottom X坐标的内边距，避免显示不全

        XAxis xAxis = mKLineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(8);//设置字体大小
        xAxis.setLabelCount(12, false);
        xAxis.setDrawGridLines(true);  //显示X轴网格
        xAxis.setDrawAxisLine(true);  //绘制坐标轴线
        xAxis.setGridColor(coordinateBgColor);//设置网格线的颜色
        xAxis.setGridLineWidth(0.5f);//设置网格线的宽度
        xAxis.setAxisLineColor(coordinateBgColor);//设置坐标轴的颜色
        xAxis.setAxisLineWidth(0.5f);//设置坐标轴的宽度
        xAxis.setTextColor(coordinateTextColor);//设置坐标轴的文本颜色
        //将X坐标转换显示
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override public String getFormattedValue(float value, AxisBase axis) {
                int index = (int) value;
                return TimeUtil.long2String(xVals.get(index), TimeUtil.CHART_FORMAT);
            }
        });

        YAxis leftAxis = mKLineChart.getAxisLeft();
        leftAxis.setEnabled(false);

        YAxis rightAxis = mKLineChart.getAxisRight();
        rightAxis.setTextSize(8);//设置字体大小
        rightAxis.setLabelCount(5, false);
        rightAxis.setDrawGridLines(true); ////显示Y轴网格
        rightAxis.setDrawAxisLine(true);  //绘制坐标轴线
        rightAxis.setGridColor(coordinateBgColor);//设置网格线的颜色
        rightAxis.setGridLineWidth(0.5f);//设置网格线的宽度
        rightAxis.setAxisLineColor(coordinateBgColor);//设置坐标轴的颜色
        rightAxis.setAxisLineWidth(0.5f);//设置坐标轴的宽度
        rightAxis.setTextColor(coordinateTextColor);//设置坐标轴的文本颜色
        //设置Y坐标的最大值和最小值，避免图表滚动时，Y坐标自适应范围
        //rightAxis.setAxisMaximum(25f);
        //rightAxis.setAxisMinimum(5f);
        rightAxis.setEnabled(true);  //显示右坐标
        rightAxis.setMinWidth(40);  //设置宽度，可能是要显示小数点后几位
        rightAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);  //设置坐标上值显示的位置
        //rightAxis.setStartAtZero(false);

        // 设置图标示
        mKLineChart.getLegend().setEnabled(false);
        mKLineChart.resetTracking();
        //设置绘制顺序，避免遮掩 (要在setData之前设置)
        mKLineChart.setDrawOrder(new CombinedChart.DrawOrder[] {
            CombinedChart.DrawOrder.CANDLE, CombinedChart.DrawOrder.LINE
        });

        //设置Left坐标轴上的markerView
        //mCombinedChart.setLeftMarkerView(new CoordinateMarkerView(this));
        mKLineChart.setRightMarkerView(new YMarkerView(this));
        mKLineChart.setLastMarkerView(new YLastMarkerView(this));
        mKLineChart.setBottomMarkerView(new XMarkerView(this));
        //设置交叉点的markerView
        mKLineChart.setCrossMarkerView(new CrossMarkerView(this));
    }

    private void initVolumeChart() {
        int chartBgColor = ContextCompat.getColor(this, R.color.chart_bg_color);
        int coordinateBgColor = ContextCompat.getColor(this, R.color.coordinate_bg_color);
        int coordinateTextColor = ContextCompat.getColor(this, R.color.coordinate_text_color);

        mVolumeChart = findViewById(R.id.volume_chart);
        mVolumeChart.setBackgroundColor(chartBgColor);
        mVolumeChart.setDrawValueAboveBar(true);
        mVolumeChart.getDescription().setEnabled(false);
        mVolumeChart.setDragEnabled(true);
        mVolumeChart.setScaleXEnabled(true);   //允许X轴缩放
        mVolumeChart.setScaleYEnabled(false);   //不允许Y轴缩放
        mVolumeChart.setAutoScaleMinMaxEnabled(true);
        mVolumeChart.setMinOffset(0f); //移除内边距
        mVolumeChart.setExtraBottomOffset(5f);//添加Bottom X坐标的内边距，避免显示不全

        Legend barChartLegend = mVolumeChart.getLegend();
        barChartLegend.setEnabled(false);
        //bar x轴
        XAxis xAxisBar = mVolumeChart.getXAxis();
        xAxisBar.setDrawGridLines(false);
        xAxisBar.setEnabled(false);//不显示坐标轴：和KLine图表共用一个X坐标轴
        xAxisBar.setDrawAxisLine(true);//绘制坐标轴
        xAxisBar.setAxisLineColor(coordinateBgColor);//设置坐标轴的颜色
        xAxisBar.setAxisLineWidth(0.5f);//设置坐标轴的宽度
        xAxisBar.setTextColor(coordinateTextColor);//设置坐标轴的文本颜色
        xAxisBar.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxisBar.setValueFormatter(new IAxisValueFormatter() {
            @Override public String getFormattedValue(float value, AxisBase axis) {
                int index = (int) value;
                return TimeUtil.long2String(xVals.get(index), TimeUtil.CHART_FORMAT);
            }
        });

        YAxis axisLeftBar = mVolumeChart.getAxisLeft();
        axisLeftBar.setEnabled(false);

        YAxis axisRightBar = mVolumeChart.getAxisRight();
        axisRightBar.setTextSize(8);//设置字体大小
        axisRightBar.setLabelCount(2, false);
        axisRightBar.setDrawGridLines(true); //显示横线
        axisRightBar.setGridColor(coordinateBgColor);//设置网格线的颜色
        axisRightBar.setGridLineWidth(0.5f);//设置网格线的宽度
        axisRightBar.setDrawAxisLine(true);  //绘制坐标轴线
        axisRightBar.setEnabled(true);  //显示右坐标
        axisRightBar.setAxisLineColor(coordinateBgColor);//设置坐标轴的颜色
        axisRightBar.setAxisLineWidth(0.5f);//设置坐标轴的宽度
        axisRightBar.setTextColor(coordinateTextColor);//设置坐标轴的文本颜色
        axisRightBar.setMinWidth(40);  //设置宽度，可能是要显示小数点后几位
        axisRightBar.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);  //设置坐标上值显示的位置
        axisRightBar.setAxisMinimum(0.0f);//设置Y轴显示最小值，不然0下面会有空隙

        //设置Left坐标轴上的markerView
        //mCombinedChart.setLeftMarkerView(new CoordinateMarkerView(this));
        mVolumeChart.setRightMarkerView(new YMarkerView(this));
        mVolumeChart.setLastMarkerView(new YLastMarkerView(this));
        mVolumeChart.setBottomMarkerView(new XMarkerView(this));
    }

    private void initDepthChart() {
        mDepthChart = findViewById(R.id.depth_chart);
        int chartBgColor = ContextCompat.getColor(this, R.color.chart_bg_color);
        int coordinateBgColor = ContextCompat.getColor(this, R.color.coordinate_bg_color);
        int coordinateTextColor = ContextCompat.getColor(this, R.color.coordinate_text_color);

        // scaling can now only be done on x- and y-axis separately
        mDepthChart.setPinchZoom(false);
        mDepthChart.setScaleXEnabled(false);   //不允许X轴缩放
        mDepthChart.setScaleYEnabled(false);   //不允许Y轴缩放

        mDepthChart.setBackgroundColor(chartBgColor);
        mDepthChart.setDrawGridBackground(false);
        mDepthChart.setAutoScaleMinMaxEnabled(false);
        mDepthChart.setDescription(null);//右下角对图表的描述信息
        mDepthChart.setMinOffset(8f);//移除内边距
        mDepthChart.setExtraBottomOffset(5f); //添加Bottom X坐标的内边距，避免显示不全

        XAxis xAxis = mDepthChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(8);//设置字体大小
        xAxis.setLabelCount(3, true);
        xAxis.setDrawGridLines(false);  //显示X轴网格
        xAxis.setDrawAxisLine(true);  //绘制坐标轴线
        xAxis.setGridColor(coordinateBgColor);//设置网格线的颜色
        xAxis.setGridLineWidth(0.5f);//设置网格线的宽度
        xAxis.setAxisLineColor(coordinateBgColor);//设置坐标轴的颜色
        xAxis.setAxisLineWidth(0.5f);//设置坐标轴的宽度
        xAxis.setTextColor(coordinateTextColor);//设置坐标轴的文本颜色

        YAxis leftAxis = mDepthChart.getAxisLeft();
        leftAxis.setEnabled(true);
        leftAxis.setTextSize(8);//设置字体大小
        //leftAxis.setLabelCount(5, false);
        leftAxis.setDrawGridLines(false); ////显示Y轴网格
        leftAxis.setDrawAxisLine(true);  //绘制坐标轴线
        leftAxis.setGridColor(coordinateBgColor);//设置网格线的颜色
        leftAxis.setGridLineWidth(0.5f);//设置网格线的宽度
        leftAxis.setAxisLineColor(coordinateBgColor);//设置坐标轴的颜色
        leftAxis.setAxisLineWidth(0.5f);//设置坐标轴的宽度
        leftAxis.setTextColor(coordinateTextColor);//设置坐标轴的文本颜色
        leftAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);  //设置坐标上值显示的位置

        YAxis rightAxis = mDepthChart.getAxisRight();
        rightAxis.setEnabled(false);

        // 设置图标示
        mDepthChart.getLegend().setEnabled(true);
        mDepthChart.getLegend().setTextColor(coordinateTextColor);
        //设置图标示例居中显示
        mDepthChart.getLegend().setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        mDepthChart.getLegend().setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        mDepthChart.resetTracking();
    }

    private LineDataSet generateLineDataSet(List<Entry> entries, int color, String label) {
        LineDataSet set = new LineDataSet(entries, label);
        set.setColor(color);
        set.setLineWidth(0.5f); //线条宽度
        set.setCubicIntensity(0.5f);//圆滑曲线
        set.setDrawCircles(false);
        set.setDrawCircleHole(false);
        set.setDrawValues(false);
        set.setHighlightEnabled(false);
        set.setAxisDependency(YAxis.AxisDependency.RIGHT);
        return set;
    }

    private LineDataSet generateDepthLineDataSet(List<Entry> entries, int color, String label) {
        LineDataSet set = new LineDataSet(entries, label);
        set.setColor(color);
        set.setLineWidth(1f); //线条宽度
        set.setCubicIntensity(0.5f);//圆滑曲线
        set.setDrawCircles(false);
        set.setDrawCircleHole(false);
        set.setDrawValues(false);
        set.setHighlightEnabled(false);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setDrawFilled(true);  //绘制填充色
        set.setFillColor(color);
        set.setFillAlpha(40);   //设置透明度
        return set;
    }

    private CandleData generateCandleData(List<CandleEntry> candleEntries) {
        int increasingColor = ContextCompat.getColor(this, R.color.increasing_color);
        int decreasingColor = ContextCompat.getColor(this, R.color.decreasing_color);
        int highLineColor = ContextCompat.getColor(this, R.color.high_line_color);

        CandleDataSet set = new CandleDataSet(candleEntries, "");
        set.setAxisDependency(YAxis.AxisDependency.RIGHT);
        set.setShadowWidth(0.7f);
        set.setDecreasingColor(decreasingColor);
        set.setDecreasingPaintStyle(Paint.Style.FILL_AND_STROKE);
        set.setIncreasingColor(increasingColor);
        set.setIncreasingPaintStyle(Paint.Style.FILL_AND_STROKE);
        set.setShadowColorSameAsCandle(true);
        set.setHighlightLineWidth(0.5f);  //选中蜡烛时的高亮线的宽度
        set.setHighLightColor(highLineColor);  //线的颜色
        set.setValueTextColor(ContextCompat.getColor(this, R.color.coordinate_text_color));

        CandleData candleData = new CandleData();
        candleData.addDataSet(set);

        return candleData;
    }

    private BarData generateBarData() {
        int increasingColor = ContextCompat.getColor(this, R.color.increasing_color);
        int decreasingColor = ContextCompat.getColor(this, R.color.decreasing_color);

        List<BarEntry> barEntries = Model.getBarEntries(btcDataList);
        BarDataSet barDataSet = new BarDataSet(barEntries, "成交量");
        barDataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);
        barDataSet.setHighLightAlpha(255);
        barDataSet.setDrawValues(false);
        barDataSet.setHighlightEnabled(true);
        barDataSet.setHighLightColor(ContextCompat.getColor(this, R.color.high_line_color));
        //修改了源码，这里传入的第一个颜色表示上涨时的颜色，第二个颜色表示下跌的颜色
        barDataSet.setColors(increasingColor, decreasingColor);

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
                Highlight highlight =
                    new Highlight(h.getX(), h.getY(), h.getXPx(), h.getYPx(), h.getDataSetIndex(),
                        mKLineChart.getAxisRight().getAxisDependency());
                mKLineChart.highlightValues(new Highlight[] { h });
            }

            @Override public void onNothingSelected() {
                //清空高亮线
                mKLineChart.highlightValues(null);
                mVolumeChart.highlightValue(null);
            }
        });
        mKLineChart.setHighestXDrawListener(new HighestXDrawListener() {
            @Override public void onHighestXDraw(float highestX) {
                //更新显示的数据
                updateText(highestX);
            }
        });
    }

    private void updateText(float x) {
        int index = (int) x;

        BarEntry barEntry = mVolumeChart.getBarData().getDataSetByIndex(0).getEntryForIndex(index);

        TextView firstMa7 = findViewById(R.id.tv_first_ma7);
        TextView firstMa30 = findViewById(R.id.tv_first_ma30);
        TextView secondMa7 = findViewById(R.id.tv_second_ma7);
        TextView secondMa30 = findViewById(R.id.tv_second_ma30);
        TextView secondVolume = findViewById(R.id.tv_second_volume);

        firstMa7.setText("MA7: " + getLineEntry(x, ma7Entries).getY());
        firstMa30.setText("MA30: " + getLineEntry(x, ma30Entries).getY());
        secondMa7.setText("MA7: " + getLineEntry(x, ma7SecondEntries).getY());
        secondMa30.setText("MA7: " + getLineEntry(x, ma30VolumeEntries).getY());
        secondVolume.setText("Volume: " + barEntry.getY());
    }

    private Entry getLineEntry(float x, List<Entry> entries) {
        for (Entry entry : entries) {
            if (entry.getX() == x) {
                return entry;
            }
        }
        return new Entry();
    }
}
