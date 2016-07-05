package com.sam_chordas.android.stockhawk.ui;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.widget.TextView;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.GraphColumns;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;

import java.util.ArrayList;
import java.util.List;

public class GraphActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor>{


    private List<String> mDate = new ArrayList<String>();
    private List mPrice = new ArrayList();
    private List mVolume = new ArrayList();
    private String mSymbol;
    private String mHighPrice;
    private String mLowPrice;
    private String mHighVolume;
    private int mY1=0, mY2 =0, mY3 =0, mY4 =0, mY5 =0, mY6 =0, mY7 =0, mY8 =0, mY9 =0;

    LineCardThree mLineCard;
    BarCardThree mBarCard;

    private TextView mBidPrice;
    private TextView mChange;
    private TextView mPerChange;
    private TextView mOpen;
    private TextView mDaysHigh;
    private TextView mDaysLowValue;
    private TextView mLastTradeDate;

    private TextView mXlabel1;
    private TextView mXlabel2;
    private TextView mXlabel3;

    private TextView mYlabel1;
    private TextView mYlabel2;
    private TextView mYlabel3;
    private TextView mYlabel4;
    private TextView mYlabel5;
    private TextView mYlabel6;
    private TextView mYlabel7;
    private TextView mYlabel8;
    private TextView mYlabel9;


    private static final int QUOTE_LOADER = 0;
    private static final int GRAPH_LOADER = 1;

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
            QuoteColumns.DAYSHIGH,
            QuoteColumns.DAYSLOW,
            QuoteColumns.DATE,
            QuoteColumns.VOLUME
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.charts);

        Intent intent = getIntent();
        mSymbol = intent.getStringExtra("symbol");

        getLoaderManager().initLoader(QUOTE_LOADER, null, this);
        getLoaderManager().initLoader(GRAPH_LOADER, null, this);

        mLineCard = (new LineCardThree((CardView) findViewById(R.id.card2), getApplicationContext()));
        mBarCard = (new BarCardThree((CardView) findViewById(R.id.card6), getApplicationContext()));

        mBidPrice = (TextView) findViewById(R.id.bid_price);
        mChange = (TextView) findViewById(R.id.change);
        mPerChange = (TextView) findViewById(R.id.per_change);
        mOpen = (TextView) findViewById(R.id.open);
        mDaysHigh =  (TextView) findViewById(R.id.days_high_value);
        mDaysLowValue = (TextView) findViewById(R.id.days_low_value);
        mLastTradeDate = (TextView) findViewById(R.id.date);

        mXlabel1 = (TextView) findViewById(R.id.xlabel1);
        mXlabel2 = (TextView) findViewById(R.id.xlabel2);
        mXlabel3 = (TextView) findViewById(R.id.xlabel3);

        mYlabel1 = (TextView) findViewById(R.id.ylabel1);
        mYlabel2 = (TextView) findViewById(R.id.ylabel2);
        mYlabel3 = (TextView) findViewById(R.id.ylabel3);
        mYlabel4 = (TextView) findViewById(R.id.ylabel4);
        mYlabel5 = (TextView) findViewById(R.id.ylabel5);
        mYlabel6 = (TextView) findViewById(R.id.ylabel6);
        mYlabel7 = (TextView) findViewById(R.id.ylabel7);
        mYlabel8 = (TextView) findViewById(R.id.ylabel8);
        mYlabel9 = (TextView) findViewById(R.id.ylabel9);

    }

    @Override
    protected void onResume() {
        super.onResume();
        getLoaderManager().initLoader(QUOTE_LOADER, null, this);
        getLoaderManager().initLoader(GRAPH_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Loader loader = null;
        switch(id) {
            case QUOTE_LOADER:
                loader = new CursorLoader(this, QuoteProvider.Quotes.CONTENT_URI,
                        QUOTE_COLUMNS,
                        QuoteColumns.ISCURRENT + " = ?" + " AND " + QuoteColumns.SYMBOL + " = ? ",
                        new String[]{"1", mSymbol},
                        null);
            break;
            case GRAPH_LOADER:
                loader = new CursorLoader(this,QuoteProvider.Graph.CONTENT_URI,
                        GRAPH_COLUMNS,
                        GraphColumns.SYMBOL + "= ?",
                        new String[] { mSymbol }, null);
                break;
        }
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch(loader.getId()) {
            case QUOTE_LOADER:
            if (data.moveToFirst()) {
                mBidPrice.setText(data.getString(data.getColumnIndex(QuoteColumns.BIDPRICE)));
                mChange.setText(data.getString(data.getColumnIndex(QuoteColumns.CHANGE)));
                mPerChange.setText(data.getString(data.getColumnIndex(QuoteColumns.PERCENT_CHANGE)));
                //mOpen.setText(data.getString(data.getColumnIndex(QuoteColumns.OPEN)));
                mDaysHigh.setText(data.getString(data.getColumnIndex(QuoteColumns.DAYSHIGH)));
                mDaysLowValue.setText(data.getString(data.getColumnIndex(QuoteColumns.DAYSLOW)));
                mLastTradeDate.setText(data.getString(data.getColumnIndex(QuoteColumns.DATE)));
            }
                break;

            case GRAPH_LOADER:


            calXlabels(data);
            calYlabels();

            int i = 0;
            while(data.moveToPosition(i)){
                mDate.add(i,data.getString(data.getColumnIndex(GraphColumns.DATE)));
                mPrice.add(i,Float.valueOf((data.getString(data.getColumnIndex(GraphColumns.BIDPRICE)))));
                mVolume.add(i,(Float.valueOf((data.getString(data.getColumnIndex(GraphColumns.VOLUME)))))/1000000L);
                i++;
            }


            mLineCard.setmLabelList(mDate);
            mLineCard.setmValueList(mPrice);
            mBarCard.setmLabelList(mDate);
            mBarCard.setmValueList(mVolume);

            if(mDate.size()!=0 && mPrice.size()!=0){
                mLineCard.init();
            }
            if(mDate.size()!=0 && mVolume.size()!=0){
                mBarCard.init();
            }
            break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {


    }

    private void calYlabels(){
        Cursor cursor = getContentResolver().query(QuoteProvider.Graph.CONTENT_URI,
                GRAPH_COLUMNS, GraphColumns.SYMBOL + "= ?",
                new String[] { mSymbol }, GraphColumns.BIDPRICE + " desc ");

        if(cursor.moveToFirst()) {
            mHighPrice = cursor.getString(cursor.getColumnIndex(GraphColumns.BIDPRICE));
            mY1 = (int)Math.round(Float.valueOf(mHighPrice)*1.1);
        }
        if(cursor.moveToLast()) {
            mLowPrice = cursor.getString(cursor.getColumnIndex(GraphColumns.BIDPRICE));
            mY5 = (int)Math.round(Float.valueOf(mLowPrice)*0.9);
            mY2 = (int) Math.round((mY1 - mY5)*0.75+ mY5);
            mY3 = (int) Math.round((mY1 - mY5)*0.5+ mY5);
            mY4 = (int) Math.round((mY1 - mY5)*0.25+ mY5);
            mLineCard.setmHighPrice(mY1);
            mLineCard.setmLowPrice(mY5);
        }

        mYlabel1.setText(Integer.toString(mY1));
        mYlabel2.setText(Integer.toString(mY2));
        mYlabel3.setText(Integer.toString(mY3));
        mYlabel4.setText(Integer.toString(mY4));
        mYlabel5.setText(Integer.toString(mY5));

        cursor = getContentResolver().query(QuoteProvider.Graph.CONTENT_URI,
                GRAPH_COLUMNS, GraphColumns.SYMBOL + "= ?",
                new String[] { mSymbol }, GraphColumns.VOLUME + " desc ");

        if(cursor.moveToFirst()) {
            mHighVolume = cursor.getString(cursor.getColumnIndex(GraphColumns.VOLUME));
            mY6 = (int)Math.round(Float.valueOf(mHighVolume)*1.1/1000000L);
            mY7 = (int) Math.round((mY6 *0.75));
            mY8 = (int) Math.round((mY6 *0.5));
            mY9 = (int) Math.round((mY6 *0.25));
        }

        mYlabel6.setText(Integer.toString(mY6));
        mYlabel7.setText(Integer.toString(mY7));
        mYlabel8.setText(Integer.toString(mY8));
        mYlabel9.setText(Integer.toString(mY9));


        cursor.close();
    }

    private void calXlabels(Cursor cursor) {

        int position[] = new int[3];

        if (cursor != null) {
            for (int i = 0; i < 3; i++) {
                position[i] = Math.round(cursor.getCount() * 0.2F * i);
                Log.d("GraphActivity", Integer.toString(position[i]));
            }

        if(cursor.moveToPosition(position[2])){
            mXlabel1.setText(cursor.getString(cursor.getColumnIndex(GraphColumns.DATE)));
        }
        if(cursor.moveToPosition(position[1])){
            mXlabel2.setText(cursor.getString(cursor.getColumnIndex(GraphColumns.DATE)));
        }
        if(cursor.moveToPosition(position[0])){
            mXlabel3.setText(cursor.getString(cursor.getColumnIndex(GraphColumns.DATE)));
        }

        }
    }
}
