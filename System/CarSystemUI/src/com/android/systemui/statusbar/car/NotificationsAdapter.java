package com.android.systemui.statusbar.car;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.fxc.libCanWrapperNDK.IMyAidlInterface2;

class NotificationsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "NotificationsAdapter";
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

    private RadioGroup lightRadio;

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

    private IMyAidlInterface2 iMyAidlInterface2;
    /*private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.d(TAG, "onServiceConnected");
            iMyAidlInterface2 = IMyAidlInterface2.Stub.asInterface(iBinder);
            Log.d(TAG, "onServiceConnected: "+(iMyAidlInterface2==null));
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d(TAG, "onServiceDisconnected");
            iMyAidlInterface2 = null;
        }
    };

    private void startAndConnectService(){
        Intent intent = new Intent();
        intent.setPackage("com.fxc.libCanWrapper");
        intent.setAction("com.fxc.libCanWrapperNDK.MyService");
        mContext.bindService(intent, conn, Context.BIND_AUTO_CREATE);
    }*/


    public NotificationsAdapter(Context context, NotificationClickHandlerFactory mNotificationClickHandlerFactory, IMyAidlInterface2 mIMyAidlInterface2) {
        Log.d(TAG, "NotificationsAdapter start");
        this.mContext = context;
        this.notificationClickHandlerFactory = mNotificationClickHandlerFactory;
        this.iMyAidlInterface2 = mIMyAidlInterface2;
        Log.d(TAG, "NotificationsAdapter start: " + (iMyAidlInterface2 == null));
        //startAndConnectService();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder"); 
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

        lightRadio=itemView.findViewById(R.id.exterior_lights);

        recentApps.add(itemView.findViewById(R.id.recent_app1));
        recentApps.add(itemView.findViewById(R.id.recent_app2));
        recentApps.add(itemView.findViewById(R.id.recent_app3));
        recentApps.add(itemView.findViewById(R.id.recent_app4));
        recentApps.add(itemView.findViewById(R.id.recent_app5));
        recentApps.add(itemView.findViewById(R.id.recent_app6));
        Log.d(TAG, "loadCurrentSetting");
        loadCurrentSetting();

        processRecentTasks();

    }

    private void loadCurrentSetting() {
        if (iMyAidlInterface2 != null) {
            String doorLockDrive = getDoorLockSeparateStatus(1);
            if (doorLockDrive.equals("Locked")) {
                doorLockLeft1Flag = 1;
            } else if (doorLockDrive.equals("Unlocked")) {
                doorLockLeft1Flag = 0;
            } else {
                doorLockLeft1Flag = -1;
            }
            String doorLockRL = getDoorLockSeparateStatus(2);
            if (doorLockRL.equals("Locked")) {
                doorLockLeft2Flag = 1;
            } else if (doorLockRL.equals("Unlocked")) {
                doorLockLeft2Flag = 0;
            } else {
                doorLockLeft2Flag = -1;
            }
            String doorLockPassenger = getDoorLockSeparateStatus(3);
            if (doorLockPassenger.equals("Locked")) {
                doorLockRight1Flag = 1;
            } else if (doorLockPassenger.equals("Unlocked")) {
                doorLockRight1Flag = 0;
            } else {
                doorLockRight1Flag = -1;
            }
            String doorLockRR = getDoorLockSeparateStatus(4);
            if (doorLockRR.equals("Locked")) {
                doorLockRight2Flag = 1;
            } else if (doorLockRR.equals("Unlocked")) {
                doorLockRight2Flag = 0;
            } else {
                doorLockRight2Flag = -1;
            }

            String rearViewMirror = getRearViewMirrorStatus();
            if (rearViewMirror.equals("Opened")) {
                rearviewMirrorFlag = true;
            } else if (rearViewMirror.equals("Closed")) {
                rearviewMirrorFlag = false;
            }

            processHeadLampStatus(); //大灯
        }


        if (doorLockLeft1Flag == 1 && doorLockLeft2Flag == 1 && doorLockRight1Flag == 1 && doorLockRight2Flag == 1) {
            doorLockFlag = 1;
            luggageTrunkFlag = -1;
        } else if (doorLockLeft1Flag == -1 || doorLockLeft2Flag == -1 || doorLockRight1Flag == -1 || doorLockRight2Flag == -1) {
            doorLockFlag = -1;
        } else {
            doorLockFlag = 0;
        }

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
                try {
                    if (rearviewMirrorFlag) {
                        rearviewMirror.setSelected(true);
                        if (iMyAidlInterface2 != null)
                            iMyAidlInterface2.setCanData("TWO,IVI_RearMirrorOpen_Req,Open");
                    } else {
                        rearviewMirror.setSelected(false);
                        if (iMyAidlInterface2 != null)
                            iMyAidlInterface2.setCanData("TWO,IVI_RearMirrorOpen_Req,Close");
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

        doorLockLeft1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
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
                    if (iMyAidlInterface2 != null) {
                        try {
                            iMyAidlInterface2.setCanData("ONE,IVI_WindowPowerDrive_Req,Disable");
                            iMyAidlInterface2.setCanData("ONE,IVI_WindowPowerRL_Req,Disable");
                            iMyAidlInterface2.setCanData("ONE,IVI_WindowPowerPassenger_Req,Disable");
                            iMyAidlInterface2.setCanData("ONE,IVI_WindowPowerRR_Req,Disable");
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }

                } else {
                    windowLock.setSelected(false);
                    if (iMyAidlInterface2 != null) {
                        try {
                            iMyAidlInterface2.setCanData("ONE,IVI_WindowPowerDrive_Req,Enable");
                            iMyAidlInterface2.setCanData("ONE,IVI_WindowPowerRL_Req,Enable");
                            iMyAidlInterface2.setCanData("ONE,IVI_WindowPowerPassenger_Req,Enable");
                            iMyAidlInterface2.setCanData("ONE,IVI_WindowPowerRR_Req,Enable");
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                }
                syncWindowAdjustBehavior();
            }
        });

        windowFrontLeftUp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (iMyAidlInterface2 != null) {
                    try {
                        iMyAidlInterface2.setCanData("ONE,IVI_WindowDrive_Req,Close");
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        windowFrontLeftDown.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (iMyAidlInterface2 != null) {
                    try {
                        iMyAidlInterface2.setCanData("ONE,IVI_WindowDrive_Req,Open");
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        windowRearLeftUp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (iMyAidlInterface2 != null) {
                    try {
                        iMyAidlInterface2.setCanData("ONE,IVI_WindowRL_Req,Close");
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        windowRearLeftDown.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (iMyAidlInterface2 != null) {
                    try {
                        iMyAidlInterface2.setCanData("ONE,IVI_WindowRL_Req,Open");
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        windowFrontRightUp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (iMyAidlInterface2 != null) {
                    try {
                        iMyAidlInterface2.setCanData("ONE,IVI_WindowPassenger_Req,Close");
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        windowFrontRightDown.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (iMyAidlInterface2 != null) {
                    try {
                        iMyAidlInterface2.setCanData("ONE,IVI_WindowPassenger_Req,Open");
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        windowRearRightUp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (iMyAidlInterface2 != null) {
                    try {
                        iMyAidlInterface2.setCanData("ONE,IVI_WindowRR_Req,Close");
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        windowRearRightDown.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (iMyAidlInterface2 != null) {
                    try {
                        iMyAidlInterface2.setCanData("ONE,IVI_WindowRR_Req,Open");
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
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

                    //TODO:需判断后车厢是否是开着的
                    luggageTrunkFlag = 0;
                    syncLuggageTrunkBehavior();
                } else if (doorLockFlag == 1) {
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

        lightRadio.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.light_radio1:
                    //雾灯相关
                    frontFogLight.setEnabled(true);
                    frontFogLight.setSelected(false);
                    rearFogLight.setEnabled(true);
                    rearFogLight.setSelected(false);
                    frontFogLightFlag = 0;
                    rearFogLightFlag = 0;
                    break;
                case R.id.light_radio2:
                    if (iMyAidlInterface2 != null) {
                        try {
                            iMyAidlInterface2.setCanData("TWO,IVI_HeadLampPower_Req,Open");
                            iMyAidlInterface2.setCanData("TWO,IVI_HighBeamPower_Req,Close");
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                    //雾灯相关
                    frontFogLight.setEnabled(true);
                    frontFogLight.setSelected(false);
                    rearFogLight.setEnabled(true);
                    rearFogLight.setSelected(false);
                    frontFogLightFlag = 0;
                    rearFogLightFlag = 0;
                    break;
                case R.id.light_radio3:
                    if (iMyAidlInterface2 != null) {
                        try {
                            iMyAidlInterface2.setCanData("TWO,IVI_HeadLampPower_Req,Open");
                            iMyAidlInterface2.setCanData("TWO,IVI_HighBeamPower_Req,Open");
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                    //雾灯相关
                    frontFogLight.setEnabled(true);
                    frontFogLight.setSelected(false);
                    rearFogLight.setEnabled(true);
                    rearFogLight.setSelected(false);
                    frontFogLightFlag = 0;
                    rearFogLightFlag = 0;
                    break;
                case R.id.light_radio4:
                    if (iMyAidlInterface2 != null) {
                        try {
                            iMyAidlInterface2.setCanData("TWO,IVI_HeadLampPower_Req,Close");
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                    //雾灯相关
                    frontFogLight.setEnabled(false);
                    rearFogLight.setEnabled(false);
                    frontFogLightFlag = -1;
                    rearFogLightFlag = -1;
                    break;
                default:
                    break;
            }
        });

    }


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
        if (getCanData1.equals("Closed")) {
            ((RadioButton) lightRadio.findViewById(R.id.light_radio4)).setChecked(true);
            frontFogLightFlag = -1;
            rearFogLightFlag = -1;
        } else if (getCanData1.equals("Opened") && getCanData2.equals("Closed")) {
            ((RadioButton) lightRadio.findViewById(R.id.light_radio2)).setChecked(true);
            frontFogLightFlag = 0;
            rearFogLightFlag = 0;
        } else if (getCanData1.equals("Opened") && getCanData2.equals("Opened")) {
            ((RadioButton) lightRadio.findViewById(R.id.light_radio3)).setChecked(true);
            frontFogLightFlag = 0;
            rearFogLightFlag = 0;
        } else {
            ((RadioButton) lightRadio.findViewById(R.id.light_radio1)).setChecked(true);
            frontFogLightFlag = 0;
            rearFogLightFlag = 0;
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

    private String getDoorLockSeparateStatus(int whichDoor) { //左前门：1；左后门：2；右前门：3；右后门：4
        String getCanData = "";
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
            default:
                break;
        }
        Log.d(TAG, "getDoorLockSeparateStatus: " + getCanData);
        return getCanData;
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
