package com.sam_chordas.android.stockhawk.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.TaskParams;

/**
 * Created by sam_chordas on 10/1/15.
 */
public class StockIntentService extends IntentService {

  private Handler mHandler;
  String LOG_TAG = StockIntentService.class.getSimpleName();

  public StockIntentService(){
    super(StockIntentService.class.getName());
  }

  public StockIntentService(String name) {
    super(name);
  }
  private class ToastRunnable implements Runnable {
    String mText;

    public ToastRunnable(String text) {
      mText = text;
    }

    @Override
    public void run(){
      Toast.makeText(getApplicationContext(), mText, Toast.LENGTH_SHORT).show();
    }
  }


  private void showToastSymbolInvalid() {
    ToastRunnable toastRunnable = new ToastRunnable("The stock code you enter does not exist or data invalid");
    mHandler.post(toastRunnable);
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    mHandler = new Handler(Looper.getMainLooper());
    return super.onStartCommand(intent, flags, startId);
  }

  @Override protected void onHandleIntent(Intent intent) {
    Log.d(LOG_TAG, "Stock Intent Service");
    StockTaskService stockTaskService = new StockTaskService(this);
    Bundle args = new Bundle();
    if (intent.getStringExtra("tag").equals("add")){
      args.putString("symbol", intent.getStringExtra("symbol"));
    }
    // We can call OnRunTask from the intent service to force it to run immediately instead of
    // scheduling a task

    try {
      stockTaskService.onRunTask(new TaskParams(intent.getStringExtra("tag"), args));
    }catch (NumberFormatException n){
      showToastSymbolInvalid();
    }
  }
}
