package com.fxc.ev.zuocang;


import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.widget.RemoteViews;
public class DynamicWidget extends AppWidgetProvider {
    private RemoteViews remoteViews;
    private boolean isDynamicModeSportClick = false;
    private boolean isDynamicModeNormalClick = true;
    private boolean isDynamicModeComfortClick = false;
    private SharedPreferences sp;
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        String action = intent.getAction();
        if (intent.hasCategory(Intent.CATEGORY_ALTERNATIVE)) { //操作widget
            Uri data = intent.getData();
            int buttonId = Integer.parseInt(data.getSchemeSpecificPart());
            switch (buttonId) {
                case R.id.widget_drive_mode_comfort:
                    /*isDynamicModeNormalClick = false;
                    isDynamicModeSportClick = false;
                    isDynamicModeComfortClick = true;*/
                    sp.edit().putInt("DynamicModeClick",1).commit();
                    pushUpdate(context,AppWidgetManager.getInstance(context));
                    break;
                case R.id.widget_drive_mode_normal:
                    /*isDynamicModeNormalClick = true;
                    isDynamicModeSportClick = false;
                    isDynamicModeComfortClick = false;*/
                    sp.edit().putInt("DynamicModeClick",0).commit();
                    pushUpdate(context,AppWidgetManager.getInstance(context));
                    break;
                case R.id.widget_drive_mode_sport:
                    /*isDynamicModeNormalClick = false;
                    isDynamicModeSportClick = true;
                    isDynamicModeComfortClick = false;*/
                    sp.edit().putInt("DynamicModeClick",2).commit();
                    pushUpdate(context,AppWidgetManager.getInstance(context));
                    break;
                case R.id.widget_open:
                    Intent intentShow = new Intent();
                    intentShow.setClass(context, MainActivity.class);
                    intentShow.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intentShow);
                    break;
            }
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        pushUpdate(context,appWidgetManager);
    }

    private void pushUpdate(Context context, AppWidgetManager appWidgetManager) {
        sp=context.getSharedPreferences("user_select",Context.MODE_PRIVATE);
        if(sp.getInt("DynamicModeClick",0)==0) {
            remoteViews = new RemoteViews(context.getPackageName(), R.layout.layout_dynamic_widget_modenormal);
        }else if(sp.getInt("DynamicModeClick",0)==2) {
            remoteViews = new RemoteViews(context.getPackageName(), R.layout.layout_dynamic_widget_modesport);
        }else if(sp.getInt("DynamicModeClick",0)==1){
            remoteViews = new RemoteViews(context.getPackageName(), R.layout.layout_dynamic_widget_modecomfort);
        }
        remoteViews.setOnClickPendingIntent(R.id.widget_drive_mode_comfort, getPendingIntent(context, R.id.widget_drive_mode_comfort));
        remoteViews.setOnClickPendingIntent(R.id.widget_drive_mode_normal, getPendingIntent(context, R.id.widget_drive_mode_normal));
        remoteViews.setOnClickPendingIntent(R.id.widget_drive_mode_sport, getPendingIntent(context, R.id.widget_drive_mode_sport));
       remoteViews.setOnClickPendingIntent(R.id.widget_open, getPendingIntent(context, R.id.widget_open));
 

        ComponentName componentName = new ComponentName(context, DynamicWidget.class);
        appWidgetManager.updateAppWidget(componentName, remoteViews);
    }


    private PendingIntent getPendingIntent(Context context, int buttonId) {
        Intent intent = new Intent();
        intent.setClass(context, DynamicWidget.class);
        intent.addCategory(Intent.CATEGORY_ALTERNATIVE);
        intent.setData(Uri.parse("" + buttonId));
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        return pi;
    }


}
