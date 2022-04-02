package com.fxc.ev.launcher.maps.search;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fxc.ev.launcher.BuildConfig;
import com.fxc.ev.launcher.R;
import com.fxc.ev.launcher.activities.LauncherActivity;
import com.fxc.ev.launcher.utils.ApplicationPreferences;
import com.fxc.ev.launcher.utils.SpUtils;
import com.fxc.ev.launcher.utils.SpaceItemDecoration;
import com.tomtom.navkit2.PoiDetailsOnboardService;
import com.tomtom.navkit2.SearchOnboardService;
import com.tomtom.navkit2.drivingassistance.Position;
import com.tomtom.navkit2.place.Coordinate;
import com.tomtom.navkit2.place.Location;
import com.tomtom.navkit2.search.FilterByGeoRadius;
import com.tomtom.navkit2.search.FtsResult;
import com.tomtom.navkit2.search.FtsResultVector;
import com.tomtom.navkit2.search.FtsResults;
import com.tomtom.navkit2.search.FtsResultsListener;
import com.tomtom.navkit2.search.Input;
import com.tomtom.navkit2.search.PoiSuggestionResults;
import com.tomtom.navkit2.search.PoiSuggestionsListener;
import com.tomtom.navkit2.search.Search;

import static com.fxc.ev.launcher.maps.search.Constants.FROM_FAV_EDIT_PAGE;
import static com.fxc.ev.launcher.maps.search.Constants.FROM_SEARCH_PAGE;
import static com.fxc.ev.launcher.maps.search.Constants.TYPE_FAVORITE;
import static com.fxc.ev.launcher.maps.search.Constants.TYPE_SEARCH;
import static com.fxc.ev.launcher.maps.search.Constants.atmBg;
import static com.fxc.ev.launcher.maps.search.Constants.atmIcon;
import static com.fxc.ev.launcher.maps.search.Constants.cafeBg;
import static com.fxc.ev.launcher.maps.search.Constants.cafeIcon;
import static com.fxc.ev.launcher.maps.search.Constants.chargingStationBg;
import static com.fxc.ev.launcher.maps.search.Constants.chargingStationIcon;
import static com.fxc.ev.launcher.maps.search.Constants.favItemDisableBg;
import static com.fxc.ev.launcher.maps.search.Constants.favItemEnableBg;
import static com.fxc.ev.launcher.maps.search.Constants.gasStationBg;
import static com.fxc.ev.launcher.maps.search.Constants.gasStationIcon;
import static com.fxc.ev.launcher.maps.search.Constants.homeDisableIcon;
import static com.fxc.ev.launcher.maps.search.Constants.homeEnableIcon;
import static com.fxc.ev.launcher.maps.search.Constants.hospitalIcon;
import static com.fxc.ev.launcher.maps.search.Constants.hotelBg;
import static com.fxc.ev.launcher.maps.search.Constants.hotelIcon;
import static com.fxc.ev.launcher.maps.search.Constants.marketBg;
import static com.fxc.ev.launcher.maps.search.Constants.marketIcon;
import static com.fxc.ev.launcher.maps.search.Constants.officeDisableIcon;
import static com.fxc.ev.launcher.maps.search.Constants.officeEnableIcon;
import static com.fxc.ev.launcher.maps.search.Constants.parkingBg;
import static com.fxc.ev.launcher.maps.search.Constants.parkingIcon;
import static com.fxc.ev.launcher.maps.search.Constants.restaurantBg;
import static com.fxc.ev.launcher.maps.search.Constants.restaurantIcon;
import static com.fxc.ev.launcher.maps.search.Constants.schoolIcon;
import static com.fxc.ev.launcher.maps.search.Constants.textDisableColor;
import static com.fxc.ev.launcher.maps.search.Constants.textEnableColor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {
    public static final String TAG = "SearchFragment";
    private View rootView;
    private EditText searchEditText;
    private ImageView clearAllImageView;
    private RecyclerView searchResultRecyclerView;
    private SearchResultsAdapter searchResultsAdapter;
    private Search search;
    private LauncherActivity launcherActivity;
    //private FtsResultVector ftsResultVector;
    private ArrayList<SearchResultItem> searchResultItemArrayList = new ArrayList<>();
    List<Location> locationList = new ArrayList<>();
    private int STEP_INIT_SEARCH = 0x1000;

    //metis@0315 add for favorites
    private LinearLayout favMainLayout;
    private TextView favMore;
    private RecyclerView favRecyclerView;
    private LinearLayout favItemParentLayout;
    private LinearLayout interestItemParentLayout;
    private LinearLayout itemLayout;
    private TextView mInterestMore;
    private List<FavEditItem> mFavEditItemList = new ArrayList<>();
    private List<FavEditItem> mInterestEditItemList = new ArrayList<>();
    private InputMethodManager mInputMethodManager;
    private String mSearchType = TYPE_SEARCH;
    private String mSourceType = "";
    private int clickIndex;
    /*public static final String TYPE_SEARCH = "search";
    public static final String TYPE_FAVORITE = "favorite";*/

    public OnMarkerChangedListener onMarkerChangedListener;

    public interface OnMarkerChangedListener {
        public void onMarkerChange(List<Location> locations);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what == STEP_INIT_SEARCH) {
                FtsResultVector ftsResultVector = (FtsResultVector) msg.obj;
                if (ftsResultVector != null) {
                    searchResultItemArrayList.clear();
                    locationList.clear();
                    if (ftsResultVector.size() != 0) {
                        for (FtsResult ftsResult : ftsResultVector) {
                            if (!TextUtils.isEmpty(ftsResult.getPoiName())) {
                                SearchResultItem searchResultItem = new SearchResultItem();
                                searchResultItem.setName(ftsResult.getPoiName());
                                searchResultItem.setAddress(ftsResult.getAddress());
                                searchResultItem.setDistance(ftsResult.getDistance());
                                searchResultItem.setPhoneNums(ftsResult.getPoiPhoneNumbers());
                                searchResultItem.setLocation(ftsResult.getLocation());
                                searchResultItem.setSearchType(mSearchType);

                                locationList.add(ftsResult.getLocation());
                                searchResultItemArrayList.add(searchResultItem);
                            }
                        }
                        searchResultRecyclerView.setVisibility(View.VISIBLE);
                        favMainLayout.setVisibility(View.GONE);
                        launcherActivity.setDisappearance(true);
                    } else {
                        searchResultRecyclerView.setVisibility(View.GONE);
                        favMainLayout.setVisibility(View.VISIBLE);
                        launcherActivity.setDisappearance(false);
                    }

                    if (mSearchType.equals(TYPE_SEARCH)) {
                        onMarkerChangedListener.onMarkerChange(locationList);
                    }
                    searchResultsAdapter.notifyDataSetChanged();
                }
            }
        }
    };

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            onMarkerChangedListener = (OnMarkerChangedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnMarkerChangedListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.search_fragment, container, false);
        launcherActivity = (LauncherActivity) getActivity();
        search = new Search(getContext(), createSearchConfiguration());
        mInputMethodManager = (InputMethodManager) launcherActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        initViews();
        return rootView;
    }

    private void initViews() {
        initSearchEditView();
        initFavoritesView();
    }

    //metis@0315 初始化搜索输入框view
    private void initSearchEditView() {
        searchEditText = rootView.findViewById(R.id.search_edit_text);
        clearAllImageView = rootView.findViewById(R.id.clear_all);
        clearAllImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchEditText.setText("");
                searchEditText.setHint("搜索");

                searchResultRecyclerView.setVisibility(View.GONE);
                favMainLayout.setVisibility(View.VISIBLE);
            }
        });
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(searchEditText.getText().toString())) {
                    clearAllImageView.setVisibility(View.VISIBLE);
                } else {
                    clearAllImageView.setVisibility(View.GONE);
                }
                search(searchEditText.getText().toString());
            }
        });

        searchResultRecyclerView = rootView.findViewById(R.id.search_results_recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        SpaceItemDecoration spaceItemDecoration = new SpaceItemDecoration();
        searchResultRecyclerView.addItemDecoration(spaceItemDecoration);
        searchResultRecyclerView.setLayoutManager(linearLayoutManager);
        searchResultsAdapter = new SearchResultsAdapter(getContext(), searchResultItemArrayList);
        searchResultRecyclerView.setAdapter(searchResultsAdapter);

        searchResultsAdapter.setItemClickListener(new SearchResultsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(SearchResultItem searchResultItem) {
                searchEditText.setText("");
                if (searchResultItem.getSearchType().equals(TYPE_SEARCH)) {
                    RoutePreviewFragment routePreviewFragment = new RoutePreviewFragment();
                    launcherActivity.setCurrentFragment(routePreviewFragment);
                    routePreviewFragment.setData(searchResultItem);
                } else if (searchResultItem.getSearchType().equals(TYPE_FAVORITE)) {
                    mSearchType = TYPE_SEARCH; //reset mSearchType value to search
                    updateItemData(clickIndex, mFavEditItemList, favItemEnableBg, textEnableColor, searchResultItem);
                    SpUtils.setDataList(launcherActivity, "favorites_edit_item_list", "favorites", mFavEditItemList);

                    if (mSourceType.equals(FROM_SEARCH_PAGE)) {
                        favItemParentLayout.removeAllViews();
                        initAutoLinearLayout(favItemParentLayout, SpUtils.clipList(mFavEditItemList, 5));
                        searchResultRecyclerView.setVisibility(View.GONE);
                        favMainLayout.setVisibility(View.VISIBLE);
                        hideSoftInput();
                    } else if (mSourceType.equals(FROM_FAV_EDIT_PAGE)) {
                        FavoritesEditFragment favoritesEditFragment = new FavoritesEditFragment();
                        launcherActivity.setCurrentFragment(favoritesEditFragment);
                    }
                }
            }
        });
    }

    private void initFavoritesView() {
        favMainLayout = rootView.findViewById(R.id.fav_main_layout);
        favItemParentLayout = rootView.findViewById(R.id.fav_item_layout);
        interestItemParentLayout = rootView.findViewById(R.id.interest_item_layout);

        favMore = rootView.findViewById(R.id.favorites_more);
        favMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launcherActivity.setCurrentFragment(new FavoritesEditFragment());
            }
        });

        mInterestMore = rootView.findViewById(R.id.interest_more);
        mInterestMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launcherActivity.setCurrentFragment(new InterestEditFragment());
            }
        });

        mFavEditItemList = SpUtils.getDataList(launcherActivity, "favorites_edit_item_list", "favorites", FavEditItem.class);
        Log.v(TAG, "mFavEditItemList:" + mFavEditItemList.size());
        if (mFavEditItemList.size() == 0) {
            mFavEditItemList.add(new FavEditItem("Home", homeDisableIcon, favItemDisableBg, textDisableColor, null, "Set Location"));
            mFavEditItemList.add(new FavEditItem("Office", officeDisableIcon, favItemDisableBg, textDisableColor, null, "Set Location"));
            SpUtils.setDataList(launcherActivity, "favorites_edit_item_list", "favorites", mFavEditItemList);
        }

        mInterestEditItemList = SpUtils.getDataList(launcherActivity, "interest_edit_list", "interest", FavEditItem.class);
        if (mInterestEditItemList.size() == 0) {
            mInterestEditItemList.add(new FavEditItem("Parking", parkingIcon, parkingBg, textEnableColor, null, null));
            mInterestEditItemList.add(new FavEditItem("Charging station", chargingStationIcon, chargingStationBg, textEnableColor, null, null));
            mInterestEditItemList.add(new FavEditItem("Supermarket", marketIcon, marketBg, textEnableColor, null, null));
            mInterestEditItemList.add(new FavEditItem("Cafe", cafeIcon, cafeBg, textEnableColor, null, null));
            mInterestEditItemList.add(new FavEditItem("Restaurant", restaurantIcon, restaurantBg, textEnableColor, null, null));
            mInterestEditItemList.add(new FavEditItem("Hotel", hotelIcon, hotelBg, textEnableColor, null, null));
            mInterestEditItemList.add(new FavEditItem("ATM", atmIcon, atmBg, textEnableColor, null, null));
            mInterestEditItemList.add(new FavEditItem("Gas Station", gasStationIcon, gasStationBg, textEnableColor, null, null));
            //mInterestEditItemList.add(new FavEditItem("School", schoolIcon, atmBg, textEnableColor, null, null));
            //mInterestEditItemList.add(new FavEditItem("Hospital", hospitalIcon, gasStationBg, textEnableColor, null, null));

            SpUtils.setDataList(launcherActivity, "interest_edit_list", "interest", mInterestEditItemList);
        }

        initAutoLinearLayout(favItemParentLayout, SpUtils.clipList(mFavEditItemList, 5));
        initAutoLinearLayout(interestItemParentLayout, SpUtils.clipList(mInterestEditItemList, 5));
    }

    private <T> void initAutoLinearLayout(LinearLayout parentLayout, List<T> dataList) {
        boolean isNewLayout = false;
        int maxWith = 450;
        int elseWith = maxWith; //剩下的宽度

        LinearLayout rowLinearLayout = new LinearLayout(launcherActivity);
        LinearLayout.LayoutParams rowLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rowLp.setMargins(0, 18, 0, 0);
        rowLinearLayout.setLayoutParams(rowLp);

        LinearLayout.LayoutParams itemViewLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        itemViewLp.setMargins(6, 0, 0, 0);

        for (int i = 0; i < dataList.size(); i++) {
            FavEditItem favEditItem = (FavEditItem) dataList.get(i);
            if (isNewLayout) {
                parentLayout.addView(rowLinearLayout);
                rowLinearLayout = new LinearLayout(launcherActivity);
                rowLinearLayout.setLayoutParams(rowLp);
                isNewLayout = false;
            }

            itemLayout = (LinearLayout) LayoutInflater.from(launcherActivity)
                    .inflate(R.layout.favorites_item, null);
            TextView favName = itemLayout.findViewById(R.id.fav_name);
            ImageView favImg = itemLayout.findViewById(R.id.fav_img);

            favName.setText(favEditItem.getName());
            favName.setTextColor(launcherActivity.getResources().getColor(favEditItem.getTextColor()));
            favImg.setImageResource(favEditItem.getImage());
            itemLayout.setBackgroundResource(favEditItem.getBackground());
            itemLayout.setTag(i);

            itemLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (favEditItem.getLocation() == null) {
                        go2Search(FROM_SEARCH_PAGE, (int) v.getTag());
                    } else {
                        SearchResultItem searchResultItem = new SearchResultItem();
                        searchResultItem.setLocation(favEditItem.getLocation());
                        searchResultItem.setName(favEditItem.getName());

                        RoutePreviewFragment routePreviewFragment = new RoutePreviewFragment();
                        launcherActivity.setCurrentFragment(routePreviewFragment);
                        routePreviewFragment.setData(searchResultItem);
                    }
                }
            });

            int textLength = (int) favName.getPaint().measureText(favName.getText().toString());
            int layoutWith = 72 + textLength;

            if (maxWith < layoutWith) {
                parentLayout.addView(rowLinearLayout);
                rowLinearLayout = new LinearLayout(launcherActivity);
                rowLinearLayout.setLayoutParams(rowLp);
                rowLinearLayout.addView(itemLayout);
                isNewLayout = true;
                continue;
            }

            if (elseWith < layoutWith) {
                isNewLayout = true;
                i--;
                //重置剩余宽度
                elseWith = maxWith;
                continue;
            } else {
                //剩余宽度减去文本框的宽度+间隔=新的剩余宽度
                elseWith -= layoutWith + 18;
                if (rowLinearLayout.getChildCount() != 0) {
                    itemLayout.setLayoutParams(itemViewLp);
                }
                rowLinearLayout.addView(itemLayout);
            }
        }
        //添加最后一行，但要防止重复添加
        parentLayout.removeView(rowLinearLayout);
        parentLayout.addView(rowLinearLayout);
    }

    public void go2Search(String sourceType, int position) {
        searchEditText.requestFocus();
        mSearchType = TYPE_FAVORITE;
        mSourceType = sourceType;
        clickIndex = position;
        showSoftInput();
    }

    private void search(String searchContent) {
        Log.v(TAG, "searchContent:" + searchContent);
        Position curPosition = launcherActivity.getLastKnownPosition();
        Coordinate coordinate = curPosition.place().location().coordinate();
        Log.v(TAG, "coordinate:" + coordinate);

        search.cancel();
        search.query(createInputConfiguration(searchContent, coordinate), new FtsResultsListener() {
            @Override
            public void onFtsResults(@NonNull FtsResults ftsResults) {
                Log.v(TAG, "ftsResults:" + ftsResults.getResults());
                Message msg = new Message();
                msg.what = STEP_INIT_SEARCH;
                msg.obj = ftsResults.getResults();
                if (handler.hasMessages(STEP_INIT_SEARCH)) {
                    handler.removeMessages(STEP_INIT_SEARCH);
                }
                handler.sendMessage(msg);
            }
        }, new PoiSuggestionsListener() {
            @Override
            public void onPoiSuggestionResults(@NonNull PoiSuggestionResults poiSuggestionResults) {
                Log.v(TAG, "poiSuggestionResults:" + poiSuggestionResults);
            }
        });
    }

    private Input createInputConfiguration(String searchText, Coordinate coordinate) {
        Input input = new Input.Builder()
                .setLanguage("zh-TW")
                .setSearchString(searchText)
                .setFilterByGeoRadius(new FilterByGeoRadius(coordinate, 50 * 1000))
                .setFuzzyLevel(5)
                .setLimit(20)
                .build();
        return input;
    }

    private Bundle createSearchConfiguration() {
        Bundle bundle = new Bundle();

        File ndsMapRootFolder = new File(getContext().getExternalFilesDir(null), ApplicationPreferences.NDS_MAP_ROOT_RELATIVE_PATH);
        bundle.putString(SearchOnboardService.MAP_PATH_KEY, ndsMapRootFolder.getPath());

        final File keystorePath = new File(getContext().getExternalFilesDir(null), ApplicationPreferences.NDS_MAP_KEYSTORE_RELATIVE_PATH);
        if (keystorePath.exists()) {
            bundle.putString(SearchOnboardService.MAP_KEYSTORE_PATH_KEY, keystorePath.getPath());
            bundle.putString(SearchOnboardService.MAP_KEYSTORE_PASSWORD_KEY, ApplicationPreferences.getKeystorePassword());
        } else {
            bundle.putString(SearchOnboardService.MAP_KEYSTORE_PATH_KEY, "");
            bundle.putString(SearchOnboardService.MAP_KEYSTORE_PASSWORD_KEY, "");
        }

        // Disable map access synchronization by setting empty MAPACCESSSYNC_SERVICE_URI
        bundle.putString(SearchOnboardService.MAPACCESSSYNC_SERVICE_URI, "");
        bundle.putString(PoiDetailsOnboardService.SEARCH_SERVICE_API_KEY, BuildConfig.API_KEY);

        return bundle;
    }

    private <T> List<T> updateItemData(int updateIndex, List<T> list, int background, int textColor, SearchResultItem searchResultItem) {
        FavEditItem favEditItem = (FavEditItem) list.get(updateIndex);
        if (favEditItem.getName().equals("Home")) {
            favEditItem.setImage(homeEnableIcon);
        } else if (favEditItem.getName().equals("Office")) {
            favEditItem.setImage(officeEnableIcon);
        } else if (favEditItem.getName().equals(Constants.ADD_FAVORITE)) {
            favEditItem.setImage(R.drawable.icon_star_normal);
            favEditItem.setName(searchResultItem.getName());
        }
        favEditItem.setBackground(background);
        favEditItem.setTextColor(textColor);
        favEditItem.setLocation(searchResultItem.getLocation());
        favEditItem.setAddress(searchResultItem.getAddress());

        return list;
    }

    private void showSoftInput() {
        if (mInputMethodManager != null) {
            mInputMethodManager.showSoftInput(searchEditText, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    private void hideSoftInput() {
        if (mInputMethodManager != null && mInputMethodManager.isActive()) {
            mInputMethodManager.hideSoftInputFromWindow(launcherActivity.getWindow().getDecorView().getWindowToken(), 0);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.v(TAG,"onStop");
        hideSoftInput();
        launcherActivity.setDisappearance(false);
        searchResultItemArrayList.clear();
        locationList.clear();
        if (mSearchType.equals(TYPE_SEARCH)) {
            onMarkerChangedListener.onMarkerChange(locationList);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        search.close();
        mSearchType = TYPE_SEARCH;//reset mSearchType value to search
    }
}
