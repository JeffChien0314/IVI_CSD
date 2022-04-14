package com.android.car.carlauncher;


import android.app.Activity;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.MotionEvent;
import android.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;


/**
 * Launcher activity that shows climate menu.
 */
public final class ClimateMenuActivity extends Activity {

    private static final String TAG = "ClimateMenuActivity";
	
	//climate menu related -->
    private ConstraintLayout climateMenuLayout;
    private ImageView downArrow;
    private ImageView powerImage;
    private ImageView autoImage;
    private ImageView acImage;
    private ImageView maxImage;
    private ImageView controlRearImage;
    private ImageView steeringWheelHeatingImage;
    private ImageView wiperHeatingImage;
    private ImageView syncImage;
    private ImageView windDirectionLeft1Image;
    private ImageView windDirectionLeft2Image;
    private ImageView windDirectionLeft3Image;
    private ImageView windDirectionRight1Image;
    private ImageView windDirectionRight2Image;
    private ImageView windDirectionRight3Image;

    private ImageView seatTemperatureHotLeftImage;
    private ImageView seatTemperatureHotLeft1Image;
    private ImageView seatTemperatureHotLeft2Image;
    private ImageView seatTemperatureHotLeft3Image;
    private ImageView seatTemperatureColdLeftImage;
    private ImageView seatTemperatureColdLeft1Image;
    private ImageView seatTemperatureColdLeft2Image;
    private ImageView seatTemperatureColdLeft3Image;
    private ImageView seatLeftImage;

    private ImageView seatTemperatureHotRightImage;
    private ImageView seatTemperatureHotRight1Image;
    private ImageView seatTemperatureHotRight2Image;
    private ImageView seatTemperatureHotRight3Image;
    private ImageView seatTemperatureColdRightImage;
    private ImageView seatTemperatureColdRight1Image;
    private ImageView seatTemperatureColdRight2Image;
    private ImageView seatTemperatureColdRight3Image;
    private ImageView seatRightImage;

    private boolean powerFlag;
    private boolean autoFlag;
    private boolean acFlag;
    private boolean maxFlag;
    private boolean controlRearFlag;
    private boolean steeringWheelHeatingFlag;
    private boolean wiperHeatingFlag;
    private boolean syncFlag;
    private boolean windDirectionLeft1Flag;
    private boolean windDirectionLeft2Flag;
    private boolean windDirectionLeft3Flag;
    private boolean windDirectionRight1Flag;
    private boolean windDirectionRight2Flag;
    private boolean windDirectionRight3Flag;
    private int seatTemperatureLeftLevel = 2;
    private int seatTemperatureRightLevel = 0;
    //climate menu related <--

    //seekbar related -->
    private ConstraintLayout seekBarLayout;
    private VerticalSeekBar temperatureSeekBarLeft;
    private IndicatorVerticalSeekBar airSeekBarLeft;
    private VerticalSeekBar volumeSeekBar;
    private IndicatorVerticalSeekBar airSeekBarRight;
    private VerticalSeekBar temperatureSeekBarRight;
    private TextView temperatureLeftText;
    private TextView airLeftText;
    private TextView airRightText;
    private TextView temperatureRightText;
    private CountDownTimer countDownTimer;
    //seekbar related <--


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_climate_menu);
        initView();
        handleIntent(getIntent());
        addListener();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void initView(){

        climateMenuLayout=findViewById(R.id.climate_menu_layout);
        downArrow=findViewById(R.id.down_arrow);
        powerImage = findViewById(R.id.btn_power);
        autoImage = findViewById(R.id.btn_auto);
        acImage = findViewById(R.id.btn_ac);
        maxImage = findViewById(R.id.btn_max);
        controlRearImage = findViewById(R.id.btn_control_rear);
        steeringWheelHeatingImage = findViewById(R.id.btn_steering_wheel_heating);
        wiperHeatingImage = findViewById(R.id.btn_wiper_heating_heating);
        syncImage = findViewById(R.id.btn_sync);
        windDirectionLeft1Image = findViewById(R.id.btn_wind_direction_left1);
        windDirectionLeft2Image = findViewById(R.id.btn_wind_direction_left2);
        windDirectionLeft3Image = findViewById(R.id.btn_wind_direction_left3);
        windDirectionRight1Image = findViewById(R.id.btn_wind_direction_right1);
        windDirectionRight2Image = findViewById(R.id.btn_wind_direction_right2);
        windDirectionRight3Image = findViewById(R.id.btn_wind_direction_right3);
        seatTemperatureHotLeftImage = findViewById(R.id.btn_seat_temperature_hot_left);
        seatTemperatureHotLeft1Image = findViewById(R.id.btn_seat_temperature_hot_left1);
        seatTemperatureHotLeft2Image = findViewById(R.id.btn_seat_temperature_hot_left2);
        seatTemperatureHotLeft3Image = findViewById(R.id.btn_seat_temperature_hot_left3);
        seatTemperatureColdLeftImage = findViewById(R.id.btn_seat_temperature_cold_left);
        seatTemperatureColdLeft1Image = findViewById(R.id.btn_seat_temperature_cold_left1);
        seatTemperatureColdLeft2Image = findViewById(R.id.btn_seat_temperature_cold_left2);
        seatTemperatureColdLeft3Image = findViewById(R.id.btn_seat_temperature_cold_left3);
        seatLeftImage = findViewById(R.id.btn_seat_left);
        seatTemperatureHotRightImage = findViewById(R.id.btn_seat_temperature_hot_right);
        seatTemperatureHotRight1Image = findViewById(R.id.btn_seat_temperature_hot_right1);
        seatTemperatureHotRight2Image = findViewById(R.id.btn_seat_temperature_hot_right2);
        seatTemperatureHotRight3Image = findViewById(R.id.btn_seat_temperature_hot_right3);
        seatTemperatureColdRightImage = findViewById(R.id.btn_seat_temperature_cold_right);
        seatTemperatureColdRight1Image = findViewById(R.id.btn_seat_temperature_cold_right1);
        seatTemperatureColdRight2Image = findViewById(R.id.btn_seat_temperature_cold_right2);
        seatTemperatureColdRight3Image = findViewById(R.id.btn_seat_temperature_cold_right3);
        seatRightImage = findViewById(R.id.btn_seat_right);


        seekBarLayout=findViewById(R.id.seekbar_layout);
        temperatureSeekBarLeft=findViewById(R.id.temperature_seekbar_left);
        airSeekBarLeft=findViewById(R.id.air_seekbar_left);
        volumeSeekBar=findViewById(R.id.volume_seekbar);
        airSeekBarRight=findViewById(R.id.air_seekbar_right);
        temperatureSeekBarRight=findViewById(R.id.temperature_seekbar_right);
        temperatureLeftText = findViewById(R.id.temperature_left_text);
        airLeftText = findViewById(R.id.air_left_text);
        airRightText = findViewById(R.id.air_right_text);
        temperatureRightText = findViewById(R.id.temperature_right_text);

        temperatureLeftText.setText((temperatureSeekBarLeft.getProgress() + 16) + "°");
        airLeftText.setText(airSeekBarLeft.getProgress() + "");
        airRightText.setText(airSeekBarRight.getProgress() + "");
        temperatureRightText.setText((temperatureSeekBarRight.getProgress()+16) + "°");
    }

    private void handleIntent(Intent intent) {
        if (intent != null) {
            String action = intent.getAction();
            if (action != null && action.equals("com.fxc.ev.climateMenu")) {
                seekBarLayout.setVisibility(View.GONE);
                climateMenuLayout.setVisibility(View.VISIBLE);
                loadCurrentClimateControl();
            } else if (action != null && action.equals("com.fxc.ev.temperature.left")) {
                seekBarLayout.setVisibility(View.VISIBLE);
                temperatureSeekBarLeft.setVisibility(View.VISIBLE);
                temperatureLeftText.setVisibility(View.VISIBLE);
                airSeekBarLeft.setVisibility(View.GONE);
                airLeftText.setVisibility(View.GONE);
                volumeSeekBar.setVisibility(View.GONE);
                airSeekBarRight.setVisibility(View.GONE);
                airRightText.setVisibility(View.GONE);
                temperatureSeekBarRight.setVisibility(View.GONE);
                temperatureRightText.setVisibility(View.GONE);
                climateMenuLayout.setVisibility(View.GONE);
            } else if (action != null && action.equals("com.fxc.ev.air.left")) {
                seekBarLayout.setVisibility(View.VISIBLE);
                temperatureSeekBarLeft.setVisibility(View.GONE);
                temperatureLeftText.setVisibility(View.GONE);
                airSeekBarLeft.setVisibility(View.VISIBLE);
                airLeftText.setVisibility(View.VISIBLE);
                volumeSeekBar.setVisibility(View.GONE);
                airSeekBarRight.setVisibility(View.GONE);
                airRightText.setVisibility(View.GONE);
                temperatureSeekBarRight.setVisibility(View.GONE);
                temperatureRightText.setVisibility(View.GONE);
                climateMenuLayout.setVisibility(View.GONE);
            } else if (action != null && action.equals("com.fxc.ev.volume")) {
                seekBarLayout.setVisibility(View.VISIBLE);
                temperatureSeekBarLeft.setVisibility(View.GONE);
                temperatureLeftText.setVisibility(View.GONE);
                airSeekBarLeft.setVisibility(View.GONE);
                airLeftText.setVisibility(View.GONE);
                volumeSeekBar.setVisibility(View.VISIBLE);
                airSeekBarRight.setVisibility(View.GONE);
                airRightText.setVisibility(View.GONE);
                temperatureSeekBarRight.setVisibility(View.GONE);
                temperatureRightText.setVisibility(View.GONE);
                climateMenuLayout.setVisibility(View.GONE);
            } else if (action != null && action.equals("com.fxc.ev.air.right")) {
                seekBarLayout.setVisibility(View.VISIBLE);
                temperatureSeekBarLeft.setVisibility(View.GONE);
                temperatureLeftText.setVisibility(View.GONE);
                airSeekBarLeft.setVisibility(View.GONE);
                airLeftText.setVisibility(View.GONE);
                volumeSeekBar.setVisibility(View.GONE);
                airSeekBarRight.setVisibility(View.VISIBLE);
                airRightText.setVisibility(View.VISIBLE);
                temperatureSeekBarRight.setVisibility(View.GONE);
                temperatureRightText.setVisibility(View.GONE);
                climateMenuLayout.setVisibility(View.GONE);
            } else if (action != null && action.equals("com.fxc.ev.temperature.right")) {
                seekBarLayout.setVisibility(View.VISIBLE);
                temperatureSeekBarLeft.setVisibility(View.GONE);
                temperatureLeftText.setVisibility(View.GONE);
                airSeekBarLeft.setVisibility(View.GONE);
                airLeftText.setVisibility(View.GONE);
                volumeSeekBar.setVisibility(View.GONE);
                airSeekBarRight.setVisibility(View.GONE);
                airRightText.setVisibility(View.GONE);
                temperatureSeekBarRight.setVisibility(View.VISIBLE);
                temperatureRightText.setVisibility(View.VISIBLE);
                climateMenuLayout.setVisibility(View.GONE);
            }
        }
    }

    private void loadCurrentClimateControl() {
        //TODO:获取目前温控相关值
        setPower(powerFlag);
        setAuto(autoFlag);
        setAc(acFlag);
        setMax(maxFlag);
        setControlRear(controlRearFlag);
        setSteeringWheelHeating(steeringWheelHeatingFlag);
        setWiperHeating(wiperHeatingFlag);
        setSync(syncFlag);
        setWindDirectionLeft1(windDirectionLeft1Flag);
        setWindDirectionLeft2(windDirectionLeft2Flag);
        setWindDirectionLeft3(windDirectionLeft3Flag);
        setWindDirectionRight1(windDirectionRight1Flag);
        setWindDirectionRight2(windDirectionRight2Flag);
        setWindDirectionRight3(windDirectionRight3Flag);
        setSeatTemperatureLeft(seatTemperatureLeftLevel);
        setSeatTemperatureRight(seatTemperatureRightLevel);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (seekBarLayout.getVisibility() == View.VISIBLE && event.getAction() == MotionEvent.ACTION_DOWN) {
            finish();
        }
        return super.onTouchEvent(event);
    }

    private void addListener() {

        downArrow.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        powerImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                powerFlag = !powerFlag;
                setPower(powerFlag);
            }
        });

        autoImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                autoFlag = !autoFlag;
                setAuto(autoFlag);
            }
        });

        acImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                acFlag = !acFlag;
                setAc(acFlag);
            }
        });

        maxImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                maxFlag = !maxFlag;
                setMax(maxFlag);
            }
        });

        controlRearImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                controlRearFlag = !controlRearFlag;
                setControlRear(controlRearFlag);
            }
        });

        steeringWheelHeatingImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                steeringWheelHeatingFlag = !steeringWheelHeatingFlag;
                setSteeringWheelHeating(steeringWheelHeatingFlag);
            }
        });

        wiperHeatingImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                wiperHeatingFlag = !wiperHeatingFlag;
                setWiperHeating(wiperHeatingFlag);
            }
        });

        syncImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                syncFlag = !syncFlag;
                setSync(syncFlag);
            }
        });

        windDirectionLeft1Image.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                windDirectionLeft1Flag = !windDirectionLeft1Flag;
                setWindDirectionLeft1(windDirectionLeft1Flag);
            }
        });

        windDirectionLeft2Image.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                windDirectionLeft2Flag = !windDirectionLeft2Flag;
                setWindDirectionLeft2(windDirectionLeft2Flag);
            }
        });

        windDirectionLeft3Image.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                windDirectionLeft3Flag = !windDirectionLeft3Flag;
                setWindDirectionLeft3(windDirectionLeft3Flag);
            }
        });

        windDirectionRight1Image.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                windDirectionRight1Flag = !windDirectionRight1Flag;
                setWindDirectionRight1(windDirectionRight1Flag);
            }
        });

        windDirectionRight2Image.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                windDirectionRight2Flag = !windDirectionRight2Flag;
                setWindDirectionRight2(windDirectionRight2Flag);
            }
        });

        windDirectionRight3Image.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                windDirectionRight3Flag = !windDirectionRight3Flag;
                setWindDirectionRight3(windDirectionRight3Flag);
            }
        });

        seatTemperatureHotLeftImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (seatTemperatureLeftLevel < 0) {
                    seatTemperatureLeftLevel = 1;
                } else if (seatTemperatureLeftLevel >= 0 && seatTemperatureLeftLevel < 3) {
                    seatTemperatureLeftLevel = seatTemperatureLeftLevel + 1;
                } else {
                    seatTemperatureLeftLevel = 0;
                }

                setSeatTemperatureLeft(seatTemperatureLeftLevel);
            }
        });

        seatTemperatureColdLeftImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (seatTemperatureLeftLevel > 0) {
                    seatTemperatureLeftLevel = -1;
                } else if (seatTemperatureLeftLevel > -3 && seatTemperatureLeftLevel <= 0) {
                    seatTemperatureLeftLevel = seatTemperatureLeftLevel - 1;
                } else {
                    seatTemperatureLeftLevel = 0;
                }

                setSeatTemperatureLeft(seatTemperatureLeftLevel);

            }
        });

        seatTemperatureHotRightImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (seatTemperatureRightLevel < 0) {
                    seatTemperatureRightLevel = 1;
                } else if (seatTemperatureRightLevel >= 0 && seatTemperatureRightLevel < 3) {
                    seatTemperatureRightLevel = seatTemperatureRightLevel + 1;
                } else {
                    seatTemperatureRightLevel = 0;
                }

                setSeatTemperatureRight(seatTemperatureRightLevel);
            }
        });

        seatTemperatureColdRightImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (seatTemperatureRightLevel > 0) {
                    seatTemperatureRightLevel = -1;
                } else if (seatTemperatureRightLevel > -3 && seatTemperatureRightLevel <= 0) {
                    seatTemperatureRightLevel = seatTemperatureRightLevel - 1;
                } else {
                    seatTemperatureRightLevel = 0;
                }

                setSeatTemperatureRight(seatTemperatureRightLevel);

            }
        });

        temperatureSeekBarLeft.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                temperatureLeftText.setText((temperatureSeekBarLeft.getProgress()+16) + "°");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //拖动停止
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //开始拖动
            }

        });

       temperatureSeekBarLeft.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (countDownTimer != null) {
                            countDownTimer.cancel();
                            countDownTimer = null;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        countDownTimer = new CountDownTimer(5000, 5000) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                            }

                            @Override
                            public void onFinish() {
                                finish();
                            }
                        }.start();

                        break;

                }
                return false;
            }

        });

        temperatureSeekBarRight.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                temperatureRightText.setText((temperatureSeekBarRight.getProgress() + 16) + "°");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //拖动停止
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //开始拖动
            }

        });

       temperatureSeekBarRight.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (countDownTimer != null) {
                            countDownTimer.cancel();
                            countDownTimer = null;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        countDownTimer = new CountDownTimer(5000, 5000) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                            }

                            @Override
                            public void onFinish() {
                                finish();
                            }
                        }.start();

                        break;

                }
                return false;
            }

        });

      
       airSeekBarLeft.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                airLeftText.setText(airSeekBarLeft.getProgress() + "");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //拖动停止
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //开始拖动
            }

        });

       airSeekBarLeft.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (countDownTimer != null) {
                            countDownTimer.cancel();
                            countDownTimer = null;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        countDownTimer = new CountDownTimer(5000, 5000) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                            }

                            @Override
                            public void onFinish() {
                                finish();
                            }
                        }.start();

                        break;

                }
                return false;
            }

        });

       airSeekBarRight.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                airRightText.setText(airSeekBarRight.getProgress() + "");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //拖动停止
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //开始拖动
            }

        });

       airSeekBarRight.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (countDownTimer != null) {
                            countDownTimer.cancel();
                            countDownTimer = null;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        countDownTimer = new CountDownTimer(5000, 5000) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                            }

                            @Override
                            public void onFinish() {
                                finish();
                            }
                        }.start();

                        break;

                }
                return false;
            }

        });

       
       volumeSeekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (countDownTimer != null) {
                            countDownTimer.cancel();
                            countDownTimer = null;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        countDownTimer = new CountDownTimer(5000, 5000) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                            }

                            @Override
                            public void onFinish() {
                                finish();
                            }
                        }.start();

                        break;

                }
                return false;
            }

        });



    }

    private void setPower(boolean flag) {
        if (flag) {
            powerImage.setImageResource(R.drawable.btn_thermal_control_active);
        } else {
            powerImage.setImageResource(R.drawable.btn_thermal_control_normal);
        }
    }

    private void setAuto(boolean flag) {
        if (flag) {
            autoImage.setImageResource(R.drawable.btn_auto_active_picture);
        } else {
            autoImage.setImageResource(R.drawable.btn_auto_normal_picture);
        }
    }

    private void setAc(boolean flag) {
        if (flag) {
            acImage.setImageResource(R.drawable.btn_ac_active);
        } else {
            acImage.setImageResource(R.drawable.btn_ac_normal);
        }
    }

    private void setMax(boolean flag){
        if (flag) {
            maxImage.setImageResource(R.drawable.btn_max_active);
        } else {
            maxImage.setImageResource(R.drawable.btn_max_normal);
        }
    }

    private void setControlRear(boolean flag) {
        if (flag) {
            controlRearImage.setImageResource(R.drawable.btn_control_rear_active);
        } else {
            controlRearImage.setImageResource(R.drawable.btn_control_rear_normal);
        }
    }

    private void setSteeringWheelHeating(boolean flag){
        if (flag) {
            steeringWheelHeatingImage.setImageResource(R.drawable.btn_steering_wheel_heating_active);
        } else {
            steeringWheelHeatingImage.setImageResource(R.drawable.btn_steering_wheel_heating_normal);
        }
    }

    private void setWiperHeating(boolean flag){
        if (flag) {
            wiperHeatingImage.setImageResource(R.drawable.btn_wiper_heating_active);
        } else {
            wiperHeatingImage.setImageResource(R.drawable.btn_wiper_heating_normal);
        }
    }

    private void setSync(boolean flag) {
        if (flag) {
            syncImage.setImageResource(R.drawable.btn_sync_active);
        } else {
            syncImage.setImageResource(R.drawable.btn_sync_normal);
        }
    }

    private void setWindDirectionLeft1(boolean flag) {
        if (flag) {
            windDirectionLeft1Image.setImageResource(R.drawable.btn_wind_direction_1_active);
        } else {
            windDirectionLeft1Image.setImageResource(R.drawable.btn_wind_direction_1_normal);
        }
    }

    private void setWindDirectionLeft2(boolean flag) {
        if (flag) {
            windDirectionLeft2Image.setImageResource(R.drawable.btn_wind_direction_2_active);
        } else {
            windDirectionLeft2Image.setImageResource(R.drawable.btn_wind_direction_2_normal);
        }
    }

    private void setWindDirectionLeft3(boolean flag) {
        if (flag) {
            windDirectionLeft3Image.setImageResource(R.drawable.btn_wind_direction_3_active);
        } else {
            windDirectionLeft3Image.setImageResource(R.drawable.btn_wind_direction_3_normal);
        }
    }

    private void setWindDirectionRight1(boolean flag) {
        if (flag) {
            windDirectionRight1Image.setImageResource(R.drawable.btn_wind_direction_1_active);
        } else {
            windDirectionRight1Image.setImageResource(R.drawable.btn_wind_direction_1_normal);
        }
    }

    private void setWindDirectionRight2(boolean flag) {
        if (flag) {
            windDirectionRight2Image.setImageResource(R.drawable.btn_wind_direction_2_active);
        } else {
            windDirectionRight2Image.setImageResource(R.drawable.btn_wind_direction_2_normal);
        }
    }

    private void setWindDirectionRight3(boolean flag) {
        if (flag) {
            windDirectionRight3Image.setImageResource(R.drawable.btn_wind_direction_3_active);
        } else {
            windDirectionRight3Image.setImageResource(R.drawable.btn_wind_direction_3_normal);
        }
    }

    private void setSeatTemperatureLeft(int value) {
        if (value == 0) {
            seatTemperatureHotLeftImage.setImageResource(R.drawable.seat_temperature_hot_normal);
            seatTemperatureHotLeft1Image.setImageResource(R.drawable.gray_normal);
            seatTemperatureHotLeft2Image.setImageResource(R.drawable.gray_normal);
            seatTemperatureHotLeft3Image.setImageResource(R.drawable.gray_normal);
            seatTemperatureColdLeftImage.setImageResource(R.drawable.btn_seat_temperature_cold_normal);
            seatTemperatureColdLeft1Image.setImageResource(R.drawable.gray_normal);
            seatTemperatureColdLeft2Image.setImageResource(R.drawable.gray_normal);
            seatTemperatureColdLeft3Image.setImageResource(R.drawable.gray_normal);
            seatLeftImage.setImageResource(R.drawable.seat_normal_left);

        } else if (value == -1) {
            seatTemperatureHotLeftImage.setImageResource(R.drawable.seat_temperature_hot_normal);
            seatTemperatureHotLeft1Image.setImageResource(R.drawable.gray_normal);
            seatTemperatureHotLeft2Image.setImageResource(R.drawable.gray_normal);
            seatTemperatureHotLeft3Image.setImageResource(R.drawable.gray_normal);
            seatTemperatureColdLeftImage.setImageResource(R.drawable.btn_seat_temperature_cold_active);
            seatTemperatureColdLeft1Image.setImageResource(R.drawable.code_enable);
            seatTemperatureColdLeft2Image.setImageResource(R.drawable.gray_normal);
            seatTemperatureColdLeft3Image.setImageResource(R.drawable.gray_normal);
            seatLeftImage.setImageResource(R.drawable.seat_ventilation_left);
        } else if (value == -2) {
            seatTemperatureHotLeftImage.setImageResource(R.drawable.seat_temperature_hot_normal);
            seatTemperatureHotLeft1Image.setImageResource(R.drawable.gray_normal);
            seatTemperatureHotLeft2Image.setImageResource(R.drawable.gray_normal);
            seatTemperatureHotLeft3Image.setImageResource(R.drawable.gray_normal);
            seatTemperatureColdLeftImage.setImageResource(R.drawable.btn_seat_temperature_cold_active);
            seatTemperatureColdLeft1Image.setImageResource(R.drawable.code_enable);
            seatTemperatureColdLeft2Image.setImageResource(R.drawable.code_enable);
            seatTemperatureColdLeft3Image.setImageResource(R.drawable.gray_normal);
            seatLeftImage.setImageResource(R.drawable.seat_ventilation_left);
        } else if (value == -3) {
            seatTemperatureHotLeftImage.setImageResource(R.drawable.seat_temperature_hot_normal);
            seatTemperatureHotLeft1Image.setImageResource(R.drawable.gray_normal);
            seatTemperatureHotLeft2Image.setImageResource(R.drawable.gray_normal);
            seatTemperatureHotLeft3Image.setImageResource(R.drawable.gray_normal);
            seatTemperatureColdLeftImage.setImageResource(R.drawable.btn_seat_temperature_cold_active);
            seatTemperatureColdLeft1Image.setImageResource(R.drawable.code_enable);
            seatTemperatureColdLeft2Image.setImageResource(R.drawable.code_enable);
            seatTemperatureColdLeft3Image.setImageResource(R.drawable.code_enable);
            seatLeftImage.setImageResource(R.drawable.seat_ventilation_left);
        } else if (value == 1) {
            seatTemperatureHotLeftImage.setImageResource(R.drawable.seat_temperature_hot_active);
            seatTemperatureHotLeft1Image.setImageResource(R.drawable.hot_enable);
            seatTemperatureHotLeft2Image.setImageResource(R.drawable.gray_normal);
            seatTemperatureHotLeft3Image.setImageResource(R.drawable.gray_normal);
            seatTemperatureColdLeftImage.setImageResource(R.drawable.btn_seat_temperature_cold_normal);
            seatTemperatureColdLeft1Image.setImageResource(R.drawable.gray_normal);
            seatTemperatureColdLeft2Image.setImageResource(R.drawable.gray_normal);
            seatTemperatureColdLeft3Image.setImageResource(R.drawable.gray_normal);
            seatLeftImage.setImageResource(R.drawable.seat_heating_left);
        } else if (value == 2) {
            seatTemperatureHotLeftImage.setImageResource(R.drawable.seat_temperature_hot_active);
            seatTemperatureHotLeft1Image.setImageResource(R.drawable.hot_enable);
            seatTemperatureHotLeft2Image.setImageResource(R.drawable.hot_enable);
            seatTemperatureHotLeft3Image.setImageResource(R.drawable.gray_normal);
            seatTemperatureColdLeftImage.setImageResource(R.drawable.btn_seat_temperature_cold_normal);
            seatTemperatureColdLeft1Image.setImageResource(R.drawable.gray_normal);
            seatTemperatureColdLeft2Image.setImageResource(R.drawable.gray_normal);
            seatTemperatureColdLeft3Image.setImageResource(R.drawable.gray_normal);
            seatLeftImage.setImageResource(R.drawable.seat_heating_left);
        } else if (value == 3) {
            seatTemperatureHotLeftImage.setImageResource(R.drawable.seat_temperature_hot_active);
            seatTemperatureHotLeft1Image.setImageResource(R.drawable.hot_enable);
            seatTemperatureHotLeft2Image.setImageResource(R.drawable.hot_enable);
            seatTemperatureHotLeft3Image.setImageResource(R.drawable.hot_enable);
            seatTemperatureColdLeftImage.setImageResource(R.drawable.btn_seat_temperature_cold_normal);
            seatTemperatureColdLeft1Image.setImageResource(R.drawable.gray_normal);
            seatTemperatureColdLeft2Image.setImageResource(R.drawable.gray_normal);
            seatTemperatureColdLeft3Image.setImageResource(R.drawable.gray_normal);
            seatLeftImage.setImageResource(R.drawable.seat_heating_left);
        }
    }

    private void setSeatTemperatureRight(int value) {
        if (value == 0) {
            seatTemperatureHotRightImage.setImageResource(R.drawable.seat_temperature_hot_normal);
            seatTemperatureHotRight1Image.setImageResource(R.drawable.gray_normal);
            seatTemperatureHotRight2Image.setImageResource(R.drawable.gray_normal);
            seatTemperatureHotRight3Image.setImageResource(R.drawable.gray_normal);
            seatTemperatureColdRightImage.setImageResource(R.drawable.btn_seat_temperature_cold_normal);
            seatTemperatureColdRight1Image.setImageResource(R.drawable.gray_normal);
            seatTemperatureColdRight2Image.setImageResource(R.drawable.gray_normal);
            seatTemperatureColdRight3Image.setImageResource(R.drawable.gray_normal);
            seatRightImage.setImageResource(R.drawable.seat_normal_right);

        } else if (value == -1) {
            seatTemperatureHotRightImage.setImageResource(R.drawable.seat_temperature_hot_normal);
            seatTemperatureHotRight1Image.setImageResource(R.drawable.gray_normal);
            seatTemperatureHotRight2Image.setImageResource(R.drawable.gray_normal);
            seatTemperatureHotRight3Image.setImageResource(R.drawable.gray_normal);
            seatTemperatureColdRightImage.setImageResource(R.drawable.btn_seat_temperature_cold_active);
            seatTemperatureColdRight1Image.setImageResource(R.drawable.code_enable);
            seatTemperatureColdRight2Image.setImageResource(R.drawable.gray_normal);
            seatTemperatureColdRight3Image.setImageResource(R.drawable.gray_normal);
            seatRightImage.setImageResource(R.drawable.seat_ventilation_right);
        } else if (value == -2) {
            seatTemperatureHotRightImage.setImageResource(R.drawable.seat_temperature_hot_normal);
            seatTemperatureHotRight1Image.setImageResource(R.drawable.gray_normal);
            seatTemperatureHotRight2Image.setImageResource(R.drawable.gray_normal);
            seatTemperatureHotRight3Image.setImageResource(R.drawable.gray_normal);
            seatTemperatureColdRightImage.setImageResource(R.drawable.btn_seat_temperature_cold_active);
            seatTemperatureColdRight1Image.setImageResource(R.drawable.code_enable);
            seatTemperatureColdRight2Image.setImageResource(R.drawable.code_enable);
            seatTemperatureColdRight3Image.setImageResource(R.drawable.gray_normal);
            seatRightImage.setImageResource(R.drawable.seat_ventilation_right);
        } else if (value == -3) {
            seatTemperatureHotRightImage.setImageResource(R.drawable.seat_temperature_hot_normal);
            seatTemperatureHotRight1Image.setImageResource(R.drawable.gray_normal);
            seatTemperatureHotRight2Image.setImageResource(R.drawable.gray_normal);
            seatTemperatureHotRight3Image.setImageResource(R.drawable.gray_normal);
            seatTemperatureColdRightImage.setImageResource(R.drawable.btn_seat_temperature_cold_active);
            seatTemperatureColdRight1Image.setImageResource(R.drawable.code_enable);
            seatTemperatureColdRight2Image.setImageResource(R.drawable.code_enable);
            seatTemperatureColdRight3Image.setImageResource(R.drawable.code_enable);
            seatRightImage.setImageResource(R.drawable.seat_ventilation_right);
        } else if (value == 1) {
            seatTemperatureHotRightImage.setImageResource(R.drawable.seat_temperature_hot_active);
            seatTemperatureHotRight1Image.setImageResource(R.drawable.hot_enable);
            seatTemperatureHotRight2Image.setImageResource(R.drawable.gray_normal);
            seatTemperatureHotRight3Image.setImageResource(R.drawable.gray_normal);
            seatTemperatureColdRightImage.setImageResource(R.drawable.btn_seat_temperature_cold_normal);
            seatTemperatureColdRight1Image.setImageResource(R.drawable.gray_normal);
            seatTemperatureColdRight2Image.setImageResource(R.drawable.gray_normal);
            seatTemperatureColdRight3Image.setImageResource(R.drawable.gray_normal);
            seatRightImage.setImageResource(R.drawable.seat_heating_right);
        } else if (value == 2) {
            seatTemperatureHotRightImage.setImageResource(R.drawable.seat_temperature_hot_active);
            seatTemperatureHotRight1Image.setImageResource(R.drawable.hot_enable);
            seatTemperatureHotRight2Image.setImageResource(R.drawable.hot_enable);
            seatTemperatureHotRight3Image.setImageResource(R.drawable.gray_normal);
            seatTemperatureColdRightImage.setImageResource(R.drawable.btn_seat_temperature_cold_normal);
            seatTemperatureColdRight1Image.setImageResource(R.drawable.gray_normal);
            seatTemperatureColdRight2Image.setImageResource(R.drawable.gray_normal);
            seatTemperatureColdRight3Image.setImageResource(R.drawable.gray_normal);
            seatRightImage.setImageResource(R.drawable.seat_heating_right);
        } else if (value == 3) {
            seatTemperatureHotRightImage.setImageResource(R.drawable.seat_temperature_hot_active);
            seatTemperatureHotRight1Image.setImageResource(R.drawable.hot_enable);
            seatTemperatureHotRight2Image.setImageResource(R.drawable.hot_enable);
            seatTemperatureHotRight3Image.setImageResource(R.drawable.hot_enable);
            seatTemperatureColdRightImage.setImageResource(R.drawable.btn_seat_temperature_cold_normal);
            seatTemperatureColdRight1Image.setImageResource(R.drawable.gray_normal);
            seatTemperatureColdRight2Image.setImageResource(R.drawable.gray_normal);
            seatTemperatureColdRight3Image.setImageResource(R.drawable.gray_normal);
            seatRightImage.setImageResource(R.drawable.seat_heating_right);
        }
    }

	}
