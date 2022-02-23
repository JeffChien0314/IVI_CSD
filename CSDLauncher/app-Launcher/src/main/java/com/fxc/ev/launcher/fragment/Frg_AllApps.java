package com.fxc.ev.launcher.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fxc.ev.launcher.R;
import com.fxc.ev.launcher.activities.GetAllApps;
import com.fxc.ev.launcher.bean.PakageMod;
import com.fxc.ev.launcher.utils.view.DraggableGridViewPager;

import java.util.List;

import androidx.fragment.app.Fragment;

public class Frg_AllApps extends Fragment {
    public static final String TAG = Frg_AllApps.class.getSimpleName();

    private Context mContext;

    private View mRootView;
    private ViewGroup points;//小圆点指示器
    private ImageView[] ivPoints;//小圆点图片集合
    private List<PakageMod> listDatas;//总的数据源

    private int totalPage;//总的页数
    private int mPageSize = 9;//每页显示的最大数量

    private DraggableGridViewPager mDraggableGridViewPager;
    private ArrayAdapter<PakageMod> mAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity().getApplicationContext();

        mRootView = inflater.inflate(R.layout.all_apps_layout, container, false);
        mDraggableGridViewPager = (DraggableGridViewPager) mRootView.findViewById(R.id.draggable_grid_view_pager);
        //初始化行、列数及gap between grids
        mDraggableGridViewPager.setColCount(3);
        mDraggableGridViewPager.setRowCount(3);
        mDraggableGridViewPager.setGridGap(30);

        //初始化小圆点指示器
        points = (ViewGroup) mRootView.findViewById(R.id.points);

        mAdapter = new ArrayAdapter<PakageMod>(mContext, 0) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                final PakageMod pakageMod = getItem(position);
                if (convertView == null) {
                    convertView = getLayoutInflater().inflate(R.layout.app_item, null);
                }
                ((ImageView) convertView.findViewById(R.id.apps_image)).setImageDrawable(pakageMod.icon);
                ((TextView) convertView.findViewById(R.id.apps_textview)).setText(pakageMod.name);
                return convertView;
            }

        };
        //获取数据源
        listDatas = new GetAllApps(mContext).getDatas();
        //总的页数，取整（这里有三种类型：Math.ceil(3.5)=4:向上取整，只要有小数都+1  Math.floor(3.5)=3:向下取整  Math.round(3.5)=4:四舍五入）
        totalPage = (int) Math.ceil(listDatas.size() * 1.0 / mPageSize);

        for (PakageMod p : listDatas) {
            mAdapter.add(p);
        }
        mDraggableGridViewPager.setAdapter(mAdapter);
        //小圆点指示器
        ivPoints = new ImageView[totalPage];
        for (int i = 0; i < ivPoints.length; i++) {
            ImageView imageView = new ImageView(mContext);
            //设置图片的宽高
            imageView.setLayoutParams(new ViewGroup.LayoutParams(15, 15));
            if (i == 0) {
                imageView.setBackgroundResource(R.drawable.ellipse_84);
            } else {
                imageView.setBackgroundResource(R.drawable.ellipse_85);
            }
            ivPoints[i] = imageView;
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            layoutParams.leftMargin = 18;//设置点点点view的左边距
            layoutParams.rightMargin = 18;//设置点点点view的右边距
            points.addView(imageView, layoutParams);
        }
        mDraggableGridViewPager.setOnPageChangeListener(new DraggableGridViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.d(TAG, "onPageScrolled position=" + position + ", positionOffset=" + positionOffset
                        + ", positionOffsetPixels=" + positionOffsetPixels);

            }

            @Override
            public void onPageSelected(int position) {
                Log.d(TAG, "onPageSelected position=" + position);
                //改变小圆圈指示器的切换效果
                setImageBackground(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Log.d(TAG, "onPageScrollStateChanged state=" + state);
            }
        });

        mDraggableGridViewPager.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PakageMod clickItem = mAdapter.getItem(position);
                Intent intent = new Intent();
                intent = mContext.getPackageManager()
                        .getLaunchIntentForPackage(clickItem.pakageName);
                mContext.startActivity(intent);
            }
        });

        mDraggableGridViewPager.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                return true;
            }
        });

        mDraggableGridViewPager.setOnRearrangeListener(new DraggableGridViewPager.OnRearrangeListener() {
            @Override
            public void onRearrange(int oldIndex, int newIndex) {
                Log.d(TAG, "OnRearrangeListener.onRearrange from=" + oldIndex + ", to=" + newIndex);
                PakageMod item = mAdapter.getItem(oldIndex);
                mAdapter.setNotifyOnChange(false);
                mAdapter.remove(item);
                mAdapter.insert(item, newIndex);
                mAdapter.notifyDataSetChanged();
            }
        });

        return mRootView;
    }

    /**
     * 改变点点点的切换效果
     *
     * @param selectItems
     */
    private void setImageBackground(int selectItems) {
        for (int i = 0; i < ivPoints.length; i++) {
            if (i == selectItems) {
                ivPoints[i].setBackgroundResource(R.drawable.ellipse_84);
            } else {
                ivPoints[i].setBackgroundResource(R.drawable.ellipse_85);
            }
        }
    }

    private void showToast(String msg) {
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
    }

}
