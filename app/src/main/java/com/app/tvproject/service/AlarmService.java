package com.app.tvproject.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.app.tvproject.mvp.model.data.ContentBean;
import com.app.tvproject.receiver.AlarmTimeReceiver;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by Administrator on 2017/9/29 0029.
 */

public class AlarmService extends Service {
    Context context;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public AlarmService() {

    }

    public AlarmService(Context context) {
        this.context = context;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ContentBean contentBean = intent.getParcelableExtra("contentbean");
        getAlarmTime(contentBean);

        return super.onStartCommand(intent, flags, startId);

    }

    /**
     * 定时执行任务
     *
     * @param
     * @param bean
     */
    private void getAlarmTime(ContentBean bean) {
        String[] temp = null;
        temp = bean.getPlayTime().split(":");
        //获取AlarmManager实例
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        long systemTime = System.currentTimeMillis();
        calendar.setTimeInMillis(systemTime);
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(temp[0]));
        calendar.set(Calendar.MINUTE, Integer.parseInt(temp[1]));
        calendar.set(Calendar.SECOND, Integer.parseInt(temp[2]));
        calendar.set(Calendar.MILLISECOND, 0);
        long time = calendar.getTimeInMillis();
        if (systemTime > time) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            time = calendar.getTimeInMillis();
        }
        long selectrdtime = time - systemTime;
        Intent intent = new Intent(this, AlarmTimeReceiver.class);
        intent.putExtra("contentbean", bean);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, (int) bean.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setExact(AlarmManager.RTC, systemTime + selectrdtime, pendingIntent);
    }
}
