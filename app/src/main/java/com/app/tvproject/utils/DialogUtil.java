package com.app.tvproject.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

/**
 * Created by www on 2017/11/16.
 */

public class DialogUtil {
    /**
     * 这是兼容的 AlertDialog
     */
    public static void showDialog(Context context, DialogInterface.OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("温馨提示").setCancelable(false).setPositiveButton("去选择",listener).
                setNegativeButton("取消",listener).setMessage("请选择您的设备信息（确认后不可更改）").show();
    }
    public static ProgressDialog showProgressDialog(Context context){
        ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage("正在更新，请勿关闭！");
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setCanceledOnTouchOutside(false);
        pd.setCancelable(false);
        pd.setProgress(0);
        return pd;
    }
}
