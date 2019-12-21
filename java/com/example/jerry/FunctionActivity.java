package com.example.jerry;

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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

public class FunctionActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    /*
    String string="92";//forward
    String string="79";//down
    String string="65";//right
    String string="87";//left
    String string="26";//Stop
    String string="52";//Dispense
     */
    private static String TAG="FunctionActicity";

    DatabaseReference databaseReference;
    Calendar calendar;
    Button btnBluetooth,btnUp,btnDown,btnRight,btnLeft,btnStop,btnDispense;

    int statusConnection=0;

    BluetoothAdapter bluetoothAdapter;
    BluetoothSocket bluetoothSocket;

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
        setContentView(R.layout.activity_function);

        btnBluetooth=(Button)findViewById(R.id.btnBluetooth);
        btnUp=(Button)findViewById(R.id.btnUp);
        btnDown=(Button)findViewById(R.id.btnDown);
        btnRight=(Button)findViewById(R.id.btnRight);
        btnLeft=(Button)findViewById(R.id.btnLeft);
        btnStop=(Button)findViewById(R.id.btnStop);
        btnDispense=(Button)findViewById(R.id.btnDispense);
        lvNewDevices=(ListView)findViewById(R.id.listNewDevices);
        lvNewDevices.setOnItemClickListener(FunctionActivity.this);
        status=(TextView)findViewById(R.id.status);
        receivedMessage=(TextView)findViewById(R.id.message);
        bluetoothAdapter= BluetoothAdapter.getDefaultAdapter();
        if(bluetoothAdapter.isEnabled())
        {
            discover();
        }
        if(!haveNetwork(FunctionActivity.this))
        {
            Toast.makeText(FunctionActivity.this,"Turn on Network Connection",Toast.LENGTH_LONG).show();
        }
        btnBluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bluetoothEnableDisable();
            }
        });

        btnUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(!bluetoothAdapter.isEnabled()) {
                        Log.d(TAG,"Do nothing");
                    }
                    else {
                        if(statusConnection==1&&haveNetwork(FunctionActivity.this))
                        {
                            String string="92";//forward
                            sendReceive.write(string.getBytes());
                            cloudDataSend("Forward");
                        }
                    }
                }catch (Exception e){
                }
            }
        });
        btnDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(!bluetoothAdapter.isEnabled()) {
                        Log.d(TAG,"Do nothing");
                    }
                    else {
                        if(statusConnection==1&&haveNetwork(FunctionActivity.this))
                        {
                            String string="79";//down
                            sendReceive.write(string.getBytes());
                            cloudDataSend("Backward");
                        }
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
                        if(statusConnection==1&&haveNetwork(FunctionActivity.this))
                        {
                            String string="65";//right
                            sendReceive.write(string.getBytes());
                            cloudDataSend("Right");
                        }
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
                        if(statusConnection==1&&haveNetwork(FunctionActivity.this))
                        {
                            String string="87";//left
                            sendReceive.write(string.getBytes());
                            cloudDataSend("Left");
                        }
                    }
                }catch (Exception e){
                }
            }
        });
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(!bluetoothAdapter.isEnabled()) {
                        Log.d(TAG,"Do nothing");
                    }
                    else {
                        if(statusConnection==1&&haveNetwork(FunctionActivity.this))
                        {
                            String string="26";//Stop
                            sendReceive.write(string.getBytes());
                            cloudDataSend("Stop");
                        }
                    }
                }catch (Exception e){
                }
            }
        });
        btnDispense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(!bluetoothAdapter.isEnabled()) {
                        Log.d(TAG,"Do nothing");
                    }
                    else {
                        if(statusConnection==1&&haveNetwork(FunctionActivity.this))
                        {
                            String string="52";//Dispense
                            sendReceive.write(string.getBytes());
                            cloudDataSend("Water Dispensed");
                        }
                    }
                }catch (Exception e){
                }
            }
        });

    }

    //Network Connectivity
    private boolean haveNetwork(Context context) {
        boolean have_wifi = false;
        boolean have_mobile = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            android.net.NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            android.net.NetworkInfo mobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (mobile != null && mobile.isConnected())
            {
                have_mobile=true;
            }
            else if(wifi != null && wifi.isConnected())
            {
                have_wifi=true;
            }
        }
        return have_mobile||have_wifi;
    }

    public void cloudDataSend(String s)
    {
        calendar=Calendar.getInstance();
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat simpleDateFormat2=new SimpleDateFormat("hh:mm:ss a");
        String time=simpleDateFormat2.format(calendar.getTime());
        String date= simpleDateFormat.format(calendar.getTime());
        databaseReference= FirebaseDatabase.getInstance().getReference(date);
        String id=databaseReference.push().getKey();
        String command=s;
        SendToDatabase sendToDatabase=new SendToDatabase(id,time,command);
        databaseReference.child(time+" "+id).setValue(sendToDatabase);
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
                    statusConnection=0;
                    break;
                case STATE_CONNECTED:
                    status.setText("Connected");
                    statusConnection=1;
                    break;
                case STATE_CONNECTION_FAILED:
                    status.setText("Connection Failed");
                    statusConnection=0;
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
