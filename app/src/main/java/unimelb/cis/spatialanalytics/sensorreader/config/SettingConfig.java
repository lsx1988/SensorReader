package unimelb.cis.spatialanalytics.sensorreader.config;

import android.content.SharedPreferences;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import unimelb.cis.spatialanalytics.sensorreader.data.FileListManager;
import unimelb.cis.spatialanalytics.sensorreader.data.MakeNotes;
import unimelb.cis.spatialanalytics.sensorreader.data.SensorData;
import unimelb.cis.spatialanalytics.sensorreader.data.Users;
import unimelb.cis.spatialanalytics.sensorreader.io.FileIO;

/**
 * Created by hanl4 on 18/03/2015.
 */
public class SettingConfig {

    /**
     * Called whenever LoinActivity is active.
     */
    private static final String TAG = "SettingConfig";
    private static String KEY_EXTERNAL_STORAGE_USER_PATH;
    public static SharedPreferences pref;

    public static String myEncryptionKey="123456789abcdefghigklmnop";//$Wi6QQ$#&.r9aTh#!IsxC20t#


    public static FragmentManager fragmentManager;


    public static void ini(SharedPreferences prefs) {
        Log.i(TAG, "SettingConfig ini");
        pref = prefs;
        Users.clear();
        SensorData.clear();
        MakeNotes.clear();
        FileListManager.clear();
        setUpPaths();


    }


    public static void iniWithoutLogin(SharedPreferences prefs) {
        Log.i(TAG, "SettingConfig ini");
        pref = prefs;
       // Users.clear();
        SensorData.clear();
        MakeNotes.clear();
        FileListManager.clear();
        setUpPaths();

        int count = 0;
        for (File folder : FileListManager.getFileCollections().keySet()) {
            List<File> childrenFiles = FileListManager.getFileCollections().get(folder);
            count ++;


        }

        Users.withoutLoginUpdateRecordTime();



    }

    /**
     * set up all the pathes
     */

    private static void setUpPaths() {

        FileIO.createFolder(new File(ConstantConfig.KEY_EXTERNAL_STORAGE_ROOT));


    }


    /**
     * set up the path for the user storage
     */
    public static boolean setUserExternalStoragePath() {
        if (Users.username == null || Users.username.equals("")) {
            Log.e(TAG, "username is null or empty");
            KEY_EXTERNAL_STORAGE_USER_PATH = ConstantConfig.KEY_EXTERNAL_STORAGE_ROOT + Users.fakeUsername;

        } else
            KEY_EXTERNAL_STORAGE_USER_PATH = ConstantConfig.KEY_EXTERNAL_STORAGE_ROOT + Users.username;


        return FileIO.createFolder(new File(KEY_EXTERNAL_STORAGE_USER_PATH));

    }


    /**
     * Get user personal folder
     *
     * @return
     */
    public static String getUserExternalStoragePath() {
        if (KEY_EXTERNAL_STORAGE_USER_PATH == null || KEY_EXTERNAL_STORAGE_USER_PATH.equals("")) {
            if (setUserExternalStoragePath())
                return KEY_EXTERNAL_STORAGE_USER_PATH;
            else
                return "";
        } else
            return KEY_EXTERNAL_STORAGE_USER_PATH;


    }


    /**
     * get sensing mode
     *
     * @return
     */
    public static int getSensingMode() {

        if (pref == null)
            return ConstantConfig.SENSING_MODE_DEFAULT;
        return pref.getInt(ConstantConfig.SENSING_MODE, ConstantConfig.SENSING_MODE_DEFAULT);
    }


    /**
     * Set sensing mode
     */
    public static void setSensingMode(int id) {
        if (pref == null)
            return;
        pref.edit().putInt(ConstantConfig.SENSING_MODE, id).commit();
        pref.edit().apply();

    }


    /**
     * Notes
     */

    public static void setNotePhoneOrientation(String str) {
        if (pref == null)
            return;
        pref.edit().putString(ConstantConfig.NOTES_PHONE_ORIENTATION, str).commit();
        pref.edit().apply();
    }

    public static void setNoteWeather(String str) {
        if (pref == null)
            return;
        pref.edit().putString(ConstantConfig.NOTES_WEATHER, str).commit();
        pref.edit().apply();

    }

    public static void setNoteRoute(String str) {
        if (pref == null)
            return;
        pref.edit().putString(ConstantConfig.NOTES_ROUTE, str).commit();
        pref.edit().apply();
    }

    public static void setNoteNotes(String str) {
        if (pref == null)
            return;
        pref.edit().putString(ConstantConfig.NOTES_NOTES, str).commit();
        pref.edit().apply();
    }

    /**
     * Reset
     */

    public static String getNotePhoneOrientation() {
        if (pref == null)
            return "";
        return pref.getString(ConstantConfig.NOTES_PHONE_ORIENTATION, "");
    }

    public static String getNoteWeather() {
        if (pref == null)
            return "";
        return pref.getString(ConstantConfig.NOTES_WEATHER, "");

    }

    public static String getNoteRoute() {
        if (pref == null)
            return "";
        return pref.getString(ConstantConfig.NOTES_ROUTE, "");
    }

    public static String getNoteNotes() {
        if (pref == null)
            return "";
        return pref.getString(ConstantConfig.NOTES_NOTES, "");
    }


    /**
     * return the value of default setting of clock hour
     *
     * @return
     */
    public static int getClockHour() {
        if (pref == null)
            return Integer.valueOf(ConstantConfig.CLOCK_TIME_HOUR_DEFAULT);
        else

            return Integer.valueOf(pref.getString(ConstantConfig.CLOCK_TIME_HOUR, ConstantConfig.CLOCK_TIME_HOUR_DEFAULT));
    }

    public static int getClockMinute() {
        if (pref == null)
            return Integer.valueOf(ConstantConfig.CLOCK_TIME_MINUTE_DEFAULT);
        else
            return Integer.valueOf(pref.getString(ConstantConfig.CLOCK_TIME_MINUTE, ConstantConfig.CLOCK_TIME_MINUTE_DEFAULT));
    }

    public static String getClockTimeString() {

/*
        pref.edit().remove(ConstantConfig.CLOCK_TIME_HOUR).commit();
        pref.edit().remove(ConstantConfig.CLOCK_TIME_MINUTE).commit();
        pref.edit().apply();*/

        String hour = pref == null ? ConstantConfig.CLOCK_TIME_HOUR_DEFAULT : pref.getString(ConstantConfig.CLOCK_TIME_HOUR, ConstantConfig.CLOCK_TIME_HOUR_DEFAULT);
        String minute = pref == null ? ConstantConfig.CLOCK_TIME_MINUTE_DEFAULT : pref.getString(ConstantConfig.CLOCK_TIME_MINUTE, ConstantConfig.CLOCK_TIME_MINUTE_DEFAULT);
        String time = hour + ":" + minute + " AM";
        //  String time="16:06 AM";

        Date dt = new Date();


        try {
            SimpleDateFormat sdf = new SimpleDateFormat(ConstantConfig.KEY_TIME_FORMAT);
            Date date = sdf.parse(time);
            return sdf.format(date);
        } catch (final ParseException e) {
            e.printStackTrace();
            Log.e(TAG, e.toString());
            return "";
        }


    }


    /**
     * set up the clock
     *
     * @param hour
     * @param minute
     */
    public static boolean setClock(int hour, int minute) {
        if (pref == null)
            return false;
        String sHour = String.format("%02d", hour);
        String sMinute = String.format("%02d", minute);
        pref.edit().putString(ConstantConfig.CLOCK_TIME_HOUR, sHour).commit();
        pref.edit().putString(ConstantConfig.CLOCK_TIME_MINUTE, sMinute).commit();
        pref.edit().apply();
        return true;

    }


    /**
     * set delay time
     *
     * @param delayTime
     */
    public static void setDelayTime(int delayTime) {
        if (pref == null)
            return;
        delayTime = delayTime > ConstantConfig.MAXIMUM_DELAY_TIME ? ConstantConfig.MAXIMUM_DELAY_TIME : delayTime;
        delayTime = delayTime < ConstantConfig.MINIMUM_DELAY_TIME ? ConstantConfig.MINIMUM_DELAY_TIME : delayTime;
        pref.edit().putInt(ConstantConfig.DELAY_TIME, delayTime).commit();
        pref.edit().apply();
    }

    public static int getDelayTime() {
        if (pref == null)
            return ConstantConfig.DEFAULT_DELAY_TIME;
        return pref.getInt(ConstantConfig.DELAY_TIME, ConstantConfig.DEFAULT_DELAY_TIME);
    }

}
