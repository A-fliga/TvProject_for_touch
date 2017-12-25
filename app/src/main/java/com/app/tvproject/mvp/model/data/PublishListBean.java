package com.app.tvproject.mvp.model.data;

import java.util.List;

/**
 * Created by www on 2017/11/16.
 */

public class PublishListBean {

    public ResultBean result;
    public String msg;
    public int code;

    public static class ResultBean {
        /**
         * 要按优先级顺序播
         */
        public List<com.app.tvproject.mvp.model.data.ContentBean> platformPublishDetailList; // 平台数组,audiencebelongto 3
        public List<com.app.tvproject.mvp.model.data.ContentBean> communityPublishDetailList;//村社数组 audiencebelongto 2
        public List<com.app.tvproject.mvp.model.data.ContentBean> propertyPublishDetailList; // 物业  audiencebelongto 1
        public List<com.app.tvproject.mvp.model.data.ContentBean> quipmentPublishDetailList ;// 设备 audiencebelongto  0,
    }
}
