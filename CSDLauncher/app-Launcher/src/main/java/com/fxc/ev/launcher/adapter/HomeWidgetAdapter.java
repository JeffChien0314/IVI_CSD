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

import java.util.ArrayList;
import java.util.List;


public class HomeWidgetAdapter extends BaseAdapter {
    private List<View> mWidgetList = new ArrayList<>();
    private LayoutInflater layoutInflater;
    private Context mContext;
    private View convertView0;
    private int lastPosition = -1;

    public HomeWidgetAdapter(Context context/*, List<View> widgetList*/) {
        mContext = context;
        //mWidgetList = widgetList;
    }

    public void setData(List<View> widgetList) {
        mWidgetList.clear();
        mWidgetList.addAll(widgetList);
		convertView0=null;
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
        //getView position=0执行多次
        /*if (getCount() > 1) {
            if (lastPosition == 0 && position == 0 && convertView != null) {
                return convertView;
            }
        }
        lastPosition = position;*/

        if (convertView == null) {
            // 给单个的View通过inflater填充个layout文件
            convertView = LayoutInflater.from(mContext).inflate(R.layout.widget_item, parent, false);
        }

        if (position == 0 && convertView0 == null) {
            convertView0 = convertView;
        } else if (position == 0 && convertView0 != null) {
            return convertView0;
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
