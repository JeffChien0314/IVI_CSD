package com.fxc.ev.launcher.maps.search;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.fxc.ev.launcher.R;

public class RoutePreviewFragment extends Fragment {
    private View mRootView;
    private SearchResultItem mSearchResultItem;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.search_route_preview_fragment, container, false);
        return mRootView;
    }

    public void setData(SearchResultItem searchResultItem){
        mSearchResultItem = searchResultItem;
    }
}
