package com.fxc.ev.launcher.utils;

import static com.fxc.ev.launcher.maps.search.Constants.FROM_MAIN_PAGE;
import static com.fxc.ev.launcher.maps.search.Constants.TYPE_FAVORITE;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.fxc.ev.launcher.R;
import com.fxc.ev.launcher.activities.LauncherActivity;
import com.fxc.ev.launcher.maps.search.FavEditItem;
import com.fxc.ev.launcher.maps.search.RoutePreviewFragment;
import com.fxc.ev.launcher.maps.search.SearchFragment;
import com.fxc.ev.launcher.maps.search.SearchResultItem;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.tomtom.navkit2.place.Coordinate;

import java.util.ArrayList;
import java.util.List;


public class SpUtils {
    /**
     * 使用SharedPreferences保存List
     * 支持类型：List<String>，List<JavaBean>
     *
     * @param context  上下文
     * @param key      储存的key
     * @param dataList 存储数据
     * @param <T>      泛型
     */
    public static <T> void setDataList(Context context, String fileName, String key, List<T> dataList) {
        if (null == dataList || dataList.size() < 0) {
            return;
        }

        SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        //转换成json数据，再保存
        String strJson = gson.toJson(dataList);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, strJson);
        editor.commit();
    }

    /**
     * 获取SharedPreferences保存的List
     *
     * @param context 上下文
     * @param key     储存的key
     * @param <T>     泛型
     * @return 存储List<T>数据
     */
    public static <T> List<T> getDataList(Context context, String fileName, String key, Class<T> cls) {
        SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        List<T> dataList = new ArrayList<T>();
        String strJson = sp.getString(key, null);
        if (null == strJson) {
            return dataList;
        }

        Gson gson = new Gson();
        JsonArray array = new JsonParser().parse(strJson).getAsJsonArray();
        for (JsonElement jsonElement : array) {
            dataList.add(gson.fromJson(jsonElement, cls));
        }

        return dataList;
    }

    public static <T> List<T> clipList(List<T> srcList, int targetSize) {
        if (srcList == null || targetSize <= 0) {
            return null;
        }

        List<T> targetList = new ArrayList<>();
        for (int i = 0; i < ((srcList.size() < targetSize) ? srcList.size() : targetSize); ) {
            targetList.add(srcList.get(i));
            i++;
        }
        return targetList;
    }

    public static Coordinate string2Coordinate(String s) {
        /*Coordinate{Latitude: 25.057740, Longitude: 121.296910, Altitude: 0.000000, IsValid: true}*/
        Log.v("Search", "s: " + s);
        String latitudeStr = s.substring((s.indexOf(":") + 2), s.indexOf(", Longitude"));
        String longitudeStr = s.substring((s.indexOf("Longitude: ") + 11), s.indexOf(", Altitude"));
        String altitudeStr = s.substring((s.indexOf("Altitude: ") + 10), s.indexOf(", IsValid"));
        String isValidStr = s.substring((s.indexOf("IsValid: ") + 9), s.indexOf("}"));

        double latitude = Double.parseDouble(latitudeStr);
        double longitude = Double.parseDouble(longitudeStr);
        double altitude = Double.parseDouble(altitudeStr);
        boolean isValid = Boolean.parseBoolean(isValidStr);

        Log.v("Search", "latitude:" + latitude + ", longitude:" + longitude + ", altitude:" + altitude + ", isValid:" + isValid);

        return new Coordinate(latitude, longitude);
    }

    public static <T> void createFavLayout(LauncherActivity launcherActivity, LinearLayout parentLayout, List<T> dataList) {
        ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) parentLayout.getLayoutParams();
        LinearLayout.LayoutParams itemViewLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        itemViewLp.setMargins(30, 0, 0, 0);

        for (int i = 0; i < dataList.size(); i++) {
            FavEditItem favEditItem = (FavEditItem) dataList.get(i);

            LinearLayout itemLayout = (LinearLayout) LayoutInflater.from(launcherActivity)
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
                    if (TextUtils.isEmpty(favEditItem.getCoordinate())) {
                        SearchFragment searchFragment = new SearchFragment();
                        launcherActivity.setCurrentFragment(searchFragment);
                        new Handler().post(new Runnable() {
                            @Override
                            public void run() {
                                searchFragment.go2SearchFromFavorites(FROM_MAIN_PAGE, (int) v.getTag());
                            }
                        });
                    } else {
                        SearchResultItem searchResultItem = new SearchResultItem();
                        searchResultItem.setCoordinate(SpUtils.string2Coordinate(favEditItem.getCoordinate()));
                        searchResultItem.setName(favEditItem.getName());
                        searchResultItem.setAddress(favEditItem.getAddress());
                        searchResultItem.setDistance(favEditItem.getDistance());
                        searchResultItem.setSearchType(TYPE_FAVORITE);

                        RoutePreviewFragment routePreviewFragment = new RoutePreviewFragment();
                        launcherActivity.setCurrentFragment(routePreviewFragment);
                        routePreviewFragment.setData(searchResultItem);

                        List<FavEditItem> recentList = getDataList(launcherActivity, "recent_list", "recent", FavEditItem.class);
                        for (int i = recentList.size() - 1; i >= 0; i--) {
                            if (recentList.get(i).getCoordinate().equals(favEditItem.getCoordinate())) {
                                recentList.remove(recentList.get(i));
                            }
                        }

                        recentList.add(favEditItem);
                        SpUtils.setDataList(launcherActivity, "recent_list", "recent", recentList);
                    }
                }
            });
            if (i == 1) {
                itemLayout.setLayoutParams(itemViewLp);
            }

            parentLayout.setLayoutParams(lp);
            parentLayout.addView(itemLayout);
        }
    }


}
