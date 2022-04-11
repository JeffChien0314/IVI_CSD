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

import java.util.List;

public class InterestEditFragment extends Fragment {
    public static final String TAG = "InterestEditFragment";
    private View mRootView;
    private ImageView interestIconBack;
    private TextView interestBtnEdit;
    private LauncherActivity launcherActivity;
    private List<FavEditItem> mInterestItemList;
    private RecyclerView interestEditRecyclerView;
    private InterestEditAdapter interestEditAdapter;
    private InterestEditFragment interestEditFragment;
    private ItemTouchHelper.Callback mItemTouchCallBack;
    private ItemTouchHelper mItemTouchHelper;
    private int fromPosition = 0;
    private boolean isFirstMove = true;
    private SearchFragment mSearchFragment;

    public InterestEditFragment(SearchFragment searchFragment) {
        this.mSearchFragment = searchFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.interest_edit_fragment, container, false);
        launcherActivity = (LauncherActivity) getActivity();
        interestEditFragment = this;
        mInterestItemList = SpUtils.getDataList(launcherActivity, "interest_edit_list", "interest", FavEditItem.class);

        initView();
        return mRootView;
    }

    private void initView() {
        interestEditRecyclerView = mRootView.findViewById(R.id.interest_edit_recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        SpaceItemDecoration spaceItemDecoration = new SpaceItemDecoration();
        interestEditRecyclerView.addItemDecoration(spaceItemDecoration);
        interestEditRecyclerView.setLayoutManager(linearLayoutManager);

        interestEditAdapter = new InterestEditAdapter(launcherActivity, mInterestItemList);
        interestEditRecyclerView.setAdapter(interestEditAdapter);

        interestEditAdapter.setItemClickListener(new InterestEditAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, InterestEditAdapter.ViewName viewName, FavEditItem favEditItem, int position) {
                switch (viewName) {
                    case ITEM:
                        Log.v(TAG, "isFocusable:" + v.isFocusable());
                        if (v.isFocusable()) {
                            launcherActivity.setCurrentFragment(mSearchFragment);

                            new Handler().post(new Runnable() {
                                @Override
                                public void run() {
                                    mSearchFragment.go2SearchFromInterest(favEditItem);
                                }
                            });
                        }
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
                interestEditAdapter.notifyItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            }

            @Override
            public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                int endPosition = viewHolder.getAdapterPosition();
                FavEditItem prev = mInterestItemList.remove(fromPosition);
                mInterestItemList.add(endPosition, prev);
                SpUtils.setDataList(launcherActivity, "interest_edit_list", "interest", mInterestItemList);

                isFirstMove = true;
            }
        };

        mItemTouchHelper = new ItemTouchHelper(mItemTouchCallBack);

        interestIconBack = mRootView.findViewById(R.id.interest_edit_back);
        interestIconBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                interestEditFragment.getFragmentManager().popBackStack();
            }
        });

        interestBtnEdit = mRootView.findViewById(R.id.interest_edit);

        interestBtnEdit.setVisibility(View.VISIBLE);
        interestBtnEdit.setText("Edit");
        interestBtnEdit.setTextColor(Color.parseColor("#bfffffff"));
        interestBtnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (interestBtnEdit.getText().equals("Edit")) {
                    interestEditAdapter.notifyItemRangeChanged(0, interestEditAdapter.getItemCount(), new EditItemStatus(View.VISIBLE, false));

                    mItemTouchHelper.attachToRecyclerView(interestEditRecyclerView);
                    interestBtnEdit.setText("Done");
                    interestBtnEdit.setTextColor(Color.parseColor("#418eff"));
                } else if (interestBtnEdit.getText().equals("Done")) {
                    interestEditAdapter.notifyItemRangeChanged(0, interestEditAdapter.getItemCount(), new EditItemStatus(View.INVISIBLE, true));

                    mItemTouchHelper.attachToRecyclerView(null);
                    interestBtnEdit.setText("Edit");
                    interestBtnEdit.setTextColor(Color.parseColor("#bfffffff"));
                }
            }
        });
    }


}
