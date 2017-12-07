package com.app.tvproject.mvp.presenter.fragment;

import com.app.tvproject.mvp.view.NullInfoDelegate;

/**
 * Created by www on 2017/11/23.
 */

public class NullInfoFragment extends FragmentPresenter<NullInfoDelegate> {
    @Override
    public Class<NullInfoDelegate> getDelegateClass() {
        return NullInfoDelegate.class;
    }

    @Override
    protected void onFragmentVisible() {

    }

    @Override
    protected void onFragmentHidden() {

    }
}
