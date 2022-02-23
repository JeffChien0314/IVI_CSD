package com.fxc.ev.launcher.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.fxc.ev.launcher.R;
import com.fxc.ev.launcher.activities.LauncherActivity;
import com.fxc.ev.launcher.bean.PakageMod;
import com.fxc.ev.launcher.utils.view.DragGridView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import androidx.fragment.app.Fragment;

public class Frg_WidgetsEdit extends Fragment {

    private Context mContext;

    private View mRootView;
    private TextView mEditTips;
    private TextView mEditDone;

    private List<PakageMod> pakageModList=new ArrayList<>();
    private List<String> mIncludedWidgetLabelList=new ArrayList<>();
    private List<String> mMoreWidgetLabelList = new ArrayList<>();

    private DragGridView mDraggableGridViewIncluded;
    private GridView mGridViewMore;
    private ArrayAdapter<PakageMod> mIncludedWidgetAdapter;
    private ArrayAdapter<PakageMod> mMoreWidgetAdapter;


    private void initAllWidgetList() {
        pakageModList.add(new PakageMod(null, "Analog clock", getResources().getDrawable(R.drawable.icon_widget_edit_music), null));
        pakageModList.add(new PakageMod(null, "my test widget", getResources().getDrawable(R.drawable.icon_widget_edit_recents), null));
        pakageModList.add(new PakageMod(null, "widget1", null, null));
        pakageModList.add(new PakageMod(null, "widget2", null, null));
        pakageModList.add(new PakageMod(null, "widget3", null, null));
        pakageModList.add(new PakageMod(null, "widget4", null, null));
        pakageModList.add(new PakageMod(null, "widget5", null, null));
        pakageModList.add(new PakageMod(null, "widget6", null, null));
        pakageModList.add(new PakageMod(null, "widget7", null, null));
        pakageModList.add(new PakageMod(null, "widget8", null, null));
        pakageModList.add(new PakageMod(null, "widget9", null, null));
        pakageModList.add(new PakageMod(null, "widget10", null, null));
        pakageModList.add(new PakageMod(null, "widget11", null, null));

        List<String> mAllWidgetLabelList = new ArrayList<>();
        for (PakageMod p : pakageModList) {
            mAllWidgetLabelList.add(p.name);
        }
        mIncludedWidgetLabelList = ((LauncherActivity) getActivity()).widgetLabelList;
        mAllWidgetLabelList.removeAll(mIncludedWidgetLabelList);
        mMoreWidgetLabelList=mAllWidgetLabelList;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity().getApplicationContext();

        mRootView = inflater.inflate(R.layout.widgets_edit_layout, container, false);
        mEditTips =(TextView) mRootView.findViewById(R.id.edit_tips);
        mEditDone =(TextView) mRootView.findViewById(R.id.edit_done);
        mDraggableGridViewIncluded = (DragGridView) mRootView.findViewById(R.id.draggable_grid_view_included);
        mGridViewMore = (GridView) mRootView.findViewById(R.id.grid_view_more);

        mIncludedWidgetAdapter = new ArrayAdapter<PakageMod>(mContext, 0) {
            @Override
            public @NotNull View getView(int position, View convertView, ViewGroup parent) {
                final PakageMod pakageMod = getItem(position);

                if (convertView == null) {
                    convertView = getLayoutInflater().inflate(R.layout.widget_edit_item, null);
                }
                if (pakageMod.name != null) {
                    convertView.findViewById(R.id.widget_null_layout).setVisibility(View.GONE);
                    convertView.findViewById(R.id.widget_edit_icon).setVisibility(View.VISIBLE);
                    convertView.findViewById(R.id.widget_main_layout).setVisibility(View.VISIBLE);
                    ((ImageView) convertView.findViewById(R.id.widget_edit_icon)).setImageDrawable(pakageMod.editIcon);
                    ((TextView) convertView.findViewById(R.id.widget_name)).setText(pakageMod.name);
                    ((ImageView) convertView.findViewById(R.id.widget_image)).setImageDrawable(pakageMod.icon);
                    if (((ImageView) convertView.findViewById(R.id.widget_edit_icon)).getDrawable() != null) {
                        ((ImageView) convertView.findViewById(R.id.widget_edit_icon)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                deleteWidget(pakageMod);
                            }
                        });
                    }
                } else {
                    convertView.findViewById(R.id.widget_null_layout).setVisibility(View.VISIBLE);
                    convertView.findViewById(R.id.widget_edit_icon).setVisibility(View.GONE);
                    convertView.findViewById(R.id.widget_main_layout).setVisibility(View.GONE);
                }

                return convertView;
            }

        };

        mMoreWidgetAdapter = new ArrayAdapter<PakageMod>(mContext, 0) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                final PakageMod pakageMod = getItem(position);
                if (convertView == null) {
                    convertView = getLayoutInflater().inflate(R.layout.widget_edit_item, null);
                }
                convertView.findViewById(R.id.widget_null_layout).setVisibility(View.GONE);
                ((ImageView) convertView.findViewById(R.id.widget_edit_icon)).setImageDrawable(pakageMod.editIcon);
                ((TextView) convertView.findViewById(R.id.widget_name)).setText(pakageMod.name);
                ((ImageView) convertView.findViewById(R.id.widget_image)).setImageDrawable(pakageMod.icon);
                if (((ImageView) convertView.findViewById(R.id.widget_edit_icon)).getDrawable() != null) {
                    ((ImageView) convertView.findViewById(R.id.widget_edit_icon)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            addWidget(pakageMod);
                        }
                    });
                }
                return convertView;
            }

        };

        initAllWidgetList();
        dispIncludedWidgets();
        dispMoreWidgets();
        if (mIncludedWidgetLabelList.size() == 4) {
            mEditTips.setVisibility(View.VISIBLE);
        } else {
            mEditTips.setVisibility(View.GONE);
        }

        mEditDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < mIncludedWidgetLabelList.size(); i++) {
                    ((LauncherActivity) getActivity()).mEditor.putString("widget" + (i + 1), mIncludedWidgetLabelList.get(i));
                    ((LauncherActivity) getActivity()).mEditor.commit();
                }
                getActivity().onBackPressed();
            }
        });
        return mRootView;
    }

    private void dispIncludedWidgets() {
        for (String label : mIncludedWidgetLabelList) {
            for (PakageMod p : pakageModList) {
                if (label.equals(p.name)) {
                    if (mIncludedWidgetLabelList.size() > 2) {
                        mIncludedWidgetAdapter.add(new PakageMod(p.pakageName, p.name, p.icon, getResources().getDrawable(R.drawable.icon_delete)));
                    } else {
                        mIncludedWidgetAdapter.add(new PakageMod(p.pakageName, p.name, p.icon, null));
                    }
                    break;
                }
            }
        }
        if (mIncludedWidgetLabelList.size() == 2) {
            mIncludedWidgetAdapter.add(new PakageMod(null, null, null, null));
            mIncludedWidgetAdapter.add(new PakageMod(null, null, null, null));
        } else if (mIncludedWidgetLabelList.size() == 3) {

        }
        mDraggableGridViewIncluded.setAdapter(mIncludedWidgetAdapter);

        mDraggableGridViewIncluded.setOnItemChangeListener(new DragGridView.OnItemChangerListener() {
            @Override
            public void onChange(int from, int to) {
                if (from >= mIncludedWidgetLabelList.size() || to >= mIncludedWidgetLabelList.size())
                    return; //虚线item不能调整位置

                PakageMod item = mIncludedWidgetAdapter.getItem(from);
                mIncludedWidgetAdapter.setNotifyOnChange(false);
                mIncludedWidgetAdapter.remove(item);
                mIncludedWidgetAdapter.insert(item, to);
                mIncludedWidgetAdapter.notifyDataSetChanged();

                //更新mIncludedWidgetLabelList
                String temp = mIncludedWidgetLabelList.get(from);
                if (from < to) {
                    for (int i = from; i < to; i++) {
                        Collections.swap(mIncludedWidgetLabelList, i, i + 1);
                    }
                } else if (from > to) {
                    for (int i = from; i > to; i--) {
                        Collections.swap(mIncludedWidgetLabelList, i, i - 1);
                    }
                }

                mIncludedWidgetLabelList.set(to, temp);

            }

        });

    }

    private void dispMoreWidgets() {
        for (String label : mMoreWidgetLabelList) {
            for (PakageMod p : pakageModList) {
                if (label.equals(p.name)) {
                    if (mIncludedWidgetLabelList.size() == 4) {
                        mMoreWidgetAdapter.add(new PakageMod(p.pakageName, p.name, p.icon, null));
                    } else {
                        mMoreWidgetAdapter.add(new PakageMod(p.pakageName, p.name, p.icon, getResources().getDrawable(R.drawable.icon_add)));
                    }
                    break;
                }
            }
        }

        mGridViewMore.setAdapter(mMoreWidgetAdapter);
    }

    private void addWidget(PakageMod pakageMod) {
        mIncludedWidgetLabelList.add(pakageMod.name);
        mMoreWidgetLabelList.remove(pakageMod.name);
        if (mIncludedWidgetLabelList.size() == 3) {
            for (int i = 0; i < 2; i++) {
                Objects.requireNonNull(mIncludedWidgetAdapter.getItem(i)).editIcon = getResources().getDrawable(R.drawable.icon_delete);
            }
        } else if (mIncludedWidgetLabelList.size() == 4) {
            for (int i = 0; i < mMoreWidgetAdapter.getCount(); i++) {
                Objects.requireNonNull(mMoreWidgetAdapter.getItem(i)).editIcon = null;
            }
        }
        Objects.requireNonNull(mIncludedWidgetAdapter.getItem(mIncludedWidgetLabelList.size() - 1)).name = pakageMod.name;
        Objects.requireNonNull(mIncludedWidgetAdapter.getItem(mIncludedWidgetLabelList.size() - 1)).icon = pakageMod.icon;
        Objects.requireNonNull(mIncludedWidgetAdapter.getItem(mIncludedWidgetLabelList.size() - 1)).editIcon = getResources().getDrawable(R.drawable.icon_delete);
        mIncludedWidgetAdapter.notifyDataSetChanged();
        mMoreWidgetAdapter.remove(pakageMod);

        if (mIncludedWidgetLabelList.size() == 4) {
            mEditTips.setVisibility(View.VISIBLE);
        } else {
            mEditTips.setVisibility(View.GONE);
        }
    }

    private void deleteWidget(PakageMod pakageMod) {
        mIncludedWidgetLabelList.remove(pakageMod.name);
        mMoreWidgetLabelList.add(pakageMod.name);

        mIncludedWidgetAdapter.setNotifyOnChange(false);
        mIncludedWidgetAdapter.remove(pakageMod);
        mIncludedWidgetAdapter.add(new PakageMod(null, null, null, null));
        if (mIncludedWidgetLabelList.size() == 2) {
            for (int i = 0; i < 2; i++) {
                Objects.requireNonNull(mIncludedWidgetAdapter.getItem(i)).editIcon = null;
            }
        } else if (mIncludedWidgetLabelList.size() == 3) {
            for (int i = 0; i < mMoreWidgetAdapter.getCount(); i++) {
                Objects.requireNonNull(mMoreWidgetAdapter.getItem(i)).editIcon = getResources().getDrawable(R.drawable.icon_add);
            }
        }
        mIncludedWidgetAdapter.notifyDataSetChanged();
        mMoreWidgetAdapter.add(new PakageMod(pakageMod.pakageName, pakageMod.name, pakageMod.icon, getResources().getDrawable(R.drawable.icon_add)));

        if (mIncludedWidgetLabelList.size() == 4) {
            mEditTips.setVisibility(View.VISIBLE);
        } else {
            mEditTips.setVisibility(View.GONE);
        }
    }

}
