package com.fxc.ev.launcher.activities;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import com.fxc.ev.launcher.bean.PakageMod;

import java.util.ArrayList;
import java.util.List;

public class GetAllApps {
    private Context mContext;
    private PackageManager packageManager;
    private int mIconDpi;
    private List<PakageMod> datas = new ArrayList<>();

    public GetAllApps(Context context) {
        this.mContext = context;
        ActivityManager activityManager =
                (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        packageManager = context.getPackageManager();
        mIconDpi = activityManager.getLauncherLargeIconDensity();
    }

    public void loadAllAppsByBatch() {
        List<ResolveInfo> apps = null;
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        apps = packageManager.queryIntentActivities(mainIntent, 0);
        for (int i = 0; i < apps.size(); i++) {
            String packageName = apps.get(i).activityInfo.applicationInfo.packageName;
            String title = apps.get(i).loadLabel(packageManager).toString();
            Drawable icon = null;
            if (title == null) {
                title = apps.get(i).activityInfo.name;
            }
            ActivityInfo info = apps.get(i).activityInfo;
            icon = getFullResIcon(info);
            datas.add(new PakageMod(packageName, title, icon,null));
        }
    }

    public Drawable getFullResIcon(ActivityInfo info) {
        Resources resources;
        try {
            resources = packageManager.getResourcesForApplication(
                    info.applicationInfo);
        } catch (PackageManager.NameNotFoundException e) {
            resources = null;
        }
        if (resources != null) {
            int iconId = info.getIconResource();
            if (iconId != 0) {
                return getFullResIcon(resources, iconId);
            }
        }
        return getFullResDefaultActivityIcon();
    }

    public Drawable getFullResDefaultActivityIcon() {
        return getFullResIcon(Resources.getSystem(),
                android.R.mipmap.sym_def_app_icon);
    }

    public Drawable getFullResIcon(Resources resources, int iconId) {
        Drawable d;
        try {
            // requires API level 15
            d = resources.getDrawableForDensity(iconId, mIconDpi);
        } catch (Resources.NotFoundException e) {
            d = null;
        }

        return (d != null) ? d : getFullResDefaultActivityIcon();
    }

    public List<PakageMod> getDatas() {
        loadAllAppsByBatch();
        return datas;
    }

}
