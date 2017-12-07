package com.app.tvproject.mvp.model.data;

/**
 * Created by Administrator on 2017/10/14 0014.
 */

public class BaseEntity<E> {

    private int code;
    private String msg;
    private E result;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public E getResult() {
        return result;
    }

    public void setResult(E result) {
        this.result = result;
    }


}
