package unimelb.cis.spatialanalytics.sensorreader.data;

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

import unimelb.cis.spatialanalytics.sensorreader.config.ConstantConfig;
import unimelb.cis.spatialanalytics.sensorreader.config.SettingConfig;
import unimelb.cis.spatialanalytics.sensorreader.config.URLConfig;
import unimelb.cis.spatialanalytics.sensorreader.helps.CustomizedTime;
import unimelb.cis.spatialanalytics.sensorreader.http.ApplicationController;
import unimelb.cis.spatialanalytics.sensorreader.http.CustomRequest;
import unimelb.cis.spatialanalytics.sensorreader.http.MyExceptionHandler;
import unimelb.cis.spatialanalytics.sensorreader.io.FileWalker;
import unimelb.cis.spatialanalytics.sensorreader.login.LoginActivity;
import unimelb.cis.spatialanalytics.sensorreader.services.SensingService;

/**
 * Created by hanl4 on 20/03/2015.
 */
public class Users {
    /**
     * User Fields
     */
    public static String username = "";
    public static int upload_times = 0;
    public static String password = "";
    public static String today = ConstantConfig.KEY_DATE_FORMAT;
    public static int record_times = 0;


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

        upload_times += increase;
        updateUserInfoToServer(context, textViewError);//update the server user record

    }


    /**
     * Update user information to sync with the server
     */
    public static void updateUserInfoToServer(final Context context, final TextView textViewError) {
        try {
            //update the record to the server

            //make http request to the server to check the user information
            Map<String, String> params = new HashMap<String, String>();
            params.put(ConstantConfig.KEY_HTTP_ACTION, "PUT");
            params.put(ConstantConfig.KEY_COUCHDB_DOC_ID, username);

            JSONObject jsonData = new JSONObject();

            jsonData.put(ConstantConfig.KEY_COUCHDB_DOC_ID, username);
            jsonData.put(ConstantConfig.KEY_USER_PASSWORD, password);
            jsonData.put(ConstantConfig.KEY_USER_UPLOAD_TIMES, upload_times);
            jsonData.put(ConstantConfig.KEY_USER_TODAY, today);
            jsonData.put(ConstantConfig.KEY_USER_RECORD_TIMES, record_times);

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

                            try {
                                /**
                                 * Self defined login function
                                 */
                                if (!json.has(ConstantConfig.KEY_ERROR)) {
                                    if (json.has(ConstantConfig.KEY_USER_UPLOAD_TIMES))
                                        upload_times = json.getInt(ConstantConfig.KEY_USER_UPLOAD_TIMES);


                                } else {
                                    // Error

                                    Log.e(TAG, json.toString());
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.e(TAG, e.toString());
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
    public static void logOut(Context context) {


        clear();
        setLoginStatus(SettingConfig.pref,false);
        setUserNameFromSharedPreferences(SettingConfig.pref,"");
        /**
         * Stop sensing
         */
        Intent mIntent = new Intent(context, SensingService.class);
        context.stopService(mIntent);

        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
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


    /**
     * get username from SharedPreferences
     *
     * @param pref SharedPreferences
     */
    public static String getUserNameFromSharedPreferences(SharedPreferences pref) {

        if (pref.contains(ConstantConfig.KEY_USER_NAME))
            return pref.getString(ConstantConfig.KEY_USER_NAME, "");
        else
            return "";


    }


    /**
     * update username to SharedPreferences
     *
     * @param pref SharedPreferences
     */
    public static void setUserNameFromSharedPreferences(SharedPreferences pref, String username) {

        pref.edit().putString(ConstantConfig.KEY_USER_NAME, username).commit();
        pref.edit().apply();


    }

    /**
     * clear local user information
     */
    public static void clear() {
        Users.upload_times = 0;
        Users.username = "";
        Users.password = "";
        Users.today = ConstantConfig.KEY_DATE_FORMAT;
        Users.record_times = 0;
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

            if (json.has(ConstantConfig.KEY_USER_TODAY)) {
                String str = json.getString(ConstantConfig.KEY_USER_TODAY);
                if (str != null && str.equals(CustomizedTime.getToday())) {
                    today = str;

                    if (json.has(ConstantConfig.KEY_USER_RECORD_TIMES)) {
                        int counts = json.getInt(ConstantConfig.KEY_USER_RECORD_TIMES);
                        record_times = counts;

                        //update the record if we have better one at local
                        List<File> folderList = new FileWalker().createGroupList();

                        //file the file name that matches today!

                        for (File file : folderList) {

                            updateUserRecords(file);

                        }


                    }
                }

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

    public static void updateUserRecords(File file) {
        String str = file.getName();
        String today = CustomizedTime.getToday();
        if (str.contains(today)) {
            today = today;

            //matches today
            int index = today.length();
            if (index == str.length()) {
                if (record_times == 0)
                    record_times = 1;
            } else {
                int lastTimes = Integer.valueOf(str.substring(index + 1, str.length()));
                if (lastTimes >= record_times)
                    record_times = lastTimes + 1;
            }

        }

    }
}
