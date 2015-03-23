package unimelb.cis.spatialanalytics.sensorreader.services;

import android.app.Notification;
import android.app.Service;
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
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import unimelb.cis.spatialanalytics.sensorreader.config.ConstantConfig;
import unimelb.cis.spatialanalytics.sensorreader.data.SensorData;
import unimelb.cis.spatialanalytics.sensorreader.io.FileIO;
import unimelb.cis.spatialanalytics.sensorreader.sensors.WifiScanner;

/**
 * Author: Han Li
 * Referenced AndyXue's Code
 * Email: hanl4@student.unimelb.edu.au
 * Date: 04/12/2014
 * <p/>
 * Initialize sensors to collect sensor data
 */
public class SensingService extends Service implements SensorEventListener, LocationListener {
    private static final String TAG = SensingService.class.getName();//for debug logger
    private static final int SCREEN_OFF_RECEIVER_DELAY = 500;

    private SensorManager sensorMgr = null;
    private PowerManager.WakeLock mWakeLock = null;

    private List<Sensor> listSensor = new ArrayList<Sensor>();//data.getListSensor();
    private Map<String,Integer> sensorListAttributes=new HashMap<String,Integer>();

    private static LocationManager locationMgr = null;
    private static Location currentLocation = null;

    private FileIO fileIO=new FileIO();


    /*
     * WIFI
     */
    WifiScanner mwifi;
    WifiScanner.Listener wifiListener;
    public static String uiWifiScan = "None";
    final static String WIFI_NAME = "Wifi";


    /*
     * Register this as a sensor event listener.
     */
    private void registerSensorListener() {
        /*
         * Sensors
		 */

        for (int i = 0; i < listSensor.size(); i++) {
            Sensor sensor = listSensor.get(i);
            sensorMgr.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }


    /*
     * Register sensor listeners
     */
    public void registerListener() {
        registerSensorListener();

		/*
		 * Location services
		 */

        try {

            locationMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            try {

				/* defaulting to our place */
                Location hardFix = new Location("ATL");
                hardFix.setLatitude(0);
                hardFix.setLongitude(0);
                hardFix.setAltitude(0);

                try {


                    String provider = locationMgr.getBestProvider(new Criteria(), true);


                    // Get the initial Current Location
                    Location bestProvider = locationMgr.getLastKnownLocation(provider);
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
					}*/
                    if (bestProvider != null)
                        currentLocation = bestProvider;
                    else

                        currentLocation = hardFix;


                } catch (Exception ex2) {
                    onLocationChanged(hardFix);
                }

                onLocationChanged(currentLocation);

            } catch (Exception ex) {
                ex.printStackTrace();
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
            }
        }


    }


    /*
     * Un-register this as a sensor event listener.
     */
    private void unregisterSensorListener() {

        sensorMgr.unregisterListener(this);


		/*		if(sensorMgr!=null)
		{
			for(int i=0;i<listSensor.size();i++){
				Sensor sensor=listSensor.get(i);
				sensorMgr.unregisterListener(this, sensor);
			}

			sensorMgr = null;
		}*/
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
            }


            try {
                locationMgr.removeUpdates(this);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            locationMgr = null;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }


    public BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "onReceive(" + intent + ")");
			/*
			if (!intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
				return;
			}*/

            if (!intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
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
        float values[] = event.values;

        String sensorType = event.sensor.getName();
 /*       int mode = 0;
        if (sensorType.equals("Step Counter") | sensorType.equals("Step Detector")) {
            mode = 1;
        } else
            mode = 3;*/


        if(!sensorListAttributes.containsKey(sensorType))
            sensorListAttributes.put(sensorType,values.length);

        fileIO.writeSensorDataToFile(sensorType, values, values.length);


    }

    @Override
    public void onCreate() {
        super.onCreate();
        //sensor set up
        initializeSensors();
        PowerManager manager =
                (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = manager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);


        //wifi set up
        setwifi();
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);

        registerReceiver(mReceiver, filter);

        //mainWifi.startScan();
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mReceiver);
        unregisterListener();

        mwifi.release();
        uiWifiScan = "None";

        mWakeLock.release();
        stopForeground(true);
        //store the attributes of sensors
        sensorListAttributes.put(ConstantConfig.KEY_LEAVING_HOME_TIMES,1);
        sensorListAttributes.put(ConstantConfig.KEY_WIFI_FILE_NAME,8);
        sensorListAttributes.put(ConstantConfig.KEY_LOCATION_FILE_NAME,5);
        sensorListAttributes.put(ConstantConfig.KEY_NOTES,0);
        sensorListAttributes.put(ConstantConfig.KEY_PRESS_STOP_BUTTON_TIME,1);

        fileIO.writeSensorAttributesToFile(sensorListAttributes);

        //clear SensorData!
        SensorData.clear();
        //SensorData.isSensorRunning=false;




    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        startForeground(android.os.Process.myPid(), new Notification());
        registerListener();
        mWakeLock.acquire();
        return START_STICKY;
    }

	/*
	 * Initialize WIFI
	 */


    private void setwifi() {
        wifiListener = new WifiScanner.Listener() {

            @Override
            public void onScanError(int error) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onScanComplete(long epochTime, List<ScanResult> scanResults) {

                WifiManager mainWifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                WifiInfo currentWifi = mainWifi.getConnectionInfo();
                fileIO.writeWifiDataToFile(scanResults, currentWifi);

            }
        };
        /////////////////////////////////////////////////////////
        mwifi = new WifiScanner(this.getApplicationContext(), wifiListener);
        mwifi.setPower();
        mwifi.scanPeriodic(10000);
    }




    /*
	 * Initiate Sensor Settings
	 */

    public void initializeSensors() {

        sensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);
        listSensor = sensorMgr.getSensorList(Sensor.TYPE_ALL);
        SensorData.setUpSensorData(listSensor);


    }




    @Override
    public void onLocationChanged(Location location) {
        // TODO Auto-generated method stub

        if (location == null) throw new NullPointerException();
        currentLocation = (location);

        double lat = currentLocation.getAltitude();
        double log = currentLocation.getLongitude();
        double alti = currentLocation.getAltitude();
        double accur = currentLocation.getAccuracy();
        double time = currentLocation.getTime();

        double values[] = {lat, log, alti, accur, time};
        int mode = 5;

        //write location information into file
        fileIO.writeLocationToFile(ConstantConfig.KEY_LOCATION_FILE_NAME, values, mode);

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

    public void initializeSensorsFilter() {

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
