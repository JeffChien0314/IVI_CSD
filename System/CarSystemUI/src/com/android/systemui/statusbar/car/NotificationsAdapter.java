package com.android.systemui.statusbar.car;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.android.systemui.R;
import com.android.car.notification.template.CarNotificationBaseViewHolder;
import com.android.car.notification.NotificationClickHandlerFactory;

class NotificationsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private NotificationClickHandlerFactory notificationClickHandlerFactory; 

    private ImageView frontFogLight;
    private ImageView rearFogLight;
    private ImageView readingLight;
    private ImageView rearviewMirror;
    private ImageView doorLockLeft1;
    private ImageView doorLockLeft2;
    private ImageView doorLockRight1;
    private ImageView doorLockRight2;
    private ImageView luggageFrunk;
    private ImageView luggageDormer;
    private ImageView luggageTrunk;
    private ImageView leaveMode;
    private ImageView windowLock;
    private ImageView doorLock;
    private ImageView sunCurtain;
    private ImageView closeScreen;
    private ImageView windowFrontLeftUp;
    private ImageView windowFrontLeftDown;
    private ImageView windowRearLeftUp;
    private ImageView windowRearLeftDown;
    private ImageView windowFrontRightUp;
    private ImageView windowFrontRightDown;
    private ImageView windowRearRightUp;
    private ImageView windowRearRightDown;

    private RadioButton offLight;

    private List<ImageView> recentApps=new ArrayList<>();


    private int frontFogLightFlag=1; //0:normal;1:active;-1:disable
    private int rearFogLightFlag;
    private boolean readingLightFlag;
    private boolean rearviewMirrorFlag;
    private int doorLockLeft1Flag;
    private int doorLockLeft2Flag;
    private int doorLockRight1Flag;
    private int doorLockRight2Flag;
    private boolean doorOpenLeft1Flag; //侦测车门有没有打开
    private boolean doorOpenLeft2Flag;
    private boolean doorOpenRight1Flag;
    private boolean doorOpenRight2Flag;
    private boolean luggageFrunkFlag;
    private boolean luggageDormerFlag;
    private int luggageTrunkFlag;
    private boolean leaveModeFlag;
    private boolean windowLockFlag;
    private int doorLockFlag;
    private boolean sunCurtainFlag;
    private boolean closeScreenFlag;


    public NotificationsAdapter(Context context,NotificationClickHandlerFactory mNotificationClickHandlerFactory) {
        this.mContext = context;
        this.notificationClickHandlerFactory=mNotificationClickHandlerFactory;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MainViewHolder viewHolder = new MainViewHolder(LayoutInflater.from(mContext).inflate(R.layout.notifications_detail, parent, false));
        viewHolder.setNotificationsAdapter(this);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 1;
    }

    private void processRecentTasks() {
        //final Context context = this.getApplication();
        final PackageManager pm = mContext.getPackageManager();
        final ActivityManager am = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        final ArrayList<ApplicationInfo> recents = new ArrayList<>();
        try {
            List<ActivityManager.RecentTaskInfo> Rapptask = am.getRecentTasks(6, 0);
            for (int j = 0; j < Rapptask.size(); j++) {
                final Intent intent = Rapptask.get(j).baseIntent;
                if (Intent.ACTION_MAIN.equals(intent.getAction()) &&
                        !intent.hasCategory(Intent.CATEGORY_HOME)) {
                    ApplicationInfo applicationInfo = pm.getApplicationInfo(intent.getComponent().getPackageName(), PackageManager.GET_META_DATA);
                    if (applicationInfo == null) continue;
                    recents.add(applicationInfo);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!recents.isEmpty()) {
            for (int i = 0; i < recents.size(); i++) {
                final int m = i;
                recentApps.get(m).setImageDrawable(pm.getApplicationIcon(recents.get(m)));
                recentApps.get(m).setOnClickListener(v -> {
                    try {
                        //通过包名启动
                        Intent intent = pm.getLaunchIntentForPackage(recents.get(m).packageName);
                        if (null != intent) {
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            mContext.startActivity(intent);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }
    }


    class MainViewHolder extends CarNotificationBaseViewHolder {

        WeakReference<NotificationsAdapter> ref;
        NotificationsAdapter notificationsAdapter;


        public MainViewHolder(@NonNull View itemView) {
            super(itemView,notificationClickHandlerFactory);
            initView(itemView);
            addListener();
        }

        public void setNotificationsAdapter(NotificationsAdapter adapter) {
            if (adapter != null) {
                ref = new WeakReference<>(adapter);
            }
            notificationsAdapter = ref.get();
        }
    }

    private void initView(View itemView) {
        frontFogLight = itemView.findViewById(R.id.front_fog_light);
        rearFogLight = itemView.findViewById(R.id.rear_fog_light);
        readingLight = itemView.findViewById(R.id.reading_light);
        rearviewMirror = itemView.findViewById(R.id.rearview_mirror);
        doorLockLeft1 = itemView.findViewById(R.id.doorlock_left1);
        doorLockLeft2 = itemView.findViewById(R.id.doorlock_left2);
        doorLockRight1 = itemView.findViewById(R.id.doorlock_right1);
        doorLockRight2 = itemView.findViewById(R.id.doorlock_right2);
        luggageFrunk = itemView.findViewById(R.id.luggage_frunk);
        luggageDormer = itemView.findViewById(R.id.luggage_dormer);
        luggageTrunk = itemView.findViewById(R.id.luggage_trunk);
        leaveMode = itemView.findViewById(R.id.leave_mode);
        windowLock = itemView.findViewById(R.id.window_lock);
        doorLock = itemView.findViewById(R.id.door_lock);
        sunCurtain = itemView.findViewById(R.id.sun_curtain);
        closeScreen = itemView.findViewById(R.id.close_screen);
        windowFrontLeftUp = itemView.findViewById(R.id.window_front_left_up);
        windowFrontLeftDown = itemView.findViewById(R.id.window_front_left_down);
        windowRearLeftUp = itemView.findViewById(R.id.window_rear_left_up);
        windowRearLeftDown = itemView.findViewById(R.id.window_rear_left_down);
        windowFrontRightUp = itemView.findViewById(R.id.window_front_right_up);
        windowFrontRightDown = itemView.findViewById(R.id.window_front_right_down);
        windowRearRightUp = itemView.findViewById(R.id.window_rear_right_up);
        windowRearRightDown = itemView.findViewById(R.id.window_rear_right_down);

        offLight = itemView.findViewById(R.id.light_radio4);

        recentApps.add(itemView.findViewById(R.id.recent_app1));
        recentApps.add(itemView.findViewById(R.id.recent_app2));
        recentApps.add(itemView.findViewById(R.id.recent_app3));
        recentApps.add(itemView.findViewById(R.id.recent_app4));
        recentApps.add(itemView.findViewById(R.id.recent_app5));
        recentApps.add(itemView.findViewById(R.id.recent_app6));

        loadCurrentSetting();

        processRecentTasks();

    }

    private void loadCurrentSetting() {
        if (frontFogLightFlag == -1) {
            frontFogLight.setEnabled(false);
        } else if (frontFogLightFlag == 0) {
            frontFogLight.setEnabled(true);
            frontFogLight.setSelected(false);
        } else if (frontFogLightFlag == 1) {
            frontFogLight.setEnabled(true);
            frontFogLight.setSelected(true);
        }

        if (rearFogLightFlag == -1) {
            rearFogLight.setEnabled(false);
        } else if (rearFogLightFlag == 0) {
            rearFogLight.setEnabled(true);
            rearFogLight.setSelected(false);
        } else if (rearFogLightFlag == 1) {
            rearFogLight.setEnabled(true);
            rearFogLight.setSelected(true);
        }

        if (readingLightFlag) readingLight.setSelected(true);
        else readingLight.setSelected(false);
        if (rearviewMirrorFlag) rearviewMirror.setSelected(true);
        else rearviewMirror.setSelected(false);

        syncDoorLockSeparateBehavior(1);
        syncDoorLockSeparateBehavior(2);
        syncDoorLockSeparateBehavior(3);
        syncDoorLockSeparateBehavior(4);


        if (luggageFrunkFlag) luggageFrunk.setSelected(true);
        else luggageFrunk.setSelected(false);

        if (luggageDormerFlag) luggageDormer.setSelected(true);
        else luggageDormer.setSelected(false);

        syncLuggageTrunkBehavior();

        if (leaveModeFlag) leaveMode.setSelected(true);
        else leaveMode.setSelected(false);

        if (windowLockFlag) windowLock.setSelected(true);
        else windowLock.setSelected(false);

        syncDoorLockBehavior();

        if (sunCurtainFlag) sunCurtain.setSelected(true);
        else sunCurtain.setSelected(false);
        if (closeScreenFlag) closeScreen.setSelected(true);
        else closeScreen.setSelected(false);

    }

    private void addListener() {

        frontFogLight.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (frontFogLightFlag == 1) frontFogLightFlag = 0;
                else if (frontFogLightFlag == 0) frontFogLightFlag = 1;

                if (frontFogLightFlag == 0) {
                    frontFogLight.setSelected(false);
                } else if (frontFogLightFlag == 1) {
                    frontFogLight.setSelected(true);
                }
            }
        });

        rearFogLight.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (rearFogLightFlag == 1) rearFogLightFlag = 0;
                else if (rearFogLightFlag == 0) rearFogLightFlag = 1;

                if (rearFogLightFlag == 0) {
                    rearFogLight.setSelected(false);
                } else if (rearFogLightFlag == 1) {
                    rearFogLight.setSelected(true);
                }
            }
        });

        readingLight.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                readingLightFlag = !readingLightFlag;
                if (readingLightFlag) readingLight.setSelected(true);
                else readingLight.setSelected(false);
            }
        });

        rearviewMirror.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                rearviewMirrorFlag = !rearviewMirrorFlag;
                if (rearviewMirrorFlag) rearviewMirror.setSelected(true);
                else rearviewMirror.setSelected(false);
            }
        });

        doorLockLeft1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (doorLockLeft1Flag == 1) doorLockLeft1Flag = 0;
                else if (doorLockLeft1Flag == 0) doorLockLeft1Flag = 1;

                if (doorLockLeft1Flag == 0) {
                    doorLockLeft1.setSelected(false);
                    if (doorLockFlag == 1) {
                        doorLockFlag = 0;
                        syncDoorLockBehavior();
                        //TODO:需判断后车厢是否是开着的
                        luggageTrunkFlag = 0;
                        syncLuggageTrunkBehavior();
                    }
                } else if (doorLockLeft1Flag == 1) {
                    doorLockLeft1.setSelected(true);
                    if (doorLockLeft2Flag == 1 && doorLockRight1Flag == 1 && doorLockRight2Flag == 1 && doorLockFlag == 0) {
                        doorLockFlag = 1;
                        syncDoorLockBehavior();
                        //TODO:需判断后车厢是否是开着的
                        luggageTrunkFlag = -1;
                        syncLuggageTrunkBehavior();
                    }
                }
            }
        });

        doorLockLeft2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (doorLockLeft2Flag == 1) doorLockLeft2Flag = 0;
                else if (doorLockLeft2Flag == 0) doorLockLeft2Flag = 1;

                if (doorLockLeft2Flag == 0) {
                    doorLockLeft2.setSelected(false);
                    if (doorLockFlag == 1) {
                        doorLockFlag = 0;
                        syncDoorLockBehavior();
                        //TODO:需判断后车厢是否是开着的
                        luggageTrunkFlag = 0;
                        syncLuggageTrunkBehavior();
                    }
                } else if (doorLockLeft2Flag == 1) {
                    doorLockLeft2.setSelected(true);
                    if (doorLockLeft1Flag == 1 && doorLockRight1Flag == 1 && doorLockRight2Flag == 1 && doorLockFlag == 0) {
                        doorLockFlag = 1;
                        syncDoorLockBehavior();
                        //TODO:需判断后车厢是否是开着的
                        luggageTrunkFlag =-1;
                        syncLuggageTrunkBehavior();
                    }
                }
            }
        });

        doorLockRight1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (doorLockRight1Flag == 1) doorLockRight1Flag = 0;
                else if (doorLockRight1Flag == 0) doorLockRight1Flag = 1;

                if (doorLockRight1Flag == 0) {
                    doorLockRight1.setSelected(false);
                    if (doorLockFlag == 1) {
                        doorLockFlag = 0;
                        syncDoorLockBehavior();
                        //TODO:需判断后车厢是否是开着的
                        luggageTrunkFlag = 0;
                        syncLuggageTrunkBehavior();
                    }
                } else if (doorLockRight1Flag == 1) {
                    doorLockRight1.setSelected(true);
                    if (doorLockLeft1Flag == 1 && doorLockLeft2Flag == 1 && doorLockRight2Flag == 1 && doorLockFlag == 0) {
                        doorLockFlag = 1;
                        syncDoorLockBehavior();
                        //TODO:需判断后车厢是否是开着的
                        luggageTrunkFlag = -1;
                        syncLuggageTrunkBehavior();
                    }
                }
            }
        });

        doorLockRight2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (doorLockRight2Flag == 1) doorLockRight2Flag = 0;
                else if (doorLockRight2Flag == 0) doorLockRight2Flag = 1;

                if (doorLockRight2Flag == 0) {
                    doorLockRight2.setSelected(false);
                    if (doorLockFlag == 1) {
                        doorLockFlag = 0;
                        syncDoorLockBehavior();
                        //TODO:需判断后车厢是否是开着的
                        luggageTrunkFlag = 0;
                        syncLuggageTrunkBehavior();
                    }
                } else if (doorLockRight2Flag == 1) {
                    doorLockRight2.setSelected(true);
                    if (doorLockLeft1Flag == 1 && doorLockLeft2Flag == 1 && doorLockRight1Flag == 1 && doorLockFlag == 0) {
                        doorLockFlag = 1;
                        syncDoorLockBehavior();
                        //TODO:需判断后车厢是否是开着的
                        luggageTrunkFlag = -1;
                        syncLuggageTrunkBehavior();
                    }
                }
            }
        });

        luggageFrunk.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                luggageFrunkFlag = !luggageFrunkFlag;
                if (luggageFrunkFlag) luggageFrunk.setSelected(true);
                else luggageFrunk.setSelected(false);
            }
        });

        luggageDormer.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                luggageDormerFlag = !luggageDormerFlag;
                if (luggageDormerFlag) luggageDormer.setSelected(true);
                else luggageDormer.setSelected(false);
            }
        });

        luggageTrunk.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (luggageTrunkFlag == 1) {
                    luggageTrunkFlag = 0;
                    syncLuggageTrunkBehavior();
                } else if (luggageTrunkFlag == 0) {
                    luggageTrunkFlag = 1;
                    syncLuggageTrunkBehavior();
                } else {
                    Toast.makeText(mContext, "Unlock before you open the door or the trunk", Toast.LENGTH_LONG).show();
                }
            }
        });

        leaveMode.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                leaveModeFlag = !leaveModeFlag;
                if (leaveModeFlag) leaveMode.setSelected(true);
                else leaveMode.setSelected(false);
            }
        });

        windowLock.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                windowLockFlag = !windowLockFlag;
                if (windowLockFlag) {
                    windowLock.setSelected(true);
                } else {
                    windowLock.setSelected(false);
                }
                syncWindowAdjustBehavior();
            }
        });

        doorLock.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
               
                if (doorLockFlag == 1) {
                    doorLockFlag = 0;
                    syncDoorLockBehavior();
                } else if (doorLockFlag == 0) {
                    doorLockFlag = 1;
                    syncDoorLockBehavior();
                } else {
                    Toast.makeText(mContext, "Make sure all doors are closed before locking", Toast.LENGTH_LONG).show();
                }

                if (doorLockFlag == 0) {
                    doorLockLeft1Flag = 0;
                    doorLockLeft2Flag = 0;
                    doorLockRight1Flag = 0;
                    doorLockRight2Flag = 0;
                    syncDoorLockSeparateBehavior(1);
                    syncDoorLockSeparateBehavior(2);
                    syncDoorLockSeparateBehavior(3);
                    syncDoorLockSeparateBehavior(4);

                    //TODO:需判断后车厢是否是开着的
                    luggageTrunkFlag = 0;
                    syncLuggageTrunkBehavior();
                } else if (doorLockFlag == 1) {
                    doorLockLeft1Flag = 1;
                    doorLockLeft2Flag = 1;
                    doorLockRight1Flag = 1;
                    doorLockRight2Flag = 1;
                    syncDoorLockSeparateBehavior(1);
                    syncDoorLockSeparateBehavior(2);
                    syncDoorLockSeparateBehavior(3);
                    syncDoorLockSeparateBehavior(4);

                    //TODO:需判断后车厢是否是开着的
                    luggageTrunkFlag = -1;
                    syncLuggageTrunkBehavior();
                }
            }
        });

        sunCurtain.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                sunCurtainFlag = !sunCurtainFlag;
                if (sunCurtainFlag) sunCurtain.setSelected(true);
                else sunCurtain.setSelected(false);
            }
        });

        closeScreen.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                closeScreenFlag = !closeScreenFlag;
                if (closeScreenFlag) closeScreen.setSelected(true);
                else closeScreen.setSelected(false);
            }
        });

        offLight.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {
                    if (isChecked) {
                        frontFogLight.setEnabled(false);
                        rearFogLight.setEnabled(false);
                        frontFogLightFlag = -1;
                        rearFogLightFlag = -1;
                    } else {
                        frontFogLight.setEnabled(true);
                        frontFogLight.setSelected(false);
                        rearFogLight.setEnabled(true);
                        rearFogLight.setSelected(false);
                        frontFogLightFlag = 0;
                        rearFogLightFlag = 0;
                    }
                });


    }

    private void syncDoorLockSeparateBehavior(int whichDoor) { //左前门：1；左后门：2；右前门：3；右后门：4
        switch (whichDoor) {
            case 1:
                if (doorLockLeft1Flag == -1) {
                    doorLockLeft1.setEnabled(false);
                } else if (doorLockLeft1Flag == 0) {
                    doorLockLeft1.setEnabled(true);
                    doorLockLeft1.setSelected(false);
                } else if (doorLockLeft1Flag == 1) {
                    doorLockLeft1.setEnabled(true);
                    doorLockLeft1.setSelected(true);
                }
                break;
            case 2:
                if (doorLockLeft2Flag == -1) {
                    doorLockLeft2.setEnabled(false);
                } else if (doorLockLeft2Flag == 0) {
                    doorLockLeft2.setEnabled(true);
                    doorLockLeft2.setSelected(false);
                } else if (doorLockLeft2Flag == 1) {
                    doorLockLeft2.setEnabled(true);
                    doorLockLeft2.setSelected(true);
                }
                break;
            case 3:
                if (doorLockRight1Flag == -1) {
                    doorLockRight1.setEnabled(false);
                } else if (doorLockRight1Flag == 0) {
                    doorLockRight1.setEnabled(true);
                    doorLockRight1.setSelected(false);
                } else if (doorLockRight1Flag == 1) {
                    doorLockRight1.setEnabled(true);
                    doorLockRight1.setSelected(true);
                }
                break;
            case 4:
                if (doorLockRight2Flag == -1) {
                    doorLockRight2.setEnabled(false);
                } else if (doorLockRight2Flag == 0) {
                    doorLockRight2.setEnabled(true);
                    doorLockRight2.setSelected(false);
                } else if (doorLockRight2Flag == 1) {
                    doorLockRight2.setEnabled(true);
                    doorLockRight2.setSelected(true);
                }
                break;
            default:
                break;

        }

    }

    private void syncDoorLockBehavior() {
        if (doorLockFlag == -1) {
            doorLock.setActivated(false);
        } else if (doorLockFlag == 0) {
            doorLock.setActivated(true);
            doorLock.setSelected(false);
        } else if (doorLockFlag == 1) {
            doorLock.setActivated(true);
            doorLock.setSelected(true);
        }
    }

    private void syncLuggageTrunkBehavior() {
        if (luggageTrunkFlag == -1) {
            //luggageTrunk.setEnabled(false);
            luggageTrunk.setActivated(false);
        } else if (luggageTrunkFlag == 0) {
            //luggageTrunk.setEnabled(true);
            luggageTrunk.setActivated(true);
            luggageTrunk.setSelected(false);
        } else if (luggageTrunkFlag == 1) {
            //luggageTrunk.setEnabled(true);
            luggageTrunk.setActivated(true);
            luggageTrunk.setSelected(true);
        }
    }

    private void syncWindowAdjustBehavior() {
        if (windowLockFlag) {
            windowFrontLeftUp.setEnabled(false);
            windowFrontLeftDown.setEnabled(false);
            windowRearLeftUp.setEnabled(false);
            windowRearLeftDown.setEnabled(false);
            windowFrontRightUp.setEnabled(false);
            windowFrontRightDown.setEnabled(false);
            windowRearRightUp.setEnabled(false);
            windowRearRightDown.setEnabled(false);
        } else {
            windowFrontLeftUp.setEnabled(true);
            windowFrontLeftDown.setEnabled(true);
            windowRearLeftUp.setEnabled(true);
            windowRearLeftDown.setEnabled(true);
            windowFrontRightUp.setEnabled(true);
            windowFrontRightDown.setEnabled(true);
            windowRearRightUp.setEnabled(true);
            windowRearRightDown.setEnabled(true);
        }
    }

}
