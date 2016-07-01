package com.sam_chordas.android.stockhawk.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.support.v7.widget.CardView;

import com.db.chart.Tools;
import com.db.chart.model.LineSet;
import com.db.chart.view.AxisController;
import com.db.chart.view.ChartView;
import com.db.chart.view.LineChartView;
import com.db.chart.view.animation.Animation;
import com.sam_chordas.android.stockhawk.R;

import java.util.List;


public class LineCardThree extends CardController {


    private final LineChartView mChart;

    public void setmLowPrice(int mLowPrice) {
        this.mLowPrice = mLowPrice;
    }

    public void setmHighPrice(int mHighPrice) {
        this.mHighPrice = mHighPrice;
    }

    private int mHighPrice;
    private int mLowPrice;


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


    public LineCardThree(CardView card, Context context){
        super(card);

        mChart = (LineChartView) card.findViewById(R.id.chart2);
    }


    @Override
    public void show(Runnable action){
        super.show(action);

        LineSet dataset = new LineSet(mLabels, mValues);
        dataset.setColor(Color.parseColor("#53c1bd"))
                //.setFill(Color.parseColor("#3d6c73"))
                .setThickness(4);
                //.setGradientFill(new int[]{Color.parseColor("#364d5a"), Color.parseColor("#3f7178")}, null);
        mChart.addData(dataset);

        Paint gridPaint = new Paint();
        gridPaint.setColor(Color.parseColor("#444444"));
        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setAntiAlias(true);
        gridPaint.setStrokeWidth(Tools.fromDpToPx(.75f));
        gridPaint.setPathEffect(new DashPathEffect(new float[]{10, 10}, 0));

        mChart.setBorderSpacing(5)
                .setAxisBorderValues(mLowPrice, mHighPrice)
                .setXLabels(AxisController.LabelPosition.NONE)
                .setYLabels(AxisController.LabelPosition.NONE)
                .setXAxis(true)
                .setYAxis(true)
                .setGrid(ChartView.GridType.FULL, 4, 4, gridPaint)
                .setBorderSpacing(Tools.fromDpToPx(0));

        Animation anim = new Animation().setEndAction(action);

        mChart.show(anim);
    }

    @Override
    public void update(){
        super.update();

        mChart.dismissAllTooltips();
        if(firstStage) {
            mChart.updateValues(0, mValues);
        }else{
            mChart.updateValues(0, mValues);
        }
        mChart.notifyDataUpdate();
    }

    @Override
    public void dismiss(Runnable action){
        super.dismiss(action);

        mChart.dismissAllTooltips();
        mChart.dismiss(new Animation().setEndAction(action));
    }
}
