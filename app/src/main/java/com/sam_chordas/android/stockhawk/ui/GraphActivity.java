package com.sam_chordas.android.stockhawk.ui;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.CardView;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.GraphColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;

public class GraphActivity extends Activity {

    private static final int GRAPH_LOADER = 0;
    private final String[] date = new String[70];
    private final float[] price = new float[70];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.charts);

        LineCardThree lineCard = (new LineCardThree((CardView) findViewById(R.id.card2), getApplicationContext()));

        Cursor cursor = getContentResolver().query(QuoteProvider.Graph.CONTENT_URI,null,null,null,null);
        for(int i=0;i<70;i++){
            if(cursor.moveToPosition(i)){
                date[i]=cursor.getString(cursor.getColumnIndex(GraphColumns.DATE));
                price[i]=Float.valueOf((cursor.getString(cursor.getColumnIndex(GraphColumns.BIDPRICE))));
            }
        }
        lineCard.setmLabels(date);
        lineCard.setmValues(price);

        cursor.close();


        lineCard.init();
    }


}
