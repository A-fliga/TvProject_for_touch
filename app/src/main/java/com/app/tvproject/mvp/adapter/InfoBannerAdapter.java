package com.app.tvproject.mvp.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.bigkoo.convenientbanner.holder.Holder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

/**
 * Created by www on 2017/11/23.
 */

public class InfoBannerAdapter implements Holder<String> {
    private ImageView bannerImg;

    @Override
    public View createView(Context context) {
        bannerImg = new ImageView(context);
        bannerImg.setAdjustViewBounds(true);
        return bannerImg;
    }

    @Override
    public void UpdateUI(Context context, int position, String data) {
        Glide.with(context).load(data)
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(bannerImg);
    }
}
