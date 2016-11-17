package com.example.huzheyuan.strategy;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.util.UUID;

public class ClientThread  extends Thread{
    private  BluetoothSocket bluetoothSocket;
    private BluetoothDevice bluetoothDevice;
    private BluetoothAdapter bluetoothAdapter;
    private UUID MY_UUID = UUID.fromString("c6e108c0-ac95-11e6-9598-0800200c9a66");
    public ClientThread(BluetoothDevice device, BluetoothAdapter adapter){
        BluetoothSocket tmp = null;
        bluetoothDevice = device;
        bluetoothAdapter = adapter;
        try{
            tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
        }catch (IOException e){

        }
        bluetoothSocket = tmp;
    }

    public void run(){
        bluetoothAdapter.cancelDiscovery();
        try {
            bluetoothSocket.connect();
        }catch (IOException connectException){
            try{
                bluetoothSocket.close();
            }catch (IOException e){
            }
        }
    }

}
