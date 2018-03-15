package com.app.tvproject.mvp.presenter.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.app.tvproject.R;
import com.app.tvproject.constants.Constants;
import com.app.tvproject.mvp.model.PublicModel;
import com.app.tvproject.mvp.model.data.BaseEntity;
import com.app.tvproject.mvp.model.data.EqInformationBean;
import com.app.tvproject.mvp.model.data.EventBusData;
import com.app.tvproject.mvp.model.data.UpdateBean;
import com.app.tvproject.mvp.model.data.UpdateUseEqBean;
import com.app.tvproject.mvp.view.TouchScreenActivityDelegate;
import com.app.tvproject.utils.AppUtil;
import com.app.tvproject.utils.ControlVolumeUtil;
import com.app.tvproject.utils.DialogUtil;
import com.app.tvproject.utils.DownLoadFileManager;
import com.app.tvproject.utils.LogUtil;
import com.app.tvproject.utils.NetUtil;
import com.app.tvproject.utils.PackageUtils;
import com.app.tvproject.utils.ProgressDialogUtil;
import com.app.tvproject.utils.SharedPreferencesUtil;
import com.app.tvproject.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Timer;
import java.util.TimerTask;

import rx.Subscriber;

import static com.app.tvproject.utils.PackageUtils.INSTALL_SUCCEEDED;

/**
 * Created by www on 2018/2/9.
 */

public class TouchScreenActivity extends ActivityPresenter<TouchScreenActivityDelegate> {
    //设备Id
    private long eqId = -1;

    private int countDownTime = 30000, hideUiTime = 4000;
    private Boolean isFirstStart = true, toMainActivity = false;
    //做定时任务用
    private Timer timer = new Timer();

    private static final String WEB_URL = "http://119.23.235.164:8083/wzt?villageId=";
    //做倒计时跳转用
    private TimerTask task, hideUiTask;

    private WebView webView;

    private int villageId;

    private Boolean isStop = false;

    @Override
    public Class<TouchScreenActivityDelegate> getDelegateClass() {
        return TouchScreenActivityDelegate.class;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//        viewDelegate.hideMainLL(true);
//        try {
//            Runtime.getRuntime().exec("su");
        viewDelegate.get(R.id.webView_rl).setOnClickListener(v -> {
            fastClick5();
        });
        initSetting();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }


    private void autoHideUi(int duration) {
        if (hideUiTask != null) {
            hideUiTask.cancel();
            hideUiTask = null;
        }
        hideUiTask = new TimerTask() {
            @Override
            public void run() {
                hideUI(true);
            }
        };
        timer.schedule(hideUiTask, duration);
    }

    private void initSetting() {
        //首次进入页面判断是否配置了设备和推送信息
        if (noSettings()) {
            runOnUiThread(() -> DialogUtil.showDialog(TouchScreenActivity.this, selectSettingsDialog));
        } else {
            villageId = SharedPreferencesUtil.getVillageId();
            checkUpdate();
        }
    }

    private void checkUpdate() {
        if (NetUtil.isConnectNoToast()) {
            PublicModel.getInstance().getUpdateInfo(new Subscriber<BaseEntity<UpdateBean>>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {
                    hideUI(false);
                    if (!isStop)
                        initWebView();
                }

                @Override
                public void onNext(BaseEntity<UpdateBean> updateBeanBaseEntity) {
                    if (!isStop) {
                        if (updateBeanBaseEntity.getResult() != null && Float.parseFloat(AppUtil.getVersionName()) < Float.parseFloat(updateBeanBaseEntity.getResult().versionNumber)) {
                            //开始下载更新并安装
                            LogUtil.d("qidong", "更新");
                            new Thread(() -> {
                                Looper.prepare();
                                startUpdate(updateBeanBaseEntity.getResult().resourceUrl);
                            }).start();
                        } else {
                            initWebView();
                        }
                    }
                }
            });
        } else {
            initWebView();
        }
    }

    private Boolean noSettings() {
        String alias = SharedPreferencesUtil.getJpushAlias();
        eqId = SharedPreferencesUtil.getEqId();
        return (alias == null || alias.isEmpty() || eqId == -1);
    }

    private DialogInterface.OnClickListener selectSettingsDialog = (dialog, which) -> {
        switch (which) {
            case -1:
                dialog.dismiss();
                Intent intent = new Intent(this, ChooseSettingsActivity.class);
                startMyActivityForResult(intent, Constants.CHOOSE_SETTINGS_REQUEST_CODE);
                break;
            case -2:
                finish();
                break;
        }
    };

    private void startUpdate(String apkUrl) {
        ProgressDialog pd = DialogUtil.showProgressDialog(this);
        runOnUiThread(pd::show);
        if (DownLoadFileManager.getInstance().downLoadApk(pd, apkUrl) && DownLoadFileManager.getInstance().getApkPath() != null) {
            closeDialog(pd);
            if (PackageUtils.install(this, DownLoadFileManager.getInstance().getApkPath()) != INSTALL_SUCCEEDED) {
                initWebView();
            }
        } else {
            closeDialog(pd);
            initWebView();
        }
    }

    private void closeDialog(ProgressDialog pd) {
        runOnUiThread(() -> {
            pd.hide();
            pd.dismiss();
        });
    }


    @Override
    protected void onStop() {
        super.onStop();
        isStop = true;
    }

    private void hideUI(Boolean disable) {
        Intent i;
        if (disable)
            i = new Intent("com.android.systembar.disable");
        else
            i = new Intent("com.android.systembar.enable");
        sendBroadcast(i);
    }

    private void initWebView() {
//        try {
//            if (viewDelegate != null)
//                viewDelegate.hideMainLL(false);
//        } catch (NullPointerException e) {
//        }
        webView = viewDelegate.get(R.id.webView);

        WebSettings webSettings = webView.getSettings();
        webSettings.setSupportZoom(false);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setDefaultTextEncodingName("utf-8");
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);

        webSettings.setDomStorageEnabled(true);

        webSettings.setAppCacheMaxSize(1024 * 1024 * 8);

        String appCachePath = getApplicationContext().getCacheDir().getAbsolutePath();

        webSettings.setAppCachePath(appCachePath);

        webSettings.setAllowFileAccess(true);

        webSettings.setAppCacheEnabled(true);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            webView.setWebContentsDebuggingEnabled(true);
//        }
        webView.loadUrl(WEB_URL + villageId);
        webView.setWebViewClient(new webViewClient());
        getEqInfo();
        startUpdateStates();
        //界面没操作多久后要自动跳到显示屏界面
        startCountDown(countDownTime);
        setTouchListener();
    }


    private void getEqInfo() {
        PublicModel.getInstance().getEqInfo(new Subscriber<BaseEntity<EqInformationBean>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                ToastUtil.l("获取设备信息错误，请重启");
                hideUI(false);
                finish();
            }

            @Override
            public void onNext(BaseEntity<EqInformationBean> eqInformationBeanBaseEntity) {
                ControlVolumeUtil.saveVoice(eqInformationBeanBaseEntity.getResult().voice);
            }
        }, String.valueOf(eqId));
    }

    //Web视图
    private class webViewClient extends WebViewClient {
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            isFirstStart = false;
            ProgressDialogUtil.instance().stopLoad();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMessageThread(EventBusData eventBusData) {
        switch (eventBusData.getAction()) {
            //处理设备信息
            case "newEquipmentNotice":
                //控制系统音量0-15
                ControlVolumeUtil.saveVoice(eventBusData.getVoice());
                ControlVolumeUtil.setVolume(this);
                break;
        }
    }


    private void setTouchListener() {
        viewDelegate.get(R.id.webView).setOnTouchListener((v, event) -> {

            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (task != null) {
                    task.cancel();
                    task = null;
                }
            }
            if (event.getAction() == MotionEvent.ACTION_UP)
                startCountDown(countDownTime);
            return false;
        });
    }

    private void fastClick5() {
        new HideClick().start();
        if (HideClick.sIsAlive >= 5) {
            hideUI(false);
            autoHideUi(hideUiTime);
        }
    }

    static class HideClick extends Thread {
        public static volatile int sIsAlive = 0;

        @Override
        public void run() {
            sIsAlive++;
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (sIsAlive > 0) {
                sIsAlive--;
            }
            super.run();

        }
    }

    @Override
    protected void onDestroy() {
        LogUtil.d("zhouqi", "onDestroy");
        hideUI(false);
        super.onDestroy();
        if (task != null) {
            task.cancel();
            task = null;
        }
        if (hideUiTask != null) {
            hideUiTask.cancel();
            hideUiTask = null;
        }
        if (timer != null) {
            timer.cancel();
            timer.purge();
        }
        if (webView != null) {
//            webView.clearHistory();
            ((ViewGroup) webView.getParent()).removeView(webView);
            webView.destroy();
            webView = null;
        }
        if (eqId != -1)
            updateServiceStatus(eqId, 0);

    }


    @Override
    protected void onResume() {
        super.onResume();
        toMainActivity = false;
        autoHideUi(hideUiTime);
        if (!isFirstStart) {
            startCountDown(countDownTime);
        }
        //注册eventBus
        EventBus.getDefault().register(this);
        LogUtil.d("zhouqi", "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (hideUiTask != null) {
            hideUiTask.cancel();
            hideUiTask = null;
        }
        hideUI(toMainActivity);
        //解绑eventBus
        EventBus.getDefault().unregister(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView != null && webView.canGoBack()) {
            webView.goBack(); //goBack()表示返回WebView的上一页面
            return true;
        }
        finish();//结束退出程序
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.CHOOSE_SETTINGS_REQUEST_CODE && resultCode == Constants.CHOOSE_SETTINGS_RESULT_CODE) {
            setEquipUsed(data, 1);

        } else finish();
    }


    /**
     * 通知设备已被使用
     */
    private void setEquipUsed(Intent data, int used) {
        if (data != null && data.getLongExtra("eqId", -1) != -1) {
            //保存设备id
            eqId = data.getLongExtra("eqId", -1);
            PublicModel.getInstance().setEquipmentUsed(new Subscriber<UpdateUseEqBean>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {
                    ToastUtil.l("出现意外错误，请重启app");
                    hideUI(false);
                    finish();
                }

                @Override
                public void onNext(UpdateUseEqBean baseEntity) {
                    SharedPreferencesUtil.saveEqId(TouchScreenActivity.this, eqId);
                    //第一次初始化要获取服务器端正在播放的内容集合
                    String voice = data.getStringExtra("voice");
                    int villageId = data.getIntExtra("villageId", -1);
                    if (villageId != -1) {
                        TouchScreenActivity.this.villageId = villageId;
                        SharedPreferencesUtil.saveVillageId(villageId);
                    }
                    if (eqId != -1) {
                        ControlVolumeUtil.saveVoice(voice);
                    }
                    ToastUtil.s("已选中当前设备信息");
                    checkUpdate();
                }

            }, String.valueOf(eqId), String.valueOf(used));
        } else ToastUtil.l("设备信息有误，请重启app");
    }

    private void startCountDown(int duration) {
        LogUtil.d("countDown", "刷新倒计时");

        if (task != null) {
            task.cancel();
            task = null;
        }
        task = new TimerTask() {
            @Override
            public void run() {
                Bundle bundle = new Bundle();
                bundle.putLong("eqId", eqId);
                toMainActivity = true;
                startMyActivity(MainActivity.class, bundle);
            }
        };
        timer.schedule(task, duration);
    }


    private void startUpdateStates() {
        // 更新设备的连接状态
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                updateServiceStatus(eqId, 1);
            }
        };
        timer.schedule(task, 0, 1800000);
    }


    private void updateServiceStatus(long eqId, int status) {
        PublicModel.getInstance().updateEqStatus(new Subscriber<UpdateUseEqBean>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                hideUI(false);
            }

            @Override
            public void onNext(UpdateUseEqBean updateUseEqBean) {
                LogUtil.w("lianjie", updateUseEqBean.toString());
            }
        }, String.valueOf(eqId), String.valueOf(status));
    }
}
