package com.fxc.ev.launcher.maps.search;

import static com.tomtom.navkit2.guidance.Instruction.DrivingSide.RIGHT;

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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fxc.ev.launcher.R;
import com.fxc.ev.launcher.activities.LauncherActivity;
import com.fxc.ev.launcher.utils.DistanceConversions;
import com.fxc.ev.launcher.utils.NavigationTimeParser;
import com.fxc.ev.launcher.utils.SpUtils;
import com.fxc.ev.launcher.utils.SpaceItemDecoration;
import com.fxc.ev.launcher.utils.Toaster;
import com.tomtom.navkit2.guidance.Instruction;
import com.tomtom.navkit2.guidance.InstructionBuilder;
import com.tomtom.navkit2.guidance.RoadInformation;
import com.tomtom.navkit2.guidance.Turn;
import com.tomtom.navkit2.navigation.Navigation;
import com.tomtom.navkit2.navigation.Route;
import com.tomtom.navkit2.navigation.RouteProgress;
import com.tomtom.navkit2.navigation.Trip;
import com.tomtom.navkit2.navigation.TripPlan;
import com.tomtom.navkit2.navigation.common.StringVector;
import com.tomtom.navkit2.place.Coordinate;

import java.util.ArrayList;
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
    private ImageView mIconGo;
    private RecyclerView mDirectionRecyclerView;
    private InstructionAdapter mInstructionAdapter;
    private SearchResultItem mSearchResultItem;
    private FavEditItem mCurFavEditItem;
    //private SearchFragment mSearchFragment;
    private RoutePreviewFragment mRoutePreviewFragment;
    private LauncherActivity launcherActivity;
    private AddFavDialog mAddFavDialog;
    private List<FavEditItem> favEditItemList;

    private Navigation mNavigation;
    private Trip mTrip;
    private TripPlan mTripPlan;
    private Coordinate mCoordinate;
    private NavigationTimeParser mNavigationTimeParser;
    private List<Instructions> instructionsList = new ArrayList<>();

    /*public RoutePreviewFragment(SearchFragment searchFragment) {
        this.mSearchFragment = searchFragment;
    }*/

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.search_route_preview_fragment, container, false);
        launcherActivity = (LauncherActivity) getActivity();
        mRoutePreviewFragment = this;
        favEditItemList = SpUtils.getDataList(launcherActivity, "favorites_edit_item_list", "favorites", FavEditItem.class);
        mCurFavEditItem = createFavEditItem(mSearchResultItem);
        mCoordinate = mSearchResultItem.getCoordinate();
        //createInstruction();
        initView();
        initRoutes();
        launcherActivity.setRouteAvoidsLayoutVisibility(View.VISIBLE);//Jerry@20220428 add:show route avoids ui
        return mRootView;
    }

    private void initView() {
        mRoutePreviewBack = mRootView.findViewById(R.id.route_preview_back);
        mRoutePreviewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launcherActivity.hideRoutes();
                mRoutePreviewFragment.getFragmentManager().popBackStack();
                if (mRoutePreviewFragment.getFragmentManager().getBackStackEntryCount() == 1) {
                    launcherActivity.setMapWidgetVisibility(View.VISIBLE);
                }
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

        mIconGo = mRootView.findViewById(R.id.icon_go);
        mIconGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTrip != null) {
                    launcherActivity.startNavigation(mTrip);
                    launcherActivity.getSupportFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                } else {
                    Toaster.show(launcherActivity.getApplicationContext(), R.string.navigation_experience_select_route_message);
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
        /*LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        SpaceItemDecoration spaceItemDecoration = new SpaceItemDecoration();
        mDirectionRecyclerView.addItemDecoration(spaceItemDecoration);
        mDirectionRecyclerView.setLayoutManager(linearLayoutManager);
        mInstructionAdapter = new InstructionAdapter(launcherActivity, instructionsList);
        mDirectionRecyclerView.setAdapter(mInstructionAdapter);*/
    }

    private void initRoutes() {
        mNavigationTimeParser = new NavigationTimeParser(launcherActivity);
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                launcherActivity.displayRoutes(mCoordinate);
            }
        });
        launcherActivity.setOnRouteInfoUpdateListener(new LauncherActivity.OnRouteInfoUpdateListener() {
            @Override
            public void OnRouteInfoUpdate(Trip trip, Route route) {
                mTrip = trip;
                String countryCode = launcherActivity.getCurrentCountryCode();
                RouteProgress routeProgress = route.snapshot().progress();
                GregorianCalendar eta = routeProgress.eta();

                DistanceConversions.FormattedDistance fd = DistanceConversions.convert((int) (routeProgress.remainingLengthInCm() / 100), countryCode);
                String remainingRouteLength = fd.distance + " " + fd.unit;

                int remainingTimeInSeconds = routeProgress.remainingTimeInSeconds() + routeProgress.remainingTrafficDelayInSeconds();
                String arrivalTime = mNavigationTimeParser.parserSecondToTime(remainingTimeInSeconds).substring(2) + " " + mNavigationTimeParser.getCurrentTime(eta.getTime());

                mNavDistance.setText(remainingRouteLength);
                mNavTime.setText(arrivalTime);
            }
        });

        /*launcherActivity.setOnInstructionUpdateListener(new LauncherActivity.OnInstructionUpdateListener() {
            @Override
            public void OnInstructionUpdate(int distanceToInstructionInMeters, List<Instruction> instructionList) {
                launcherActivity.setUpdateInstruction(false);
                if (instructionsList.size() != 0) instructionsList.clear();
                instructionsList.add(new Instructions(distanceToInstructionInMeters, instructionList.get(0)));
                mInstructionAdapter.notifyDataSetChanged();
            }
        });*/
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "onDestroy");
        mTrip = null;
        //launcherActivity.hideRoutes();
        launcherActivity.setRouteAvoidsLayoutVisibility(View.GONE);//Jerry@20220428 add:hide route avoids ui
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
            if (favEditItemList.size() == 1 && favEditItemList.get(0).getName().equals(Constants.ADD_FAVORITE)) {
                favEditItemList.clear();
            }
            favEditItemList.add(mCurFavEditItem);
            SpUtils.setDataList(launcherActivity, "favorites_edit_item_list", "favorites", favEditItemList);
        }
    }

}
