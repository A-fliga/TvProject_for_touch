/*
 * Copyright (c) 2015, 张涛.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.app.tvproject.mvp.presenter.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;

import com.app.tvproject.R;
import com.app.tvproject.mvp.presenter.IPresenter;
import com.app.tvproject.mvp.view.ViewDelegate;
import com.app.tvproject.utils.LogUtil;

import java.io.Serializable;
import java.util.Stack;


/**
 * Presenter base class for Activity
 * Presenter层的实现基类
 *
 * @param <T> View delegate class type
 */
public abstract class ActivityPresenter<T extends ViewDelegate> extends AppCompatActivity implements IPresenter<T> {
    protected volatile T viewDelegate;
    private static Stack<Activity> activityStack;

    public ActivityPresenter() {
        viewDelegate = ViewDelegate.newInstance(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //在基类里设置全屏属性
        setTheme(R.style.Theme_AppCompat_Light_NoActionBar);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        super.onCreate(savedInstanceState);
        addActivity(this);
        viewDelegate.create(getLayoutInflater(), null, savedInstanceState);
        setContentView(viewDelegate.getRootView());
        viewDelegate.initWidget();
        bindEvenListener();
    }


    /**
     * 结束指定类名的Activity
     */
    public static void finishActivity(Class<?> cls) {
        activityStack.stream().filter(activity -> activity.getClass().equals(cls) && !activity.isFinishing()).forEach(Activity::finish);
    }

    /**
     * 获取栈顶Activity（堆栈中最后一个压入的）
     */
    public static Activity getTopActivity() {
        return activityStack.lastElement();
    }

    /**
     * 结束所有Activity
     */
    public synchronized static void finishAllActivity() {
        activityStack.stream().filter(activity -> activity != null && !activity.isFinishing()).forEach(Activity::finish);
    }


    /**
     * 添加Activity到堆栈
     */
    public void addActivity(Activity activity) {
        if (activityStack == null) {
            activityStack = new Stack<>();
        }
        activityStack.add(activity);
    }

    protected void bindEvenListener() {
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (viewDelegate == null) {
            try {
                viewDelegate = getDelegateClass().newInstance();
            } catch (InstantiationException e) {
                throw new RuntimeException("create IDelegate error");
            } catch (IllegalAccessException e) {
                throw new RuntimeException("create IDelegate error");
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (viewDelegate.getOptionsMenuId() != 0) {
            getMenuInflater().inflate(viewDelegate.getOptionsMenuId(), menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    public void startMyActivity(Class<?> pClass, Bundle pBundle) {
        Intent intent = new Intent(viewDelegate.getActivity(), pClass);
        if (pBundle != null)
            intent.putExtras(pBundle);
        viewDelegate.getActivity().startActivity(intent);
    }

    public void startMyActivity(Class<?> pClass, String key, Serializable pBundle) {
        Intent intent = new Intent(viewDelegate.getActivity(), pClass);
        if (pBundle != null && key != null)
            intent.putExtra(key, pBundle);
        viewDelegate.getActivity().startActivity(intent);
    }

    public void startMyActivityWithFinish(Class<?> pClass, Bundle pBundle) {
        Intent intent = new Intent(viewDelegate.getActivity(), pClass);
        if (pBundle != null)
            intent.putExtras(pBundle);
        viewDelegate.getActivity().startActivity(intent);
        viewDelegate.getActivity().finish();
    }

    public void startMyActivityForResult(Intent intent, int requestCode) {
        viewDelegate.getActivity().startActivityForResult(intent, requestCode);
    }

    public void startMyActivityForResult(Class<?> pClass, String action, int requestCode) {
        Intent intent;
        if (action != null)
            intent = new Intent(action);
        else
            intent = new Intent(viewDelegate.getActivity(), pClass);
        viewDelegate.getActivity().startActivityForResult(intent, requestCode);
    }

    public void startMyActivityForResult(Class<?> clazz, int requestCode, Bundle bundle) {
        try {
            Intent intent = new Intent(viewDelegate.getActivity(), clazz);
            if (null != bundle) {
                intent.putExtras(bundle);
            }
            viewDelegate.getActivity().startActivityForResult(intent, requestCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewDelegate = null;
    }

}
