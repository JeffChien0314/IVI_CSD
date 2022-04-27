package com.android.systemui.statusbar.car.climate;

import android.app.Activity;
import android.os.Bundle;
import android.os.UserHandle;
import android.widget.SeekBar;
import android.widget.TextView;
import android.content.Intent;
import android.os.CountDownTimer;
import android.view.MotionEvent;
import android.view.View;

import com.android.systemui.R;


public class AirRightActivity extends Activity {

    private IndicatorVerticalSeekBar airSeekBarRight;
    private TextView airRightText;
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_air_right);
        airSeekBarRight = findViewById(R.id.air_seekbar_right);
        airRightText = findViewById(R.id.air_right_text);

        handleIntent(getIntent());

        airSeekBarRight.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                airRightText.setText(airSeekBarRight.getProgress() + "");

                Intent intent = new Intent("air.right.change");
                intent.putExtra("className", "com.android.systemui.statusbar.car.climate.AirRightActivity");
                intent.putExtra("value", airSeekBarRight.getProgress() + "");
                //sendBroadcast(intent);
                sendBroadcastAsUser(intent, UserHandle.ALL);

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

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (intent != null) {
            String seekBarValue = intent.getStringExtra("defaultValue");
                
            if (seekBarValue != null && !seekBarValue.isEmpty()) {
                airRightText.setText(seekBarValue);
                try {
                    int progress = Integer.parseInt(seekBarValue);
                    airSeekBarRight.setProgress(progress);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            finish();
        }
        return super.onTouchEvent(event);
    }

 

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
    


}
