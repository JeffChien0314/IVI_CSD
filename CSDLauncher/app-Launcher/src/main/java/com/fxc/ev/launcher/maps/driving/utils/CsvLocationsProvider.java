/**
 * Copyright (c) 2015-2021 TomTom N.V. All rights reserved.
 *
 * This software is the proprietary copyright of TomTom N.V. and its subsidiaries and may be used
 * for internal evaluation purposes or commercial use strictly subject to separate licensee
 * agreement between you and TomTom. If you are the licensee, you are only permitted to use
 * this Software in accordance with the terms of your license agreement. If you are not the
 * licensee then you are not authorised to use this software in any manner and should
 * immediately return it to TomTom N.V.
 */
package com.fxc.ev.launcher.maps.driving.utils;

import android.content.Context;
import android.location.Location;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class CsvLocationsProvider implements LocationsProvider {

    private final static int TIME_COL_IDX = 0;
    private final static int PROVIDER_COL_IDX = 1;
    private final static int LATITUDE_COL_IDX = 2;
    private final static int LONGITUDE_COL_IDX = 3;
    private final static int ACCURACY_COL_IDX = 4;
    private final static int BEARING_COL_IDX = 5;
    private final static int SPEED_COL_IDX = 7;
    private final static int ALTITUDE_COL_IDX = 9;
    private final static String CSV_SEPARATOR = ",";
    private final Context context;
    private final String assetFileName;
    private final int numberOfHeaderLines;


    public CsvLocationsProvider(Context context, String assetFileName, int numberOfHeaderLines) {
        this.context = context;
        this.assetFileName = assetFileName;
        this.numberOfHeaderLines = numberOfHeaderLines;
    }

    @Override
    public List<Location> getLocations() {
        return parseFromAssets();
    }

    private List<Location> parseFromAssets() {

        List<Location> result = new ArrayList<>();

        try (InputStream fileStream = getInputStreamForAsset();
             BufferedReader fileReader = new BufferedReader(new InputStreamReader(fileStream))) {

            //Skip header lines
            for (int i = 0; i < numberOfHeaderLines; i++) {
                fileReader.readLine();
            }

            //Read and parse gps data
            String fileLine = fileReader.readLine();
            while (fileLine != null) {
                result.add(parseLine(fileLine));
                fileLine = fileReader.readLine();
            }

        } catch (IOException e) {
            Timber.e(e.getMessage());
        }

        return result;
    }

    private Location parseLine(String line) {

        String[] tokens = line.split(CSV_SEPARATOR);
        if (tokens.length == 0) {
            throw new IllegalArgumentException("CSV file line format incorrect");
        }

        Location location = new Location(tokens[PROVIDER_COL_IDX]);
        location.setTime(Long.valueOf(tokens[TIME_COL_IDX]));
        location.setLatitude(Double.valueOf(tokens[LATITUDE_COL_IDX]));
        location.setLongitude(Double.valueOf(tokens[LONGITUDE_COL_IDX]));
        location.setAccuracy(Float.valueOf(tokens[ACCURACY_COL_IDX]));
        location.setBearing(Float.valueOf(tokens[BEARING_COL_IDX]));
        location.setSpeed(Float.valueOf(tokens[SPEED_COL_IDX]));
        location.setAltitude(Double.valueOf(tokens[ALTITUDE_COL_IDX]));

        return location;
    }

    private InputStream getInputStreamForAsset() throws IOException {
        return context.getAssets().open(assetFileName);
    }
}