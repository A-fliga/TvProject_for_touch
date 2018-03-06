package com.app.tvproject.receiver;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.app.tvproject.mvp.model.data.EventBusData;
import com.app.tvproject.utils.LogUtil;
import com.app.tvproject.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by Administrator on 2017/10/8 0008.
 */

public class JpushCustomerReceiver extends BroadcastReceiver {
    private EventBusData eventBusData;

    @Override
    public void onReceive(Context context, Intent intent) {
//        ToastUtil.l("触发推送了，触发推送了，触发推送了，触发推送了触发推送了，触发推送了，");
        if (eventBusData == null) {
            eventBusData = new EventBusData();
        }
        Bundle bundle = intent.getExtras();
        //接收发送下来的通知
        if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            try {
                String popUp = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA)).getString("androidNotification_extras_key");
                JSONObject jsonObject = new JSONObject(popUp);
                String action = jsonObject.getString("action");
                if (action.equals("newEquipmentNotice")) {
                    String voice = jsonObject.getString("voice");
                    eventBusData.setVoice(voice);
                } else if (jsonObject.has("pdId")) {
                    eventBusData.setContent_id(jsonObject.getLong("pdId"));
                }
                eventBusData.setAction(action);
                EventBus.getDefault().post(eventBusData);
            } catch (Exception e) {
                LogUtil.d("receive", "解析异常");
            }
        }
    }

}
