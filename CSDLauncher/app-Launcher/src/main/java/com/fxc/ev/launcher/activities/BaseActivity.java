/**
 * Copyright (c) 2015-2021 TomTom N.V. All rights reserved.
 * <p>
 * This software is the proprietary copyright of TomTom N.V. and its subsidiaries and may be used
 * for internal evaluation purposes or commercial use strictly subject to separate licensee
 * agreement between you and TomTom. If you are the licensee, you are only permitted to use
 * this Software in accordance with the terms of your license agreement. If you are not the
 * licensee then you are not authorised to use this software in any manner and should
 * immediately return it to TomTom N.V.
 */
package com.fxc.ev.launcher.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.fxc.ev.launcher.R;

public class BaseActivity extends AppCompatActivity {

    public RelativeLayout mMainLayout;
    public FrameLayout mContentContainer;
    //public LinearLayout mToolMenuLayout;
    protected Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//metis add

        mContext = this;
        mMainLayout = findViewById(R.id.main_layout);
        //mToolMenuLayout = findViewById(R.id.tool_menu_layout);
        mContentContainer = findViewById(R.id.content_container);

        initContentContainerView();
    }

    protected void initContentContainerView() {

    }

}
