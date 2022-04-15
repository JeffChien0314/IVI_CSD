package com.fxc.ev.launcher.maps.poicatsearch;

import android.os.Environment;

public class Constants {
    public static boolean IS_DEMO = true;
    public static boolean IS_STORAGE_DOWNLOAD = true;
    public static int RADIUS_IN_METERS= 10000;
    public static int LIMIT = 3;

    public static final float SPEED_MULTIPLIER = 2.0f;

    public static final String STOP_NAVIGATION = "stop navigation";
    public static final String TTS_CONTROL_TOGGLE = "navigation_tts_control";

    public static final String PARKING = "Parking";
    public static final String CHARGING_STATION = "Charging station";
    public static final String SUPERMARKET = "Supermarket";
    public static final String CAFE = "Cafe";
    public static final String RESTAURANT = "Restaurant";
    public static final String HOTEL = "Hotel";
    public static final String ATM = "ATM";
    public static final String GAS_STATION = "Gas station";
    public static final String HOSPITAL = "Hospital";
    public static final String SCHOOL = "School";

    public static String[] ALL_CATEGORY = {PARKING,CHARGING_STATION,SUPERMARKET,CAFE,RESTAURANT,HOTEL,
            ATM,GAS_STATION,HOSPITAL,SCHOOL};

    public static int[] SCALE_TYPE_1KM_2KM = {4,4,3,3,3,3,2,2,1,1};
    public static int[] SCALE_TYPE_2KM_4KM = {4,4,3,2,2,2,2,1,1,1};
    public static int[] SCALE_TYPE_4KM_8KM = {4,4,3,2,2,2,2,1,1,1};
    public static int[] SCALE_TYPE_8KM_10KM = {3,3,2,2,2,2,1,1,1,1};
    public static int[] SCALE_TYPE_10KM_15KM = {3,3,2,2,2,2,1,1,1,1};
    public static int[] SCALE_TYPE_15KM_25KM = {2,2,2,2,1,1,1,1,1,1};
    public static int[] SCALE_TYPE_25KM_35KM = {1,1,1,1,1,1,1,1,1,1};
    public static int[] SCALE_TYPE_35KM_MORE = {0,0,0,0,0,0,0,0,0,0};

    public static final double ONE_KM = 1000;
    public static final double TWO_KM = 2000;
    public static final double FOUR_KM = 4000;
    public static final double EIGHT_KM = 8000;
    public static final double TEN_KM = 10000;
    public static final double FIFTEEN_KM = 15000;
    public static final double TWENTY_FIVW_KM = 25000;
    public static final double THIRTY_FIVE_KM = 35000;

    public static final String MOVE_RIGHT = "move_right";
    public static final String MOVE_LEFT = "move_left";

    public static final String NDS_MAP_STORAGE_PATH = Environment.DIRECTORY_DOWNLOADS;

    public static final int POI_SEARCH_MESSAGE = 0x001;
    public static final int POI_SEARCH_DELAY_MESSAGE = 0x002;
    public static final int POI_SEARCH_DELAY_MILLIS = 1000;
}
