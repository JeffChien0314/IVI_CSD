package com.fxc.ev.launcher.utils.view;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.fxc.ev.launcher.R;
import com.fxc.ev.launcher.maps.poicatsearch.Constants;
import com.fxc.ev.launcher.utils.NavigationTimeParser;

import java.util.Date;

//Jerry@20220317 add
public class OutlineView extends LinearLayout {
    private TextView nipOutlineDistance;
    private TextView nipOutlineTimeRemaining;
    private TextView nipOutlineTime;
    private ImageView nipEndNavigationToggle;
    private Button nipEndNavigation;
    private LinearLayout nipEndNavigationLayout;
    private ConstraintLayout nipRootContainer;
    private NavigationTimeParser navigationTimeParser;
    private Context mContext;

    public OutlineView(Context context) {
        this(context, null);
    }

    public OutlineView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OutlineView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        inflate(context, R.layout.outline_view, this);
        nipEndNavigation = findViewById(R.id.nipEndNavigation);
        nipOutlineDistance = findViewById(R.id.nipOutlineDistance);
        nipOutlineTimeRemaining = findViewById(R.id.nipOutlineTimeRemaining);
        nipOutlineTime = findViewById(R.id.nipOutlineTime);
        nipEndNavigationToggle = findViewById(R.id.nipEndNavigationToggle);
        nipEndNavigationLayout = findViewById(R.id.nipEndNavigationLayout);
        nipRootContainer = findViewById(R.id.nipRootContainer);

        navigationTimeParser = new NavigationTimeParser(context);

        addListener();
    }

    private void addListener() {
        nipRootContainer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setLayoutVisibility();
            }
        });
        nipEndNavigationToggle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setLayoutVisibility();
            }
        });
        nipEndNavigation.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.sendBroadcast(new Intent(Constants.STOP_NAVIGATION));
                nipEndNavigationLayout.setVisibility(GONE);
            }
        });
    }

    public void updateTripInfo(String distance, int timeRemaining, Date date) {
        nipOutlineDistance.setText(distance);
        nipOutlineTimeRemaining.setText(navigationTimeParser.parserSecondToTime(timeRemaining));
        nipOutlineTime.setText(navigationTimeParser.getCurrentTime(date));
    }

    private void setLayoutVisibility(){
        if (VISIBLE != nipEndNavigationLayout.getVisibility()) {
            nipEndNavigationLayout.setVisibility(VISIBLE);
            nipEndNavigationToggle.setBackgroundResource(R.drawable.icon_step_collapse_normal);
        } else {
            nipEndNavigationLayout.setVisibility(GONE);
            nipEndNavigationToggle.setBackgroundResource(R.drawable.icon_step_expand_normal);
        }
    }
}
