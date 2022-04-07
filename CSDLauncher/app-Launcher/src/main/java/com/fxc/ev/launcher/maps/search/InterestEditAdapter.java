package com.fxc.ev.launcher.maps.search;

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


public class InterestEditAdapter extends RecyclerView.Adapter<InterestEditAdapter.FavoritesItemViewHolder> {
    public static final String TAG = "InterestEditAdapter";
    private List<FavEditItem> mFavEditItemList;
    private FavoritesItemViewHolder favoritesItemViewHolder;
    private LauncherActivity mLauncherActivity;
    private RecyclerView mOwnerRecyclerView;
    private EditItemStatus editItemStatus;

    public enum ViewName {
        ITEM,
        EDIT,
        MOVE
    }

    private OnItemClickListener mItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, ViewName viewName, FavEditItem favEditItem, int position);
    }

    public InterestEditAdapter(LauncherActivity launcherActivity, RecyclerView ownerRecyclerView, List<FavEditItem> favEditItemList) {
        this.mLauncherActivity = launcherActivity;
        this.mFavEditItemList = favEditItemList;
        this.mOwnerRecyclerView = ownerRecyclerView;
    }

    @NonNull
    @Override
    public FavoritesItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.v(TAG, "onCreateViewHolder");
        View view = LayoutInflater.from(mLauncherActivity).inflate(R.layout.fav_edit_list_item, parent, false);
        favoritesItemViewHolder = new FavoritesItemViewHolder(view);
        return favoritesItemViewHolder;
    }

    @Override
    public void onViewAttachedToWindow(@NonNull FavoritesItemViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        Log.v(TAG, "onViewAttachedToWindow: editItemStatus: " + editItemStatus);
        if (editItemStatus != null) {
            holder.setEditBtnVisibility(editItemStatus.visibility);
            holder.itemView.setFocusable(editItemStatus.isFocus);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull FavoritesItemViewHolder holder, int position) {
        Log.v(TAG, "onBindViewHolder");
        FavEditItem favEditItem = mFavEditItemList.get(position);
        holder.initHolder(favEditItem);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoritesItemViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        } else {
            editItemStatus = (EditItemStatus) payloads.get(0);
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
        private ImageView move;
        private LinearLayout icon_layout;


        public FavoritesItemViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.icon);
            name = itemView.findViewById(R.id.name);

            move = itemView.findViewById(R.id.icon_move);
            icon_layout = itemView.findViewById(R.id.icon_layout);
        }

        public void initHolder(FavEditItem favEditItem) {
            name.setText(favEditItem.getName());

            icon.setImageResource(favEditItem.getImage());
            icon_layout.setBackgroundResource(favEditItem.getBackground());

            setEditBtnVisibility(View.GONE);

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
            move.setVisibility(visibility);
        }
    }
}
