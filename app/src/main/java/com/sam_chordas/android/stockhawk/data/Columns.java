package com.sam_chordas.android.stockhawk.data;

/**
 * Created by Tony on 2016/7/7.
 */
public class Columns {
    public static final String[] GRAPH_COLUMNS = {
            GraphColumns._ID,
            GraphColumns.SYMBOL,
            GraphColumns.DATE,
            GraphColumns.BIDPRICE,
            GraphColumns.VOLUME
    };
    public static final String[] QUOTE_COLUMNS = {
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
            QuoteColumns.OPEN,
            QuoteColumns.VOLUME,
            QuoteColumns.NAME
    };
}
