package com.sam_chordas.android.stockhawk.ui;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.widget.TextView;

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
    private String mHighPrice;
    private String mLowPrice;
    private String mHighVolume;
    private int y1=0,y2=0,y3=0,y4=0,y5=0,y6=0,y7=0,y8=0,y9=0;

    private static final String[] GRAPH_COLUMNS = {
            GraphColumns._ID,
            GraphColumns.SYMBOL,
            GraphColumns.DATE,
            GraphColumns.BIDPRICE,
            GraphColumns.VOLUME
    };
    private static final String[] QUOTE_COLUMNS = {
            QuoteColumns._ID,
            QuoteColumns.SYMBOL,
            QuoteColumns.PERCENT_CHANGE,
            QuoteColumns.CHANGE,
            QuoteColumns.BIDPRICE,
            QuoteColumns.ISUP,
            QuoteColumns.ISCURRENT,
            QuoteColumns.TIME,
            QuoteColumns.VOLUME
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.charts);

        Intent intent = getIntent();
        String symbol = intent.getStringExtra("symbol");

        LineCardThree lineCard = (new LineCardThree((CardView) findViewById(R.id.card2), getApplicationContext()));
        BarCardThree barCard = (new BarCardThree((CardView) findViewById(R.id.card6), getApplicationContext()));
        TextView ylabel1 = (TextView) findViewById(R.id.ylabel1);
        TextView ylabel2 = (TextView) findViewById(R.id.ylabel2);
        TextView ylabel3 = (TextView) findViewById(R.id.ylabel3);
        TextView ylabel4 = (TextView) findViewById(R.id.ylabel4);
        TextView ylabel5 = (TextView) findViewById(R.id.ylabel5);
        TextView ylabel6 = (TextView) findViewById(R.id.ylabel6);
        TextView ylabel7 = (TextView) findViewById(R.id.ylabel7);
        TextView ylabel8 = (TextView) findViewById(R.id.ylabel8);
        TextView ylabel9 = (TextView) findViewById(R.id.ylabel9);


        Cursor cursor = getContentResolver().query(QuoteProvider.Graph.CONTENT_URI,
                GRAPH_COLUMNS, GraphColumns.SYMBOL + "= ?",
                new String[] { symbol }, null);


//        Cursor cursor = getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
//                QUOTE_COLUMNS, QuoteColumns.SYMBOL + "= ?",
//                new String[] { symbol }, null);

        int i = 0;
        while(cursor.moveToPosition(i)){
            date.add(i,cursor.getString(cursor.getColumnIndex(GraphColumns.DATE)));
            price.add(i,Float.valueOf((cursor.getString(cursor.getColumnIndex(GraphColumns.BIDPRICE)))));
            volume.add(i,(Float.valueOf((cursor.getString(cursor.getColumnIndex(GraphColumns.VOLUME)))))/1000000L);

//            date.add(i,cursor.getString(cursor.getColumnIndex(QuoteColumns.TIME)));
//            price.add(i,Float.valueOf((cursor.getString(cursor.getColumnIndex(QuoteColumns.BIDPRICE)))));
//            volume.add(i,(Float.valueOf((cursor.getString(cursor.getColumnIndex(QuoteColumns.VOLUME)))))/100000L);
            i++;
        }

        lineCard.setmLabelList(date);
        lineCard.setmValueList(price);
        barCard.setmLabelList(date);
        barCard.setmValueList(volume);

        cursor = getContentResolver().query(QuoteProvider.Graph.CONTENT_URI,
                GRAPH_COLUMNS, GraphColumns.SYMBOL + "= ?",
                new String[] { symbol }, GraphColumns.BIDPRICE + " desc ");

        if(cursor.moveToFirst()) {
            mHighPrice = cursor.getString(cursor.getColumnIndex(GraphColumns.BIDPRICE));
            y1 = (int)Math.round(Float.valueOf(mHighPrice)*1.1);
        }
        if(cursor.moveToLast()) {
            mLowPrice = cursor.getString(cursor.getColumnIndex(GraphColumns.BIDPRICE));
            y5 = (int)Math.round(Float.valueOf(mLowPrice)*0.9);
            y2 = (int) Math.round((y1-y5)*0.75+y5);
            y3 = (int) Math.round((y1-y5)*0.5+y5);
            y4 = (int) Math.round((y1-y5)*0.25+y5);
            lineCard.setmHighPrice(y1);
            lineCard.setmLowPrice(y5);
        }

        ylabel1.setText(Integer.toString(y1));
        ylabel2.setText(Integer.toString(y2));
        ylabel3.setText(Integer.toString(y3));
        ylabel4.setText(Integer.toString(y4));
        ylabel5.setText(Integer.toString(y5));

       cursor = getContentResolver().query(QuoteProvider.Graph.CONTENT_URI,
             GRAPH_COLUMNS, GraphColumns.SYMBOL + "= ?",
             new String[] { symbol }, GraphColumns.VOLUME + " desc ");

       if(cursor.moveToFirst()) {
           mHighVolume = cursor.getString(cursor.getColumnIndex(GraphColumns.VOLUME));
           y6 = (int)Math.round(Float.valueOf(mHighVolume)*1.1/1000000L);
           y7 = (int) Math.round((y6*0.75));
           y8 = (int) Math.round((y6*0.5));
           y9 = (int) Math.round((y6*0.25));
       }

        ylabel6.setText(Integer.toString(y6));
        ylabel7.setText(Integer.toString(y7));
        ylabel8.setText(Integer.toString(y8));
        ylabel9.setText(Integer.toString(y9));


        cursor.close();



        if(date.size()!=0 && price.size()!=0){

            lineCard.init();
        }
        if(date.size()!=0 && volume.size()!=0){
            barCard.init();
        }
    }

}
