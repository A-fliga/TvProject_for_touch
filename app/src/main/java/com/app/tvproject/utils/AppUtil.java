package com.app.tvproject.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.SystemClock;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.util.List;

/**
 * 应用配置工具类
 * Created by www on 2017/4/13.
 */
public class AppUtil {

    /**
     * 上次点击时间戳
     */
    private static long lastClickTime;

    /**
     * 应用统一Handler
     */
    private static Handler sHandler;

    /**
     * ContentResolver对象
     */
    private static ContentResolver resolver;

    public static Handler getHandler() {
        return sHandler;
    }

    public static void initHandler() {
        sHandler = new Handler();
    }

    public static ContentResolver getResolver() {
        return resolver;
    }

    public static void initResolver(Context context) {
        resolver = context.getContentResolver();
    }

    /**
     * 获取客户端版本名
     *
     * @return
     */
    public static String getVersionName() {
        String verName = "";
        try {
            String packageName = ContextUtil.getContext().getPackageName();
            PackageManager pManager = ContextUtil.getContext().getPackageManager();
            PackageInfo pInfo = pManager.getPackageInfo(packageName, 0);
            verName = pInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return verName;
    }

    /**
     * 获取客户端版本号
     *
     * @return
     */
    public static int getVersionCode() {
        try {
            return ContextUtil.getContext().getPackageManager().getPackageInfo(
                    ContextUtil.getContext().getPackageName(), 0).versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 判断应用是否安装
     *
     * @param packageName
     * @return
     */
    public static boolean isAppExist(String packageName) {
        boolean has = false;
        try {
            final PackageManager pManager = ContextUtil.getContext().getPackageManager();
            List<PackageInfo> pInfo = pManager.getInstalledPackages(0);
            for (PackageInfo info : pInfo) {
                if (info.packageName.equals(packageName)) {
                    has = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return has;
    }

    /**
     * 判断软键盘是否启用
     *
     * @return
     */
    public static boolean isKeyBoardActive() {
        InputMethodManager imm = (InputMethodManager) ContextUtil.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        return imm.isActive();
    }

    /**
     * 打开软键盘
     *
     * @param editText
     */
    public static void openKeyBoard(EditText editText) {
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        InputMethodManager imm = (InputMethodManager) ContextUtil.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (null != imm) {
            imm.showSoftInput(editText, InputMethodManager.SHOW_FORCED);
        }
    }

    /**
     * 开关软键盘
     *
     * @param editText
     */
    public static void togKeyBoard(EditText editText) {
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        InputMethodManager imm = (InputMethodManager) ContextUtil.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (null != imm) {
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 关闭软键盘
     *
     * @param context
     */
    public static void closeKeyBoard(Context context) {
        InputMethodManager imm = (InputMethodManager) ContextUtil.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (null != imm) {
            imm.hideSoftInputFromWindow(((Activity) context)
                    .getWindow().getDecorView().getWindowToken(), 0);
        }
    }

    /**
     * 判断是否快速双击
     *
     * @return
     */
    public static boolean isFastDoubleClick(int during) {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < during) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    /**
     * 判断是否快速5次点击
     *
     * @return
     */
    final static int COUNTS = 5;//点击次数
    static long[] mHits = new long[COUNTS];

    public static void isFastFifthClick(int duration) {
            /**
             * 实现双击方法
             * src 拷贝的源数组
             * srcPos 从源数组的那个位置开始拷贝.
             * dst 目标数组
             * dstPos 从目标数组的那个位子开始写数据
             * length 拷贝的元素的个数
             */
            System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
            //实现左移，然后最后一个位置更新距离开机的时间，如果最后一个时间和最开始时间小于DURATION，即连续5次点击
            mHits[mHits.length - 1] = System.currentTimeMillis();
            if (mHits[0] >= (System.currentTimeMillis() - duration)) {
                String tips = "您已在[" + duration + "]ms内连续点击【" + mHits.length + "】次了！！！";
            }
    }



}