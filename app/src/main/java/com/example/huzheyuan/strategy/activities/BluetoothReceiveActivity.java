package com.example.huzheyuan.strategy.activities;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.huzheyuan.strategy.R;
import com.example.huzheyuan.strategy.bluetoothVEX.ClientThread;

import java.util.ArrayList;
import java.util.Set;

public class BluetoothReceiveActivity extends AppCompatActivity {
    String deviceInfo;
    String address;
    FloatingActionButton fabBT;
    SwipeRefreshLayout refreshBT;
    ListView bTList;
//    ListView discoverList;
//    TextView bTList;
    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    BluetoothDevice bluetoothDevice = null;
    BluetoothSocket bluetoothSocket;
    ArrayList<String> bTDeviceList = new ArrayList<>();
    //ArrayList<String> discoverDeviceList = new ArrayList<>();
    ClientThread clientThread; //Questioning???
    AlertDialog alert = null;
    AlertDialog.Builder builder = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_receive);
        fabBT = (FloatingActionButton) findViewById(R.id.fabBlueTooth);
        refreshBT = (SwipeRefreshLayout) findViewById(R.id.refreshBTDevice);
        bTList = (ListView) findViewById(R.id.blueToothList);
//        discoverList = (ListView) findViewById(R.id.blueToothDiscoverList);
        // 注册用以接收到已搜索到的蓝牙设备的receiver
        IntentFilter bTFound = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(broadcastReceiver, bTFound);
        // 注册搜索完时的receiver
        bTFound = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(broadcastReceiver,bTFound);
    }

    @Override
    protected void onStart(){
        super.onStart();
        fabBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openBT();//open blueTooth
                pairedDevice();
            }
        });
        bTList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(BluetoothReceiveActivity.this,bTDeviceList.get(position),
                        Toast.LENGTH_SHORT).show();
                deviceInfo = bTDeviceList.get(position);
                address = deviceInfo.substring(deviceInfo.indexOf(":") - 2).trim(); //amazing way
                Log.e("Address: " ,address);
                alert = null;
                builder = new AlertDialog.Builder(BluetoothReceiveActivity.this);
                alert = builder.setTitle("Bluetooth Connection")
                        .setMessage("Connect to " + address)
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                alert.cancel();
                            }
                        })
                        .setPositiveButton("Connecting", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(BluetoothReceiveActivity.this
                                        ,"Connecting to " + address
                                        , Toast.LENGTH_SHORT ).show();
                                //testament in process
                                clientThread = new ClientThread
                                        (bluetoothDevice,bluetoothAdapter,address);
                                clientThread.run();
                            }
                        }).create();
                alert.show();
            }
        });

        refreshBT.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
//                pairedDevice();
                scanDevice(bluetoothAdapter.isEnabled());//Scan for the devices
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshBT.setRefreshing(false);
                    }
                },7000); // only scan/ refresh for 7 seconds
            }
        });
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(bluetoothAdapter.isDiscovering()){
            System.out.println(bluetoothAdapter.isDiscovering());
            bluetoothAdapter.cancelDiscovery();
            System.out.println(bluetoothAdapter.isDiscovering());
        }
        this.unregisterReceiver(broadcastReceiver);
    }
    public void openBT(){
        if(bluetoothAdapter == null) { // check for bluetooth function
            Toast.makeText(this,"No Bluetooth Available",Toast.LENGTH_SHORT).show();
        }
        if(!bluetoothAdapter.isEnabled()){ //弹出对话框提示用户打开
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, 0);
//            Intent enabler = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
//            startActivityForResult(enabler,0);

//            Toast.makeText(this,"Scanning",Toast.LENGTH_SHORT).show();
//            bluetoothAdapter.startDiscovery();
//            IntentFilter bTFound = new IntentFilter(BluetoothDevice.ACTION_FOUND);
//            registerReceiver(broadcastReceiver, bTFound);
        }
        else{
//            Intent enabler = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
//            startActivityForResult(enabler,0);
            Toast.makeText(this,"Bluetooth On!",Toast.LENGTH_SHORT).show();
//            bluetoothAdapter.startDiscovery();
//            IntentFilter bTFound = new IntentFilter(BluetoothDevice.ACTION_FOUND);
//            registerReceiver(broadcastReceiver, bTFound);
        }
    }
    public void scanDevice(boolean bTenable){
        if(bTenable){
            if(bluetoothAdapter.isDiscovering()){
                System.out.println(bluetoothAdapter.isDiscovering());
                bluetoothAdapter.cancelDiscovery();
            }
            Toast.makeText(this,"Scanning",Toast.LENGTH_SHORT).show();
            int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
            pairedDevice();
            bluetoothAdapter.startDiscovery();
            System.out.println("DISCOVER " + bluetoothAdapter.isDiscovering());
        }
        else{
            Toast.makeText(this,"Scan Failed", Toast.LENGTH_SHORT).show();
            refreshBT.setRefreshing(false);
        }
    }
    public void pairedDevice() {
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                bTDeviceList.clear();// clear everything before you add new things
                bTDeviceList.add(device.getName() + "\n" + device.getAddress());// add new data
                Log.e("Bond", device.getName() + "\n" + device.getAddress());
                // 显示在TextView上
//                    bTList.append(device.getName() + "\n" + device.getAddress());
                    bTList.setAdapter(new ArrayAdapter<>
                            (BluetoothReceiveActivity.this,android.R.layout.simple_list_item_1,bTDeviceList));
            }
        } else {
            bTDeviceList.clear();
        }
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // 获得已经搜索到的蓝牙设备
            if(action.equals(BluetoothDevice.ACTION_FOUND)){
                bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = bluetoothDevice.getName();
                String deviceMac = bluetoothDevice.getAddress();
                Log.e("Naming", deviceName + " " + deviceMac);
                bTDeviceList.add(deviceName + "\n" + deviceMac);
                Log.e("BT", bluetoothDevice.getName() + "\n" + bluetoothDevice.getAddress());
                // 显示在TextView上
//                bTList.append(bluetoothDevice.getName() + "\n" + bluetoothDevice.getAddress());
                bTList.setAdapter(new ArrayAdapter<>
                        (BluetoothReceiveActivity.this,android.R.layout.simple_list_item_1,bTDeviceList));
            }
        }
    };
}
