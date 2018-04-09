package com.app.tvproject.mvp.presenter.activity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.VideoView;

import com.app.tvproject.R;
import com.app.tvproject.constants.Constants;
import com.app.tvproject.mvp.adapter.InfoListAdapter;
import com.app.tvproject.mvp.model.PublicModel;
import com.app.tvproject.mvp.model.data.BaseEntity;
import com.app.tvproject.mvp.model.data.ContentBean;
import com.app.tvproject.mvp.model.data.EqInformationBean;
import com.app.tvproject.mvp.model.data.EventBusData;
import com.app.tvproject.mvp.model.data.PublishListBean;
import com.app.tvproject.mvp.model.data.UpdateUseEqBean;
import com.app.tvproject.mvp.model.data.WeatherBean;
import com.app.tvproject.mvp.presenter.fragment.ImgWithTextFragment;
import com.app.tvproject.mvp.presenter.fragment.NullInfoFragment;
import com.app.tvproject.mvp.presenter.fragment.VideoFragment;
import com.app.tvproject.mvp.view.CustomerView.CustomerVideoView;
import com.app.tvproject.mvp.view.MainActivityDelegate;
import com.app.tvproject.myDao.DaoManager;
import com.app.tvproject.receiver.NetBroadCastReceiver;
import com.app.tvproject.utils.BaiduVoiceUtil;
import com.app.tvproject.utils.ControlVolumeUtil;
import com.app.tvproject.utils.DownLoadFileManager;
import com.app.tvproject.utils.FileUtil;
import com.app.tvproject.utils.LogUtil;
import com.app.tvproject.utils.NetUtil;
import com.app.tvproject.utils.SharedPreferencesUtil;
import com.app.tvproject.utils.ToastUtil;
import com.baidu.tts.client.SpeechSynthesizer;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import rx.Subscriber;

import static com.app.tvproject.myDao.DaoUtil.deleteAll;
import static com.app.tvproject.myDao.DaoUtil.deleteContentById;
import static com.app.tvproject.myDao.DaoUtil.insertOrReplaceContent;
import static com.app.tvproject.myDao.DaoUtil.insertOrReplaceList;
import static com.app.tvproject.myDao.DaoUtil.loadAllValidInformation;
import static com.app.tvproject.myDao.DaoUtil.loadAllValidNotice;
import static com.app.tvproject.myDao.DaoUtil.queryContentById;

/**
 * Created by www on 2017/11/16.
 */

public class MainActivity extends ActivityPresenter<MainActivityDelegate> implements NetBroadCastReceiver.NetListener {
    //记录播放进度
    private int position = 0;
    //设备Id
    private long eqId = -1;

    //做定时任务用
    private Timer timer;

    private TimerTask noticeTask, informationTask, interCutNoticeTask, interCutInfoTask, dataTask, weatherTask;

    private TimerTask finishActivityTask;
    //百度语音的引擎
    private SpeechSynthesizer mSpeechSynthesizer;

    //音乐播放器
    private MediaPlayer mediaPlayer;
    private VideoFragment videoFragment;
    private CustomerVideoView videoView;

    private ImgWithTextFragment cutImgFragment;
    private VideoFragment cutVideoFragment;
    //记住正在插播的通知或资讯的Id，处理停播用
    private long interCutNoticeId, interCutInfoId;

    //设置状态 来保证页面未初始化完成时转换fragment报错的问题
    private Boolean isStop = false;
    //记录插播时之前的页面播放了多久用的
    private long cutNoticeTime = 0, cutInfoTime = 0;
    private Boolean isChangeSort = false;
    private Boolean isCutting = false;
    private ContentBean cutBean2 = null;//二次插播的数据
    //测试信息用的
    public TextView info_tv,info_tv_push;
    private RecyclerView recyclerView, notice_list_recycler;
    private List<ContentBean> beanList = new ArrayList<>();
    private List<ContentBean> noticeList = new ArrayList<>();
    private InfoListAdapter adapter, adapter2;
    private Button nextInfoBtn;
    private static MainActivity activity;
    int i = 0;

    @Override
    public Class<MainActivityDelegate> getDelegateClass() {
        return MainActivityDelegate.class;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        hideUI(true);
        DownLoadFileManager.getInstance().stopDownLoad(false);
        Bundle bundle = getIntent().getExtras();

        timer = new Timer();
        if (viewDelegate != null) { //这些测试用的
            nextInfoBtn = (Button) findViewById(R.id.nextInfoBtn);
            List<Integer> title = new ArrayList<>();
            title.add(R.string.tv_right_content);
            title.add(R.string.tv_right_content1);
            title.add(R.string.tv_right_content2);
            recyclerView = viewDelegate.get(R.id.info_list_recycler);
            info_tv = viewDelegate.get(R.id.info_tv);
            info_tv_push = viewDelegate.get(R.id.info_tv_push);
            notice_list_recycler = viewDelegate.get(R.id.notice_list_recycler);
            if (Constants.isDebug) {
                recyclerView.setVisibility(View.VISIBLE);
                info_tv.setVisibility(View.VISIBLE);
                info_tv_push.setVisibility(View.VISIBLE);
            }



            TextView tv = (TextView) findViewById(R.id.right_main_tv);
//            i = SharedPreferencesUtil.getTitle();
            i = 1;
            if (i == 1 || i == 2) {
                tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 58);
            } else tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 65);
            tv.setText(title.get(i));
            nextInfoBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView tv = (TextView) findViewById(R.id.right_main_tv);
                    i++;
                    if (i > 2)
                        i = 0;
                    SharedPreferencesUtil.saveTitle(i);
                    if (i == 1 || i == 2) {
                        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 58);
                    } else tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 65);
                    tv.setText(title.get(i));

//                    if (getInformationTask() != null) {
//                        getInformationTask().cancel();
//                        setTaskNull(false);
//                    }
//                    if (getInterCutInfoTask() != null) {
//                        getInterCutInfoTask().cancel();
//                        setTaskNull(true);
//                    }
//                    nextInformation(false);
                }
            });
        }
        if (bundle != null) {
            eqId = bundle.getLong("eqId", -1);
            info_tv.setText("当前设备：" + eqId);
            //删除下载的临时数据
            DownLoadFileManager.getInstance().deleteTempData();
            //初始化服务器数据
            initServiceData();
        } else ToastUtil.l("数据出错");
//        eqId = getIntent().getLongExtra("eqId", -1);
////        viewDelegate.hideMainRl(true);
//        initServiceData();

        //把logCat写到本地
//        log2File();
    }

    private void hideUI(Boolean disable) {
        Intent i;
        if (disable)
            i = new Intent("com.android.systembar.disable");
        else
            i = new Intent("com.android.systembar.enable");
        sendBroadcast(i);
    }


    private void log2File() {
        try {
            Process p = Runtime.getRuntime().exec("logcat -d");
            final InputStream is = p.getInputStream();
            new Thread() {
                @Override
                public void run() {
                    FileOutputStream os = null;
                    try {
                        File file = new File(Constants.DOWNLOAD_DIR + "logCat");
                        if (!file.exists()) {
                            file.mkdirs();
                        }
                        os = new FileOutputStream(Constants.DOWNLOAD_DIR + "logCat/" + "logMsg.txt");
                        int len = 0;
                        byte[] buf = new byte[1024];
                        while (-1 != (len = is.read(buf))) {
                            os.write(buf, 0, len);
                            os.flush();
                        }
                    } catch (Exception e) {
                        Log.d("writelog", "read logcat process failed. message: " + e.getMessage());
                    } finally {
                        if (null != os) {
                            try {
                                os.close();
                                os = null;
                            } catch (IOException e) {
                                // Do nothing
                            }
                        }
                    }
                }
            }.start();
        } catch (Exception e) {
            Log.d("writelog", "open logcat process failed. message: " + e.getMessage());
        }
    }


    private void initServiceData() {
        getEqInfo();
        initData(false);
        finishActivityTask = new TimerTask() {
            @Override
            public void run() {
                toDestroy();
                finish();
            }
        };
        if (timer != null)
            timer.schedule(finishActivityTask, 3600000);
        //初始化百度语音
        initBaiDuVoice();
//        viewDelegate.hideMainRl(true);
        //点击屏幕要退回到webView
        viewDelegate.get(R.id.main_rl).setOnClickListener(v -> {
                    toDestroy();
                    finish();
                }
        );
    }


    private void getEqInfo() {
        PublicModel.getInstance().getEqInfo(new Subscriber<BaseEntity<EqInformationBean>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                ToastUtil.l("获取设备信息错误");
            }

            @Override
            public void onNext(BaseEntity<EqInformationBean> eqInformationBeanBaseEntity) {
                ControlVolumeUtil.saveVoice(eqInformationBeanBaseEntity.getResult().voice);
            }
        }, String.valueOf(eqId));
    }


    private void initData(Boolean deleteAll) {
        //显示出视图
        viewDelegate.hideMainRl(false);
        //每次进入app，要拉取一遍服务器的播放列表
        getPublishList(deleteAll, false);

        //注册eventBus
        EventBus.getDefault().register(this);

        //清空数据库和shared及所有缓存文件，测试用
        Button button = viewDelegate.get(R.id.clear);
        button.setOnClickListener(v -> deleteDataAndShared(true, true));

        //更新一下时间和天气
        getTime();
        getNowWeather();
    }

    private void deleteDataAndShared(Boolean deleteFile, Boolean clearShared) {
        //清空保存的下标和数据库内容
        deleteAll();
        if (clearShared) {
            LogUtil.d("shuaxin", "清除shared");
            SharedPreferencesUtil.resetShared();
        }
        //是否要删掉原来的全部文件
        if (deleteFile)
            DownLoadFileManager.getInstance().deleteFilesByDirectory(DownLoadFileManager.getInstance().getDownloadDir());
    }


    private void initInfo() {
        //取出本地资讯
        List<ContentBean> informationList = loadAllValidInformation();
        beanList.clear();
        beanList.addAll(informationList);
        adapter = new InfoListAdapter(this, beanList, true);
        initRecycler(recyclerView, adapter);
        if (informationList.size() != 0) {
            nextInformation(true);
        } else {
            setInfoNull(-1);
        }
    }

    private void initRecycler(RecyclerView recyclerView, InfoListAdapter adapter) {

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    private void getPublishList(Boolean deleteAll, Boolean clearShared) {
        if (viewDelegate != null) {
            if (NetUtil.isConnectNoToast()) {
                //获取设备的播放列表
                getPublishDetailList(eqId, deleteAll, clearShared);
            } else {
                // 没网络就只播放本地数据库的内容
                startLocalDataPlay();
            }
        }
    }


    private void initNotice() {
        //取出通知
        List<ContentBean> notice = loadAllValidNotice();
        noticeList.clear();
        noticeList.addAll(notice);
        adapter2 = new InfoListAdapter(this, noticeList, false);
        initRecycler(notice_list_recycler, adapter2);
        if (notice.size() != 0) {
            nextNotice(true);
        } else if (viewDelegate != null) viewDelegate.setNoticeNull();
    }

    //开始播放时长倒计时并循环数组
    private synchronized void countDownNotice(long duration) {
        noticeTask = new TimerTask() {
            @Override
            public void run() {
                cutNoticeTime = 0;
                nextNotice(false);
            }
        };
        if (timer != null) {
            if (duration >= 0)
                timer.schedule(noticeTask, duration * 1000);
            else timer.schedule(noticeTask, 15000);
        }
        cutNoticeTime = System.currentTimeMillis();
    }

    // 这里要注意正在播放的内容的下标的处理,noAddPosition：是否不加1，true为不加
    private synchronized void nextNotice(boolean noAddPosition) {
        List<ContentBean> noticeList = loadAllValidNotice();
//        LogUtil.w("ceshi", "开始下一条：noticeList.size()=" + noticeList.size() + " " + noAddPosition);
        if (noticeList.size() == 0) {
            viewDelegate.setNoticeNull();
        } else {
            //获取准确的下一个要播放的position
            int nextPosition = noAddPosition ? SharedPreferencesUtil.getNoticePosition() :
                    (SharedPreferencesUtil.getNoticePosition() + 1) % noticeList.size();
            if (nextPosition == -1)
                nextPosition = 0;
//            LogUtil.w("ceshi", "应该播的坐标：" + nextPosition);
            ContentBean contentBean = noticeList.get(nextPosition);
            if (contentBean != null && viewDelegate != null) {
                contentBean.setSpots(0);
                viewDelegate.startMarquee(contentBean);
//                LogUtil.w("ceshi", "正在播放的ID：" + contentBean.getId() + contentBean.getHeadline());
                SharedPreferencesUtil.saveNoticeId(contentBean.getId());
//                LogUtil.w("ceshi", "开始进入倒计时");
                countDownNotice(contentBean.getDuration());
            }
            //避免当前bean为空但list还有内容的意外情况
            else if (loadAllValidNotice().size() != 0) {
                nextNotice(false);
            }
        }
    }


    private void startLocalDataPlay() {
        isChangeSort = false;
        //注册listener
        NetBroadCastReceiver.setNetChangeListener(this);
        //取出资讯内容
        initInfo();
        //取出通知
        initNotice();
    }


    private void getPublishDetailList(long equipId, Boolean deleteAll, Boolean clearShared) {

        PublicModel.getInstance().getPublishList(new Subscriber<PublishListBean>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                if (!isStop && viewDelegate != null) {
                    DownLoadFileManager.getInstance().stopDownLoad(false);
                    startLocalDataPlay();
                }
            }

            @Override
            public void onNext(PublishListBean publishListBean) {
                if (!isStop && viewDelegate != null) {
                    DownLoadFileManager.getInstance().stopDownLoad(false);
                    List<ContentBean> serverList = new ArrayList<>();
                    serverList.addAll(publishListBean.result);
                    //是否要清空数据
                    if (deleteAll)
                        refreshData(serverList);
                    else
                        compareWithServer(serverList, clearShared);
                    isChangeSort = false;
                    NetBroadCastReceiver.setNetChangeListener(MainActivity.this);
                }
            }
        }, String.valueOf(equipId), null);
    }


    private void refreshData(List<ContentBean> serverList) {
        deleteDataAndShared(false, true);
//        for (int i = 0; i < serverList.size(); i++) {
//            if (serverList.get(i).size() != 0) {
        insertOrReplaceList(serverList);
//            }
//        }
        startDownLoad();
        initInfo();
        initNotice();
    }


    /**
     * 先拿到pushList, 然后取出本地所有的资讯成list<资讯1> 把本地所有的缓存和share删了（除了设备号）。
     * 把pushlist保存到本地数据库，提取出pushlist里所有的资讯成list<资讯2>,把两个list的id遍历出来成list<id1>,list<id2>
     * 遍历list<id2>，
     * 如果list<id1>里面有，则在list<资讯1> indexOf出这个id对应的contentBean，比较updateTime,如果2大于1则去下载，else 检查
     * 1中的imgUrl对应的文件是否存在，不存在，走下载，存在，则替换掉2中的imgUrl
     * 如果list<id1>中没有，则根据id查询contentBean，去下载imgUrl
     * 之后调用init方法 搞定
     */
    private void compareWithServer(List<ContentBean> serverList, Boolean clearShared) {
        if (serverList.size() != 0) {
            List<ContentBean> infoList = new ArrayList<>();
            List<ContentBean> noticeList = new ArrayList<>();
            List<ContentBean> resultList = new ArrayList<>();
            for (int i = 0; i < serverList.size(); i++) {
                if (serverList.get(i).getPublishTypeId() == Constants.PUBLISH_TYPE_INFORMATION || serverList.get(i).getPublishTypeId() == Constants.PUBLISH_TYPE_ADVERT)
                    infoList.add(serverList.get(i));
                if (serverList.get(i).getPublishTypeId() == Constants.PUBLISH_TYPE_NOTICE) {
                    noticeList.add(serverList.get(i));
                }
            }
            for (int i = 0; i < infoList.size(); i++) {
                ContentBean bean = infoList.get(i);
                if (bean.getResourcesUrl() != null) {
                    bean.setResourcesDir(FileUtil.getFileName(bean));
                    if (hasBgm(bean)) {
                        bean.setBgmDir(FileUtil.getBgmFileName(bean));
                    }
                    resultList.add(bean);
                }
            }

            if (resultList.size() == 0) {
                getListNull(true, false);
            } else {
                //之前本地的集合和集合Id
                List<ContentBean> beforeList = loadAllValidInformation();
                List<Long> beforeId = new ArrayList<>();
                for (int i = 0; i < beforeList.size(); i++) {
                    beforeId.add(beforeList.get(i).getId());
                }
                //服务器得到的集合和Id
                List<Long> afterId = new ArrayList<>();
                for (int i = 0; i < resultList.size(); i++) {
                    afterId.add(resultList.get(i).getId());
                }

                //要找出相同的Id和对应下标
                List<Long> sameId = new ArrayList<>();
                List<Integer> sameIdIndex = new ArrayList<>();
                for (int j = 0; j < afterId.size(); j++) {
                    for (int i = 0; i < beforeId.size(); i++) {
                        if (beforeId.get(i).equals(afterId.get(j))) {
                            LogUtil.w("测试刷新", "有相同的id，id值为：" + afterId.get(j) + "标题为:" + queryContentById(afterId.get(j)).getHeadline() + "在after的下标为:" + j);
                            sameId.add(afterId.get(j));
                            sameIdIndex.add(j);
                        }
                    }
                }
                //有相同id的，要比较它们的sortTime，如果发现有编辑过，就删掉本地缓存，没编辑过，就设置图片路径
                for (int i = 0; i < sameId.size(); i++) {
                    ContentBean beforeBean = queryContentById(sameId.get(i));
                    ContentBean afterBean = resultList.get(sameIdIndex.get(i));
                    LogUtil.w("测试刷新", "相同Id它们的sort为：beforeBean：" + beforeBean.getSort() + " afterBean:" + afterBean.getSort());
                    //编辑过，要把原来的多余数据删了
                    if (beforeBean.getSort() < afterBean.getSort()) {
//                        DownLoadFileManager.getInstance().addDeleteTask(beforeBean.getResourcesDir());
                        if (hasBgm(beforeBean)) {
//                            DownLoadFileManager.getInstance().addDeleteTask(beforeBean.getBgmDir());
                        }
                    }
                }
                beforeId.removeAll(sameId);
                for (int i = 0; i < beforeId.size(); i++) {
                    ContentBean beforeBean = queryContentById(beforeId.get(i));
                    DownLoadFileManager.getInstance().addDeleteTask(beforeBean.getResourcesDir());
                    if (hasBgm(beforeBean))
                        DownLoadFileManager.getInstance().addDeleteTask(beforeBean.getBgmDir());
                }
                deleteDataAndShared(false, clearShared);
                insertOrReplaceList(resultList);
                insertOrReplaceList(noticeList);
                if (!isStop && viewDelegate != null) {
                    startDownLoad();
                    initInfo();
                    initNotice();
                }
                LogUtil.d("qidong", "对比完成");
            }
        } else getListNull(true, true);
    }

    private void getListNull(Boolean deleteFile, Boolean clearShared) {
        setInfoNull(-1);
        deleteDataAndShared(deleteFile, clearShared);
        initInfo();
        initNotice();
    }


    //对比文件是否都存在
    private Boolean isFileAllExists(String[] imgUrl) {
        Boolean allExists = true;
        for (String anImgUrl : imgUrl) {
            File file = new File(anImgUrl);
            if (!file.exists())
                allExists = false;
        }
        return allExists;
    }

    private void startDownLoad() {
        List<ContentBean> infoList = loadAllValidInformation();
        for (int i = 0; i < infoList.size(); i++) {
            ContentBean contentBean = infoList.get(i);
            if (contentBean.getResourcesDir() != null) {
                initDownLoad(contentBean);
            }
        }
    }


    //定时刷新时间、日期
    private void getTime() {
        viewDelegate.initDate();
        viewDelegate.initLunar();
        dataTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> {
                    if (viewDelegate != null)
                        viewDelegate.initClock();
                });
            }
        };
        if (timer != null)
            timer.schedule(dataTask, 0, 1000);
    }


    //定时更新天气 2小时更新一次
    private void getNowWeather() {
        weatherTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> {
                    PublicModel.getInstance().getWeather(new Subscriber<WeatherBean>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
//                            LogUtil.w("weather", e.toString());
                        }

                        @Override
                        public void onNext(WeatherBean weatherBean) {
                            viewDelegate.initWeather(weatherBean.result);
                        }
                    });
                });
            }
        };
        if (timer != null)
            timer.schedule(weatherTask, 0, 7200000);
    }

    //初始化百度语音的东西
    private void initBaiDuVoice() {
        BaiduVoiceUtil.initialEnv();
        mSpeechSynthesizer = BaiduVoiceUtil.initTTs();
    }


    /**
     * 接受更新ui的事件
     *
     * @param
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMessageThread(EventBusData eventBusData) {
//        ToastUtil.l("收到消息了,收到消息了,收到消息了,收到消息了,收到消息了" +
//                "收到消息了,收到消息了,收到消息了");
        String action = eventBusData.getAction();
        long contentId = eventBusData.getContent_id();
        switch (action) {
            //处理内容信息
            case "pushNotice":
                info_tv_push.setText("设备Id:" + eqId + " 收到的内容Id:" + contentId);
                getPublishContent(contentId);
                break;
            //处理设备信息
            case "newEquipmentNotice":
                //控制系统音量0-15
                ControlVolumeUtil.saveVoice(eventBusData.getVoice());
                ControlVolumeUtil.setVolume(this);
                break;
            //处理清空缓存信息
            case "emptyNotice":
                //清空内容缓存
                deleteDataAndShared(true, true);
                break;
            case "updateNotice":
                Log.d("sss", "updateNotice");
                getPublishContent(contentId);
                break;

            //是否要刷新列表
            case "changeSort":
                if (isChangeSort) {
                    ToastUtil.l("请过一会再刷新");
                } else {
                    DownLoadFileManager.getInstance().stopDownLoad(true);
                    stopVoiceAndVideo();
                    stopTask();
                    isChangeSort = true;
                    getPublishList(false, true);
                }
                break;
            //停播
            case "stopPlay":
                ContentBean contentBean = queryContentById(contentId);
                if (contentBean != null) {
                    switch (contentBean.getPublishTypeId()) {
                        //停播的是通知
                        case Constants.PUBLISH_TYPE_NOTICE:
                            stopNotice(contentId);
                            break;
                        //停播的是资讯
                        case Constants.PUBLISH_TYPE_INFORMATION:
                        case Constants.PUBLISH_TYPE_ADVERT:
                            beanList.remove(contentBean);
                            if (adapter != null)
                                adapter.notifyDataSetChanged();
                            DownLoadFileManager.getInstance().setStopId(contentId);
                            stopInformation(contentId, contentBean);
                            DownLoadFileManager.getInstance().setStopId(-1);
                            break;
                    }
                }
        }

    }

    private void stopTask() {
        if (noticeTask != null) {
            noticeTask.cancel();
            noticeTask = null;
        }
        if (informationTask != null) {
            informationTask.cancel();
            informationTask = null;
        }
        if (interCutInfoTask != null) {
            interCutInfoTask.cancel();
            interCutInfoTask = null;
        }
        if (interCutNoticeTask != null) {
            interCutNoticeTask.cancel();
            interCutNoticeTask = null;
        }
    }


    private void stopInformation(long contentId, ContentBean stopBean) {
        LogUtil.w("ceshi", "停播： informationId" + SharedPreferencesUtil.getInformationId() + "   stopId" + contentId);
        //如果停播的资讯正在播放，并且在插播的内容不在播放，要取消原来的跳转任务并且立即切换下一个内容,这里记得一定要先切换再删数据库，要不查询下标会不对
        if (SharedPreferencesUtil.getInformationId() == contentId) {
            if (informationTask != null)
                informationTask.cancel();
            nextInformation(false);
        }
        if (contentId == interCutInfoId) {
            interCutInfoId = -1;
            ContentBean beforeInfoBean = queryContentById(SharedPreferencesUtil.getInformationId());
            showOrHideContent(queryContentById(contentId).getImgormo(), beforeInfoBean, stopBean);
            if (cutInfoTime != 0 && loadAllValidInformation().size() != 1) {
                countDownInformation((beforeInfoBean.getDuration() * 1000 - cutInfoTime + 1000) / 1000);
            }
            if (interCutInfoTask != null)
                interCutInfoTask.cancel();
        }
        ContentBean contentBean = queryContentById(contentId);
        deleteContentById(contentId);
        if (loadAllValidInformation().size() == 0) {
            setInfoNull(contentBean.getImgormo());
        }
    }

    private void setInfoNull(int isImg) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        NullInfoFragment nullInfoFragment = new NullInfoFragment();
        if (!isStop) {
            if (isImg == Constants.IS_IMAGE)
                transaction.replace(R.id.img_frameLayout, nullInfoFragment).commit();
            if (isImg == Constants.IS_VIDEO)
                transaction.replace(R.id.videoFrameLayout, nullInfoFragment).commit();
        }
        if (isImg == -1) {
            viewDelegate.setImgFrameVisibility(false);
            viewDelegate.setVideoFrameVisibility(false);
        }
        viewDelegate.setTitle("");
        viewDelegate.setTagContent("");
        stopVoiceAndVideo();
    }

    private void stopNotice(long contentId) {
//        LogUtil.w("ceshi", "停播： noticeId" + SharedPreferencesUtil.getNoticeId() + "   stopId" + contentId);
        //如果停播的通知正在播放，并且在插播的内容不在播放，要取消原来的跳转任务并且立即切换下一个内容,这里一定要先切换再删数据库，如果先删，next方法里查询新的position会错误
        if (SharedPreferencesUtil.getNoticeId() == contentId) {
            if (noticeTask != null)
                noticeTask.cancel();
            nextNotice(false);
        }
        //如果停播的是插播的内容，要判断插播的内容是否在播放，是的话马上替换成原来的内容
        if (contentId == interCutNoticeId) {
            interCutNoticeId = -1;
            ContentBean beforeNoticeBean = queryContentById(SharedPreferencesUtil.getNoticeId());
            if (viewDelegate != null)
                viewDelegate.startMarquee(beforeNoticeBean);
            if (cutNoticeTime != 0 && loadAllValidNotice().size() != 1) {
                countDownNotice((beforeNoticeBean.getDuration() * 1000 - cutNoticeTime + 1500) / 1000);
            }
            if (interCutNoticeTask != null) {
                interCutNoticeTask.cancel();
                interCutNoticeTask = null;
            }
        }
        deleteContentById(contentId);
        if (loadAllValidNotice().size() == 0 && viewDelegate != null)
            viewDelegate.setNoticeNull();
    }


    /**
     * 通知设备已被使用
     */
    private void setEquipUsed(long id, int used) {
        PublicModel.getInstance().setEquipmentUsed(new Subscriber<UpdateUseEqBean>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(UpdateUseEqBean baseEntity) {
                ToastUtil.s("已选中当前设备信息");
            }

        }, String.valueOf(id), String.valueOf(used));
    }


    private void getPublishContent(long contentId) {
        PublicModel.getInstance().getPublishContent(new Subscriber<BaseEntity<ContentBean>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
//                LogUtil.w(e.toString());
            }

            @Override
            public void onNext(BaseEntity<ContentBean> contentBeanBaseEntity) {
                beanList.add(contentBeanBaseEntity.getResult());
                if (adapter != null)
                    adapter.notifyDataSetChanged();
                startShowContent(contentBeanBaseEntity.getResult());
            }
        }, String.valueOf(contentId), String.valueOf(eqId));
    }


    /**
     * 开始播放内容
     */
    private void startShowContent(ContentBean contentBean) {
        if (System.currentTimeMillis() < contentBean.getEndtime()) {
            //获取播放的指定时间
            String time = contentBean.getPlayTime();
            //当定时的时候播放完成后才加入播放列表
            if (time != null && !time.isEmpty()) {
                // TODO 这里要加入定时播放的任务
//                getAlarmTime( contentBean);
            } else {
                //不需要定时
                notAtTime(contentBean);
            }
        }
    }


    private void notAtTime(ContentBean contentBean) {

        //获取推送的类型  1资讯 2广告 3通知
        int publishType = contentBean.getPublishTypeId();
        //推送过来为通知类
        if (publishType == Constants.PUBLISH_TYPE_NOTICE) {
            if (contentBean.getSpots() == Constants.IS_SPOTS && loadAllValidNotice().size() > 0) {
                //开始插播通知
                startInterCutNotice(contentBean);
            } else
                isNotice(contentBean);
        }
        //为资讯类
        if (publishType == Constants.PUBLISH_TYPE_INFORMATION || publishType == Constants.PUBLISH_TYPE_ADVERT) {
            if (contentBean.getSpots() == Constants.IS_SPOTS && loadAllValidInformation().size() > 0) {
                //开始插播资讯
                if (!isCutting) {
                    startInterCutInfo(contentBean);
                } else {
                    ToastUtil.l("请等待当前插播内容播放完毕");
                    cutBean2 = contentBean;
                }
            } else if (contentBean.getResourcesUrl() != null && !contentBean.getResourcesUrl().isEmpty()) {
                isInformation(contentBean);
            }
        }
    }

    private Boolean hasBgm(ContentBean contentBean) {
        return contentBean.getTransformsound() != 1 && contentBean.getBgm() != null && !contentBean.getBgm().isEmpty();
    }

    private synchronized void startInterCutInfo(ContentBean contentBean) {
        ControlVolumeUtil.setVolume(this);
        isCutting = true;
        if (informationTask != null) {
            informationTask.cancel();
            informationTask = null;
        }
        ContentBean beforeBean = queryContentById(SharedPreferencesUtil.getInformationId());
        //插播的类型
        int cutType = contentBean.getImgormo();

        //判断之前在播放的是什么内容
        if (beforeBean != null) {
            switch (beforeBean.getImgormo()) {
                //原本播放的是图文
                case Constants.IS_IMAGE:
                    if (mSpeechSynthesizer != null)
                        mSpeechSynthesizer.pause();
                    if (hasBgm(beforeBean) && mediaPlayer != null) {
                        LogUtil.d("idceshi", "activity里的id：" + mediaPlayer.toString());
                        mediaPlayer.pause();
                    }
                    if (viewDelegate != null)
                        viewDelegate.setImgFrameVisibility(false);
                    break;
                //原本播放的是视频
                case Constants.IS_VIDEO:
                    if (cutType == Constants.IS_IMAGE && viewDelegate != null)
                        viewDelegate.setVideoFrameVisibility(false);
                    if (videoFragment != null) {
                        videoView = videoFragment.getVideoView();
                        LogUtil.w("idceshi", "main里原来的view的id" + videoView.getCurrentPosition() + videoView.toString());
                        if (videoView != null) {
                            videoView.pause();
                            position = videoView.getCurrentPosition();
                            LogUtil.w("seek", "暂停播放：" + position);
                        }
                    }
                    break;
            }
        }
        cutInfoTime = System.currentTimeMillis() - cutInfoTime;
        if (contentBean.getResourcesUrl() != null)
            contentBean.setResourcesDir(FileUtil.getFileName(contentBean));
        if (hasBgm(contentBean))
            contentBean.setBgmDir(FileUtil.getBgmFileName(contentBean));
        insertOrReplaceContent(contentBean);
        interCutInfoId = contentBean.getId();

        //判断插播的是什么内容
        switch (cutType) {
            //插播的是图文
            case Constants.IS_IMAGE:
                if (viewDelegate != null) {
                    viewDelegate.setTagContent(contentBean.getTagName());
                    viewDelegate.setCutImgVisibility(true);
                }
                setLogoAndTitle(true, contentBean.getHeadline());
                ImgWithTextFragment imgFragment = new ImgWithTextFragment();
                cutImgFragment = imgFragment;
                beginInterCutTransaction(true, imgFragment, contentBean);
                if (hasBgm(contentBean) && !FileUtil.isFileExists(contentBean.getBgmDir())) {
//                    DownLoadFileManager.getInstance().addDownLoadBgm(contentBean);
                }
                break;
            //插播的是视频
            case Constants.IS_VIDEO:
                if (viewDelegate != null)
                    viewDelegate.setTagContent(null);
                if (beforeBean != null && beforeBean.getImgormo() == Constants.IS_IMAGE && viewDelegate != null) {
                    setLogoAndTitle(false, contentBean.getHeadline());
                    cutVideoFragment = new VideoFragment();
                    viewDelegate.setVisibility(false);
                    beginInterCutTransaction(false, cutVideoFragment, contentBean);
                }
                if (beforeBean != null && beforeBean.getImgormo() == Constants.IS_VIDEO) {
                    cutVideoFragment = videoFragment;
                    cutVideoFragment.setMContentBean(contentBean);
                    cutVideoFragment.setIsSpots(true);
                    CustomerVideoView cutVideoView = cutVideoFragment.getCut_videoView();
                    LogUtil.w("idceshi", "要开始替换插播的id" + videoView.getCurrentPosition() + " " + cutVideoView.toString());
                    cutVideoFragment.initVideoView(true, cutVideoView, contentBean.getResourcesUrl());
                }
                break;
        }

        interCutInfoTask = new TimerTask() {
            @Override
            public void run() {
                showOrHideContent(cutType, beforeBean, contentBean);
                if (cutInfoTime != 0 && loadAllValidInformation().size() != 1) {
                    countDownInformation((beforeBean.getDuration() * 1000 - cutInfoTime + 1000) / 1000);
                }
                if (beforeBean.getImgormo() == Constants.IS_IMAGE && viewDelegate != null)
                    viewDelegate.setTagContent(beforeBean.getTagName());
                else if (viewDelegate != null) viewDelegate.setTagContent(null);
                //插播结束后置成-1
                interCutInfoId = -1;
                //如果插播完只有它，那就当正常数据处理
                if (loadAllValidInformation().size() == 1) {
                    SharedPreferencesUtil.saveInformationId(contentBean.getId());
                    nextInformation(false);
                }
            }
        };
        if (timer != null)
            timer.schedule(interCutInfoTask, contentBean.getDuration() * 1000);
        String[] imgUrl = contentBean.getResourcesDir().replaceAll(" ", "").split(",");
        if (!isFileAllExists(imgUrl)) {
            for (int j = 0; j < imgUrl.length; j++) {
//                DownLoadFileManager.getInstance().addDownloadTask(j, contentBean);
            }
        }
    }

    private void showOrHideContent(int cutType, ContentBean beforeBean, ContentBean cutBean) {
        //插播的是图片，完后要把插播的视图隐藏
        if (cutType == Constants.IS_IMAGE && viewDelegate != null) {
            viewDelegate.setCutImgVisibility(false);
            if (mSpeechSynthesizer != null)
                mSpeechSynthesizer.stop();
            if (hasBgm(cutBean)) {
                cutImgFragment.stopMediaPlayer();
            }
        }
        //插播的是视频，完后要把插播的视图隐藏
        if (cutType == Constants.IS_VIDEO) {
            if (cutVideoFragment != null) {
                CustomerVideoView cutVideo = cutVideoFragment.getVideoView();
                if (beforeBean != null && beforeBean.getImgormo() == Constants.IS_VIDEO) {
                    //一定要恢复标志位
                    cutVideoFragment.setIsSpots(false);
                }
                if (cutVideo != null) {
                    cutVideo.pause();
                    cutVideo.stopPlayback();
                    runOnUiThread(() -> cutVideo.setVisibility(View.GONE));

                }
            }
        }
        if (beforeBean != null && beforeBean.getImgormo() == Constants.IS_IMAGE && viewDelegate != null) {
            setLogoAndTitle(true, beforeBean.getHeadline());
            viewDelegate.setImgFrameVisibility(true);
            if (mSpeechSynthesizer != null) {
                if (beforeBean.getTransformsound() == 1) {
                    String text = beforeBean.getContent().replaceAll(" ", "").replaceAll("\r|\n", "");
                    String[] data = text.split("\\*");
                    for (String aData : data) {
                        mSpeechSynthesizer.speak(aData);
                    }
                }
            }
            if (hasBgm(beforeBean) && mediaPlayer != null) {
//                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.start();
            }
        }
        if (beforeBean != null && beforeBean.getImgormo() == Constants.IS_VIDEO && viewDelegate != null) {
            setLogoAndTitle(false, beforeBean.getHeadline());
            viewDelegate.setVideoFrameVisibility(true);
            if (videoView != null) {
                //如果插播的是视频，而且之前播的也是视频，要把之前被隐藏的view显示出来
                runOnUiThread(() -> videoView.setVisibility(View.VISIBLE));
                LogUtil.w("idceshi", "插播完后继续播放的id" + videoView.getCurrentPosition() + videoView.toString());
                if (position != 0) {
                    videoView.seekTo(position);
                    LogUtil.w("seek", "恢复播放：" + position);
                }
                videoView.start();
                position = 0;
            }
        }
        isCutting = false;
        EventBus.getDefault().post("CuttingFinish");
    }

    /**
     * 接受更新ui的事件
     *
     * @param
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void CuttingFinish(String status) {
        if (status.equals("CuttingFinish") && cutBean2 != null) {
            startInterCutInfo(cutBean2);
            cutBean2 = null;
        }
    }

    private void beginInterCutTransaction(Boolean isImg, Fragment fragment, ContentBean contentBean) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("contentBean", contentBean);
        bundle.putBoolean("Spots", true);
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        fragment.setArguments(bundle);
        if (!isStop) {
            if (isImg) {
                transaction.replace(R.id.img_interCut_frameLayout, fragment).commitAllowingStateLoss();
            } else {
                transaction.replace(R.id.videoFrameLayout, fragment).commitAllowingStateLoss();
            }
        }
    }


    private void startInterCutNotice(ContentBean contentBean) {
        //插播步骤：取消之前的定时任务，马上替换当前内容，完成之后再设置成之前的内容
        if (noticeTask != null)
            noticeTask.cancel();
        //
        cutNoticeTime = System.currentTimeMillis() - cutNoticeTime;
//        LogUtil.w("shijian", "插播过来距离上次的时间：" + cutNoticeTime);
        if (viewDelegate != null)
            viewDelegate.startMarquee(contentBean);
        insertOrReplaceContent(contentBean);
        interCutNoticeId = contentBean.getId();
        interCutNoticeTask = new TimerTask() {
            @Override
            public void run() {
                ContentBean beforeNoticeBean = queryContentById(SharedPreferencesUtil.getNoticeId());
                if (viewDelegate != null)
                    viewDelegate.startMarquee(beforeNoticeBean);
                if (cutNoticeTime != 0 && loadAllValidNotice().size() != 1) {
//                    LogUtil.w("shijian", "回复上次的时间：" + (contentBean.getDuration() * 1000 - cutNoticeTime + 1500) / 1000);
                    countDownNotice((beforeNoticeBean.getDuration() * 1000 - cutNoticeTime + 1500) / 1000);
                }
                //插播结束后置成-1
                interCutNoticeId = -1;
                //如果插播完只有它，那就当正常数据处理
                if (loadAllValidNotice().size() == 1) {
                    SharedPreferencesUtil.saveNoticeId(contentBean.getId());
                    nextNotice(false);
                }
            }
        };
        if (timer != null)
            timer.schedule(interCutNoticeTask, contentBean.getDuration() * 1000);
    }


    //为资讯类的操作
    private void isInformation(ContentBean contentBean) {
        LogUtil.d("load", loadAllValidInformation().size() + "");
        if (contentBean.getResourcesUrl() != null)
            contentBean.setResourcesDir(FileUtil.getFileName(contentBean));
        if (hasBgm(contentBean))
            contentBean.setBgmDir(FileUtil.getBgmFileName(contentBean));
        insertOrReplaceContent(contentBean);
        LogUtil.w("ceshi", "这个新数据插在了队伍的" + loadAllValidInformation().indexOf(contentBean) + "处");
        LogUtil.w("ceshi", loadAllValidInformation().size() + "  " + SharedPreferencesUtil.getInformationId());
        //如果本来没有存过资讯 或者 收到的资讯ID和正在播放的ID相同（替换操作），则马上更新界面
        LogUtil.d("load", loadAllValidInformation().size() + "");
        for (int i = 0; i < loadAllValidInformation().size(); i++) {
            LogUtil.d("load" + loadAllValidInformation().get(i).toString());
        }
        if (SharedPreferencesUtil.getInformationId() == contentBean.getId()) {
            if (informationTask != null) {
                informationTask.cancel();
                informationTask = null;
            }
            SharedPreferencesUtil.saveInformationId(contentBean.getId());
            nextInformation(true);

        } else if (loadAllValidInformation().size() == 1) {
            SharedPreferencesUtil.saveInformationId(contentBean.getId());
            nextInformation(true);
        }
        initDownLoad(contentBean);
    }

    private void initDownLoad(ContentBean contentBean) {
        String[] contentList = contentBean.getResourcesDir().replaceAll(" ", "").split(",");
        for (int i = 0; i < contentList.length; i++) {
            if (!isFileAllExists(contentList))
                DownLoadFileManager.getInstance().addDownloadTask(i, contentBean);
        }
        //下载背景音乐
        if (hasBgm(contentBean) && !FileUtil.isFileExists(contentBean.getBgmDir())) {
            DownLoadFileManager.getInstance().addDownLoadBgm(contentBean);
        }
    }


    private void isNotice(ContentBean contentBean) {
        //如果本来没有存过通知 或者 收到的通知ID和正在播放的ID相同（替换操作），则马上更新界面
        insertOrReplaceContent(contentBean);
//        LogUtil.w("ceshi", loadAllValidNotice().size() + "  " + SharedPreferencesUtil.getNoticeId());
        if (SharedPreferencesUtil.getNoticeId() == contentBean.getId()) {
            if (noticeTask != null) {
                noticeTask.cancel();
                noticeTask = null;
            }
            SharedPreferencesUtil.saveNoticeId(contentBean.getId());
            nextNotice(true);

        } else if (loadAllValidNotice().size() == 1) {
            SharedPreferencesUtil.saveNoticeId(contentBean.getId());
            nextNotice(true);
        }
    }


    //下一条资讯
    public synchronized void nextInformation(boolean noAddPosition) {
        if (interCutInfoTask != null) {
            interCutInfoTask.cancel();
            interCutInfoTask = null;
        }
        ControlVolumeUtil.setVolume(this);
        List<ContentBean> informationList = loadAllValidInformation();
        LogUtil.w("ceshi", "开始下一条：informationList.size()=" + informationList.size() + " " + noAddPosition);
        if (informationList.size() == 0) {
            setInfoNull(-1);
        } else {
            int nextPosition = noAddPosition ? SharedPreferencesUtil.getInfoPosition() :
                    (SharedPreferencesUtil.getInfoPosition() + 1) % informationList.size();
            if (nextPosition == -1)
                nextPosition = 0;
            LogUtil.w("ceshi", "应该播的资讯坐标：" + nextPosition);
            ContentBean contentBean = informationList.get(nextPosition);
            LogUtil.w("download", "看看之前的数据" + contentBean.getResourcesUrl());
            if (contentBean != null) {
                stopVoiceAndVideo();
                showInfoContent(contentBean);
//                String[] url = contentBean.getResourcesUrl().split(",");
//                boolean hasHttp = false;
//                boolean hasLocal = false;
//                List<Integer> httpIndex = new ArrayList<>();
//                List<Integer> localIndex = new ArrayList<>();
//                for (int i = 0; i < url.length; i++) {
//                    if (url[i].replaceAll(" ", "").substring(0, 4).equals("http")) {
//                        hasHttp = true;
//                        httpIndex.add(i);
//                        LogUtil.w("download", "有未下载的：第" + i + "个,连接：" + url[i]);
//                    } else {
//                        hasLocal = true;
//                        localIndex.add(i);
//                    }
//                }
//                //如果imgUrl还有http的 要判断一下网络情况，然后根据具体情况处理
//                if (hasHttp) {
//                    if (NetUtil.isConnectNoToast()) {
//                        //有网，有未下载的，去下载
//                        for (int i = 0; i < httpIndex.size(); i++) {
//                            LogUtil.w("download", "有网 ，准备去下载：" + url[httpIndex.get(i)]);
////                            DownLoadFileManager.getInstance().addDownloadTask(httpIndex.get(i), contentBean);
//                        }
//                        showInfoContent(contentBean);
//                    } else {
//                        if (hasLocal) {
//                            LogUtil.w("download", "断网后，有缓存过，看看现在的数据" + queryContentById(contentBean.getId()).getResourcesUrl());
//                            showInfoContent(contentBean);
//                        } else {
//                            //没网并且没有已缓存过的，直接下一个
//                            LogUtil.w("download", "无网 ，也没有缓存过的");
//                            SharedPreferencesUtil.saveInformationId(contentBean.getId());
//                            nextInformation(false);
//                        }
//                    }
//                } else {
//                    //如果都已缓存本地，则直接播放
//                    LogUtil.w("download", "都已缓存本地");
//                    showInfoContent(contentBean);
//                }
            } else if (loadAllValidInformation().size() != 0) {
                nextInformation(false);
            }
        }

    }

    private void showInfoContent(ContentBean contentBean) {
        countDownInformation(contentBean.getDuration());
        showInformation(contentBean);
        LogUtil.w("ceshi", "正在播放的ID：" + contentBean.getId() + contentBean.getHeadline() + ",,占队伍的" + loadAllValidInformation().indexOf(contentBean));
        SharedPreferencesUtil.saveInformationId(contentBean.getId());
        LogUtil.w("ceshi", "开始进入倒计时");
    }

    private void showInformation(ContentBean contentBean) {
        if (contentBean != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    info_tv.setText("总数" + loadAllValidInformation().size() + "Id:" + eqId + " 正在播的Id:" + contentBean.getId() + "音量：" + ControlVolumeUtil.getVoice() + contentBean.getHeadline());
                }
            });
            contentBean.setSpots(0);
            if (contentBean.getImgormo() == Constants.IS_IMAGE && viewDelegate != null)
                viewDelegate.setTagContent(contentBean.getTagName());
            else if (viewDelegate != null) viewDelegate.setTagContent(null);
            switch (contentBean.getImgormo()) {
                //是纯图或者图文内容
                case Constants.IS_IMAGE:
                    setLogoAndTitle(true, contentBean.getHeadline());
                    ImgWithTextFragment imgFragment = new ImgWithTextFragment();
                    beginTransaction(true, imgFragment, contentBean);
                    break;
                //是视频
                case Constants.IS_VIDEO:
                    //要隐藏标题
                    setLogoAndTitle(false, contentBean.getHeadline());
                    videoFragment = new VideoFragment();
                    beginTransaction(false, videoFragment, contentBean);
                    break;
            }
        }
    }

    public TimerTask getInformationTask() {
        return informationTask;
    }

    public void setTaskNull(Boolean cut) {
        if (cut)
            interCutInfoTask = null;
        else informationTask = null;
    }

    public TimerTask getInterCutInfoTask() {
        return interCutInfoTask;
    }

    private void setLogoAndTitle(Boolean isImg, String content) {
        if (viewDelegate != null) {
            viewDelegate.setTitleType(isImg);
            viewDelegate.setLogo(isImg);
            if (isImg) {
                viewDelegate.setTitle(content);
            } else viewDelegate.setTitle("");
        }
    }

    private void beginTransaction(Boolean isImg, Fragment fragment, ContentBean contentBean) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("contentBean", contentBean);
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        fragment.setArguments(bundle);
        if (viewDelegate != null)
            viewDelegate.setVisibility(isImg);
        //切换页面时要把之前的停止
        stopVoiceAndVideo();
        if (!isStop) {
            if (isImg) {
                transaction.replace(R.id.img_frameLayout, fragment).commitAllowingStateLoss();
            } else {
                transaction.replace(R.id.videoFrameLayout, fragment).commitAllowingStateLoss();
            }
        }
    }

    private void stopVoiceAndVideo() {
        try {
            if (videoFragment != null) {
                VideoView videoView = videoFragment.getVideoView();
                if (videoView != null && videoView.isPlaying()) {
                    videoView.pause();
                    videoView.stopPlayback();
                }
            }
            if (getSpeechSynthesizer() != null) {
                mSpeechSynthesizer.stop();
            }

            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            }
        } catch (IllegalStateException e) {
            mediaPlayer = null;
            mediaPlayer = new MediaPlayer();
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }


    private synchronized void countDownInformation(long duration) {
        LogUtil.w("ceshi", "资讯倒计时的时间：" + duration);
        informationTask = new TimerTask() {
            @Override
            public void run() {
                cutInfoTime = 0;
                nextInformation(false);
            }
        };
        if (timer != null) {
            if (duration >= 0)
                timer.schedule(informationTask, duration * 1000);
            if (duration < 0)
                timer.schedule(informationTask, 15000);
        }
        cutInfoTime = System.currentTimeMillis();
    }

    public SpeechSynthesizer getSpeechSynthesizer() {
        return mSpeechSynthesizer == null ? null : mSpeechSynthesizer;
    }

    public MediaPlayer getMediaPlayer() {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            }
        } catch (IllegalStateException e) {
            mediaPlayer = null;
            mediaPlayer = new MediaPlayer();
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        mediaPlayer = new MediaPlayer();
        return mediaPlayer;
    }

    private void toDestroy() {
        stopAllTask();
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
        if (mSpeechSynthesizer != null) {
            mSpeechSynthesizer.stop();
            mSpeechSynthesizer.release();//销毁百度语音单例对象
        }

        try {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            }
        } catch (IllegalStateException e) {

        }
    }

    public static MainActivity getInstance(){
        return activity;
    }
    @Override
    protected void onDestroy() {
        isStop = true;
        activity = null;
        toDestroy();
        //退出app停止下载
        DownLoadFileManager.getInstance().stopDownLoad(true);
        NetBroadCastReceiver.setNetChangeListener(null);
        //退出应用后解绑eventBus
        EventBus.getDefault().unregister(this);
        //关闭数据库
        DaoManager.getInstance().closeConnection();
        super.onDestroy();
    }

    private void stopAllTask() {
        if (dataTask != null) {
            dataTask.cancel();
            dataTask = null;
        }
        if (noticeTask != null) {
            noticeTask.cancel();
            noticeTask = null;
        }
        if (informationTask != null) {
            informationTask.cancel();
            informationTask = null;
        }
        if (interCutNoticeTask != null) {
            interCutNoticeTask.cancel();
            interCutNoticeTask = null;
        }
        if (interCutInfoTask != null) {
            interCutInfoTask.cancel();
            interCutInfoTask = null;
        }
        if (weatherTask != null) {
            weatherTask.cancel();
            weatherTask = null;
        }
        if (finishActivityTask != null) {
            finishActivityTask.cancel();
            finishActivityTask = null;
        }
        if (netTask != null) {
            netTask.cancel();
            netTask = null;
        }
    }

    private TimerTask netTask;

    @Override
    public void netChange(Boolean isConnect) {
        if (netTask != null) {
            netTask.cancel();
            netTask = null;
        }
        if (isConnect) {
            netTask = new TimerTask() {
                @Override
                public void run() {
                    stopVoiceAndVideo();
                    stopTask();
                    getPublishList(false, false);
                }
            };
            timer.schedule(netTask, 2000);

        }
    }
}
