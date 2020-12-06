package com.example.bluetoothfinder;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    TextView statusTextView;
    Button searchButton;
    ListView listView;
    ArrayList<String> blueToothDevices = new ArrayList<String>();
    ArrayList<String> addresses = new ArrayList<String>();
    ArrayAdapter arrayAdapter;

    BluetoothAdapter blueToothAdapter;

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i("Action", action);

            if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                statusTextView.setText("Done!");
                searchButton.setEnabled(true);
            }
            else if(BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String name = device.getName();
                String address = device.getAddress();
                String rssi = Integer.toString(intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE));
                //Log.i("Device FOUND!", "Name: " + name + " Address: " + address + " RSSI: " + rssi);
                if(!addresses.contains(address)) {
                    addresses.add(address);
                    String deviceString = "";
                    if(name == null || name.equals("")) {
                        deviceString = address + " RSSI: " + rssi + "dBm";
                    } else {
                        deviceString = name + " RSSI: " + rssi + "dBm";
                    }
                    blueToothDevices.add(deviceString);
                    arrayAdapter.notifyDataSetChanged();
                }
            }
        }
    };

    public void searchClicked(View view) {
        //Log.i("Button", "Pressed");
        statusTextView.setText("Searching...");
        searchButton.setEnabled(false);
        blueToothDevices.clear();
        addresses.clear();
        blueToothAdapter.startDiscovery();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        statusTextView = findViewById(R.id.statusTextView);
        searchButton = findViewById(R.id.searchButton);
        listView = findViewById(R.id.listView);
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, blueToothDevices);
        listView.setAdapter(arrayAdapter);

        blueToothAdapter = BluetoothAdapter.getDefaultAdapter();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(broadcastReceiver, intentFilter);
    }
}
