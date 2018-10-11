package com.projekttest.gospodin.bluetooth_remote_car.accelerometer;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

import java.io.IOException;
import java.util.Locale;

public class Accelerometer extends Accelerometer_Activity implements SensorEventListener{

    private NativeBluetoothSocket _btSocket;
    private SensorManager _sManager;
    private Sensor _accMeter;
    private Context _context;
    private TextView txtX, txtY, txtZ;
    private double x;
    private double y;
    private double z;

    public Accelerometer (Context context, TextView txtx, TextView txty, TextView txtz) {
        this._context = context;
        this.txtX = txtx;
        this.txtY = txty;
        this.txtZ = txtz;
        _sManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        _accMeter = _sManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }


    public void set_btSocket(NativeBluetoothSocket _btSocket) {
        this._btSocket = _btSocket;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        WindowManager windowMgr = (WindowManager) _context.getSystemService(Context.WINDOW_SERVICE);
        int rotationIndex = windowMgr.getDefaultDisplay().getRotation();

        /**0 - landscape desno | 1 - portrait normalno | 2 - landscape levo | 3 - portrait naopacke*/

        if(_btSocket != null){
            if (rotationIndex == 0 || rotationIndex == 2) {
                x = event.values[1];
                y = event.values[0];
                z = event.values[2];
            } else {
                x = -event.values[0];
                y = -event.values[1];
                z = event.values[2];
            }

            ((Accelerometer_Activity) _context).runOnUiThread(new Runnable(){
                @Override
                public void run(){

                    txtX.setText(String.format(Locale.getDefault(),"x = %s - [%.4f]",(int)Math.round(x), x));
                    txtY.setText(String.format(Locale.getDefault(),"y = %s - [%.4f]",(int)Math.round(y), y));
                    txtZ.setText(String.format(Locale.getDefault(),"z = %s - [%.4f]",(int)Math.round(z), z));
                }
            });

            sendEventValues();
        }
        else{
            //_unregisterListener();
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void _registerListener(){
        if(_sManager == null){
            _sManager = (SensorManager) _context.getSystemService(Context.SENSOR_SERVICE);
            _accMeter = _sManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
        _sManager.registerListener(this, _accMeter, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void _unregisterListener(){
        _sManager.unregisterListener(this, _accMeter);
        _sManager = null;
    }

    private void sendCmd(String cmd) {
        if(_btSocket != null) {
            try {
                _btSocket.getOutputStream().write(cmd.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendEventValues() {
        if(_btSocket != null) {
            int xx = (int) Math.round(x);
            int yy = (int) Math.round(y);

            char cmd = encode(xx, yy);


            sendCmd("<" + cmd + ">");

            Log.d("Data", xx + "\t" + yy + "\tcmd: " + cmd);
            try {
                Thread.sleep(50);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private char encode(int x, int y){
        char cmd = '\0';
        /**
         *   f po y -
         *   b po y +
         *   l po x +
         *   r po x -
         */

        /******************stop*******************/
        if(x >= -2 && y <= 2){
            cmd = 's';
        }


        /******************fw*******************/
        if((x >= 3 && x <= 5) && (y <= 3 && y >= -3)){
            cmd = 'q';
        }
        if((x >= 5 && x <= 7) && (y <= 3 && y >= -3)){
            cmd = 'w';
        }
        if((x >= 7 && x <= 10) && (y <= 3 && y >= -3)){
            cmd = 'e';
        }


        /******************back*******************/
        if((x <= -3 && x >= -5) && (y <= 3 && y >= -3)){
            cmd = 'r';
        }
        if((x <= -5 && x >= -7) && (y <= 3 && y >= -3)){
            cmd = 't';
        }
        if((x <= -7 && x >= -10) && (y <= 3 && y >= -3)){
            cmd = 'z';
        }


        /******************left*******************/
        if((y >= 3 && y <= 10) && (x <= 3 && x >= -3)){
            cmd = 'a';
        }


        /******************right*******************/
        if((y <= -3 && y >= -10) && (x <= 3 && x >= -3)){
            cmd = 'd';
        }


        /******************fw+l*******************/
        if((y >= 2 && y >= 5) && (x >= 2 && x <= 4)){
            cmd = 'f';
        }
        if((y >= 2 && y >= 5) && (x >= 4 && x <= 6)){
            cmd = 'g';
        }
        if((y >= 2 && y >= 5) && (x >= 6 && x <= 8)){
            cmd = 'h';
        }


        /******************fw+r*******************/
        if((y <= -2 && y >= -5) && (x >= 3 && x <= 5)){
            cmd = 'j';
        }
        if((y <= -2 && y >= -5) && (x >= 5 && x <= 7)){
            cmd = 'k';
        }
        if((y <= -2 && y >= -5) && (x >= 7 && x <= 10)){
            cmd = 'l';
        }


        /******************b+l*******************/
        if((y >= 2 && y <= 5) && (x <= -2 && x >= -4)){
            cmd = 'y';
        }
        if((y >= 2 && y <= 5) && (x <= -4 && x >= -6)){
            cmd = 'x';
        }
        if((y >= 2 && y <= 5) && (x <= -6 && x >= -8)){
            cmd = 'c';
        }


        /******************b+r*******************/
        if((y <= -2 && y >= -5) && (x <= -3 && x >= -5)){
            cmd = 'v';
        }
        if((y <= -2 && y >= -5) && (x <= -5 && x >= -7)){
            cmd = 'b';
        }
        if((y <= -2 && y >= -5) && (x <= -7 && x >= -10)){
            cmd = 'n';
        }

        return cmd;
    }
}


