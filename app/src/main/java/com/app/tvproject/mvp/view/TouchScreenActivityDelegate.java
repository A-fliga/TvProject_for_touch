package com.app.tvproject.mvp.view;

import android.view.View;

import com.app.tvproject.R;

/**
 * Created by www on 2018/2/9.
 */

public class TouchScreenActivityDelegate extends ViewDelegate {
    @Override
    public void onDestroy() {

    }

    @Override
    public int getRootLayoutId() {
        return R.layout.activity_touch_screen;
    }

    public void hideMainLL(Boolean hide) {
        getActivity().runOnUiThread(() -> {
            if (hide)
                get(R.id.touch_ll).setVisibility(View.GONE);
            else get(R.id.touch_ll).setVisibility(View.VISIBLE);
        });

    }
}
