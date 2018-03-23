package com.app.tvproject.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.app.tvproject.mvp.presenter.activity.TouchScreenActivity;
import com.app.tvproject.utils.LogUtil;
import com.app.tvproject.utils.ToastUtil;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by www on 2018/2/11.
 */

public class AutoStartReceiver extends BroadcastReceiver {
    static final String ACTION = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION)) {
            Timer timer = new Timer();
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    Intent mainActivityIntent = new Intent(context, TouchScreenActivity.class);  // 要启动的Activity
                    mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(mainActivityIntent);
                }
            };
            timer.schedule(task, 5000);
        }
    }
}
