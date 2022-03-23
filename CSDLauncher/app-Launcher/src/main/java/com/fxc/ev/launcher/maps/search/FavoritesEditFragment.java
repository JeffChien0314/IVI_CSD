package com.fxc.ev.launcher.maps.search;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fxc.ev.launcher.R;
import com.fxc.ev.launcher.activities.LauncherActivity;
import com.fxc.ev.launcher.utils.SpUtils;
import com.fxc.ev.launcher.utils.SpaceItemDecoration;

import java.util.ArrayList;
import java.util.List;

public class FavoritesEditFragment extends Fragment {
    private View mRootView;
    private ImageView iconBack;
    private TextView btnEdit;
    private RecyclerView favEditRecyclerView;
    private LauncherActivity launcherActivity;
    private FavoritesEditAdapter favoritesEditAdapter;

    private List<FavEditItem> mFavEditItemList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.favorites_edit_fragment, container, false);
        launcherActivity = (LauncherActivity) getActivity();
        mFavEditItemList = SpUtils.getDataList(launcherActivity, "favorites_edit_item_list", "favorites", FavEditItem.class);
        initView();
        return mRootView;
    }

    private void initView() {
        iconBack = mRootView.findViewById(R.id.fav_edit_back);
        btnEdit = mRootView.findViewById(R.id.fav_edit);
        favEditRecyclerView = mRootView.findViewById(R.id.fav_edit_recyclerview);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        SpaceItemDecoration spaceItemDecoration = new SpaceItemDecoration();
        favEditRecyclerView.addItemDecoration(spaceItemDecoration);
        favEditRecyclerView.setLayoutManager(linearLayoutManager);

        favoritesEditAdapter = new FavoritesEditAdapter(launcherActivity, mFavEditItemList);
        favEditRecyclerView.setAdapter(favoritesEditAdapter);
    }

}
