package com.sam_chordas.android.stockhawk.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.CardView;

import com.sam_chordas.android.stockhawk.R;

public class ChartActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.charts);

        (new LineCardOne((CardView) findViewById(R.id.card1), getApplicationContext())).init();
    }
}
