package com.example.kchart.mychart;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;

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
    private CoordinateMarkerView mLeftMarkerView;
    private CoordinateMarkerView mRightMarkerView;
    private CoordinateMarkerView mBottomMarkerView;
    //交叉点的marker
    private CrossMarkerView mCrossMarkerView;

    public MyCombinedChart(Context context) {
        this(context, null);
    }

    public MyCombinedChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setLeftMarkerView(CoordinateMarkerView leftMarkerView) {
        this.mLeftMarkerView = leftMarkerView;
    }

    public void setRightMarkerView(CoordinateMarkerView rightMarkerView) {
        this.mRightMarkerView = rightMarkerView;
    }

    public void setBottomMarkerView(CoordinateMarkerView bottomMarkerView) {
        this.mBottomMarkerView = bottomMarkerView;
    }

    public void setCrossMarkerView(CrossMarkerView crossMarkerView) {
        this.mCrossMarkerView = crossMarkerView;
    }

    /**
     * 绘制markers:拷贝Chart类中的源码，然后修改
     */
    @Override protected void drawMarkers(Canvas canvas) {
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
                mLeftMarkerView.refreshContent(e.getY(), e, highlight);
                // draw the marker:pos[0]对应的坐标是X,pos[1]对应的坐标是Y
                mLeftMarkerView.draw(canvas,
                    mViewPortHandler.contentLeft() - mLeftMarkerView.getWidth(),
                    pos[1] - mLeftMarkerView.getHeight() / 2);
            }

            if (mRightMarkerView != null) {
                mRightMarkerView.refreshContent(e.getY(), e, highlight);
                mRightMarkerView.draw(canvas, mViewPortHandler.contentRight(),
                    pos[1] - mRightMarkerView.getHeight() / 2);
            }

            if (mBottomMarkerView != null) {
                mBottomMarkerView.refreshContent(e.getX(), e, highlight);
                mBottomMarkerView.draw(canvas, pos[0] - mBottomMarkerView.getWidth() / 2,
                    mViewPortHandler.contentBottom());
            }

            if (mCrossMarkerView != null) {
                mCrossMarkerView.refreshContent(e, highlight);
                mCrossMarkerView.draw(canvas, pos[0], pos[1]);
            }
        }
    }
}
