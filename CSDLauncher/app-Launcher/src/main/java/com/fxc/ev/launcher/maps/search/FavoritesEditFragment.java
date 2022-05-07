package com.fxc.ev.launcher.maps.search;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fxc.ev.launcher.R;
import com.fxc.ev.launcher.activities.LauncherActivity;
import com.fxc.ev.launcher.utils.SpUtils;
import com.fxc.ev.launcher.utils.SpaceItemDecoration;

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
    private AlertDialog mDialog;
    private ImageView dialogIcon;
    private EditText dialogEditText;
    private ImageView dialogClearAll;
    private ImageView dialogHomeIcon;
    private ImageView dialogOfficeIcon;
    private ImageView dialogStarIcon;
    private ImageView dialogHeartIcon;
    private ImageView dialogPinIcon;
    private Button dialogDoneBtn;
    private Button dialogCancelBtn;
    private ImageView dialogDeleteBtn;
    private InputMethodManager mInputMethodManager;
    private int fromPosition = 0;
    private boolean isFirstMove = true;
    private SearchFragment mSearchFragment;
    private EditDialog mEditDialog;

    public FavoritesEditFragment(SearchFragment searchFragment) {
        this.mSearchFragment = searchFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.favorites_edit_fragment, container, false);
        launcherActivity = (LauncherActivity) getActivity();
        favoritesEditFragment = this;
        mFavEditItemList = SpUtils.getDataList(launcherActivity, "favorites_edit_item_list", "favorites", FavEditItem.class);
        mInputMethodManager = (InputMethodManager) launcherActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
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
                        Log.v(TAG, "isFocusable:" + v.isFocusable());
                        if (v.isFocusable()) {
                            if (TextUtils.isEmpty(favEditItem.getCoordinate())) {
                                launcherActivity.getSupportFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                                launcherActivity.setCurrentFragment(mSearchFragment, false);
                                new Handler().post(new Runnable() {
                                    @Override
                                    public void run() {
                                        mSearchFragment.go2SearchFromFavorites(Constants.FROM_FAV_EDIT_PAGE, position);
                                    }
                                });
                            } else {
                                SearchResultItem searchResultItem = new SearchResultItem();
                                searchResultItem.setCoordinate(SpUtils.string2Coordinate(favEditItem.getCoordinate()));
                                searchResultItem.setName(favEditItem.getName());
                                searchResultItem.setAddress(favEditItem.getAddress());
                                searchResultItem.setDistance(favEditItem.getDistance());
                                searchResultItem.setSearchType(Constants.TYPE_FAVORITE);

                                RoutePreviewFragment routePreviewFragment = new RoutePreviewFragment();
                                launcherActivity.setCurrentFragment(routePreviewFragment, false);
                                routePreviewFragment.setData(searchResultItem);

                                mSearchFragment.saveData2Recent(searchResultItem, favEditItem);
                            }
                        }
                        break;
                    case EDIT:
                        Log.v(TAG, "edit 被点击啦: " + v.findViewById(R.id.icon_edit).isFocusable());
                        mEditDialog = new EditDialog(launcherActivity, favEditItem, position);
                        mEditDialog.setDeleteBtnVisibility(View.VISIBLE);
                        mEditDialog.showDialog();
                        //showDialog(favEditItem, position);
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
        if (mFavEditItemList.size() == 1 && mFavEditItemList.get(0).getName().equals(Constants.ADD_FAVORITE)) {
            btnEdit.setVisibility(View.INVISIBLE);
        } else {
            btnEdit.setVisibility(View.VISIBLE);
        }
        btnEdit.setText("Edit");
        btnEdit.setTextColor(Color.parseColor("#bfffffff"));
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnEdit.getText().equals("Edit")) {
                    for (int i = 0; i < mFavEditItemList.size(); i++) {
                        FavoritesEditAdapter.FavoritesItemViewHolder viewHolder = (FavoritesEditAdapter.FavoritesItemViewHolder) favEditRecyclerView.findViewHolderForAdapterPosition(i);
                        if (TextUtils.isEmpty(mFavEditItemList.get(i).getCoordinate())) {
                            viewHolder.itemView.findViewById(R.id.icon_edit).setVisibility(View.INVISIBLE);
                        } else {
                            viewHolder.itemView.findViewById(R.id.icon_edit).setVisibility(View.VISIBLE);
                        }
                        viewHolder.itemView.findViewById(R.id.icon_move).setVisibility(View.VISIBLE);

                        viewHolder.itemView.setFocusable(false);
                    }
                    mItemTouchHelper.attachToRecyclerView(favEditRecyclerView);
                    btnEdit.setText("Done");
                    btnEdit.setTextColor(Color.parseColor("#418eff"));
                } else if (btnEdit.getText().equals("Done")) {
                    for (int i = 0; i < mFavEditItemList.size(); i++) {
                        FavoritesEditAdapter.FavoritesItemViewHolder viewHolder = (FavoritesEditAdapter.FavoritesItemViewHolder) favEditRecyclerView.findViewHolderForAdapterPosition(i);
                        viewHolder.itemView.findViewById(R.id.icon_edit).setVisibility(View.GONE);
                        viewHolder.itemView.findViewById(R.id.icon_move).setVisibility(View.GONE);

                        viewHolder.itemView.setFocusable(true);
                    }
                    mItemTouchHelper.attachToRecyclerView(null);
                    btnEdit.setText("Edit");
                    btnEdit.setTextColor(Color.parseColor("#bfffffff"));
                }
            }
        });
    }

    class EditDialog extends FavoriteBaseDialog {
        int position;

        protected EditDialog(LauncherActivity launcherActivity, FavEditItem favEditItem, int position) {
            super(launcherActivity, favEditItem);
            this.position = position;
        }

        @Override
        public void processDoneEvent() {
            favoritesEditAdapter.notifyItemChanged(position, new EditItemStatus(View.VISIBLE, false));
            SpUtils.setDataList(launcherActivity, "favorites_edit_item_list", "favorites", mFavEditItemList);
        }

        @Override
        public void processDeleteEvent(FavEditItem favEditItem) {
            if (mFavEditItemList.size() == 1) {
                favEditItem.setName(Constants.ADD_FAVORITE);
                favEditItem.setImage(R.drawable.icon_add_disable);
                favEditItem.setBackground(R.drawable.fav_item_btn_disable_bg);
                favEditItem.setTextColor(Constants.textDisableColor);
                favEditItem.setCoordinate("");
                favEditItem.setAddress("");
                favoritesEditAdapter.notifyItemChanged(position, new EditItemStatus(View.INVISIBLE, true));

                mItemTouchHelper.attachToRecyclerView(null);
                btnEdit.setVisibility(View.INVISIBLE);
            } else {
                mFavEditItemList.remove(position);
                favoritesEditAdapter.notifyItemRemoved(position);
                favoritesEditAdapter.notifyItemRangeChanged(position, favoritesEditAdapter.getItemCount(), new EditItemStatus(View.VISIBLE, false));
            }
            mEditDialog.dismissDialog();
            hideSoftInput();
            SpUtils.setDataList(launcherActivity, "favorites_edit_item_list", "favorites", mFavEditItemList);
        }
    }

    private void showDialog(FavEditItem favEditItem, int position) {
        initDialogView();
        initDialogIcon(favEditItem);

        dialogEditText.setText(favEditItem.getName());
        dialogEditText.requestFocus();
        dialogEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(dialogEditText.getText())) {
                    dialogClearAll.setVisibility(View.GONE);
                    dialogDoneBtn.setFocusable(false);
                    dialogDoneBtn.setBackgroundResource(R.drawable.dialog_btn_disable_bg);
                } else {
                    dialogClearAll.setVisibility(View.VISIBLE);
                    dialogDoneBtn.setFocusable(true);
                    dialogDoneBtn.setBackgroundResource(R.drawable.dialog_btn_select_bg);
                }
            }
        });

        dialogClearAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogEditText.setText("");
                dialogClearAll.setVisibility(View.GONE);
                dialogDoneBtn.setFocusable(false);
                dialogDoneBtn.setBackgroundResource(R.drawable.dialog_btn_disable_bg);
            }
        });

        dialogDoneBtn.setBackgroundResource(R.drawable.dialog_btn_select_bg);
        dialogDoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialogDoneBtn.isFocusable()) {
                    favEditItem.setName(dialogEditText.getText().toString());
                    mDialog.dismiss();
                    hideSoftInput();
                    favoritesEditAdapter.notifyItemChanged(position, new EditItemStatus(View.VISIBLE, false));
                    SpUtils.setDataList(launcherActivity, "favorites_edit_item_list", "favorites", mFavEditItemList);
                }
            }
        });

        dialogCancelBtn.setBackgroundResource(R.drawable.transparent);
        dialogCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogDoneBtn.setBackgroundResource(R.drawable.transparent);
                dialogCancelBtn.setBackgroundResource(R.drawable.dialog_btn_select_bg);
                mDialog.dismiss();
                hideSoftInput();
            }
        });

        dialogDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFavEditItemList.size() == 1) {
                    favEditItem.setName(Constants.ADD_FAVORITE);
                    favEditItem.setImage(R.drawable.icon_add_disable);
                    favEditItem.setBackground(R.drawable.fav_item_btn_disable_bg);
                    favEditItem.setTextColor(Constants.textDisableColor);
                    favEditItem.setCoordinate("");
                    favEditItem.setAddress("");
                    favoritesEditAdapter.notifyItemChanged(position, new EditItemStatus(View.INVISIBLE, true));

                    mItemTouchHelper.attachToRecyclerView(null);
                    btnEdit.setVisibility(View.INVISIBLE);
                } else {
                    mFavEditItemList.remove(position);
                    favoritesEditAdapter.notifyItemRemoved(position);
                    favoritesEditAdapter.notifyItemRangeChanged(position, favoritesEditAdapter.getItemCount(), new EditItemStatus(View.VISIBLE, false));
                }
                mDialog.dismiss();
                hideSoftInput();
                SpUtils.setDataList(launcherActivity, "favorites_edit_item_list", "favorites", mFavEditItemList);
            }
        });

        mDialog.show();
    }

    private void initDialogIcon(FavEditItem favEditItem) {
        dialogIcon.setImageResource(favEditItem.getImage());
        setDialogIconBg(favEditItem.getImage());

        dialogHomeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogIcon.setImageResource(R.drawable.icon_home_normal);
                setDialogIconBg(R.drawable.icon_home_normal);
                favEditItem.setImage(R.drawable.icon_home_normal);
            }
        });
        dialogOfficeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogIcon.setImageResource(R.drawable.icon_office_normal);
                setDialogIconBg(R.drawable.icon_office_normal);
                favEditItem.setImage(R.drawable.icon_office_normal);
            }
        });
        dialogStarIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogIcon.setImageResource(R.drawable.icon_star_normal);
                setDialogIconBg(R.drawable.icon_star_normal);
                favEditItem.setImage(R.drawable.icon_star_normal);
            }
        });
        dialogHeartIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogIcon.setImageResource(R.drawable.icon_heart_normal);
                setDialogIconBg(R.drawable.icon_heart_normal);
                favEditItem.setImage(R.drawable.icon_heart_normal);
            }
        });
        dialogPinIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogIcon.setImageResource(R.drawable.icon_pin_normal);
                setDialogIconBg(R.drawable.icon_pin_normal);
                favEditItem.setImage(R.drawable.icon_pin_normal);
            }
        });
    }

    private void setDialogIconBg(int resId) {
        switch (resId) {
            case R.drawable.icon_home_normal:
                dialogHomeIcon.setBackgroundResource(R.drawable.dialog_fav_icon_select_bg);
                dialogOfficeIcon.setBackgroundResource(R.drawable.transparent);
                dialogStarIcon.setBackgroundResource(R.drawable.transparent);
                dialogHeartIcon.setBackgroundResource(R.drawable.transparent);
                dialogPinIcon.setBackgroundResource(R.drawable.transparent);
                break;
            case R.drawable.icon_office_normal:
                dialogHomeIcon.setBackgroundResource(R.drawable.transparent);
                dialogOfficeIcon.setBackgroundResource(R.drawable.dialog_fav_icon_select_bg);
                dialogStarIcon.setBackgroundResource(R.drawable.transparent);
                dialogHeartIcon.setBackgroundResource(R.drawable.transparent);
                dialogPinIcon.setBackgroundResource(R.drawable.transparent);
                break;
            case R.drawable.icon_star_normal:
                dialogHomeIcon.setBackgroundResource(R.drawable.transparent);
                dialogOfficeIcon.setBackgroundResource(R.drawable.transparent);
                dialogStarIcon.setBackgroundResource(R.drawable.dialog_fav_icon_select_bg);
                dialogHeartIcon.setBackgroundResource(R.drawable.transparent);
                dialogPinIcon.setBackgroundResource(R.drawable.transparent);
                break;
            case R.drawable.icon_heart_normal:
                dialogHomeIcon.setBackgroundResource(R.drawable.transparent);
                dialogOfficeIcon.setBackgroundResource(R.drawable.transparent);
                dialogStarIcon.setBackgroundResource(R.drawable.transparent);
                dialogHeartIcon.setBackgroundResource(R.drawable.dialog_fav_icon_select_bg);
                dialogPinIcon.setBackgroundResource(R.drawable.transparent);
                break;
            case R.drawable.icon_pin_normal:
                dialogHomeIcon.setBackgroundResource(R.drawable.transparent);
                dialogOfficeIcon.setBackgroundResource(R.drawable.transparent);
                dialogStarIcon.setBackgroundResource(R.drawable.transparent);
                dialogHeartIcon.setBackgroundResource(R.drawable.transparent);
                dialogPinIcon.setBackgroundResource(R.drawable.dialog_fav_icon_select_bg);
                break;
        }
    }

    private void initDialogView() {
        View view = LayoutInflater.from(launcherActivity).inflate(R.layout.fav_item_edit_dialog, null, false);
        mDialog = new AlertDialog.Builder(launcherActivity).setView(view).create();

        Window dialogWindow = mDialog.getWindow();
        dialogWindow.setBackgroundDrawableResource(android.R.color.transparent);

        dialogIcon = view.findViewById(R.id.dialog_fav_icon);
        dialogEditText = view.findViewById(R.id.dialog_fav_edit_text);
        dialogClearAll = view.findViewById(R.id.dialog_fav_clear_all);

        dialogHomeIcon = view.findViewById(R.id.dialog_home_icon);
        dialogOfficeIcon = view.findViewById(R.id.dialog_office_icon);
        dialogStarIcon = view.findViewById(R.id.dialog_star_icon);
        dialogHeartIcon = view.findViewById(R.id.dialog_heart_icon);
        dialogPinIcon = view.findViewById(R.id.dialog_pin_icon);

        dialogDoneBtn = view.findViewById(R.id.dialog_btn_done);
        dialogCancelBtn = view.findViewById(R.id.dialog_btn_cancel);
        dialogDeleteBtn = view.findViewById(R.id.dialog_delete);

    }

    private void showSoftInput() {
        if (mInputMethodManager != null) {
            mInputMethodManager.showSoftInput(dialogEditText, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    private void hideSoftInput() {
        if (mInputMethodManager != null && mInputMethodManager.isActive()) {
            mInputMethodManager.hideSoftInputFromWindow(launcherActivity.getWindow().getDecorView().getWindowToken(), 0);
        }
    }

}
