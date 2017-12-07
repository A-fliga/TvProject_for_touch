package com.app.tvproject.mvp.model.data;

import java.util.List;

/**
 * Created by Administrator on 2017/9/18 0018.
 */

public class AliasBean {


    /**
     * result : {"list":[{"voice":50,"dormantStartTime":"00:00:00","address":"龙华社区位于成都绕城高速路与成龙路交汇处","isDormant":0,"dormantStopTime":"00:00:00","id":38,"equipmentNumber":"5RP2E-EPH3K-BR3LG-KMGTE-FN8PY"},{"voice":22,"dormantStartTime":"10:02:08","address":"新张集乡西北部","isDormant":1,"dormantStopTime":"10:02:08","id":58,"equipmentNumber":"5ZQ2A-NI239-4F4K7-H9N8Q-VTSYT"}]}
     * msg : 操作成功！
     * code : 0
     */

    private ResultBean result;
    private String msg;
    private int code;

    public ResultBean getResult() {
        return  result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public static class ResultBean {
        private List<ListBean> list;

        public List<ListBean> getList() {
            return list;
        }

        public void setList(List<ListBean> list) {
            this.list = list;
        }

        public static class ListBean {
            /**
             * voice : 50
             * dormantStartTime : 00:00:00
             * address : 龙华社区位于成都绕城高速路与成龙路交汇处
             * isDormant : 0
             * dormantStopTime : 00:00:00
             * id : 38
             * equipmentNumber : 5RP2E-EPH3K-BR3LG-KMGTE-FN8PY
             */

            private int voice;
            private String dormantStartTime;
            private String address;
            private int isDormant;
            private String dormantStopTime;
            private int id;
            private String equipmentNumber;

            public int getVoice() {
                return voice;
            }

            public void setVoice(int voice) {
                this.voice = voice;
            }

            public String getDormantStartTime() {
                return dormantStartTime;
            }

            public void setDormantStartTime(String dormantStartTime) {
                this.dormantStartTime = dormantStartTime;
            }

            public String getAddress() {
                return address;
            }

            public void setAddress(String address) {
                this.address = address;
            }

            public int getIsDormant() {
                return isDormant;
            }

            public void setIsDormant(int isDormant) {
                this.isDormant = isDormant;
            }

            public String getDormantStopTime() {
                return dormantStopTime;
            }

            public void setDormantStopTime(String dormantStopTime) {
                this.dormantStopTime = dormantStopTime;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getEquipmentNumber() {
                return equipmentNumber;
            }

            public void setEquipmentNumber(String equipmentNumber) {
                this.equipmentNumber = equipmentNumber;
            }
        }
    }
}
