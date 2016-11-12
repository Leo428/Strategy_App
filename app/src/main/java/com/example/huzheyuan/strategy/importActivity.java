package com.example.huzheyuan.strategy;

import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;

public class importActivity extends AppCompatActivity {
    private SwipeRefreshLayout refreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import);
        ListView fileList = (ListView) findViewById(R.id.fileList);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refreshFile);
    }

    @Override
    protected void onStart(){
        super.onStart();
        /**
         * this is the function for refreshing the database file
         */
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Toast.makeText(importActivity.this,"Refreshing",Toast.LENGTH_SHORT).show();
                //File dataFiles = new File()
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.setRefreshing(false);
                    }
                },2000);
            }
        });
    }
}

