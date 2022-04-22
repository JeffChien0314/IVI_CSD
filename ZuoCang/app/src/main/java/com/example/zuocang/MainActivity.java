package com.example.zuocang;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.zuocang.adapter.MenuListAdapter;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    protected ListView mMenuListView;
    String[] itemName = {"Doors,Windows", "Lights", "Drive Mode", "Seats", "Mirrors", "Steering"};
    private String TAG = "MainActivity";
    private MenuListAdapter mListAdapter;


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
    }
    }
