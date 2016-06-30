package com.sam_chordas.android.stockhawk.ui;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.CardView;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.GraphColumns;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteDatabase;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;

import java.util.ArrayList;
import java.util.List;

public class GraphActivity extends Activity {

    private static final int GRAPH_LOADER = 0;
    private List<String> date = new ArrayList<String>();
    private List price = new ArrayList();
    private List volume = new ArrayList();

    private static final String[] GRAPH_COLUMNS = {
            GraphColumns._ID,
            GraphColumns.SYMBOL,
            GraphColumns.DATE,
            GraphColumns.BIDPRICE,
            GraphColumns.VOLUME
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.charts);

        Intent intent = getIntent();
        String symbol = intent.getStringExtra("symbol");

        LineCardThree lineCard = (new LineCardThree((CardView) findViewById(R.id.card2), getApplicationContext()));
        BarCardThree barCard = (new BarCardThree((CardView) findViewById(R.id.card6), getApplicationContext()));

        Cursor cursor = getContentResolver().query(QuoteProvider.Graph.CONTENT_URI,
                GRAPH_COLUMNS, GraphColumns.SYMBOL + "= ?",
                new String[] { symbol }, null);

        int i = 0;
        while(cursor.moveToPosition(i)){
            date.add(i,cursor.getString(cursor.getColumnIndex(GraphColumns.DATE)));
            price.add(i,Float.valueOf((cursor.getString(cursor.getColumnIndex(GraphColumns.BIDPRICE)))));
            volume.add(i,(Float.valueOf((cursor.getString(cursor.getColumnIndex(GraphColumns.VOLUME)))))/100000L);
            i++;
        }

        lineCard.setmLabelList(date);
        lineCard.setmValueList(price);
        barCard.setmLabelList(date);
        barCard.setmValueList(volume);

        cursor.close();

        if(date.size()!=0 && price.size()!=0){
            lineCard.init();
        }
        if(date.size()!=0 && volume.size()!=0){
            barCard.init();
        }

    }


}
