package com.app.tvproject.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.app.tvproject.mvp.model.data.ContentBean;
import com.app.tvproject.receiver.DataChangeReceiver;

/**
 * Created by Administrator on 2017/9/29 0029.
 */

public class TimeService extends Service {
    Context context;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    public TimeService() {

    }
    public TimeService(Context context) {
        this.context=context;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ContentBean contentBean=intent.getParcelableExtra("contentbean");
        Log.d("sss","TimeService:");
        startRecever(contentBean);
        return super.onStartCommand(intent, flags, startId);

    }
    private void startRecever(ContentBean contentBean){
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_TICK);
        filter.addAction(Intent.ACTION_TIME_CHANGED);
        registerReceiver(new DataChangeReceiver(contentBean), filter);
    }
}
