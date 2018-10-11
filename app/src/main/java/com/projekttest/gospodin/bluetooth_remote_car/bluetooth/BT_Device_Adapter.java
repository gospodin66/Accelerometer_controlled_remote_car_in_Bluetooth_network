package com.projekttest.gospodin.bluetooth_remote_car.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.projekttest.gospodin.bluetooth_remote_car.R;
import java.util.ArrayList;

/**
 * Created by gospodin on 30/10/2017.
 */

public class BT_Device_Adapter extends ArrayAdapter<BT_Device>{

    private Context kontekst;
    private TextView _device_name;
    private TextView _mac_addr;
    private static BluetoothAdapter btAdapter;


    public BT_Device_Adapter(Context context, ArrayList <BT_Device> bt_device, BluetoothAdapter btAdapter1) {
        super(context, android.R.layout.simple_list_item_1, bt_device);
        this.btAdapter = btAdapter1;
        this.kontekst = context;
    }


    public View getView(int pozicija, View view, ViewGroup parent){
        BT_Device bt_device = (BT_Device) getItem(pozicija);
        View v = null;

        LayoutInflater mInflater = (LayoutInflater)  kontekst.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        v = mInflater.inflate(R.layout.listview_device, null);

        _mac_addr = (TextView) v.findViewById(R.id.textMAC_Adresa);
        _device_name = (TextView) v.findViewById(R.id.textNazivUredaja);

        _device_name.setText(bt_device.getNaziv_uredaja());
        _mac_addr.setText(bt_device.getMac_adresa());

        return v;
    }
}
