package com.app.tvproject.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;

import com.app.tvproject.utils.LogUtil;
import com.app.tvproject.utils.NetUtil;

/**
 * 监听网络
 * Created by www on 2017/11/3 0003.
 */

public class NetBroadCastReceiver extends BroadcastReceiver {
    private static NetListener listener;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            Boolean isConnect = NetUtil.isConnectNoToast();
            if (listener != null && isConnect) {
                listener.netChange(isConnect);
            }
        }
    }

    public static void setNetChangeListener(NetListener listener) {
        NetBroadCastReceiver.listener = listener;
    }

    public interface NetListener {
        void netChange(Boolean isConnect);
    }

}
