package com.fxc.ev.launcher.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.tomtom.navkit2.place.Coordinate;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

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

}
