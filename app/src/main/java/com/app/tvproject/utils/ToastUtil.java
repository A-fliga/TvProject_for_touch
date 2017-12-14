package com.app.tvproject.utils;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.widget.Toast;

/**
 * 可在任何线程使用的toast工具类
 * <p>
 * Created by www on 2017/5/16.
 */
public class ToastUtil {

    public static Toast toast;

    /**
     * 短Toast提示
     *
     * @param msg
     */
    public static void s(@NonNull final String msg) {
        if (toast == null) {
            toast = Toast.makeText(ContextUtil.getContext(), msg, Toast.LENGTH_SHORT);
        } else {
            toast.setText(msg);
        }
        toast.show();
    }

    /**
     * 短Toast提示
     *
     * @param res
     */
    public static void s(@StringRes final int res) {
        if (toast == null) {
            toast = Toast.makeText(ContextUtil.getContext(), res, Toast.LENGTH_SHORT);
        } else {
            toast.setText(res);
        }
        toast.show();
    }

    /**
     * 长Toast提示
     *
     * @param msg
     */
    public static void l(@NonNull final String msg) {
        if (toast == null) {
            toast = Toast.makeText(ContextUtil.getContext(), msg, Toast.LENGTH_LONG);
        } else {
            toast.setText(msg);
        }
        toast.show();
    }

    /**
     * 长Toast提示
     *
     * @param res
     */
    public static void l(@StringRes final int res) {
        if (toast == null) {
            toast = Toast.makeText(ContextUtil.getContext(), res, Toast.LENGTH_LONG);
        } else {
            toast.setText(res);
        }
        toast.show();
    }
}