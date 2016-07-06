package com.sam_chordas.android.stockhawk.service;

import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.RemoteException;
import android.util.Log;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;
import com.sam_chordas.android.stockhawk.data.GraphColumns;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.rest.Utils;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by sam_chordas on 9/30/15.
 * The GCMTask service is primarily for periodic tasks. However, OnRunTask can be called directly
 * and is used for the initialization and adding task as well.
 */
public class StockTaskService extends GcmTaskService{
  private String LOG_TAG = StockTaskService.class.getSimpleName();

  private OkHttpClient client = new OkHttpClient();
  private Context mContext;
  private StringBuilder mStoredSymbols = new StringBuilder();
  private boolean isUpdate;
  StringBuilder urlStringBuilderQuote = new StringBuilder();
  StringBuilder urlStringBuilderGraph = new StringBuilder();

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
          QuoteColumns.OPEN,
          QuoteColumns.VOLUME
  };


  public StockTaskService(){}

  public StockTaskService(Context context){
    mContext = context;
  }

  String fetchData(String url) throws IOException{
    Request request = new Request.Builder()
        .url(url)
        .build();

    Response response = client.newCall(request).execute();
    return response.body().string();
  }

  @Override
  public int onRunTask(TaskParams params) throws NumberFormatException {
      Cursor initQueryCursor;
      String urlString;
      String getResponse;
      int result = GcmNetworkManager.RESULT_FAILURE;

      if (mContext == null) {
          mContext = this;
      }
      if (!params.getTag().equals("graph")) {
          try {
              // Base URL for the Yahoo query
              urlStringBuilderQuote.append("https://query.yahooapis.com/v1/public/yql?q=");
              urlStringBuilderQuote.append(URLEncoder.encode("select * from yahoo.finance.quotes where symbol "
                      + "in (", "UTF-8"));
          } catch (UnsupportedEncodingException e) {
              e.printStackTrace();
          }
          if (params.getTag().equals("init") || params.getTag().equals("periodic")) {
              isUpdate = true;
              initQueryCursor = mContext.getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
                      new String[]{"Distinct " + QuoteColumns.SYMBOL}, null,
                      null, null);
              if (initQueryCursor.getCount() == 0 || initQueryCursor == null) {
                  // Init task. Populates DB with quotes for the symbols seen below
                  try {
                      urlStringBuilderQuote.append(
                              URLEncoder.encode("\"YHOO\",\"AAPL\",\"GOOG\",\"MSFT\")", "UTF-8"));
                  } catch (UnsupportedEncodingException e) {
                      e.printStackTrace();
                  }
              } else if (initQueryCursor != null) {
                  DatabaseUtils.dumpCursor(initQueryCursor);
                  initQueryCursor.moveToFirst();
                  for (int i = 0; i < initQueryCursor.getCount(); i++) {
                      mStoredSymbols.append("\"" +
                              initQueryCursor.getString(initQueryCursor.getColumnIndex("symbol")) + "\",");
                      initQueryCursor.moveToNext();
                  }
                  mStoredSymbols.replace(mStoredSymbols.length() - 1, mStoredSymbols.length(), ")");
                  try {
                      urlStringBuilderQuote.append(URLEncoder.encode(mStoredSymbols.toString(), "UTF-8"));
                  } catch (UnsupportedEncodingException e) {
                      e.printStackTrace();
                  }
              }
          } else if (params.getTag().equals("add")) {
              isUpdate = false;
              // get symbol from params.getExtra and build query
              String stockInput = params.getExtras().getString("symbol");
              try {
                  urlStringBuilderQuote.append(URLEncoder.encode("\"" + stockInput + "\")", "UTF-8"));
              } catch (UnsupportedEncodingException e) {
                  e.printStackTrace();
              }
          }

          // finalize the URL for the API query.
          urlStringBuilderQuote.append("&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables."
                  + "org%2Falltableswithkeys&callback=");

          if (urlStringBuilderQuote != null) {
              urlString = urlStringBuilderQuote.toString();
              Log.d(LOG_TAG, urlString);
              try {
                  getResponse = fetchData(urlString);
                  result = GcmNetworkManager.RESULT_SUCCESS;
                  try {
                      ContentValues contentValues = new ContentValues();
                      // update ISCURRENT to 0 (false) so new data is current
                      if (isUpdate) {
                          contentValues.put(QuoteColumns.ISCURRENT, 0);
                          mContext.getContentResolver().update(QuoteProvider.Quotes.CONTENT_URI, contentValues,
                                  null, null);
                      }

                      mContext.getContentResolver().applyBatch(QuoteProvider.AUTHORITY,
                              Utils.quoteJsonToContentVals(getResponse));
                  } catch (RemoteException | OperationApplicationException e) {
                      Log.e(LOG_TAG, "Error applying batch insert", e);
                  } catch (NumberFormatException n) {
                      throw n;
                  }
              } catch (IOException e) {
                  e.printStackTrace();
              }
          }
      }

      if (params.getTag().equals("graph")) {
          // get symbol from params.getExtra and build query
          String stockInput = params.getExtras().getString("symbol");

          initQueryCursor = mContext.getContentResolver().query(QuoteProvider.Graph.CONTENT_URI,
                  GRAPH_COLUMNS, GraphColumns.SYMBOL + " = ? " + " AND " + GraphColumns.DATE + " = ?",
                  new String[]{stockInput, "2016-07-05"}, null);

          if (initQueryCursor.getCount() == 0 || initQueryCursor == null) {
              // Init task. Populates DB with quotes for the symbols seen below
              try {
                  urlStringBuilderGraph.append("https://query.yahooapis.com/v1/public/yql?q=");
                  urlStringBuilderGraph.append(URLEncoder.encode("select * from yahoo.finance.historicaldata where symbol = "
                          , "UTF-8"));
                  urlStringBuilderGraph.append(URLEncoder.encode("\"" + stockInput + "\"" +
                          " and" +
                          " startDate = " +
                          "\"" + "2016-02-04" + "\"" +
                          " and" +
                          " endDate = " +
                          "\"" + "2016-07-05" + "\"", "UTF-8"));
                  urlStringBuilderGraph.append("&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables."
                          + "org%2Falltableswithkeys&callback=");
              } catch (UnsupportedEncodingException e) {
                  e.printStackTrace();
              }
              if (urlStringBuilderGraph != null) {
                  urlString = urlStringBuilderGraph.toString();
                  Log.d(LOG_TAG, urlString);
                  try {
                      getResponse = fetchData(urlString);
                      result = GcmNetworkManager.RESULT_SUCCESS;
                      try {
                          ContentValues contentValues = new ContentValues();
                          mContext.getContentResolver().applyBatch(QuoteProvider.AUTHORITY,
                                  Utils.graphJsonToContentVals(getResponse));
                      } catch (RemoteException | OperationApplicationException e) {
                          Log.e(LOG_TAG, "Error applying batch insert", e);
                      } catch (NumberFormatException n) {
                          throw n;
                      }
                  } catch (IOException e) {
                      e.printStackTrace();
                  }
              }
          } else if (initQueryCursor != null) {
              result = GcmNetworkManager.RESULT_SUCCESS;
          }
      }   return result;
  }
}


