package com.example.huzheyuan.strategy;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;

public class ClientThread  extends Thread{
    private  BluetoothSocket bluetoothSocket;
    private BluetoothDevice bluetoothDevice;
    private BluetoothAdapter bluetoothAdapter;
    public ClientThread(BluetoothDevice device, BluetoothAdapter adapter){
        BluetoothSocket tmp = null;
        bluetoothDevice = device;
        bluetoothAdapter = adapter;
        try{
            tmp = device.createRfcommSocketToServiceRecord();
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
