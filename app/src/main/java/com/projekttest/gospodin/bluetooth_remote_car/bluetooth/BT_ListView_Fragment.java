package com.projekttest.gospodin.bluetooth_remote_car.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ToggleButton;
import com.projekttest.gospodin.bluetooth_remote_car.accelerometer.Accelerometer_Activity;
import com.projekttest.gospodin.bluetooth_remote_car.R;
import java.util.ArrayList;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BT_ListView_Fragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class BT_ListView_Fragment extends Fragment implements AbsListView.OnItemClickListener {

    private static int REQUEST_COARSE_LOCATION = 1;
    private static BluetoothAdapter bt_adapter;
    private OnFragmentInteractionListener mListener;
    private ListView mListView;
    private ArrayAdapter<BT_Device>mAdapter;
    private BT_Device bt_device;
    private ToggleButton btnScan;
    private ArrayList<BT_Device> bt_deviceLista;


    public BT_ListView_Fragment() {
        // Required empty public constructor
    }

    private void doPermissionCheck() {
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_COARSE_LOCATION);
        } else {
            Log.e("PREMISSION", "PERMISSION GRANTED");
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {


        if(REQUEST_COARSE_LOCATION == requestCode){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted by the user on permission dialog
            } else {
                // Permission is not granted by the user on permission dialog
            }
        }
    }

    private final BroadcastReceiver bReciever = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                Log.d("DEVICELIST", "Bluetooth device found\n");

                BluetoothDevice uredaj = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                bt_device = new BT_Device(uredaj.getName(),uredaj.getAddress(),false);

                if (!bt_device.isConnected()){
                    mAdapter.add(bt_device);
                    mAdapter.notifyDataSetChanged();
                }
            }
        }
    };

    public static BT_ListView_Fragment newInstance(BluetoothAdapter adapter) {
        BT_ListView_Fragment fragment = new BT_ListView_Fragment();
        bt_adapter = adapter;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        doPermissionCheck();
        Log.d("DEVICELIST", "Super called for DeviceListFragment onCreate\n");
        bt_deviceLista = new ArrayList<BT_Device>();


        Set<BluetoothDevice> pairedDevices = bt_adapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                BT_Device novi_uredaj= new BT_Device(device.getName(),device.getAddress(),false);
                bt_deviceLista.add(novi_uredaj);
            }
        }

        // If there are no devices, add an item that states so. It will be handled in the view.
        if(bt_deviceLista.size() == 0) {
            bt_deviceLista.add(new BT_Device("No Devices", "", false));
        }


        Log.d("DEVICELIST", "DeviceList populated\n");

        mAdapter = new BT_Device_Adapter(getActivity(), bt_deviceLista, bt_adapter);

        Log.d("DEVICELIST", "Adapter created\n");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_listview_bt_, container, false);
        btnScan = (ToggleButton) v.findViewById(R.id.btnScan);
        mListView = (ListView) v.findViewById(R.id.lista_uredajaView);
        ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);


        btnScan.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                if (isChecked) {
                    mAdapter.clear();
                    getActivity().registerReceiver(bReciever, filter);
                    bt_adapter.startDiscovery();
                } else {
                    getActivity().unregisterReceiver(bReciever);
                    bt_adapter.cancelDiscovery();
                }
            }
        });


        return v;
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d("DEVICELIST", "onItemClick position: " + position +
                " id: " + id + " name: " + bt_deviceLista.get(position).getNaziv_uredaja() + "\n");
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onFragmentInteraction(bt_deviceLista.get(position).getNaziv_uredaja());

            Intent intent = new Intent(getActivity(), Accelerometer_Activity.class);
            intent.putExtra("MAC_addr", bt_deviceLista.get(position).getMac_adresa());

            btnScan.setChecked(false);

            startActivity(intent);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String id);
    }

}
