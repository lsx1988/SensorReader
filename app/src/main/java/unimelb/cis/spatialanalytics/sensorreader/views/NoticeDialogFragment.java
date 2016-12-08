package unimelb.cis.spatialanalytics.sensorreader.views;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import unimelb.cis.spatialanalytics.sensorreader.R;

/**
 * Created by hanl4 on 18/03/2015.
 * Make notes about sensor collection
 */
public class NoticeDialogFragment extends DialogFragment {

    //Activity activity;
    /*
     * Settings
     */
    String s_phone_orient;
    String s_route;
    /*JSONObject s_activity = new JSONObject();*/
    Map<String, String> s_activity = new HashMap<>();
    String s_weather;
    String s_notes;
    JSONObject json = new JSONObject();

    View dialoglayout;

    private final String TAG = this.getClass().getSimpleName();
    private boolean isSuccessSaved = false;

    private String jsonValues;

    // Use this instance of the interface to deliver action events
    NoticeDialogListener mListener;

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface NoticeDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);

        public void onDialogNegativeClick(DialogFragment dialog);
    }

 /*   public NoticeDialogFragment (Activity activity)
    {
        this.activity=activity;
    }*/


    public boolean getIsSuccessSaved() {
        return isSuccessSaved;
    }

    public String getJsonValues() {
        return jsonValues;
    }


    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (NoticeDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Build the dialog and set up the button click handlers
       /* AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("TEST1")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Send the positive button event back to the host activity
                        mListener.onDialogPositiveClick(NoticeDialogFragment.this);
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Send the negative button event back to the host activity
                        mListener.onDialogNegativeClick(NoticeDialogFragment.this);
                    }
                });
        return builder.create();*/


        resetParameters();

        LayoutInflater inflater = getActivity().getLayoutInflater();

        dialoglayout = inflater.inflate(R.layout.dialog_collection_config, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialoglayout);

       /* final AlertDialog dialog = builder.create();
        dialog.show();*/


        final EditText editText_phone_orientation = (EditText) dialoglayout.findViewById(R.id.editText_phone_position);
        editText_phone_orientation.setVisibility(View.INVISIBLE);

        final EditText editText_weather = (EditText) dialoglayout.findViewById(R.id.edit_weather);
        editText_weather.setVisibility(View.INVISIBLE);

        EditText editText_activity = (EditText) dialoglayout.findViewById(R.id.edit_activity);
        editText_activity.setVisibility(View.INVISIBLE);

        RadioGroup radGrp_orient = (RadioGroup) dialoglayout.findViewById(R.id.radioGroup_phone_orientation);
        int checkedRadioButtonID_orient = radGrp_orient.getCheckedRadioButtonId();
        radGrp_orient.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup arg0, int id) {
                switch (id) {
                    case -1:
                        break;
                    case R.id.radio_phone_orientation_1:
                        RadioButton radio = (RadioButton) dialoglayout.findViewById(R.id.radio_phone_orientation_1);
                        s_phone_orient = radio.getText().toString();
                        editText_phone_orientation.setVisibility(View.INVISIBLE);

                    case R.id.radio_phone_orientation_2:
                        radio = (RadioButton) dialoglayout.findViewById(R.id.radio_phone_orientation_2);
                        s_phone_orient = radio.getText().toString();
                        editText_phone_orientation.setVisibility(View.INVISIBLE);

                        break;
                    case R.id.radio_phone_orientation_3:
                        radio = (RadioButton) dialoglayout.findViewById(R.id.radio_phone_orientation_3);
                        s_phone_orient = radio.getText().toString();
                        editText_phone_orientation.setVisibility(View.INVISIBLE);
                        break;
                    case R.id.radio_phone_orientation_4:
                        radio = (RadioButton) dialoglayout.findViewById(R.id.radio_phone_orientation_4);
                        s_phone_orient = radio.getText().toString();
                        editText_phone_orientation.setVisibility(View.INVISIBLE);
                        break;

                    case R.id.radio_phone_orientation_5:
                        radio = (RadioButton) dialoglayout.findViewById(R.id.radio_phone_orientation_5);
                        s_phone_orient = radio.getText().toString();
                        editText_phone_orientation.setVisibility(View.INVISIBLE);
                        break;

                    case R.id.radio_phone_orientation_6:
                        radio = (RadioButton) dialoglayout.findViewById(R.id.radio_phone_orientation_6);
                        s_phone_orient = radio.getText().toString();
                        editText_phone_orientation.setVisibility(View.INVISIBLE);
                        break;

                    case R.id.radio_phone_orientation_7:
                        radio = (RadioButton) dialoglayout.findViewById(R.id.radio_phone_orientation_7);

                        editText_phone_orientation.setVisibility(View.VISIBLE);

                        break;

                    default:
                        break;
                }
            }
        });


        RadioGroup radGrp_weather = (RadioGroup) dialoglayout.findViewById(R.id.radioGroup_weather);
        int checkedRadioButtonID_weather = radGrp_weather.getCheckedRadioButtonId();
        radGrp_weather.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup arg0, int id) {
                switch (id) {
                    case -1:
                        break;
                    case R.id.radio_weather_1:
                        RadioButton radio = (RadioButton) dialoglayout.findViewById(R.id.radio_weather_1);
                        s_weather = radio.getText().toString();
                        editText_weather.setVisibility(View.INVISIBLE);

                        //					Toast.makeText(getApplicationContext(), s_phone_orient,Toast.LENGTH_SHORT).show();
                    case R.id.radio_weather_2:
                        radio = (RadioButton) dialoglayout.findViewById(R.id.radio_weather_2);
                        s_weather = radio.getText().toString();
                        editText_weather.setVisibility(View.INVISIBLE);

                        break;
                    case R.id.radio_weather_3:
                        radio = (RadioButton) dialoglayout.findViewById(R.id.radio_weather_3);
                        s_weather = radio.getText().toString();
                        editText_weather.setVisibility(View.INVISIBLE);
                        break;
                    case R.id.radio_weather_4:
                        radio = (RadioButton) dialoglayout.findViewById(R.id.radio_weather_4);
                        s_weather = radio.getText().toString();
                        editText_weather.setVisibility(View.INVISIBLE);
                        break;

                    case R.id.radio_weather_5:
                        radio = (RadioButton) dialoglayout.findViewById(R.id.radio_weather_5);
                        s_weather = radio.getText().toString();
                        editText_weather.setVisibility(View.VISIBLE);
                        break;


                    default:
                        break;
                }
            }
        });


        Button btnOK = (Button) dialoglayout.findViewById(R.id.btn_OK);

        // Login button Click Event
        btnOK.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                mListener.onDialogPositiveClick(NoticeDialogFragment.this);
                //isSuccessSaved = saveSettingInputs();
              /*  boolean flag = saveSettingInputs();
                if (flag) {
                    dialog.dismiss();


                }*/



            }
        });


        Button btnCancel = (Button) dialoglayout.findViewById(R.id.btn_Cancel);

        // Login button Click Event
        btnCancel.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                //	alertDialog.cancel();
                mListener.onDialogNegativeClick(NoticeDialogFragment.this);
                //dialog.dismiss();

            }
        });


        return builder.create();


    }


    /**
     * reset parameters
     */
    public void resetParameters() {
        s_activity = new Hashtable<>();
        s_weather = "";
        s_phone_orient = "";
        s_notes = "";
        s_route = "";
        json = new JSONObject();

    }


    /**
     * save settings or notes into local txt file
     *
     * @return
     */
    public boolean saveSettingInputs() {
        TextView errText = (TextView) dialoglayout.findViewById(R.id.textView_errors);
        errText.setText("");
        JSONObject err = new JSONObject();
        boolean flag = true;


        try {

            EditText editText_phone_orientation = (EditText) dialoglayout.findViewById(R.id.editText_phone_position);

            EditText editText_weather = (EditText) dialoglayout.findViewById(R.id.edit_weather);
            EditText editText_note = (EditText) dialoglayout.findViewById(R.id.edit_note);

            EditText editText_route = (EditText) dialoglayout.findViewById(R.id.edit_route);

            EditText editText_activity = (EditText) dialoglayout.findViewById(R.id.edit_activity);


            if (editText_phone_orientation.isShown()) {
                if (editText_phone_orientation.getText().toString() == null || editText_phone_orientation.getText().toString().equals("")) {
                    flag = false;
                    err.put("Phone Orientation EdidText", "Can Not Be Null");


                } else
                    s_phone_orient = editText_phone_orientation.getText().toString();
            } else {
                RadioGroup radGrp_orient = (RadioGroup) dialoglayout.findViewById(R.id.radioGroup_phone_orientation);
                int checkedRadioButtonID_orient = radGrp_orient.getCheckedRadioButtonId();
                s_phone_orient = ((RadioButton) dialoglayout.findViewById(checkedRadioButtonID_orient)).getText().toString();
            }


            if (editText_weather.isShown()) {
                if (editText_weather.getText().toString() == null || editText_weather.getText().toString().equals("")) {
                    err.put("Weather EdidText", "Can Not Be Null");
                    flag = false;
                } else
                    s_weather = editText_weather.getText().toString();
            } else {
                RadioGroup radGrp_weather = (RadioGroup) dialoglayout.findViewById(R.id.radioGroup_weather);
                int checkeID = radGrp_weather.getCheckedRadioButtonId();
                s_weather = ((RadioButton) dialoglayout.findViewById(checkeID)).getText().toString();
            }


            if (editText_activity.isShown()) {
                if (editText_activity.getText().toString() == null || editText_activity.getText().toString().equals("")) {
                    err.put("Activity EdidText", "Can Not Be Null");
                    flag = false;
                } else
                    s_activity.put("8", editText_activity.getText().toString());
            }


            CheckBox checkbox;
            checkbox = (CheckBox) dialoglayout.findViewById(R.id.checkBox_activity_1);
            if (checkbox.isChecked())
                s_activity.put("1", checkbox.getText().toString());
            checkbox = (CheckBox) dialoglayout.findViewById(R.id.checkBox_activity_2);
            if (checkbox.isChecked())
                s_activity.put("2", checkbox.getText().toString());

            checkbox = (CheckBox) dialoglayout.findViewById(R.id.checkBox_activity_3);
            if (checkbox.isChecked())
                s_activity.put("3", checkbox.getText().toString());

            checkbox = (CheckBox) dialoglayout.findViewById(R.id.checkBox_activity_4);
            if (checkbox.isChecked())
                s_activity.put("4", checkbox.getText().toString());

            checkbox = (CheckBox) dialoglayout.findViewById(R.id.checkBox_activity_5);
            if (checkbox.isChecked())
                s_activity.put("5", checkbox.getText().toString());

            checkbox = (CheckBox) dialoglayout.findViewById(R.id.checkBox_activity_6);
            if (checkbox.isChecked())
                s_activity.put("6", checkbox.getText().toString());

            checkbox = (CheckBox) dialoglayout.findViewById(R.id.checkBox_activity_7);
            if (checkbox.isChecked())
                s_activity.put("7", checkbox.getText().toString());


            if (editText_route.getText().toString() == null || editText_route.getText().toString().equals("")) {
                err.put("Route EdidText", "Can Not Be Null");
                flag = false;
            } else
                s_route = editText_route.getText().toString();


            if (s_phone_orient == null || s_phone_orient.equals("")) {
                err.put("Phone Orient Info", "Can Not Be Null");
                flag = false;
            }
            if (s_weather == null || s_weather.equals("")) {

                flag = false;
                err.put("Weather Info", "Can Not Be Null");
            }

            if (s_activity == null || s_activity.equals("") || s_activity.size() == 0) {
                err.put("Main Activity Info", "Can Not Be Null");
                flag = false;
            }
            if (s_route == null || s_route.equals("")) {
                err.put("Route Info", "Can Not Be Null");
                flag = false;
            }

            if (flag) {
                json.put("Phone Orient", s_phone_orient);
                json.put("Weather", s_weather);
                json.put("Activities", s_activity);
                json.put("Notes", editText_note.getText().toString());
                json.put("Route", s_route);

                jsonValues = json.toString();


            } else {

                errText.setText(err.toString());
            }


        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.e(TAG, e.toString());
        }


        return flag;

    }


    /**
     * for activity section data check
     *
     * @param view
     */
    public void onCheckboxClicked(View view) {

        EditText editText_activity = (EditText) dialoglayout.findViewById(R.id.edit_activity);

        CheckBox checkbox = (CheckBox) view;
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch (view.getId()) {

            case R.id.checkBox_activity_8:
                if (checked)
                    editText_activity.setVisibility(View.VISIBLE);
                else {
                    if (s_activity.containsKey("8"))
                        s_activity.remove("8");
                    editText_activity.setVisibility(View.INVISIBLE);
                }

                break;


        }
    }


}