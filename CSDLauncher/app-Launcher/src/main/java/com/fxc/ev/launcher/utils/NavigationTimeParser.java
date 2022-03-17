package com.fxc.ev.launcher.utils;

import android.content.Context;
import android.text.format.DateFormat;

import com.fxc.ev.launcher.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

//Jerry@20220317 add:date parser
public class NavigationTimeParser {
    private Context mContext;

    public NavigationTimeParser(Context mContext) {
        this.mContext = mContext;
    }

    public Date parserDateFormat(String string) {
        if (string == null) return null;

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
        Date date = null;
        try {
            date = simpleDateFormat.parse(string);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date;
    }

    public String dateCompareTo(Date date) {
        if (date == null) return null;
        String dateInfo = null;

        Date todayDate = new Date();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(todayDate);
        int day1 = calendar.get(Calendar.DAY_OF_YEAR);

        calendar.setTime(date);
        int day2 = calendar.get(Calendar.DAY_OF_YEAR);

        if (date != null) {
            int day = day1 - day2;
            switch (day) {
                case 0:
                    if(getTimeFormat()) {
                        dateInfo = getCurrentTime(date);
                    }else{
                        if(Calendar.AM == getTimeStyle(date)){
                            dateInfo = getCurrentTime(date)+" AM";
                        }else {
                            dateInfo = getCurrentTime(date)+" PM";
                        }
                    }
                    break;
                case 1:
                    dateInfo = "昨天";
                    break;
                default:
                    dateInfo = dateToWeek(date);
            }
        }
        return dateInfo;
    }

    public String getCurrentTime(Date date) {
        String time;
        String TIME_FORMAT = "hh:mm";//hh 12h ,HH 24h
        if(getTimeFormat()){
            TIME_FORMAT = "HH:mm";
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(TIME_FORMAT);
        time = simpleDateFormat.format(date);
        return time;
    }

    private boolean getTimeFormat(){
        return DateFormat.is24HourFormat(mContext);
    }

    private int getTimeStyle(Date date) {
        int mStyle;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        mStyle = calendar.get(Calendar.AM_PM);

        return mStyle;
    }

    public String dateToWeek(Date date) {
        String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        //一周的第几天
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        return weekDays[w];
    }

    public String parserSecondToTime(int time) {
        String timeStr = null;
        int hour = 0;
        int minute = 0;
        int second = 0;

        if (time <= 0) {
            return String.format(mContext.getResources().getText(R.string.nip_instruction_remaining_min).toString(),
                    00);
        } else {
            minute = time / 60;
            if (minute < 60) {
                second = time % 60;
                timeStr = String.format(mContext.getResources().getText(R.string.nip_instruction_remaining_min).toString(),
                            minute);
            } else {
                hour = minute / 60;
                if (hour > 99)
                    return String.format(mContext.getResources().getText(R.string.nip_instruction_remaining).toString(),
                            99,59);

                minute = minute % 60;
                second = time - hour * 3600 - minute * 60;
                timeStr = String.format(mContext.getResources().getText(R.string.nip_instruction_remaining).toString(),
                            hour,minute);
            }
        }
        return timeStr;
    }
}
