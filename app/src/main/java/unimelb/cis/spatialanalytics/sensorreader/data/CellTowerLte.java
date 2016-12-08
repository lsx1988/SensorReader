package unimelb.cis.spatialanalytics.sensorreader.data;

import android.telephony.CellIdentityLte;
import android.telephony.CellSignalStrengthLte;

/**
 * Created by hanl4 on 16/06/2016.
 */
public class CellTowerLte {

    CellIdentityLte  tower = null;
    CellSignalStrengthLte rss = null;
    public CellTowerLte (CellIdentityLte cellIdentityLte, CellSignalStrengthLte signalStrengthLte)
    {
        this.tower = cellIdentityLte;
        this.rss = signalStrengthLte;
    }


    public CellSignalStrengthLte getRss() {
        return rss;
    }

    public void setRss(CellSignalStrengthLte rss) {
        this.rss = rss;
    }

    public CellIdentityLte getTower() {
        return tower;
    }

    public void setTower(CellIdentityLte tower) {
        this.tower = tower;
    }
}
