package com.app.tvproject.mvp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.tvproject.R;
import com.app.tvproject.mvp.model.data.ContentBean;

import java.util.List;

/**
 * Created by www on 2018/3/2.
 */

public class InfoListAdapter extends RecyclerView.Adapter<InfoListAdapter.InfoListViewHolder> {
    private Boolean isInfo;
    private Context context;
    private List<ContentBean> beanList;

    public InfoListAdapter(Context context, List<ContentBean> beanList, Boolean isInfo) {
        this.context = context;
        this.beanList = beanList;
        this.isInfo = isInfo;
    }

    @Override
    public InfoListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new InfoListViewHolder(LayoutInflater.from(context).inflate(R.layout.item_recycler, parent, false));
    }

    @Override
    public void onBindViewHolder(InfoListViewHolder holder, int position) {
        ContentBean bean = beanList.get(position);
        if (isInfo)
            holder.tv.setText("id:" + bean.getId() + "标题：" + bean.getHeadline().replaceAll(" ", ""));
        else holder.tv.setText("id:" + bean.getId() + bean.getContent().replaceAll(" ", ""));
    }

    @Override
    public int getItemCount() {
        return beanList.size();
    }

    public class InfoListViewHolder extends RecyclerView.ViewHolder {
        public TextView tv;

        public InfoListViewHolder(View itemView) {
            super(itemView);
            tv = (TextView) itemView.findViewById(R.id.item_tv);
        }
    }
}
