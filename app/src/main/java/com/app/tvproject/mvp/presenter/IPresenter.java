package com.app.tvproject.mvp.presenter;


import com.app.tvproject.mvp.view.IDelegate;

/**
 * Created by www on 2017/5/5.
 */
public interface IPresenter<T extends IDelegate> {

    Class<T> getDelegateClass();
}