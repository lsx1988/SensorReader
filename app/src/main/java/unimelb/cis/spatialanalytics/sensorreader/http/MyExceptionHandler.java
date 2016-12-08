package unimelb.cis.spatialanalytics.sensorreader.http;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import unimelb.cis.spatialanalytics.sensorreader.config.ConstantConfig;

/**
 * Created by hanl4 on 21/02/2015.
 * Mainly handle the VolleyError (internet)
 */
public class MyExceptionHandler {

    private String TAG = this.getClass().getSimpleName();
    private String parent;
    private Context context;

    private JSONObject errorJson = new JSONObject();//message

    public MyExceptionHandler(String parent,Context context) {
        this.parent = parent;
        this.context=context;
    }


    public void getVolleyError(String errorMsg, VolleyError error,TextView textViewError) {
        if(textViewError==null)
            return ;
        textViewError.setText("");
        if (errorMsg == null || errorMsg.equals(""))
            errorMsg = "Error";


        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
            errorMsg = "TimeoutError or NoConnectionError";

        } else if (error instanceof AuthFailureError) {
            //TODO
            errorMsg = "AuthFailureError";
        } else if (error instanceof ServerError) {
            //TODO
            errorMsg = "ServerError";
        } else if (error instanceof NetworkError) {
            //TODO
            errorMsg = "NetworkError";
        } else if (error instanceof ParseError) {
            //TODO
            errorMsg = "ParseError";

        }

        errorMsg = errorMsg + " ; " + error.toString();

        Log.e(parent, errorMsg);

        //TODO change to other way to deal with error to make it more customized


        if(context==null)
        {
            Log.e(parent, "Context is null");

            if (textViewError != null)
                textViewError.setText(errorMsg);
            else
                Log.e(parent, "TextViewError is null");


        }

        else {
             Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show();
         }


    }


    /**
     * get error message
     *
     */

    public JSONObject getJsonError(String error) {

        try {
            errorJson.put(ConstantConfig.KEY_ERROR, error);
            errorJson.put("tag", parent);
            return errorJson;
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.e(TAG, e.toString());
            return null;
        }


    }


    public void getToastError(String errorMsg)
    {
        Log.e(parent, errorMsg);

        if (context != null)
            Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show();
        else
            Log.e(parent, "Context is null");
    }

}
