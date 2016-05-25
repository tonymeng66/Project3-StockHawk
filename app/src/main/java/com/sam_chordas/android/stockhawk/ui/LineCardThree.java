package com.sam_chordas.android.stockhawk.ui;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;

import com.db.chart.Tools;
import com.db.chart.model.LineSet;
import com.db.chart.view.AxisController;
import com.db.chart.view.LineChartView;
import com.db.chart.view.animation.Animation;
import com.sam_chordas.android.stockhawk.R;


public class LineCardThree extends CardController {


    private final LineChartView mChart;

    public void setmLabels(String[] mLabels) {
        this.mLabels = mLabels;
    }

    public void setmValues(float[] mValues) {
        this.mValues = mValues;
    }

    private String[] mLabels;
    private float[] mValues;


    public LineCardThree(CardView card, Context context){
        super(card);

        mChart = (LineChartView) card.findViewById(R.id.chart2);
    }


    @Override
    public void show(Runnable action){
        super.show(action);

        LineSet dataset = new LineSet(mLabels, mValues);
        dataset.setColor(Color.parseColor("#53c1bd"))
                .setFill(Color.parseColor("#3d6c73"))
                .setThickness(4)
                .setGradientFill(new int[]{Color.parseColor("#364d5a"), Color.parseColor("#3f7178")}, null);
        mChart.addData(dataset);

        mChart.setBorderSpacing(1)
                .setAxisBorderValues(0, 100)
                .setXLabels(AxisController.LabelPosition.NONE)
                .setYLabels(AxisController.LabelPosition.NONE)
                .setXAxis(false)
                .setYAxis(false)
                .setBorderSpacing(Tools.fromDpToPx(5));

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
