package unimelb.cis.spatialanalytics.sensorreader.io;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
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
import unimelb.cis.spatialanalytics.sensorreader.data.CellTower;
import unimelb.cis.spatialanalytics.sensorreader.data.CellTowerLte;
import unimelb.cis.spatialanalytics.sensorreader.data.DeviceItem;
import unimelb.cis.spatialanalytics.sensorreader.data.SensorData;
import unimelb.cis.spatialanalytics.sensorreader.helps.CustomizedTime;

/**
 * Created by hanl4 on 18/03/2015.
 * Basically this class is used to write sensor data/configuration/notes information onto local external storage.
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
        if (!checkFile(file, "presentLeavingTime"))
            return "";

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
        if (!checkFile(file, "readTxt"))
            return "";
        Log.i(TAG,"reading txt file...");

        int MaxLines = 100;

        //Read text from file
        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            int count = 0;

            while ((line = br.readLine()) != null) {
                count++;
                text.append(line);
                text.append('\n');
                if (count > MaxLines)
                    break;

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
        if (!checkFile(f, "readDoc"))
            return "";

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

        if (!checkFile(file, "recordCurrentTime"))
            return;

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

        if (!checkFile(file, "writePressStopButtonTime"))
            return;
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
     * Check the file information in case of error!
     *
     * @param file
     * @param s
     * @return
     */
    public boolean checkFile(File file, String s) {
        if (file == null) {
            Log.e(TAG + ":" + s, "file is null");
            return false;
        }
        if (!file.exists()) {
            Log.e(TAG + ":" + s, "file doesn't exist" + file.getAbsolutePath());
            return false;
        }
        return true;

    }

    /**
     * write notes.
     */
    public void writeNotes(File file, String data, boolean isSuccess, boolean isSuccessByUser) {

        if (!checkFile(file, "writeNotes"))
            return;
        try {
            String flag_success = "System:" + String.valueOf(isSuccess);
            byte[] bflag_success = flag_success.getBytes();
            String flag_success_user = "User:" + String.valueOf(isSuccessByUser);
            byte[] bflag_success_user = flag_success_user.getBytes();

            String comma = "\n";
            byte[] bComma = comma.getBytes();
            byte[] bdata = data.getBytes();

            OutputStream fo = new FileOutputStream(file, true);
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
        if (SettingConfig.getUserExternalStoragePath() == null) {
            Log.e(TAG, "SettingConfig.getUserExternalStoragePath() is null");
            return null;
        }


        /*
        TEST
         */

        boolean mExternalStorageAvailable = false;
        boolean mExternalStorageWriteable = false;
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // We can read and write the media
            mExternalStorageAvailable = mExternalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            // We can only read the media
            mExternalStorageAvailable = true;
            mExternalStorageWriteable = false;
        } else {
            // Something else is wrong. It may be one of many other states, but all we need
            //  to know is we can neither read nor write
            mExternalStorageAvailable = mExternalStorageWriteable = false;
        }

        Log.i(TAG, "Storage Information : mExternalStorageAvailable-> " + mExternalStorageAvailable + "; mExternalStorageWriteable-> " + mExternalStorageWriteable + "; state->" + state);



        File myNewFolder = new File(SettingConfig.getUserExternalStoragePath() + "/" + folder + "/");
        try {
            if (!myNewFolder.exists()) {
                myNewFolder.mkdir();
            }
        }catch (Exception e)
        {
            Log.e(TAG, "Wrong in creating the folder : " + myNewFolder.getAbsolutePath() + "; Error: " + e.toString());
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
            Log.e(TAG, "error in file creation: " + myNewFolder.getAbsolutePath() + ";" + ioExp.toString());
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
        if (folder == null) {
            Log.e(TAG, "folder is null; createFolder failed!");
            return false;
        }
        if (!folder.exists()) {
            if (folder.mkdir()) {
                Log.d(TAG, "creating new folder success : " + folder.getAbsolutePath());
                return true;
            } else {
                Log.e(TAG, "creating new folder failed! : " + folder.getAbsolutePath());

                return false;

            }
        }
        return true;


    }



	/*
     * Write Sensor Data
	 */

    public void writeSensorDataToFile(String sensorsName, float[] values, int mode,long currentTimeMills,long timestamp) {


        //mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(event.sensor.getType()), SensorManager.SENSOR_DELAY_NORMAL);

        String newLine = "\n";
        byte[] bnewLine = newLine.getBytes();
        String sSeparator = ",";
        byte[] bSeparator = sSeparator.getBytes();
        byte[] bSensorName = sensorsName.getBytes();


        if (SensorData.hashCounts.get(sensorsName) == null) {
            Log.e(TAG, "SensorData.hashCounts.get(sensorsName) is null" + sensorsName);
            return;
        }
        int count = SensorData.hashCounts.get(sensorsName) + 1;
        SensorData.hashCounts.put(sensorsName, count);
        String sCount = String.valueOf(count);
        byte[] bCount = sCount.getBytes();


        String sBeginner = "$";
        byte[] bBeginner = sBeginner.getBytes();
        //long currentTimeMills = CustomizedTime.getCurrentTimeMillis();
        String sCurrentTimeMills = String.valueOf(currentTimeMills);
        byte[] bCurrentTimeMills = sCurrentTimeMills.getBytes();


        String sTimestamp = String.valueOf(timestamp);
        byte[] bTimestamp = sTimestamp.getBytes();

        try {
            File file = SensorData.hashSensorDataFiles.get(sensorsName);
            if (!checkFile(file, "writeSensorDataToFile"))
                return;

            OutputStream fo = new FileOutputStream(file, true);
            fo.write(bBeginner);
            fo.write(bCount);
            fo.write(bSeparator);
            fo.write(bCurrentTimeMills);
            fo.write(bSeparator);
            fo.write(bTimestamp);
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

    public void writeLocationToFile(String sensorsName, double[] values, int mode, String provider) {


        //mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(event.sensor.getType()), SensorManager.SENSOR_DELAY_NORMAL);

        String newLine = "\n";
        byte[] bnewLine = newLine.getBytes();
        String sSeparator = ",";
        byte[] bSeparator = sSeparator.getBytes();
        byte[] bSensorName = sensorsName.getBytes();
        byte[] bProvider = provider.getBytes();


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
            File file = SensorData.hashSensorDataFiles.get(sensorsName);
            if (!checkFile(file, "writeLocationToFile"))
                return;
            OutputStream fo = new FileOutputStream(file, true);
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

            fo.write(bSeparator);
            fo.write(bProvider);



            fo.write(bnewLine);
            fo.close();
        } catch (IOException e) {
            Log.e(TAG, "File write failed: " + e.toString());
        }
    }




	/*
     * Write Cellular tower scan information
	 */

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void writeCellTowerDataToFile( List<CellTower> cellTowerList) {
        if (cellTowerList == null) {
            Log.e(TAG, "cellTowerList is null");
            return;
        }

        Iterator<CellTower> resultIterator = cellTowerList.iterator();


        String newLine = "\n";
        byte[] bnewLine = newLine.getBytes();
        String sSeparator = ",";
        byte[] bSeparator = sSeparator.getBytes();


        if (SensorData.hashCounts.get(ConstantConfig.KEY_CELLTOWRER_FILE_NAME) == null) {
            Log.e(TAG, " SensorData.hashCounts.get(ConstantConfig.KEY_CELLTOWRER_FILE_NAME) is null");
            return;
        }
        int count = SensorData.hashCounts.get(ConstantConfig.KEY_CELLTOWRER_FILE_NAME) + 1;
        SensorData.hashCounts.put(ConstantConfig.KEY_CELLTOWRER_FILE_NAME, count);
        String sCount = String.valueOf(count);
        byte[] bCount = sCount.getBytes();


        String sBeginner = "$";
        byte[] bBeginner = sBeginner.getBytes();
        long currentTimeMills = CustomizedTime.getCurrentTimeMillis();
        String sCurrentTimeMills = String.valueOf(currentTimeMills);
        byte[] bCurrentTimeMills = sCurrentTimeMills.getBytes();


        try {
            if (resultIterator.hasNext()) {
                File file = SensorData.hashSensorDataFiles.get(ConstantConfig.KEY_CELLTOWRER_FILE_NAME);
                if (!checkFile(file, "KEY_CELLTOWRER_FILE_NAME"))
                    return;

                OutputStream fo = new FileOutputStream(file, true);
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

                    CellTower res = resultIterator.next();

                    String sCid = String.valueOf(res.getCid());
                    byte [] byte_cid = sCid.getBytes();
                    fo.write(byte_cid);
                    fo.write(bSeparator);

                    String sRssi = String.valueOf(res.getRssi());
                    byte [] byte_rssi = sRssi.getBytes();
                    fo.write(byte_rssi);
                    fo.write(bSeparator);


                    String sNetworkType = String.valueOf(res.getNetworkType());
                    byte [] byte_networkType = sNetworkType.getBytes();
                    fo.write(byte_networkType);
                    fo.write(bSeparator);

                    String sLac = String.valueOf(res.getLac());
                    byte [] byte_lac = sLac.getBytes();
                    fo.write(byte_lac);
                    fo.write(bSeparator);

                    String sPsc = String.valueOf(res.getPsc());
                    byte [] byte_psc = sPsc.getBytes();
                    fo.write(byte_psc);


                    fo.write(bnewLine);


                }

                //fo.write(bnewLine);
                fo.close();

            }


        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }
    }



	/*
     * Write Cellular tower scan information
	 */

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void writeCellTowerLteDataToFile( List<CellTowerLte> cellTowerList) {
        if (cellTowerList == null) {
            Log.e(TAG, "cellTowerList is null");
            return;
        }

        Iterator<CellTowerLte> resultIterator = cellTowerList.iterator();


        String newLine = "\n";
        byte[] bnewLine = newLine.getBytes();
        String sSeparator = ",";
        byte[] bSeparator = sSeparator.getBytes();


        if (SensorData.hashCounts.get(ConstantConfig.KEY_CELLTOWRER_FILE_NAME) == null) {
            Log.e(TAG, " SensorData.hashCounts.get(ConstantConfig.KEY_CELLTOWRER_FILE_NAME) is null");
            return;
        }
        int count = SensorData.hashCounts.get(ConstantConfig.KEY_CELLTOWRER_FILE_NAME) + 1;
        SensorData.hashCounts.put(ConstantConfig.KEY_CELLTOWRER_FILE_NAME, count);
        String sCount = String.valueOf(count);
        byte[] bCount = sCount.getBytes();


        String sBeginner = "$";
        byte[] bBeginner = sBeginner.getBytes();
        long currentTimeMills = CustomizedTime.getCurrentTimeMillis();
        String sCurrentTimeMills = String.valueOf(currentTimeMills);
        byte[] bCurrentTimeMills = sCurrentTimeMills.getBytes();


        try {
            if (resultIterator.hasNext()) {
                File file = SensorData.hashSensorDataFiles.get(ConstantConfig.KEY_CELLTOWRER_FILE_NAME);
                if (!checkFile(file, "KEY_CELLTOWRER_FILE_NAME"))
                    return;

                OutputStream fo = new FileOutputStream(file, true);
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

                    CellTowerLte res = resultIterator.next();

                    String sPci = String.valueOf(res.getTower().getPci());
                    byte [] byte_sPci = sPci.getBytes();
                    fo.write(byte_sPci);
                    fo.write(bSeparator);


                    String sCid = String.valueOf(res.getTower().getCi());
                    byte [] byte_cid = sCid.getBytes();
                    fo.write(byte_cid);
                    fo.write(bSeparator);

                    String sMcc = String.valueOf(res.getTower().getMcc());
                    byte [] byte_sMcc = sMcc.getBytes();
                    fo.write(byte_sMcc);
                    fo.write(bSeparator);


                    String sMnc = String.valueOf(res.getTower().getMnc());
                    byte [] byte_sMnc = sMnc.getBytes();
                    fo.write(byte_sMnc);
                    fo.write(bSeparator);

                    String sTac = String.valueOf(res.getTower().getTac());
                    byte [] byte_sTac = sTac.getBytes();
                    fo.write(byte_sTac);
                    fo.write(bSeparator);

                    String sRssi = String.valueOf(res.getRss().getDbm());
                    byte [] byte_rssi = sRssi.getBytes();
                    fo.write(byte_rssi);
                    fo.write(bSeparator);


                    String sLevel = String.valueOf(res.getRss().getLevel());
                    byte [] byte_sLevel = sLevel.getBytes();
                    fo.write(byte_sLevel);
                    fo.write(bSeparator);


                    String sAsuLevel = String.valueOf(res.getRss().getAsuLevel());
                    byte [] byte_sAsuLevel = sAsuLevel.getBytes();
                    fo.write(byte_sAsuLevel);
                    fo.write(bSeparator);


                    String sTimingAdvance = String.valueOf(res.getRss().getTimingAdvance());
                    byte [] byte_sTimingAdvance = sTimingAdvance.getBytes();
                    fo.write(byte_sTimingAdvance);



                    fo.write(bnewLine);


                }

                //fo.write(bnewLine);
                fo.close();

            }


        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }
    }

	/*
     * Write Cellular tower scan information
	 */

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void writeBluetoothDataToFile( List<DeviceItem> deviceItems) {
        if (deviceItems == null) {
            Log.e(TAG, "Bluetooth Device deviceItems is null");
            return;
        }

        Iterator<DeviceItem> resultIterator = deviceItems.iterator();


        String newLine = "\n";
        byte[] bnewLine = newLine.getBytes();
        String sSeparator = ",";
        byte[] bSeparator = sSeparator.getBytes();


        if (SensorData.hashCounts.get(ConstantConfig.KEY_BLUETOOTH_FILE_NAME) == null) {
            Log.e(TAG, " SensorData.hashCounts.get(ConstantConfig.KEY_BLUETOOTH_FILE_NAME) is null");
            return;
        }
        int count = SensorData.hashCounts.get(ConstantConfig.KEY_BLUETOOTH_FILE_NAME) + 1;
        SensorData.hashCounts.put(ConstantConfig.KEY_BLUETOOTH_FILE_NAME, count);
        String sCount = String.valueOf(count);
        byte[] bCount = sCount.getBytes();


        String sBeginner = "$";
        byte[] bBeginner = sBeginner.getBytes();
        long currentTimeMills = CustomizedTime.getCurrentTimeMillis();
        String sCurrentTimeMills = String.valueOf(currentTimeMills);
        byte[] bCurrentTimeMills = sCurrentTimeMills.getBytes();


        try {
            if (resultIterator.hasNext()) {
                File file = SensorData.hashSensorDataFiles.get(ConstantConfig.KEY_BLUETOOTH_FILE_NAME);
                if (!checkFile(file, "KEY_BLUETOOTH_FILE_NAME"))
                    return;

                OutputStream fo = new FileOutputStream(file, true);
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

                    DeviceItem res = resultIterator.next();

                    String deviceName = res.getDeviceName();
                    byte [] byte_deviceName = deviceName.getBytes();
                    fo.write(byte_deviceName);
                    fo.write(bSeparator);


                    String address = res.getAddress();
                    byte [] byte_address = address.getBytes();
                    fo.write(byte_address);
                    fo.write(bSeparator);

                    String connected = String.valueOf(res.getConnected());
                    byte [] byte_connected = connected.getBytes();
                    fo.write(byte_connected);
                    fo.write(bSeparator);


                    String sRssi = String.valueOf(res.getRssi());
                    byte [] byte_rssi = sRssi.getBytes();
                    fo.write(byte_rssi);



                    fo.write(bnewLine);


                }

                //fo.write(bnewLine);
                fo.close();

            }


        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }
    }





	/*
     * Write WIFI scan information
	 */

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void writeWifiDataToFile(List<ScanResult> wifiList, WifiInfo currentWifi) {
        if (wifiList == null) {
            Log.e(TAG, "wifiList is null");
            return;
        }
        if (currentWifi == null) {
            Log.e(TAG, "currentWifi is null");
            return;
        }

        Iterator<ScanResult> resultIterator = wifiList.iterator();


        String newLine = "\n";
        byte[] bnewLine = newLine.getBytes();
        String sSeparator = ",";
        byte[] bSeparator = sSeparator.getBytes();


        if (SensorData.hashCounts.get(ConstantConfig.KEY_WIFI_FILE_NAME) == null) {
            Log.e(TAG, " SensorData.hashCounts.get(ConstantConfig.KEY_WIFI_FILE_NAME) is null");
            return;
        }
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
                File file = SensorData.hashSensorDataFiles.get(ConstantConfig.KEY_WIFI_FILE_NAME);
                if (!checkFile(file, "writeWifiDataToFile"))
                    return;

                OutputStream fo = new FileOutputStream(file, true);
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


                    String timestamp=String.valueOf(scanResult.timestamp);
                    byte[] byte_timestamp= timestamp.getBytes();
                    fo.write(byte_timestamp);
                    fo.write(bSeparator);


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
            File file = SensorData.hashSensorDataFiles.get(ConstantConfig.KEY_LEAVING_HOME_TIMES);
            if (!checkFile(file, "writeLeavingHomeTimeToFile"))
                return;
            OutputStream fo = new FileOutputStream(file, true);

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

        if (sensorAttributes == null || sensorAttributes.size() == 0)
            return;
        String newLine = "\n";
        byte[] bnewLine = newLine.getBytes();
        String sSeparator = ",";
        byte[] bSeparator = sSeparator.getBytes();

        try {


            File file = SensorData.hashSensorDataFiles.get(ConstantConfig.KEY_SENSOR_VALUE_ATTRIBUTES);
            if (!checkFile(file, "writeSensorAttributesToFile")) {

                return;
            }

            OutputStream fo = new FileOutputStream(file, true);


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
