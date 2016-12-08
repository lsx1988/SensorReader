package unimelb.cis.spatialanalytics.sensorreader.data;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import unimelb.cis.spatialanalytics.sensorreader.alarm.Alarm;
import unimelb.cis.spatialanalytics.sensorreader.config.ConstantConfig;
import unimelb.cis.spatialanalytics.sensorreader.config.SettingConfig;
import unimelb.cis.spatialanalytics.sensorreader.config.URLConfig;
import unimelb.cis.spatialanalytics.sensorreader.helps.CustomizedTime;
import unimelb.cis.spatialanalytics.sensorreader.http.ApplicationController;
import unimelb.cis.spatialanalytics.sensorreader.http.CustomRequest;
import unimelb.cis.spatialanalytics.sensorreader.http.MyExceptionHandler;
import unimelb.cis.spatialanalytics.sensorreader.io.FileWalker;
import unimelb.cis.spatialanalytics.sensorreader.login.LoginActivity;
import unimelb.cis.spatialanalytics.sensorreader.sensors.SensingService;

/**
 * Created by hanl4 on 20/03/2015.
 */
public class Users {
    /**
     * User Fields
     */
    public static String fakeUsername = "temp";
    public static String username = "newman2";
    private static int upload_times = 0;
    private static int temp_upload_times = 0;
    public static String password = "";
    public static String today = ConstantConfig.KEY_DATE_FORMAT;
    public static int record_times = 0;
    public static String device="";


    public static int getUpload_times() {
        //return upload_times;
        if(username.equals(""))
            return upload_times;

        Map<String, String> params = new HashMap<String, String>();
        params.put(ConstantConfig.KEY_HTTP_ACTION, "GET");
        params.put(ConstantConfig.KEY_COUCHDB_DOC_ID, username);
        String url = URLConfig.getCouchDBAPI();//get doc
        ApplicationController.getInstance().getRequestQueue().start();

        CustomRequest customRequest = new CustomRequest(
                Request.Method.POST,    /////// first parameter
                url,
                params,     /////// third parameter
                ////////////////////////////// fourth parameter //////////////////////////////////////
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject json) {
                        Log.d(TAG, json.toString());

                        if (!json.has(ConstantConfig.KEY_ERROR)) {
                            //user successfully logged in
                            //record user information into system
                            try {
                                upload_times=json.getInt(ConstantConfig.KEY_USER_UPLOAD_TIMES);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.e(TAG,e.toString());
                            }


                        } else {
                            // Error
                            Log.e(TAG,"read user document error");

                        }

                        ApplicationController.getInstance().getRequestQueue().stop();

                    }
                },
                ///////////////////////////////////////////////////////////////////////////////////////
                //////////////////////////////////// fifth parameter ///////////////////////////
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        MyExceptionHandler exceptionHandler = new MyExceptionHandler(TAG, null);

                        exceptionHandler.getVolleyError("read user document failed", error, null);
                        ApplicationController.getInstance().getRequestQueue().stop();


                    }
                }
                ///////////////////////////////////////////////////////////////////////////////////////
        );
        // Adding request to request queue
        ApplicationController.getInstance().addToRequestQueue(customRequest, TAG);

        return  upload_times;



    }

    private static String TAG = "Users";


    /**
     * update the record times
     *
     * @param increase
     * @param context
     * @param textViewError
     */
    public static void setUpload_times(int increase, final Context context, final TextView textViewError) {
        if (increase == 0)
            return;

        temp_upload_times = upload_times;
        temp_upload_times += increase;


        /**
         * Update user information to sync with the server
         */

        try {
            //update the record to the server

            //make http request to the server to check the user information
            Map<String, String> params = new HashMap<String, String>();
            params.put(ConstantConfig.KEY_HTTP_ACTION, "PUT");
            params.put(ConstantConfig.KEY_COUCHDB_DOC_ID, username);

            JSONObject jsonData = new JSONObject();

            jsonData.put(ConstantConfig.KEY_COUCHDB_DOC_ID, username);
            jsonData.put(ConstantConfig.KEY_USER_PASSWORD, password);
            jsonData.put(ConstantConfig.KEY_USER_UPLOAD_TIMES, temp_upload_times);
            jsonData.put(ConstantConfig.KEY_USER_TODAY, today);
            jsonData.put(ConstantConfig.KEY_USER_RECORD_TIMES, record_times);
            jsonData.put(ConstantConfig.KEY_USER_DEVICE_MODE,device);

            params.put(ConstantConfig.KEY_COUCHDB_DOC_DATA, jsonData.toString());


            String url = URLConfig.getCouchDBAPI();//get doc
            ApplicationController.getInstance().getRequestQueue().start();
            CustomRequest customRequest = new CustomRequest(
                    Request.Method.POST,    /////// first parameter
                    url,
                    params,     /////// third parameter
                    ////////////////////////////// fourth parameter //////////////////////////////////////
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject json) {
                            Log.d(TAG, json.toString());


                            /**
                             * Self defined login function
                             */
                            if (!json.has(ConstantConfig.KEY_ERROR)) {

                                upload_times = temp_upload_times;
                                setLocalPrefUserInfo(SettingConfig.pref);//upload local user information as well;

                            } else {
                                // Error

                                Log.e(TAG, json.toString());
                            }


                            ApplicationController.getInstance().getRequestQueue().stop();

                        }
                    },
                    ///////////////////////////////////////////////////////////////////////////////////////
                    //////////////////////////////////// fifth parameter ///////////////////////////
                    new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {

                            new MyExceptionHandler(TAG, context).getVolleyError("login failed", error, textViewError);
                            ApplicationController.getInstance().getRequestQueue().stop();


                        }
                    }
                    ///////////////////////////////////////////////////////////////////////////////////////
            );
            // Adding request to request queue
            ApplicationController.getInstance().addToRequestQueue(customRequest, TAG);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, e.toString());
        }


    }


    /**
     * Log out the user
     *
     * @param context
     */
    public static void logOut(Activity context) {

        Log.i(TAG,"logOut is called");

        SensorData.clear();
        clear();
        setLoginStatus(SettingConfig.pref, false);
        setLocalPrefUserInfo(SettingConfig.pref);
        MakeNotes.clear();
        FileListManager.clear();
        SettingConfig.setSensingMode(ConstantConfig.SENSING_MODE_DEFAULT);
        SettingConfig.setDelayTime(ConstantConfig.DEFAULT_DELAY_TIME);
        SettingConfig.setClock(Integer.valueOf(ConstantConfig.CLOCK_TIME_HOUR_DEFAULT),Integer.valueOf(ConstantConfig.CLOCK_TIME_MINUTE_DEFAULT));
        SettingConfig.setNotePhoneOrientation("");

        /**
         * Stop sensing
         */
        if (context != null) {
            Intent mIntent = new Intent(context, SensingService.class);
            context.stopService(mIntent);

            Intent intent = new Intent(context, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);


            //cancel alarm
            Alarm alarm=new Alarm(context,null);
            alarm.cancelAlarm(ConstantConfig.ALARM_CODE_REPEAT);
        }
    }


    /**
     * Handle force close the app!
     */
    public static void exceptionalLogOut(Activity context) {
        Log.i(TAG,"exceptionalLogOut is called!");
        /**
         * Stop sensing
         */
        if(context==null)
        {
            Log.e(TAG, "exceptionalLogOut context is null");
            return;
        }

        Intent mIntent = new Intent(context, SensingService.class);
        context.stopService(mIntent);




        //cancel alarm
        Alarm alarm=new Alarm(context,null);
        alarm.cancelAlarm(ConstantConfig.ALARM_CODE_DELAY);

/*        SensorData.clear();
        clear();

        MakeNotes.clear();
        FileListManager.clear();*/


    }


    /**
     * To detect user has logged in the system before or not.
     *
     * @param pref SharedPreferences
     * @return true means user has logged into the system before; otherwise return false.
     */
    public static boolean isUserLoggedInBefore(SharedPreferences pref)

    {
        if (pref.contains(ConstantConfig.LOCAL_USER_LOGIN))
            if (pref.getBoolean(ConstantConfig.LOCAL_USER_LOGIN, false))
                return true;

        return false;

    }

    /**
     * If user has successfully logged into our system, record it locally.
     *
     * @param pref SharedPreferences
     * @param flag set the the status of logging the same value as flag
     */

    public static void setLoginStatus(SharedPreferences pref, boolean flag) {
        pref.edit().putBoolean(ConstantConfig.LOCAL_USER_LOGIN, flag).commit();
        pref.edit().apply();

    }


/*    *//**
     * get username from SharedPreferences
     *
     * @param pref SharedPreferences
     *//*
    public static String getUserNameFromSharedPreferences(SharedPreferences pref) {

        if (pref.contains(ConstantConfig.KEY_USER_NAME))
            return pref.getString(ConstantConfig.KEY_USER_NAME, "");
        else
            return "";


    }*/


  /*  *//**
     * update username to SharedPreferences
     *
     * @param pref SharedPreferences
     *//*
    public static void setUserNameFromSharedPreferences(SharedPreferences pref, String username) {

        pref.edit().putString(ConstantConfig.KEY_USER_NAME, username).commit();
        pref.edit().apply();


    }*/



    /**
     * update username to SharedPreferences
     *
     * @param pref SharedPreferences
     */
    public static void setLocalPrefUserInfo(SharedPreferences pref) {

        pref.edit().putString(ConstantConfig.KEY_USER_NAME, username).commit();
        pref.edit().putString(ConstantConfig.KEY_USER_PASSWORD,password).commit();
        pref.edit().putInt(ConstantConfig.KEY_USER_UPLOAD_TIMES, upload_times).commit();
        pref.edit().putString(ConstantConfig.KEY_USER_TODAY, today).commit();
        pref.edit().putInt(ConstantConfig.KEY_USER_RECORD_TIMES, record_times).commit();
        pref.edit().putString(ConstantConfig.KEY_USER_DEVICE_MODE,device).commit();
        pref.edit().apply();


    }

    /**
     * sync user information with the server
     *
     * @param pref SharedPreferences
     */
    public static boolean syncLocalFromPref(SharedPreferences pref) {
        Log.i(TAG, "syncLocalFromPref");

        if (pref.contains(ConstantConfig.KEY_USER_NAME)) {
            username = pref.getString(ConstantConfig.KEY_USER_NAME, "");
            if(username==null ||username.equals(""))
                return false;
        }
        else
            return false;

        if (pref.contains(ConstantConfig.KEY_USER_PASSWORD)) {
            password = pref.getString(ConstantConfig.KEY_USER_PASSWORD, "");
            if(password==null ||password.equals(""))
                return false;
        }
        else
            return false;

        if (pref.contains(ConstantConfig.KEY_USER_TODAY)) {
            today = pref.getString(ConstantConfig.KEY_USER_TODAY, "");

        }
        else
            return false;

        if (pref.contains(ConstantConfig.KEY_USER_RECORD_TIMES))
            record_times= pref.getInt(ConstantConfig.KEY_USER_RECORD_TIMES, 0);
        else
            return false;

        if (pref.contains(ConstantConfig.KEY_USER_UPLOAD_TIMES))
            upload_times= pref.getInt(ConstantConfig.KEY_USER_UPLOAD_TIMES, 0);
        else
            return false;

        if(pref.contains(ConstantConfig.KEY_USER_DEVICE_MODE)) {
            device = pref.getString(ConstantConfig.KEY_USER_DEVICE_MODE, "");
          /*  if(device==null ||device.equals(""))
                return false;*/
        }
        else
            return false;



        //update the record if we have better one at local
        List<File> folderList = new FileWalker().createGroupList();

        //file the file name that matches today!

        for (File file : folderList) {

            updateUserRecordTimesInfo(file);

        }

        return true;




    }


    public static void withoutLoginUpdateRecordTime()
    {


        //update the record if we have better one at local
        List<File> folderList = new FileWalker().createGroupList();

        //file the file name that matches today!

        for (File file : folderList) {

            updateUserRecordTimesInfo(file);

        }
    }


    /**
     * clear local user information
     */
    public static void clear() {
        Log.i(TAG,"clear()");
        Users.upload_times = 0;
        Users.username = "";
        Users.password = "";
        Users.today = ConstantConfig.KEY_DATE_FORMAT;
        Users.record_times = 0;
        Users.device="";


    }




    /**
     * sync user information with the server
     *
     * @param json response from the server
     */
    public static void syncLocalFromServer(JSONObject json) {
        try {
            if (json.has(ConstantConfig.KEY_COUCHDB_DOC_ID))
                username = json.getString(ConstantConfig.KEY_COUCHDB_DOC_ID);
            if (json.has(ConstantConfig.KEY_USER_PASSWORD))
                password = json.getString(ConstantConfig.KEY_USER_PASSWORD);
            if (json.has(ConstantConfig.KEY_USER_UPLOAD_TIMES))
                upload_times = json.getInt(ConstantConfig.KEY_USER_UPLOAD_TIMES);
            if(json.has(ConstantConfig.KEY_USER_DEVICE_MODE))
                device=json.getString(ConstantConfig.KEY_USER_DEVICE_MODE);

            if (json.has(ConstantConfig.KEY_USER_TODAY)) {
                String str = json.getString(ConstantConfig.KEY_USER_TODAY);
                if (str != null && str.equals(CustomizedTime.getToday())) {
                    today = str;

                    if (json.has(ConstantConfig.KEY_USER_RECORD_TIMES)) {
                        int counts = json.getInt(ConstantConfig.KEY_USER_RECORD_TIMES);
                        if (counts > record_times)
                            record_times = counts;

                    }
                }

            }


            //update the record if we have better one at local
            List<File> folderList = new FileWalker().createGroupList();

            //file the file name that matches today!

            for (File file : folderList) {

                updateUserRecordTimesInfo(file);

            }


        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, e.toString());
        }

    }

    /**
     * update user record for file
     *
     * @param file
     */

    public static void updateUserRecordTimesInfo(File file) {
        String str = file.getName();
        String currentDay = CustomizedTime.getToday();
        if (str.contains(currentDay)) {
            today = currentDay;

            //matches today
            int index = today.length();
            if (index == str.length()) {
                if (record_times == 0)
                    record_times = 1;
            } else {
                int lastTimes = Integer.valueOf(str.substring(index + 1, str.length()));
                if (lastTimes >= record_times)
                    record_times = lastTimes;
            }

        }

    }
}
