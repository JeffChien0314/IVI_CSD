package com.fxc.ev.launcher.maps.search;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.fxc.ev.launcher.R;
import com.fxc.ev.launcher.activities.LauncherActivity;
import com.fxc.ev.launcher.utils.DistanceConversions;
import com.fxc.ev.launcher.utils.EtaFormatter;
import com.fxc.ev.launcher.utils.NavigationTimeParser;
import com.fxc.ev.launcher.utils.SpUtils;
import com.tomtom.navkit2.navigation.Navigation;
import com.tomtom.navkit2.navigation.Route;
import com.tomtom.navkit2.navigation.RouteProgress;
import com.tomtom.navkit2.navigation.Trip;
import com.tomtom.navkit2.navigation.TripPlan;
import com.tomtom.navkit2.place.Coordinate;
import com.tomtom.navkit2.place.Location;

import java.util.GregorianCalendar;
import java.util.List;

public class RoutePreviewFragment extends Fragment {
    public static final String TAG = "RoutePreviewFragment";
    private View mRootView;
    private ImageView mRoutePreviewBack;
    private TextView mAddFav;
    private LinearLayout mAddFavLayout;
    private ImageView mAddFavImage;
    private TextView mPoiName;
    private TextView mPoiAddress;
    private LinearLayout mNavLayout;
    private TextView mNavDistance;
    private TextView mNavTime;
    private RecyclerView mDirectionRecyclerView;
    private SearchResultItem mSearchResultItem;
    private FavEditItem mCurFavEditItem;
    private SearchFragment mSearchFragment;
    private RoutePreviewFragment mRoutePreviewFragment;
    private LauncherActivity launcherActivity;
    private AddFavDialog mAddFavDialog;
    private List<FavEditItem> favEditItemList;

    private Navigation mNavigation;
    private Trip mTrip;
    private TripPlan mTripPlan;
    private Coordinate mCoordinate;
    private NavigationTimeParser mNavigationTimeParser;

    public RoutePreviewFragment(SearchFragment searchFragment) {
        this.mSearchFragment = searchFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.search_route_preview_fragment, container, false);
        launcherActivity = (LauncherActivity) getActivity();
        mRoutePreviewFragment = this;
        favEditItemList = SpUtils.getDataList(launcherActivity, "favorites_edit_item_list", "favorites", FavEditItem.class);
        mCurFavEditItem = createFavEditItem(mSearchResultItem);
        initView();
        initRoutes();
        return mRootView;
    }

    private void initView() {
        mRoutePreviewBack = mRootView.findViewById(R.id.route_preview_back);
        mRoutePreviewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRoutePreviewFragment.getFragmentManager().popBackStack();
            }
        });

        mAddFavLayout = mRootView.findViewById(R.id.fav_add_layout);
        mAddFavImage = mRootView.findViewById(R.id.fav_add_img);
        mAddFav = mRootView.findViewById(R.id.fav_add_text);
        if (isFavorite(mCurFavEditItem)) {
            mAddFavImage.setImageResource(setAddFavIcon(mCurFavEditItem.getImage()));
            mAddFav.setText(mCurFavEditItem.getName());
            mAddFav.setTextColor(Color.parseColor("#418eff"));
        } else {
            mAddFavImage.setImageResource(R.drawable.icon_favorite_add_normal);
            mAddFav.setText("Add to Favorites");
            mAddFav.setTextColor(Color.parseColor("#bfffffff"));
        }
        mAddFavLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFavorite(mCurFavEditItem)) {
                    mAddFavImage.setImageResource(R.drawable.icon_favorite_add_normal);
                    mAddFav.setText("Add to Favorites");
                    mAddFav.setTextColor(Color.parseColor("#bfffffff"));
                    refreshFavList(mCurFavEditItem);
                } else {
                    mAddFavDialog = new AddFavDialog(launcherActivity, mCurFavEditItem);
                    mAddFavDialog.setDeleteBtnVisibility(View.INVISIBLE);
                    mAddFavDialog.showDialog();
                }
            }
        });

        mPoiName = mRootView.findViewById(R.id.poi_name);
        mPoiName.setText(mSearchResultItem.getName());
        mPoiAddress = mRootView.findViewById(R.id.poi_address);
        mPoiAddress.setText(mSearchResultItem.getAddress());
        mNavLayout = mRootView.findViewById(R.id.nav_layout);
        mNavLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mNavTime = mRootView.findViewById(R.id.nav_time);
        mNavDistance = mRootView.findViewById(R.id.nav_distance);
        mDirectionRecyclerView = mRootView.findViewById(R.id.direction_recyclerview);
    }

    private void initRoutes() {
        mNavigationTimeParser = new NavigationTimeParser(launcherActivity);
        mCoordinate = mSearchResultItem.getCoordinate();
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                launcherActivity.displayRoutes(mCoordinate);
            }
        });
        launcherActivity.setOnRouteInfoUpdateListener(new LauncherActivity.OnRouteInfoUpdateListener() {
            @Override
            public void OnRouteInfoUpdate(Route route) {
                String countryCode = launcherActivity.getCurrentCountryCode();
                RouteProgress routeProgress = route.snapshot().progress();
                GregorianCalendar eta = routeProgress.eta();
                DistanceConversions.FormattedDistance fd = DistanceConversions.convert((int) (routeProgress.remainingLengthInCm() / 100), countryCode);

                String remainingRouteLength = fd.distance + " " + fd.unit;

                int trafficDelayInMins = Math.round(routeProgress.remainingTrafficDelayInSeconds() / 60.0f);
                String trafficDelayText = trafficDelayInMins > 0 ? "\u26a0 +" + trafficDelayInMins + "min" : "";

                int remainingTimeInSeconds = routeProgress.remainingTimeInSeconds() + routeProgress.remainingTrafficDelayInSeconds();

                String estimatedArrivalTime = EtaFormatter.toString(eta) + trafficDelayText;
                String arrivalTime = mNavigationTimeParser.parserSecondToTime(remainingTimeInSeconds).substring(2) + " " + estimatedArrivalTime;

                Log.v(TAG, "remainingRouteLength: " + remainingRouteLength);
                Log.v(TAG, "arrivalTime: " + arrivalTime);
                mNavDistance.setText(remainingRouteLength);
                mNavTime.setText(arrivalTime);
            }
        });
    }

    public void setData(SearchResultItem searchResultItem) {
        mSearchResultItem = searchResultItem;
    }

    private FavEditItem createFavEditItem(SearchResultItem searchResultItem) {
        FavEditItem favEditItem = new FavEditItem();
        favEditItem.setName(searchResultItem.getName());
        favEditItem.setAddress(searchResultItem.getAddress());
        favEditItem.setCoordinate(searchResultItem.getCoordinate().toString());
        favEditItem.setDistance(searchResultItem.getDistance());
        return favEditItem;
    }

    private boolean isFavorite(FavEditItem curFavEditItem) {
        for (FavEditItem favEditItem : favEditItemList) {
            if (favEditItem.getCoordinate() != null && favEditItem.getCoordinate().equals(curFavEditItem.getCoordinate())) {
                curFavEditItem.setName(favEditItem.getName());
                curFavEditItem.setImage(favEditItem.getImage());
                return true;
            }
        }
        return false;
    }

    private void refreshFavList(FavEditItem curFavEditItem) {
        for (int i = favEditItemList.size() - 1; i >= 0; i--) {
            FavEditItem favEditItem = favEditItemList.get(i);
            if (favEditItem.getCoordinate() != null && favEditItem.getCoordinate().equals(curFavEditItem.getCoordinate())) {
                if (favEditItemList.size() == 1) {
                    favEditItem.setName(Constants.ADD_FAVORITE);
                    favEditItem.setImage(R.drawable.icon_add_disable);
                    favEditItem.setBackground(R.drawable.fav_item_btn_disable_bg);
                    favEditItem.setTextColor(Constants.textDisableColor);
                    favEditItem.setCoordinate("");
                    favEditItem.setAddress("");
                } else {
                    favEditItem.setImage(0);
                    favEditItem.setTextColor(0);
                    favEditItem.setBackground(0);
                    favEditItemList.remove(favEditItem);
                }
                SpUtils.setDataList(launcherActivity, "favorites_edit_item_list", "favorites", favEditItemList);
            }
        }
    }

    private int setAddFavIcon(int resId) {
        if (resId == R.drawable.icon_home_normal) {
            return R.drawable.icon_home_active;
        } else if (resId == R.drawable.icon_office_normal) {
            return R.drawable.icon_office_active;
        } else if (resId == R.drawable.icon_star_normal) {
            return R.drawable.icon_star_active;
        } else if (resId == R.drawable.icon_heart_normal) {
            return R.drawable.icon_heart_active;
        } else if (resId == R.drawable.icon_pin_normal) {
            return R.drawable.icon_pin_active;
        } else {
            return R.drawable.icon_star_active;
        }
    }

    class AddFavDialog extends FavoriteBaseDialog {

        protected AddFavDialog(LauncherActivity launcherActivity, FavEditItem favEditItem) {
            super(launcherActivity, favEditItem);
        }

        @Override
        public void processDoneEvent() {
            mAddFavImage.setImageResource(setAddFavIcon(mCurFavEditItem.getImage()));
            mAddFav.setText(mCurFavEditItem.getName());
            mAddFav.setTextColor(Color.parseColor("#418eff"));
            mCurFavEditItem.setTextColor(Constants.textEnableColor);
            mCurFavEditItem.setBackground(Constants.favItemEnableBg);
            favEditItemList.add(mCurFavEditItem);
            SpUtils.setDataList(launcherActivity, "favorites_edit_item_list", "favorites", favEditItemList);
        }
    }

}
