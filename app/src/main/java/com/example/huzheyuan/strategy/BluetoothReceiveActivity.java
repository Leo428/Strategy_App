package com.example.huzheyuan.strategy;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class BluetoothReceiveActivity extends AppCompatActivity {
    FloatingActionButton fabBT;
    SwipeRefreshLayout refreshBT;
    ListView bTList;
    BluetoothAdapter bluetoothAdapter;
    BluetoothDevice bluetoothDevice;
    BluetoothSocket bluetoothSocket;
    ArrayList<String> bTDeviceList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_receive);
        fabBT = (FloatingActionButton) findViewById(R.id.fabBlueTooth);
        refreshBT = (SwipeRefreshLayout) findViewById(R.id.refreshBTDevice);
        bTList = (ListView) findViewById(R.id.blueToothList);
        bTDeviceList = new ArrayList<>();
    }
    @Override
    protected void onStart(){
        super.onStart();
        fabBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openBT();//open blueTooth
            }
        });
//        refreshBT.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                scanDevice(bluetoothAdapter.isEnabled());
//            }
//        });
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(bluetoothAdapter != null){
            bluetoothAdapter.cancelDiscovery();
        }
        this.unregisterReceiver(broadcastReceiver);

    }

    public void openBT(){
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(bluetoothAdapter == null) { // check for bluetooth function
            Toast.makeText(this,"No Bluetooth Available",Toast.LENGTH_SHORT).show();
        }
        if(!bluetoothAdapter.isEnabled()){ //弹出对话框提示用户是后打开
//            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            startActivityForResult(enableBluetooth,0);
            Intent enabler = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            startActivityForResult(enabler,0);

            Toast.makeText(this,"Scanning",Toast.LENGTH_SHORT).show();
            bluetoothAdapter.startDiscovery();
            IntentFilter bTFound = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(broadcastReceiver, bTFound);
        }
        else{
            Intent enabler = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            startActivityForResult(enabler,0);

            Toast.makeText(this,"Scanning",Toast.LENGTH_SHORT).show();
            bluetoothAdapter.startDiscovery();
            IntentFilter bTFound = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(broadcastReceiver, bTFound);
        }
    }

//    public void scanDevice(boolean bTenable){
//        if(bTenable){
//            Toast.makeText(this,"Scanning",Toast.LENGTH_SHORT).show();
//            bluetoothAdapter.startDiscovery();
//            IntentFilter bTFound = new IntentFilter(BluetoothDevice.ACTION_FOUND);
//            registerReceiver(broadcastReceiver, bTFound);
//        }
//        else{
//            Toast.makeText(this,"Scan Failed", Toast.LENGTH_SHORT).show();
//        }
//    }

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(bluetoothDevice.ACTION_FOUND.equals(action)){
                bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                bTDeviceList.add(bluetoothDevice.getName() + "\n" + bluetoothDevice.getAddress());
                Log.e("BT", bluetoothDevice.getName() + "\n" + bluetoothDevice.getAddress());
                bTList.setAdapter(new ArrayAdapter<String>
                        (BluetoothReceiveActivity.this,android.R.layout.simple_list_item_1,bTDeviceList));
            }
        }
    };
}
