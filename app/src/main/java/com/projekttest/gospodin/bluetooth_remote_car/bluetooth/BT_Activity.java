package com.projekttest.gospodin.bluetooth_remote_car.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.projekttest.gospodin.bluetooth_remote_car.R;

public class BT_Activity extends AppCompatActivity implements  BT_ListView_Fragment.OnFragmentInteractionListener{

    private BluetoothAdapter bt_adapter;
    public static int REQUEST_BLUETOOTH = 1;

    public BT_Activity(){

    }

    private void provjeraKompatibilnosti(){
        bt_adapter = BluetoothAdapter.getDefaultAdapter();

        if (bt_adapter == null) {
            new AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage("No Bluetooth service")
                    .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            System.exit(0);
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }

    protected void ukljucivanje_BT(){

        provjeraKompatibilnosti();

        if (!bt_adapter.isEnabled()) {
            Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBT, REQUEST_BLUETOOTH);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bt);

        ukljucivanje_BT();

        FragmentManager fragmentManager = getSupportFragmentManager();

        BT_ListView_Fragment bt_device_fragment = BT_ListView_Fragment.newInstance(bt_adapter);
        fragmentManager.beginTransaction().replace(R.id.main_layout, bt_device_fragment).commit();


    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
    }
    @Override
    protected void onPause(){
        super.onPause();
    }
    @Override
    protected void onResume(){
        super.onResume();
    }


    public void onFragmentInteraction(String id) {

    }

}
