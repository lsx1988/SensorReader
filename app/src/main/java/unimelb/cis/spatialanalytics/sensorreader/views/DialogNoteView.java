package unimelb.cis.spatialanalytics.sensorreader.views;

import android.content.Context;
import android.util.AttributeSet;
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
    private String s_notes;

    public String getS_notes() {
        return s_notes;
    }

    public DialogNoteView(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        dialoglayout = inflater.inflate(R.layout.dialog_note, this, true);

    }


    public boolean validate(boolean isRequireModify) {
        TextView errText = (TextView) dialoglayout.findViewById(R.id.textView_errors);
        errText.setText("");
        JSONObject err = new JSONObject();
        boolean flag = true;


        EditText editText_note = (EditText) dialoglayout.findViewById(R.id.edit_note);
        s_notes = editText_note.getText().toString();

        if(isRequireModify)
        {

            if (s_notes == null || s_notes.equals("")) {

                flag = false;
                try {
                    err.put("Note Info", "Note can't be empty if you want to make some modifications about your data collection to fix the mistakes during the collection");
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAG, e.toString());
                    flag = false;
                }
            }


        }

        if (flag) {
            MakeNotes.setS_notes(s_notes);

        } else
            errText.setText(err.toString());


        return flag;
    }

    public boolean validate() {
        TextView errText = (TextView) dialoglayout.findViewById(R.id.textView_errors);
        errText.setText("");
        JSONObject err = new JSONObject();
        boolean flag = true;


        EditText editText_note = (EditText) dialoglayout.findViewById(R.id.edit_note);
        MakeNotes.setS_notes(editText_note.getText().toString());


        return flag;
    }

}
