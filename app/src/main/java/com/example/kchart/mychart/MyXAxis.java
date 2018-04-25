package com.example.kchart.mychart;

import android.util.SparseArray;
import com.github.mikephil.charting.components.XAxis;

/**
 * <pre>
 *     author : Clement
 *     time   : 2018/04/25
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class MyXAxis extends XAxis {

    private SparseArray<String> labels;

    public SparseArray<String> getXLabels() {
        return labels;
    }

    public void setXLabels(SparseArray<String> labels) {
        this.labels = labels;
    }
}