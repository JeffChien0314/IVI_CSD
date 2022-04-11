package com.fxc.ev.launcher.maps.search;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fxc.ev.launcher.R;
import com.fxc.ev.launcher.activities.LauncherActivity;

import java.util.List;


public class FavoritesEditAdapter extends RecyclerView.Adapter<FavoritesEditAdapter.FavoritesItemViewHolder> {
    public static final String TAG = "FavoritesEditAdapter";
    private List<FavEditItem> mFavEditItemList;
    private FavoritesItemViewHolder favoritesItemViewHolder;
    private LauncherActivity mLauncherActivity;

    public enum ViewName {
        ITEM,
        EDIT,
        MOVE
    }

    private OnItemClickListener mItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, ViewName viewName, FavEditItem favEditItem, int position);
    }

    public FavoritesEditAdapter(LauncherActivity launcherActivity, List<FavEditItem> favEditItemList) {
        this.mLauncherActivity = launcherActivity;
        this.mFavEditItemList = favEditItemList;
    }

    @NonNull
    @Override
    public FavoritesItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mLauncherActivity).inflate(R.layout.fav_edit_list_item, parent, false);
        favoritesItemViewHolder = new FavoritesItemViewHolder(view);
        return favoritesItemViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FavoritesItemViewHolder holder, int position) {
        FavEditItem favEditItem = mFavEditItemList.get(position);
        holder.initHolder(favEditItem);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoritesItemViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        } else {
            EditItemStatus editItemStatus = (EditItemStatus) payloads.get(0);
            FavEditItem favEditItem = mFavEditItemList.get(position);

            holder.name.setText(favEditItem.getName());

            if (favEditItem.getName().equals(Constants.ADD_FAVORITE)) {
                holder.icon.setImageResource(R.drawable.icon_add_normal);
            } else {
                holder.icon.setImageResource(favEditItem.getImage());
            }

            if (favEditItem.getLocation() == null) {
                holder.icon_layout.setBackgroundResource(R.drawable.fav_item_icon_disable_bg);
            } else {
                holder.icon_layout.setBackgroundResource(favEditItem.getBackground());
            }

            if (TextUtils.isEmpty(favEditItem.getAddress())) {
                holder.address.setVisibility(View.GONE);
            } else {
                holder.address.setVisibility(View.VISIBLE);
                holder.address.setText(favEditItem.getAddress());
            }

            holder.setEditBtnVisibility(editItemStatus.visibility);
            holder.itemView.setFocusable(editItemStatus.isFocus);
        }
    }

    public void setItemClickListener(OnItemClickListener itemClickListener) {
        mItemClickListener = itemClickListener;
    }

    @Override
    public int getItemCount() {
        return mFavEditItemList.size();
    }

    class FavoritesItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView icon;
        private TextView name;
        private TextView address;
        private ImageView edit;
        private ImageView move;
        private LinearLayout icon_layout;


        public FavoritesItemViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.icon);
            name = itemView.findViewById(R.id.name);
            address = itemView.findViewById(R.id.address);
            edit = itemView.findViewById(R.id.icon_edit);
            move = itemView.findViewById(R.id.icon_move);
            icon_layout = itemView.findViewById(R.id.icon_layout);
        }

        public void initHolder(FavEditItem favEditItem) {
            icon.setImageResource(favEditItem.getImage());
            name.setText(favEditItem.getName());

            if (favEditItem.getLocation() == null) {
                if (favEditItem.getName().equals(Constants.ADD_FAVORITE)) {
                    icon.setImageResource(R.drawable.icon_add_normal);
                } else if (favEditItem.getName().equals("Home")) {
                    icon.setImageResource(R.drawable.icon_home_normal);
                } else if (favEditItem.getName().equals("Office")) {
                    icon.setImageResource(R.drawable.icon_office_normal);
                }
                icon_layout.setBackgroundResource(R.drawable.fav_item_icon_disable_bg);
            } else {
                icon.setImageResource(favEditItem.getImage());
                icon_layout.setBackgroundResource(favEditItem.getBackground());
            }

            if (TextUtils.isEmpty(favEditItem.getAddress())) {
                address.setVisibility(View.GONE);
            } else {
                address.setVisibility(View.VISIBLE);
                address.setText(favEditItem.getAddress());
            }

            setEditBtnVisibility(View.GONE);
            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mItemClickListener != null) {
                        mItemClickListener.onItemClick(v, ViewName.EDIT, favEditItem, getAdapterPosition());
                    }
                }
            });

            move.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mItemClickListener != null) {
                        mItemClickListener.onItemClick(v, ViewName.MOVE, favEditItem, getAdapterPosition());
                    }
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mItemClickListener != null) {
                        mItemClickListener.onItemClick(v, ViewName.ITEM, favEditItem, getAdapterPosition());
                    }
                }
            });
        }

        public void setEditBtnVisibility(int visibility) {
            edit.setVisibility(visibility);
            move.setVisibility(visibility);
        }
    }
}
