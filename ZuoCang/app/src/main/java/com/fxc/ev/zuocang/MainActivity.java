package com.fxc.ev.zuocang;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.fxc.ev.zuocang.adapter.MenuListAdapter;

import java.util.ArrayList;

public class MainActivity extends Activity implements View.OnClickListener {
    protected ListView mMenuListView;
    String[] itemName = {"Doors,Windows", "Lights", "Drive Mode", "Seats", "Mirrors", "Steering"};
    private String TAG = "MainActivity";
    private MenuListAdapter mListAdapter;
    private Switch mAmbientSwitch, mFrontlightSystemSwitch,mKeyNearDoorAutoUnlockSwitch;
    private LinearLayout mAmbientDisplayLayout;
    private OneButtonDialog oneButtonDialog;
    private TwoButtonDialog twoButtonDialog;
    private Button mDriveModeComfort, mDriveModeNormal, mDriveModeSport;
    private ImageView mDriveModeDisplayImage,mAmbientModeOriginal,mAmbientModePassion,mAmbientModeFlowing,mAmbientModeWave;
    private ImageView mAmbientModeDisplayImage,mAmbientModeStars,mAmbientModeRainBow,mAmbientModeRunning,mAmbientModeRhythm;
    private ImageView mExteriorLightAuto,mExteriorHighBeamLight,mExteriorLowBeamLight,mExteriorLightOff,mHighBeamLightAuto;
    private ImageView mFrogFrontLight,mFrogRearLight;
    private ImageView mReadingLightl,mReadingLight,mReadingLightR,mPuddleLight;
    private boolean isHighBeamAutoOpen = false;
    private boolean isFrogFrontLightOpen = false;
    private boolean isFrogRearLightOpen = false;
    private boolean isExteriorLightAutoOpen = true;
    private boolean isReadingLightLOpen = false;
    private boolean isReadingLightOpen = false;
    private boolean isReadingLightROpen = false;
    private boolean isPubbleLightLOpen = false;
    private boolean isWindowLockOpen = false;
    private boolean isDoorLockOpen = false;
    private boolean isSunCurtainOpen = false;
    /*private boolean isFrontLeftUpArrowClick = false;
    private boolean isFrontLeftDownArrowClick = false;
    private boolean isRearLeftUpArrowClick = false;
    private boolean isRearLeftDownArrowClick = false;
    private boolean isFrontRightUpArrowClick = false;
    private boolean isFrontRightDownArrowClick = false;
    private boolean isRearRightUpArrowClick = false;
    private boolean isRearRightDownArrowClick = false;*/
    private boolean isLeftFrontDoorLock = false;
    private boolean isLeftRearDoorLock = false;
    private boolean isRightFrontDoorLock = false;
    private boolean isRightRearDoorLock = false;
    private boolean isRearLuggageTrunkOpen = false;
    private boolean isChildLockOpen = false;
    private boolean isInPCondition = false;
    private boolean isMirrorOpen = true;
    private boolean isDoorOpen = false;
    private boolean isRearLuggageTrunkEnable = true;
    private ArrayList<Integer> images = new ArrayList<>();
    private ScrollView mScrollView;
    private ImageView mWindowFrontLeftUp,mWindowFrontLeft,mWindowFrontLeftDown,mWindowRearLeftUp,mWindowRearLeft,mWindowRearLeftDown;
    private ImageView mWindowFrontRightUp,mWindowFrontRight,mWindowFrontRightDown,mWindowRearRightUp,mWindowRearRight,mWindowRearRightDown;
    private ImageView mWindowLock,mDoorLock,mChildLock,mSunCurtain,mLeftChildLockIcon,mRightChildLockIcon;
    private ImageView mLeftFrontDoorLock,mLeftRearDoorLock,mRightFrontDoorLock,mRightRearDoorLock;
    private ImageView mRearLuggageTrunk,mSteeringWaringIcon,mMirrorFolderDisplayIcon,mSeatsWaringIcon;
    private TextView mSteeringInstruction,mSteeringWaringInstruction,mSeatsInstruction,mSeatsWaringInstruction,mSeatsLMessageNumber,mSeatsRMessageNumber,mToastContent;
    private ImageView mLSeatsFallForward,mLSeatsFallBackward,mLSeatsFallUp,mLSeatsFallDown,mLSeatsFallHigh,mLSeatsFallLow,mLSeatsMoveForward,mLSeatsMoveBackward,mLSeat;
    private ImageView mRSeatsFallForward,mRSeatsFallBackward,mRSeatsFallUp,mRSeatsFallDown,mRSeatsFallHigh,mRSeatsFallLow,mRSeatsMoveForward,mRSeatsMoveBackward,mRSeat;
/*    private boolean isLFallForwardArrowClick = false;
    private boolean isLFallBackwardArrowClick = false;
    private boolean isLFallUpArrowClick = false;
    private boolean isLFallDownArrowClick = false;
    private boolean isLFallHighArrowClick = false;
    private boolean isLFallLowArrowClick = false;
    private boolean isLMoveForwardArrowClick = false;
    private boolean isLMoveBackwardArrowClick = false;
    private boolean isRFallForwardArrowClick = false;
    private boolean isRFallBackwardArrowClick = false;
    private boolean isRFallUpArrowClick = false;
    private boolean isRFallDownArrowClick = false;
    private boolean isRFallHighArrowClick = false;
    private boolean isRFallLowArrowClick = false;
    private boolean isRMoveForwardArrowClick = false;
    private boolean isRMoveBackwardArrowClick = false;*/
private ImageView mLLockMask,mRLockMask;
private Button mSeatsLMessageReduce,mSeatsRMessageReduce,mSeatsLMessagePlus,mSeatsRMessagePlus;
private ImageView mLMirrorUp,mLMirrorDown,mLMirrorLeft,mLMirrorRight,mRMirrorUp,mRMirrorDown,mRMirrorLeft,mRMirrorRight;
private ImageView mSteerControlArrowUp,mSteerControlArrowDown,mSteerControlArrowForward,mSteerControlArrowBackward;
private int mLMessageNum,mRMessageNum;
private Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    public ArrayList<Integer> getImages() {
        images.add(R.drawable.selector_door);
        images.add(R.drawable.selector_lights);
        images.add(R.drawable.selector_drive_mode);
        images.add(R.drawable.selector_seats);
        images.add(R.drawable.selector_mirrors);
        images.add(R.drawable.selector_steering);
        Log.i(TAG, "onReceive: action=" + images.size());
        return images;
    }

    private void initView() {
        mMenuListView = (ListView) findViewById(R.id.menu_item_list);
        mListAdapter = new MenuListAdapter(this, getImages(), itemName);
        mAmbientSwitch = findViewById(R.id.ambient_switch);
        mKeyNearDoorAutoUnlockSwitch = findViewById(R.id.key_near_door_auto_unlock_switch);
        mFrontlightSystemSwitch = findViewById(R.id.frontlight_system_switch);
        mAmbientDisplayLayout = findViewById(R.id.ambient_layout);
        mDriveModeComfort = findViewById(R.id.drive_mode_comfort);
        mDriveModeNormal = findViewById(R.id.drive_mode_normal);
        mDriveModeSport = findViewById(R.id.drive_mode_sport);
        mDriveModeDisplayImage=findViewById(R.id.dynamic_mode_image);
        mAmbientModeDisplayImage=findViewById(R.id.car_ambient_display);
        mAmbientModeOriginal=findViewById(R.id.original);
        mAmbientModePassion=findViewById(R.id.passion);
        mAmbientModeFlowing=findViewById(R.id.flowing);
        mAmbientModeWave=findViewById(R.id.wave);
        mAmbientModeStars=findViewById(R.id.stars);
        mAmbientModeRainBow=findViewById(R.id.rainbow);
        mAmbientModeRunning=findViewById(R.id.running);
        mAmbientModeRhythm=findViewById(R.id.rhythm);
        mExteriorLightAuto=findViewById(R.id.exterior_light_auto);
        mExteriorHighBeamLight=findViewById(R.id.exterior_light_high_beam);
        mExteriorLowBeamLight = findViewById(R.id.exterior_light_low_beam);
        mToastContent = findViewById(R.id.toast_content);
        //mExteriorLightOff = findViewById(R.id.exterior_light_off);
        mHighBeamLightAuto = findViewById(R.id.exterior_high_beam_light_auto);
        mFrogFrontLight = findViewById(R.id.frog_front_light);
        mFrogRearLight = findViewById(R.id.frog_rear_light);
        mReadingLightl = findViewById(R.id.reading_light_l);
        mReadingLight = findViewById(R.id.reading_light);
        mReadingLightR = findViewById(R.id.reading_light_r);
        mPuddleLight = findViewById(R.id.pubble_light);
        mScrollView = findViewById(R.id.all_detail_scroll);
        mWindowLock = findViewById(R.id.window_lock);
        mDoorLock = findViewById(R.id.door_lock);
        mChildLock = findViewById(R.id.child_lock);
        mSunCurtain = findViewById(R.id.sun_curtain);
        mWindowFrontLeftUp = findViewById(R.id.window_front_left_up);
        mWindowFrontLeft = findViewById(R.id.window_front_left);
        mWindowFrontLeftDown = findViewById(R.id.window_front_left_down);
        mWindowRearLeftUp = findViewById(R.id.window_rear_left_up);
        mWindowRearLeft = findViewById(R.id.window_rear_left);
        mWindowRearLeftDown = findViewById(R.id.window_rear_left_down);
        mWindowFrontRightUp = findViewById(R.id.window_front_right_up);
        mWindowFrontRight = findViewById(R.id.window_front_right);
        mWindowFrontRightDown = findViewById(R.id.window_front_right_down);
        mWindowRearRightUp = findViewById(R.id.window_rear_right_up);
        mWindowRearRight = findViewById(R.id.window_rear_right);
        mWindowRearRightDown = findViewById(R.id.window_rear_right_down);
        mLeftFrontDoorLock = findViewById(R.id.doorlock_left1);
        mLeftRearDoorLock = findViewById(R.id.doorlock_left2);
        mRightFrontDoorLock = findViewById(R.id.doorlock_right1);
        mRightRearDoorLock = findViewById(R.id.doorlock_right2);
        mRearLuggageTrunk = findViewById(R.id.luggage_trunk);
        mLeftChildLockIcon = findViewById(R.id.childlock_left);
        mRightChildLockIcon = findViewById(R.id.childlock_right);
        mSteeringWaringIcon = findViewById(R.id.steering_warning_icon);
        mSteeringInstruction = findViewById(R.id.steering_instrcution);
        mSteeringWaringInstruction = findViewById(R.id.steering_warning_instrcution);
        mSeatsWaringIcon = findViewById(R.id.seats_warning_icon);
        mSeatsInstruction = findViewById(R.id.seats_instruction);
        mSeatsWaringInstruction = findViewById(R.id.seats_warning_instrcution);
        mLSeatsFallForward = findViewById(R.id.arrow_l_fall_forward);
        mLSeatsFallBackward = findViewById(R.id.arrow_l_fall_backward);
        mLSeatsFallUp = findViewById(R.id.arrow_l_up);
        mLSeatsFallDown = findViewById(R.id.arrow_l_down);
        mLSeatsFallHigh = findViewById(R.id.arrow_l_higher);
        mLSeatsFallLow = findViewById(R.id.arrow_l_lower);
        mLSeat = findViewById(R.id.seats_l);
        mLSeatsMoveForward = findViewById(R.id.arrow_l_move_forward);
        mLSeatsMoveBackward = findViewById(R.id.arrow_l_move_backward);
        mRSeatsFallForward = findViewById(R.id.arrow_r_fall_forward);
        mRSeatsFallBackward = findViewById(R.id.arrow_r_fall_backward);
        mRSeatsFallUp = findViewById(R.id.arrow_r_up);
        mRSeatsFallDown = findViewById(R.id.arrow_r_down);
        mRSeatsFallHigh = findViewById(R.id.arrow_r_higher);
        mRSeatsFallLow = findViewById(R.id.arrow_r_lower);
        mLLockMask = findViewById(R.id.l_lock_mask);
        mRLockMask = findViewById(R.id.r_lock_mask);
        mRSeatsMoveForward = findViewById(R.id.arrow_r_move_forward);
        mRSeatsMoveBackward = findViewById(R.id.arrow_r_move_backward);
        mRSeat = findViewById(R.id.seats_r);
        mSeatsLMessageReduce = findViewById(R.id.message_l_reduce);
        mSeatsRMessageReduce = findViewById(R.id.message_r_reduce);
        mSeatsLMessagePlus = findViewById(R.id.message_l_plus);
        mSeatsRMessagePlus = findViewById(R.id.message_r_plus);
        mSeatsLMessageNumber = findViewById(R.id.l_message_number);
        mSeatsRMessageNumber = findViewById(R.id.r_message_number);
        resetSeatsToNormal();
        mMirrorFolderDisplayIcon = findViewById(R.id.folder);
        mLMirrorUp = findViewById(R.id.mirror_l_up);
        mLMirrorDown = findViewById(R.id.mirror_l_down);
        mLMirrorLeft = findViewById(R.id.mirror_l_left);
        mLMirrorRight = findViewById(R.id.mirror_l_right);
        mRMirrorUp = findViewById(R.id.mirror_r_up);
        mRMirrorDown = findViewById(R.id.mirror_r_down);
        mRMirrorLeft = findViewById(R.id.mirror_r_left);
        mRMirrorRight = findViewById(R.id.mirror_r_right);
        mSteerControlArrowUp = findViewById(R.id.steering_control_arrow_up);
        mSteerControlArrowDown = findViewById(R.id.steering_control_arrow_down);
        mSteerControlArrowForward = findViewById(R.id.steering_control_arrow_move_forward);
        mSteerControlArrowBackward = findViewById(R.id.steering_control_arrow_move_backward);
        resetMirrorToNormal();
        if(isInPCondition){
            mSteeringInstruction.setVisibility(View.VISIBLE);
            mSteeringWaringIcon.setVisibility(View.GONE);
            mLLockMask.setVisibility(View.GONE);
            //mRLockMask.setVisibility(View.GONE);
            mSeatsWaringIcon.setVisibility(View.GONE);
            mSeatsInstruction.setVisibility(View.VISIBLE);
            mSeatsWaringInstruction.setVisibility(View.GONE);
            mSteeringWaringInstruction.setVisibility(View.GONE);
        }else{
            mSteeringInstruction.setVisibility(View.GONE);
            mLLockMask.setVisibility(View.VISIBLE);
            //mRLockMask.setVisibility(View.VISIBLE);
            mSteeringWaringIcon.setVisibility(View.VISIBLE);
            mSteeringWaringInstruction.setVisibility(View.VISIBLE);
            mSeatsWaringIcon.setVisibility(View.VISIBLE);
            mSeatsInstruction.setVisibility(View.GONE);
            mSeatsWaringInstruction.setVisibility(View.VISIBLE);
            mLLockMask.setOnClickListener(this);
            //mRLockMask.setOnClickListener(this);
        }
        mFrogFrontLight.setEnabled(true);
        mFrogRearLight.setEnabled(true);
        mExteriorLightAuto.setSelected(true);
        mDriveModeNormal.setSelected(true);
        mAmbientModeOriginal.setSelected(true);
        mAmbientModeDisplayImage.setBackgroundResource(R.drawable.car_blur);
        mDriveModeDisplayImage.setBackgroundResource(R.drawable.img_dynamic_normal);
        resetSteeringToNormal();
        mDriveModeSport.setOnClickListener(this);
        mDriveModeComfort.setOnClickListener(this);
        mDriveModeNormal.setOnClickListener(this);
        mAmbientModeOriginal.setOnClickListener(this);
        mAmbientModePassion.setOnClickListener(this);
        mAmbientModeFlowing.setOnClickListener(this);
        mAmbientModeWave.setOnClickListener(this);
        mAmbientModeStars.setOnClickListener(this);
        mAmbientModeRainBow.setOnClickListener(this);
        mAmbientModeRunning.setOnClickListener(this);
        mAmbientModeRhythm.setOnClickListener(this);
        mExteriorLightAuto.setOnClickListener(this);
        mExteriorHighBeamLight.setOnClickListener(this);
        mExteriorLowBeamLight.setOnClickListener(this);
        //mExteriorLightOff.setOnClickListener(this);
        mHighBeamLightAuto.setOnClickListener(this);
        mFrogFrontLight.setOnClickListener(this);
        mFrogRearLight.setOnClickListener(this);
        mReadingLightl.setOnClickListener(this);
        mReadingLight.setOnClickListener(this);
        mReadingLightR.setOnClickListener(this);
        mPuddleLight.setOnClickListener(this);
        mWindowLock.setOnClickListener(this);
        mDoorLock.setOnClickListener(this);
        mChildLock.setOnClickListener(this);
        mSunCurtain.setOnClickListener(this);
        mMirrorFolderDisplayIcon.setOnClickListener(this);
        mWindowFrontLeftUp.setOnClickListener(this);
        mWindowFrontLeft.setOnClickListener(this);
        mWindowFrontLeftDown.setOnClickListener(this);
        mWindowRearLeftUp.setOnClickListener(this);
        mWindowRearLeft.setOnClickListener(this);
        mWindowRearLeftDown.setOnClickListener(this);
        mWindowFrontRightUp.setOnClickListener(this);
        mWindowFrontRight.setOnClickListener(this);
        mWindowFrontRightDown.setOnClickListener(this);
        mWindowRearRightUp.setOnClickListener(this);
        mWindowRearRight.setOnClickListener(this);
        mWindowRearRightDown.setOnClickListener(this);
        mLeftFrontDoorLock.setOnClickListener(this);
        mLeftRearDoorLock.setOnClickListener(this);
        mRightFrontDoorLock.setOnClickListener(this);
        mRightRearDoorLock.setOnClickListener(this);
        mRearLuggageTrunk.setOnClickListener(this);
        mLSeatsFallForward.setOnClickListener(this);
        mLSeatsFallBackward.setOnClickListener(this);
        mLSeatsFallUp.setOnClickListener(this);
        mLSeatsFallDown.setOnClickListener(this);
        mLSeatsFallHigh.setOnClickListener(this);
        mLSeatsFallLow.setOnClickListener(this);
        mLSeatsMoveForward.setOnClickListener(this);
        mLSeatsMoveBackward.setOnClickListener(this);
        mRSeatsFallForward.setOnClickListener(this);
        mRSeatsFallBackward.setOnClickListener(this);
        mRSeatsFallUp.setOnClickListener(this);
        mRSeatsFallDown.setOnClickListener(this);
        mRSeatsFallHigh.setOnClickListener(this);
        mRSeatsFallLow.setOnClickListener(this);
        mRSeatsMoveForward.setOnClickListener(this);
        mRSeatsMoveBackward.setOnClickListener(this);
        mSeatsLMessageReduce.setOnClickListener(this);
        mSeatsRMessageReduce.setOnClickListener(this);
        mSeatsLMessagePlus.setOnClickListener(this);
        mSeatsRMessagePlus.setOnClickListener(this);
        mMirrorFolderDisplayIcon.setOnClickListener(this);
        mLMirrorUp.setOnClickListener(this);
        mLMirrorDown.setOnClickListener(this);
        mLMirrorLeft.setOnClickListener(this);
        mLMirrorRight.setOnClickListener(this);
        mRMirrorUp.setOnClickListener(this);
        mRMirrorDown.setOnClickListener(this);
        mRMirrorLeft.setOnClickListener(this);
        mRMirrorRight.setOnClickListener(this);
        mSteerControlArrowUp.setOnClickListener(this);
        mSteerControlArrowDown.setOnClickListener(this);
        mSteerControlArrowForward.setOnClickListener(this);
        mSteerControlArrowBackward.setOnClickListener(this);
        mRearLuggageTrunk.setActivated(false);
        mMenuListView.setAdapter(mListAdapter);
        mScrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view, int x, int y, int oldX, int oldY) {
                if(mAmbientDisplayLayout.getVisibility()==View.GONE) {
                    if(oldY>=0 && y<1189){
                        mMenuListView.getChildAt(0).setSelected(true);
                        mMenuListView.getChildAt(1).setSelected(false);
                        mMenuListView.getChildAt(2).setSelected(false);
                        mMenuListView.getChildAt(3).setSelected(false);
                        mMenuListView.getChildAt(4).setSelected(false);
                        mMenuListView.getChildAt(5).setSelected(false);
                    }else if((oldY>=1189 && y<2111)){
                        mMenuListView.getChildAt(0).setSelected(false);
                        mMenuListView.getChildAt(1).setSelected(true);
                        mMenuListView.getChildAt(2).setSelected(false);
                        mMenuListView.getChildAt(3).setSelected(false);
                        mMenuListView.getChildAt(4).setSelected(false);
                        mMenuListView.getChildAt(5).setSelected(false);
                        resetSeatsToNormal();
                    }else if((oldY>=2111 && y<2705)){
                        mMenuListView.getChildAt(0).setSelected(false);
                        mMenuListView.getChildAt(1).setSelected(false);
                        mMenuListView.getChildAt(2).setSelected(true);
                        mMenuListView.getChildAt(3).setSelected(false);
                        mMenuListView.getChildAt(4).setSelected(false);
                        mMenuListView.getChildAt(5).setSelected(false);
                        resetMirrorToNormal();
                    }else if((oldY>=2705 && y<3455)){
                        mMenuListView.getChildAt(0).setSelected(false);
                        mMenuListView.getChildAt(1).setSelected(false);
                        mMenuListView.getChildAt(2).setSelected(false);
                        mMenuListView.getChildAt(3).setSelected(true);
                        mMenuListView.getChildAt(4).setSelected(false);
                        mMenuListView.getChildAt(5).setSelected(false);
                        resetSteeringToNormal();
                    }else if((oldY>=3455 && y<4298)){
                        mMenuListView.getChildAt(0).setSelected(false);
                        mMenuListView.getChildAt(1).setSelected(false);
                        mMenuListView.getChildAt(2).setSelected(false);
                        mMenuListView.getChildAt(3).setSelected(false);
                        mMenuListView.getChildAt(4).setSelected(true);
                        mMenuListView.getChildAt(5).setSelected(false);
                        resetSeatsToNormal();
                    }else if((oldY>=4298 && y<5298)){
                        mMenuListView.getChildAt(0).setSelected(false);
                        mMenuListView.getChildAt(1).setSelected(false);
                        mMenuListView.getChildAt(2).setSelected(false);
                        mMenuListView.getChildAt(3).setSelected(false);
                        mMenuListView.getChildAt(4).setSelected(false);
                        mMenuListView.getChildAt(5).setSelected(true);
                        resetMirrorToNormal();
                    }
                }else{
                    if(oldY>=0 && y<1189){
                        mMenuListView.getChildAt(0).setSelected(true);
                        mMenuListView.getChildAt(1).setSelected(false);
                        mMenuListView.getChildAt(2).setSelected(false);
                        mMenuListView.getChildAt(3).setSelected(false);
                        mMenuListView.getChildAt(4).setSelected(false);
                        mMenuListView.getChildAt(5).setSelected(false);
                    }else if((oldY>=1189 && y<2761)){
                        mMenuListView.getChildAt(0).setSelected(false);
                        mMenuListView.getChildAt(1).setSelected(true);
                        mMenuListView.getChildAt(2).setSelected(false);
                        mMenuListView.getChildAt(3).setSelected(false);
                        mMenuListView.getChildAt(4).setSelected(false);
                        mMenuListView.getChildAt(5).setSelected(false);
                        resetSeatsToNormal();
                    }else if((oldY>=2761 && y<3355)){
                        mMenuListView.getChildAt(0).setSelected(false);
                        mMenuListView.getChildAt(1).setSelected(false);
                        mMenuListView.getChildAt(2).setSelected(true);
                        mMenuListView.getChildAt(3).setSelected(false);
                        mMenuListView.getChildAt(4).setSelected(false);
                        mMenuListView.getChildAt(5).setSelected(false);
                        resetMirrorToNormal();
                    }else if((oldY>=3355 && y<4105)){
                        mMenuListView.getChildAt(0).setSelected(false);
                        mMenuListView.getChildAt(1).setSelected(false);
                        mMenuListView.getChildAt(2).setSelected(false);
                        mMenuListView.getChildAt(3).setSelected(true);
                        mMenuListView.getChildAt(4).setSelected(false);
                        mMenuListView.getChildAt(5).setSelected(false);
                        resetSteeringToNormal();
                    }else if((oldY>=4105 && y<4988)){
                        mMenuListView.getChildAt(0).setSelected(false);
                        mMenuListView.getChildAt(1).setSelected(false);
                        mMenuListView.getChildAt(2).setSelected(false);
                        mMenuListView.getChildAt(3).setSelected(false);
                        mMenuListView.getChildAt(4).setSelected(true);
                        mMenuListView.getChildAt(5).setSelected(false);
                        resetSeatsToNormal();
                    }else if((oldY>=4948 && y<5948)){
                        mMenuListView.getChildAt(0).setSelected(false);
                        mMenuListView.getChildAt(1).setSelected(false);
                        mMenuListView.getChildAt(2).setSelected(false);
                        mMenuListView.getChildAt(3).setSelected(false);
                        mMenuListView.getChildAt(4).setSelected(false);
                        mMenuListView.getChildAt(5).setSelected(true);
                        resetMirrorToNormal();
                    }
                }
            }
        });
        mMenuListView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mMenuListView.getChildAt(0).setSelected(true);
            }
        }, 500);

        mMenuListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                view.setSelected(true);
                if(mAmbientDisplayLayout.getVisibility()==View.GONE) {
                    if(position==0){
                        mScrollView.scrollTo(60,0);
                    }else if(position==1){
                        resetSeatsToNormal();
                        mScrollView.scrollTo(60,1195);
                    }else if(position==2){
                        mScrollView.scrollTo(60, 2117);
                        resetMirrorToNormal();
                    }else if(position==3){
                        mScrollView.scrollTo(60, 2715);
                        mScrollView.getHeight();
                        resetSteeringToNormal();
                    }else if(position==4){
                        mScrollView.scrollTo(60, 3461);
                        resetSeatsToNormal();
                    }else if(position==5){
                        resetMirrorToNormal();
                        mScrollView.scrollTo(60, 4304);
                        mMenuListView.getChildAt(5).setSelected(true);
                    }
                }else{
                    if(position==0){
                        mScrollView.scrollTo(60,0);
                    }else if(position==1){
                        resetSeatsToNormal();
                        mScrollView.scrollTo(60,1195);
                    }else if(position==2){
                        mScrollView.scrollTo(60, 2767);
                    }else if(position==3){
                        mScrollView.scrollTo(60, 3365);
                    }else if(position==4){
                        mScrollView.scrollTo(60, 4111);
                        resetSeatsToNormal();
                    }else if(position==5){
                        mScrollView.scrollTo(60, 4954);
                        mMenuListView.getChildAt(5).setSelected(true);
                    }
                }
            }
        });
        mAmbientSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mAmbientSwitch.setChecked(true);
                    mAmbientDisplayLayout.setVisibility(View.VISIBLE);
                } else {
                    mAmbientSwitch.setChecked(false);
                    mAmbientDisplayLayout.setVisibility(View.GONE);
                }
            }
        });
        mFrontlightSystemSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mFrontlightSystemSwitch.setChecked(true);
                    oneButtonDialog = new OneButtonDialog(MainActivity.this);

                    oneButtonDialog.setMessage("The Adaptive Front Lighting System (AFS) moves the light projector to the left or right automatically with changes in car speed and steering wheel angle. It increase the effective lighting range and allows the driver to see the road clearly when they’re turning towards, helping to avoid blind spots.");
                    oneButtonDialog.setYesOnclickListener(new OneButtonDialog.onYesOnclickListener() {
                        @Override
                        public void onYesClick() {
                            oneButtonDialog.dismiss();
                        }
                    });
                    //new Dialog(MainActivity.this).show();
                    oneButtonDialog.show();

                } else {
                    mFrontlightSystemSwitch.setChecked(false);
                }
            }
        });
        mKeyNearDoorAutoUnlockSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mKeyNearDoorAutoUnlockSwitch.setChecked(true);
                    oneButtonDialog = new OneButtonDialog(MainActivity.this);

                    oneButtonDialog.setMessage("The car will automatically unlock doors when it recognizes your key(or authorized cell phone) is within range(XX meter).\n" +
                            "\n" +
                            "And if none of the doors are opened within two minutes of unlocking, all are locked again automatically");
                    oneButtonDialog.setYesOnclickListener(new OneButtonDialog.onYesOnclickListener() {
                        @Override
                        public void onYesClick() {
                            oneButtonDialog.dismiss();
                        }
                    });
                    //new Dialog(MainActivity.this).show();
                    oneButtonDialog.show();

                } else {
                    mKeyNearDoorAutoUnlockSwitch.setChecked(false);
                }
            }
        });
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        mLMessageNum=(Integer.parseInt(String.valueOf(mSeatsLMessageNumber.getText())));
        mRMessageNum=(Integer.parseInt(String.valueOf(mSeatsRMessageNumber.getText())));
        if (i == mWindowLock.getId()) {
            if (isWindowLockOpen == false) {
                mWindowFrontLeftUp.setEnabled(false);
                mWindowFrontLeft.setEnabled(false);
                mWindowFrontLeftDown.setEnabled(false);
                mWindowRearLeftUp.setEnabled(false);
                mWindowRearLeft.setEnabled(false);
                mWindowRearLeftDown.setEnabled(false);
                mWindowFrontRightUp.setEnabled(false);
                mWindowFrontRight.setEnabled(false);
                mWindowFrontRightDown.setEnabled(false);
                mWindowRearRightUp.setEnabled(false);
                mWindowRearRight.setEnabled(false);
                mWindowRearRightDown.setEnabled(false);
                mWindowLock.setSelected(true);
                isWindowLockOpen = true;
            }else{
                mWindowFrontLeftUp.setEnabled(true);
                mWindowFrontLeft.setEnabled(true);
                mWindowFrontLeftDown.setEnabled(true);
                mWindowRearLeftUp.setEnabled(true);
                mWindowRearLeft.setEnabled(true);
                mWindowRearLeftDown.setEnabled(true);
                mWindowFrontRightUp.setEnabled(true);
                mWindowFrontRight.setEnabled(true);
                mWindowFrontRightDown.setEnabled(true);
                mWindowRearRightUp.setEnabled(true);
                mWindowRearRight.setEnabled(true);
                mWindowRearRightDown.setEnabled(true);
                mWindowLock.setSelected(false);
                isWindowLockOpen = false;
            }
            return;
        }else if (i == mDoorLock.getId()) {
            if(isDoorOpen==false) {
                mDoorLock.setEnabled(true);
                if (isDoorLockOpen == false) {
                    mLeftFrontDoorLock.setSelected(true);
                    mLeftRearDoorLock.setSelected(true);
                    mRightFrontDoorLock.setSelected(true);
                    mRightRearDoorLock.setSelected(true);
                    mDoorLock.setSelected(true);
                    isLeftRearDoorLock = true;
                    isLeftFrontDoorLock = true;
                    isRightFrontDoorLock = true;
                    isRightRearDoorLock = true;
                    mRearLuggageTrunk.setActivated(true);
                    isRearLuggageTrunkEnable = false;
                    isDoorLockOpen = true;
                } else {
                    mLeftFrontDoorLock.setSelected(false);
                    mLeftRearDoorLock.setSelected(false);
                    mRightFrontDoorLock.setSelected(false);
                    mRightRearDoorLock.setSelected(false);
                    isLeftRearDoorLock = false;
                    isLeftFrontDoorLock = false;
                    isRightFrontDoorLock = false;
                    isRightRearDoorLock = false;
                    mRearLuggageTrunk.setActivated(false);
                    mRearLuggageTrunk.setSelected(false);
                    isRearLuggageTrunkEnable = true;
                    mDoorLock.setSelected(false);
                    isDoorLockOpen = false;
                }
            }else{
                mDoorLock.setEnabled(false);
                mToastContent.setText("Make sure all doors are closed before locking");
                mToastContent.setVisibility(View.VISIBLE);
                handler.postDelayed(runnable, 3000);
            }
            return;
        }else if (i == mLeftFrontDoorLock.getId()) {
            if(isDoorLockOpen==false) {
                if (isLeftFrontDoorLock == false) {
                    mLeftFrontDoorLock.setSelected(true);
                    isLeftFrontDoorLock =true;
                    if(isDoorLockOpen()){
                        mDoorLock.setSelected(true);
                        isDoorLockOpen=true;
                        mRearLuggageTrunk.setActivated(true);
                        isRearLuggageTrunkEnable=false;
                    }
                }else{
                    mLeftFrontDoorLock.setSelected(false);
                    isLeftFrontDoorLock =false;
                }
            }else{
                mLeftFrontDoorLock.setSelected(false);
                isLeftFrontDoorLock =false;
                mDoorLock.setSelected(false);
                isDoorLockOpen=false;
                mRearLuggageTrunk.setActivated(false);
                isRearLuggageTrunkEnable=true;
            }
            return;
        }else if (i == mLeftRearDoorLock.getId()) {
            if(isDoorLockOpen==false) {
                if (isLeftRearDoorLock == false) {
                    mLeftRearDoorLock.setSelected(true);
                    isLeftRearDoorLock = true;
                    if(isDoorLockOpen()){
                        mDoorLock.setSelected(true);
                        isDoorLockOpen = true;
                        mRearLuggageTrunk.setActivated(true);
                        isRearLuggageTrunkEnable=false;
                    }
                }else{
                    mLeftRearDoorLock.setSelected(false);
                    isLeftRearDoorLock =false;
                }
            }else{
                mLeftRearDoorLock.setSelected(false);
                isLeftRearDoorLock =false;
                mDoorLock.setSelected(false);
                isDoorLockOpen=false;
                mRearLuggageTrunk.setActivated(false);
                isRearLuggageTrunkEnable=true;
            }
            return;
        }else if (i == mRightFrontDoorLock.getId()) {
            if(isDoorLockOpen==false) {
                if (isRightFrontDoorLock == false) {
                    mRightFrontDoorLock.setSelected(true);
                    isRightFrontDoorLock =true;
                    if(isDoorLockOpen()){
                        mDoorLock.setSelected(true);
                        isDoorLockOpen=true;
                        mRearLuggageTrunk.setActivated(true);
                        isRearLuggageTrunkEnable=false;
                    }
                }else{
                    mRightFrontDoorLock.setSelected(false);
                    isRightFrontDoorLock =false;
                }
            }else{
                mRightFrontDoorLock.setSelected(false);
                isRightFrontDoorLock =false;
                mDoorLock.setSelected(false);
                isDoorLockOpen=false;
                mRearLuggageTrunk.setActivated(false);
                isRearLuggageTrunkEnable=true;
            }
            return;
        }else if (i == mRightRearDoorLock.getId()) {
            if(isDoorLockOpen==false) {
                if (isRightRearDoorLock == false) {
                    mRightRearDoorLock.setSelected(true);
                    isRightRearDoorLock =true;
                    if(isDoorLockOpen()){
                        mDoorLock.setSelected(true);
                        isDoorLockOpen=true;
                        mRearLuggageTrunk.setActivated(true);
                        isRearLuggageTrunkEnable=false;
                    }
                }else{
                    mRightRearDoorLock.setSelected(false);
                    isRightRearDoorLock =false;
                }
            }else{
                mRightRearDoorLock.setSelected(false);
                isRightRearDoorLock =false;
                mDoorLock.setSelected(false);
                isDoorLockOpen=false;
                mRearLuggageTrunk.setActivated(false);
                isRearLuggageTrunkEnable=true;
            }
            return;
        } else if (i == mRearLuggageTrunk.getId()) {
            if(isRearLuggageTrunkEnable==false && isRearLuggageTrunkOpen==false){
              mToastContent.setText("Unlock before you open the door or the trunk");
              mToastContent.setVisibility(View.VISIBLE);
              handler.postDelayed(runnable, 3000);
            }else{
                if(isRearLuggageTrunkOpen == false ){
                    mRearLuggageTrunk.setSelected(true);
                    isRearLuggageTrunkOpen=true;
                }else{
                    mRearLuggageTrunk.setSelected(false);
                    isRearLuggageTrunkOpen=false;
                }
            }
            return;
        } else if (i == mChildLock.getId()) {
            if (isChildLockOpen == false) {
                mChildLock.setSelected(true);
                isChildLockOpen = true;
                mLeftChildLockIcon.setVisibility(View.VISIBLE);
                mRightChildLockIcon.setVisibility(View.VISIBLE);
            }else{
                mChildLock.setSelected(false);
                isChildLockOpen = false;
                mLeftChildLockIcon.setVisibility(View.GONE);
                mRightChildLockIcon.setVisibility(View.GONE);
            }
            return;
        } else if (i == mSunCurtain.getId()) {
            if (isSunCurtainOpen == false) {
                mSunCurtain.setSelected(true);
                isSunCurtainOpen = true;
            }else{
                mSunCurtain.setSelected(false);
                isSunCurtainOpen = false;
            }
            return;
        } else if (i == mDriveModeComfort.getId()) {
            mDriveModeComfort.setSelected(true);
            mDriveModeNormal.setSelected(false);
            mDriveModeSport.setSelected(false);
            mDriveModeDisplayImage.setBackgroundResource(R.drawable.img_dynamic_comfort);
            return;
        }else if (i == mDriveModeNormal.getId()) {
            mDriveModeComfort.setSelected(false);
            mDriveModeNormal.setSelected(true);
            mDriveModeSport.setSelected(false);
            mDriveModeDisplayImage.setBackgroundResource(R.drawable.img_dynamic_normal);
            return;
        }else if (i == mDriveModeSport.getId()) {
            mDriveModeComfort.setSelected(false);
            mDriveModeNormal.setSelected(false);
            mDriveModeSport.setSelected(true);
            mDriveModeDisplayImage.setBackgroundResource(R.drawable.img_dynamic_sport);
            return;
        } else if (i == mAmbientModeOriginal.getId()) {
            mAmbientModeOriginal.setSelected(true);
            mAmbientModePassion.setSelected(false);
            mAmbientModeFlowing.setSelected(false);
            mAmbientModeWave.setSelected(false);
            mAmbientModeStars.setSelected(false);
            mAmbientModeRainBow.setSelected(false);
            mAmbientModeRunning.setSelected(false);
            mAmbientModeRhythm.setSelected(false);
            mAmbientModeDisplayImage.setBackgroundResource(R.drawable.car_blur);
            return;
        } else if (i == mAmbientModePassion.getId()) {
            mAmbientModeOriginal.setSelected(false);
            mAmbientModePassion.setSelected(true);
            mAmbientModeFlowing.setSelected(false);
            mAmbientModeWave.setSelected(false);
            mAmbientModeStars.setSelected(false);
            mAmbientModeRainBow.setSelected(false);
            mAmbientModeRunning.setSelected(false);
            mAmbientModeRhythm.setSelected(false);
            mAmbientModeDisplayImage.setBackgroundResource(R.drawable.car_passion);
            return;
        } else if (i == mAmbientModeFlowing.getId()) {
            mAmbientModeOriginal.setSelected(false);
            mAmbientModePassion.setSelected(false);
            mAmbientModeFlowing.setSelected(true);
            mAmbientModeWave.setSelected(false);
            mAmbientModeStars.setSelected(false);
            mAmbientModeRainBow.setSelected(false);
            mAmbientModeRunning.setSelected(false);
            mAmbientModeRhythm.setSelected(false);
            mAmbientModeDisplayImage.setBackgroundResource(R.drawable.car_flowing);
            return;
        } else if (i == mAmbientModeWave.getId()) {
            mAmbientModeOriginal.setSelected(false);
            mAmbientModePassion.setSelected(false);
            mAmbientModeFlowing.setSelected(false);
            mAmbientModeWave.setSelected(true);
            mAmbientModeStars.setSelected(false);
            mAmbientModeRainBow.setSelected(false);
            mAmbientModeRunning.setSelected(false);
            mAmbientModeRhythm.setSelected(false);
            mAmbientModeDisplayImage.setBackgroundResource(R.drawable.car_blur);
            return;
        } else if (i == mAmbientModeStars.getId()) {
            mAmbientModeOriginal.setSelected(false);
            mAmbientModePassion.setSelected(false);
            mAmbientModeFlowing.setSelected(false);
            mAmbientModeWave.setSelected(false);
            mAmbientModeStars.setSelected(true);
            mAmbientModeRainBow.setSelected(false);
            mAmbientModeRunning.setSelected(false);
            mAmbientModeRhythm.setSelected(false);
            mAmbientModeDisplayImage.setBackgroundResource(R.drawable.car_stars);
            return;
        } else if (i == mAmbientModeRainBow.getId()) {
            mAmbientModeOriginal.setSelected(false);
            mAmbientModePassion.setSelected(false);
            mAmbientModeFlowing.setSelected(false);
            mAmbientModeWave.setSelected(false);
            mAmbientModeStars.setSelected(false);
            mAmbientModeRainBow.setSelected(true);
            mAmbientModeRunning.setSelected(false);
            mAmbientModeRhythm.setSelected(false);
            mAmbientModeDisplayImage.setBackgroundResource(R.drawable.car_rainbow);
            return;
        } else if (i == mAmbientModeRunning.getId()) {
            mAmbientModeOriginal.setSelected(false);
            mAmbientModePassion.setSelected(false);
            mAmbientModeFlowing.setSelected(false);
            mAmbientModeWave.setSelected(false);
            mAmbientModeStars.setSelected(false);
            mAmbientModeRainBow.setSelected(false);
            mAmbientModeRunning.setSelected(true);
            mAmbientModeRhythm.setSelected(false);
            mAmbientModeDisplayImage.setBackgroundResource(R.drawable.car_blur);
            return;
        } else if (i == mAmbientModeRhythm.getId()) {
            mAmbientModeOriginal.setSelected(false);
            mAmbientModePassion.setSelected(false);
            mAmbientModeFlowing.setSelected(false);
            mAmbientModeWave.setSelected(false);
            mAmbientModeStars.setSelected(false);
            mAmbientModeRainBow.setSelected(false);
            mAmbientModeRunning.setSelected(false);
            mAmbientModeRhythm.setSelected(true);
            mAmbientModeDisplayImage.setBackgroundResource(R.drawable.car_blur);
            return;
        } else if (i == mExteriorLightAuto.getId()) {
            if(isExteriorLightAutoOpen) {
                mExteriorLightAuto.setSelected(true);
                mExteriorHighBeamLight.setSelected(false);
                mExteriorLowBeamLight.setSelected(false);
               // mExteriorLightOff.setSelected(false);
                mExteriorHighBeamLight.setEnabled(true);
                mExteriorLowBeamLight.setEnabled(true);
                mFrogFrontLight.setEnabled(true);
                mFrogRearLight.setEnabled(true);
                isExteriorLightAutoOpen=false;
            }else{
                mExteriorLightAuto.setSelected(false);
                mFrogFrontLight.setSelected(false);
                mFrogRearLight.setSelected(false);
                isExteriorLightAutoOpen=true;
            }
            return;
        } else if (i == mExteriorHighBeamLight.getId()) {
            mExteriorLightAuto.setSelected(false);
            mExteriorHighBeamLight.setSelected(true);
            mExteriorLowBeamLight.setSelected(false);
            //mExteriorLightOff.setSelected(false);
            mFrogFrontLight.setEnabled(true);
            mFrogRearLight.setEnabled(true);
            isExteriorLightAutoOpen=true;
            return;
        } else if (i == mExteriorLowBeamLight.getId()) {
            mExteriorLightAuto.setSelected(false);
            mExteriorHighBeamLight.setSelected(false);
            mExteriorLowBeamLight.setSelected(true);
            //mExteriorLightOff.setSelected(false);
            mFrogFrontLight.setEnabled(true);
            mFrogRearLight.setEnabled(true);
            isExteriorLightAutoOpen=true;
            return;
        } /*else if (i ==  mExteriorLightOff.getId()) {
            mExteriorLightAuto.setSelected(false);
            mExteriorHighBeamLight.setSelected(false);
            mExteriorLowBeamLight.setSelected(false);
            mExteriorHighBeamLight.setEnabled(false);
            mExteriorLowBeamLight.setEnabled(false);
            mExteriorLightOff.setSelected(true);
            mFrogFrontLight.setEnabled(false);
            mFrogRearLight.setEnabled(false);
            isExteriorLightAutoOpen=true;
            return;
        }*/else if (i ==  mHighBeamLightAuto.getId()) {
            if (isHighBeamAutoOpen == false) {
                mHighBeamLightAuto.setSelected(true);
                isHighBeamAutoOpen = true;
            }else{
                mHighBeamLightAuto.setSelected(false);
                isHighBeamAutoOpen = false;
            }
            return;
        }else if (i ==  mFrogFrontLight.getId()) {
            if (isFrogFrontLightOpen == false) {
                mFrogFrontLight.setSelected(true);
                isFrogFrontLightOpen = true;
            }else{
                mFrogFrontLight.setSelected(false);
                isFrogFrontLightOpen = false;
            }
            return;
        }else if (i ==  mFrogRearLight.getId()) {
            if (isFrogRearLightOpen == false) {
                mFrogRearLight.setSelected(true);
                isFrogRearLightOpen = true;
            }else{
                mFrogRearLight.setSelected(false);
                isFrogRearLightOpen = false;
            }
            return;
        }else if (i ==  mReadingLightl.getId()) {
            if (isReadingLightLOpen == false) {
                mReadingLightl.setSelected(true);
                isReadingLightLOpen = true;
            }else{
                mReadingLightl.setSelected(false);
                isReadingLightLOpen = false;
            }
            return;
        }else if (i ==  mReadingLight.getId()) {
            if (isReadingLightOpen == false) {
                mReadingLight.setSelected(true);
                isReadingLightOpen = true;
            }else{
                mReadingLight.setSelected(false);
                isReadingLightOpen = false;
            }
            return;
        }else if (i ==  mReadingLightR.getId()) {
            if (isReadingLightROpen == false) {
                mReadingLightR.setSelected(true);
                isReadingLightROpen = true;
            }else{
                mReadingLightR.setSelected(false);
                isReadingLightROpen = false;
            }
            return;
        }else if (i ==  mPuddleLight.getId()) {
            if (isPubbleLightLOpen == false) {
                mPuddleLight.setSelected(true);
                isPubbleLightLOpen = true;
            }else{
                mPuddleLight.setSelected(false);
                isPubbleLightLOpen = false;
            }
            return;
        }else if (i ==  mPuddleLight.getId()) {
            if (isPubbleLightLOpen == false) {
                mPuddleLight.setSelected(true);
                isPubbleLightLOpen = true;
            }else{
                mPuddleLight.setSelected(false);
                isPubbleLightLOpen = false;
            }
            return;
        }else if (i ==  mLSeatsFallForward.getId()) {
            mLSeatsFallForward.setSelected(true);
            mLSeatsFallBackward.setSelected(false);
            mLSeatsFallUp.setSelected(false);
            mLSeatsFallDown.setSelected(false);
            mLSeatsFallHigh.setSelected(false);
            mLSeatsFallLow.setSelected(false);
            mLSeatsMoveForward.setSelected(false);
            mLSeatsMoveBackward.setSelected(false);
            mLSeat.setBackgroundResource(R.drawable.seat_l_highlight_2);
            return;
        }else if (i ==  mLSeatsFallBackward.getId()) {
            mLSeatsFallForward.setSelected(false);
            mLSeatsFallBackward.setSelected(true);
            mLSeatsFallUp.setSelected(false);
            mLSeatsFallDown.setSelected(false);
            mLSeatsFallHigh.setSelected(false);
            mLSeatsFallLow.setSelected(false);
            mLSeatsMoveForward.setSelected(false);
            mLSeatsMoveBackward.setSelected(false);
            mLSeat.setBackgroundResource(R.drawable.seat_l_highlight_2);
            return;
        }else if (i ==  mLSeatsFallUp.getId()) {
            mLSeatsFallForward.setSelected(false);
            mLSeatsFallBackward.setSelected(false);
            mLSeatsFallUp.setSelected(true);
            mLSeatsFallDown.setSelected(false);
            mLSeatsFallHigh.setSelected(false);
            mLSeatsFallLow.setSelected(false);
            mLSeatsMoveForward.setSelected(false);
            mLSeatsMoveBackward.setSelected(false);
            mLSeat.setBackgroundResource(R.drawable.seat_l_highlight_3);
            return;
        }else if (i ==  mLSeatsFallDown.getId()) {
            mLSeatsFallForward.setSelected(false);
            mLSeatsFallBackward.setSelected(false);
            mLSeatsFallUp.setSelected(false);
            mLSeatsFallDown.setSelected(true);
            mLSeatsFallHigh.setSelected(false);
            mLSeatsFallLow.setSelected(false);
            mLSeatsMoveForward.setSelected(false);
            mLSeatsMoveBackward.setSelected(false);
            mLSeat.setBackgroundResource(R.drawable.seat_l_highlight_3);
            return;
        }else if (i ==  mLSeatsFallHigh.getId()) {
            mLSeatsFallForward.setSelected(false);
            mLSeatsFallBackward.setSelected(false);
            mLSeatsFallUp.setSelected(false);
            mLSeatsFallDown.setSelected(false);
            mLSeatsFallHigh.setSelected(true);
            mLSeatsFallLow.setSelected(false);
            mLSeatsMoveForward.setSelected(false);
            mLSeatsMoveBackward.setSelected(false);
            mLSeat.setBackgroundResource(R.drawable.seat_l_highlight_1);
            return;
        }else if (i ==  mLSeatsFallLow.getId()) {
            mLSeatsFallForward.setSelected(false);
            mLSeatsFallBackward.setSelected(false);
            mLSeatsFallUp.setSelected(false);
            mLSeatsFallDown.setSelected(false);
            mLSeatsFallHigh.setSelected(false);
            mLSeatsFallLow.setSelected(true);
            mLSeatsMoveForward.setSelected(false);
            mLSeatsMoveBackward.setSelected(false);
            mLSeat.setBackgroundResource(R.drawable.seat_l_highlight_1);
            return;
        }else if (i ==  mLSeatsMoveForward.getId()) {
            mLSeatsFallForward.setSelected(false);
            mLSeatsFallBackward.setSelected(false);
            mLSeatsFallUp.setSelected(false);
            mLSeatsFallDown.setSelected(false);
            mLSeatsFallHigh.setSelected(false);
            mLSeatsFallLow.setSelected(false);
            mLSeatsMoveForward.setSelected(true);
            mLSeatsMoveBackward.setSelected(false);
            mLSeat.setBackgroundResource(R.drawable.seat_l_highlight_3);
            return;
        }else if (i ==  mLSeatsMoveBackward.getId()) {
            mLSeatsFallForward.setSelected(false);
            mLSeatsFallBackward.setSelected(false);
            mLSeatsFallUp.setSelected(false);
            mLSeatsFallDown.setSelected(false);
            mLSeatsFallHigh.setSelected(false);
            mLSeatsFallLow.setSelected(false);
            mLSeatsMoveForward.setSelected(false);
            mLSeatsMoveBackward.setSelected(true);
            mLSeat.setBackgroundResource(R.drawable.seat_l_highlight_3);
            return;
        }else if (i ==  mRSeatsFallForward.getId()) {
            mRSeatsFallForward.setSelected(true);
            mRSeatsFallBackward.setSelected(false);
            mRSeatsFallUp.setSelected(false);
            mRSeatsFallDown.setSelected(false);
            mRSeatsFallHigh.setSelected(false);
            mRSeatsFallLow.setSelected(false);
            mRSeatsMoveForward.setSelected(false);
            mRSeatsMoveBackward.setSelected(false);
            mRSeat.setBackgroundResource(R.drawable.seat_r_highlight_2);
            return;
        }else if (i ==  mRSeatsFallBackward.getId()) {
            mRSeatsFallForward.setSelected(false);
            mRSeatsFallBackward.setSelected(true);
            mRSeatsFallUp.setSelected(false);
            mRSeatsFallDown.setSelected(false);
            mRSeatsFallHigh.setSelected(false);
            mRSeatsFallLow.setSelected(false);
            mRSeatsMoveForward.setSelected(false);
            mRSeatsMoveBackward.setSelected(false);
            mRSeat.setBackgroundResource(R.drawable.seat_r_highlight_2);
            return;
        }else if (i ==  mRSeatsFallUp.getId()) {
            mRSeatsFallForward.setSelected(false);
            mRSeatsFallBackward.setSelected(false);
            mRSeatsFallUp.setSelected(true);
            mRSeatsFallDown.setSelected(false);
            mRSeatsFallHigh.setSelected(false);
            mRSeatsFallLow.setSelected(false);
            mRSeatsMoveForward.setSelected(false);
            mRSeatsMoveBackward.setSelected(false);
            mRSeat.setBackgroundResource(R.drawable.seat_r_highlight_3);
            return;
        }else if (i ==  mRSeatsFallDown.getId()) {
            mRSeatsFallForward.setSelected(false);
            mRSeatsFallBackward.setSelected(false);
            mRSeatsFallUp.setSelected(false);
            mRSeatsFallDown.setSelected(true);
            mRSeatsFallHigh.setSelected(false);
            mRSeatsFallLow.setSelected(false);
            mRSeatsMoveForward.setSelected(false);
            mRSeatsMoveBackward.setSelected(false);
            mRSeat.setBackgroundResource(R.drawable.seat_r_highlight_3);
            return;
        }else if (i ==  mRSeatsFallHigh.getId()) {
            mRSeatsFallForward.setSelected(false);
            mRSeatsFallBackward.setSelected(false);
            mRSeatsFallUp.setSelected(false);
            mRSeatsFallDown.setSelected(false);
            mRSeatsFallHigh.setSelected(true);
            mRSeatsFallLow.setSelected(false);
            mRSeatsMoveForward.setSelected(false);
            mRSeatsMoveBackward.setSelected(false);
            mRSeat.setBackgroundResource(R.drawable.seat_r_highlight_1);
            return;
        }else if (i ==  mRSeatsFallLow.getId()) {
            mRSeatsFallForward.setSelected(false);
            mRSeatsFallBackward.setSelected(false);
            mRSeatsFallUp.setSelected(false);
            mRSeatsFallDown.setSelected(false);
            mRSeatsFallHigh.setSelected(false);
            mRSeatsFallLow.setSelected(true);
            mRSeatsMoveForward.setSelected(false);
            mRSeatsMoveBackward.setSelected(false);
            mRSeat.setBackgroundResource(R.drawable.seat_r_highlight_1);
            return;
        }else if (i ==  mRSeatsMoveForward.getId()) {
            mRSeatsFallForward.setSelected(false);
            mRSeatsFallBackward.setSelected(false);
            mRSeatsFallUp.setSelected(false);
            mRSeatsFallDown.setSelected(false);
            mRSeatsFallHigh.setSelected(false);
            mRSeatsFallLow.setSelected(false);
            mRSeatsMoveForward.setSelected(true);
            mRSeatsMoveBackward.setSelected(false);
            mRSeat.setBackgroundResource(R.drawable.seat_r_highlight_3);
            return;
        }else if (i ==  mRSeatsMoveBackward.getId()) {
            mRSeatsFallForward.setSelected(false);
            mRSeatsFallBackward.setSelected(false);
            mRSeatsFallUp.setSelected(false);
            mRSeatsFallDown.setSelected(false);
            mRSeatsFallHigh.setSelected(false);
            mRSeatsFallLow.setSelected(false);
            mRSeatsMoveForward.setSelected(false);
            mRSeatsMoveBackward.setSelected(true);
            mRSeat.setBackgroundResource(R.drawable.seat_r_highlight_3);
            return;
        }else if (i ==   mSeatsLMessageReduce.getId()) {
            if ( mLMessageNum > 0) {
                mSeatsLMessageNumber.setText(Integer.toString(mLMessageNum-1));
            }else{

                mSeatsLMessageNumber.setText("0");
            }
            return;
        }else if (i ==   mSeatsLMessagePlus.getId()) {
            if (mLMessageNum < 7) {
                mSeatsLMessageNumber.setText( Integer.toString(mLMessageNum+1));
            }else{

                mSeatsLMessageNumber.setText("7");
            }
            return;
        }else if (i ==   mSeatsRMessageReduce.getId()) {
            if ( mRMessageNum > 0) {
                mSeatsRMessageNumber.setText(Integer.toString(mRMessageNum-1));
            }else{

                mSeatsRMessageNumber.setText("0");
            }
            return;
        }else if (i ==   mSeatsRMessagePlus.getId()) {
            if (mRMessageNum < 7) {
                mSeatsRMessageNumber.setText( Integer.toString(mRMessageNum+1));
            }else{

                mSeatsRMessageNumber.setText("7");
            }
            return;
        }else if (i == mLLockMask.getId()) {
            twoButtonDialog = new TwoButtonDialog(MainActivity.this);
            twoButtonDialog.setMessage("DO NOT adjust the driver seat while driving. You may lose control of the vehicle. Are you sure still want to unlock this function?");
            twoButtonDialog.setYesOnclickListener(new TwoButtonDialog.onYesOnclickListener() {
                @Override
                public void onYesClick() {
                    twoButtonDialog.dismiss();
                    mLLockMask.setVisibility(View.GONE);
                    //mRLockMask.setVisibility(View.GONE);
                    mSeatsWaringIcon.setVisibility(View.GONE);
                    mSeatsInstruction.setVisibility(View.VISIBLE);
                    mSeatsWaringInstruction.setVisibility(View.GONE);
                }
            });
            twoButtonDialog.setCancelOnclickListener(new TwoButtonDialog.onCancelOnclickListener() {
                @Override
                public void onCancelClick() {
                    twoButtonDialog.dismiss();
                }
            });
            //new Dialog(MainActivity.this).show();
            twoButtonDialog.show();
            return;
        }
        else if (i ==  mLMirrorUp.getId()) {
            mLMirrorUp.setSelected(true);
            mLMirrorDown.setSelected(false);
            mLMirrorLeft.setSelected(false);
            mLMirrorRight.setSelected(false);
            return;
        }else if (i ==  mLMirrorDown.getId()) {
            mLMirrorUp.setSelected(false);
            mLMirrorDown.setSelected(true);
            mLMirrorLeft.setSelected(false);
            mLMirrorRight.setSelected(false);
            return;
        }else if (i ==  mLMirrorLeft.getId()) {
            mLMirrorUp.setSelected(false);
            mLMirrorDown.setSelected(false);
            mLMirrorLeft.setSelected(true);
            mLMirrorRight.setSelected(false);
            return;
        }else if (i ==  mLMirrorRight.getId()) {
            mLMirrorUp.setSelected(false);
            mLMirrorDown.setSelected(false);
            mLMirrorLeft.setSelected(false);
            mLMirrorRight.setSelected(true);
            return;
        }else if (i ==  mRMirrorUp.getId()) {
            mRMirrorUp.setSelected(true);
            mRMirrorDown.setSelected(false);
            mRMirrorLeft.setSelected(false);
            mRMirrorRight.setSelected(false);
            return;
        }else if (i ==  mRMirrorDown.getId()) {
            mRMirrorUp.setSelected(false);
            mRMirrorDown.setSelected(true);
            mRMirrorLeft.setSelected(false);
            mRMirrorRight.setSelected(false);
            return;
        }else if (i ==  mRMirrorLeft.getId()) {
            mRMirrorUp.setSelected(false);
            mRMirrorDown.setSelected(false);
            mRMirrorLeft.setSelected(true);
            mRMirrorRight.setSelected(false);
            return;
        }else if (i ==  mRMirrorRight.getId()) {
            mRMirrorUp.setSelected(false);
            mRMirrorDown.setSelected(false);
            mRMirrorLeft.setSelected(false);
            mRMirrorRight.setSelected(true);
            return;
        } else if (i ==  mMirrorFolderDisplayIcon.getId()) {
            if (isMirrorOpen == false) {
                mMirrorFolderDisplayIcon.setSelected(true);
                isMirrorOpen = true;
            }else{
                mMirrorFolderDisplayIcon.setSelected(false);
                isMirrorOpen = false;
            }
            return;
        }else if (i ==  mSteerControlArrowUp.getId()) {
            mSteerControlArrowUp.setSelected(true);
            mSteerControlArrowDown.setSelected(false);
            mSteerControlArrowForward.setSelected(false);
            mSteerControlArrowBackward.setSelected(false);
            return;
        }else if (i == mSteerControlArrowDown.getId()) {
            mSteerControlArrowUp.setSelected(false);
            mSteerControlArrowDown.setSelected(true);
            mSteerControlArrowForward.setSelected(false);
            mSteerControlArrowBackward.setSelected(false);
            return;
        }else if (i == mSteerControlArrowForward.getId()) {
            mSteerControlArrowUp.setSelected(false);
            mSteerControlArrowDown.setSelected(false);
            mSteerControlArrowForward.setSelected(true);
            mSteerControlArrowBackward.setSelected(false);
            return;
        }else if (i == mSteerControlArrowBackward.getId()) {
            mSteerControlArrowUp.setSelected(false);
            mSteerControlArrowDown.setSelected(false);
            mSteerControlArrowForward.setSelected(false);
            mSteerControlArrowBackward.setSelected(true);
            return;
        }
    }
    public boolean isDoorLockOpen() {
        if (isLeftFrontDoorLock && isLeftRearDoorLock && isRightFrontDoorLock && isRightRearDoorLock) {
            return true;
        }
        return false;
    }
    private void resetSeatsToNormal(){
        mRSeatsFallForward.setSelected(false);
        mRSeatsFallBackward.setSelected(false);
        mRSeatsFallUp.setSelected(false);
        mRSeatsFallDown.setSelected(false);
        mRSeatsFallHigh.setSelected(false);
        mRSeatsFallLow.setSelected(false);
        mRSeatsMoveForward.setSelected(false);
        mRSeatsMoveBackward.setSelected(false);
        mLSeatsFallForward.setSelected(false);
        mLSeatsFallBackward.setSelected(false);
        mLSeatsFallUp.setSelected(false);
        mLSeatsFallDown.setSelected(false);
        mLSeatsFallHigh.setSelected(false);
        mLSeatsFallLow.setSelected(false);
        mLSeatsMoveForward.setSelected(false);
        mLSeatsMoveBackward.setSelected(false);
        mLSeat.setBackgroundResource(R.drawable.seat_l_normal);
        mRSeat.setBackgroundResource(R.drawable.seat_r_normal);
    }
    private void resetMirrorToNormal(){
        mLMirrorUp.setSelected(false);
        mLMirrorDown.setSelected(false);
        mLMirrorLeft.setSelected(false);
        mLMirrorRight.setSelected(false);
        mRMirrorUp.setSelected(false);
        mRMirrorDown.setSelected(false);
        mRMirrorLeft.setSelected(false);
        mRMirrorRight.setSelected(false);
    }
    private void resetSteeringToNormal(){
        mSteerControlArrowUp.setSelected(false);
        mSteerControlArrowDown.setSelected(false);
        mSteerControlArrowForward.setSelected(false);
        mSteerControlArrowBackward.setSelected(false);
    }
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                mToastContent.setVisibility(View.GONE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
}
