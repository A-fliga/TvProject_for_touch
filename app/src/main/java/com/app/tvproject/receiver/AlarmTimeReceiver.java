package com.app.tvproject.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.app.tvproject.mvp.model.data.ContentBean;
import com.app.tvproject.mvp.model.data.EventBusData;

import org.greenrobot.eventbus.EventBus;

import java.util.Calendar;
import java.util.TimeZone;

import cn.jpush.android.service.AlarmReceiver;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by Administrator on 2017/10/26 0026.
 */

public class AlarmTimeReceiver extends BroadcastReceiver {
    //    MainActivity activity;
    private EventBusData eventBusData = null;

    @Override
    public void onReceive(Context context, Intent intent) {
        ContentBean contentBean = intent.getParcelableExtra("contentbean");
        EventBus.getDefault().post(contentBean);
        //如果当前时间大于结束时间 取消定时
        if (System.currentTimeMillis() < contentBean.getEndtime()) {
            //重新开一个定时器
            getAlarmTime(contentBean, context);

        } else {
            //取消定时器
            cancelAlarm(contentBean, context);
        }

//        Intent mintent=new Intent(context, AlarmService.class);
//        intent.putExtra("contentbean", contentBean);
//        context.startService(mintent);

    }

    /**
     * 定时执行任务
     *
     * @param
     * @param bean
     */
    private void getAlarmTime(ContentBean bean, Context context) {
        String[] temp = null;
        temp = bean.getPlayTime().split(":");
        //获取AlarmManager实例
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
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
        Intent intent = new Intent(context, AlarmTimeReceiver.class);
        intent.putExtra("contentbean", bean);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int) bean.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setExact(AlarmManager.RTC, systemTime + selectrdtime, pendingIntent);
//        alarmManager.setWindow(AlarmManager.RTC, systemTime + selectrdtime, 0, pendingIntent);
        //AlarmManager.INTERVAL_DAY
//        alarmManager.setWindow(AlarmManager.RTC_WAKEUP, systemTime + selectrdtime,1000*60*60*24, pendingIntent);
    }

    /**
     * 取消定时
     *
     * @param bean
     */

    public static void cancelAlarm(ContentBean bean, Context context) {
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendIntent = PendingIntent.getBroadcast(context,
                (int) bean.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
// 与上面的intent匹配（filterEquals(intent)）的闹钟会被取消
        alarmMgr.cancel(pendIntent);
    }
}
