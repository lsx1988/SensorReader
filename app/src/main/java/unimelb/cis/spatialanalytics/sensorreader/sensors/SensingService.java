package unimelb.cis.spatialanalytics.sensorreader.sensors;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.CellIdentityGsm;
import android.telephony.CellIdentityLte;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import unimelb.cis.spatialanalytics.sensorreader.config.ConstantConfig;
import unimelb.cis.spatialanalytics.sensorreader.config.ForceCloseExceptionHandler;
import unimelb.cis.spatialanalytics.sensorreader.data.CellTowerLte;
import unimelb.cis.spatialanalytics.sensorreader.data.DeviceItem;
import unimelb.cis.spatialanalytics.sensorreader.data.SensorData;
import unimelb.cis.spatialanalytics.sensorreader.io.FileIO;

/**
 * Author: Han Li
 * Referenced https://github.com/andyxue/sensors/blob/master/src/com/example/app/TheService.java
 * Email: hanl4@student.unimelb.edu.au
 * Date: 04/12/2014
 * <p/>
 * Initialize sensors to collect sensor data
 */
public class SensingService extends Service implements SensorEventListener, LocationListener {
    private static final String TAG = SensingService.class.getSimpleName();//for debug logger
    private static final int SCREEN_OFF_RECEIVER_DELAY = 500;

    private SensorManager sensorMgr = null;
    //private PowerManager.WakeLock mWakeLock = null;

    private List<Sensor> listSensor = new ArrayList<Sensor>();//data.getListSensor();


    private static LocationManager locationMgr = null;
    private static Location currentLocation = null;
    private static BluetoothAdapter mBluetoothAdapter = null;

    private ArrayList <DeviceItem> deviceItemList = new ArrayList<>();


    private FileIO fileIO = new FileIO();


    /*
     * WIFI
     */
    WifiScanner mwifi;
    WifiScanner.Listener wifiListener;
    public static String uiWifiScan = "None";
    final static String WIFI_NAME = "Wifi";


    /**
     * Sensor Switcher
     */

/*

//Cellular Signal Only
    private boolean isBluetooth = false;
    private boolean isGPS = false;
    private boolean isCellTower = true;
    private boolean isSensorAll = false;
    private boolean isCellTowerOnly = true;

*/


            //All Sensor preclude cellular data
    private boolean isBluetooth = false;
    private boolean isGPS = true;
    private boolean isCellTower = false;
    private boolean isSensorAll = true;
    private boolean isCellTowerOnly = false;



/*    //All Sensor
    private boolean isBluetooth = false;
    private boolean isGPS = true;
    private boolean isCellTower = true;
    private boolean isSensorAll = true;
    private boolean isCellTowerOnly = false;
    */

    /*
     * Register this as a sensor event listener.
     */
    private void registerSensorListener() {
        /*
         * Sensors
		 */

        for (int i = 0; i < listSensor.size(); i++) {
            Sensor sensor = listSensor.get(i);
/*

           if(sensor.getType()==Sensor.TYPE_ACCELEROMETER
                    || sensor.getType()==Sensor.TYPE_LINEAR_ACCELERATION
                    || sensor.getType()==Sensor.TYPE_ROTATION_VECTOR
                    || sensor.getType()==Sensor.TYPE_GRAVITY
                    || sensor.getType()==Sensor.TYPE_MAGNETIC_FIELD
                    || sensor.getType()==Sensor.TYPE_GYROSCOPE)
                //sensorMgr.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);

               sensorMgr.registerListener(this, sensor, 80000);


            else
                sensorMgr.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);


*/

            sensorMgr.registerListener(this, sensor, /*80000*/  SensorManager.SENSOR_DELAY_NORMAL);


        }
    }


    /*
     * Register sensor listeners
     */
    public void registerListener() {
        registerSensorListener();


        //Updated code for location acquiring

        // Acquire a reference to the system Location Manager

// Register the listener with the Location Manager to receive location updates

        String provider = locationMgr.getBestProvider(new Criteria(), true);

        if(isGPS && locationMgr != null && provider != null)
        {

            locationMgr.requestLocationUpdates(provider /*LocationManager.GPS_PROVIDER*/, 0, 0, this);
        }





/*
//OLD Location Acquirment
		*/
/*
         * Location services
		 *//*


        try {


            try {

				*/
/* defaulting to our place *//*

                Location hardFix = new Location("ATL");
                hardFix.setLatitude(0);
                hardFix.setLongitude(0);
                hardFix.setAltitude(0);

                try {


                    String provider = locationMgr.getBestProvider(new Criteria(), true);


                    // Get the initial Current Location
                    Location bestProvider = locationMgr.getLastKnownLocation(provider);
                    */
/*
                    Location gps = locationMgr.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    Location network = locationMgr.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
					if(gps!=null)
					{
						currentLocation=gps;
					}
					else if (network!=null)
					{
						currentLocation=network;
					}*//*

                    if (bestProvider != null)
                        currentLocation = bestProvider;
                    else

                        currentLocation = hardFix;


                } catch (Exception ex2) {
                    onLocationChanged(hardFix);
                    Log.e(TAG, ex2.toString());

                }

                onLocationChanged(currentLocation);

            } catch (Exception ex) {
                ex.printStackTrace();
                Log.e(TAG, ex.toString());
            }
        } catch (Exception ex1) {
            try {
                unregisterSensorListener();

                if (locationMgr != null) {
                    locationMgr.removeUpdates(this);
                    locationMgr = null;
                }
            } catch (Exception ex2) {
                ex2.printStackTrace();
                Log.e(TAG, ex2.toString());
            }
        }

*/

    }


    /**
     * Get the cellular tower information
     */

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void fetchCellularInfo()
    {



        //Get the cellular information
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

/*

        GsmCellLocation cellLocation = (GsmCellLocation)tm.getCellLocation();
        if(cellLocation != null)
            System.out.println("Cell Info : " + cellLocation.getCid() + "; " + cellLocation.getLac());

        Log.i(TAG, "Phone network type : " + tm.getNetworkType() + "; " + tm.getPhoneType());

        List<NeighboringCellInfo> neighbours = tm.getNeighboringCellInfo();

        int i = 0;
        List<CellTower> cellTowerList = new ArrayList<CellTower>();
        for( NeighboringCellInfo info : neighbours)
        {
            Log.i(TAG, info.getCid() + "; " + info.getNetworkType() + "; " + info.getRssi() + "; " + info.getPsc());

            CellTower cell = new CellTower(info.getCid(),info.getRssi(), info.getPsc(),info.getNetworkType(), info.getLac());
            cellTowerList.add(cell);


        }

*/

        if(tm == null )
            return;

        List<CellInfo> cellInfos = tm.getAllCellInfo();
        List<CellTowerLte>  cellTowerLtes = new ArrayList<CellTowerLte>();

        if(cellInfos == null )
            return;
        //http://www.programcreek.com/java-api-examples/index.php?api=android.telephony.CellInfoGsm
        //http://stackoverflow.com/questions/17618167/get-signalstrength-without-using-phonestatelistener-onsignalstrengthchanged
        for (CellInfo  inputCellInfo : cellInfos) {
            Location location = null;
            if (inputCellInfo instanceof CellInfoGsm) {

                CellInfoGsm gsm = (CellInfoGsm) inputCellInfo;
                if(gsm == null)
                    return;
                CellIdentityGsm id = gsm.getCellIdentity();
                //location = db.query(id.getMcc(), id.getMnc(), id.getCid(), id.getLac());
                CellSignalStrengthGsm rss = gsm.getCellSignalStrength();

                //Log.i(TAG, "Cid->" + id + "; rss->" + rss.getDbm() + "; Cell info->" + id.toString() + "; errorRate->" + rss.toString());




            }
            else if(inputCellInfo instanceof CellInfoCdma)
            {
                Log.i(TAG, "It is CellInfoCdma ");

            } else if (inputCellInfo instanceof CellInfoLte) {
                 CellSignalStrengthLte lte = ((CellInfoLte) inputCellInfo).getCellSignalStrength();
                // do what you need
                //Log.i(TAG, "It is CellInfoLte ");



                CellInfoLte cellInfoLte = (CellInfoLte) inputCellInfo;
                if(cellInfoLte == null)
                    return;

                CellIdentityLte id = cellInfoLte.getCellIdentity();

                //location = db.query(id.getMcc(), id.getMnc(), id.getCid(), id.getLac());
                CellSignalStrengthLte rss = cellInfoLte.getCellSignalStrength();

               // System.out.println("Cid->" + id.getPci() + "; rss->" + rss.getDbm() + "; Cell info->" + id.toString() + "; errorRate->" + rss.toString());

                //Log.i(TAG, "Cell Info : " + id.toString() + "  ;  Signal Info : " + rss.toString());


                if(id == null || rss == null)
                    return;

                CellTowerLte cellTowerLte = new CellTowerLte(id, rss);
                cellTowerLtes.add(cellTowerLte);


            } else {

                Log.e(TAG,"Unknown type of cell signal!");

            }


        }




        fileIO.writeCellTowerLteDataToFile(cellTowerLtes);



    }



/*
    private PhoneStateListener mPhoneStateListener = new PhoneStateListener() {

        @Override
        public void onCellInfoChanged(List<CellInfo> cellInfoList) {
            // This callback method will be called automatically by Android OS
            // Every time a cell info changed (if you are registered)
            // Here, you will receive a cellInfoList....
            // Same list that you will receive in RSSI_values() method that you created
            // You can maybe move your whole code to here....
        }


        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);
           signalStrength.getGsmSignalStrength();


        }

    };*/


    /**
     * Write the location information into the file
     * @param location
     */

    public void makeUseOfNewLocation(Location location) {

        Log.i(TAG, "Recording Location Information...");
        if (location == null)
           // throw new NullPointerException() ;
        {
            Log.e(TAG, "Location is NULL");
            return;
        }
         currentLocation = (location);

        double lat = currentLocation.getAltitude();
        double log = currentLocation.getLongitude();
        double alti = currentLocation.getAltitude();
        double accur = currentLocation.getAccuracy();
        double time = currentLocation.getTime();
        String provider = location.getProvider();



        int mode = 5;

        double values[] = {lat, log, alti, accur,  time};//iCell.get(0)

        //write location information into file
        fileIO.writeLocationToFile(ConstantConfig.KEY_LOCATION_FILE_NAME, values, mode, provider);


    }

    /**
     * un-register sensors
     */
    public void unregisterSensorListener() {
        if (sensorMgr != null)
            sensorMgr.unregisterListener(this);
        else
            Log.e(TAG, "sensorMgr is null!");
    }

    /*
     * Unregister sensor listeners
     */
    public void unregisterListener() {


        try {
            try {

                unregisterSensorListener();


            } catch (Exception ex) {
                ex.printStackTrace();
                Log.e(TAG, ex.toString());
            }


            try {
                if (locationMgr != null)
                    locationMgr.removeUpdates(this);
                else
                    Log.e(TAG, "locationMgr is null");
            } catch (Exception ex) {
                ex.printStackTrace();
                Log.e(TAG, ex.toString());
            }
            //locationMgr = null;
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.e(TAG,ex.toString());
        }

    }


    public BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "onReceive(" + intent.toString() + ")");

            /*
            if (!intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
				return;
			}*/

            /*
            Bluetooth
             */
            String action = intent.getAction();
            Log.i(TAG, "Action is : " + action );

            if(isBluetooth)

            {
                // When discovery finds a device
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    // Get the BluetoothDevice object from the Intent
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    // Add the name and address to an array adapter to show in a ListView

                    int rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);
                    Log.i(TAG, "RSSI Strength is : " + rssi);
                    //Toast.makeText(getApplicationContext(),"  RSSI: " + rssi + "dBm", Toast.LENGTH_SHORT).show();
                    DeviceItem newDevice = new DeviceItem(device.getName(), device.getAddress(), false, rssi);


                    deviceItemList.add(newDevice);
                    return;
                } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                    Log.i(TAG, "Bluetooth scan finished!");
                    fileIO.writeBluetoothDataToFile(deviceItemList);

                    if (!mBluetoothAdapter.isDiscovering()) {
                        Log.i(TAG, "DeviceItemList is : " + deviceItemList.size());
                        deviceItemList.clear();
                        Log.i(TAG, "DeviceItemList is clear : " + deviceItemList.size());

                        mBluetoothAdapter.startDiscovery();
                    }

                    return;
                }

            }

            if (!intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                Log.i(TAG, "Action is Wrong: " + intent.getAction());
                return;
            }

            Runnable runnable = new Runnable() {
                public void run() {
                    Log.i(TAG, "Runnable executing.");
                    unregisterListener();
                    registerListener();
                }
            };

            new Handler().postDelayed(runnable, SCREEN_OFF_RECEIVER_DELAY);
        }
    };

    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void onSensorChanged(SensorEvent event) {

        if(!isCellTowerOnly)

        {
            float values[] = event.values;
            long timestamp = event.timestamp;
            long timeMillions = System.currentTimeMillis();
            String sensorType = event.sensor.getName();
            if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
                System.out.println("Light Sensor is here" + event.values);
                // sensorType = ConstantConfig.KEY_LIGHT_FILE_NAME;
            }

            sensorType = sensorType.replaceAll("/", "");

 /*       int mode = 0;
        if (sensorType.equals("Step Counter") | sensorType.equals("Step Detector")) {
            mode = 1;
        } else
            mode = 3;*/


            if (!SensorData.sensorListAttributes.containsKey(sensorType))
                SensorData.sensorListAttributes.put(sensorType, values.length);

            if (fileIO != null)
                fileIO.writeSensorDataToFile(sensorType, values, values.length, timeMillions, event.timestamp);
            else
                Log.e(TAG, "fileIO=null");


        }

        if(event.sensor.getType() == Sensor.TYPE_PRESSURE && isCellTower)
        {

           fetchCellularInfo();
        }


    }

    @Override
    public void onCreate() {
        super.onCreate();
        Thread.setDefaultUncaughtExceptionHandler(new ForceCloseExceptionHandler(getApplicationContext()));

        Log.i(TAG, "onCreate()");

        //sensor set up
        initializeSensors();
       /* PowerManager manager =
                (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = manager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
*/

        //wifi set up
        if(!isCellTowerOnly)
            setWifi();

        //IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        IntentFilter filter = new IntentFilter();
        if(isBluetooth)
        {
            filter.addAction(BluetoothDevice.ACTION_FOUND);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        }

        filter.addAction(Intent.ACTION_SCREEN_OFF);


        registerReceiver(mReceiver, filter);


        if(isBluetooth) {
            if (!mBluetoothAdapter.isDiscovering()) {
                Log.i(TAG, "Bluetooth start sensing..");
                mBluetoothAdapter.startDiscovery();
            }
        }



        //mainWifi.startScan();
    }

    @Override
    public void onDestroy() {

        Log.i(TAG, "onDestroy");
        unregisterReceiver(mReceiver);
        unregisterListener();

        if (mwifi != null)
            mwifi.release();
        uiWifiScan = "None";

/*        if (mWakeLock != null)
            mWakeLock.release();
        */
        stopForeground(true);
        //store the attributes of sensors

        recordOtherInfo();

        //clear SensorData!
        //SensorData.clear();
        SensorData.isSensorRunning = false;
        //SensorData.isSensorRunning=false;


    }

    /**
     * write some other information after the sensing server was stopped.
     */
    public boolean recordOtherInfo() {

        boolean flag = true;
        /**
         * record the time of stopping sensing service
         */
        File file = SensorData.hashSensorDataFiles.get(ConstantConfig.KEY_PRESS_STOP_BUTTON_TIME);
        if (fileIO != null)
            fileIO.writePressStopButtonTime(file);
        else
            flag = false;


        /**
         * record the attributes of sensor data
         */
        if (SensorData.sensorListAttributes != null) {

            SensorData.sensorListAttributes.put(ConstantConfig.KEY_LEAVING_HOME_TIMES, 1);
            SensorData.sensorListAttributes.put(ConstantConfig.KEY_WIFI_FILE_NAME, 8);
            SensorData.sensorListAttributes.put(ConstantConfig.KEY_LOCATION_FILE_NAME, 6);
            SensorData.sensorListAttributes.put(ConstantConfig.KEY_NOTES, 0);
            SensorData.sensorListAttributes.put(ConstantConfig.KEY_PRESS_STOP_BUTTON_TIME, 1);
            SensorData.sensorListAttributes.put(ConstantConfig.KEY_BLUETOOTH_FILE_NAME,5);
            SensorData.sensorListAttributes.put(ConstantConfig.KEY_CELLTOWRER_FILE_NAME,5);
            if (fileIO != null)

                fileIO.writeSensorAttributesToFile(SensorData.sensorListAttributes);
            else {
                flag = false;
                Log.e(TAG, "fileIO is null");
            }
        } else {
            Log.e(TAG, "SensorData.sensorListAttributes is null");
            flag = false;
        }

        return false;


    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind");
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        Log.i(TAG, "onStartCommand");
        startForeground(android.os.Process.myPid(), new Notification());
        registerListener();
        //mWakeLock.acquire();
        return START_STICKY;
    }

	/*
     * Initialize WIFI
	 */


    private void setWifi() {
        wifiListener = new WifiScanner.Listener() {

            @Override
            public void onScanError(int error) {
                // TODO Auto-generated method stub
                Log.e(TAG, "wifi scan error code : " + error);

            }

            @Override
            public void onScanComplete(long epochTime, List<ScanResult> scanResults) {

                WifiManager mainWifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                WifiInfo currentWifi = mainWifi.getConnectionInfo();
                //stop the server in auto mode if the wifi disconnected

                Log.i(TAG, "WIFI SCANNING RESULT");

                if (fileIO != null)

                    fileIO.writeWifiDataToFile(scanResults, currentWifi);
                else {
                    Log.e(TAG, "fileIO is null!");
                }




                //Start Bluetooth Discovery();
       /*         if(!mBluetoothAdapter.isDiscovering()) {
                    mBluetoothAdapter.startDiscovery();
                }*/

/*                if (SettingConfig.getSensingMode() == ConstantConfig.SENSING_MODE_AUTO
                        && FragmentMainPanel.isSuccessBySystem
                        && currentWifi == null) {

                    Log.e(TAG, "SensingService calling destroy by itself");
                    FragmentMainPanel.onSensingServerResult.onSensingServiceResult();
                }*/


            }
        };
        /////////////////////////////////////////////////////////
        mwifi = new WifiScanner(this.getApplicationContext(), wifiListener);
        mwifi.setPower();
        mwifi.scanPeriodic(ConstantConfig.WiFi_SAMPLE_RATE);
    }




    /*
     * Initiate Sensor Settings
	 */

    public void initializeSensors() {

        locationMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        sensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);


        if(isCellTowerOnly)
        {


            List <Sensor> sensors = sensorMgr.getSensorList(Sensor.TYPE_ALL);
            for(Sensor sensor : sensors) {
                if ( sensor.getType() == Sensor.TYPE_PRESSURE)

                {
                    listSensor.add(sensor);

                }

            }




        }

     else if(isSensorAll )
        {
            listSensor = sensorMgr.getSensorList(Sensor.TYPE_ALL);


        }
        else

        {

            List <Sensor> sensors = sensorMgr.getSensorList(Sensor.TYPE_ALL);
            for(Sensor sensor : sensors) {
                if (sensor.getType() == Sensor.TYPE_ACCELEROMETER
                        || sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION
                        || sensor.getType() == Sensor.TYPE_ROTATION_VECTOR
                        || sensor.getType() == Sensor.TYPE_GRAVITY
                        || sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD
                        || sensor.getType() == Sensor.TYPE_PRESSURE
                        || sensor.getType() == Sensor.TYPE_RELATIVE_HUMIDITY
                        || sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE
                        || sensor.getType() == Sensor.TYPE_ORIENTATION
                        || sensor.getType() == Sensor.TYPE_LIGHT
                        || sensor.getType() == Sensor.TYPE_PROXIMITY
                        || sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED
                        || sensor.getType() == Sensor.TYPE_GYROSCOPE_UNCALIBRATED
                        || sensor.getType() == Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR
                        || sensor.getType() == Sensor.TYPE_GAME_ROTATION_VECTOR
                        || sensor.getType() == Sensor.TYPE_GYROSCOPE)

                {
                    listSensor.add(sensor);

                }

            }

        }





        //Set up Bluetooth
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        /*
        if (mBluetoothAdapter == null) {
            new AlertDialog.Builder(this)
                    .setTitle("Not compatible")
                    .setMessage("Your phone does not support Bluetooth")
                    .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            System.exit(0);
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
*/


        if (mBluetoothAdapter == null) {

            Toast.makeText(getApplicationContext(), "Your phone does not support Bluetooth", Toast.LENGTH_SHORT).show();

        }
        //Toast.makeText(getApplicationContext(),"  RSSI: " + rssi + "dBm", Toast.LENGTH_SHORT).show();

        if (!mBluetoothAdapter.isEnabled())
        {
            mBluetoothAdapter.enable();
        }
        /*
        if (!mBluetoothAdapter.isEnabled()) {
            new AlertDialog.Builder(this)
                    .setTitle("Enable Bluetooth")
                    .setMessage("Please enable Bluetooth first")
                    .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            System.exit(0);
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }

*/



        SensorData.setUpSensorData(listSensor);



    }



    public interface OnSensingServiceResult {
        public void onSensingServiceResult();

    }

    /**
     * Get Paired Bluetooth Devices
     */
    public void getPairedBluetoothDevices()
    {
        Log.i(TAG,"Getting paired bluetooth devices");
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
// If there are paired devices
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
                // Add the name and address to an array adapter to show in a ListView
               Log.i(TAG,device.getName() + "\n" + device.getAddress());

            }
        }

    }

    @Override
    public void onLocationChanged(Location location) {
        // TODO Auto-generated method stub

        makeUseOfNewLocation(location);
      //  fetchCellularInfo();
       // getPairedBluetoothDevices();


        //		writeWifiScans();


    }


    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub

    }


    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub

    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }



    /*
     * Initiate Sensor Settings (NOt Used)
	 */

    public void initializeSensorsFilter22() {

        sensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);
        List<Sensor> sensors = null;
        sensors = sensorMgr.getSensorList(Sensor.TYPE_PRESSURE);
        Sensor sensor = null;

        if (sensors.size() > 0) {
            sensor = sensors.get(0);
            listSensor.add(sensor);

        }

        sensors = sensorMgr.getSensorList(Sensor.TYPE_GRAVITY);

        if (sensors.size() > 0) {
            sensor = sensors.get(0);
            listSensor.add(sensor);
        }

        sensors = sensorMgr.getSensorList(Sensor.TYPE_MAGNETIC_FIELD);

        if (sensors.size() > 0) {
            sensor = sensors.get(0);
            listSensor.add(sensor);
        }

        sensors = sensorMgr.getSensorList(Sensor.TYPE_ACCELEROMETER);

        if (sensors.size() > 0) {
            sensor = sensors.get(0);
            listSensor.add(sensor);
        }

        sensors = sensorMgr.getSensorList(Sensor.TYPE_LINEAR_ACCELERATION);
        if (sensors.size() > 0) {
            sensor = sensors.get(0);
            listSensor.add(sensor);
        }

        sensors = sensorMgr.getSensorList(Sensor.TYPE_GYROSCOPE);

        if (sensors.size() > 0) {
            sensor = sensors.get(0);
            listSensor.add(sensor);

        }

        sensors = sensorMgr.getSensorList(Sensor.TYPE_ORIENTATION);

        if (sensors.size() > 0) {
            sensor = sensors.get(0);
            listSensor.add(sensor);
        }

        sensors = sensorMgr.getSensorList(Sensor.TYPE_AMBIENT_TEMPERATURE);

        if (sensors.size() > 0) {
            sensor = sensors.get(0);
            listSensor.add(sensor);

        }

        sensors = sensorMgr.getSensorList(Sensor.TYPE_RELATIVE_HUMIDITY);

        if (sensors.size() > 0) {
            sensor = sensors.get(0);
            listSensor.add(sensor);
        }


        sensors = sensorMgr.getSensorList(Sensor.TYPE_STEP_COUNTER);

        if (sensors.size() > 0) {
            sensor = sensors.get(0);
            listSensor.add(sensor);
        }

        sensors = sensorMgr.getSensorList(Sensor.TYPE_STEP_DETECTOR);

        if (sensors.size() > 0) {
            sensor = sensors.get(0);
            listSensor.add(sensor);
        }

        SensorData.setUpSensorData(listSensor);


    }


}
