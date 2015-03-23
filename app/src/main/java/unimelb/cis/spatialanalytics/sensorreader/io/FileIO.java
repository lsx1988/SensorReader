package unimelb.cis.spatialanalytics.sensorreader.io;

import android.content.Context;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import unimelb.cis.spatialanalytics.sensorreader.config.ConstantConfig;
import unimelb.cis.spatialanalytics.sensorreader.config.SettingConfig;
import unimelb.cis.spatialanalytics.sensorreader.data.SensorData;
import unimelb.cis.spatialanalytics.sensorreader.helps.CustomizedTime;

/**
 * Created by hanl4 on 18/03/2015.
 */
public class FileIO {
    private static final String TAG = "FileIO";


    /**
     * read file from local mainly to present leaving home time
     *
     * @param file
     * @return
     */
    public String presentLeavingTime(File file) {
        //Read text from file
        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            int count = 0;
            while ((line = br.readLine()) != null) {
                String s[] = line.split(",");
                String time = CustomizedTime.getMillisToDateTime(Long.valueOf(s[1]));
                String str = String.valueOf(++count) + " : " + time;
                text.append(str);

                text.append('\n');
            }
            br.close();
        } catch (IOException e) {
            //You'll need to add proper error handling here
            Log.e(TAG, e.toString());
        }

        return text.toString();

    }


    /**
     * read file from local
     *
     * @param file
     * @return
     */
    public String readTxt(File file) {
        //Read text from file
        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;


            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        } catch (IOException e) {
            //You'll need to add proper error handling here
            Log.e(TAG, e.toString());
        }

        return text.toString();

    }


    /**
     * read file from disk (originally coded)
     *
     * @param f
     * @return
     */
    public String readDoc(File f) {
        String text = "";
        int read, N = 1024 * 1024;
        char[] buffer = new char[N];

        try {
            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);

            while (true) {
                read = br.read(buffer, 0, N);
                String temp = new String(buffer, 0, read);
                Log.d(TAG, "leaving home time record " + temp);
                text += temp;

                if (read < N) {
                    break;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.e(TAG, ex.toString());
        }

        return text;
    }

    /**
     * store the leaving time into local txt files
     *
     * @param file
     * @param counts
     */
    public void recordCurrentTime(File file, int counts) {


        //mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(event.sensor.getType()), SensorManager.SENSOR_DELAY_NORMAL);

        String newLine = "\n";
        byte[] bnewLine = newLine.getBytes();
        String sSeparator = ",";
        byte[] bSeparator = sSeparator.getBytes();

        String sCount = String.valueOf(counts);
        byte[] bCount = sCount.getBytes();

        String sBeginner = "$";
        byte[] bBeginner = sBeginner.getBytes();
        long currentTimeMills = CustomizedTime.getCurrentTimeMillis();
        String sCurrentTimeMills = String.valueOf(currentTimeMills);
        byte[] bCurrentTimeMills = sCurrentTimeMills.getBytes();

        try {
            OutputStream fo = new FileOutputStream(file, true);

            fo.write(bBeginner);
            fo.write(bCount);
            fo.write(bSeparator);
            fo.write(bCurrentTimeMills);
            fo.write(bnewLine);
            fo.close();
        } catch (IOException e) {
            Log.e(TAG, "File write failed: " + e.toString());
        }


    }


    /**
     * Record the time of pressing stop sensing button
     *
     * @param file
     */
    public void writePressStopButtonTime(File file) {
        String newLine = "\n";
        byte[] bnewLine = newLine.getBytes();


        long currentTimeMills = CustomizedTime.getCurrentTimeMillis();
        String sCurrentTimeMills = String.valueOf(currentTimeMills);
        byte[] bCurrentTimeMills = sCurrentTimeMills.getBytes();

        try {
            OutputStream fo = new FileOutputStream(file, true);

            fo.write(bCurrentTimeMills);
            fo.write(bnewLine);
            fo.close();
        } catch (IOException e) {
            Log.e(TAG, "writePressStopButtonTime failed: " + e.toString());
        }


    }


    /**
     * write notes.
     */
    public void writeNotes(File myData, String data, boolean isSuccess, boolean isSuccessByUser) {
        System.out.println(myData.toString());

        try {
            String flag_success = "System:" + String.valueOf(isSuccess);
            byte[] bflag_success = flag_success.getBytes();
            String flag_success_user = "User:" + String.valueOf(isSuccessByUser);
            byte[] bflag_success_user = flag_success_user.getBytes();

            String comma = "\n";
            byte[] bComma = comma.getBytes();
            byte[] bdata = data.getBytes();

            OutputStream fo = new FileOutputStream(myData, true);
            fo.write(bComma);
            fo.write(bflag_success);
            fo.write(bComma);
            fo.write(bflag_success_user);
            fo.write(bComma);
            fo.write(bdata);
            fo.close();

        } catch (IOException e) {
            Log.e(TAG, "File write failed: " + e.toString());
        }

    }


    /**
     * if file doesn't exist, create new one; if the file exists, delete it first
     *
     * @param folder
     * @param fileName
     */

    public static File createFile(String folder, String fileName) {
        File myNewFolder = new File(SettingConfig.getUserExternalStoragePath()+ "/" + folder + "/");
        if (!myNewFolder.exists()) {
            myNewFolder.mkdir();
        }

        File dataFile = new File(myNewFolder + "/" + fileName);
        try {
            if (!dataFile.exists()) {
                dataFile.createNewFile();
                Log.d(TAG, "creating new file : " + dataFile.getAbsolutePath().toString());
            } else {
                dataFile.delete();
                Log.d(TAG, "deleting existing file : " + dataFile.getAbsolutePath().toString());

            }
        } catch (IOException ioExp) {
            Log.e(TAG, "error in file creation " + folder + " " + fileName + ";" + ioExp.toString());
        }
        return dataFile;

    }


    /**
     * Create new folders
     *
     * @param folder
     * @return
     */

    public static boolean createFolder(File folder) {
        if (!folder.exists()) {
            if (folder.mkdir()) {
                Log.d(TAG, "creating new folder success : " + folder.getAbsolutePath());
                return true;
            } else {
                Log.e(TAG, "creating new folder success : " + folder.getAbsolutePath());

                return false;

            }
        }
        return true;


    }



	/*
     * Write Sensor Data
	 */

    public void writeSensorDataToFile(String sensorsName, float[] values, int mode) {


        //mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(event.sensor.getType()), SensorManager.SENSOR_DELAY_NORMAL);

        String newLine = "\n";
        byte[] bnewLine = newLine.getBytes();
        String sSeparator = ",";
        byte[] bSeparator = sSeparator.getBytes();
        byte[] bSensorName = sensorsName.getBytes();


        int count = SensorData.hashCounts.get(sensorsName) + 1;
        SensorData.hashCounts.put(sensorsName, count);
        String sCount = String.valueOf(count);
        byte[] bCount = sCount.getBytes();


        String sBeginner = "$";
        byte[] bBeginner = sBeginner.getBytes();
        long currentTimeMills = CustomizedTime.getCurrentTimeMillis();
        String sCurrentTimeMills = String.valueOf(currentTimeMills);
        byte[] bCurrentTimeMills = sCurrentTimeMills.getBytes();

        try {
            OutputStream fo = new FileOutputStream(SensorData.hashSensorDataFiles.get(sensorsName), true);
            fo.write(bBeginner);
            fo.write(bCount);
            fo.write(bSeparator);
            fo.write(bCurrentTimeMills);
            fo.write(bnewLine);

            for (int i = 0; i < mode; i++) {
                float float_value = values[i];
                String str_value = String.valueOf(float_value);
                byte[] byte_value = str_value.getBytes();
                fo.write(byte_value);
                if (i < mode - 1)
                    fo.write(bSeparator);

            }


            fo.write(bnewLine);
            fo.close();
        } catch (IOException e) {
            Log.e(TAG, "File write failed: " + e.toString());
        }
    }


	/*
     * Write location information
	 */

    public void writeLocationToFile(String sensorsName, double[] values, int mode) {


        //mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(event.sensor.getType()), SensorManager.SENSOR_DELAY_NORMAL);

        String newLine = "\n";
        byte[] bnewLine = newLine.getBytes();
        String sSeparator = ",";
        byte[] bSeparator = sSeparator.getBytes();
        byte[] bSensorName = sensorsName.getBytes();


        int count = SensorData.hashCounts.get(sensorsName) + 1;
        SensorData.hashCounts.put(sensorsName, count);
        String sCount = String.valueOf(count);
        byte[] bCount = sCount.getBytes();


        String sBeginner = "$";
        byte[] bBeginner = sBeginner.getBytes();
        long currentTimeMills = CustomizedTime.getCurrentTimeMillis();
        String sCurrentTimeMills = String.valueOf(currentTimeMills);
        byte[] bCurrentTimeMills = sCurrentTimeMills.getBytes();

        try {
            OutputStream fo = new FileOutputStream(SensorData.hashSensorDataFiles.get(sensorsName), true);
            fo.write(bBeginner);
            fo.write(bCount);
            fo.write(bSeparator);
            fo.write(bCurrentTimeMills);
            fo.write(bnewLine);

            for (int i = 0; i < mode; i++) {
                double float_value = values[i];
                String str_value = String.valueOf(float_value);
                byte[] byte_value = str_value.getBytes();
                fo.write(byte_value);
                if (i < mode - 1)
                    fo.write(bSeparator);

            }


            fo.write(bnewLine);
            fo.close();
        } catch (IOException e) {
            Log.e(TAG, "File write failed: " + e.toString());
        }
    }



	/*
     * Write WIFI scan information
	 */

    public void writeWifiDataToFile(List<ScanResult> wifiList, WifiInfo currentWifi) {

        Iterator<ScanResult> resultIterator = wifiList.iterator();


        String newLine = "\n";
        byte[] bnewLine = newLine.getBytes();
        String sSeparator = ",";
        byte[] bSeparator = sSeparator.getBytes();


        int count = SensorData.hashCounts.get(ConstantConfig.KEY_WIFI_FILE_NAME) + 1;
        SensorData.hashCounts.put(ConstantConfig.KEY_WIFI_FILE_NAME, count);
        String sCount = String.valueOf(count);
        byte[] bCount = sCount.getBytes();


        String sBeginner = "$";
        byte[] bBeginner = sBeginner.getBytes();
        long currentTimeMills = CustomizedTime.getCurrentTimeMillis();
        String sCurrentTimeMills = String.valueOf(currentTimeMills);
        byte[] bCurrentTimeMills = sCurrentTimeMills.getBytes();


        try {
            if (resultIterator.hasNext()) {

                OutputStream fo = new FileOutputStream(SensorData.hashSensorDataFiles.get(ConstantConfig.KEY_WIFI_FILE_NAME), true);
                fo.write(bBeginner);
                fo.write(bCount);
                fo.write(bSeparator);
                fo.write(bCurrentTimeMills);
                fo.write(bnewLine);

                int counts = 0;

                while (resultIterator.hasNext()) {

                    counts++;

                    String sCounts = String.valueOf(counts);
                    byte[] bCounts = sCounts.getBytes();

                    fo.write(bCounts);
                    fo.write(bSeparator);

                    ScanResult scanResult = resultIterator.next();


                    String SSID = scanResult.SSID;
                    byte[] byte_SSID = SSID.getBytes();
                    fo.write(byte_SSID);
                    fo.write(bSeparator);

                    String BSSID = scanResult.BSSID;
                    byte[] byte_BSSID = BSSID.getBytes();
                    fo.write(byte_BSSID);
                    fo.write(bSeparator);


                    String level = String.valueOf(scanResult.level);
                    byte[] byte_level = level.getBytes();
                    fo.write(byte_level);
                    fo.write(bSeparator);


                    String capabilities = scanResult.capabilities;
                    byte[] byte_capabilities = capabilities.getBytes();
                    fo.write(byte_capabilities);
                    fo.write(bSeparator);


                    /*String timestamp=String.valueOf(scanResult.timestamp);
                    byte[] byte_timestamp= timestamp.getBytes();
                    fo.write(byte_timestamp);
                    fo.write(bSeparator);*/


                    //check if the wifi is the connected one or not; if yes, set to 1; otherwise 0.
                    String flag_connected = String.valueOf(isCurrentConnectedWifi(scanResult, currentWifi));
                    byte[] byte_flag_connected = flag_connected.getBytes();
                    fo.write(byte_flag_connected);


                    fo.write(bnewLine);


                }

                //fo.write(bnewLine);
                fo.close();

            }


        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }
    }


    /**
     * check given wifi access point is the connected one or not
     *
     * @param scanResult
     * @param currentWifi
     * @return
     */
    public boolean isCurrentConnectedWifi(ScanResult scanResult, WifiInfo currentWifi) {

        if (currentWifi != null) {
            if (currentWifi.getSSID() != null) {

                if (currentWifi.getSSID().replace("\"", "").equals(scanResult.SSID))
                    return true;
            }
        }
        return false;
    }


    /**
     * Get connected wifi name
     *
     * @param context
     * @return
     */
    public String getWifiName(Context context) {
        WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (manager.isWifiEnabled()) {
            WifiInfo wifiInfo = manager.getConnectionInfo();
            if (wifiInfo != null) {
                NetworkInfo.DetailedState state = WifiInfo.getDetailedStateOf(wifiInfo.getSupplicantState());
                if (state == NetworkInfo.DetailedState.CONNECTED || state == NetworkInfo.DetailedState.OBTAINING_IPADDR) {
                    return wifiInfo.getSSID();
                }
            }
        }
        return null;
    }

	/*
     * Write leaving home time records
	 */

    public void writeLeavingHomeTimeToFile() {


        //mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(event.sensor.getType()), SensorManager.SENSOR_DELAY_NORMAL);

        String newLine = "\n";
        byte[] bnewLine = newLine.getBytes();
        String sSeparator = ",";
        byte[] bSeparator = sSeparator.getBytes();


        int count = SensorData.hashCounts.get(ConstantConfig.KEY_LEAVING_HOME_TIMES) + 1;
        SensorData.hashCounts.put(ConstantConfig.KEY_LEAVING_HOME_TIMES, count);
        String sCount = String.valueOf(count);
        byte[] bCount = sCount.getBytes();


        String sBeginner = "$";
        byte[] bBeginner = sBeginner.getBytes();
        long currentTimeMills = CustomizedTime.getCurrentTimeMillis();
        String sCurrentTimeMills = String.valueOf(currentTimeMills);
        byte[] bCurrentTimeMills = sCurrentTimeMills.getBytes();

        try {
            OutputStream fo = new FileOutputStream(SensorData.hashSensorDataFiles.get(ConstantConfig.KEY_LEAVING_HOME_TIMES), true);

            fo.write(bBeginner);
            fo.write(bCount);
            fo.write(bSeparator);
            fo.write(bCurrentTimeMills);
            fo.write(bnewLine);


            fo.write(bnewLine);
            fo.close();
        } catch (IOException e) {
            Log.e(TAG, "File write failed: " + e.toString());
        }
    }



    /*
	 * Write sensor data attributes to file
	 */

    public void writeSensorAttributesToFile(Map<String, Integer> sensorAttributes) {

        String newLine = "\n";
        byte[] bnewLine = newLine.getBytes();
        String sSeparator = ",";
        byte[] bSeparator = sSeparator.getBytes();

        try {


            OutputStream fo = new FileOutputStream(SensorData.hashSensorDataFiles.get(ConstantConfig.KEY_SENSOR_VALUE_ATTRIBUTES), true);


            for (String key : sensorAttributes.keySet()) {
                byte[] bKey = key.getBytes();
                fo.write(bKey);
                fo.write(bSeparator);

                int value = sensorAttributes.get(key);
                String sValue = String.valueOf(value);
                byte[] byte_value = sValue.getBytes();
                fo.write(byte_value);

                fo.write(bnewLine);


            }

            //fo.write(bnewLine);
            fo.close();


        } catch (IOException e) {

            Log.e(TAG, e.toString());
        }
    }


}
