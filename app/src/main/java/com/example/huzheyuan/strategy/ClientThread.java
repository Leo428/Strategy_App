package com.example.huzheyuan.strategy;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

public class ClientThread  extends Thread{
    private  BluetoothSocket bluetoothSocket;
    private BluetoothDevice bluetoothDevice;
    private BluetoothAdapter bluetoothAdapter;
    private UUID MY_UUID = UUID.fromString("c6e108c0-ac95-11e6-9598-0800200c9a66");
    private String address = null;
    private PairTool pairTool;
    public ClientThread(BluetoothDevice device, BluetoothAdapter adapter, String deviceAddress){
        bluetoothSocket = null;
        bluetoothDevice = device;
        bluetoothAdapter = adapter;
        pairTool = new PairTool();
        address = deviceAddress;
    }

    public void run(){
        if(bluetoothAdapter.isDiscovering()){ // cancel discovery before connection
            bluetoothAdapter.cancelDiscovery();
        }
        try {
            if(bluetoothDevice == null){ // 判断是否可以获得
                bluetoothDevice = bluetoothAdapter.getRemoteDevice(address);
                Log.i("connect address: ", address);

                try {
                    pairTool.createBond(bluetoothDevice.getClass(),bluetoothDevice);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if(bluetoothSocket == null && bluetoothDevice != null){ // start connection
                Log.i("State ", "Connecting");
                bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(MY_UUID);
                bluetoothSocket.connect(); // connect
            }
        }catch (IOException connectException){
            try{
                bluetoothSocket.close();
            }catch (IOException e){
                // TODO: handle exception
            }
        }
    }
}
