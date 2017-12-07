package com.app.tvproject.utils;

import android.os.Build;

import java.util.Locale;

/**
 * 系统配置工具类
 * Created by orchid on 2017/4/13.
 */
public class SystemUtil {

    /**
     * 获取Android SDK版本号
     *
     * @return
     */
    public static int getAndroidSDKVersion() {
        return Build.VERSION.SDK_INT;
    }

    public static boolean isZh() {
        Locale locale = ContextUtil.getContext().getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        if (language.endsWith("zh"))
            return true;
        else
            return false;
    }
}