package com.app.tvproject.mvp.view;

import com.app.tvproject.R;
import com.app.tvproject.mvp.adapter.ChooseSettingsAdapter;

/**
 * Created by www on 2017/11/16.
 * 选择省市配置信息的视图代理
 */

public class ChooseSettingsActivityDelegate extends ViewDelegate {

    @Override
    public void onDestroy() {

    }

    @Override
    public int getRootLayoutId() {
        return R.layout.activity_choose_area;
    }

    public void initSettingsView(ChooseSettingsAdapter adapter){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setRecycler(get(R.id.setting_recycler),adapter,true);
            }
        });

    }
}
