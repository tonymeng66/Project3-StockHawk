package com.sam_chordas.android.stockhawk.ui;

import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.view.View;

import com.db.chart.Tools;
import com.db.chart.model.BarSet;
import com.db.chart.view.AxisController;
import com.db.chart.view.BarChartView;
import com.db.chart.view.ChartView;
import com.db.chart.view.Tooltip;
import com.db.chart.view.animation.Animation;
import com.db.chart.view.animation.easing.ElasticEase;
import com.sam_chordas.android.stockhawk.R;

import java.util.List;


public class BarCardThree extends CardController {


    private final Context mContext;


    private final BarChartView mChart;

    private List<String> mLabelList;
    private List mValueList;

    private String[] mLabels;
    private float[] mValues;

    public void setmLabelList(List<String> mLabelList) {
        this.mLabelList = mLabelList;
        mLabels = mLabelList.toArray(new String[mLabelList.size()]);
    }

    public void setmValueList(List mValueList) {
        this.mValueList = mValueList;
        mValues =  new float[mValueList.size()];
        for(int i=0;i<mValueList.size();i++)
            mValues[i]=(float)mValueList.get(i);
    }


    public BarCardThree(CardView card, Context context){
        super(card);

        mContext = context;
        mChart = (BarChartView) card.findViewById(R.id.chart6);
    }


    @Override
    public void show(Runnable action) {
        super.show(action);

//        Tooltip tip = new Tooltip(mContext);
//        tip.setBackgroundColor(Color.parseColor("#CC7B1F"));
//
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
//            PropertyValuesHolder alpha = PropertyValuesHolder.ofFloat(View.ALPHA, 1);
//            tip.setEnterAnimation(alpha).setDuration(150);
//
//            alpha = PropertyValuesHolder.ofFloat(View.ALPHA,0);
//            tip.setExitAnimation(alpha).setDuration(150);
//        }
//
//        mChart.setTooltips(tip);

        BarSet dataset = new BarSet(mLabels, mValues);
        dataset.setColor(Color.parseColor("#eb993b"));
        mChart.addData(dataset);

        mChart.setBarSpacing(Tools.fromDpToPx(1));

        Paint gridPaint = new Paint();
        gridPaint.setColor(Color.parseColor("#444444"));
        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setAntiAlias(true);
        gridPaint.setStrokeWidth(Tools.fromDpToPx(.75f));
        gridPaint.setPathEffect(new DashPathEffect(new float[]{10, 10}, 0));

        mChart.setXLabels(AxisController.LabelPosition.NONE)
                .setYLabels(AxisController.LabelPosition.NONE)
                .setXAxis(true)
                .setYAxis(true)
                .setGrid(ChartView.GridType.FULL, 4, 4, gridPaint);

        Animation anim = new Animation()
                .setEasing(new ElasticEase())
                .setEndAction(action);

        mChart.show(anim);
    }


    @Override
    public void update() {
        super.update();

//        mChart.dismissAllTooltips();
//        if (firstStage)
//            mChart.updateValues(0, mValues);
//        else
//            mChart.updateValues(0, mValues);
//        mChart.notifyDataUpdate();
    }


    @Override
    public void dismiss(Runnable action) {
        super.dismiss(action);

//        mChart.dismissAllTooltips();
        mChart.dismiss(new Animation()
                .setEasing(new ElasticEase())
                .setEndAction(action));
    }

}
