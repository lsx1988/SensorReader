package unimelb.cis.spatialanalytics.sensorreader.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import unimelb.cis.spatialanalytics.sensorreader.MainActivity;
import unimelb.cis.spatialanalytics.sensorreader.R;
import unimelb.cis.spatialanalytics.sensorreader.config.ConstantConfig;
import unimelb.cis.spatialanalytics.sensorreader.config.ForceCloseExceptionHandler;
import unimelb.cis.spatialanalytics.sensorreader.config.SettingConfig;
import unimelb.cis.spatialanalytics.sensorreader.config.URLConfig;
import unimelb.cis.spatialanalytics.sensorreader.data.Users;
import unimelb.cis.spatialanalytics.sensorreader.http.ApplicationController;
import unimelb.cis.spatialanalytics.sensorreader.http.CustomRequest;
import unimelb.cis.spatialanalytics.sensorreader.http.MyExceptionHandler;
import unimelb.cis.spatialanalytics.sensorreader.sensors.SensingService;

public class LoginActivity extends ActionBarActivity {

    private Button btnLogin;
    private EditText editTextUsername;
    private EditText editTextPasswords;
    private TextView textViewError;
    private String TAG = this.getClass().getSimpleName();
    private MyExceptionHandler exceptionHandler;

    private SharedPreferences pref;

    private String username;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new ForceCloseExceptionHandler(this));
        exceptionHandler = new MyExceptionHandler(TAG, getApplicationContext());
        //ini
        pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SettingConfig.ini(pref);

        Users.device= android.os.Build.MODEL;



        if (Users.isUserLoggedInBefore(pref)) {

            //get username from pref
            final boolean isTrue = Users.syncLocalFromPref(pref);
            if (!isTrue) {
                iniViews();
                return;
            }else {
                launchMainActivity();
                return;
            }



            //user has logged in before, and then update the local user information from the server

            //make http request to the server to check the user information

            //@Deprecated
                    //not longer support the following
           /* Map<String, String> params = new HashMap<String, String>();
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

                            *//**
                             * Self defined login function
                             *//*
                            if (!json.has(ConstantConfig.KEY_ERROR)) {
                                //user successfully logged in
                                //record user information into system
                                Users.syncLocalFromServer(json);
                                //Users.setUserNameFromSharedPreferences(pref,username);

                                SettingConfig.setUserExternalStoragePath();


                                //Launch MainActivity Screen

                                launchMainActivity();

                                // Close Login Screen
                                finish();
                            } else {
                                // Error in login
                                iniViews();
                                Log.e(TAG, "Connect to the server error, please manually login!" + json.toString());
                            }

                            ApplicationController.getInstance().getRequestQueue().stop();

                        }
                    },
                    ///////////////////////////////////////////////////////////////////////////////////////
                    //////////////////////////////////// fifth parameter ///////////////////////////
                    new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            iniViews();
                            exceptionHandler.getVolleyError("login failed", error, textViewError);
                            ApplicationController.getInstance().getRequestQueue().stop();


                        }
                    }
                    ///////////////////////////////////////////////////////////////////////////////////////
            );
            // Adding request to request queue
            ApplicationController.getInstance().addToRequestQueue(customRequest, TAG);*/

        } else {
            iniViews();
        }


    }

    /**
     * iniViews
     */
    public void iniViews() {
        Log.i(TAG,"login panel interface");
        setContentView(R.layout.activity_login);


        btnLogin = (Button) findViewById(R.id.btnLogin);
        editTextUsername = (EditText) findViewById(R.id.login_username);
        editTextPasswords = (EditText) findViewById(R.id.loginPassword);
        textViewError = (TextView) findViewById(R.id.login_error);
    }


    /**
     * When user presses login button.
     */

    public void login(View view) {


        username = editTextUsername.getText().toString();
        password = editTextPasswords.getText().toString();
        textViewError.setText("");

        if (username.trim().equals("")) {
            textViewError.setText("Username can't be empty!");
            return;
        }
        if (password.trim().equals("")) {
            textViewError.setText("Password can't be empty!");
            return;
        }

        /**
         * get device information
         */





        //make http request to the server to check the user information
        Map<String, String> params = new HashMap<String, String>();
        params.put(ConstantConfig.KEY_HTTP_ACTION, ConstantConfig.HTTP_ACTION_LOGIN);
        params.put(ConstantConfig.KEY_COUCHDB_DOC_ID, username);
        params.put(ConstantConfig.KEY_USER_PASSWORD, password);

        String url = URLConfig.getLoginServlet();//get doc
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

                                //user successfully logged in
                                //record user information into system
                                Users.syncLocalFromServer(json);
                                SettingConfig.setUserExternalStoragePath();
                                Users.setLoginStatus(pref, true);
                                Users.setLocalPrefUserInfo(pref);
                                InputMethodManager imm = (InputMethodManager)
                                        getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(editTextUsername.getWindowToken(), 0);
                                imm.hideSoftInputFromWindow(editTextUsername.getWindowToken(), 0);

                                //Launch MainActivity Screen

                                launchMainActivity();

                                // Close Login Screen
                                finish();


                            } else {
                                // Error in login
                                textViewError.setText(json.toString());
                            }

                        ApplicationController.getInstance().getRequestQueue().stop();

                    }
                },
                ///////////////////////////////////////////////////////////////////////////////////////
                //////////////////////////////////// fifth parameter ///////////////////////////
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                        exceptionHandler.getVolleyError("login failed", error, textViewError);
                        ApplicationController.getInstance().getRequestQueue().stop();


                    }
                }
                ///////////////////////////////////////////////////////////////////////////////////////
        );
        // Adding request to request queue
        ApplicationController.getInstance().addToRequestQueue(customRequest, TAG);
    }


    /**
     * launch main activity after successfully log in
     */
    private void launchMainActivity() {


        //secure to close the service if it is not set up.
        Intent service = new Intent(getApplicationContext(), SensingService.class);
        stopService(service);


        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();//close login view.
    }


}
