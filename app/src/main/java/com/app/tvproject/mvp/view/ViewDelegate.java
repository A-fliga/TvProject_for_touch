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
package com.app.tvproject.mvp.view;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.app.tvproject.mvp.presenter.IPresenter;


/**
 * View delegate base class
 * 视图层代理的基类
 *
 * @author kymjs (http://www.kymjs.com/) on 10/23/15.
 */
public abstract class ViewDelegate implements IDelegate {
    protected final SparseArray<View> mViews = new SparseArray<View>();

    protected View rootView;

    public abstract int getRootLayoutId();

    @Override
    public void create(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        int rootLayoutId = getRootLayoutId();
        rootView = inflater.inflate(rootLayoutId, container, false);
    }
    public static <T extends ViewDelegate> T newInstance(IPresenter presenter) {
        T viewDelegate;
        try {
            viewDelegate = (T) presenter.getDelegateClass().newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException("create ViewDelegate error");
        } catch (IllegalAccessException e) {
            throw new RuntimeException("create ViewDelegate error");
        }
        return viewDelegate;
    }


    @Override
    public int getOptionsMenuId() {
        return 0;
    }


    @Override
    public View getRootView() {
        return rootView;
    }

    public void setRootView(View rootView) {
        this.rootView = rootView;
    }

    @Override
    public void initWidget() {
    }

    public <T extends View> T bindView(int id) {
        T view = (T) mViews.get(id);
        if (view == null) {
            view = (T) rootView.findViewById(id);
            mViews.put(id, view);
        }
        return view;
    }

    public void createMenu(Menu menu, MenuInflater inflater) {
        if (getOptionsMenuId() != 0) {
            inflater.inflate(getOptionsMenuId(), menu);
        }
    }

    public <T extends View> T get(int id) {
        return (T) bindView(id);
    }

    public void setOnClickListener(View.OnClickListener listener, int... ids) {
        if (ids == null) {
            return;
        }
        for (int id : ids) {
            get(id).setOnClickListener(listener);
        }
    }

    public void toast(CharSequence msg) {
        Toast.makeText(rootView.getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    public <T extends Activity> T getActivity() {
        return (T) rootView.getContext();
    }


    public void setRecycler(final RecyclerView recycler, final RecyclerView.Adapter adapter, final boolean scroll) {
        LinearLayoutManager linearLayoutManager;
        if (scroll) {
            linearLayoutManager = new LinearLayoutManager(getActivity());
        } else {
            linearLayoutManager = new LinearLayoutManager(getActivity()) {
                @Override
                public boolean canScrollVertically() {
                    return false;
                }
            };
        }
        linearLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        recycler.setLayoutManager(linearLayoutManager);
        recycler.setHasFixedSize(true);
        recycler.setItemAnimator(new DefaultItemAnimator());
        recycler.setAdapter(adapter);
    }

    public void setRecycler(final RecyclerView recycler, final RecyclerView.Adapter adapter, final int count, final boolean scroll) {
        GridLayoutManager gridLayoutManager;
        if (scroll) {
            gridLayoutManager = new GridLayoutManager(getActivity(), count);
        } else {
            gridLayoutManager = new GridLayoutManager(getActivity(), count) {
                @Override
                public boolean canScrollVertically() {
                    return false;
                }
            };
        }
        gridLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        recycler.setLayoutManager(gridLayoutManager);
        recycler.setHasFixedSize(true);
        recycler.setItemAnimator(new DefaultItemAnimator());
        recycler.setAdapter(adapter);
    }
}
