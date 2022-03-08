package com.fxc.ev.launcher.maps.poicatsearch;

import android.content.Context;
import android.os.Bundle;

import com.fxc.ev.launcher.BuildConfig;
import com.tomtom.navkit2.SearchOnboardService;
import com.tomtom.navkit2.search.Search;

public class BundleConfig {
    private Context context;

    public BundleConfig(Context context) {
        this.context = context;
    }

    public Bundle createBundleConfiguration() {
        final Bundle bundle = new Bundle();

        // Disable map access synchronization by setting empty MAPACCESSSYNC_SERVICE_URI
        bundle.putString(SearchOnboardService.MAPACCESSSYNC_SERVICE_URI, "");

        //bundle.putString(SearchOnboardService.MAP_POI_CATEGORY_COLLECTION_ID_KEY, "0");
        bundle.putString(SearchOnboardService.SEARCH_SERVICE_API_KEY, BuildConfig.API_KEY);
        bundle.putString(SearchOnboardService.SEARCH_URI_KEY, "https://api.tomtom.com/search/2/search");
        bundle.putString(Search.SEARCH_API_KEY, BuildConfig.API_KEY);

        return bundle;
    }
}
