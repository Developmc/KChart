package com.example.kchart;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import com.example.kchart.mychart.CrossMarkerView;
import com.example.kchart.mychart.MyCombinedChart;
import com.example.kchart.mychart.XMarkerView;
import com.example.kchart.mychart.YMarkerView;
import com.example.kchart.test.Model;
import com.example.kchart.test.StockListBean;
import com.github.mikephil.charting.charts.CandleStickChart;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private CandleStickChart mCandleChart;
    private LineChart mLineChart;
    private MyCombinedChart mCombinedChart;

    private int itemcount;
    private List<CandleEntry> candleEntries = new ArrayList<>();
    private ArrayList<String> xVals;
    private LineData lineData;
    private CandleData candleData;
    private CombinedData combinedData;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        //显示组合图
        //initCombinedChart();
        //有测试数据的组合图
        loadTestChartData();

        //显示折线图
        //initLineChart();

        //显示K线图
        //initCandleChart();
    }

    private void initLineChart() {
        mLineChart = findViewById(R.id.line_chart);
        int color = ContextCompat.getColor(this, R.color.dark);

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        mLineChart.setMaxVisibleValueCount(60);
        // scaling can now only be done on x- and y-axis separately
        mLineChart.setPinchZoom(false);
        mLineChart.setBackgroundColor(color);
        mLineChart.setDrawGridBackground(false);
        XAxis xAxis = mLineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);

        YAxis leftAxis = mLineChart.getAxisLeft();
        //        leftAxis.setEnabled(false);
        leftAxis.setLabelCount(7, false);
        leftAxis.setDrawGridLines(false);
        leftAxis.setDrawAxisLine(false);

        YAxis rightAxis = mLineChart.getAxisRight();
        rightAxis.setEnabled(false);
        //        rightAxis.setStartAtZero(false);
        // 设置比例图标示
        mLineChart.getLegend().setEnabled(true);
        mLineChart.resetTracking();

        LineData data = getLineDatas(40, 30);
        mLineChart.setData(data);
        mLineChart.invalidate();
    }

    /**
     * 显示组合图
     */
    private void initCombinedChart() {
        mCombinedChart = findViewById(R.id.combined_chart);
        int color = ContextCompat.getColor(this, R.color.dark);

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        mCombinedChart.setMaxVisibleValueCount(60);
        // scaling can now only be done on x- and y-axis separately
        mCombinedChart.setPinchZoom(false);
        mCombinedChart.setBackgroundColor(color);
        mCombinedChart.setDrawGridBackground(false);
        XAxis xAxis = mCombinedChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);

        YAxis leftAxis = mCombinedChart.getAxisLeft();
        //        leftAxis.setEnabled(false);
        leftAxis.setLabelCount(7, false);
        leftAxis.setDrawGridLines(false);
        leftAxis.setDrawAxisLine(false);

        YAxis rightAxis = mCombinedChart.getAxisRight();
        rightAxis.setEnabled(false);
        //        rightAxis.setStartAtZero(false);
        // 设置比例图标示
        mCombinedChart.getLegend().setEnabled(true);
        mCombinedChart.resetTracking();

        CombinedData combinedData = new CombinedData();
        combinedData.setData(getLineDatas(40, 30));
        combinedData.setData(getCandleData(40));
        //设置绘制顺序，避免遮掩 (要在setData之前设置)
        mCombinedChart.setDrawOrder(new CombinedChart.DrawOrder[] {
            CombinedChart.DrawOrder.CANDLE, CombinedChart.DrawOrder.LINE
        });
        mCombinedChart.setData(combinedData);  //设置数据源
        mCombinedChart.invalidate();
        //mCombinedChart.animateX(2500);
    }

    private void initCandleChart() {
        mCandleChart = findViewById(R.id.candler_chart);
        int color = ContextCompat.getColor(this, R.color.dark);

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        mCandleChart.setMaxVisibleValueCount(60);
        // scaling can now only be done on x- and y-axis separately
        mCandleChart.setPinchZoom(false);
        mCandleChart.setBackgroundColor(color);
        mCandleChart.setDrawGridBackground(false);
        XAxis xAxis = mCandleChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);

        YAxis leftAxis = mCandleChart.getAxisLeft();
        //        leftAxis.setEnabled(false);
        leftAxis.setLabelCount(7, false);
        leftAxis.setDrawGridLines(false);
        leftAxis.setDrawAxisLine(false);

        YAxis rightAxis = mCandleChart.getAxisRight();
        rightAxis.setEnabled(false);
        //        rightAxis.setStartAtZero(false);
        // 设置比例图标示
        mCandleChart.getLegend().setEnabled(true);
        mCandleChart.resetTracking();

        CandleData data = getCandleData(40);
        mCandleChart.setData(data);
        mCandleChart.invalidate();
        //从左往右绘制，不需要再调用invalidate函数
        //mChart.animateX(2500);
    }

    private CandleData getCandleData(int progress) {
        ArrayList<CandleEntry> yVals = new ArrayList<CandleEntry>();

        for (int i = 0; i < progress; i++) {
            float mult = progress + 1;
            float val = (float) (Math.random() * 40) + mult;

            float high = (float) (Math.random() * 9) + 8f;
            float low = (float) (Math.random() * 9) + 8f;

            float open = (float) (Math.random() * 6) + 1f;
            float close = (float) (Math.random() * 6) + 1f;

            boolean even = i % 2 == 0;

            //CandleEntry的参数：x表示x坐标，shadowH表示影线的最大值，shadowL表示影线的最小值，open表示开盘价，close表示收盘价
            yVals.add(new CandleEntry(i, val + high, val - low, even ? val + open : val - open,
                even ? val - close : val + close, getResources().getDrawable(R.drawable.star)));
        }

        CandleDataSet set = new CandleDataSet(yVals, "Data Set");

        set.setDrawValues(false);//在图表中的元素上面是否显示数值
        set.setDrawIcons(false);//是否显示icon
        set.setAxisDependency(YAxis.AxisDependency.LEFT);

        set.setShadowColor(Color.DKGRAY); //影线颜色
        set.setShadowWidth(0.7f); //影线宽度
        set.setShadowColorSameAsCandle(true);  //影线颜色和实体一致

        set.setIncreasingColor(Color.RED);  //红色升
        set.setIncreasingPaintStyle(Paint.Style.FILL);  //填充模式

        set.setDecreasingColor(Color.GREEN);
        set.setDecreasingPaintStyle(Paint.Style.STROKE);  //边框模式

        set.setNeutralColor(Color.BLUE);  //当天价格不涨不跌（一字线）颜色

        set.setHighlightLineWidth(1f); //选中蜡烛时的线宽

        //图表名称，可以通过mChart.getLegend().setEnable(true)显示在标注上
        set.setLabel("KLine");

        CandleData data = new CandleData(set);
        return data;
    }

    private LineData getLineDatas(int count, float range) {

        ArrayList<Entry> yVals1 = new ArrayList<Entry>();

        for (int i = 0; i < count; i++) {
            //float mult = range / 2f;
            //float val = getRandom(2) + 40;
            float val = 0L;
            int temp = getRandom(i + 1);
            if (i < 10) {
                val = 40 + temp;
            } else if (i < 20) {
                val = 40 + temp * 0.7F;
            } else if (i < 30) {
                val = 50 - temp;
            } else {
                val = 40 + temp * 0.2F;
            }
            yVals1.add(new Entry(i, val));
        }

        ArrayList<Entry> yVals2 = new ArrayList<Entry>();

        for (int i = 0; i < count - 1; i++) {
            float mult = range;
            float val = (float) getRandom(3) + 50;
            yVals2.add(new Entry(i, val));
        }

        ArrayList<Entry> yVals3 = new ArrayList<Entry>();

        for (int i = 0; i < count; i++) {
            float mult = range;
            float val = (float) getRandom(5) + 60;
            yVals3.add(new Entry(i, val));
        }

        LineDataSet set1 = getLineDateSet(yVals1, Color.BLUE, "set1");
        LineDataSet set2 = getLineDateSet(yVals2, Color.WHITE, "set2");
        LineDataSet set3 = getLineDateSet(yVals3, Color.YELLOW, "set3");
        // create a data object with the datasets
        LineData lineData = new LineData(set1, set2, set3);
        lineData.setValueTextColor(Color.WHITE);
        lineData.setValueTextSize(9f);

        return lineData;
    }

    private int getRandom(int value) {
        Random random = new Random();
        return random.nextInt(value);
    }

    private LineDataSet getLineDateSet(List<Entry> entries, int color, String label) {
        LineDataSet set = new LineDataSet(entries, label);
        set.setColor(color);
        set.setLineWidth(1f);
        set.setDrawCircles(false);
        set.setDrawCircleHole(false);
        set.setDrawValues(false);
        set.setHighlightEnabled(false);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setDrawFilled(false);//有填充颜色
        set.setCubicIntensity(0.01f);
        return set;
    }

    //https://github.com/colin-phang/KLineChartDemo/blob/master/app/src/main/java/android/colin/democandlechart/MainActivity.java

    /***** 从github上找到的测试数据 *****/

    private void loadTestChartData() {
        mCombinedChart = findViewById(R.id.combined_chart);
        int color = ContextCompat.getColor(this, R.color.dark);

        // scaling can now only be done on x- and y-axis separately
        mCombinedChart.setPinchZoom(false);
        mCombinedChart.setBackgroundColor(color);
        mCombinedChart.setDrawGridBackground(false);
        mCombinedChart.setAutoScaleMinMaxEnabled(true);
        mCombinedChart.setDescription(null);//右下角对图表的描述信息

        XAxis xAxis = mCombinedChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        //将X坐标转换显示
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override public String getFormattedValue(float value, AxisBase axis) {
                int index = (int) value;
                //这里时间注意要乘以1000
                return xVals.get(index);
            }
        });

        YAxis leftAxis = mCombinedChart.getAxisLeft();
        leftAxis.setLabelCount(8, false);
        leftAxis.setDrawGridLines(false);
        leftAxis.setDrawAxisLine(true);  //绘制坐标轴线
        //设置Y坐标的最大值和最小值，避免图表滚动时，Y坐标自适应范围
        leftAxis.setAxisMaximum(25f);
        leftAxis.setAxisMinimum(5f);
        leftAxis.setEnabled(false);  //不显示左边的Y坐标
        leftAxis.setMinWidth(50);  //设置宽度
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);  //在外部显示

        YAxis rightAxis = mCombinedChart.getAxisRight();
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

        // 设置比例图标示
        mCombinedChart.getLegend().setEnabled(true);
        mCombinedChart.resetTracking();
        //设置绘制顺序，避免遮掩 (要在setData之前设置)
        mCombinedChart.setDrawOrder(new CombinedChart.DrawOrder[] {
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
        combinedData = new CombinedData();

        /*k line*/
        candleData = generateCandleData();
        combinedData.setData(candleData);
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

        combinedData.setData(lineData);
        mCombinedChart.setData(combinedData);//当前屏幕会显示所有的数据

        //设置缩放（X坐标最多显示40个单位）
        mCombinedChart.setVisibleXRangeMaximum(40);
        //移动当前的X到最后
        mCombinedChart.moveViewToX(itemcount);
        mCombinedChart.invalidate();

        //设置选中的监听器
        mCombinedChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override public void onValueSelected(Entry e, Highlight h) {

            }

            @Override public void onNothingSelected() {

            }
        });

        //设置Left坐标轴上的markerView
        //mCombinedChart.setLeftMarkerView(new CoordinateMarkerView(this));
        mCombinedChart.setRightMarkerView(new YMarkerView(this));
        mCombinedChart.setBottomMarkerView(new XMarkerView(this, xVals));
        //设置交叉点的markerView
        mCombinedChart.setCrossMarkerView(new CrossMarkerView(this));
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
        set.setAxisDependency(YAxis.AxisDependency.LEFT);

        return set;
    }

    private CandleData generateCandleData() {

        CandleDataSet set = new CandleDataSet(candleEntries, "");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
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
}
