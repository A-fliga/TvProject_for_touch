package com.app.tvproject.receiver;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.app.tvproject.mvp.model.data.EventBusData;
import com.app.tvproject.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by Administrator on 2017/10/8 0008.
 */

public class JpushCustomerReceiver extends BroadcastReceiver {
    private NotificationManager nm;
    String TAG = "sss";
    private EventBusData eventBusData = null;

    @Override
    public void onReceive(Context context, Intent intent) {
        ToastUtil.l("触发推送了，触发推送了，触发推送了，触发推送了触发推送了，触发推送了，");
        if (null == nm) {
            nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        if (eventBusData == null) {
            eventBusData = new EventBusData();
        }
        Bundle bundle = intent.getExtras();
        //接收发送下来的通知
        if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
            long pdId = 0;
            String action = null;
            JSONObject extrasJson = null;
            JSONObject jsonObject = null;
            try {
                if (extrasJson == null) {
                    extrasJson = new JSONObject(extras);
                }
                String popUp = extrasJson.getString("androidNotification_extras_key");
                if (jsonObject == null) {
                    jsonObject = new JSONObject(popUp);
                }
                action = jsonObject.getString("action");
                if (action.equals("newEquipmentNotice")) {
                    int voice = jsonObject.getInt("voice");
                    eventBusData.setVoice(voice);
                } else {
                    if(jsonObject.has("pdId")) {
                        pdId = jsonObject.getLong("pdId");
                        eventBusData.setContent_id(pdId);
                    }
                }
                eventBusData.setAction(action);


            } catch (Exception e) {
                Log.d(TAG, "解析异常");

            }
            EventBus.getDefault().post(eventBusData);
        }
    }

}
