package com.fxc.ev.zuocang;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;

import com.fxc.ev.zuocang.adapter.MenuListAdapter;
import com.fxc.libCanWrapperNDK.ICanStCallback;
import com.fxc.libCanWrapperNDK.IMyAidlInterface2;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

    public class MainActivity extends Activity implements View.OnClickListener {
        protected ListView mMenuListView;
        String[] itemName = {"Doors,Windows", "Lights", "Drive Mode", "Seats", "Mirrors", "Steering"};
        private String TAG = "MainActivity";
        private MenuListAdapter mListAdapter;
        private Switch mAmbientSwitch, mFrontlightSystemSwitch,mKeyNearDoorAutoUnlockSwitch,mShiftingDoorAutoUnlockSwitch,mDoorAutoLockSwitch,mMirrorAutoFoldSwitch1,mMirrorAutoFoldSwitch2,mMirrorAutoTiltSwitch;
        private LinearLayout mAmbientDisplayLayout;
        private OneButtonDialog oneButtonDialog;
        private TwoButtonDialog twoButtonDialog;
        private Button mDriveModeComfort, mDriveModeNormal, mDriveModeSport;
        private ImageView mDriveModeDisplayImage,mAmbientModeOriginal,mAmbientModePassion,mAmbientModeFlowing,mAmbientModeWave;
        private ImageView mAmbientModeDisplayImage,mAmbientModeStars,mAmbientModeRainBow,mAmbientModeRunning,mAmbientModeRhythm;
        private ImageView mExteriorLightAuto,mExteriorHighBeamLight,mExteriorLowBeamLight,mExteriorLightOff,mHighBeamLightAuto;
        private ImageView mFrogFrontLight,mFrogRearLight;
        private ImageView mReadingLightl,mReadingLight,mReadingLightR,mPuddleLight;
        private DismissControlViewTimerTask mDismissControlViewTimerTask;
        private Timer mDismissControlViewTimer;
        private boolean isWindowLockOpen;
        private int frontFogLightFlag=0; //0:normal;1:active;-1:disable
        private boolean isInPCondition = false;
        private boolean isDoorOpen = false;
        private ArrayList<Integer> images = new ArrayList<>();
        private ScrollView mScrollView;
        private ImageView mWindowFrontLeftUp,mWindowFrontLeft,mWindowFrontLeftDown,mWindowRearLeftUp,mWindowRearLeft,mWindowRearLeftDown;
        private ImageView mWindowFrontRightUp,mWindowFrontRight,mWindowFrontRightDown,mWindowRearRightUp,mWindowRearRight,mWindowRearRightDown;
        private ImageView mWindowLock,mDoorLock,mChildLock,mSunCurtain,mLeftChildLockIcon,mRightChildLockIcon;
        private ImageView mLeftFrontDoorLock,mLeftRearDoorLock,mRightFrontDoorLock,mRightRearDoorLock;
        private ImageView mRearLuggageTrunk,mLuggageFrunk,mLuggagedormer,mSteeringWaringIcon,mMirrorFolderDisplayIcon,mSeatsWaringIcon;
        private TextView mSteeringInstruction,mSteeringWaringInstruction,mSeatsInstruction,mSeatsWaringInstruction,mSeatsLMessageNumber,mSeatsRMessageNumber,mToastContent;
        private ImageView mLSeatsFallForward,mLSeatsFallBackward,mLSeatsFallUp,mLSeatsFallDown,mLSeatsFallHigh,mLSeatsFallLow,mLSeatsMoveForward,mLSeatsMoveBackward,mLSeat;
        private ImageView mRSeatsFallForward,mRSeatsFallBackward,mRSeatsFallUp,mRSeatsFallDown,mRSeatsFallHigh,mRSeatsFallLow,mRSeatsMoveForward,mRSeatsMoveBackward,mRSeat;
        private ImageView mLLockMask,mRLockMask;
        private Button mSeatsLMessageReduce,mSeatsRMessageReduce,mSeatsLMessagePlus,mSeatsRMessagePlus;
        private ImageView mLMirrorUp,mLMirrorDown,mLMirrorLeft,mLMirrorRight,mRMirrorUp,mRMirrorDown,mRMirrorLeft,mRMirrorRight;
        private ImageView mSteerControlArrowUp,mSteerControlArrowDown,mSteerControlArrowForward,mSteerControlArrowBackward;
        static private int mLMessageNum=1,mRMessageNum=1;
        private LinearLayout mServiceBindInfo;
        private Handler handler = new Handler();
        private IMyAidlInterface2 iMyAidlInterface2;
        private SharedPreferences sp;
        private Boolean isAmbientModeOriginalClick=true;
        private Boolean isAmbientModePassionClick=false;
        private Boolean isAmbientModeFlowingClick=false;
        private Boolean isAmbientModeWaveClick=false;
        private Boolean isAmbientModeStarsClick=false;
        private Boolean isAmbientModeRainbowClick=false;
        private Boolean isAmbientModeRunningClick=false;
        private Boolean isAmbientModeRhythmClick=false;
        private boolean isDoorLockOpen = false;
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
        private boolean isRearLuggageTrunkEnable = true;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startAndConnectService();
        setContentView(R.layout.activity_main);
        sp=this.getSharedPreferences("user",Context.MODE_PRIVATE);
        initView();
        mServiceBindInfo = findViewById(R.id.service_bind_information);
        mServiceBindInfo.postDelayed(new Runnable() {
            @Override
            public void run() {
                mServiceBindInfo.setVisibility(View.GONE);
                loadCurrentSetting();
            }
        }, 2500);


    }
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.d(TAG, "onServiceConnected");
            iMyAidlInterface2 = IMyAidlInterface2.Stub.asInterface(iBinder);
            try {
                iMyAidlInterface2.registerCallback(mICanStCallback);
                Log.d(TAG, "onCallBack");
            } catch (RemoteException e) {
                e.printStackTrace();
            }

            /*try{
                iMyAidlInterface2.register(iCanStCallback);
            } catch (RemoteException e){
                e.printStackTrace();
            }*/
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            iMyAidlInterface2 = null;
            System.out.println("iMyAidlInterface2 Disconnected");

        }
    };

    private void startAndConnectService(){
        Log.d(TAG, "startAndConnectService");
        Intent intent = new Intent();
        intent.setPackage("com.fxc.libCanWrapper");
        intent.setAction("com.fxc.libCanWrapperNDK.MyService");
        bindService(intent, conn, Context.BIND_AUTO_CREATE);
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
        mShiftingDoorAutoUnlockSwitch = findViewById(R.id.shifting_door_auto_unlock_switch);
        mDoorAutoLockSwitch = findViewById(R.id.door_auto_lock);
        mMirrorAutoFoldSwitch1 = findViewById(R.id.mirror_auto_fold_switch1);
        mMirrorAutoFoldSwitch2 = findViewById(R.id.mirror_auto_fold_switch2);
        mMirrorAutoTiltSwitch = findViewById(R.id.mirror_auto_tilt_switch);
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
        mExteriorLightOff = findViewById(R.id.exterior_light_off);
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
        mLuggageFrunk = findViewById(R.id.luggage_frunk);
        mLuggagedormer = findViewById(R.id.luggage_dormer);
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
//        mFrogFrontLight.setEnabled(true);
//        mFrogRearLight.setEnabled(true);
       // mExteriorLightAuto.setSelected(true);
        //mDriveModeNormal.setSelected(true);
        //mAmbientModeOriginal.setSelected(true);
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
        mExteriorLightOff.setOnClickListener(this);
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
        mLuggageFrunk.setOnClickListener(this);
        mLuggagedormer.setOnClickListener(this);
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
                        sp.edit().putInt("mMenuListClick",0).commit();
                        mMenuListView.getChildAt(0).setSelected(true);
                        mMenuListView.getChildAt(1).setSelected(false);
                        mMenuListView.getChildAt(2).setSelected(false);
                        mMenuListView.getChildAt(3).setSelected(false);
                        mMenuListView.getChildAt(4).setSelected(false);
                        mMenuListView.getChildAt(5).setSelected(false);
                    }else if((oldY>=1189 && y<2111)){
                        sp.edit().putInt("mMenuListClick",1).commit();
                        mMenuListView.getChildAt(0).setSelected(false);
                        mMenuListView.getChildAt(1).setSelected(true);
                        mMenuListView.getChildAt(2).setSelected(false);
                        mMenuListView.getChildAt(3).setSelected(false);
                        mMenuListView.getChildAt(4).setSelected(false);
                        mMenuListView.getChildAt(5).setSelected(false);
                        resetSeatsToNormal();
                    }else if((oldY>=2111 && y<2705)){
                        sp.edit().putInt("mMenuListClick",2).commit();
                        mMenuListView.getChildAt(0).setSelected(false);
                        mMenuListView.getChildAt(1).setSelected(false);
                        mMenuListView.getChildAt(2).setSelected(true);
                        mMenuListView.getChildAt(3).setSelected(false);
                        mMenuListView.getChildAt(4).setSelected(false);
                        mMenuListView.getChildAt(5).setSelected(false);
                        resetMirrorToNormal();
                    }else if((oldY>=2705 && y<3455)){
                        sp.edit().putInt("mMenuListClick",3).commit();
                        mMenuListView.getChildAt(0).setSelected(false);
                        mMenuListView.getChildAt(1).setSelected(false);
                        mMenuListView.getChildAt(2).setSelected(false);
                        mMenuListView.getChildAt(3).setSelected(true);
                        mMenuListView.getChildAt(4).setSelected(false);
                        mMenuListView.getChildAt(5).setSelected(false);
                        resetSteeringToNormal();
                    }else if((oldY>=3455 && y<4298)){
                        sp.edit().putInt("mMenuListClick",4).commit();
                        mMenuListView.getChildAt(0).setSelected(false);
                        mMenuListView.getChildAt(1).setSelected(false);
                        mMenuListView.getChildAt(2).setSelected(false);
                        mMenuListView.getChildAt(3).setSelected(false);
                        mMenuListView.getChildAt(4).setSelected(true);
                        mMenuListView.getChildAt(5).setSelected(false);
                        resetSeatsToNormal();
                    }else if((oldY>=4298 && y<5298)){
                        sp.edit().putInt("mMenuListClick",5).commit();
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
                        sp.edit().putInt("mMenuListClick",0).commit();
                        mMenuListView.getChildAt(0).setSelected(true);
                        mMenuListView.getChildAt(1).setSelected(false);
                        mMenuListView.getChildAt(2).setSelected(false);
                        mMenuListView.getChildAt(3).setSelected(false);
                        mMenuListView.getChildAt(4).setSelected(false);
                        mMenuListView.getChildAt(5).setSelected(false);
                    }else if((oldY>=1189 && y<2761)){
                        sp.edit().putInt("mMenuListClick",1).commit();
                        mMenuListView.getChildAt(0).setSelected(false);
                        mMenuListView.getChildAt(1).setSelected(true);
                        mMenuListView.getChildAt(2).setSelected(false);
                        mMenuListView.getChildAt(3).setSelected(false);
                        mMenuListView.getChildAt(4).setSelected(false);
                        mMenuListView.getChildAt(5).setSelected(false);
                        resetSeatsToNormal();
                    }else if((oldY>=2761 && y<3355)){
                        sp.edit().putInt("mMenuListClick",2).commit();
                        mMenuListView.getChildAt(0).setSelected(false);
                        mMenuListView.getChildAt(1).setSelected(false);
                        mMenuListView.getChildAt(2).setSelected(true);
                        mMenuListView.getChildAt(3).setSelected(false);
                        mMenuListView.getChildAt(4).setSelected(false);
                        mMenuListView.getChildAt(5).setSelected(false);
                        resetMirrorToNormal();
                    }else if((oldY>=3355 && y<4105)){
                        sp.edit().putInt("mMenuListClick",3).commit();
                        mMenuListView.getChildAt(0).setSelected(false);
                        mMenuListView.getChildAt(1).setSelected(false);
                        mMenuListView.getChildAt(2).setSelected(false);
                        mMenuListView.getChildAt(3).setSelected(true);
                        mMenuListView.getChildAt(4).setSelected(false);
                        mMenuListView.getChildAt(5).setSelected(false);
                        resetSteeringToNormal();
                    }else if((oldY>=4105 && y<4988)){
                        sp.edit().putInt("mMenuListClick",4).commit();
                        mMenuListView.getChildAt(0).setSelected(false);
                        mMenuListView.getChildAt(1).setSelected(false);
                        mMenuListView.getChildAt(2).setSelected(false);
                        mMenuListView.getChildAt(3).setSelected(false);
                        mMenuListView.getChildAt(4).setSelected(true);
                        mMenuListView.getChildAt(5).setSelected(false);
                        resetSeatsToNormal();
                    }else if((oldY>=4948 && y<5948)){
                        sp.edit().putInt("mMenuListClick",5).commit();
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
       /* mMenuListView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mMenuListView.getChildAt(0).setSelected(true);
            }
        }, 500);*/

        mMenuListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                view.setSelected(true);
                if(mAmbientDisplayLayout.getVisibility()==View.GONE) {
                    if(position==0){
                        sp.edit().putInt("mMenuListClick",0).commit();
                        mScrollView.scrollTo(60,0);
                    }else if(position==1){
                        resetSeatsToNormal();
                        mScrollView.scrollTo(60,1195);
                        sp.edit().putInt("mMenuListClick",1).commit();
                    }else if(position==2){
                        sp.edit().putInt("mMenuListClick",2).commit();
                        mScrollView.scrollTo(60, 2117);
                        resetMirrorToNormal();
                    }else if(position==3){
                        sp.edit().putInt("mMenuListClick",3).commit();
                        mScrollView.scrollTo(60, 2715);
                        mScrollView.getHeight();
                        resetSteeringToNormal();
                    }else if(position==4){
                        sp.edit().putInt("mMenuListClick",4).commit();
                        mScrollView.scrollTo(60, 3461);
                        resetSeatsToNormal();
                    }else if(position==5){
                        sp.edit().putInt("mMenuListClick",5).commit();
                        resetMirrorToNormal();
                        mScrollView.scrollTo(60, 4304);
                        mMenuListView.getChildAt(5).setSelected(true);
                    }
                }else{
                    if(position==0){
                        sp.edit().putInt("mMenuListClick",0).commit();
                        mScrollView.scrollTo(60,0);
                    }else if(position==1){
                        sp.edit().putInt("mMenuListClick",1).commit();
                        resetSeatsToNormal();
                        mScrollView.scrollTo(60,1195);
                    }else if(position==2){
                        sp.edit().putInt("mMenuListClick",2).commit();
                        mScrollView.scrollTo(60, 2767);
                    }else if(position==3){
                        sp.edit().putInt("mMenuListClick",3).commit();
                        mScrollView.scrollTo(60, 3365);
                    }else if(position==4){
                        sp.edit().putInt("mMenuListClick",4).commit();
                        mScrollView.scrollTo(60, 4111);
                        resetSeatsToNormal();
                    }else if(position==5){
                        sp.edit().putInt("mMenuListClick",5).commit();
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
                    if (iMyAidlInterface2 != null) {
                        try {
                            iMyAidlInterface2.setCanData("ONE,IVI_MoodLightSwitchSet_Req,On");
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                    mAmbientSwitch.setChecked(true);
                    sp.edit().putBoolean("mAmbientSwitchOpen",true).commit();
                    mAmbientDisplayLayout.setVisibility(View.VISIBLE);

                } else {
                    if (iMyAidlInterface2 != null) {
                        try {
                            iMyAidlInterface2.setCanData("ONE,IVI_MoodLightSwitchSet_Req,Off");
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                    mAmbientSwitch.setChecked(false);
                    sp.edit().putBoolean("mAmbientSwitchOpen",false).commit();
                    mAmbientDisplayLayout.setVisibility(View.GONE);
                }
            }
        });
        mFrontlightSystemSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mFrontlightSystemSwitch.setChecked(true);
                    sp.edit().putBoolean("mFrontLightSystemSwitchOpen",true).commit();
                    oneButtonDialog = new OneButtonDialog(MainActivity.this);

                    oneButtonDialog.setMessage("The Adaptive Front Lighting System (AFS) moves the light projector to the left or right automatically with changes in car speed and steering wheel angle. It increase the effective lighting range and allows the driver to see the road clearly when they???re turning towards, helping to avoid blind spots.");
                    oneButtonDialog.setYesOnclickListener(new OneButtonDialog.onYesOnclickListener() {
                        @Override
                        public void onYesClick() {
                            oneButtonDialog.dismiss();
                        }
                    });
                    //new Dialog(MainActivity.this).show();
                    oneButtonDialog.show();

                } else {
                    sp.edit().putBoolean("mFrontLightSystemSwitchOpen",false).commit();
                    mFrontlightSystemSwitch.setChecked(false);
                }
            }
        });
        mKeyNearDoorAutoUnlockSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mKeyNearDoorAutoUnlockSwitch.setChecked(true);
                    sp.edit().putBoolean("mKeyNearDoorAutoUnlockSwitchOpen",true).commit();
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
                    sp.edit().putBoolean("mKeyNearDoorAutoUnlockSwitchOpen",false).commit();
                    mKeyNearDoorAutoUnlockSwitch.setChecked(false);
                }
            }
        });

        mShiftingDoorAutoUnlockSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mShiftingDoorAutoUnlockSwitch.setChecked(true);
                    sp.edit().putBoolean("mShiftingDoorAutoUnlockSwitchOpen",true).commit();
                } else {
                    mShiftingDoorAutoUnlockSwitch.setChecked(false);
                    sp.edit().putBoolean("mShiftingDoorAutoUnlockSwitchOpen",false).commit();
                }
            }
        });
        mDoorAutoLockSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mDoorAutoLockSwitch.setChecked(true);
                    sp.edit().putBoolean("mDoorAutoLockSwitchOpen",true).commit();
                } else {
                    mDoorAutoLockSwitch.setChecked(false);
                    sp.edit().putBoolean("mDoorAutoLockSwitchOpen",false).commit();
                }
            }
        });
        mMirrorAutoFoldSwitch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mMirrorAutoFoldSwitch1.setChecked(true);
                    sp.edit().putBoolean("mMirrorAutoFoldSwitch1Open",true).commit();
                } else {
                    mMirrorAutoFoldSwitch1.setChecked(false);
                    sp.edit().putBoolean("mMirrorAutoFoldSwitch1Open",false).commit();
                }
            }
        });
        mMirrorAutoFoldSwitch2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mMirrorAutoFoldSwitch2.setChecked(true);
                    sp.edit().putBoolean("mMirrorAutoFoldSwitch2Open",true).commit();
                } else {
                    mMirrorAutoFoldSwitch2.setChecked(false);
                    sp.edit().putBoolean("mMirrorAutoFoldSwitch2Open",false).commit();
                }
            }
        });
        mMirrorAutoTiltSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mMirrorAutoTiltSwitch.setChecked(true);
                    sp.edit().putBoolean("mMirrorAutoTiltSwitchOpen",true).commit();
                } else {
                    mMirrorAutoTiltSwitch.setChecked(false);
                    sp.edit().putBoolean("mMirrorAutoTiltSwitchOpen",false).commit();
                }
            }
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");

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
    private String getDoorLockSeparateStatus(int whichDoor) { //????????????1???????????????2???????????????3???????????????4???????????????5
        String getCanData = "";
        Log.d(TAG, "getDoorLockSeparateStatus1: " + getCanData);
        switch (whichDoor) {
            case 1:
                try {
                    getCanData = iMyAidlInterface2.getReqCanData("ONE,ZGW_DoorLockDrive_St");
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case 2:
                try {
                    getCanData = iMyAidlInterface2.getReqCanData("ONE,ZGW_DoorLockRL_St");
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case 3:
                try {
                    getCanData = iMyAidlInterface2.getReqCanData("ONE,ZGW_DoorLockPassenger_St");
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case 4:
                try {
                    getCanData = iMyAidlInterface2.getReqCanData("ONE,ZGW_DoorLockRR_St");
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case 5:
                try {
                    getCanData = iMyAidlInterface2.getReqCanData("ONE,ZGW_TrunkDoor_St");
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
        Log.d(TAG, "getDoorLockSeparateStatus: " + getCanData);
        return getCanData;
    }
   /* private void syncDoorLockBehavior() {
        if (doorLockFlag == -1) {
            mDoorLock.setEnabled(false);
        } else if (doorLockFlag == 0) {
            mDoorLock.setEnabled(true);
            mDoorLock.setSelected(false);
        } else if (doorLockFlag == 1) {
            mDoorLock.setEnabled(true);
            mDoorLock.setSelected(true);
        }
    }*/

  /*  private void syncLuggageTrunkBehavior() {
        if (luggageTrunkFlag == -1) {
            //luggageTrunk.setEnabled(false);
            mRearLuggageTrunk.setActivated(false);
        } else if (luggageTrunkFlag == 0) {
            //luggageTrunk.setEnabled(true);
            mRearLuggageTrunk.setActivated(true);
            mRearLuggageTrunk.setSelected(false);
        } else if (luggageTrunkFlag == 1) {
            //luggageTrunk.setEnabled(true);
            mRearLuggageTrunk.setActivated(true);
            mRearLuggageTrunk.setSelected(true);
        }
    }*/
   /* private void syncDoorLockSeparateBehavior(int whichDoor) { //????????????1???????????????2???????????????3???????????????4
        switch (whichDoor) {
            case 1:
                if (doorLockLeft1Flag == -1) {
                    mLeftFrontDoorLock.setEnabled(false);
                } else if (doorLockLeft1Flag == 0) {
                    mLeftFrontDoorLock.setEnabled(true);
                    mLeftFrontDoorLock.setSelected(false);
                } else if (doorLockLeft1Flag == 1) {
                    mLeftFrontDoorLock.setEnabled(true);
                    mLeftFrontDoorLock.setSelected(true);
                }
                break;
            case 2:
                if (doorLockLeft2Flag == -1) {
                    mLeftRearDoorLock.setEnabled(false);
                } else if (doorLockLeft2Flag == 0) {
                    mLeftRearDoorLock.setEnabled(true);
                    mLeftRearDoorLock.setSelected(false);
                } else if (doorLockLeft2Flag == 1) {
                    mLeftRearDoorLock.setEnabled(true);
                    mLeftRearDoorLock.setSelected(true);
                }
                break;
            case 3:
                if (doorLockRight1Flag == -1) {
                    mRightFrontDoorLock.setEnabled(false);
                } else if (doorLockRight1Flag == 0) {
                    mRightFrontDoorLock.setEnabled(true);
                    mRightFrontDoorLock.setSelected(false);
                } else if (doorLockRight1Flag == 1) {
                    mRightFrontDoorLock.setEnabled(true);
                    mRightFrontDoorLock.setSelected(true);
                }
                break;
            case 4:
                if (doorLockRight2Flag == -1) {
                    mRightRearDoorLock.setEnabled(false);
                } else if (doorLockRight2Flag == 0) {
                    mRightRearDoorLock.setEnabled(true);
                    mRightRearDoorLock.setSelected(false);
                } else if (doorLockRight2Flag == 1) {
                    mRightRearDoorLock.setEnabled(true);
                    mRightRearDoorLock.setSelected(true);
                }
                break;
            default:
                break;

        }

    }*/
    private void processHeadLampStatus() {
        String getCanData1 = "";
        String getCanData2 = "";
        try {
            getCanData1 = iMyAidlInterface2.getReqCanData("TWO,ZGW_HeadLampPower_St");
            getCanData2 = iMyAidlInterface2.getReqCanData("TWO,ZGW_HighBeamPower_St");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "processHeadLampStatus1: " + getCanData1);
        Log.d(TAG, "processHeadLampStatus2: " + getCanData2);
        if (getCanData1.equals("Closed") || sp.getInt("mExteriorLightClick",0) == 3) {
           mExteriorLightOff.setSelected(true);
           mFrogFrontLight.setEnabled(false);
           mFrogRearLight.setEnabled(false);
            mFrogFrontLight.setSelected(false);
            mFrogRearLight.setSelected(false);
            //frontFogLightFlag = 0;
            //rearFogLightFlag = 0;
        } else if ((getCanData1.equals("Opened") && getCanData2.equals("Closed")) || sp.getInt("mExteriorLightClick",0) == 2) {
            mExteriorLowBeamLight.setSelected(true);
            mFrogFrontLight.setEnabled(true);
            mFrogRearLight.setEnabled(true);
           /* frontFogLightFlag = 0;
            rearFogLightFlag = 0;*/
        } else if ((getCanData1.equals("Opened") && getCanData2.equals("Opened")) || sp.getInt("mExteriorLightClick",0) == 1) {
           mExteriorHighBeamLight.setSelected(true);
            mFrogFrontLight.setEnabled(true);
            mFrogRearLight.setEnabled(true);
            /*frontFogLightFlag = 0;
            rearFogLightFlag = 0;*/
        } else if(getCanData1.equals("Auto") || sp.getInt("mExteriorLightClick",0) == 0){
            mExteriorLightAuto.setSelected(true);
            mFrogFrontLight.setEnabled(true);
            mFrogRearLight.setEnabled(true);
          /*  frontFogLightFlag = 0;
            rearFogLightFlag = 0;*/
        }
    }
    private String getRearViewMirrorStatus() {
        String getCanData = "";
        try {
            getCanData = iMyAidlInterface2.getReqCanData("TWO,ZGW_RearMirrorOpen_St");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "getRearViewMirrorStatus: " + getCanData);
        return getCanData;
    }
    private String getLReadLightStatus() {
        String getCanData = "";
        try {
            getCanData = iMyAidlInterface2.getReqCanData("TWO,ZGW_LeftMapLamp_St");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "getLReadLightStatus: " + getCanData);
        return getCanData;
    }
    private String getReadLightStatus() {
        String getCanData = "";
        try {
            getCanData = iMyAidlInterface2.getReqCanData("TWO,ZGW_RoomLamp_St");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "getReadLightStatus: " + getCanData);
        return getCanData;
    }
    private String getRReadLightStatus() {
        String getCanData = "";
        try {
            getCanData = iMyAidlInterface2.getReqCanData("TWO,ZGW_RightMapLamp_St");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "getRReadLightStatus: " + getCanData);
        return getCanData;
    }
    private void syncWindowAdjustBehavior(Boolean windowLockFlag) {
        if (windowLockFlag) {
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
        } else {
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
        }
    }
    private String getAmbientLightAndDriveModeStatus() {
        String getCanData = "";
        try {
            getCanData = iMyAidlInterface2.getReqCanData("ONE,NU_MoodLightMode_St");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "getAmbientLightAndDriveModeStatus: " + getCanData);
        return getCanData;
    }
    private String getAmbientLightSwitchStatus() {
        String getCanData = "";
        try {
            getCanData = iMyAidlInterface2.getReqCanData("ONE,NU_MoodLightSwitch_St");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "getAmbientLightSwitchStatus: " + getCanData);
        return getCanData;
    }

    private void startDismissControlViewTimer() {
        cancelDismissControlViewTimer();
        mDismissControlViewTimer = new Timer();
        mDismissControlViewTimerTask = new DismissControlViewTimerTask();
        mDismissControlViewTimer.schedule(mDismissControlViewTimerTask, mDismissControlTime);
    }
    private int mDismissControlTime = 15000;
    private class DismissControlViewTimerTask extends TimerTask {

        @Override
        public void run() {
            if(isAmbientModeOriginalClick){
                if (iMyAidlInterface2 != null) {
                    try {
                        iMyAidlInterface2.setCanData("ONE,IVI_MoodLightModeSet_Req,Original");
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }else if(isAmbientModePassionClick) {
                if (iMyAidlInterface2 != null) {
                    try {
                        iMyAidlInterface2.setCanData("ONE,IVI_MoodLightModeSet_Req,Passion");
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }else if(isAmbientModeFlowingClick){
                    if (iMyAidlInterface2 != null) {
                        try {
                            iMyAidlInterface2.setCanData("ONE,IVI_MoodLightModeSet_Req,Flowing");
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
            }else if(isAmbientModeWaveClick) {
                if (iMyAidlInterface2 != null) {
                    try {
                        iMyAidlInterface2.setCanData("ONE,IVI_MoodLightModeSet_Req,Wave");
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }else if(isAmbientModeStarsClick) {
                if (iMyAidlInterface2 != null) {
                    try {
                        iMyAidlInterface2.setCanData("ONE,IVI_MoodLightModeSet_Req,Stars");
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }else if(isAmbientModeRainbowClick) {
                if (iMyAidlInterface2 != null) {
                    try {
                        iMyAidlInterface2.setCanData("ONE,IVI_MoodLightModeSet_Req,Rainbow");
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }else if(isAmbientModeRunningClick) {
                if (iMyAidlInterface2 != null) {
                    try {
                        iMyAidlInterface2.setCanData("ONE,IVI_MoodLightModeSet_Req,Running");
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }else if(isAmbientModeRhythmClick) {
                if (iMyAidlInterface2 != null) {
                    try {
                        iMyAidlInterface2.setCanData("ONE,IVI_MoodLightModeSet_Req,Rhythm");
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    private void cancelDismissControlViewTimer() {
        if (mDismissControlViewTimer != null) {
            mDismissControlViewTimer.cancel();
            mDismissControlViewTimer = null;
        }
        if (mDismissControlViewTimerTask != null) {
            mDismissControlViewTimerTask.cancel();
            mDismissControlViewTimerTask = null;
        }
    }
    private ICanStCallback mICanStCallback = new ICanStCallback.Stub() {
        @Override
        public void onCallback(String aString) throws RemoteException {
            Log.d(TAG, "onCallback: " + aString);
            String[] separated = aString.split(",");
            if (separated[1].equals("ZGW_LeftMapLamp_St")) {
                if (separated[2].equals("Open")) {
                    mReadingLightl.setSelected(true);
                    //isReadingLightLOpen = true;
                    sp.edit().putBoolean("isReadingLightLOpenClick", true).commit();
                } else if (separated[2].equals("Close")) {
                    mReadingLightl.setSelected(false);
                    //isReadingLightLOpen = false;
                    sp.edit().putBoolean("isReadingLightLOpenClick", false).commit();
                } else {
                    Log.d(TAG, "onCallback1: " + aString);
                    mReadingLightl.setSelected(false);
                    //isReadingLightLOpen = false;
                    sp.edit().putBoolean("isReadingLightLOpenClick", false).commit();
                }
            } else if (separated[1].equals("ZGW_RightMapLamp_St")) {
                if (separated[2].equals("Open")) {
                    mReadingLightR.setSelected(true);
                    //isReadingLightROpen = true;
                    sp.edit().putBoolean("isReadingLightROpenClick", true).commit();
                } else if (separated[2].equals("Close")) {
                    sp.edit().putBoolean("isReadingLightROpenClick", false).commit();
                    mReadingLightR.setSelected(false);
                    //isReadingLightROpen = false;
                }/*else{
                    Log.d(TAG, "onCallback1: " + aString);
                    sp.edit().putBoolean("isReadingLightROpenClick",false).commit();
                    Log.d(TAG, "loadCurrentSetting5"+isReadingLightROpen);
                    mReadingLightR.setSelected(false);
                    isReadingLightROpen = false;
                }*/
            } else if (separated[1].equals("ZGW_RoomLamp_St")) {
                if (separated[2].equals("Open")) {
                    mReadingLight.setSelected(true);
                    //isReadingLightOpen = true;
                    sp.edit().putBoolean("isReadingLightOpenClick", true).commit();
                } else if (separated[2].equals("Close")) {
                    mReadingLight.setSelected(false);
                    //isReadingLightOpen = false;
                    sp.edit().putBoolean("isReadingLightOpenClick", false).commit();
                } else {
                    Log.d(TAG, "onCallback1: " + aString);
                    mReadingLight.setSelected(false);
                    sp.edit().putBoolean("isReadingLightOpenClick", false).commit();
                    //isReadingLightOpen = false;
                }
            } else if (separated[1].equals("ZGW_RearMirrorOpen_St")) {
                if (separated[2].equals("Open")) {
                    mMirrorFolderDisplayIcon.setSelected(true);
                    //isMirrorOpen = true;
                    sp.edit().putBoolean("isMirrorOpenClick", true).commit();
                } else if (separated[2].equals("Close")) {
                    mMirrorFolderDisplayIcon.setSelected(false);
                    //isMirrorOpen = false;
                    sp.edit().putBoolean("isMirrorOpenClick", false).commit();
                } else {
                    Log.d(TAG, "onCallback1: " + aString);
                    mMirrorFolderDisplayIcon.setSelected(false);
                   // isMirrorOpen = false;
                    sp.edit().putBoolean("isMirrorOpenClick", false).commit();
                }
            } else if (separated[1].equals("ZGW_HeadLampPower_St")) {
                if (separated[2].equals("Open")) {
                    if (separated[1].equals("ZGW_HighBeamPower_St")) {
                        if (separated[2].equals("Open")) {
                            mExteriorLightAuto.setSelected(false);
                            sp.edit().putInt("mExteriorLightClick", 1).commit();
                            mExteriorHighBeamLight.setSelected(true);
                            mExteriorLowBeamLight.setSelected(false);
                            mExteriorLightOff.setSelected(false);
                            mFrogFrontLight.setEnabled(true);
                            mFrogRearLight.setEnabled(true);
                        } else if (separated[2].equals("Closed")) {
                            sp.edit().putInt("mExteriorLightClick", 2).commit();
                            mExteriorLightAuto.setSelected(false);
                            mExteriorHighBeamLight.setSelected(false);
                            mExteriorLowBeamLight.setSelected(true);
                            mExteriorLightOff.setSelected(false);
                            mFrogFrontLight.setEnabled(true);
                            mFrogRearLight.setEnabled(true);
                            sp.edit().putInt("rearFogLightFlagClick",0).commit();
                            sp.edit().putInt("frontFogLightFlagClick",0).commit();
                        } else {
                            mExteriorLightAuto.setSelected(false);
                            mExteriorHighBeamLight.setSelected(false);
                            mExteriorLowBeamLight.setSelected(false);
                            mExteriorLightOff.setSelected(false);
                            mFrogFrontLight.setEnabled(true);
                            mFrogRearLight.setEnabled(true);//??????????????????Auto?????????
                        }
                    } else if (separated[2].equals("Closed")) {
                        mExteriorLightAuto.setSelected(false);
                        sp.edit().putInt("mExteriorLightClick", 3).commit();
                        mExteriorHighBeamLight.setSelected(false);
                        mExteriorLowBeamLight.setSelected(false);
                        mExteriorLightOff.setSelected(true);
                        mFrogFrontLight.setEnabled(false);
                        mFrogRearLight.setEnabled(false);
                        mFrogFrontLight.setSelected(false);
                        mFrogRearLight.setSelected(false);
                        //rearFogLightFlag = 0;
                        //frontFogLightFlag = 0;
                    } else if (separated[2].equals("Auto")) {
                        mExteriorLightAuto.setSelected(true);
                        sp.edit().putInt("mExteriorLightClick", 0).commit();
                        mExteriorHighBeamLight.setSelected(false);
                        mExteriorLowBeamLight.setSelected(false);
                        mExteriorLightOff.setSelected(false);
                        mFrogFrontLight.setEnabled(true);
                        mFrogRearLight.setEnabled(true);
                    }
                }
            }else if(separated[1].equals("ZGW_DoorLockDrive_St")) {
                if (sp.getBoolean("isDoorLockOpenClick", false) == false) {
                    if (sp.getBoolean("isLeftFrontDoorLockClick", false) == false) {
                        if (separated[2].equals("Lock")) {
                            sp.edit().putBoolean("isLeftFrontDoorLockClick", true).commit();
                            mLeftFrontDoorLock.setSelected(true);
                            isLeftFrontDoorLock = true;
                        }
                        if (isDoorLockOpen()) {
                            mDoorLock.setSelected(true);
                            isDoorLockOpen = true;
                            mRearLuggageTrunk.setActivated(true);
                            isRearLuggageTrunkEnable = false;
                            sp.edit().putBoolean("isDoorLockOpenClick", true).commit();
                            sp.edit().putBoolean("isRearLuggageTrunkEnableClick",false).commit();
                        }
                    } else {
                        if (separated[2].equals("Unlock")) {
                            mLeftFrontDoorLock.setSelected(false);
                            isLeftFrontDoorLock = false;
                            sp.edit().putBoolean("isLeftFrontDoorLockClick", false).commit();
                        }

                    }
                } else {
                    if (separated[2].equals("Unlock")) {
                        sp.edit().putBoolean("isLeftFrontDoorLockClick", false).commit();
                        sp.edit().putBoolean("isDoorLockOpenClick", false).commit();
                        mLeftFrontDoorLock.setSelected(false);
                        isLeftFrontDoorLock = false;
                        mDoorLock.setSelected(false);
                        isDoorLockOpen = false;
                        mRearLuggageTrunk.setActivated(false);
                        isRearLuggageTrunkEnable = true;
                        sp.edit().putBoolean("isRearLuggageTrunkEnableClick",true).commit();
                    }
                }
            }else if (separated[1].equals("ZGW_DoorLockRL_St")) {
                if(sp.getBoolean("isDoorLockOpenClick", false)==false) {
                    if (sp.getBoolean("isLeftRearDoorLockClick", false) == false) {
                        if (separated[2].equals("Lock")) {
                            sp.edit().putBoolean("isLeftRearDoorLockClick",true).commit();
                            mLeftRearDoorLock.setSelected(true);
                            isLeftRearDoorLock = true;
                        }
                        if(isDoorLockOpen()){
                            sp.edit().putBoolean("isDoorLockOpenClick",true).commit();
                            mDoorLock.setSelected(true);
                            isDoorLockOpen = true;
                            mRearLuggageTrunk.setActivated(true);
                            sp.edit().putBoolean("isRearLuggageTrunkEnableClick",false).commit();
                            isRearLuggageTrunkEnable=false;
                        }
                    }else{
                        if (separated[2].equals("Unlock")) {
                            sp.edit().putBoolean("isLeftRearDoorLockClick",false).commit();
                            mLeftRearDoorLock.setSelected(false);
                            isLeftRearDoorLock =false;
                        }

                    }
                }else{
                    if (separated[2].equals("Unlock")) {
                        sp.edit().putBoolean("isLeftRearDoorLockClick",false).commit();
                        sp.edit().putBoolean("isDoorLockOpenClick",false).commit();
                         sp.edit().putBoolean("isRearLuggageTrunkEnableClick",true).commit();
                        mLeftRearDoorLock.setSelected(false);
                        isLeftRearDoorLock =false;
                        mDoorLock.setSelected(false);
                        isDoorLockOpen=false;
                        mRearLuggageTrunk.setActivated(false);
                        isRearLuggageTrunkEnable=true;
                    }
                }
            }else if (separated[1].equals("ZGW_DoorLockPassenger_St")) {
                if(sp.getBoolean("isDoorLockOpenClick", false)==false) {
                    if (sp.getBoolean("isRightFrontDoorLockClick", false) == false) {
                        if (separated[2].equals("Lock")) {
                            sp.edit().putBoolean("isRightFrontDoorLockClick",true).commit();
                            mRightFrontDoorLock.setSelected(true);
                            isRightFrontDoorLock =true;
                        }
                        if(isDoorLockOpen()){
                            sp.edit().putBoolean("isDoorLockOpenClick",true).commit();
                            sp.edit().putBoolean("isRearLuggageTrunkEnableClick",false).commit();
                            mDoorLock.setSelected(true);
                            isDoorLockOpen=true;
                            mRearLuggageTrunk.setActivated(true);
                            isRearLuggageTrunkEnable=false;
                        }
                    }else{
                        if (separated[2].equals("Unlock")) {
                            sp.edit().putBoolean("isRightFrontDoorLockClick",false).commit();
                            mRightFrontDoorLock.setSelected(false);
                            isRightFrontDoorLock =false;
                        }
                    }
                }else{
                    if (separated[2].equals("Unlock")) {
                        sp.edit().putBoolean("isRightFrontDoorLockClick",false).commit();
                        sp.edit().putBoolean("isDoorLockOpenClick",false).commit();
                         sp.edit().putBoolean("isRearLuggageTrunkEnableClick",true).commit();
                        mRightFrontDoorLock.setSelected(false);
                        isRightFrontDoorLock =false;
                        mDoorLock.setSelected(false);
                        isDoorLockOpen=false;
                        mRearLuggageTrunk.setActivated(false);
                        isRearLuggageTrunkEnable=true;
                    }
                }
            }else if (separated[1].equals("ZGW_DoorLockRR_St")) {
                if(sp.getBoolean("isDoorLockOpenClick", false)==false) {
                    if (sp.getBoolean("isRightRearDoorLockClick", false) == false) {
                        if (separated[2].equals("Lock")) {
                            sp.edit().putBoolean("isRightRearDoorLockClick",true).commit();
                            mRightRearDoorLock.setSelected(true);
                            isRightRearDoorLock =true;
                        }
                        if(isDoorLockOpen()){
                            sp.edit().putBoolean("isDoorLockOpenClick",true).commit();
                            sp.edit().putBoolean("isRearLuggageTrunkEnableClick",false).commit();
                            mDoorLock.setSelected(true);
                            isDoorLockOpen=true;
                            mRearLuggageTrunk.setActivated(true);
                            isRearLuggageTrunkEnable=false;
                        }
                    }else{
                        if (separated[2].equals("Unlock")) {
                            sp.edit().putBoolean("isRightRearDoorLockClick",false).commit();
                            mRightRearDoorLock.setSelected(false);
                            isRightRearDoorLock =false;
                        }
                    }
                }else{
                    if (separated[2].equals("Unlock")) {
                        sp.edit().putBoolean("isRightRearDoorLockClick",false).commit();
                        sp.edit().putBoolean("isDoorLockOpenClick",false).commit();
                        // sp.edit().putBoolean("isRearLuggageTrunkEnableClick",true).commit();
                        mRightRearDoorLock.setSelected(false);
                        isRightRearDoorLock =false;
                        mDoorLock.setSelected(false);
                        isDoorLockOpen=false;
                        mRearLuggageTrunk.setActivated(false);
                        isRearLuggageTrunkEnable=true;
                    }
                }
            } else if (separated[1].equals("ZGW_TrunkDoorLock_St")) {
               /* if(isRearLuggageTrunkEnable==false && isRearLuggageTrunkOpen==false){
                    mToastContent.setText("Unlock before you open the door or the trunk");
                    mToastContent.setVisibility(View.VISIBLE);
                    handler.postDelayed(runnable, 3000);
                }else{*/
                    if(sp.getBoolean("isRearLuggageTrunkOpenClick", false) == false ){
                        if (separated[2].equals("Lock")) {
                            sp.edit().putBoolean("isRearLuggageTrunkOpenClick",true).commit();
                            mRearLuggageTrunk.setSelected(true);
                            isRearLuggageTrunkOpen=true;
                        }
                    }else{
                        if (separated[2].equals("Unlock")) {
                            sp.edit().putBoolean("isRearLuggageTrunkOpenClick", false).commit();
                            mRearLuggageTrunk.setSelected(false);
                            isRearLuggageTrunkOpen = false;
                        }
                    }
               // }
            }
        }
    };
    private void loadCurrentSetting() {
        if (sp.getInt("frontFogLightFlagClick",0)==1) {
            mFrogFrontLight.setSelected(true);
        } else if (sp.getInt("frontFogLightFlagClick",0)==0)  {
            mFrogFrontLight.setSelected(false);
        }
        if (sp.getInt("rearFogLightFlagClick",0)==1) {
            mFrogRearLight.setSelected(true);
        } else if (sp.getInt("rearFogLightFlagClick",0)==0)  {
            mFrogRearLight.setSelected(false);
        }
        Log.d(TAG, "loadCurrentSetting"+iMyAidlInterface2);
        if (iMyAidlInterface2 != null) {
         String doorLockDrive = getDoorLockSeparateStatus(1);
            if (doorLockDrive.equals("Locked") || sp.getBoolean("isLeftFrontDoorLockClick",false)) {
                mLeftFrontDoorLock.setSelected(true);
            } else if (doorLockDrive.equals("Unlocked") || sp.getBoolean("isLeftFrontDoorLockClick",false)==false) {
                mLeftFrontDoorLock.setSelected(false);
            }
            String doorLockRL = getDoorLockSeparateStatus(2);
       if (doorLockRL.equals("Locked")|| sp.getBoolean("isLeftRearDoorLockClick",false)) {
                mLeftRearDoorLock.setSelected(true);
            } else if (doorLockRL.equals("Unlocked")|| sp.getBoolean("isLeftRearDoorLockClick",false)==false) {
               mLeftRearDoorLock.setSelected(false);
            }
            String doorLockPassenger = getDoorLockSeparateStatus(3);
            if (doorLockPassenger.equals("Locked") || sp.getBoolean("isRightFrontDoorLockClick",false)) {
                mRightFrontDoorLock.setSelected(true);
            } else if (doorLockPassenger.equals("Unlocked")|| sp.getBoolean("isRightFrontDoorLockClick",false)==false) {
                mRightFrontDoorLock.setSelected(false);
            }
            String doorLockRR = getDoorLockSeparateStatus(4);
            if (doorLockRR.equals("Locked") || sp.getBoolean("isRightRearDoorLockClick",false)) {
                mRightRearDoorLock.setSelected(true);
            } else if (doorLockRR.equals("Unlocked") || sp.getBoolean("isRightRearDoorLockClick",false)==false) {
                mRightRearDoorLock.setSelected(false);
            }
            String rearLuggageTrunk = getDoorLockSeparateStatus(5);
            if (rearLuggageTrunk.equals("Locked")|| sp.getBoolean("isRearLuggageTrunkOpenClick",false)) {
                mRearLuggageTrunk.setSelected(true);
            } else if (rearLuggageTrunk.equals("Unlocked")||sp.getBoolean("isRearLuggageTrunkOpenClick",false)==false) {
                mRearLuggageTrunk.setSelected(false);;
            } else if(sp.getBoolean("isRearLuggageTrunkEnableClick",false)==false){
                mRearLuggageTrunk.setActivated(true);
            }else if(sp.getBoolean("isRearLuggageTrunkEnableClick",false)){
                mRearLuggageTrunk.setActivated(false);
            }

            String rearViewMirror = getRearViewMirrorStatus();
            if (rearViewMirror.equals("Opened") || sp.getBoolean("isMirrorOpenClick",true)) {
                mMirrorFolderDisplayIcon.setSelected(true);
            } else if (rearViewMirror.equals("Closed")|| sp.getBoolean("isMirrorOpenClick",true)==false) {
                mMirrorFolderDisplayIcon.setSelected(false);
            }
            String mLReadingLight = getLReadLightStatus();
            if (mLReadingLight.equals("Opened") ||sp.getBoolean("isReadingLightLOpenClick",false)) {
                mReadingLightl.setSelected(true);
            } else if (mLReadingLight.equals("Closed") ||sp.getBoolean("isReadingLightLOpenClick",false) == false) {
                mReadingLightl.setSelected(false);
            }
            String mReadingLightStatus = getReadLightStatus();
            if (mReadingLightStatus.equals("Opened")||sp.getBoolean("isReadingLightOpenClick",false)) {
                mReadingLight.setSelected(true);
            } else if (mReadingLightStatus.equals("Closed")||sp.getBoolean("isReadingLightOpenClick",false) == false) {
                mReadingLight.setSelected(false);
            }
            String mRReadingLight = getRReadLightStatus();
            Log.d(TAG, "loadCurrentSetting1"+sp.getBoolean("isReadingLightROpenClick",false));
            if (mRReadingLight.equals("Opened")||sp.getBoolean("isReadingLightROpenClick",false)) {
                Log.d(TAG, "loadCurrentSetting2"+sp.getBoolean("isReadingLightROpenClick",false));
                mReadingLightR.setSelected(true);
            } else if (mRReadingLight.equals("Closed")||sp.getBoolean("isReadingLightROpenClick",false)==false) {
                mReadingLightR.setSelected(false);
            }
            String mAmbientLightSwitchStatus = getAmbientLightSwitchStatus();
            String mAmbientLightAndDriveMode = getAmbientLightAndDriveModeStatus();
            if(mAmbientLightSwitchStatus.equals("On") || sp.getBoolean(("mAmbientSwitchOpen"),false)) {
                mAmbientSwitch.setChecked(true);
                mAmbientDisplayLayout.setVisibility(View.VISIBLE);
                if (mAmbientLightAndDriveMode.equals("Original") || sp.getInt("mAmbientMode", 0) == 0) {
                    mAmbientModeOriginal.setSelected(true);
                    mAmbientModeDisplayImage.setBackgroundResource(R.drawable.car_blur);
                }else if (mAmbientLightAndDriveMode.equals("Passion") || sp.getInt("mAmbientMode", 0) == 1) {
                    mAmbientModePassion.setSelected(true);
                    mAmbientModeDisplayImage.setBackgroundResource(R.drawable.car_passion);
                }else if (mAmbientLightAndDriveMode.equals("Flowing") || sp.getInt("mAmbientMode", 0) == 2) {
                    mAmbientModeFlowing.setSelected(true);
                    mAmbientModeDisplayImage.setBackgroundResource(R.drawable.car_flowing);
                }else if (mAmbientLightAndDriveMode.equals("Wave") || sp.getInt("mAmbientMode", 0) == 3) {
                    mAmbientModeWave.setSelected(true);
                    mAmbientModeDisplayImage.setBackgroundResource(R.drawable.car_blur);
                }else if (mAmbientLightAndDriveMode.equals("Stars") || sp.getInt("mAmbientMode", 0) == 4) {
                    mAmbientModeStars.setSelected(true);
                    mAmbientModeDisplayImage.setBackgroundResource(R.drawable.car_stars);
                }else if (mAmbientLightAndDriveMode.equals("Rainbow") || sp.getInt("mAmbientMode", 0) == 5) {
                    mAmbientModeRainBow.setSelected(true);
                    mAmbientModeDisplayImage.setBackgroundResource(R.drawable.car_rainbow);
                }else if (mAmbientLightAndDriveMode.equals("Running") || sp.getInt("mAmbientMode", 0) == 6) {
                    mAmbientModeRunning.setSelected(true);
                    mAmbientModeDisplayImage.setBackgroundResource(R.drawable.car_blur);
                }else if (mAmbientLightAndDriveMode.equals("Rhythm") || sp.getInt("mAmbientMode", 0) == 7) {
                    mAmbientModeRhythm.setSelected(true);
                    mAmbientModeDisplayImage.setBackgroundResource(R.drawable.car_blur);
                }
            }else if(mAmbientLightSwitchStatus.equals("Off") || sp.getBoolean(("mAmbientSwitchOpen"),false)==false){
                mAmbientSwitch.setChecked(false);
                mAmbientDisplayLayout.setVisibility(View.GONE);
            }
            if (mAmbientLightAndDriveMode.equals("D-Normal") || sp.getInt("mDriveMode", 0) == 0) {
                mDriveModeNormal.setSelected(true);
                mDriveModeDisplayImage.setBackgroundResource(R.drawable.img_dynamic_normal);
            }else if (mAmbientLightAndDriveMode.equals("D-Comfort") || sp.getInt("mDriveMode", 0) == 1) {
                mDriveModeComfort.setSelected(true);
                mDriveModeDisplayImage.setBackgroundResource(R.drawable.img_dynamic_comfort);
            }else if (mAmbientLightAndDriveMode.equals("D-Sport") || sp.getInt("mDriveMode", 0) == 2) {
                mDriveModeSport.setSelected(true);
                mDriveModeDisplayImage.setBackgroundResource(R.drawable.img_dynamic_sport);
            }
            processHeadLampStatus();
        }
        if(sp.getBoolean("isDoorLockOpenClick",false)) {
            mDoorLock.setSelected(true);
        } else {
            mDoorLock.setSelected(false);
        }
        if(sp.getBoolean("mWindowLock",false)) {
            mWindowLock.setSelected(true);
            syncWindowAdjustBehavior(true);
        } else {
            mWindowLock.setSelected(false);
            syncWindowAdjustBehavior(false);
        }

        if(sp.getBoolean("mChildLockOpen",false)) {
            mChildLock.setSelected(true);
            mLeftChildLockIcon.setVisibility(View.VISIBLE);
            mRightChildLockIcon.setVisibility(View.VISIBLE);
        } else {
            mChildLock.setSelected(false);
            mLeftChildLockIcon.setVisibility(View.GONE);
            mRightChildLockIcon.setVisibility(View.GONE);
        }
        if(sp.getBoolean("mSunCurtainOpen",false)) mSunCurtain.setSelected(true);
        else mSunCurtain.setSelected(false);
        if(sp.getBoolean("mLuggageFrunkOpen",false)) mLuggageFrunk.setSelected(true);
        else mLuggageFrunk.setSelected(false);
        if(sp.getBoolean("mLuggageDormerOpen",false)) mLuggagedormer.setSelected(true);
        else mLuggagedormer.setSelected(false);

       /* if (rearFogLightFlag == -1) {
            mFrogRearLight.setEnabled(false);
            mFrogRearLight.setSelected(false);

        } else if (rearFogLightFlag == 0) {
            mFrogRearLight.setEnabled(true);
            mFrogRearLight.setSelected(false);
        } else if (rearFogLightFlag == 1) {
            mFrogRearLight.setEnabled(true);
            mFrogRearLight.setSelected(true);
        }*/
        /*if (isReadingLightOpen) mReadingLight.setSelected(true);
        else mReadingLight.setSelected(false);
        if (isReadingLightLOpen) mReadingLightl.setSelected(true);
        else mReadingLightl.setSelected(false);
        if (isReadingLightROpen) mReadingLightR.setSelected(true);
        else mReadingLightR.setSelected(false);*/
        if( sp.getBoolean("isHighBeamAutoOpenClick",false))  mHighBeamLightAuto.setSelected(true);
        else mHighBeamLightAuto.setSelected(false);
        mSeatsLMessageNumber.setText(Integer.toString(mLMessageNum));
        mSeatsRMessageNumber.setText(Integer.toString(mRMessageNum));
      /*  if (isMirrorOpen) mMirrorFolderDisplayIcon.setSelected(true);
        else mMirrorFolderDisplayIcon.setSelected(false);*/

        if (sp.getInt("mMenuListClick", 0) == 0) {
            mMenuListView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mMenuListView.getChildAt(0).setSelected(true);
                }
            }, 500);
        }else if (sp.getInt("mMenuListClick", 0) == 1) {
            mMenuListView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mMenuListView.getChildAt(1).setSelected(true);
                }
            }, 500);
        }else if (sp.getInt("mMenuListClick", 0) == 2) {
            mMenuListView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mMenuListView.getChildAt(2).setSelected(true);
                }
            }, 500);
        }else if (sp.getInt("mMenuListClick", 0) == 3) {
            mMenuListView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mMenuListView.getChildAt(3).setSelected(true);
                }
            }, 500);
        }else if (sp.getInt("mMenuListClick", 0) == 4) {
            mMenuListView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mMenuListView.getChildAt(4).setSelected(true);
                }
            }, 500);
        }else if (sp.getInt("mMenuListClick", 0) == 5) {
            mMenuListView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mMenuListView.getChildAt(5).setSelected(true);
                }
            }, 500);
        }
        if(sp.getBoolean(("mKeyNearDoorAutoUnlockSwitchOpen"),false)) {
            mKeyNearDoorAutoUnlockSwitch.setChecked(true);
            oneButtonDialog.dismiss();
        }
        else mKeyNearDoorAutoUnlockSwitch.setChecked(false);
        if(sp.getBoolean(("mShiftingDoorAutoUnlockSwitchOpen"),false)) mShiftingDoorAutoUnlockSwitch.setChecked(true);
        else mShiftingDoorAutoUnlockSwitch.setChecked(false);
        if(sp.getBoolean(("mDoorAutoLockSwitchOpen"),false)) mDoorAutoLockSwitch.setChecked(true);
        else mDoorAutoLockSwitch.setChecked(false);
        if(sp.getBoolean(("mFrontLightSystemSwitchOpen"),false)){
            mFrontlightSystemSwitch.setChecked(true);
            oneButtonDialog.dismiss();
        }
        else mFrontlightSystemSwitch.setChecked(false);
        if(sp.getBoolean(("mMirrorAutoFoldSwitch1Open"),false)) mMirrorAutoFoldSwitch1.setChecked(true);
        else mMirrorAutoFoldSwitch1.setChecked(false);
        if(sp.getBoolean(("mMirrorAutoFoldSwitch2Open"),false)) mMirrorAutoFoldSwitch2.setChecked(true);
        else mMirrorAutoFoldSwitch2.setChecked(false);
        if(sp.getBoolean(("mMirrorAutoTiltSwitchOpen"),false)) mMirrorAutoTiltSwitch.setChecked(true);
        else mMirrorAutoTiltSwitch.setChecked(false);
    }



    @Override
    public void onClick(View v) {
        int i = v.getId();
        mLMessageNum=(Integer.parseInt(String.valueOf(mSeatsLMessageNumber.getText())));
        mRMessageNum=(Integer.parseInt(String.valueOf(mSeatsRMessageNumber.getText())));
        if (i == mWindowLock.getId()) {
            isWindowLockOpen = sp.getBoolean("mWindowLock",false);
            if (isWindowLockOpen) {
                sp.edit().putBoolean("mWindowLock",false).commit();
                if (iMyAidlInterface2 != null) {
                    try {
                        iMyAidlInterface2.setCanData("ONE,IVI_WindowPowerDrive_Req,Disable");
                       /* iMyAidlInterface2.setCanData("ONE,IVI_WindowPowerRL_Req,Disable");
                        iMyAidlInterface2.setCanData("ONE,IVI_WindowPowerPassenger_Req,Disable");
                        iMyAidlInterface2.setCanData("ONE,IVI_WindowPowerRR_Req,Disable");*/
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                isWindowLockOpen = false;
                mWindowLock.setSelected(false);
            }else{
                sp.edit().putBoolean("mWindowLock",true).commit();
                if (iMyAidlInterface2 != null) {
                    try {
                        iMyAidlInterface2.setCanData("ONE,IVI_WindowPowerDrive_Req,Enable");
                      /*  iMyAidlInterface2.setCanData("ONE,IVI_WindowPowerRL_Req,Enable");
                        iMyAidlInterface2.setCanData("ONE,IVI_WindowPowerPassenger_Req,Enable");
                        iMyAidlInterface2.setCanData("ONE,IVI_WindowPowerRR_Req,Enable");*/
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                isWindowLockOpen = true;
                mWindowLock.setSelected(true);
            }
            syncWindowAdjustBehavior(isWindowLockOpen);
            return;
        }else if (i == mWindowFrontLeftUp.getId()) {
            if (iMyAidlInterface2 != null) {
                try {
                    iMyAidlInterface2.setCanData("ONE,IVI_WindowDrive_Req,Close");
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            return;
        }else if (i == mWindowFrontLeftDown.getId()) {
            if (iMyAidlInterface2 != null) {
                try {
                    iMyAidlInterface2.setCanData("ONE,IVI_WindowDrive_Req,Open");
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            return;
        }else if (i == mWindowRearLeftUp.getId()) {
            if (iMyAidlInterface2 != null) {
                try {
                    iMyAidlInterface2.setCanData("ONE,IVI_WindowRL_Req,Close");
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            return;
        }else if (i == mWindowRearLeftDown.getId()) {
            if (iMyAidlInterface2 != null) {
                try {
                    iMyAidlInterface2.setCanData("ONE,IVI_WindowRL_Req,Open");
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            return;
        }else if (i == mWindowFrontRightUp.getId()) {
            if (iMyAidlInterface2 != null) {
                try {
                    iMyAidlInterface2.setCanData("ONE,IVI_WindowPassenger_Req,Close");
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            return;
        }else if (i == mWindowFrontRightDown.getId()) {
            if (iMyAidlInterface2 != null) {
                try {
                    iMyAidlInterface2.setCanData("ONE,IVI_WindowPassenger_Req,Open");
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            return;
        }else if (i == mWindowRearRightUp.getId()) {
            if (iMyAidlInterface2 != null) {
                try {
                    iMyAidlInterface2.setCanData("ONE,IVI_WindowRR_Req,Close");
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            return;
        }else if (i == mWindowRearRightDown.getId()) {
            if (iMyAidlInterface2 != null) {
                try {
                    iMyAidlInterface2.setCanData("ONE,IVI_WindowRR_Req,Open");
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            return;
        } /*else if (i == mDoorLock.getId()) {
            if(isDoorOpen==false) {
                mDoorLock.setEnabled(true);
                if (doorLockFlag == 0) {
                    doorLockLeft1Flag = 0;
                    doorLockLeft2Flag = 0;
                    doorLockRight1Flag = 0;
                    doorLockRight2Flag = 0;
                    doorLockFlag = 1;
                    syncDoorLockBehavior();
                    if (iMyAidlInterface2 != null) {
                        try {
                            iMyAidlInterface2.setCanData("ONE,IVI_DoorLockDrive_Req,Unlock");
                            iMyAidlInterface2.setCanData("ONE,IVI_DoorLockRL_Req,Unlock");
                            iMyAidlInterface2.setCanData("ONE,IVI_DoorLockPassenger_Req,Unlock");
                            iMyAidlInterface2.setCanData("ONE,IVI_DoorLockRR_Req,Unlock");
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                    syncDoorLockSeparateBehavior(1);
                    syncDoorLockSeparateBehavior(2);
                    syncDoorLockSeparateBehavior(3);
                    syncDoorLockSeparateBehavior(4);

                    //TODO:????????????????????????????????????
                    luggageTrunkFlag = 0;
                    syncLuggageTrunkBehavior();
                } else if (doorLockFlag == 1) {
                    doorLockFlag = 0;
                    syncDoorLockBehavior();
                    doorLockLeft1Flag = 1;
                    doorLockLeft2Flag = 1;
                    doorLockRight1Flag = 1;
                    doorLockRight2Flag = 1;
                    if (iMyAidlInterface2 != null) {
                        try {
                            iMyAidlInterface2.setCanData("ONE,IVI_DoorLockDrive_Req,Lock");
                            iMyAidlInterface2.setCanData("ONE,IVI_DoorLockRL_Req,Lock");
                            iMyAidlInterface2.setCanData("ONE,IVI_DoorLockPassenger_Req,Lock");
                            iMyAidlInterface2.setCanData("ONE,IVI_DoorLockRR_Req,Lock");
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                    syncDoorLockSeparateBehavior(1);
                    syncDoorLockSeparateBehavior(2);
                    syncDoorLockSeparateBehavior(3);
                    syncDoorLockSeparateBehavior(4);

                    //TODO:????????????????????????????????????
                    luggageTrunkFlag = -1;
                    syncLuggageTrunkBehavior();
                }
            } else{
                mDoorLock.setEnabled(false);
                mToastContent.setText("Make sure all doors are closed before locking");
                mToastContent.setVisibility(View.VISIBLE);
                handler.postDelayed(runnable, 3000);
            }
            return;
        }else if (i == mLeftFrontDoorLock.getId()) {
            try {
                if (doorLockLeft1Flag == 1) {
                    doorLockLeft1Flag = 0;
                    if (iMyAidlInterface2 != null)
                        iMyAidlInterface2.setCanData("ONE,IVI_DoorLockDrive_Req,Unlock");
                } else if (doorLockLeft1Flag == 0) {
                    doorLockLeft1Flag = 1;
                    if (iMyAidlInterface2 != null)
                        iMyAidlInterface2.setCanData("ONE,IVI_DoorLockDrive_Req,Lock");
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }

            if (doorLockLeft1Flag == 0) {
                mLeftFrontDoorLock.setSelected(false);
                if (doorLockFlag == 1) {
                    doorLockFlag = 0;
                    syncDoorLockBehavior();
                    //TODO:????????????????????????????????????
                    luggageTrunkFlag = 0;
                    syncLuggageTrunkBehavior();
                }
            } else if (doorLockLeft1Flag == 1) {
                mLeftFrontDoorLock.setSelected(true);
                if (doorLockLeft2Flag == 1 && doorLockRight1Flag == 1 && doorLockRight2Flag == 1 && doorLockFlag == 0) {
                    doorLockFlag = 1;
                    syncDoorLockBehavior();
                    //TODO:????????????????????????????????????
                    luggageTrunkFlag = -1;
                    syncLuggageTrunkBehavior();
                }
            }
            return;

        }else if (i == mLeftRearDoorLock.getId()) {
            try {
                if (doorLockLeft2Flag == 1) {
                    doorLockLeft2Flag = 0;
                    if (iMyAidlInterface2 != null)
                        iMyAidlInterface2.setCanData("ONE,IVI_DoorLockRL_Req,Unlock");
                } else if (doorLockLeft2Flag == 0) {
                    doorLockLeft2Flag = 1;
                    if (iMyAidlInterface2 != null)
                        iMyAidlInterface2.setCanData("ONE,IVI_DoorLockRL_Req,Lock");
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }

            if (doorLockLeft2Flag == 0) {
                mLeftRearDoorLock.setSelected(false);
                if (doorLockFlag == 1) {
                    doorLockFlag = 0;
                    syncDoorLockBehavior();
                    //TODO:????????????????????????????????????
                    luggageTrunkFlag = 0;
                    syncLuggageTrunkBehavior();
                }
            } else if (doorLockLeft2Flag == 1) {
                mLeftRearDoorLock.setSelected(true);
                if (doorLockLeft1Flag == 1 && doorLockRight1Flag == 1 && doorLockRight2Flag == 1 && doorLockFlag == 0) {
                    doorLockFlag = 1;
                    syncDoorLockBehavior();
                    //TODO:????????????????????????????????????
                    luggageTrunkFlag =-1;
                    syncLuggageTrunkBehavior();
                }
            }
            return;
        }else if (i == mRightFrontDoorLock.getId()) {
            try {
                if (doorLockRight1Flag == 1) {
                    doorLockRight1Flag = 0;
                    if (iMyAidlInterface2 != null)
                        iMyAidlInterface2.setCanData("ONE,IVI_DoorLockPassenger_Req,Unlock");
                } else if (doorLockRight1Flag == 0) {
                    doorLockRight1Flag = 1;
                    if (iMyAidlInterface2 != null)
                        iMyAidlInterface2.setCanData("ONE,IVI_DoorLockPassenger_Req,Lock");
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }

            if (doorLockRight1Flag == 0) {
                mRightFrontDoorLock.setSelected(false);
                if (doorLockFlag == 1) {
                    doorLockFlag = 0;
                    syncDoorLockBehavior();
                    //TODO:????????????????????????????????????
                    luggageTrunkFlag = 0;
                    syncLuggageTrunkBehavior();
                }
            } else if (doorLockRight1Flag == 1) {
                mRightFrontDoorLock.setSelected(true);
                if (doorLockLeft1Flag == 1 && doorLockLeft2Flag == 1 && doorLockRight2Flag == 1 && doorLockFlag == 0) {
                    doorLockFlag = 1;
                    syncDoorLockBehavior();
                    //TODO:????????????????????????????????????
                    luggageTrunkFlag = -1;
                    syncLuggageTrunkBehavior();
                }
            }
            return;
        }else if (i == mRightRearDoorLock.getId()) {
            try {
                if (doorLockRight2Flag == 1) {
                    doorLockRight2Flag = 0;
                    if (iMyAidlInterface2 != null)
                        iMyAidlInterface2.setCanData("ONE,IVI_DoorLockRR_Req,Unlock");
                } else if (doorLockRight2Flag == 0) {
                    doorLockRight2Flag = 1;
                    if (iMyAidlInterface2 != null)
                        iMyAidlInterface2.setCanData("ONE,IVI_DoorLockRR_Req,Lock");
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }

            if (doorLockRight2Flag == 0) {
                mRightRearDoorLock.setSelected(false);
                if (doorLockFlag == 1) {
                    doorLockFlag = 0;
                    syncDoorLockBehavior();
                    //TODO:????????????????????????????????????
                    luggageTrunkFlag = 0;
                    syncLuggageTrunkBehavior();
                }
            } else if (doorLockRight2Flag == 1) {
                mRightRearDoorLock.setSelected(true);
                if (doorLockLeft1Flag == 1 && doorLockLeft2Flag == 1 && doorLockRight1Flag == 1 && doorLockFlag == 0) {
                    doorLockFlag = 1;
                    syncDoorLockBehavior();
                    //TODO:????????????????????????????????????
                    luggageTrunkFlag = -1;
                    syncLuggageTrunkBehavior();
                }
            }
            return;
        }*/
        else if (i == mDoorLock.getId()) {
            if(isDoorOpen==false) {
                mDoorLock.setEnabled(true);
                if (sp.getBoolean("isDoorLockOpenClick",false)== false) {
                    if (iMyAidlInterface2 != null) {
                        try {
                            iMyAidlInterface2.setCanData("ONE,IVI_DoorLockDrive_Req,Lock");
                            iMyAidlInterface2.setCanData("ONE,IVI_DoorLockRL_Req,Lock");
                            iMyAidlInterface2.setCanData("ONE,IVI_DoorLockPassenger_Req,Lock");
                            iMyAidlInterface2.setCanData("ONE,IVI_DoorLockRR_Req,Lock");
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                    mLeftFrontDoorLock.setSelected(true);
                    mLeftRearDoorLock.setSelected(true);
                    mRightFrontDoorLock.setSelected(true);
                    mRightRearDoorLock.setSelected(true);
                    mDoorLock.setSelected(true);
                    isLeftRearDoorLock = true;
                    isLeftFrontDoorLock = true;
                    isRightFrontDoorLock = true;
                    isRightRearDoorLock = true;
                    sp.edit().putBoolean("isLeftRearDoorLockClick",true).commit();
                    sp.edit().putBoolean("isLeftFrontDoorLockClick",true).commit();
                    sp.edit().putBoolean("isRightFrontDoorLockClick",true).commit();
                    sp.edit().putBoolean("isRightRearDoorLockClick",true).commit();
                    sp.edit().putBoolean("isDoorLockOpenClick",true).commit();
                    sp.edit().putBoolean("isRearLuggageTrunkEnableClick",false).commit();
                    mRearLuggageTrunk.setActivated(true);
                    isRearLuggageTrunkEnable = false;
                    isDoorLockOpen = true;
                } else {
                    if (iMyAidlInterface2 != null) {
                        try {
                            iMyAidlInterface2.setCanData("ONE,IVI_DoorLockDrive_Req,Unlock");
                            iMyAidlInterface2.setCanData("ONE,IVI_DoorLockRL_Req,Unlock");
                            iMyAidlInterface2.setCanData("ONE,IVI_DoorLockPassenger_Req,Unlock");
                            iMyAidlInterface2.setCanData("ONE,IVI_DoorLockRR_Req,Unlock");
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                    sp.edit().putBoolean("isLeftRearDoorLockClick",false).commit();
                    sp.edit().putBoolean("isLeftFrontDoorLockClick",false).commit();
                    sp.edit().putBoolean("isRightFrontDoorLockClick",false).commit();
                    sp.edit().putBoolean("isRightRearDoorLockClick",false).commit();
                    sp.edit().putBoolean("isDoorLockOpenClick",false).commit();
                    sp.edit().putBoolean("isRearLuggageTrunkEnableClick",true).commit();
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
            if(sp.getBoolean("isDoorLockOpenClick",false)==false) {
                if (sp.getBoolean("isLeftFrontDoorLockClick",false) == false) {
                    if (iMyAidlInterface2 != null) {
                        try {
                            iMyAidlInterface2.setCanData("ONE,IVI_DoorLockDrive_Req,Lock");
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                    sp.edit().putBoolean("isLeftFrontDoorLockClick",true).commit();
                    mLeftFrontDoorLock.setSelected(true);
                    isLeftFrontDoorLock =true;
                    if(isDoorLockOpen()){
                        mDoorLock.setSelected(true);
                        isDoorLockOpen=true;
                        mRearLuggageTrunk.setActivated(true);
                        isRearLuggageTrunkEnable=false;
                        sp.edit().putBoolean("isDoorLockOpenClick",true).commit();
                        sp.edit().putBoolean("isRearLuggageTrunkEnableClick",false).commit();
                    }
                }else{
                    if (iMyAidlInterface2 != null) {
                        try {
                            iMyAidlInterface2.setCanData("ONE,IVI_DoorLockDrive_Req,Unlock");
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                    mLeftFrontDoorLock.setSelected(false);
                    isLeftFrontDoorLock =false;
                    sp.edit().putBoolean("isLeftFrontDoorLockClick",false).commit();
                }
            }else{
                if (iMyAidlInterface2 != null) {
                    try {
                        iMyAidlInterface2.setCanData("ONE,IVI_DoorLockDrive_Req,Unlock");
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                sp.edit().putBoolean("isLeftFrontDoorLockClick",false).commit();
                sp.edit().putBoolean("isDoorLockOpenClick",false).commit();
                mLeftFrontDoorLock.setSelected(false);
                isLeftFrontDoorLock =false;
                mDoorLock.setSelected(false);
                isDoorLockOpen=false;
                mRearLuggageTrunk.setActivated(false);
                isRearLuggageTrunkEnable=true;
                sp.edit().putBoolean("isRearLuggageTrunkEnableClick",true).commit();
            }
            return;
        }else if (i == mLeftRearDoorLock.getId()) {
            if(sp.getBoolean("isDoorLockOpenClick",false)==false) {
                if (sp.getBoolean("isLeftRearDoorLockClick",false)  == false) {
                    if (iMyAidlInterface2 != null) {
                        try {
                            iMyAidlInterface2.setCanData("ONE,IVI_DoorLockRL_Req,Lock");
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                    sp.edit().putBoolean("isLeftRearDoorLockClick",true).commit();
                    mLeftRearDoorLock.setSelected(true);
                    isLeftRearDoorLock = true;
                    if(isDoorLockOpen()){
                        sp.edit().putBoolean("isDoorLockOpenClick",true).commit();
                        mDoorLock.setSelected(true);
                        isDoorLockOpen = true;
                        mRearLuggageTrunk.setActivated(true);
                        sp.edit().putBoolean("isRearLuggageTrunkEnableClick",false).commit();
                        isRearLuggageTrunkEnable=false;
                    }
                }else{
                    if (iMyAidlInterface2 != null) {
                        try {
                            iMyAidlInterface2.setCanData("ONE,IVI_DoorLockRL_Req,Unlock");
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                    sp.edit().putBoolean("isLeftRearDoorLockClick",false).commit();
                    mLeftRearDoorLock.setSelected(false);
                    isLeftRearDoorLock =false;
                }
            }else{
                if (iMyAidlInterface2 != null) {
                    try {
                        iMyAidlInterface2.setCanData("ONE,IVI_DoorLockRL_Req,Unlock");
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                sp.edit().putBoolean("isLeftRearDoorLockClick",false).commit();
                sp.edit().putBoolean("isDoorLockOpenClick",false).commit();
                sp.edit().putBoolean("isRearLuggageTrunkEnableClick",true).commit();
                mLeftRearDoorLock.setSelected(false);
                isLeftRearDoorLock =false;
                mDoorLock.setSelected(false);
                isDoorLockOpen=false;
                mRearLuggageTrunk.setActivated(false);
                isRearLuggageTrunkEnable=true;
            }
            return;
        }else if (i == mRightFrontDoorLock.getId()) {
            if(sp.getBoolean("isDoorLockOpenClick",false)==false) {
                if (sp.getBoolean("isRightFrontDoorLockClick",false) == false) {
                    if (iMyAidlInterface2 != null) {
                        try {
                            iMyAidlInterface2.setCanData("ONE,IVI_DoorLockPassenger_Req,Lock");
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                    sp.edit().putBoolean("isRightFrontDoorLockClick",true).commit();
                    mRightFrontDoorLock.setSelected(true);
                    isRightFrontDoorLock =true;
                    if(isDoorLockOpen()){
                        sp.edit().putBoolean("isDoorLockOpenClick",true).commit();
                        sp.edit().putBoolean("isRearLuggageTrunkEnableClick",false).commit();
                        mDoorLock.setSelected(true);
                        isDoorLockOpen=true;
                        mRearLuggageTrunk.setActivated(true);
                        isRearLuggageTrunkEnable=false;
                    }
                }else{
                    if (iMyAidlInterface2 != null) {
                        try {
                            iMyAidlInterface2.setCanData("ONE,IVI_DoorLockPassenger_Req,Unlock");
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                    sp.edit().putBoolean("isRightFrontDoorLockClick",false).commit();
                    mRightFrontDoorLock.setSelected(false);
                    isRightFrontDoorLock =false;
                }
            }else{
                if (iMyAidlInterface2 != null) {
                    try {
                        iMyAidlInterface2.setCanData("ONE,IVI_DoorLockPassenger_Req,Unlock");
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                sp.edit().putBoolean("isRightFrontDoorLockClick",false).commit();
                sp.edit().putBoolean("isDoorLockOpenClick",false).commit();
                sp.edit().putBoolean("isRearLuggageTrunkEnableClick",true).commit();
                mRightFrontDoorLock.setSelected(false);
                isRightFrontDoorLock =false;
                mDoorLock.setSelected(false);
                isDoorLockOpen=false;
                mRearLuggageTrunk.setActivated(false);
                isRearLuggageTrunkEnable=true;
            }
            return;
        }else if (i == mRightRearDoorLock.getId()) {
            if(sp.getBoolean("isDoorLockOpenClick",false)==false) {
                if (sp.getBoolean("isRightRearDoorLockClick",false) == false) {
                    if (iMyAidlInterface2 != null) {
                        try {
                            iMyAidlInterface2.setCanData("ONE,IVI_DoorLockRR_Req,Lock");
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                    sp.edit().putBoolean("isRightRearDoorLockClick",true).commit();
                    mRightRearDoorLock.setSelected(true);
                    isRightRearDoorLock =true;
                    if(isDoorLockOpen()){
                        sp.edit().putBoolean("isDoorLockOpenClick",true).commit();
                        sp.edit().putBoolean("isRearLuggageTrunkEnableClick",false).commit();
                        mDoorLock.setSelected(true);
                        isDoorLockOpen=true;
                        mRearLuggageTrunk.setActivated(true);
                        isRearLuggageTrunkEnable=false;
                    }
                }else{
                    if (iMyAidlInterface2 != null) {
                        try {
                            iMyAidlInterface2.setCanData("ONE,IVI_DoorLockRR_Req,Unlock");
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                    sp.edit().putBoolean("isRightRearDoorLockClick",false).commit();
                    mRightRearDoorLock.setSelected(false);
                    isRightRearDoorLock =false;
                }
            }else{
                if (iMyAidlInterface2 != null) {
                    try {
                        iMyAidlInterface2.setCanData("ONE,IVI_DoorLockRR_Req,Unlock");
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                sp.edit().putBoolean("isRightRearDoorLockClick",false).commit();
                sp.edit().putBoolean("isDoorLockOpenClick",false).commit();
                sp.edit().putBoolean("isRearLuggageTrunkEnableClick",true).commit();
                mRightRearDoorLock.setSelected(false);
                isRightRearDoorLock =false;
                mDoorLock.setSelected(false);
                isDoorLockOpen=false;
                mRearLuggageTrunk.setActivated(false);
                isRearLuggageTrunkEnable=true;
            }
            return;
        } else if (i == mRearLuggageTrunk.getId()) {
            if(sp.getBoolean("isRearLuggageTrunkEnableClick",false)==false && sp.getBoolean("isRearLuggageTrunkOpenClick",false)==false){
                mToastContent.setText("Unlock before you open the door or the trunk");
                mToastContent.setVisibility(View.VISIBLE);
                handler.postDelayed(runnable, 3000);
            }else{
                if(sp.getBoolean("isRearLuggageTrunkOpenClick",false) == false ){
                    if (iMyAidlInterface2 != null) {
                        try {
                            iMyAidlInterface2.setCanData("ONE,IVI_TrunkDoorLock_Req");
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                    sp.edit().putBoolean("isRearLuggageTrunkOpenClick",true).commit();
                    mRearLuggageTrunk.setSelected(true);
                    isRearLuggageTrunkOpen=true;
                }else{
                    sp.edit().putBoolean("isRearLuggageTrunkOpenClick",false).commit();
                    mRearLuggageTrunk.setSelected(false);
                    isRearLuggageTrunkOpen=false;
                }
            }
            return;
        } else if (i == mLuggageFrunk.getId()) {
            if (sp.getBoolean("mLuggageFrunkOpen",false) == false) {
                sp.edit().putBoolean("mLuggageFrunkOpen",true).commit();
                mLuggageFrunk.setSelected(true);
                //isLuggageFrunkClick = true;
            }else{
                mLuggageFrunk.setSelected(false);
                sp.edit().putBoolean("mLuggageFrunkOpen",false).commit();
                //isLuggageFrunkClick = false;
            }
            return;
        }else if (i == mLuggagedormer.getId()) {
            if (sp.getBoolean("mLuggageDormerOpen",false) == false) {
                mLuggagedormer.setSelected(true);
                sp.edit().putBoolean("mLuggageDormerOpen",true).commit();
                //isLuggageDormerClick = true;
            }else{
                mLuggagedormer.setSelected(false);
                sp.edit().putBoolean("mLuggageDormerOpen",false).commit();
                //isLuggageDormerClick = false;
            }
            return;
        }/* else if (i == mRearLuggageTrunk.getId()) {
       *//*     if(isRearLuggageTrunkEnable==false && isRearLuggageTrunkOpen==false){
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
            }*//*
            if (luggageTrunkFlag == 1) {
                luggageTrunkFlag = 0;
                syncLuggageTrunkBehavior();
            } else if (luggageTrunkFlag == 0) {
                luggageTrunkFlag = 1;
                syncLuggageTrunkBehavior();
            } else {
                mToastContent.setText("Unlock before you open the door or the trunk");
                mToastContent.setVisibility(View.VISIBLE);
                handler.postDelayed(runnable, 3000);
            }
            return;
        }*/ else if (i == mChildLock.getId()) {
            if (sp.getBoolean("mChildLockOpen",false) == false) {
                sp.edit().putBoolean("mChildLockOpen",true).commit();
                mChildLock.setSelected(true);
                //isChildLockOpen = true;
                mLeftChildLockIcon.setVisibility(View.VISIBLE);
                mRightChildLockIcon.setVisibility(View.VISIBLE);
            }else{
                mChildLock.setSelected(false);
                sp.edit().putBoolean("mChildLockOpen",false).commit();
                //isChildLockOpen = false;
                mLeftChildLockIcon.setVisibility(View.GONE);
                mRightChildLockIcon.setVisibility(View.GONE);
            }
            return;
        } else if (i == mSunCurtain.getId()) {
            if (sp.getBoolean("mSunCurtainOpen",false) == false) {
                mSunCurtain.setSelected(true);
                sp.edit().putBoolean("mSunCurtainOpen",true).commit();
                //isSunCurtainOpen = true;
            }else{
                sp.edit().putBoolean("mSunCurtainOpen",false).commit();
                mSunCurtain.setSelected(false);
                //isSunCurtainOpen = false;
            }
            return;
        } else if (i == mDriveModeComfort.getId()) {
            sp.edit().putInt("mDriveMode",1).commit();
            if (iMyAidlInterface2 != null) {
                try {
                    iMyAidlInterface2.setCanData("ONE,IVI_MoodLightModeSet_Req,D-Comfort");
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            mDriveModeComfort.setSelected(true);
            mDriveModeNormal.setSelected(false);
            mDriveModeSport.setSelected(false);
            mDriveModeDisplayImage.setBackgroundResource(R.drawable.img_dynamic_comfort);
            startDismissControlViewTimer();
            return;
        }else if (i == mDriveModeNormal.getId()) {
            if (iMyAidlInterface2 != null) {
                try {
                    iMyAidlInterface2.setCanData("ONE,IVI_MoodLightModeSet_Req,D-Normal");
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            sp.edit().putInt("mDriveMode",0).commit();
            mDriveModeComfort.setSelected(false);
            mDriveModeNormal.setSelected(true);
            mDriveModeSport.setSelected(false);
            startDismissControlViewTimer();
            mDriveModeDisplayImage.setBackgroundResource(R.drawable.img_dynamic_normal);
            return;
        }else if (i == mDriveModeSport.getId()) {
            if (iMyAidlInterface2 != null) {
                try {
                    iMyAidlInterface2.setCanData("ONE,IVI_MoodLightModeSet_Req,D-Sport");
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            sp.edit().putInt("mDriveMode",2).commit();
            startDismissControlViewTimer();
            mDriveModeComfort.setSelected(false);
            mDriveModeNormal.setSelected(false);
            mDriveModeSport.setSelected(true);
            mDriveModeDisplayImage.setBackgroundResource(R.drawable.img_dynamic_sport);
            return;
        } else if (i == mAmbientModeOriginal.getId()) {
            if (iMyAidlInterface2 != null) {
                try {
                    iMyAidlInterface2.setCanData("ONE,IVI_MoodLightModeSet_Req,Original");
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            sp.edit().putInt("mAmbientMode",0).commit();
            mAmbientModeOriginal.setSelected(true);
            mAmbientModePassion.setSelected(false);
            mAmbientModeFlowing.setSelected(false);
            mAmbientModeWave.setSelected(false);
            mAmbientModeStars.setSelected(false);
            mAmbientModeRainBow.setSelected(false);
            mAmbientModeRunning.setSelected(false);
            mAmbientModeRhythm.setSelected(false);
            mAmbientModeDisplayImage.setBackgroundResource(R.drawable.car_blur);
            isAmbientModeOriginalClick=true;
            isAmbientModePassionClick=false;
            isAmbientModeFlowingClick=false;
            isAmbientModeWaveClick=false;
            isAmbientModeStarsClick=false;
            isAmbientModeRainbowClick=false;
            isAmbientModeRunningClick=false;
            isAmbientModeRhythmClick=false;
            return;
        } else if (i == mAmbientModePassion.getId()) {
            if (iMyAidlInterface2 != null) {
                try {
                    iMyAidlInterface2.setCanData("ONE,IVI_MoodLightModeSet_Req,Passion");
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            sp.edit().putInt("mAmbientMode",1).commit();
            mAmbientModeOriginal.setSelected(false);
            mAmbientModePassion.setSelected(true);
            mAmbientModeFlowing.setSelected(false);
            mAmbientModeWave.setSelected(false);
            mAmbientModeStars.setSelected(false);
            mAmbientModeRainBow.setSelected(false);
            mAmbientModeRunning.setSelected(false);
            mAmbientModeRhythm.setSelected(false);
            mAmbientModeDisplayImage.setBackgroundResource(R.drawable.car_passion);
            isAmbientModeOriginalClick=false;
            isAmbientModePassionClick=true;
            isAmbientModeFlowingClick=false;
            isAmbientModeWaveClick=false;
            isAmbientModeStarsClick=false;
            isAmbientModeRainbowClick=false;
            isAmbientModeRunningClick=false;
            isAmbientModeRhythmClick=false;
            return;
        } else if (i == mAmbientModeFlowing.getId()) {
            if (iMyAidlInterface2 != null) {
                try {
                    iMyAidlInterface2.setCanData("ONE,IVI_MoodLightModeSet_Req,Flowing");
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            sp.edit().putInt("mAmbientMode",2).commit();
            mAmbientModeOriginal.setSelected(false);
            mAmbientModePassion.setSelected(false);
            mAmbientModeFlowing.setSelected(true);
            mAmbientModeWave.setSelected(false);
            mAmbientModeStars.setSelected(false);
            mAmbientModeRainBow.setSelected(false);
            mAmbientModeRunning.setSelected(false);
            mAmbientModeRhythm.setSelected(false);
            mAmbientModeDisplayImage.setBackgroundResource(R.drawable.car_flowing);
            isAmbientModeOriginalClick=false;
            isAmbientModePassionClick=false;
            isAmbientModeFlowingClick=true;
            isAmbientModeWaveClick=false;
            isAmbientModeStarsClick=false;
            isAmbientModeRainbowClick=false;
            isAmbientModeRunningClick=false;
            isAmbientModeRhythmClick=false;
            return;
        } else if (i == mAmbientModeWave.getId()) {
            if (iMyAidlInterface2 != null) {
                try {
                    iMyAidlInterface2.setCanData("ONE,IVI_MoodLightModeSet_Req,Wave");
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            sp.edit().putInt("mAmbientMode",3).commit();
            mAmbientModeOriginal.setSelected(false);
            mAmbientModePassion.setSelected(false);
            mAmbientModeFlowing.setSelected(false);
            mAmbientModeWave.setSelected(true);
            mAmbientModeStars.setSelected(false);
            mAmbientModeRainBow.setSelected(false);
            mAmbientModeRunning.setSelected(false);
            mAmbientModeRhythm.setSelected(false);
            mAmbientModeDisplayImage.setBackgroundResource(R.drawable.car_blur);
            isAmbientModeOriginalClick=false;
            isAmbientModePassionClick=false;
            isAmbientModeFlowingClick=false;
            isAmbientModeWaveClick=true;
            isAmbientModeStarsClick=false;
            isAmbientModeRainbowClick=false;
            isAmbientModeRunningClick=false;
            isAmbientModeRhythmClick=false;
            return;
        } else if (i == mAmbientModeStars.getId()) {
            if (iMyAidlInterface2 != null) {
                try {
                    iMyAidlInterface2.setCanData("ONE,IVI_MoodLightModeSet_Req,Stars");
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            sp.edit().putInt("mAmbientMode",4).commit();
            mAmbientModeOriginal.setSelected(false);
            mAmbientModePassion.setSelected(false);
            mAmbientModeFlowing.setSelected(false);
            mAmbientModeWave.setSelected(false);
            mAmbientModeStars.setSelected(true);
            mAmbientModeRainBow.setSelected(false);
            mAmbientModeRunning.setSelected(false);
            mAmbientModeRhythm.setSelected(false);
            mAmbientModeDisplayImage.setBackgroundResource(R.drawable.car_stars);
            isAmbientModeOriginalClick=false;
            isAmbientModePassionClick=false;
            isAmbientModeFlowingClick=false;
            isAmbientModeWaveClick=false;
            isAmbientModeStarsClick=true;
            isAmbientModeRainbowClick=false;
            isAmbientModeRunningClick=false;
            isAmbientModeRhythmClick=false;
            return;
        } else if (i == mAmbientModeRainBow.getId()) {
            if (iMyAidlInterface2 != null) {
                try {
                    iMyAidlInterface2.setCanData("ONE,IVI_MoodLightModeSet_Req,Rainbow");
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            sp.edit().putInt("mAmbientMode",5).commit();
            mAmbientModeOriginal.setSelected(false);
            mAmbientModePassion.setSelected(false);
            mAmbientModeFlowing.setSelected(false);
            mAmbientModeWave.setSelected(false);
            mAmbientModeStars.setSelected(false);
            mAmbientModeRainBow.setSelected(true);
            mAmbientModeRunning.setSelected(false);
            mAmbientModeRhythm.setSelected(false);
            mAmbientModeDisplayImage.setBackgroundResource(R.drawable.car_rainbow);
            isAmbientModeOriginalClick=false;
            isAmbientModePassionClick=false;
            isAmbientModeFlowingClick=false;
            isAmbientModeWaveClick=false;
            isAmbientModeStarsClick=false;
            isAmbientModeRainbowClick=true;
            isAmbientModeRunningClick=false;
            isAmbientModeRhythmClick=false;
            return;
        } else if (i == mAmbientModeRunning.getId()) {
            if (iMyAidlInterface2 != null) {
                try {
                    iMyAidlInterface2.setCanData("ONE,IVI_MoodLightModeSet_Req,Running");
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            sp.edit().putInt("mAmbientMode",6).commit();
            mAmbientModeOriginal.setSelected(false);
            mAmbientModePassion.setSelected(false);
            mAmbientModeFlowing.setSelected(false);
            mAmbientModeWave.setSelected(false);
            mAmbientModeStars.setSelected(false);
            mAmbientModeRainBow.setSelected(false);
            mAmbientModeRunning.setSelected(true);
            mAmbientModeRhythm.setSelected(false);
            mAmbientModeDisplayImage.setBackgroundResource(R.drawable.car_blur);
            isAmbientModeOriginalClick=false;
            isAmbientModePassionClick=false;
            isAmbientModeFlowingClick=false;
            isAmbientModeWaveClick=false;
            isAmbientModeStarsClick=false;
            isAmbientModeRainbowClick=false;
            isAmbientModeRunningClick=true;
            isAmbientModeRhythmClick=false;
            return;
        } else if (i == mAmbientModeRhythm.getId()) {
            if (iMyAidlInterface2 != null) {
                try {
                    iMyAidlInterface2.setCanData("ONE,IVI_MoodLightModeSet_Req,Rhythm");
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            sp.edit().putInt("mAmbientMode",7).commit();
            mAmbientModeOriginal.setSelected(false);
            mAmbientModePassion.setSelected(false);
            mAmbientModeFlowing.setSelected(false);
            mAmbientModeWave.setSelected(false);
            mAmbientModeStars.setSelected(false);
            mAmbientModeRainBow.setSelected(false);
            mAmbientModeRunning.setSelected(false);
            mAmbientModeRhythm.setSelected(true);
            mAmbientModeDisplayImage.setBackgroundResource(R.drawable.car_blur);
            isAmbientModeOriginalClick=false;
            isAmbientModePassionClick=false;
            isAmbientModeFlowingClick=false;
            isAmbientModeWaveClick=false;
            isAmbientModeStarsClick=false;
            isAmbientModeRainbowClick=false;
            isAmbientModeRunningClick=false;
            isAmbientModeRhythmClick=true;
            return;
        } else if (i == mExteriorLightAuto.getId()) {
            //if(isExteriorLightAutoOpen) {
            mExteriorLightAuto.setSelected(true);
            sp.edit().putInt("mExteriorLightClick",0).commit();
            mExteriorHighBeamLight.setSelected(false);
            mExteriorLowBeamLight.setSelected(false);
            mExteriorLightOff.setSelected(false);
            mFrogFrontLight.setEnabled(true);
            mFrogRearLight.setEnabled(true);
           /* sp.edit().putInt("rearFogLightFlagClick",0).commit();
            sp.edit().putInt("frontFogLightFlagClick",0).commit();
            rearFogLightFlag=0;
            frontFogLightFlag=0;*/
            //isExteriorLightAutoOpen=false;
            //}
            return;
        } else if (i == mExteriorHighBeamLight.getId()) {
            if (iMyAidlInterface2 != null) {
                try {
                    iMyAidlInterface2.setCanData("TWO,IVI_HeadLampPower_Req,Open");
                    iMyAidlInterface2.setCanData("TWO,IVI_HighBeamPower_Req,Open");
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            mExteriorLightAuto.setSelected(false);
            sp.edit().putInt("mExteriorLightClick",1).commit();
            mExteriorHighBeamLight.setSelected(true);
            mExteriorLowBeamLight.setSelected(false);
            mExteriorLightOff.setSelected(false);
            mFrogFrontLight.setEnabled(true);
            mFrogRearLight.setEnabled(true);
         /*   sp.edit().putInt("rearFogLightFlagClick",0).commit();
            sp.edit().putInt("frontFogLightFlagClick",0).commit();
            rearFogLightFlag=0;
            frontFogLightFlag=0;*/
            //isExteriorLightAutoOpen=true;
            return;
        } else if (i == mExteriorLowBeamLight.getId()) {
            if (iMyAidlInterface2 != null) {
                try {
                    iMyAidlInterface2.setCanData("TWO,IVI_HeadLampPower_Req,Open");
                    iMyAidlInterface2.setCanData("TWO,IVI_HighBeamPower_Req,Close");
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            sp.edit().putInt("mExteriorLightClick",2).commit();
            mExteriorLightAuto.setSelected(false);
            mExteriorHighBeamLight.setSelected(false);
            mExteriorLowBeamLight.setSelected(true);
            mExteriorLightOff.setSelected(false);
            mFrogFrontLight.setEnabled(true);
            mFrogRearLight.setEnabled(true);
         /*   sp.edit().putInt("rearFogLightFlagClick",0).commit();
            sp.edit().putInt("frontFogLightFlagClick",0).commit();
            rearFogLightFlag=0;
            frontFogLightFlag=0;*/
            //isExteriorLightAutoOpen=true;
            return;
        } else if (i ==  mExteriorLightOff.getId()) {
            if (iMyAidlInterface2 != null) {
                try {
                    iMyAidlInterface2.setCanData("TWO,IVI_HeadLampPower_Req,Close");
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            mExteriorLightAuto.setSelected(false);
            sp.edit().putInt("mExteriorLightClick",3).commit();
            mExteriorHighBeamLight.setSelected(false);
            mExteriorLowBeamLight.setSelected(false);
            mExteriorLightOff.setSelected(true);
            mFrogFrontLight.setEnabled(false);
            mFrogRearLight.setEnabled(false);
            mFrogFrontLight.setSelected(false);
            mFrogRearLight.setSelected(false);
            sp.edit().putInt("rearFogLightFlagClick",0).commit();
            sp.edit().putInt("frontFogLightFlagClick",0).commit();
            //rearFogLightFlag=0;
            //frontFogLightFlag=0;
            //isExteriorLightAutoOpen=true;
            return;
        }else if (i ==  mHighBeamLightAuto.getId()) {
            if (sp.getBoolean("isHighBeamAutoOpenClick",false) == false) {
                sp.edit().putBoolean("isHighBeamAutoOpenClick",true).commit();
                mHighBeamLightAuto.setSelected(true);
                //isHighBeamAutoOpen = true;
            }else{
                sp.edit().putBoolean("isHighBeamAutoOpenClick",false).commit();
                mHighBeamLightAuto.setSelected(false);
                //isHighBeamAutoOpen = false;
            }
            return;
        }else if (i ==  mFrogFrontLight.getId()) {
            if (sp.getInt("frontFogLightFlagClick",0) == 1) {
                Log.d(TAG, "loadCurrentSetting9"+frontFogLightFlag );
                sp.edit().putInt("frontFogLightFlagClick",0).commit();
                //frontFogLightFlag = 0;
                mFrogFrontLight.setSelected(false);
            } else if (sp.getInt("frontFogLightFlagClick",0) == 0) {
                Log.d(TAG, "loadCurrentSetting10"+frontFogLightFlag );
                sp.edit().putInt("frontFogLightFlagClick",1).commit();
                //frontFogLightFlag = 1;
                mFrogFrontLight.setSelected(true);
            }
            return;
        }else if (i ==  mFrogRearLight.getId()) {
            if (sp.getInt("rearFogLightFlagClick",0) == 1) {
                sp.edit().putInt("rearFogLightFlagClick",0).commit();
                //rearFogLightFlag = 0;
                mFrogRearLight.setSelected(false);
            } else if (sp.getInt("rearFogLightFlagClick",0) == 0) {
                sp.edit().putInt("rearFogLightFlagClick",1).commit();
                //rearFogLightFlag = 1;
                mFrogRearLight.setSelected(true);
            }
            return;
        }else if (i ==  mReadingLightl.getId()) {
            if (sp.getBoolean("isReadingLightLOpenClick",false)== false) {
                if (iMyAidlInterface2 != null) {
                    try {
                        iMyAidlInterface2.setCanData("TWO,IVI_LeftMapLamp_Req,Open");

                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                sp.edit().putBoolean("isReadingLightLOpenClick", true).commit();
                mReadingLightl.setSelected(true);
                //isReadingLightLOpen = true;
            }else{
                if (iMyAidlInterface2 != null) {
                    try {
                        iMyAidlInterface2.setCanData("TWO,IVI_LeftMapLamp_Req,Close");
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                sp.edit().putBoolean("isReadingLightLOpenClick", false).commit();
                mReadingLightl.setSelected(false);
                //isReadingLightLOpen = false;
            }
            return;
        }else if (i ==  mReadingLight.getId()) {
            if (sp.getBoolean("isReadingLightOpenClick",false) == false) {
                if (iMyAidlInterface2 != null) {
                    try {
                        iMyAidlInterface2.setCanData("TWO,IVI_RoomLamp_Req,Open");
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                sp.edit().putBoolean("isReadingLightOpenClick", true).commit();
                mReadingLight.setSelected(true);
               // isReadingLightOpen = true;
            }else{
                if (iMyAidlInterface2 != null) {
                    try {
                        iMyAidlInterface2.setCanData("TWO,IVI_RoomLamp_Req,Close");
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                sp.edit().putBoolean("isReadingLightOpenClick", false).commit();
                mReadingLight.setSelected(false);
                //isReadingLightOpen = false;
            }
            return;
        }else if (i ==  mReadingLightR.getId()) {
            if (sp.getBoolean("sReadingLightROpenClick",false)== false) {
                if (iMyAidlInterface2 != null) {
                    try {
                        iMyAidlInterface2.setCanData("TWO,IVI_RightMapLamp_Req,Close");
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                sp.edit().putBoolean("isReadingLightROpenClick",true).commit();
                mReadingLightR.setSelected(true);
                //isReadingLightROpen = true;
            }else{
                if (iMyAidlInterface2 != null) {
                    try {
                        iMyAidlInterface2.setCanData("TWO,IVI_RightMapLamp_Req,Open");
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                sp.edit().putBoolean("isReadingLightROpenClick",false).commit();
                mReadingLightR.setSelected(false);
                //isReadingLightROpen = false;
            }
            return;
        }else if (i ==  mPuddleLight.getId()) {
            if (sp.getBoolean("isPubbleLightLOpenClick",false) == false) {
                sp.edit().putBoolean("isPubbleLightLOpenClick",true).commit();
                mPuddleLight.setSelected(true);
                //isPubbleLightLOpen = true;
            }else{
                sp.edit().putBoolean("isPubbleLightLOpenClick",false).commit();
                mPuddleLight.setSelected(false);
               // isPubbleLightLOpen = false;
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
                mLMessageNum = mLMessageNum-1;
                mSeatsLMessageNumber.setText(Integer.toString(mLMessageNum));
            }else{
                mLMessageNum = 0;
                mSeatsLMessageNumber.setText("0");
            }
            return;
        }else if (i ==   mSeatsLMessagePlus.getId()) {
            if (mLMessageNum < 7) {
                mLMessageNum = mLMessageNum+1;
                mSeatsLMessageNumber.setText( Integer.toString(mLMessageNum));
            }else{
                mLMessageNum = 7;
                mSeatsLMessageNumber.setText("7");
            }
            return;
        }else if (i ==   mSeatsRMessageReduce.getId()) {
            if ( mRMessageNum > 0) {
                mRMessageNum = mRMessageNum-1;
                mSeatsRMessageNumber.setText(Integer.toString(mRMessageNum));
            }else{
                mRMessageNum = 0;
                mSeatsRMessageNumber.setText("0");
            }
            return;
        }else if (i ==   mSeatsRMessagePlus.getId()) {
            if (mRMessageNum < 7) {
                mRMessageNum = mRMessageNum+1;
                mSeatsRMessageNumber.setText( Integer.toString(mRMessageNum));
            }else{
                mRMessageNum = 7;
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
            if (iMyAidlInterface2 != null) {
                try {
                    iMyAidlInterface2.setCanData("TWO,IVI_LMirrorAngle_Req,Move Up");
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            mLMirrorUp.setSelected(true);
            mLMirrorDown.setSelected(false);
            mLMirrorLeft.setSelected(false);
            mLMirrorRight.setSelected(false);
            return;
        }else if (i ==  mLMirrorDown.getId()) {
            if (iMyAidlInterface2 != null) {
                try {
                    iMyAidlInterface2.setCanData("TWO,IVI_LMirrorAngle_Req,Move Down");
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            mLMirrorUp.setSelected(false);
            mLMirrorDown.setSelected(true);
            mLMirrorLeft.setSelected(false);
            mLMirrorRight.setSelected(false);
            return;
        }else if (i ==  mLMirrorLeft.getId()) {
            if (iMyAidlInterface2 != null) {
                try {
                    iMyAidlInterface2.setCanData("TWO,IVI_LMirrorAngle_Req,Move Left");
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            mLMirrorUp.setSelected(false);
            mLMirrorDown.setSelected(false);
            mLMirrorLeft.setSelected(true);
            mLMirrorRight.setSelected(false);
            return;
        }else if (i ==  mLMirrorRight.getId()) {
            if (iMyAidlInterface2 != null) {
                try {
                    iMyAidlInterface2.setCanData("TWO,IVI_LMirrorAngle_Req,Move Right");
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            mLMirrorUp.setSelected(false);
            mLMirrorDown.setSelected(false);
            mLMirrorLeft.setSelected(false);
            mLMirrorRight.setSelected(true);
            return;
        }else if (i ==  mRMirrorUp.getId()) {
            if (iMyAidlInterface2 != null) {
                try {
                    iMyAidlInterface2.setCanData("TWO,IVI_RMirrorAngle_Req,Move Up");
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            mRMirrorUp.setSelected(true);
            mRMirrorDown.setSelected(false);
            mRMirrorLeft.setSelected(false);
            mRMirrorRight.setSelected(false);
            return;
        }else if (i ==  mRMirrorDown.getId()) {
            if (iMyAidlInterface2 != null) {
                try {
                    iMyAidlInterface2.setCanData("TWO,IVI_RMirrorAngle_Req,Move Down");
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            mRMirrorUp.setSelected(false);
            mRMirrorDown.setSelected(true);
            mRMirrorLeft.setSelected(false);
            mRMirrorRight.setSelected(false);
            return;
        }else if (i ==  mRMirrorLeft.getId()) {
            if (iMyAidlInterface2 != null) {
                try {
                    iMyAidlInterface2.setCanData("TWO,IVI_RMirrorAngle_Req,Move Left");
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            mRMirrorUp.setSelected(false);
            mRMirrorDown.setSelected(false);
            mRMirrorLeft.setSelected(true);
            mRMirrorRight.setSelected(false);
            return;
        }else if (i ==  mRMirrorRight.getId()) {
            if (iMyAidlInterface2 != null) {
                try {
                    iMyAidlInterface2.setCanData("TWO,IVI_RMirrorAngle_Req,Move Right");
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            mRMirrorUp.setSelected(false);
            mRMirrorDown.setSelected(false);
            mRMirrorLeft.setSelected(false);
            mRMirrorRight.setSelected(true);
            return;
        } else if (i ==  mMirrorFolderDisplayIcon.getId()) {
            if (sp.getBoolean("isMirrorOpenClick",false)  == false) {
                if (iMyAidlInterface2 != null) {
                    try {
                        iMyAidlInterface2.setCanData("TWO,IVI_RearMirrorOpen_Req,Open");
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                sp.edit().putBoolean("isMirrorOpenClick",true).commit();
                mMirrorFolderDisplayIcon.setSelected(true);
                //isMirrorOpen = true;
            } else{
                if (iMyAidlInterface2 != null) {
                    try {
                        iMyAidlInterface2.setCanData("TWO,IVI_RearMirrorOpen_Req,Close");
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                sp.edit().putBoolean("isMirrorOpenClick",false).commit();
                mMirrorFolderDisplayIcon.setSelected(false);
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
            if (sp.getBoolean("isRightFrontDoorLockClick",false) && sp.getBoolean("isRightRearDoorLockClick",false) && sp.getBoolean("isLeftRearDoorLockClick",false) && sp.getBoolean("isLeftFrontDoorLockClick",false)) {
                return true;
            }
            return false;
        }
}
