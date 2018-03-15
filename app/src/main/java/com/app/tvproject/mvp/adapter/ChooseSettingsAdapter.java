package com.app.tvproject.mvp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.tvproject.R;
import com.app.tvproject.mvp.model.data.ChooseSettingsBean;
import com.app.tvproject.utils.AppUtil;
import com.app.tvproject.utils.ToastUtil;

import java.util.List;

/**
 * Created by www on 2017/11/16.
 * 选择省市配置信息的adapter
 */

public class ChooseSettingsAdapter extends RecyclerView.Adapter<ChooseSettingsAdapter.ChooseSettingViewHolder> {
    private Context context;
    private List<ChooseSettingsBean.ResultBean> beanList;
    private OnItemClickListener listener;

    public ChooseSettingsAdapter(Context context, List<ChooseSettingsBean.ResultBean> beanList) {
        this.context = context;
        this.beanList = beanList;
        if (beanList.size() == 0)
            ToastUtil.s("暂无数据");
    }

    @Override
    public ChooseSettingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ChooseSettingViewHolder(LayoutInflater.from(context).inflate(R.layout.item_choose_settings, parent, false));
    }

    @Override
    public void onBindViewHolder(ChooseSettingViewHolder holder, int position) {
        ChooseSettingsBean.ResultBean settingBean = beanList.get(position);
        holder.chooseItemTv.setText((settingBean.names == null || settingBean.names.isEmpty()) ?
                settingBean.equipmentnumber + "  " + settingBean.address : settingBean.names);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (settingBean.equipmentnumber != null) {
                    if (!AppUtil.isFastDoubleClick(3000)) {
                        listener.onItemClick(settingBean);
                    }
                } else
                    listener.onItemClick(settingBean);
            }
        });
    }

    @Override
    public int getItemCount() {
        return beanList.size() == 0 ? 0 : beanList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(ChooseSettingsBean.ResultBean resultBean);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    class ChooseSettingViewHolder extends RecyclerView.ViewHolder {
        public TextView chooseItemTv;

        public ChooseSettingViewHolder(View itemView) {
            super(itemView);
            chooseItemTv = (TextView) itemView.findViewById(R.id.setting_content);
        }
    }
}
