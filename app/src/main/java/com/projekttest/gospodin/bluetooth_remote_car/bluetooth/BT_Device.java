package com.projekttest.gospodin.bluetooth_remote_car.bluetooth;

/**
 * Created by gospodin on 30/10/2017.
 */

public class BT_Device {

    private String device_name;
    private String mac_addr;
    private boolean connected;

    public String getNaziv_uredaja() {
        return device_name;
    }

    public String getMac_adresa() {
        return mac_addr;
    }

    public boolean isConnected() {
        return connected;
    }

    public BT_Device(String naziv, String mac, boolean _connected){

        this.device_name = naziv;
        this.mac_addr = mac;
        this.connected = _connected;

    }

}
