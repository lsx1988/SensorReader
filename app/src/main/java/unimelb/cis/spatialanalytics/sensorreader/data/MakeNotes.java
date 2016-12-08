package unimelb.cis.spatialanalytics.sensorreader.data;

import android.util.Log;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import unimelb.cis.spatialanalytics.sensorreader.config.ConstantConfig;
import unimelb.cis.spatialanalytics.sensorreader.config.SettingConfig;
import unimelb.cis.spatialanalytics.sensorreader.http.MyExceptionHandler;

/**
 * Created by hanl4 on 23/03/2015.
 * mainly handle the setting data information
 */
public class MakeNotes {

    private static String s_phone_orient = "";
    private static String s_route = "";
    private static Map<String, String> s_activity = new HashMap<>();
    private static String s_weather = "";
    private static String s_notes = "";
    private static boolean isRequireModify=false;
    private static JSONObject json = new JSONObject();
    private static final String TAG = "MakeNotes";


    /**
     * views
     */

    private static TextView textViewPhoneOrientation;
    private static TextView textViewRoute;
    private static TextView textViewWeather;
    private static TextView textViewActivity;
    private static TextView textViewNote;


    public static void setS_phone_orient(String s) {
        if (s != null && !s.equals("")) {
            s_phone_orient = s;
            if (textViewPhoneOrientation != null)
                textViewPhoneOrientation.setText(s);
            SettingConfig.setNotePhoneOrientation(s_phone_orient);
        }
    }

    /**
     * clear the data information
     */
    public static void clear() {
        Log.i(TAG, "clear()");
        s_phone_orient = "";
        SettingConfig.setNotePhoneOrientation("");
        s_route = "";
        s_activity = new HashMap<>();
        s_weather = "";
        s_notes = "";
        isRequireModify=false;
    }


    /**
     * form JSON data
     */

    public static String formJsonData() {
        try {
            json.put(ConstantConfig.NOTES_PHONE_ORIENTATION, SettingConfig.getNotePhoneOrientation());

            json.put(ConstantConfig.NOTES_WEATHER, s_weather);
            json.put(ConstantConfig.NOTES_ACTIVITY, s_activity);
            json.put(ConstantConfig.NOTES_ROUTE, s_route);
            json.put(ConstantConfig.NOTES_NOTES, s_notes);
            String mode = "";
            if (SettingConfig.getSensingMode() == ConstantConfig.SENSING_MODE_AUTO)
                mode = "auto";
            else if (SettingConfig.getSensingMode() == ConstantConfig.SENSING_MODE_MANUAL)
                mode = "manual";
            else
                mode = "wrong mode";

            json.put(ConstantConfig.SENSING_MODE, mode);
            return json.toString();

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, e.toString());
            return new MyExceptionHandler(TAG, null).getJsonError(e.toString()).toString();

        }
    }


    public static Map<String, String> getS_activity() {
        return s_activity;
    }

    public static void setS_activity(Map<String, String> s_activity) {
        MakeNotes.s_activity = s_activity;
        if (MakeNotes.textViewActivity != null)
            MakeNotes.textViewActivity.setText(s_activity.toString());
    }

    public static String getS_route() {
        return s_route;
    }

    public static void setS_route(String s_route) {
        MakeNotes.s_route = s_route;
        if (MakeNotes.textViewRoute != null)
            MakeNotes.textViewRoute.setText(s_route);
    }

    public static String getS_phone_orient() {
        return s_phone_orient;
    }

    public static String getS_weather() {
        return s_weather;
    }

    public static void setS_weather(String s_weather) {
        MakeNotes.s_weather = s_weather;
        if (MakeNotes.textViewWeather != null)
            MakeNotes.textViewWeather.setText(s_weather);
    }

    public static String getS_notes() {
        return s_notes;
    }

    public static void setS_notes(String s_notes) {
        MakeNotes.s_notes = s_notes;
        if (MakeNotes.textViewNote != null)
            MakeNotes.textViewNote.setText(s_notes);
    }
    public static void setIsRequireModify(boolean isRequireModify)
    {
        MakeNotes.isRequireModify=isRequireModify;
    }
    public static boolean getIsRequireModify()
    {
        return isRequireModify;
    }

    public static void setTextViewPhoneOrientation(TextView textViewPhoneOrientation) {
        MakeNotes.textViewPhoneOrientation = textViewPhoneOrientation;
    }

    public static void setTextViewRoute(TextView textViewRoute) {
        MakeNotes.textViewRoute = textViewRoute;
    }

    public static void setTextViewWeather(TextView textViewWeather) {
        MakeNotes.textViewWeather = textViewWeather;
    }

    public static void setTextViewActivity(TextView textViewActivity) {
        MakeNotes.textViewActivity = textViewActivity;
    }

    public static void setTextViewNote(TextView textViewNote) {
        MakeNotes.textViewNote = textViewNote;
    }




    public static void initializeViews() {
        MakeNotes.textViewPhoneOrientation.setText(SettingConfig.getNotePhoneOrientation());
        MakeNotes.textViewRoute.setText(MakeNotes.s_route);
        MakeNotes.textViewWeather.setText(MakeNotes.s_weather);
        MakeNotes.textViewActivity.setText(MakeNotes.s_activity.toString());
        MakeNotes.textViewNote.setText(MakeNotes.s_notes);
    }

    public static TextView getTextViewPhoneOrientation() {
        return textViewPhoneOrientation;
    }

    public static TextView getTextViewRoute() {
        return textViewRoute;
    }

    public static TextView getTextViewWeather() {
        return textViewWeather;
    }

    public static TextView getTextViewActivity() {
        return textViewActivity;
    }

    public static TextView getTextViewNote() {
        return textViewNote;
    }
}
