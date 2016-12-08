package unimelb.cis.spatialanalytics.sensorreader.views;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import unimelb.cis.spatialanalytics.sensorreader.R;
import unimelb.cis.spatialanalytics.sensorreader.data.MakeNotes;

/**
 * Created by hanl4 on 25/03/2015.
 */
public class DialogNoteView extends LinearLayout {

    private String TAG = this.getClass().getSimpleName();
    private View dialoglayout;
    private String s_route;

    public String getS_route() {
        return s_route;
    }

    public DialogNoteView(Context context) {
        super(context);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        dialoglayout = inflater.inflate(R.layout.dialog_route, this, true);

    }


    public boolean validate() {
        TextView errText = (TextView) dialoglayout.findViewById(R.id.textView_errors);
        errText.setText("");
        JSONObject err = new JSONObject();
        boolean flag = true;

        try {

            EditText editText_route = (EditText) dialoglayout.findViewById(R.id.edit_route);
            if (editText_route.getText().toString() == null || editText_route.getText().toString().equals("")) {
                err.put("Route EdidText", "Can Not Be Null");
                flag = false;
            } else
                s_route = editText_route.getText().toString();

            if (s_route == null || s_route.equals("")) {
                err.put("Route Info", "Can Not Be Null");
                flag = false;
            }


            if (flag) {
                MakeNotes.s_route = s_route;

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
