package com.app.tvproject.utils;

import android.content.Context;

import java.lang.reflect.Method;

/**
 * Context工具类
 * Created by www on 2017/4/13.
 */
public class ContextUtil {

    /**
     * Context对象
     */
    private static Context mInstance;

    /**
     * 初始化
     *
     * @param context
     */
    public static void init(Context context) {
        mInstance = context;
//        ExceptionHandler.getInstance().init();
        AppUtil.initHandler();
        AppUtil.initResolver(context);
    }

    /**
     * 获取Context对象
     *
     * @return
     */
    public static Context getContext() {
        if (null == mInstance) {
            try {
                Context context = (Context) Class.forName("android.app.ActivityThread")
                        .getMethod("currentApplication").invoke(null, (Object[]) null);
                if (null != context) {
                    mInstance = context;
                    return context;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                Context context = (Context) Class.forName("android.app.AppGlobals")
                        .getMethod("getInitialApplication").invoke(null, (Object[]) null);
                if (null != context) {
                    mInstance = context;
                    return context;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                Class<?> ActivityThread = Class.forName("android.app.ActivityThread");
                Method method = ActivityThread.getMethod("currentActivityThread");
                Object currentActivityThread = method.invoke(ActivityThread);
                Method getApplication = currentActivityThread.getClass().getMethod("getApplication");
                mInstance = (Context) getApplication.invoke(currentActivityThread);
            } catch (Exception e) {
                e.printStackTrace();
            }
            throw new IllegalStateException("ContextUtil is not initialed, it is recommend to init with application context.");
        }
        return mInstance;
    }
}