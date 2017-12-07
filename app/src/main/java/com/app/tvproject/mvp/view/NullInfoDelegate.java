package com.app.tvproject.mvp.view;

import com.app.tvproject.R;

/**
 * Created by www on 2017/11/23.
 * 空白fragment视图
 */

public class NullInfoDelegate extends ViewDelegate{
    @Override
    public void onDestroy() {

    }

    @Override
    public int getRootLayoutId() {
        return R.layout.fragment_null_info;
    }
}
