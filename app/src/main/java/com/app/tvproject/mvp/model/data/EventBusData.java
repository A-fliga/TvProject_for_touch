package com.app.tvproject.mvp.model.data;

/**
 * Created by Administrator on 2017/10/9 0009.
 */

public class EventBusData {

    private long content_id;

    public long getContent_id() {
        return content_id;
    }

    public void setContent_id(long content_id) {
        this.content_id = content_id;
    }



    private int voice;
    private String dormantStartTime;
    private int 资讯;
    private int 广告;
    private String address;
    private int isDormant;
    private String dormantStopTime;
    private String action;
    private int 通知;
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

    public int get资讯() {
        return 资讯;
    }

    public void set资讯(int 资讯) {
        this.资讯 = 资讯;
    }

    public int get广告() {
        return 广告;
    }

    public void set广告(int 广告) {
        this.广告 = 广告;
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

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public int get通知() {
        return 通知;
    }

    public void set通知(int 通知) {
        this.通知 = 通知;
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
