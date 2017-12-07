package com.app.tvproject.mvp.presenter.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.tvproject.mvp.presenter.IPresenter;
import com.app.tvproject.mvp.view.ViewDelegate;

/**
 * Presenter base class for Fragment
 * Presenter层的实现基类
 *
 * @param <T> View delegate class type
 * @author kymjs (http://www.kymjs.com/) on 10/23/15.
 */
public abstract class FragmentPresenter<T extends ViewDelegate> extends Fragment implements IPresenter<T> {

    protected boolean viewCreated = false;
    protected T viewDelegate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        viewDelegate = ViewDelegate.newInstance(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = viewDelegate.getRootView();
        if (rootView != null) {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null) {
                parent.removeView(rootView);
            }
            return rootView;
        }
        viewDelegate.create(inflater, container, savedInstanceState);
        return viewDelegate.getRootView();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewCreated = true;
        viewDelegate.initWidget();
        bindEvenListener();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewCreated = false;
    }

    protected void bindEvenListener() {
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        viewDelegate.createMenu(menu, inflater);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (viewDelegate == null) {
            viewDelegate = ViewDelegate.newInstance(this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        viewDelegate.onDestroy();
        viewDelegate = null;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            onFragmentVisible();
//            FileUtil.writeLog(getClass().getName() + "->visibleToUser");
        } else {
            onFragmentHidden();
//            FileUtil.writeLog(getClass().getName() + "->invisibleToUser");
        }
    }

    protected abstract void onFragmentVisible();

    protected abstract void onFragmentHidden();

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