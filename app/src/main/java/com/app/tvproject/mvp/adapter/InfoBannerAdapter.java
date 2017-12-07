package com.app.tvproject.mvp.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.bigkoo.convenientbanner.holder.Holder;
import com.bumptech.glide.Glide;

/**
 * Created by www on 2017/11/23.
 */

public class InfoBannerAdapter implements Holder<String> {
    private ImageView bannerImg;

    @Override
    public View createView(Context context) {
        bannerImg = new ImageView(context);
        bannerImg.setAdjustViewBounds(true);
        bannerImg.setScaleType(ImageView.ScaleType.FIT_CENTER);
        return bannerImg;
    }

    @Override
    public void UpdateUI(Context context, int position, String data) {
        Glide.with(context).load(data).into(bannerImg);
    }

}
