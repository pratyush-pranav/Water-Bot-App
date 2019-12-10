package com.example.bt2ndtrial;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private static String TAG="MainActicity";

    Button btnEnable,btnUp,btnBack,btnRight,btnLeft;

    BluetoothAdapter bluetoothAdapter;
    BluetoothSocket bluetoothSocket;
    int k;
    SendReceive sendReceive;


    static final UUID mUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    public ArrayList<BluetoothDevice> mBTDevices = new ArrayList<>();
    public DeviceListAdapter mDeviceListAdapter;
    ListView lvNewDevices;
    TextView status,receivedMessage;

    static final int STATE_CONNECTING=2;
    static final int STATE_CONNECTED=3;
    static final int STATE_CONNECTION_FAILED=4;
    static final int STATE_MESSAGE_RECEIVED=5;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnEnable=(Button)findViewById(R.id.btnEnable);
        btnEnable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bluetoothEnableDisable();
            }
        });
        btnUp=(Button)findViewById(R.id.btnUp);
        btnBack=(Button)findViewById(R.id.btnBack);
        btnLeft=(Button)findViewById(R.id.btnRight);
        btnRight=(Button)findViewById(R.id.btnLeft);

            btnUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if(!bluetoothAdapter.isEnabled()) {
                            Log.d(TAG,"Do nothing");
                        }
                        else {
                            String string="forward";
                            sendReceive.write(string.getBytes());
                        }
                    }catch (Exception e){
                    }
                }
            });


            btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if(!bluetoothAdapter.isEnabled()) {
                            Log.d(TAG,"Do nothing");
                        }
                        else {
                            String string="backward";
                            sendReceive.write(string.getBytes());
                        }
                    }catch (Exception e){
                    }
                }
            });


            btnLeft.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if(!bluetoothAdapter.isEnabled()) {
                            Log.d(TAG,"Do nothing");
                        }
                        else {
                            String string="left";
                            sendReceive.write(string.getBytes());
                        }
                    }catch (Exception e){
                    }
                }
            });


            btnRight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if(!bluetoothAdapter.isEnabled()) {
                            Log.d(TAG,"Do nothing");
                        }
                        else {
                            String string="right";
                            sendReceive.write(string.getBytes());
                        }
                    }catch (Exception e){
                    }
                }
            });

        lvNewDevices=(ListView)findViewById(R.id.lvNewDevices);
        lvNewDevices.setOnItemClickListener(MainActivity.this);
        status=(TextView)findViewById(R.id.status);
        receivedMessage=(TextView)findViewById(R.id.message);
        bluetoothAdapter= BluetoothAdapter.getDefaultAdapter();
        if(bluetoothAdapter.isEnabled())
        {
            discover();
        }
    }


    public void bluetoothEnableDisable()
    {
        if(bluetoothAdapter==null)
        {
            Log.d(TAG,"Bluetooth not Supported");

        }
        if(!bluetoothAdapter.isEnabled())
        {
            Log.d(TAG, "Disabling BT.");
            Intent intent=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(intent);
            IntentFilter BTIntent= new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(broadcastReceiver1,BTIntent);
        }
        if(bluetoothAdapter.isEnabled()){
            Log.d(TAG, "Disabling BT.");
            bluetoothAdapter.disable();
            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(broadcastReceiver1, BTIntent);
        }
    }
    private final BroadcastReceiver broadcastReceiver1 = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (action.equals(bluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, bluetoothAdapter.ERROR);

                switch(state){
                    case BluetoothAdapter.STATE_OFF:
                        Log.d(TAG, "onReceive: STATE OFF");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d(TAG, "mBroadcastReceiver1: STATE TURNING OFF");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.d(TAG, "mBroadcastReceiver1: STATE ON");
                        discover();
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d(TAG, "mBroadcastReceiver1: STATE TURNING ON");
                        break;
                }
            }
        }
    };

    public void discover()
    {

        Log.d(TAG, "btnDiscover: Looking for unpaired devices.");

        if(bluetoothAdapter.isDiscovering()){
            bluetoothAdapter.cancelDiscovery();
            Log.d(TAG, "btnDiscover: Canceling discovery.");

            //check BT permissions in manifest
            checkBTPermissions();

            bluetoothAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(broadcastReceiver2, discoverDevicesIntent);
        }
        if(!bluetoothAdapter.isDiscovering()){

            //check BT permissions in manifest
            checkBTPermissions();

            bluetoothAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(broadcastReceiver2, discoverDevicesIntent);
        }
    }

    /**
     * This method is required for all devices running API23+
     * Android must programmatically check the permissions for bluetooth. Putting the proper permissions
     * in the manifest is not enough.
     *
     * NOTE: This will only execute on versions > LOLLIPOP because it is not needed otherwise.
     */
    private void checkBTPermissions() {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            if (permissionCheck != 0) {

                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001); //Any number
            }
        }else{
            Log.d(TAG, "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
        //first cancel discovery because its very memory intensive.
        bluetoothAdapter.cancelDiscovery();
        k=i;
        Log.d(TAG, "onItemClick: You Clicked on a device.");
        String deviceName = mBTDevices.get(i).getName();
        String deviceAddress = mBTDevices.get(i).getAddress();

        Log.d(TAG, "onItemClick: deviceName = " + deviceName);
        Log.d(TAG, "onItemClick: deviceAddress = " + deviceAddress);




        if(mBTDevices.get(i).getBondState()!=BluetoothDevice.BOND_BONDED) {
            //create the bond.
            //NOTE: Requires API 17+? I think this is JellyBean
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
                Log.d(TAG, "Trying to pair with " + deviceName);
                mBTDevices.get(i).createBond();
            }
        }
        else
        {
            ConnectThread connectThread=new ConnectThread(mBTDevices.get(i));
            connectThread.start();
            //thread
            /*bluetoothDevice= bluetoothAdapter.getRemoteDevice(deviceAddress);
            try {
                bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(mUUID);
                bluetoothSocket.connect();
                Log.d(TAG,"Here Connected");
            }catch (Exception e) {
                Log.d(TAG,"Still not Connected");
            }*/
        }
    }
    private BroadcastReceiver broadcastReceiver2 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.d(TAG, "onReceive: ACTION FOUND.");
            if (action.equals(BluetoothDevice.ACTION_FOUND)){
                BluetoothDevice device = intent.getParcelableExtra (BluetoothDevice.EXTRA_DEVICE);
                mBTDevices.add(device);
                Log.d(TAG, "onReceive: " + device.getName() + ": " + device.getAddress());
                mDeviceListAdapter = new DeviceListAdapter(context, R.layout.device_adapter_view, mBTDevices);
                lvNewDevices.setAdapter(mDeviceListAdapter);
            }
        }
    };

    Handler handler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what)
            {

                case STATE_CONNECTING:
                    status.setText("Connecting");
                    break;
                case STATE_CONNECTED:
                    status.setText("Connected");
                    break;
                case STATE_CONNECTION_FAILED:
                    status.setText("Connection Failed");
                    break;
                case STATE_MESSAGE_RECEIVED:
                    byte [] readBuffer=(byte[]) msg.obj;
                    String tempMsg=new String(readBuffer,0,msg.arg1);
                    receivedMessage.setText(tempMsg);
            }
            return true;
        }
    });

    private class ConnectThread extends Thread{
        BluetoothDevice device;
        ConnectThread(BluetoothDevice bluetoothDevice){
            device=bluetoothDevice;
            try {
                bluetoothSocket = device.createRfcommSocketToServiceRecord(mUUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            device=bluetoothAdapter.getRemoteDevice(device.getAddress());
            try {
                bluetoothSocket.connect();
                Message message=Message.obtain();
                message.what=STATE_CONNECTED;
                handler.sendMessage(message);

                sendReceive=new SendReceive(bluetoothSocket);
                sendReceive.start();

            }catch (Exception e) {
                e.printStackTrace();

                Message message=Message.obtain();
                message.what=STATE_CONNECTION_FAILED;
                handler.sendMessage(message);
            }
        }
    }
    private class SendReceive extends Thread{
        private final BluetoothSocket bluetoothSocket;
        private final InputStream inputStream;
        private final OutputStream outputStream;

        public SendReceive(BluetoothSocket socket)
        {
            bluetoothSocket=socket;
            InputStream tempIn=null;
            OutputStream tempOut=null;

            try{
                tempIn=bluetoothSocket.getInputStream();
                tempOut=bluetoothSocket.getOutputStream();

            }catch (Exception e)
            {
                e.printStackTrace();
            }
            inputStream=tempIn;
            outputStream=tempOut;

        }
        public void run()
        {
            byte[] buffer= new byte[1024];
            int bytes;
            while (true)
            {
                try {
                    bytes=inputStream.read(buffer);
                    handler.obtainMessage(STATE_MESSAGE_RECEIVED,bytes,-1,buffer).sendToTarget();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void write(byte[] bytes)
        {
            try {
                outputStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
