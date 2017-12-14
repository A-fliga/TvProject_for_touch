package com.app.tvproject.mvp.presenter.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.app.tvproject.constants.Constants;
import com.app.tvproject.mvp.model.data.ContentBean;
import com.app.tvproject.mvp.presenter.activity.MainActivity;
import com.app.tvproject.mvp.view.ImgWithTextDelegate;
import com.app.tvproject.utils.BaiduVoiceUtil;
import com.app.tvproject.utils.LogUtil;
import com.app.tvproject.utils.NetUtil;
import com.baidu.tts.client.SpeechSynthesizer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by www on 2017/11/22.
 */

public class ImgWithTextFragment extends FragmentPresenter<ImgWithTextDelegate> {
    private ContentBean contentBean;
    private SpeechSynthesizer speechSynthesizer;

    @Override
    public Class<ImgWithTextDelegate> getDelegateClass() {
        return ImgWithTextDelegate.class;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            contentBean = bundle.getParcelable("contentBean");
        }
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MainActivity activity = (MainActivity) getActivity();
        //要转语音
        if (contentBean.getTransformsound() == 1) {
            if (contentBean.getSpots() == Constants.IS_SPOTS)
                speechSynthesizer = BaiduVoiceUtil.initTTs();
            else speechSynthesizer = activity.getSpeechSynthesizer();
            String text = contentBean.getContent().replaceAll(" ", "").replaceAll("\r|\n", "");
            Log.d("ceshi", "完整:" + text);
            String[] data = text.split("\\*");
            for (String aData : data) {
                Log.d("ceshi", "拆分:" + aData);
                speechSynthesizer.speak(aData);
            }
        }
        setBannerImgLoader(contentBean);
    }

    private void setBannerImgLoader(ContentBean contentBean) {
        List<String> imgUrlList = new ArrayList<>();
        String[] imgUrl = contentBean.getImageurl().replaceAll(" ", "").split(",");
        for (String anImgUrl : imgUrl) {
            if(!NetUtil.isConnectNoToast()) {
                if (!(anImgUrl.replaceAll(" ", "").substring(0, 4).equals("http")) && !anImgUrl.isEmpty()) {
                    imgUrlList.add(anImgUrl);
                }
            }
            else {
                if(!anImgUrl.isEmpty()){
                    imgUrlList.add(anImgUrl);
                }
            }
        }
        viewDelegate.showImgBanner(imgUrlList);
    }

    @Override
    protected void onFragmentVisible() {

    }

    @Override
    protected void onFragmentHidden() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.w("xiaohui", "图文fragment被销毁");
        if (speechSynthesizer != null) {
            speechSynthesizer.stop();
        }
    }
}
