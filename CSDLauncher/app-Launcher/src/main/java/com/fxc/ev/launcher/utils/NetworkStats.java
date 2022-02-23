/*
 * Copyright (C) 2019 TomTom NV. All rights reserved.
 *
 * This software is the proprietary copyright of TomTom NV and its subsidiaries and may be
 * used for internal evaluation purposes or commercial use strictly subject to separate
 * license agreement between you and TomTom NV. If you are the licensee, you are only permitted
 * to use this software in accordance with the terms of your license agreement. If you are
 * not the licensee, you are not authorized to use this software in any manner and should
 * immediately return or destroy it.
 */

package com.fxc.ev.launcher.utils;

import android.content.Context;
import android.net.TrafficStats;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Collects the network statistics from the platform along with aggregating all of the analytic
 * events related to data consumption for comparative analysis.
 *
 * <p>Users of this component should invoke NetworkStats#sharedInstance().init(Context) to setup and
 * then invoke NetworkStats#processEvent(String, Map<String, String>) for each analytic event.
 *
 * <p>A snapshot of the statistics can be acquired using NetworkStats#getSnapshot() and observed by
 * implementing NetworkStats.Listener and registering via NetworkStats#setListener.
 */
public class NetworkStats {

  public interface Listener {
    void OnNetworkStatsUpdated(Snapshot snapshot);
  }

  public class Statistic {
    public final String name;
    public int count = 0;
    public long total = 0L;

    public Statistic(String name) {
      this.name = name;
    }

    public Statistic clone() {
      Statistic es = new Statistic(name);
      es.count = count;
      es.total = total;
      return es;
    }
  };

  public class Snapshot {
    public long rx = 0L;
    public long tx = 0L;
    public final List<Statistic> statistics = new ArrayList<>();

    public Snapshot clone() {
      Snapshot cloned = new Snapshot();
      cloned.rx = rx;
      cloned.tx = tx;
      for (Statistic statistic : statistics) {
        cloned.statistics.add(statistic.clone());
      }
      return cloned;
    }
  }

  private static class SingletonHolder {
    static final NetworkStats instance = new NetworkStats();
  }

  public static NetworkStats sharedInstance() {
    return SingletonHolder.instance;
  }

  private Context context = null;
  private int uid = -1;
  private final Snapshot snapshot = new Snapshot();
  private final Handler handler = new Handler(Looper.getMainLooper());
  private Listener listener = null;
  private final Runnable runnable =
      new Runnable() {
        @Override
        public void run() {
          if (listener != null) {
            listener.OnNetworkStatsUpdated(getSnapshot());
          }
          handler.postDelayed(this, 1000L); // update network once per second
        }
      };
  private final Map<String, Statistic> events = new HashMap<>();
  private static final long UNKNOWN_RXTX = -1L;
  private long initialRx = UNKNOWN_RXTX;
  private long initialTx = UNKNOWN_RXTX;

  private NetworkStats() {}

  public void init(Context context) {
    uid = context.getApplicationInfo().uid;

    updateNetworkTrafficSnapshot();

    events.clear();
    snapshot.statistics.clear();
    addEvent("mapdisplay_tile_received", "Map Display");
    addEvent("mapdisplay_traffic_tile_received", "Traffic");
    addEvent("navigation_tile_received", "Navigation");
    addEvent("trip_plan_received", "Routing");
  }

  private void addEvent(String event, String name) {
    final Statistic statistic = new Statistic(name);
    snapshot.statistics.add(statistic);
    events.put(event, statistic);
  }

  public void setListener(Listener listener) {
    this.listener = listener;
  }

  public Snapshot getSnapshot() {
    // We should make this thread safe but as it isn't safety critical this can be left to
    // some future improvement
    return snapshot.clone();
  }

  private void updateNetworkTrafficSnapshot() {
    final long rx = TrafficStats.getUidRxBytes(uid);
    final long tx = TrafficStats.getUidTxBytes(uid);

    if (initialRx == UNKNOWN_RXTX) {
      initialRx = rx;
      initialTx = tx;
    }

    snapshot.rx = rx - initialRx;
    snapshot.tx = tx - initialTx;
  }

  public void processEvent(String key, Map<String, String> attributes) {
    updateNetworkTrafficSnapshot();

    final Statistic es = events.get(key);
    if (es == null) {
      Log.e("NetworkStats", "No stats for " + key);
      return;
    }
    if (!attributes.containsKey("number_of_bytes")) {
      Log.e("NetworkStats", "No number_of_bytes attribute for " + key);
      return;
    }
    es.count = es.count + 1;
    es.total = es.total + Integer.parseInt(attributes.get("number_of_bytes"));

    handler.removeCallbacks(runnable);
    handler.post(runnable);
  }
}
