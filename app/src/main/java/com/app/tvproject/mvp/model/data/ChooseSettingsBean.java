package com.app.tvproject.mvp.model.data;

import java.util.List;

/**
 * Created by www on 2017/11/16
 * 选择省市区配置信息的bean
 */

public class ChooseSettingsBean {

    public ResultBean result;
    public String msg;
    public int code;

    public static class ResultBean {
        public List<ListBean> list;

        public static class ListBean {
            public int voice;
            public String dormantStartTime;
            public String 资讯;
            public int 广告;
            public String address;
            public int isDormant;
            public String dormantStopTime;
            public int 通知;
            public long id;
            public String equipmentNumber;
            public String name;
        }
    }
}
