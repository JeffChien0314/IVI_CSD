/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.systemui.statusbar.car;

import android.content.Context;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.BroadcastReceiver;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageView;

import com.android.systemui.Dependency;
import com.android.systemui.R;
import com.android.systemui.statusbar.phone.StatusBarIconController;
import static android.content.Context.WINDOW_SERVICE;
import android.car.userlib.CarUserManagerHelper;
import android.os.UserManager;
import android.content.pm.UserInfo;
import android.app.ActivityManager;
import android.os.RemoteException;

/**
 * A custom navigation bar for the automotive use case.
 * <p>
 * The navigation bar in the automotive use case is more like a list of shortcuts, rendered
 * in a linear layout.
 */
class CarNavigationBarView extends LinearLayout {
    private View mNavButtons;
    private CarNavigationButton mNotificationsButton;
    private CarStatusBar mCarStatusBar;
    private Context mContext;
    private View mLockScreenButtons;
    // used to wire in open/close gestures for notifications
    private OnTouchListener mStatusBarWindowTouchListener;

    private CarUserManagerHelper mCarUserManagerHelper;

	private View mUserInfo;
	private LocalBroadcastManager localBroadcastManager;
	private BroadcastReceiver mUsernameChangeReceiver;

    //EV温控
    private LinearLayout mEvClimateLayout;
    private ImageView mLeftTemperatureUp;
    private ImageView mLeftTemperatureDown;
    private CarFacetButton mLeftTemperature;
    private ImageView mLeftAirUp;
    private ImageView mLeftAirDown;
    private CarFacetButton mLeftAir;
    private ImageView mAirCirculation;
    private ImageView mFogRemoval;
    private ImageView mRightAirUp;
    private ImageView mRightAirDown;
    private CarFacetButton mRightAir;
    private ImageView mRightTemperatureUp;
    private ImageView mRightTemperatureDown;
    private CarFacetButton mRightTemperature;   
    


    public CarNavigationBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mCarUserManagerHelper=new CarUserManagerHelper(mContext);
		
		localBroadcastManager=LocalBroadcastManager.getInstance(mContext);
		mUsernameChangeReceiver=new UsernameChangeReceiver();
		localBroadcastManager.registerReceiver(mUsernameChangeReceiver, new IntentFilter("user_name_change"));
    }

    @Override
    public void onFinishInflate() {
        mNavButtons = findViewById(R.id.nav_buttons);
        mLockScreenButtons = findViewById(R.id.lock_screen_nav_buttons);

        mNotificationsButton = findViewById(R.id.notifications);
        if (mNotificationsButton != null) {
            mNotificationsButton.setOnClickListener(this::onNotificationsClick);
        }
        View mStatusIcons = findViewById(R.id.statusIcons);
        if (mStatusIcons != null) {
            // Attach the controllers for Status icons such as wifi and bluetooth if the standard
            // container is in the view.
            StatusBarIconController.DarkIconManager mDarkIconManager =
                    new StatusBarIconController.DarkIconManager(
                            mStatusIcons.findViewById(R.id.statusIcons));
            mDarkIconManager.setShouldLog(true);
            Dependency.get(StatusBarIconController.class).addIconGroup(mDarkIconManager);
        }
        // needs to be clickable so that it will receive ACTION_MOVE events

        mUserInfo = findViewById(R.id.user_info);
        if (mUserInfo != null) {
            //int userId= mCarUserManagerHelper.getCurrentProcessUserId();
            //UserManager userManager= (UserManager)mContext.getSystemService(Context.USER_SERVICE);
            //UserInfo userInfo=userManager.getUserInfo(userId);
            UserInfo userInfo = null;
            try {
                userInfo = ActivityManager.getService().getCurrentUser();
            } catch (RemoteException re) {

            }
            if (userInfo != null) {
                ((TextView) findViewById(R.id.user_name)).setText(userInfo.name);
                Drawable icon = userInfo.iconPath != null ? Drawable.createFromPath(userInfo.iconPath) : null;
                if (icon != null) {
                    ((ImageView) findViewById(R.id.user_icon)).setImageDrawable(icon);
                } else {
                    ((ImageView) findViewById(R.id.user_icon)).setImageDrawable(mContext.getDrawable(R.drawable.ic_menu_cc_am));
                }

            }
        }        
        setClickable(true);
        mEvClimateLayout = findViewById(R.id.ev_climate_layout);
        if (mEvClimateLayout != null) {
            mLeftTemperatureUp = findViewById(R.id.left_temperature_up);
            mLeftTemperatureDown = findViewById(R.id.left_temperature_down);
            mLeftTemperature = findViewById(R.id.left_temperature);
            mLeftAirUp = findViewById(R.id.left_air_up);
            mLeftAirDown = findViewById(R.id.left_air_down);
            mLeftAir = findViewById(R.id.left_air);
            mAirCirculation = findViewById(R.id.air_circulation);
            mAirCirculation.setTag("" + R.drawable.selector_air_circulation_1); //方便比对图片
            mFogRemoval = findViewById(R.id.fog_removal);
            mRightAirUp = findViewById(R.id.right_air_up);
            mRightAirDown = findViewById(R.id.right_air_down);
            mRightAir = findViewById(R.id.right_air);
            mRightTemperatureUp = findViewById(R.id.right_temperature_up);
            mRightTemperatureDown = findViewById(R.id.right_temperature_down);
            mRightTemperature = findViewById(R.id.right_temperature);

            AddClimateListener();
        }
    }

    private void AddClimateListener() {
        mLeftTemperatureUp.setOnClickListener(v -> {
            try {
                String originalLeftTemperature = mLeftTemperature.getTextViewValue();
                if (originalLeftTemperature == null || originalLeftTemperature.isEmpty()) return;
                if (Integer.parseInt(originalLeftTemperature.substring(0, originalLeftTemperature.length() - 1)) < 32)
                    mLeftTemperature.updateTextView(Integer.parseInt(originalLeftTemperature.substring(0, originalLeftTemperature.length() - 1)) + 1 + "°");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        mLeftTemperatureDown.setOnClickListener(v -> {
            try {
                String originalLeftTemperature = mLeftTemperature.getTextViewValue();
                if (originalLeftTemperature == null || originalLeftTemperature.isEmpty()) return;
                if (Integer.parseInt(originalLeftTemperature.substring(0, originalLeftTemperature.length() - 1)) > 16)
                    mLeftTemperature.updateTextView(Integer.parseInt(originalLeftTemperature.substring(0, originalLeftTemperature.length() - 1)) - 1 + "°");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        mLeftAirUp.setOnClickListener(v -> {
            try {
                String originalLeftAir = mLeftAir.getTextViewValue();
                if (originalLeftAir == null || originalLeftAir.isEmpty()) return;
                if (Integer.parseInt(originalLeftAir) < 6)
                    mLeftAir.updateTextView(Integer.parseInt(originalLeftAir) + 1 + "");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        mLeftAirDown.setOnClickListener(v -> {
            try {
                String originalLeftAir = mLeftAir.getTextViewValue();
                if (originalLeftAir == null || originalLeftAir.isEmpty()) return;
                if (Integer.parseInt(originalLeftAir) > 0)
                    mLeftAir.updateTextView(Integer.parseInt(originalLeftAir) - 1 + "");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        mAirCirculation.setOnClickListener(v -> {
            Object tag = mAirCirculation.getTag();
            if (tag != null) {
                String rTag = (String) tag;

                if (rTag.equals("" + R.drawable.selector_air_circulation_1)) {
                    mAirCirculation.setImageResource(R.drawable.selector_air_circulation_2);
                    mAirCirculation.setTag("" + R.drawable.selector_air_circulation_2);
                } else if (rTag.equals("" + R.drawable.selector_air_circulation_2)) {
                    mAirCirculation.setImageResource(R.drawable.selector_air_circulation_3);
                    mAirCirculation.setTag("" + R.drawable.selector_air_circulation_3);
                } else if (rTag.equals("" + R.drawable.selector_air_circulation_3)) {
                    mAirCirculation.setImageResource(R.drawable.selector_air_circulation_1);
                    mAirCirculation.setTag("" + R.drawable.selector_air_circulation_1);
                }
            }
        });

        mRightTemperatureUp.setOnClickListener(v -> {
            try {
                String originalRightTemperature = mRightTemperature.getTextViewValue();
                if (originalRightTemperature == null || originalRightTemperature.isEmpty()) return;
                if (Integer.parseInt(originalRightTemperature.substring(0, originalRightTemperature.length() - 1)) < 32)
                    mRightTemperature.updateTextView(Integer.parseInt(originalRightTemperature.substring(0, originalRightTemperature.length() - 1)) + 1 + "°");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        mRightTemperatureDown.setOnClickListener(v -> {
            try {
                String originalRightTemperature = mRightTemperature.getTextViewValue();
                if (originalRightTemperature == null || originalRightTemperature.isEmpty()) return;
                if (Integer.parseInt(originalRightTemperature.substring(0, originalRightTemperature.length() - 1)) > 16)
                    mRightTemperature.updateTextView(Integer.parseInt(originalRightTemperature.substring(0, originalRightTemperature.length() - 1)) - 1 + "°");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        mRightAirUp.setOnClickListener(v -> {
            try {
                String originalRightAir = mRightAir.getTextViewValue();
                if (originalRightAir == null || originalRightAir.isEmpty()) return;
                if (Integer.parseInt(originalRightAir) < 6)
                    mRightAir.updateTextView(Integer.parseInt(originalRightAir) + 1 + "");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        mRightAirDown.setOnClickListener(v -> {
            try {
                String originalRightAir = mRightAir.getTextViewValue();
                if (originalRightAir == null || originalRightAir.isEmpty()) return;
                if (Integer.parseInt(originalRightAir) > 0)
                    mRightAir.updateTextView(Integer.parseInt(originalRightAir) - 1 + "");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

    // Used to forward touch events even if the touch was initiated from a child component
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mStatusBarWindowTouchListener != null) {
            boolean shouldConsumeEvent = shouldConsumeNotificationButtonEvent(ev);
            // Forward touch events to the status bar window so it can drag
            // windows if required (Notification shade)
            mStatusBarWindowTouchListener.onTouch(this, ev);
            // return true if child views should not receive this event.
            if (shouldConsumeEvent) {
                return true;
            }
        }
        return super.onInterceptTouchEvent(ev);
    }

    /**
     * If the motion event is over top of the notification button while the notification
     * panel is open, we need the statusbar touch listeners handle the event instead of the button.
     * Since the statusbar listener will trigger a close of the notification panel before the
     * any button click events are fired this will prevent reopening the panel.
     *
     * Note: we can't use requestDisallowInterceptTouchEvent because the gesture detector will
     * always receive the ACTION_DOWN and thus think a longpress happened if no other events are
     * received
     *
     * @return true if the notification button should not receive the event
     */
    private boolean shouldConsumeNotificationButtonEvent(MotionEvent ev) {
        if (mNotificationsButton == null || !mCarStatusBar.isNotificationPanelOpen()) {
            return false;
        }
        Rect notificationButtonLocation = new Rect();
        mNotificationsButton.getHitRect(notificationButtonLocation);
        return notificationButtonLocation.contains((int) ev.getX(), (int) ev.getY());
    }


    void setStatusBar(CarStatusBar carStatusBar) {
        mCarStatusBar = carStatusBar;
    }

    /**
     * Set a touch listener that will be called from onInterceptTouchEvent and onTouchEvent
     *
     * @param statusBarWindowTouchListener The listener to call from touch and intercept touch
     */
    void setStatusBarWindowTouchListener(OnTouchListener statusBarWindowTouchListener) {
        mStatusBarWindowTouchListener = statusBarWindowTouchListener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mStatusBarWindowTouchListener != null) {
            mStatusBarWindowTouchListener.onTouch(this, event);
        }
        return super.onTouchEvent(event);
    }

    protected void onNotificationsClick(View v) {
        mCarStatusBar.togglePanel();
    }

    /**
     * If there are buttons declared in the layout they will be shown and the normal
     * Nav buttons will be hidden.
     */
    public void showKeyguardButtons() {
        if (mLockScreenButtons == null) {
            return;
        }
        mLockScreenButtons.setVisibility(View.VISIBLE);
        mNavButtons.setVisibility(View.GONE);
    }

    /**
     * If there are buttons declared in the layout they will be hidden and the normal
     * Nav buttons will be shown.
     */
    public void hideKeyguardButtons() {
        if (mLockScreenButtons == null) return;

        mNavButtons.setVisibility(View.VISIBLE);
        mLockScreenButtons.setVisibility(View.GONE);
    }

    /**
     * Toggles the notification unseen indicator on/off.
     *
     * @param hasUnseen true if the unseen notification count is great than 0.
     */
    void toggleNotificationUnseenIndicator(Boolean hasUnseen) {
        if (mNotificationsButton == null) return;

        mNotificationsButton.setUnseen(hasUnseen);
    }
	
	class UsernameChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null) return;


            if ("user_name_change".equals(intent.getAction())) {
                String userName = intent.getStringExtra("userName");
				if (mUserInfo != null) {
                ((TextView) findViewById(R.id.user_name)).setText(userName);
            
                }        
                
            }
        }
    }
    
}
