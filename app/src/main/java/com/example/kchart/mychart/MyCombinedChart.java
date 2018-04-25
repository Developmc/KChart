package com.example.kchart.mychart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import com.example.kchart.R;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.dataprovider.BarLineScatterCandleBubbleDataProvider;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.IBarLineScatterCandleBubbleDataSet;
import com.github.mikephil.charting.interfaces.datasets.ICandleDataSet;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;

/**
 * <pre>
 *     author : Clement
 *     time   : 2018/04/19
 *     desc   : 自定义CombinedChart：实现坐标marker
 *     version: 1.0
 * </pre>
 */
public class MyCombinedChart extends CombinedChart {
    //左侧Y轴marker
    private YMarkerView mLeftMarkerView;
    private YMarkerView mRightMarkerView;
    private YLastMarkerView mLastMarkerView;
    private XMarkerView mBottomMarkerView;
    //交叉点的marker
    private CrossMarkerView mCrossMarkerView;
    private HighestXDrawListener mHighestXDrawListener;

    private Paint mPaint;

    public MyCombinedChart(Context context) {
        this(context, null);
    }

    public MyCombinedChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }

    public void setLeftMarkerView(YMarkerView leftMarkerView) {
        this.mLeftMarkerView = leftMarkerView;
    }

    public void setRightMarkerView(YMarkerView rightMarkerView) {
        this.mRightMarkerView = rightMarkerView;
    }

    public void setBottomMarkerView(XMarkerView bottomMarkerView) {
        this.mBottomMarkerView = bottomMarkerView;
    }

    public XMarkerView getBottomMarkerView() {
        return this.mBottomMarkerView;
    }

    public void setLastMarkerView(YLastMarkerView lastMarkerView) {
        this.mLastMarkerView = lastMarkerView;
    }

    public void setCrossMarkerView(CrossMarkerView crossMarkerView) {
        this.mCrossMarkerView = crossMarkerView;
    }

    public void setHighestXDrawListener(HighestXDrawListener listener) {
        this.mHighestXDrawListener = listener;
    }

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setColor(ContextCompat.getColor(getContext(), R.color.coordinate_text_color));
        mPaint.setTextSize(Utils.convertDpToPixel(10));
    }

    /**
     * 绘制markers:拷贝Chart类中的源码，然后修改
     */
    @Override protected void drawMarkers(Canvas canvas) {
        //绘制最右侧entry对应的值
        drawLastEntryMarker(canvas);

        // if there is no marker view or drawing marker is disabled
        if (!isDrawMarkersEnabled() || !valuesToHighlight()) return;

        for (int i = 0; i < mIndicesToHighlight.length; i++) {

            Highlight highlight = mIndicesToHighlight[i];

            IDataSet set = mData.getDataSetByIndex(highlight.getDataSetIndex());

            Entry e = mData.getEntryForHighlight(mIndicesToHighlight[i]);
            int entryIndex = set.getEntryIndex(e);

            // make sure entry not null
            if (e == null || entryIndex > set.getEntryCount() * mAnimator.getPhaseX()) continue;

            float[] pos = getMarkerPosition(highlight);

            // check bounds
            if (!mViewPortHandler.isInBounds(pos[0], pos[1])) continue;

            if (mLeftMarkerView != null) {
                // callbacks to update the content
                mLeftMarkerView.refreshContent(e, highlight);
                // draw the marker:pos[0]对应的坐标是X,pos[1]对应的坐标是Y
                mLeftMarkerView.draw(canvas,
                    mViewPortHandler.contentLeft() - mLeftMarkerView.getWidth(),
                    pos[1] - mLeftMarkerView.getHeight() / 2);
            }

            if (mRightMarkerView != null) {
                mRightMarkerView.refreshContent(e, highlight);
                mRightMarkerView.draw(canvas, mViewPortHandler.contentRight(),
                    pos[1] - mRightMarkerView.getHeight() / 2);
            }

            if (mBottomMarkerView != null) {
                mBottomMarkerView.refreshContent(e, highlight);
                mBottomMarkerView.draw(canvas, pos[0] - mBottomMarkerView.getWidth() / 2,
                    mViewPortHandler.contentBottom());
            }

            if (mCrossMarkerView != null) {
                mCrossMarkerView.refreshContent(e, highlight);
                mCrossMarkerView.draw(canvas, pos[0], pos[1]);
            }
        }
    }

    @Override protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制蜡烛图的最大值和最小值
        showCandleMaxMinMarker(canvas);
        //监听绘制X轴
        if (mHighestXDrawListener != null) {
            mHighestXDrawListener.onHighestXDraw(mXBounds.max);
        }
    }

    /**
     * https://www.jianshu.com/p/e2c0c8a31b09
     * 设置最大值蜡烛图标记
     */
    private void showCandleMaxMinMarker(Canvas canvas) {
        CandleData candleData = this.getCandleData();
        if (candleData != null) {
            for (ICandleDataSet set : candleData.getDataSets()) {
                if (set.isVisible()) {
                    drawMaxMinCandleMarker(canvas, set);
                }
            }
        }
    }

    /**
     * 绘制最大最小值蜡烛图标记
     */
    private void drawMaxMinCandleMarker(Canvas c, ICandleDataSet dataSet) {
        mXBounds.set(this, dataSet);
        float maxFloat = Float.MIN_VALUE;
        float minFloat = Float.MAX_VALUE;

        //坐标系上最右边的entry
        CandleEntry rightEntry = dataSet.getEntryForIndex(mXBounds.max);
        CandleEntry maxEntry = null;
        CandleEntry minEntry = null;
        for (int j = mXBounds.min; j <= mXBounds.range + mXBounds.min; j++) {
            // get the entry
            CandleEntry e = dataSet.getEntryForIndex(j);
            if (e == null) continue;
            final float high = e.getHigh();
            final float low = e.getLow();
            //求最大值和最小值
            if (high > maxFloat) {
                maxEntry = e;
                maxFloat = high;
            }
            if (low < minFloat) {
                minEntry = e;
                minFloat = low;
            }
        }

        int triangleWidth = 20;
        float[] mBodyBuffers = new float[4];

        if (maxEntry != null) {
            float[] maxPixels = getCandleEntryPixel(maxEntry, dataSet);
            float left = maxPixels[0];
            float top = maxPixels[3];
            float right = maxPixels[2];

            float x = left + (right - left) / 2;
            float rightX = getRightX(rightEntry, dataSet);

            int textWidth = Utils.calcTextWidth(mPaint, "" + maxFloat);
            int textHeight = Utils.calcTextHeight(mPaint, "" + maxFloat);
            //如果右侧有足够的空间，则显示在右侧
            if ((x + triangleWidth + textWidth) < rightX) {
                //绘制一条线和文本
                c.drawLine(x, top, x + triangleWidth, top, mPaint);
                c.drawText(String.valueOf(maxFloat), x + triangleWidth, top + textHeight / 2,
                    mPaint);
            }
            //右侧空间不够，显示在左侧
            else {
                //绘制一条线和文本
                c.drawLine(x, top, x - triangleWidth, top, mPaint);
                c.drawText(String.valueOf(maxFloat), x - triangleWidth - textWidth,
                    top + textHeight / 2, mPaint);
            }
        }
        if (minEntry != null) {
            float[] minPixels = getCandleEntryPixel(minEntry, dataSet);
            float left = minPixels[0];
            float right = minPixels[2];
            float bottom = minPixels[1];

            float x = left + (right - left) / 2;
            float rightX = getRightX(rightEntry, dataSet);

            int textWidth = Utils.calcTextWidth(mPaint, "" + minFloat);
            int textHeight = Utils.calcTextHeight(mPaint, "" + minFloat);

            //如果右侧有足够的空间，则显示在右侧
            if ((x + triangleWidth + textWidth) < rightX) {
                //绘制一条线和文本
                c.drawLine(x, bottom, x + triangleWidth, bottom, mPaint);
                c.drawText(String.valueOf(minFloat), x + triangleWidth, bottom + textHeight / 2,
                    mPaint);
            }
            //右侧空间不够，显示在左侧
            else {
                //绘制一条线和文本
                c.drawLine(x, bottom, x - triangleWidth, bottom, mPaint);
                c.drawText(String.valueOf(minFloat), x - triangleWidth - textWidth,
                    bottom + textHeight / 2, mPaint);
            }
        }
    }

    /**
     * 将CandleEntry所在的坐标转为Pixel
     */
    private float[] getCandleEntryPixel(CandleEntry rightEntry, ICandleDataSet dataSet) {
        Transformer trans = this.getTransformer(dataSet.getAxisDependency());

        float phaseY = mAnimator.getPhaseY();
        float barSpace = dataSet.getBarSpace();

        final float xPos = rightEntry.getX();
        final float open = rightEntry.getOpen();
        final float close = rightEntry.getClose();
        final float high = rightEntry.getHigh();
        final float low = rightEntry.getLow();

        float[] mBodyBuffers = new float[4];
        mBodyBuffers[0] = xPos + barSpace;
        mBodyBuffers[1] = low * phaseY;
        mBodyBuffers[2] = (xPos - barSpace);
        mBodyBuffers[3] = high * phaseY;
        trans.pointValuesToPixel(mBodyBuffers);

        //float left = mBodyBuffers[0];
        //float top = mBodyBuffers[3];
        //float right = mBodyBuffers[2];
        //float bottom = mBodyBuffers[1];
        return mBodyBuffers;
    }

    /**
     * 将BarEntry所在的坐标转为Pixel
     */
    private float[] getBarEntryPixel(BarEntry rightEntry, IBarDataSet dataSet) {
        Transformer trans = this.getTransformer(dataSet.getAxisDependency());

        float phaseY = mAnimator.getPhaseY();

        final float xPos = rightEntry.getX();
        final float y = rightEntry.getY();

        float[] mBodyBuffers = new float[4];
        mBodyBuffers[1] = 0;
        mBodyBuffers[3] = y * phaseY;
        trans.pointValuesToPixel(mBodyBuffers);

        //float left = mBodyBuffers[0];
        //float top = mBodyBuffers[3];
        //float right = mBodyBuffers[2];
        //float bottom = mBodyBuffers[1];
        return mBodyBuffers;
    }

    /**
     * 将坐标值转为pixel
     */
    private float getRightX(CandleEntry rightEntry, ICandleDataSet dataSet) {
        float[] mBodyBuffers = getCandleEntryPixel(rightEntry, dataSet);
        float left = mBodyBuffers[0];
        float right = mBodyBuffers[2];

        return left + (right - left) / 2;
    }

    /**
     * 绘制最右侧entry对应的值
     */
    private void drawLastEntryMarker(Canvas canvas) {
        if (mLastMarkerView != null) {
            //蜡烛图
            CandleData candleData = this.getCandleData();
            if (candleData != null) {
                for (ICandleDataSet candleSet : candleData.getDataSets()) {
                    if (candleSet.isVisible()) {
                        XBounds tempBounds = new XBounds();
                        tempBounds.set(this, candleSet);
                        //坐标系上最右边的entry
                        CandleEntry rightEntry = candleSet.getEntryForIndex(tempBounds.max);
                        float[] maxPixels = getCandleEntryPixel(rightEntry, candleSet);
                        float posY = maxPixels[3];
                        mLastMarkerView.refreshContent(rightEntry, null);
                        mLastMarkerView.draw(canvas, mViewPortHandler.contentRight(),
                            posY - mLastMarkerView.getHeight() / 2);
                    }
                }
            }

            //BarChart
            BarData barData = this.getBarData();
            if (barData != null) {
                for (IBarDataSet barDataSet : barData.getDataSets()) {
                    if (barDataSet.isVisible()) {
                        XBounds tempBounds = new XBounds();
                        tempBounds.set(this, barDataSet);
                        //坐标系上最右边的entry
                        BarEntry rightEntry = barDataSet.getEntryForIndex(tempBounds.max);
                        float[] maxPixels = getBarEntryPixel(rightEntry, barDataSet);
                        float posY = maxPixels[3];
                        mLastMarkerView.refreshContent(rightEntry, null);
                        mLastMarkerView.draw(canvas, mViewPortHandler.contentRight(),
                            posY - mLastMarkerView.getHeight() / 2);
                    }
                }
            }
        }
    }

    protected XBounds mXBounds = new XBounds();

    /**
     * Class representing the bounds of the current viewport in terms of indices in the values array
     * of a DataSet.
     */
    protected class XBounds {

        /**
         * minimum visible entry index
         */
        public int min;

        /**
         * maximum visible entry index
         */
        public int max;

        /**
         * range of visible entry indices
         */
        public int range;

        /**
         * Calculates the minimum and maximum x values as well as the range between them.
         */
        public void set(BarLineScatterCandleBubbleDataProvider chart,
            IBarLineScatterCandleBubbleDataSet dataSet) {
            float phaseX = Math.max(0.f, Math.min(1.f, mAnimator.getPhaseX()));

            float low = chart.getLowestVisibleX();
            float high = chart.getHighestVisibleX();

            Entry entryFrom = dataSet.getEntryForXValue(low, Float.NaN, DataSet.Rounding.DOWN);
            Entry entryTo = dataSet.getEntryForXValue(high, Float.NaN, DataSet.Rounding.UP);

            min = entryFrom == null ? 0 : dataSet.getEntryIndex(entryFrom);
            max = entryTo == null ? 0 : dataSet.getEntryIndex(entryTo);
            range = (int) ((max - min) * phaseX);
        }
    }
}
