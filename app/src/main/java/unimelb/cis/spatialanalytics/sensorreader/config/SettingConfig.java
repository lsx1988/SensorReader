package unimelb.cis.spatialanalytics.sensorreader.config;

import android.content.SharedPreferences;
import android.util.Log;

import java.io.File;

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


    public static void ini(SharedPreferences prefs) {
        pref=prefs;
        Users.clear();
        SensorData.clear();
        setUpPaths();
        KEY_EXTERNAL_STORAGE_USER_PATH=null;



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
            return false;

        }
        KEY_EXTERNAL_STORAGE_USER_PATH = ConstantConfig.KEY_EXTERNAL_STORAGE_ROOT + Users.username;


        return FileIO.createFolder(new File(KEY_EXTERNAL_STORAGE_USER_PATH));

    }


    /**
     * Get user personal folder
     * @return
     */
    public static String getUserExternalStoragePath() {
       if (KEY_EXTERNAL_STORAGE_USER_PATH == null) {
            if (setUserExternalStoragePath())
                return KEY_EXTERNAL_STORAGE_USER_PATH;
            else
                return null;
        } else
            return KEY_EXTERNAL_STORAGE_USER_PATH;


    }

}
