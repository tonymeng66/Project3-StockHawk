package com.sam_chordas.android.stockhawk.rest;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.sam_chordas.android.stockhawk.data.Columns;
import com.sam_chordas.android.stockhawk.data.GraphColumns;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sam_chordas on 10/8/15.
 */
public class Utils {

  private static String LOG_TAG = Utils.class.getSimpleName();

  public static boolean showPercent = true;

  public static ArrayList quoteJsonToContentVals(String JSON)throws NumberFormatException{
    ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>();
    JSONObject jsonObject = null;

    JSONArray resultsArray = null;
    Log.i(LOG_TAG, "GET FB: " +JSON);
    try{
      jsonObject = new JSONObject(JSON);

      if (jsonObject != null && jsonObject.length() != 0){
        jsonObject = jsonObject.getJSONObject("query");
        int count = Integer.parseInt(jsonObject.getString("count"));
        if (count == 1){
          jsonObject = jsonObject.getJSONObject("results")
              .getJSONObject("quote");
          batchOperations.add(buildBatchOperation(jsonObject));
        } else{
          resultsArray = jsonObject.getJSONObject("results").getJSONArray("quote");

          if (resultsArray != null && resultsArray.length() != 0){
            for (int i = 0; i < resultsArray.length(); i++){
              jsonObject = resultsArray.getJSONObject(i);
              batchOperations.add(buildBatchOperation(jsonObject));
            }
          }
        }
      }
    } catch (JSONException e){
        Log.e(LOG_TAG, "String to JSON failed: " + e);
    }catch (NumberFormatException n){
        throw n;
    }
    return batchOperations;
  }

  public static ArrayList graphJsonToContentVals(String JSON)throws NumberFormatException{
      ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>();
      JSONObject jsonObject = null;
      JSONArray resultsArray = null;
      Log.i(LOG_TAG, "GET FB: " +JSON);
      try{
          jsonObject = new JSONObject(JSON);
          if (jsonObject != null && jsonObject.length() != 0){
              jsonObject = jsonObject.getJSONObject("query");
              int count = Integer.parseInt(jsonObject.getString("count"));
              //TODO
              //do something when count = 0
              if (count == 1){
                  jsonObject = jsonObject.getJSONObject("results").getJSONObject("quote");
                  batchOperations.add(buildBatchOperation(jsonObject));
              } else{
                  resultsArray = jsonObject.getJSONObject("results").getJSONArray("quote");
                   if (resultsArray != null && resultsArray.length() != 0){
                      for (int i = 0; i < resultsArray.length(); i++){
                          jsonObject = resultsArray.getJSONObject(i);
                          batchOperations.add(graphBuildBatchOperation(jsonObject));
                      }
                  }
              }
          }
      } catch (JSONException e){
          Log.e(LOG_TAG, "String to JSON failed: " + e);
      }catch (NumberFormatException n){
          throw n;
      }
      return batchOperations;
  }

  public static String truncateBidPrice(String bidPrice)throws NumberFormatException{
    bidPrice = String.format("%.2f", Float.parseFloat(bidPrice));
    return bidPrice;
  }

  public static String truncateChange(String change, boolean isPercentChange)throws NumberFormatException{
    String weight = change.substring(0,1);
    String ampersand = "";
    if (isPercentChange){
      ampersand = change.substring(change.length() - 1, change.length());
      change = change.substring(0, change.length() - 1);
    }
    change = change.substring(1, change.length());
    double round = (double) Math.round(Double.parseDouble(change) * 100) / 100;
    change = String.format("%.2f", round);
    StringBuffer changeBuffer = new StringBuffer(change);
    changeBuffer.insert(0, weight);
    changeBuffer.append(ampersand);
    change = changeBuffer.toString();
    return change;
  }

  public static ContentProviderOperation buildBatchOperation(JSONObject jsonObject)throws NumberFormatException{
    ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(
        QuoteProvider.Quotes.CONTENT_URI);
    try {
      String change = jsonObject.getString("Change");
      String symbol = jsonObject.getString("symbol");
      String bid = jsonObject.getString("Bid");
      String changeInPercent = jsonObject.getString("ChangeinPercent");
      String volume =  jsonObject.getString("Volume");
      String open = jsonObject.getString("Open");
      String daysHigh = jsonObject.getString("DaysHigh");
      String daysLow = jsonObject.getString("DaysLow");
      String date = jsonObject.getString("LastTradeDate");
      String name = jsonObject.getString("Name");

      try {
        builder.withValue(QuoteColumns.SYMBOL, symbol);
        builder.withValue(QuoteColumns.BIDPRICE, truncateBidPrice(bid));
        builder.withValue(QuoteColumns.PERCENT_CHANGE, truncateChange(
                changeInPercent, true));
        builder.withValue(QuoteColumns.CHANGE, truncateChange(change, false));
        builder.withValue(QuoteColumns.ISCURRENT, 1);
        builder.withValue(QuoteColumns.VOLUME, volume);
        builder.withValue(QuoteColumns.OPEN, open);
        builder.withValue(QuoteColumns.DAYSHIGH, daysHigh);
        builder.withValue(QuoteColumns.DAYSLOW, daysLow);
        builder.withValue(QuoteColumns.DATE, date);
        builder.withValue(QuoteColumns.NAME, name);

      }catch(NumberFormatException e){
        throw e;
      }
      if (change.charAt(0) == '-') {
        builder.withValue(QuoteColumns.ISUP, 0);
      } else {
          builder.withValue(QuoteColumns.ISUP, 1);
      }

    } catch (JSONException e){
      e.printStackTrace();
    }
    return builder.build();
  }

  public static ContentProviderOperation graphBuildBatchOperation(JSONObject jsonObject)throws NumberFormatException{
    ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(
            QuoteProvider.Graph.CONTENT_URI);
    try {
      String symbol = jsonObject.getString("Symbol");
      double bid = jsonObject.getDouble("Adj_Close");
      String date = jsonObject.getString("Date");
      double vol = jsonObject.getDouble("Volume");

      try {
        builder.withValue(GraphColumns.SYMBOL, symbol);
        builder.withValue(GraphColumns.BIDPRICE, bid);
        builder.withValue(GraphColumns.DATE, date);
        builder.withValue(GraphColumns.VOLUME, vol);
      }catch(NumberFormatException e){
        throw e;
      }

    } catch (JSONException e){
      e.printStackTrace();
    }
    return builder.build();
  }

    public static String getLastTradeDate(Context context, String symbol){
        Cursor cursor = context.getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
                Columns.QUOTE_COLUMNS,
                QuoteColumns.ISCURRENT + " = ?" + " AND " + QuoteColumns.SYMBOL + " = ? ",
                new String[]{"1", symbol},null);
        if(cursor.moveToFirst()){
            return convertDateFormat(cursor.getString(cursor.getColumnIndex(QuoteColumns.DATE)));
        }else{
            return null;
        }

    }

   public static String getDate6MBack(){
       DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

       Calendar cal = Calendar.getInstance();
       cal.add(Calendar.DATE, -180);
       Date todate1 = cal.getTime();
       String fromdate = dateFormat.format(todate1);
       return fromdate;
   }

    public static String convertDateFormat(String date){
        String convertDate = null;
        SimpleDateFormat format1 = new SimpleDateFormat("MM/dd/yyyy");
        SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");

        try{
            Date parsed = format1.parse(date);
            convertDate = format2.format(parsed);
        }catch (ParseException e){
            e.printStackTrace();
        }

        return convertDate;
    }

}
