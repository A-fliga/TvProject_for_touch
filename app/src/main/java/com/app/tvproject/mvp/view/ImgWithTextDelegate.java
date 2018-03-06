package com.app.tvproject.mvp.view;

import com.app.tvproject.R;
import com.app.tvproject.mvp.adapter.InfoBannerAdapter;
import com.bigkoo.convenientbanner.ConvenientBanner;

import java.util.List;

/**
 * Created by www on 2017/11/22.
 */

public class ImgWithTextDelegate extends ViewDelegate {
    private ConvenientBanner convenientBanner;

    @Override
    public void onDestroy() {
    }

    @Override
    public int getRootLayoutId() {
        return R.layout.fragment_img_with_text;
    }

    @Override
    public void initWidget() {
        super.initWidget();
        convenientBanner = get(R.id.img_banner);
    }

    public void showImgBanner(List<String> imgUrl) {
        convenientBanner.setScrollDuration(5000);
        convenientBanner.getViewPager().setCanScroll(false);
        if (imgUrl.size() == 1) {
            convenientBanner.setCanLoop(false);
        }
        if (imgUrl.size() > 1) {
            convenientBanner.setCanLoop(true);
            convenientBanner.setManualPageable(true);
        }
        convenientBanner.setPages(InfoBannerAdapter::new, imgUrl);
        convenientBanner.startTurning(8000);
    }

    public ConvenientBanner getConvenientBanner() {
        return convenientBanner;
    }
}
