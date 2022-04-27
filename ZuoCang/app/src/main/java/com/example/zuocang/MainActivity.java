package com.example.zuocang;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;

import com.example.zuocang.adapter.MenuListAdapter;

import java.util.ArrayList;

public class MainActivity extends Activity implements View.OnClickListener {
    protected ListView mMenuListView;
    String[] itemName = {"Doors,Windows", "Lights", "Drive Mode", "Seats", "Mirrors", "Steering"};
    private String TAG = "MainActivity";
    private MenuListAdapter mListAdapter;
    private Switch mAmbientSwitch, mFrontlightSystemSwitch;
    private LinearLayout mAmbientDisplayLayout;
    private OneButtonDialog oneButtonDialog;
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
    private ArrayList<Integer> images = new ArrayList<>();

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
        mExteriorLightOff = findViewById(R.id.exterior_light_off);
        mHighBeamLightAuto = findViewById(R.id.exterior_high_beam_light_auto);
        mFrogFrontLight = findViewById(R.id.frog_front_light);
        mFrogRearLight = findViewById(R.id.frog_rear_light);
        mReadingLightl = findViewById(R.id.reading_light_l);
        mReadingLight = findViewById(R.id.reading_light);
        mReadingLightR = findViewById(R.id.reading_light_r);
        mPuddleLight = findViewById(R.id.pubble_light);
        mFrogFrontLight.setEnabled(true);
        mFrogRearLight.setEnabled(true);
        mExteriorLightAuto.setSelected(true);
        mDriveModeNormal.setSelected(true);
        mAmbientModeOriginal.setSelected(true);
        mAmbientModeDisplayImage.setBackgroundResource(R.drawable.car_blur);
        mDriveModeDisplayImage.setBackgroundResource(R.drawable.img_dynamic_normal);
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
        mMenuListView.setAdapter(mListAdapter);
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
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == mDriveModeComfort.getId()) {
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
            mAmbientModeOriginal.setSelected(true);
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
                mExteriorLightOff.setSelected(false);
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
            mExteriorLightOff.setSelected(false);
            mFrogFrontLight.setEnabled(true);
            mFrogRearLight.setEnabled(true);
            isExteriorLightAutoOpen=true;
            return;
        } else if (i == mExteriorLowBeamLight.getId()) {
            mExteriorLightAuto.setSelected(false);
            mExteriorHighBeamLight.setSelected(false);
            mExteriorLowBeamLight.setSelected(true);
            mExteriorLightOff.setSelected(false);
            mFrogFrontLight.setEnabled(true);
            mFrogRearLight.setEnabled(true);
            isExteriorLightAutoOpen=true;
            return;
        } else if (i ==  mExteriorLightOff.getId()) {
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
        }else if (i ==  mHighBeamLightAuto.getId()) {
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
        }
    }
}
