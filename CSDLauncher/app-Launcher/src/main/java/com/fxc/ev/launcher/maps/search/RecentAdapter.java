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


public class RecentAdapter extends RecyclerView.Adapter<RecentAdapter.FavoritesItemViewHolder> {
    public static final String TAG = "RecentAdapter";
    private List<FavEditItem> mFavEditItemList;
    private FavoritesItemViewHolder favoritesItemViewHolder;
    private LauncherActivity mLauncherActivity;

    private OnItemClickListener mItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, FavEditItem favEditItem, int position);
    }

    public RecentAdapter(LauncherActivity launcherActivity, List<FavEditItem> favEditItemList) {
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
        FavEditItem favEditItem = mFavEditItemList.get((mFavEditItemList.size() - 1) - position);
        holder.initHolder(favEditItem);
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
        private LinearLayout icon_layout;


        public FavoritesItemViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.icon);
            name = itemView.findViewById(R.id.name);
            address = itemView.findViewById(R.id.address);
            icon_layout = itemView.findViewById(R.id.icon_layout);
        }

        public void initHolder(FavEditItem favEditItem) {
            name.setText(favEditItem.getName());
            address.setText(favEditItem.getAddress());
            icon.setImageResource(favEditItem.getImage());
            icon_layout.setBackgroundResource(favEditItem.getBackground());

            address.setVisibility(View.VISIBLE);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mItemClickListener != null) {
                        mItemClickListener.onItemClick(v, favEditItem, getAdapterPosition());
                    }
                }
            });
        }
    }
}
