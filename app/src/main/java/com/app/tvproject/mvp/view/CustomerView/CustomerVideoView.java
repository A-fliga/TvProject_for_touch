package com.app.tvproject.mvp.view.CustomerView;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

/**
 * Created by Administrator on 2017/10/17 0017.
 */

public class CustomerVideoView extends VideoView {
    public CustomerVideoView(Context context) {
        super(context);
    }

    public CustomerVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomerVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 全屏处理。
        int width = getDefaultSize(0, widthMeasureSpec);
        int height = getDefaultSize(0, heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

}
