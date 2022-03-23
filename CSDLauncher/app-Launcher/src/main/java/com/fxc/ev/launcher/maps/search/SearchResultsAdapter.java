package com.fxc.ev.launcher.maps.search;

import android.content.Context;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import androidx.recyclerview.widget.RecyclerView;

import com.fxc.ev.launcher.R;

import java.util.ArrayList;


public class SearchResultsAdapter extends RecyclerView.Adapter<SearchResultsAdapter.SearchItemViewHolder> {
    public static final String TAG = "SearchResultsAdapter";
    private ArrayList<SearchResultItem> searchResultItemArrayList;
    private OnItemClickListener mItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(SearchResultItem searchResultItem);
    }

    public SearchResultsAdapter(Context context, ArrayList<SearchResultItem> searchResultItemArrayList) {
        this.searchResultItemArrayList = searchResultItemArrayList;
    }

    @NonNull
    @Override
    public SearchItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_result_item, parent, false);
        return new SearchItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchItemViewHolder holder, int position) {
        SearchResultItem searchResultItem = searchResultItemArrayList.get(position);
        if (searchResultItem != null) {
            Log.v(TAG, "name: " + searchResultItem.getName()
                    + ",address:" + searchResultItem.getAddress()
                    + ",distance:" + searchResultItem.getDistance()
                    + ",phoneNum::" + searchResultItem.getPhoneNums());
            holder.name.setText(searchResultItem.getName());
            if (!TextUtils.isEmpty(searchResultItem.getAddress())) {
                holder.address.setVisibility(View.VISIBLE);
                holder.address.setText(searchResultItem.getAddress());
            } else {
                holder.address.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(String.valueOf(searchResultItem.getDistance()))) {
                holder.distance.setVisibility(View.VISIBLE);
                int distance = searchResultItem.getDistance();
                if (distance >= 10 * 1000) {
                    holder.distance.setText(String.valueOf(distance / 1000));
                    holder.distanceUnits.setText("km");
                } else if (distance > 1000 && distance < 10 * 1000) {
                    holder.distance.setText(String.valueOf(distance * 0.001).substring(0, 3));
                    holder.distanceUnits.setText("km");
                } else {
                    holder.distance.setText(String.valueOf(distance));
                    holder.distanceUnits.setText("m");
                }
            } else {
                holder.distance.setVisibility(View.GONE);
            }

            if (searchResultItem.getSearchType().equals("search")) {
                holder.icon.setImageResource(R.drawable.icon_search_item_nav);
            } else {
                holder.icon.setImageResource(0);
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mItemClickListener != null) {
                        mItemClickListener.onItemClick(searchResultItem);
                    }
                }
            });
        }
    }


    public void setItemClickListener(OnItemClickListener itemClickListener) {
        mItemClickListener = itemClickListener;
    }

    @Override
    public int getItemCount() {
        return searchResultItemArrayList.size();
    }

    class SearchItemViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private TextView address;
        private TextView openingHours;
        private TextView phoneNum;
        private TextView distance;
        private TextView distanceUnits;
        private ImageView icon;

        public SearchItemViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            address = itemView.findViewById(R.id.address);
            openingHours = itemView.findViewById(R.id.opening_hours);
            phoneNum = itemView.findViewById(R.id.phone_num);
            distance = itemView.findViewById(R.id.distance);
            distanceUnits = itemView.findViewById(R.id.distance_units);
            icon = itemView.findViewById(R.id.icon);
        }
    }
}
