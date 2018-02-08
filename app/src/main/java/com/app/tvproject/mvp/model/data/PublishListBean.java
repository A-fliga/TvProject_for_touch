package com.app.tvproject.mvp.model.data;

import java.util.List;

/**
 * Created by www on 2017/11/16.
 */

public class PublishListBean {
    /**
     * result : [{"transformsound":0,"imgormo":2,"endtime":1517414400000,"bgm":"","starttime":1511884800000,"sort":1511884800000,"tagName":"yui","audiencebelongto":2,"resourcesUrl":"resource/wztresource/monitorsPublish/1516872700414.mp4","content":"1","duration":1,"playCount":11,"spots":1,"playTime":"18:51:34","audiencebelongtoId":13,"id":29,"headline":"123322222111"}]
     * msg : 操作成功！
     * code : 0
     */

    public String msg;
    public int code;
    public List<ContentBean> result;
    // 平台数组,audiencebelongto 3
    //村社数组 audiencebelongto 2
    // 物业  audiencebelongto 1
    // 设备 audiencebelongto  0


//    public ResultBean result;
//    public String msg;
//    public int code;
//
//    public static class ResultBean {
//        /**
//         * 要按优先级顺序播
//         */
//        public List<com.app.tvproject.mvp.model.data.ContentBean> platformPublishDetailList; // 平台数组,audiencebelongto 3
//        public List<com.app.tvproject.mvp.model.data.ContentBean> communityPublishDetailList;//村社数组 audiencebelongto 2
//        public List<com.app.tvproject.mvp.model.data.ContentBean> propertyPublishDetailList; // 物业  audiencebelongto 1
//        public List<com.app.tvproject.mvp.model.data.ContentBean> quipmentPublishDetailList ;// 设备 audiencebelongto  0,
//    }
}
