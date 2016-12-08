package unimelb.cis.spatialanalytics.sensorreader.data;

/**
 * Created by Matt on 5/12/2015.
 */
public class DeviceItem {

    private String deviceName;
    private String address;
    private boolean connected;
    private int rssi;

    public String getDeviceName() {
        return deviceName;
    }

    public boolean getConnected() {
        return connected;
    }

    public String getAddress() {
        return address;
    }

    public int getRssi() {return rssi;}

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public DeviceItem(String name, String address, boolean connected, int rssi){
        if(name == null)
            this.deviceName = "";
        else
        this.deviceName = name;
        if(address == null)
            this.address = "";
        else
        this.address = address;
        this.connected = connected;
        this.rssi = rssi;
    }
}
