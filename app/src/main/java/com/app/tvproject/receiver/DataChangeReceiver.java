package com.app.tvproject.receiver;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;
import android.view.WindowManager;

import com.app.tvproject.mvp.model.data.ContentBean;

import org.greenrobot.eventbus.EventBus;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2017/9/30 0030.
 */

public class DataChangeReceiver extends BroadcastReceiver {
    private static Calendar calendar = Calendar.getInstance();
    private TimerTask timerTask = null;
    SimpleDateFormat formatter = null;
    private ContentBean contentBean;

    @Override
    public void onReceive(Context context, Intent intent) {
//        if (eventBusData == null) {
//            eventBusData = new EventBusData();
//        }
        getNowTime();

    }

    public DataChangeReceiver(ContentBean contentBean) {
        this.contentBean = contentBean;
    }

    /**
     * 获取系统当前时间
     *
     * @param
     */
    private void getNowTime() {
        long time = System.currentTimeMillis();
        calendar.setTimeInMillis(time);
        int nowhour = calendar.get(Calendar.HOUR_OF_DAY);
        int nowminutes = calendar.get(Calendar.MINUTE);
        String[] temp = null;
        temp = contentBean.getPlayTime().split(":");
        int hour= Integer.parseInt(temp[0]);
        int minutes= Integer.parseInt(temp[1]);
        Log.d("sss", "hour:"+hour+" "+"minutes:"+minutes);
        if (nowhour==hour){
            if (nowminutes==minutes){
                String tasktime = getY_M_Dstr() + contentBean.getPlayTime();
                    timerSchedule(tasktime, contentBean);

            }

        }

    }


    /**
     * 控制app屏幕亮度，不是系统亮度
     *
     * @param context
     * @param brightness 0-255
     */
    private void setLight(Activity context, int brightness) {
        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        lp.screenBrightness = Float.valueOf(brightness) * (1f / 255f);
        context.getWindow().setAttributes(lp);
    }

    /**
     * 请求亮屏
     *
     * @param
     */
    private void acquireWakeLock(Context context) {
        Log.d("sss", "debug0");
        PowerManager.WakeLock wakeLock;
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "tag");
        wakeLock.acquire();
    }

    // 释放设备电源锁
    private void releaseWakeLock() {
//        if (null != wakeLock && wakeLock.isHeld()) {
//            wakeLock.setReferenceCounted(false);
//            Log.d("sss", "call releaseWakeLock");
//            wakeLock.release(0);
//            wakeLock = null;
//        } else {
//            wakeLock.acquire(System.currentTimeMillis() + 5 * 1000);
//            Log.d("sss", "call releaseWakeLock2222");
//        }
    }

    /**
     * 定时任务 某个时间点执行的任务
     */
    private void timerSchedule(String strtime, ContentBean bean) {
        timerTask = new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = 0 * 1;
                message.obj = bean;
                mHandler.sendMessage(message);
            }
        };
        Timer timer = new Timer(true);
        timer.schedule(timerTask, strToDateLong(strtime));

    }

    /**
     * string类型时间转换为date
     *
     * @param strDate
     * @return
     */
    public Date strToDateLong(String strDate) {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(strDate, pos);
        return strtodate;
    }

    /**
     * 获取当前的年月日
     *
     * @return
     */
    private String getY_M_Dstr() {
        Calendar calendar = Calendar.getInstance();
        long time = System.currentTimeMillis();
        calendar.setTimeInMillis(time);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return year + "-" + month + "-" + day + " ";
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0 * 1) {
                ContentBean bean = (ContentBean) msg.obj;
                EventBus.getDefault().post(bean);
            }
        }
    };

    /**
     * 判断指定时间是否已经过去 true 指定时间在未来
     *
     * @param time
     * @return
     */
    private boolean aBooleantime(String time) {
        if (formatter == null) {
            formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
        try {
            Date date = formatter.parse(time);
            long nowTime = System.currentTimeMillis();
            long istime = nowTime - date.getTime();
            if (istime < 0) {
                return true;
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }
}
