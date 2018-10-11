package com.projekttest.gospodin.bluetooth_remote_car.accelerometer;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.projekttest.gospodin.bluetooth_remote_car.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.UUID;

public class Accelerometer_Activity extends AppCompatActivity {
    private TextView textX;
    private TextView textY;
    private TextView textZ;
    private TextView text_client;
    private ToggleButton btnConn;
    private BluetoothAdapter _btAdapter = null;
    private BluetoothDevice _btDevice = null;
    private NativeBluetoothSocket _btSocket = null;
    private boolean secure = true;
    private static final UUID DEFAULT_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private ProgressDialog dialog;
    private Accelerometer accMeter;

    public TextView getTextX() {
        return textX;
    }

    public TextView getTextY() {
        return textY;
    }

    public TextView getTextZ() {
        return textZ;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accelerometer);

        textX = (TextView) findViewById(R.id.textX);
        textY = (TextView) findViewById(R.id.textY);
        textZ = (TextView) findViewById(R.id.textZ);
        text_client = (TextView) findViewById(R.id.text_remoteName);

        accMeter = new Accelerometer(this, textX, textY, textZ);


        btnConn = (ToggleButton) findViewById(R.id.connBtn);

        String mac_addr = getIntent().getStringExtra("MAC_addr");
        Log.d("MAC",""+mac_addr);

        _btAdapter = BluetoothAdapter.getDefaultAdapter();
        _btDevice = _btAdapter.getRemoteDevice(mac_addr);


        Log.d("BLUETOOTHDEVICE","Uređaj: "+_btDevice+"\tAdapter: "+_btAdapter);
        final ConnectThread thr = new ConnectThread(_btDevice, _btAdapter);
            thr.start();


        btnConn.setChecked(false);
        btnConn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    if (_btSocket != null) {
                        try {
                            _btSocket.close();
                            Log.d("Disconnect", "Disconnected");
                            Toast msg = Toast.makeText(Accelerometer_Activity.this, "Disconnected", Toast.LENGTH_SHORT);
                            msg.setGravity(Gravity.CENTER, 0, 0);
                            msg.show();
                            _btSocket = null;
                            accMeter.set_btSocket(null);

                            text_client.setText(R.string.disconnected);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                else{
                    final ConnectThread thr = new ConnectThread(_btDevice, _btAdapter);
                    if (!thr.isAlive()) {
                        thr.start();
                    }
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(_btSocket != null){
            accMeter._registerListener();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(dialog.isShowing()){
            return;
        }
        if(_btSocket != null) {
            try {
                accMeter._unregisterListener();
                _btSocket.close();
                _btSocket = null;
                accMeter.set_btSocket(null);
                Log.e("Bluetooth Socket","CLOSED");

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onStop(){
        super.onStop();
        finish();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(dialog != null && dialog.isShowing()){
            dialog.cancel();
        }
    }



    private class ConnectThread extends Thread {

        private final BluetoothDevice bTDevice;
        private final BluetoothSocket btSocket;
        private final BluetoothAdapter btAdapter;

        private ConnectThread(BluetoothDevice bTDevice1, BluetoothAdapter adapter) {
            BluetoothSocket tmp = null;

            bTDevice = bTDevice1;
            btAdapter = adapter;

            try {
                tmp = createBluetoothSocket(bTDevice);
                Log.e("Socket", "Socket's create() Success");

            } catch (IOException e) {
                Log.e("Socket", "Socket's create() method failed", e);
            }
            btSocket = tmp;

        }

        public void run() {

            btAdapter.cancelDiscovery();

            try {
                runOnUiThread(new Runnable() {
                    public void run() {
                         dialog = ProgressDialog.show(Accelerometer_Activity.this, "Connecting to: "+bTDevice.getName(),
                                                      "In progress...", true, false);
                    }
                });
                Thread.sleep(1000);
                    connect();


                // TODO: cancel Connect thread onDestroy
                    if (ConnectThread.currentThread().isInterrupted()) {
                        btSocket.close();
                        return;
                    }


                if(btSocket != null){
                        try {
                            accMeter._registerListener();
                            Log.d("Accelerometer", "Registered");

                        } catch (Exception e) {
                            Log.d("Accelerometer", "Unregistered");
                            e.printStackTrace();
                        }

                }

                dialog.dismiss();
                Log.d("BT_device: ", "Connected");

                runOnUiThread(new Runnable() {
                    public void run() {

                        text_client.setText(String.format("Connected to: %s", bTDevice.getName()));
                        Toast msg = Toast.makeText(Accelerometer_Activity.this, "Connected", Toast.LENGTH_SHORT);/////////////////////////////////////////////////
                        msg.setGravity(Gravity.CENTER, 0, 0);
                        msg.show();
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
                try {
                    btSocket.close();
                    Log.d("BT_Socket: ", "Closed");

                } catch (IOException e1) {
                    Log.d("BT_device", "Could not close the client socket");

                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                Log.d("Thread", "Interrupted");
            }

        }

    }



    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        if(Build.VERSION.SDK_INT >= 10){
            try {
                final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", new Class[] { UUID.class });
                return (BluetoothSocket) m.invoke(device, DEFAULT_UUID);
            } catch (Exception e) {
                Log.e("CREATE_BT_SOCKET", "Could not create Insecure RFComm Connection",e);
            }
        }
        return  device.createRfcommSocketToServiceRecord(DEFAULT_UUID);
    }

    public NativeBluetoothSocket connect() throws IOException {
        boolean success = false;

        while (selectSocket()) {
            _btAdapter.cancelDiscovery();

            try {
                _btSocket.connect();
                accMeter.set_btSocket(_btSocket);
                success = true;
                break;
            } catch (IOException e) {
                /**fallback*/
                try {
                    if(_btSocket != null) {
                        _btSocket = new FallbackBluetoothSocket(_btSocket.getUnderlyingSocket());
                        Thread.sleep(500);
                        _btSocket.connect();
                        accMeter.set_btSocket(_btSocket);
                        success = true;
                        break;
                    }
                    else{
                        break;
                    }
                } catch (FallbackException e1) {
                    Log.w("BT", "Could not initialize FallbackBluetoothSocket classes.", e);
                } catch (InterruptedException e1) {
                    Log.w("BT", e1.getMessage(), e1);
                } catch (IOException e1) {
                    Log.w("BT", "Fallback failed. Cancelling.", e1);

                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast msg = Toast.makeText(Accelerometer_Activity.this, "Connect to remote device failed", Toast.LENGTH_LONG);
                                msg.setGravity(Gravity.CENTER, 0, 0);
                                msg.show();
                                btnConn.setChecked(true);
                                dialog.dismiss();

                            }
                        });
                    break;
                }
            }
        }

        if (!success) {
            throw new IOException("Could not connect to device: "+ _btDevice.getAddress());
        }

        return _btSocket;
    }

    private boolean selectSocket() throws IOException {

        BluetoothSocket tmp;

        Log.i("BT", "Attempting to connect to Protocol: "+ DEFAULT_UUID);
        if (secure) {
            tmp = _btDevice.createRfcommSocketToServiceRecord(DEFAULT_UUID);
        } else {
            tmp = _btDevice.createInsecureRfcommSocketToServiceRecord(DEFAULT_UUID);
        }
        _btSocket = new NativeBluetoothSocket(tmp);

        return true;
    }

    public interface BluetoothSocketWrapper {

        InputStream getInputStream() throws IOException;

        OutputStream getOutputStream() throws IOException;

        String getRemoteDeviceName();

        void connect() throws IOException;

        String getRemoteDeviceAddress();

        void close() throws IOException;

        BluetoothSocket getUnderlyingSocket();

    }

    public class NativeBluetoothSocket implements BluetoothSocketWrapper {

        private BluetoothSocket socket;

        private NativeBluetoothSocket(BluetoothSocket tmp) {
            this.socket = tmp;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return socket.getInputStream();
        }

        @Override
        public OutputStream getOutputStream() throws IOException {
            return socket.getOutputStream();
        }

        @Override
        public String getRemoteDeviceName() {
            return socket.getRemoteDevice().getName();
        }

        @Override
        public void connect() throws IOException {
            socket.connect();
        }

        @Override
        public String getRemoteDeviceAddress() {
            return socket.getRemoteDevice().getAddress();
        }

        @Override
        public void close() throws IOException {
            socket.close();
        }

        @Override
        public BluetoothSocket getUnderlyingSocket() {
            return socket;
        }

    }

    public class FallbackBluetoothSocket extends NativeBluetoothSocket {

        private BluetoothSocket fallbackSocket;

        private FallbackBluetoothSocket(BluetoothSocket tmp) throws FallbackException {
            super(tmp);
            try
            {
                Class<?> _class = tmp.getRemoteDevice().getClass();      // kreira se nova klasa u koju se sprema bluetooth socket
                Class<?>[] paramTypes = new Class<?>[] {Integer.TYPE};
                Method metoda = _class.getMethod("createRfcommSocket", paramTypes);
                Object[] params = new Object[] {Integer.valueOf(1)};
                fallbackSocket = (BluetoothSocket) metoda.invoke(tmp.getRemoteDevice(), params);
            }
            catch (Exception e)
            {
                throw new FallbackException(e);
            }
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return fallbackSocket.getInputStream();
        }

        @Override
        public OutputStream getOutputStream() throws IOException {
            return fallbackSocket.getOutputStream();
        }


        @Override
        public void connect() throws IOException {
            fallbackSocket.connect();
        }


        @Override
        public void close() throws IOException {
            fallbackSocket.close();
        }

    }

    private static class FallbackException extends Exception {

        private static final long serialVersionUID = 1L;

        private FallbackException(Exception e) {

            super(e);
        }

    }

}