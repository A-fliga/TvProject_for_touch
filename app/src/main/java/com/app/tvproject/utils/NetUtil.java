package com.app.tvproject.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


/**
 * 网络工具类
 * Created by orchid on 2017/4/13.
 */
public class NetUtil {

    /**
     * 获取当前网络连接类别
     *
     * @return state -1无网；0:WIFI网络；1:其他网络
     */
    public static Boolean isConnect() {
        Boolean state = false;
        try {
            ConnectivityManager cm = (ConnectivityManager) ContextUtil.getContext()
                    .getSystemService(Context.CONNECTIVITY_SERVICE);// 获取系统的连接服务
            NetworkInfo activeNetInfo = cm.getActiveNetworkInfo();// 获取网络的连接情况
            if (null == activeNetInfo) {// 无网
                state = false;
                ToastUtil.s("无网络连接");
            } else if (activeNetInfo.isConnected()) {// 有网
                LogUtil.w("net", activeNetInfo.getType() + "  " + ConnectivityManager.TYPE_WIFI + " " + ConnectivityManager.TYPE_MOBILE);
                state = true;
            }
        } catch (Exception e) {
            state = false;
            e.printStackTrace();
        }
        return state;
    }

    public static Boolean isConnectNoToast() {
        Boolean state = false;
        try {
            ConnectivityManager cm = (ConnectivityManager) ContextUtil.getContext()
                    .getSystemService(Context.CONNECTIVITY_SERVICE);// 获取系统的连接服务
            NetworkInfo activeNetInfo = cm.getActiveNetworkInfo();// 获取网络的连接情况
            if (null == activeNetInfo) {// 无网
                state = false;
            } else if (activeNetInfo.isConnected()) {// 有网
                state = true;
            }
        } catch (Exception e) {
            state = false;
            e.printStackTrace();
        }
        return state;
    }
}