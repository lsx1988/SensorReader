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
public class DialogWeatherView extends LinearLayout {

    private String TAG = this.getClass().getSimpleName();
    private View dialoglayout;
    private String s_weather;

    public String getS_weather() {
        return s_weather;
    }

    public DialogWeatherView(Context context, AttributeSet attrs) {
        super(context,attrs);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        dialoglayout = inflater.inflate(R.layout.dialog_weather, this, true);
        final EditText editText_weather = (EditText) dialoglayout.findViewById(R.id.edit_weather);
        editText_weather.setVisibility(View.INVISIBLE);


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

                        //					Toast.makeText(getApplicationgetActivity()(), s_phone_orient,Toast.LENGTH_SHORT).show();
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


    }


    public boolean validate() {
        TextView errText = (TextView) dialoglayout.findViewById(R.id.textView_errors);
        errText.setText("");
        JSONObject err = new JSONObject();
        boolean flag = true;

        try {


            EditText editText_weather = (EditText) dialoglayout.findViewById(R.id.edit_weather);
            if (editText_weather.isShown()) {
                if (editText_weather.getText().toString() == null || editText_weather.getText().toString().equals("")) {
                    err.put("Weather EdidText", "Can Not Be Null");
                    flag = false;
                } else
                    s_weather = editText_weather.getText().toString();
            } else {
                RadioGroup radGrp_weather = (RadioGroup) dialoglayout.findViewById(R.id.radioGroup_weather);
                int checkeID = radGrp_weather.getCheckedRadioButtonId();
                if (dialoglayout.findViewById(checkeID) == null)
                    flag = false;
                else
                    s_weather = ((RadioButton) dialoglayout.findViewById(checkeID)).getText().toString();
            }

            if (s_weather == null || s_weather.equals("")) {

                flag = false;
                err.put("Weather Info", "Can Not Be Null");
            }

            if (flag) {
                MakeNotes.setS_weather (s_weather);

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
