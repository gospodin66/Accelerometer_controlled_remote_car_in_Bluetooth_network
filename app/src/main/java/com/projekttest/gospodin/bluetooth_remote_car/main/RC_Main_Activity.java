package com.projekttest.gospodin.bluetooth_remote_car.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

import com.projekttest.gospodin.bluetooth_remote_car.bluetooth.BT_Activity;
import com.projekttest.gospodin.bluetooth_remote_car.R;


public class RC_Main_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_rc);

        ImageButton bluetoothButton = (ImageButton) findViewById(R.id.bluetoothButton);
        bluetoothButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                startActivity(new Intent(RC_Main_Activity.this, BT_Activity.class));
            }
        });
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

}