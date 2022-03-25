package com.fxc.ev.launcher.maps.search;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fxc.ev.launcher.R;
import com.fxc.ev.launcher.activities.LauncherActivity;
import com.fxc.ev.launcher.utils.SpUtils;
import com.fxc.ev.launcher.utils.SpaceItemDecoration;

import java.util.ArrayList;
import java.util.List;

public class FavoritesEditFragment extends Fragment {
    public static final String TAG = "FavoritesEditFragment";
    private View mRootView;
    private ImageView iconBack;
    private TextView btnEdit;
    private RecyclerView favEditRecyclerView;
    private LauncherActivity launcherActivity;
    private FavoritesEditAdapter favoritesEditAdapter;
    private List<FavEditItem> mFavEditItemList;
    private FavoritesEditFragment favoritesEditFragment;
    private ItemTouchHelper.Callback mItemTouchCallBack;
    private ItemTouchHelper mItemTouchHelper;
    private int fromPosition = 0;
    private boolean isFirstMove = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.favorites_edit_fragment, container, false);
        launcherActivity = (LauncherActivity) getActivity();
        favoritesEditFragment = this;
        mFavEditItemList = SpUtils.getDataList(launcherActivity, "favorites_edit_item_list", "favorites", FavEditItem.class);
        initView();
        return mRootView;
    }

    private void initView() {

        favEditRecyclerView = mRootView.findViewById(R.id.fav_edit_recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        SpaceItemDecoration spaceItemDecoration = new SpaceItemDecoration();
        favEditRecyclerView.addItemDecoration(spaceItemDecoration);
        favEditRecyclerView.setLayoutManager(linearLayoutManager);

        favoritesEditAdapter = new FavoritesEditAdapter(launcherActivity, mFavEditItemList);
        favEditRecyclerView.setAdapter(favoritesEditAdapter);

        favoritesEditAdapter.setItemClickListener(new FavoritesEditAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, FavoritesEditAdapter.ViewName viewName, FavEditItem favEditItem, int position) {
                switch (viewName) {
                    case ITEM:
                        if (v.isFocusable()) {
                            if (favEditItem.getLocation() == null) {
                                SearchFragment searchFragment = new SearchFragment();
                                launcherActivity.setCurrentFragment(searchFragment);
                                new Handler().post(new Runnable() {
                                    @Override
                                    public void run() {
                                        searchFragment.go2Search(Constants.FROM_FAV_EDIT_PAGE, position);
                                    }
                                });
                            } else {
                                SearchResultItem searchResultItem = new SearchResultItem();
                                searchResultItem.setLocation(favEditItem.getLocation());
                                searchResultItem.setName(favEditItem.getName());

                                RoutePreviewFragment routePreviewFragment = new RoutePreviewFragment();
                                launcherActivity.setCurrentFragment(routePreviewFragment);
                                routePreviewFragment.setData(searchResultItem);
                            }
                        }
                        break;
                    case EDIT:
                        Log.v(TAG, "edit 被点击啦: " + v.findViewById(R.id.icon_edit).isFocusable());

                        break;
                    case MOVE:
                        Log.v(TAG, "move 被点击啦: " + v.findViewById(R.id.icon_move).isFocusable());
                        break;
                }
            }
        });

        mItemTouchCallBack = new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                return makeMovementFlags(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                if (isFirstMove) {
                    fromPosition = viewHolder.getAdapterPosition();
                    isFirstMove = false;
                }
                favoritesEditAdapter.notifyItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            }

            @Override
            public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                int endPosition = viewHolder.getAdapterPosition();
                FavEditItem prev = mFavEditItemList.remove(fromPosition);
                mFavEditItemList.add(endPosition, prev);
                SpUtils.setDataList(launcherActivity, "favorites_edit_item_list", "favorites", mFavEditItemList);

                isFirstMove = true;
            }
        };

        mItemTouchHelper = new ItemTouchHelper(mItemTouchCallBack);

        iconBack = mRootView.findViewById(R.id.fav_edit_back);
        iconBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                favoritesEditFragment.getFragmentManager().popBackStack();
            }
        });

        btnEdit = mRootView.findViewById(R.id.fav_edit);
        btnEdit.setText("Edit");
        btnEdit.setTextColor(Color.parseColor("#bfffffff"));
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnEdit.getText().equals("Edit")) {
                    for (int i = 0; i < mFavEditItemList.size(); i++) {
                        FavoritesEditAdapter.FavoritesItemViewHolder viewHolder = (FavoritesEditAdapter.FavoritesItemViewHolder) favEditRecyclerView.findViewHolderForAdapterPosition(i);
                        if (mFavEditItemList.get(i).getLocation() == null) {
                            viewHolder.itemView.findViewById(R.id.icon_edit).setVisibility(View.INVISIBLE);
                        } else {
                            viewHolder.itemView.findViewById(R.id.icon_edit).setVisibility(View.VISIBLE);
                            viewHolder.itemView.findViewById(R.id.icon_edit).setFocusable(true);
                        }
                        viewHolder.itemView.findViewById(R.id.icon_move).setVisibility(View.VISIBLE);
                        viewHolder.itemView.findViewById(R.id.icon_move).setFocusable(true);


                        viewHolder.itemView.setFocusable(false);
                    }
                    mItemTouchHelper.attachToRecyclerView(favEditRecyclerView);
                    btnEdit.setText("Done");
                    btnEdit.setTextColor(Color.parseColor("#418eff"));
                } else if (btnEdit.getText().equals("Done")) {
                    for (int i = 0; i < mFavEditItemList.size(); i++) {
                        FavoritesEditAdapter.FavoritesItemViewHolder viewHolder = (FavoritesEditAdapter.FavoritesItemViewHolder) favEditRecyclerView.findViewHolderForAdapterPosition(i);
                        viewHolder.itemView.findViewById(R.id.icon_edit).setVisibility(View.GONE);
                        viewHolder.itemView.findViewById(R.id.icon_edit).setFocusable(false);

                        viewHolder.itemView.findViewById(R.id.icon_move).setVisibility(View.GONE);
                        viewHolder.itemView.findViewById(R.id.icon_move).setFocusable(false);

                        viewHolder.itemView.setFocusable(true);
                    }
                    mItemTouchHelper.attachToRecyclerView(null);
                    btnEdit.setText("Edit");
                    btnEdit.setTextColor(Color.parseColor("#bfffffff"));
                }
            }
        });
    }

}
