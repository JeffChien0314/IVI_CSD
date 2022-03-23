package com.fxc.ev.launcher.maps.search;

import android.content.Context;
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
import com.fxc.ev.launcher.utils.SpUtils;

import java.util.ArrayList;
import java.util.List;


public class FavoritesEditAdapter extends RecyclerView.Adapter<FavoritesEditAdapter.FavoritesItemViewHolder> {
    public static final String TAG = "SearchResultsAdapter";
    List<FavEditItem> mFavEditItemList;

    private OnItemClickListener mItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(SearchResultItem searchResultItem);
    }

    public FavoritesEditAdapter(Context context, List<FavEditItem> favEditItemList) {
        this.mFavEditItemList = favEditItemList;
    }

    @NonNull
    @Override
    public FavoritesItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fav_edit_list_item, parent, false);
        return new FavoritesItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoritesItemViewHolder holder, int position) {
        FavEditItem favEditItem = mFavEditItemList.get(position);
        holder.icon.setImageResource(favEditItem.getImage());
        holder.icon_layout.setBackgroundResource(favEditItem.getBackground());
        holder.name.setText(favEditItem.getName());
        holder.address.setText(favEditItem.getAddress());
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
    }
}
