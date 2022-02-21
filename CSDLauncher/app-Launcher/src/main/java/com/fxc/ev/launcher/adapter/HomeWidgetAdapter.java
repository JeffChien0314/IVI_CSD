package com.fxc.ev.launcher.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.fxc.ev.launcher.R;

import java.util.List;


public class HomeWidgetAdapter extends BaseAdapter {
    private List<View> mWidgetList;
    private LayoutInflater layoutInflater;
    private Context mContext;

    public HomeWidgetAdapter(Context context, List<View> widgetList) {
        mContext = context;
        mWidgetList = widgetList;
    }

    @Override
    public int getCount() {
        return mWidgetList.size();
    }

    @Override
    public Object getItem(int position) {
        return mWidgetList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            // 给单个的View通过inflater填充个layout文件
            convertView = LayoutInflater.from(mContext).inflate(R.layout.widget_item, parent, false);
        }
        FrameLayout itemView = (FrameLayout) convertView.findViewById(R.id.item_view);
        View widgetItem = mWidgetList.get(position);
        ViewGroup viewGroup = (ViewGroup) widgetItem.getParent();
        if (viewGroup != null) {
            viewGroup.removeAllViews();
        }
        itemView.addView(mWidgetList.get(position));
        return convertView;
    }

}
