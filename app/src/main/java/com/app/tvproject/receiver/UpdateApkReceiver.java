package com.app.tvproject.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.app.tvproject.mvp.presenter.activity.MainActivity;
import com.app.tvproject.mvp.presenter.activity.TouchScreenActivity;
import com.app.tvproject.utils.ToastUtil;

/**
 * Created by www on 2017/12/21.
 */

public class UpdateApkReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.PACKAGE_REPLACED")){
            ToastUtil.l("更新成功");
            Intent intent2 = new Intent(context, TouchScreenActivity.class);
            intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent2);
        }
    }

}
