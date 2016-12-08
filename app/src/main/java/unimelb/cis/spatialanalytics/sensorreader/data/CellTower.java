package unimelb.cis.spatialanalytics.sensorreader.data;

/**
 * Created by hanl4 on 1/06/2016.
 */
public class CellTower {
    private int cid;
    private int rssi;
    private int psc;
    private int networkType;
    private int lac;



    public CellTower(int cid, int rssi, int psc, int networkType, int lac)
    {
        this.cid = cid;
        this.rssi = rssi;
        this.psc = psc;
        this.networkType = networkType;
        this.lac = lac;
    }

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public int getPsc() {
        return psc;
    }

    public void setPsc(int psc) {
        this.psc = psc;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public int getLac() {
        return lac;
    }

    public void setLac(int lac) {
        this.lac = lac;
    }

    public int getNetworkType() {
        return networkType;
    }

    public void setNetworkType(int networkType) {
        this.networkType = networkType;
    }
}
