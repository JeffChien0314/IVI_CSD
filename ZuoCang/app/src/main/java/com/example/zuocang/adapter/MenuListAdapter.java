package com.example.zuocang.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.example.zuocang.R;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;

/**
 * Created by Jennifer on 2022/4/18.
 */

public class MenuListAdapter extends BaseAdapter {
    private Context context;
    String [] itemName;
    ArrayList<Integer> itemImage;

    public MenuListAdapter(Context context, ArrayList<Integer> Images, String[] ItemName) {
        this.context = context;
         itemImage = Images;
         itemName = ItemName;

    }

    @Override
    public int getCount() {
        return itemImage.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
       MenuListAdapter.ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.menu_list, null);
            viewHolder.itemName = (TextView) convertView.findViewById(R.id.description);
            viewHolder.itemImage = (ImageView) convertView.findViewById(R.id.menu_icon);
            viewHolder.mView = (View) convertView.findViewById(R.id.menu_divider);
            viewHolder.mMenuItem = (LinearLayout) convertView.findViewById(R.id.menu_item);
            convertView.setTag(viewHolder);            //表示給View新增一個格外的資料，
        } else {
            viewHolder = (MenuListAdapter.ViewHolder) convertView.getTag();//通過getTag的方法將資料取出來
        }
        viewHolder.itemImage.setImageResource(itemImage.get(position));
        if(position==0){
            viewHolder.itemName.setText("Doors"+"\n"+"Windows");
        }else {
            viewHolder.itemName.setText(itemName[position]);
        }
        if(position == itemImage.size()-1){
            viewHolder.mView.setVisibility(GONE);

        }
        return convertView;
    }

    /**
     * 定義一個內部類
     * 宣告相應的控制元件引用
     */
    public class ViewHolder {

        public ImageView itemImage;
        public TextView itemName;
        public View mView;
        public LinearLayout mMenuItem;
    }
}
