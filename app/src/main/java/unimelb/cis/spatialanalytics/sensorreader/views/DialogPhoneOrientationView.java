package unimelb.cis.spatialanalytics.sensorreader.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import unimelb.cis.spatialanalytics.sensorreader.R;
import unimelb.cis.spatialanalytics.sensorreader.data.MakeNotes;

/**
 * Created by hanl4 on 25/03/2015.
 */
public class DialogPhoneOrientationView extends LinearLayout {

    private String TAG = this.getClass().getSimpleName();
    private View dialoglayout;
    private String s_phone_orient;

    public String getS_phone_orient() {
        return s_phone_orient;
    }

    public DialogPhoneOrientationView(Context context,AttributeSet attrs) {
        super(context,attrs);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        dialoglayout = inflater.inflate(R.layout.dialog_phone_orientation, this, true);

        final EditText editText_phone_orientation = (EditText) dialoglayout.findViewById(R.id.editText_phone_position);
        editText_phone_orientation.setVisibility(View.INVISIBLE);


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




    }


    public boolean validate() {
        TextView errText = (TextView) dialoglayout.findViewById(R.id.textView_errors);
        errText.setText("");
        JSONObject err = new JSONObject();
        boolean flag = true;

        try {


            EditText editText_phone_orientation = (EditText) dialoglayout.findViewById(R.id.editText_phone_position);

            if (editText_phone_orientation.isShown()) {
                if (editText_phone_orientation.getText().toString() == null || editText_phone_orientation.getText().toString().equals("")) {
                    flag = false;
                    err.put("Phone Orientation EdidText", "Can Not Be Null");


                } else
                    s_phone_orient = editText_phone_orientation.getText().toString();
            } else {
                RadioGroup radGrp_orient = (RadioGroup) dialoglayout.findViewById(R.id.radioGroup_phone_orientation);
                int checkedRadioButtonID_orient = radGrp_orient.getCheckedRadioButtonId();
                if (dialoglayout.findViewById(checkedRadioButtonID_orient) == null)
                    flag = false;
                else
                    s_phone_orient = ((RadioButton) dialoglayout.findViewById(checkedRadioButtonID_orient)).getText().toString();
            }


            if (s_phone_orient == null || s_phone_orient.equals("")) {
                err.put("Phone Orient Info", "Can Not Be Null");
                flag = false;
            }
            if (flag) {
                MakeNotes.setS_phone_orient(s_phone_orient);


            } else
                errText.setText(err.toString());


        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.e(TAG, e.toString());
            flag = false;
        }
        return flag;
    }
}
