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
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fxc.ev.launcher.BuildConfig;
import com.fxc.ev.launcher.R;
import com.fxc.ev.launcher.activities.LauncherActivity;
import com.fxc.ev.launcher.utils.ApplicationPreferences;
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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment  {
    public static final String TAG = "metis.SearchFragment";
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
    private LinearLayout favItemLayout;
    private LinearLayout interestItemLayout;
    private TextView mInterestMore;

    public OnMarkerChangedListener onMarkerChangedListener;

    public interface OnMarkerChangedListener {
        public void onMarkerChange(List<Location> locations);
    }

    public class KeyValuePair {
        public String name;
        public int image;
        public int background;
        public int textColor;

        public KeyValuePair(String name, int image, int background, int textColor) {
            this.name = name;
            this.image = image;
            this.background = background;
            this.textColor = textColor;
        }
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

                                locationList.add(ftsResult.getLocation());
                                searchResultItemArrayList.add(searchResultItem);
                            }
                        }
                        searchResultRecyclerView.setVisibility(View.VISIBLE);
                        favMainLayout.setVisibility(View.GONE);
                    } else {
                        searchResultRecyclerView.setVisibility(View.GONE);
                        favMainLayout.setVisibility(View.VISIBLE);
                    }
                    onMarkerChangedListener.onMarkerChange(locationList);
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
                searchResultItemArrayList.clear();
                searchResultsAdapter.notifyDataSetChanged();
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
                    search(searchEditText.getText().toString());
                } else {
                    clearAllImageView.setVisibility(View.GONE);
                }
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
                RoutePreviewFragment routePreviewFragment = new RoutePreviewFragment();
                launcherActivity.setCurrentFragment(routePreviewFragment);
                routePreviewFragment.setData(searchResultItem);
            }
        });
    }

    private void initFavoritesView() {
        favMainLayout = rootView.findViewById(R.id.fav_main_layout);
        favMore = rootView.findViewById(R.id.favorites_more);
        favMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launcherActivity.setCurrentFragment(new FavoritesEditFragment());
            }
        });

        favItemLayout = rootView.findViewById(R.id.fav_item_layout);
        interestItemLayout = rootView.findViewById(R.id.interest_item_layout);
        mInterestMore = rootView.findViewById(R.id.interest_more);
        mInterestMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launcherActivity.setCurrentFragment(new InterestEditFragment());
            }
        });


        List<KeyValuePair> list1 = new ArrayList<>();
        List<KeyValuePair> list2 = new ArrayList<>();
        list1.add(new KeyValuePair("Home", R.drawable.icon_home_normal, R.drawable.fav_item_btn_disable_bg, R.color.fav_text_disable_color));
        list1.add(new KeyValuePair("Office", R.drawable.icon_office_normal, R.drawable.fav_item_btn_disable_bg, R.color.fav_text_disable_color));
        list1.add(new KeyValuePair("favorites11111111111111", R.drawable.icon_star_normal, R.drawable.fav_item_btn_disable_bg, R.color.fav_text_disable_color));

        list2.add(new KeyValuePair("Parking", R.drawable.icon_parking_normal, R.drawable.interest_parking_bg, R.color.fav_text_enable_color));
        list2.add(new KeyValuePair("Charging station", R.drawable.icon_charge_staiotn_normal, R.drawable.interest_charging_station_bg, R.color.fav_text_enable_color));
        list2.add(new KeyValuePair("Supermarket", R.drawable.icon_market_normal, R.drawable.interest_market_bg, R.color.fav_text_enable_color));
        list2.add(new KeyValuePair("Cafe", R.drawable.icon_cafe_normal, R.drawable.interest_market_bg, R.color.fav_text_enable_color));
        list2.add(new KeyValuePair("Restaurant", R.drawable.icon_restaurant_normal, R.drawable.interest_restaurant_bg, R.color.fav_text_enable_color));

        initAutoLinearLayout(favItemLayout, list1);
        initAutoLinearLayout(interestItemLayout, list2);

    }

    private void initAutoLinearLayout(LinearLayout parentLayout, List<KeyValuePair> dataList) {
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
            if (isNewLayout) {
                parentLayout.addView(rowLinearLayout);
                rowLinearLayout = new LinearLayout(launcherActivity);
                rowLinearLayout.setLayoutParams(rowLp);
                isNewLayout = false;
            }

            LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(launcherActivity).inflate(R.layout.favorites_item, null);
            TextView favName = linearLayout.findViewById(R.id.fav_name);
            ImageView favImg = linearLayout.findViewById(R.id.fav_img);

            favName.setText(dataList.get(i).name);
            favName.setTextColor(launcherActivity.getResources().getColor(dataList.get(i).textColor));
            favImg.setImageResource(dataList.get(i).image);
            linearLayout.setBackgroundResource(dataList.get(i).background);

            int textLength = (int) favName.getPaint().measureText(favName.getText().toString());
            int layoutWith = 72 + textLength;
            Log.v(TAG, "textLength:" + textLength + ",layoutWith: " + layoutWith);

            if (maxWith < layoutWith) {
                parentLayout.addView(rowLinearLayout);
                rowLinearLayout = new LinearLayout(launcherActivity);
                rowLinearLayout.setLayoutParams(rowLp);
                rowLinearLayout.addView(linearLayout);
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
                    linearLayout.setLayoutParams(itemViewLp);
                }
                rowLinearLayout.addView(linearLayout);
            }
        }
        //添加最后一行，但要防止重复添加
        parentLayout.removeView(rowLinearLayout);
        parentLayout.addView(rowLinearLayout);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        search.close();
    }
}
