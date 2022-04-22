package com.fxc.ev.launcher.maps.search;

import static com.fxc.ev.launcher.maps.search.Constants.TYPE_FAVORITE;

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
import com.fxc.ev.launcher.activities.LauncherActivity;
import com.fxc.ev.launcher.utils.DistanceConversions;

import java.util.ArrayList;


public class SearchResultsAdapter extends RecyclerView.Adapter<SearchResultsAdapter.SearchItemViewHolder> {
    public static final String TAG = "SearchResultsAdapter";
    private ArrayList<SearchResultItem> searchResultItemArrayList;
    private OnItemClickListener mItemClickListener;
    private LauncherActivity launcherActivity;

    public interface OnItemClickListener {
        void onItemClick(ViewName viewName, SearchResultItem searchResultItem);
    }

    public enum ViewName {
        ITEM,
        NAVIGATION,
    }

    public SearchResultsAdapter(LauncherActivity launcherActivity, ArrayList<SearchResultItem> searchResultItemArrayList) {
        this.launcherActivity = launcherActivity;
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
                    + ",address: " + searchResultItem.getAddress()
                    + ",distance: " + searchResultItem.getDistance()
                    + ",phoneNum: " + searchResultItem.getPhoneNums()
                    + ",location: " + searchResultItem.getCoordinate());
            holder.name.setText(searchResultItem.getName());
            if (!TextUtils.isEmpty(searchResultItem.getAddress())) {
                holder.address.setVisibility(View.VISIBLE);
                holder.address.setText(searchResultItem.getAddress());
            } else {
                holder.address.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(String.valueOf(searchResultItem.getDistance()))) {
                String countryCode = launcherActivity.getCurrentCountryCode();
                holder.distance.setVisibility(View.VISIBLE);
                DistanceConversions.FormattedDistance fd = DistanceConversions.convert((searchResultItem.getDistance()), countryCode);
                holder.distance.setText(fd.distance);
                holder.distanceUnits.setText(fd.unit);
            } else {
                holder.distance.setVisibility(View.GONE);
            }

            if (searchResultItem.getSearchType().equals(TYPE_FAVORITE)) {
                holder.icon.setImageResource(0);
            } else {
                holder.icon.setImageResource(R.drawable.icon_search_item_nav);
                holder.icon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mItemClickListener != null) {
                            mItemClickListener.onItemClick(ViewName.NAVIGATION, searchResultItem);
                        }
                    }
                });
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mItemClickListener != null) {
                        mItemClickListener.onItemClick(ViewName.ITEM, searchResultItem);
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
