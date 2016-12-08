package unimelb.cis.spatialanalytics.sensorreader.data;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.wifi.ScanResult;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import unimelb.cis.spatialanalytics.sensorreader.config.ConstantConfig;
import unimelb.cis.spatialanalytics.sensorreader.helps.GenerateFileNames;
import unimelb.cis.spatialanalytics.sensorreader.io.FileIO;


/**
 * Store all the sensor data mainly the files that store the sensor values
 * 1) the files that store sensor data
 * 2) the information/attributes about sensor list
 * 3) others
 * <p/>
 * Please notice that, this class is used when the sensing is running on! After the sensing stopped,
 * all the parameters will be cleared.
 */
public class SensorData {


    private static String TAG="SensorData";
    //public static boolean isStart=false;//indicate the start button
    public static boolean isSensorRunning = false;

    public static boolean hasPressedRecordButton = false;

    public static String dateFolder;

    public static List<Sensor> listSensor = new ArrayList<>();
    public static Map<String, File> hashSensorDataFiles = new ConcurrentHashMap<String, File>();
    public static List<ScanResult> wifiList = new ArrayList<ScanResult>();
    public static Map<String, Integer> sensorListAttributes = new HashMap<String, Integer>();
    public static Map<String, Integer> hashCounts = new ConcurrentHashMap<String, Integer>();


    public static void setUpSensorData(List<Sensor> list) {

//        File myNewFolder = new File(ConstantConfig.getKEY_EXTERNAL_STORAGE_PATH());
//        if (!myNewFolder.exists()) {
//            myNewFolder.mkdir();
//        }

        clear();
        isSensorRunning = true;


     Map<String, Boolean> map = new HashMap<String,Boolean>();
        for(Sensor sensor : list)
        {
            if(!map.containsKey(sensor.getName())) {
                map.put(sensor.getName(), true);
                listSensor.add(sensor);
            }
        }

       // listSensor = list;
        initializeOutputFiles();
    }

    /**
     * general all the files/info for a given sensor by given the name of that sensor
     *
     * @param sensorNames
     */
    private static void configSensorFiles(String sensorNames) {
        if(sensorNames == null)
            return;

        sensorNames = sensorNames.replaceAll("/", "");

        String fileNameSplitSymbol = ConstantConfig.FILE_NAME_SPLIT_SYMBOL;
        String filename = sensorNames + fileNameSplitSymbol + dateFolder + ".txt";
        File file = FileIO.createFile(dateFolder, filename);
        if (file != null)
        {
            hashSensorDataFiles.put(sensorNames, file);
            hashCounts.put(sensorNames, 0);
        }

    }

    private static void initializeOutputFiles() {

        dateFolder = GenerateFileNames.getFileNames();
        String sensorNames;

        if (listSensor != null) {

            for (int i = 0; i < listSensor.size(); i++) {
                sensorNames = listSensor.get(i).getName();

              //  if(listSensor.get(i).getType() != Sensor.TYPE_LIGHT )
                {
                    configSensorFiles(sensorNames);

                }


            }

        }

        //Light
/*        sensorNames = ConstantConfig.KEY_LIGHT_FILE_NAME;
        configSensorFiles(sensorNames);
        */

//CellTower
        sensorNames = ConstantConfig.KEY_CELLTOWRER_FILE_NAME;
        configSensorFiles(sensorNames);

        //Bluetooth
        sensorNames = ConstantConfig.KEY_BLUETOOTH_FILE_NAME;
        configSensorFiles(sensorNames);

        //location
        sensorNames = ConstantConfig.KEY_LOCATION_FILE_NAME;
        configSensorFiles(sensorNames);

        //WIFI
        sensorNames = ConstantConfig.KEY_WIFI_FILE_NAME;
        configSensorFiles(sensorNames);

        //Record leaving home time
        sensorNames = ConstantConfig.KEY_LEAVING_HOME_TIMES;
        configSensorFiles(sensorNames);

        //Notes
        sensorNames = ConstantConfig.KEY_NOTES;
        configSensorFiles(sensorNames);

        //record the time of pressing stop recording button
        sensorNames = ConstantConfig.KEY_PRESS_STOP_BUTTON_TIME;
        configSensorFiles(sensorNames);


        //record the sensor value attributes
        sensorNames = ConstantConfig.KEY_SENSOR_VALUE_ATTRIBUTES;
        configSensorFiles(sensorNames);


    }


    /**
     * clear data only when finished the sensor collection or at the bargaining.
     */

    public static void clear() {
        Log.i(TAG, "clear()");
        listSensor = new ArrayList<>();
        if (hashSensorDataFiles != null)
            hashSensorDataFiles.clear();
        if (wifiList != null)
            wifiList.clear();
        if (sensorListAttributes != null)
            sensorListAttributes.clear();
        if (hashCounts != null)
            hashCounts.clear();

    }


    public static List<String> getAllSensorList(Context context) {

        List<Sensor> sensors;
        List<String> sensorNames = new ArrayList<String>();
        if (context == null)
            return sensorNames;

        if (listSensor == null || listSensor.size() == 0) {
            SensorManager sensorMgr = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
            sensors = sensorMgr.getSensorList(Sensor.TYPE_ALL);
        } else {
            sensors = listSensor;
        }


        for (Sensor sensor : sensors) {

            sensorNames.add(sensor.getName());
        }
        sensorNames.add(ConstantConfig.KEY_LOCATION_FILE_NAME);
        sensorNames.add(ConstantConfig.KEY_WIFI_FILE_NAME);


        return sensorNames;
    }


}
