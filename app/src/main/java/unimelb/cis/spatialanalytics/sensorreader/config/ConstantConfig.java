package unimelb.cis.spatialanalytics.sensorreader.config;

import android.os.Environment;

import unimelb.cis.spatialanalytics.sensorreader.R;

/**
 * Created by hanl4 on 18/03/2015.
 */
public class ConstantConfig {

    //Sensor file names
    public static final String KEY_LOCATION_FILE_NAME = "Location";
    public static final String KEY_LIGHT_FILE_NAME = "Light";
    public static final String KEY_CELLTOWRER_FILE_NAME = "CellTower";
    public static final String KEY_BLUETOOTH_FILE_NAME = "Bluetooth";

    public static final String KEY_WIFI_FILE_NAME = "Wifi";
    public static final String KEY_LEAVING_HOME_TIMES = "Leaving_Home_Time";
    public static final String KEY_NOTES = "Notes";
    public static final String KEY_PRESS_STOP_BUTTON_TIME = "StopTime";
    public static final String KEY_SENSOR_VALUE_ATTRIBUTES = "Sensor_Value_Attributes";

    public static final String KEY_FILE_ROOT = "/Leaving Home/";//the root of storing sensor data files
    public static String KEY_EXTERNAL_STORAGE_DIRECTORY = Environment.getExternalStorageDirectory().toString();
    public static String KEY_EXTERNAL_STORAGE_ROOT = KEY_EXTERNAL_STORAGE_DIRECTORY + KEY_FILE_ROOT;


    //split symbol to separate the files
    public static final String FILE_NAME_SPLIT_SYMBOL = "#";

    //upload file
    public static final String KEY_FILE_UPLOAD_FILE_DATA = "file";
    public static final String KEY_FILE_UPLOAD_STRING_DATA = "string";

    //file walker
    public static final int KEY_WALK_FOLDER = 0;
    public static final int KEY_WALK_FILE = 1;


    /**
     * JSON response codes HTTP
     */
    public static final String KEY_SUCCESS = "ok";
    public static final String KEY_ERROR = "error";

    /**
     * Fragment IDs
     */
    public static final int FRAGMENT_MAIN_PANEL = 0;
    public static final int FRAGMENT_UPLOAD_FILE = 1;
    public static final int FRAGMENT_SETTING = 2;
    public static final int FRAGMENT_STATEMENT = 3;
    public static final int FRAGMENT_ACCOUNT = 4;


    //for volley maximum threads pool
    public static final int MAXIMUM_THREAD_POOL = 1;

    //colors
    public static final String COLOR_DIALOG_DISMISS ="#1e88e5";// "#ff37474f";

    //maximum disk cache for volley
    public static final int MAXIMUM_DISK_CACHE_SIZE = 30 * 1024 * 1024;//0*1024*1024; //50 MB


    //maximum uploading file size to conquer the problem of out of memory
    public static final int MAXIMUM_UPLOAD_FILE_SIZE = 50;//0*1024*1024; //50 MB

    //define 1MB size
    public static final int MB_UNIT = 1024 * 1024;

    //maximum time out for uploading the files to the server
    public static final int MAXIMUM_TIME_OUT = 45000;//45s


    //keys for HTTP request
    public static final String KEY_HTTP_ACTION = "action";
    //for couchDB
    public static final String KEY_COUCHDB_DOC_ID = "_id";
    public static final String KEY_COUCHDB_DOC_DATA = "data";
    public static final String KEY_COUCHDB_DOC_REV = "rev";


    //user info
    public static final String KEY_USER_NAME = "username";
    public static final String KEY_USER_PASSWORD = "password";
    public static final String KEY_USER_UPLOAD_TIMES = "upload_times";
    public static final String KEY_USER_TODAY = "today";
    public static final String KEY_USER_RECORD_TIMES = "today_record_times";
    public static final String KEY_USER_DEVICE_MODE="device";


    //user login
    public static final String LOCAL_USER_LOGIN = "isUserLogin";


    //public data format
    public static final String KEY_DATE_FORMAT = "yyyy_MM_dd";
    public static final String KEY_TIME_FORMAT = "hh:mm a";


    //modes
    public static final String SENSING_MODE = "sensing_mode";
    public static final int SENSING_MODE_AUTO = R.id.radio_auto;
    public static final int SENSING_MODE_DEFAULT=R.id.radio_manual;
    public static final int SENSING_MODE_MANUAL = R.id.radio_manual;

    public static final int STOP_SENSING_MODE_MANUAL=0;
    public static final int STOP_SENSING_MODE_ALARM_DELAY=1;



    public static final int NOTE_METHOD_SETTING_DIALOG = 1;
    public static final int NOTE_METHOD_SETTING_FRAGMENT = 0;


    //notes
    public static final String NOTES_PHONE_ORIENTATION = "phone_orientation";
    public static final String NOTES_WEATHER = "weather";
    public static final String NOTES_ACTIVITY = "activity";
    public static final String NOTES_ROUTE = "route";
    public static final String NOTES_NOTES = "note";


    //clock
    public static final String CLOCK_TIME_HOUR = "hour";
    public static final String CLOCK_TIME_MINUTE = "minute";

    public static final String CLOCK_TIME_HOUR_DEFAULT = "07";
    public static final String CLOCK_TIME_MINUTE_DEFAULT = "30";


    /**
     * set up alarms
     */
    public static final String ALARM_TYPE = "alarm";
    public static final int ALARM_CODE_REPEAT = 0;
    public static final int ALARM_CODE_DELAY = 1;
    public static final int ALARM_DELAY_TIME_UNIT = 60 * 1000;//1 min


    /**
     * WIFI Settings
     */
    public static final int WiFi_SAMPLE_RATE = 0; //100;//5 secs 30000


    //button colors
    public static final String COLOR_DIALOG_BUTTON_OK="#3B5998";
    public static final String COLOR_DIALOG_BUTTON_CANCEL="#ff80cbc4";

    //delay time settings
    public static final String DELAY_TIME="delay_time";
    public static final int MAXIMUM_DELAY_TIME=5;
    public static final int MINIMUM_DELAY_TIME=2;
    public static final int DEFAULT_DELAY_TIME=3;

    //login action
    public static final String HTTP_ACTION_LOGIN="login";

}
