package com.app.tvproject.utils;

import android.util.Log;

import com.app.tvproject.BuildConfig;


/**
 * 日志打印类
 * Created by www on 3/16/17.
 */
public final class LogUtil {

    private static final String TAG = "CLOUD";

    public static void d(String debug) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, getMethodInfo() + debug);
        }
    }

    public static void d(String pTAG, String debug) {
        if (BuildConfig.DEBUG) {
            Log.d(pTAG, debug);
        }
    }

    public static void i(String info) {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, getMethodInfo() + info);
        }
    }

    public static void i(String pTAG, String info) {
        if (BuildConfig.DEBUG) {
            Log.i(pTAG, getMethodInfo() + info);
        }
    }

    public static void w(String info) {
        if (BuildConfig.DEBUG) {
            Log.w(TAG, getMethodInfo() + info);
        }
    }

    public static void w(String pTAG, String info) {
        if (BuildConfig.DEBUG) {
            Log.w(pTAG, getMethodInfo() + info);
        }
    }

    public static void e(String error) {
        if (BuildConfig.DEBUG) {
            Log.e(TAG, getMethodInfo() + error);
        }
    }

    public static void e(String pTAG, String error) {
        if (BuildConfig.DEBUG) {
            Log.e(pTAG, getMethodInfo() + error);
        }
    }

    /**
     * 从堆栈中取得必要方法信息
     *
     * @return
     */
    private static String getMethodInfo() {
        final StackTraceElement[] sts = Thread.currentThread().getStackTrace();
        if (null == sts) {
            return null;
        }
        for (StackTraceElement st : sts) {
            if (st.isNativeMethod()) {
                continue;
            }
            if (st.getClassName().equals(Thread.class.getName())) {
                continue;
            }
            if (st.getClassName().equals(LogUtil.class.getName())) {
                continue;
            }
            return "[Thread-" + Thread.currentThread().getName() + "]: "
                    + st.getClassName() + ":" + st.getLineNumber() + "->"
                    + st.getMethodName() + "() ";
        }
        return null;
    }
}