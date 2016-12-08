package unimelb.cis.spatialanalytics.sensorreader.views;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import unimelb.cis.spatialanalytics.sensorreader.R;
import unimelb.cis.spatialanalytics.sensorreader.data.MakeNotes;

/**
 * Created by hanl4 on 25/03/2015.
 */
public class DialogWeatherView extends LinearLayout {

    private String TAG = this.getClass().getSimpleName();
    private View dialoglayout;
    private Map<String, String> s_activity;

    public Map<String, String> getS_activity() {
        return s_activity;
    }

    public DialogWeatherView(Context context) {
        super(context);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        dialoglayout = inflater.inflate(R.layout.dialog_activity, this, true);

        EditText editText_activity = (EditText) dialoglayout.findViewById(R.id.edit_activity);
        editText_activity.setVisibility(View.INVISIBLE);
    }


    public boolean validate() {
        TextView errText = (TextView) dialoglayout.findViewById(R.id.textView_errors);
        errText.setText("");
        JSONObject err = new JSONObject();
        boolean flag = true;

        try {


            EditText editText_activity = (EditText) dialoglayout.findViewById(R.id.edit_activity);

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


            if (s_activity == null || s_activity.equals("") || s_activity.size() == 0) {
                err.put("Main Activity Info", "Can Not Be Null");
                flag = false;
            }

            if (flag) {
                MakeNotes.s_activity = s_activity;

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
