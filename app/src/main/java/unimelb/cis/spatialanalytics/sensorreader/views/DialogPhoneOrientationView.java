package unimelb.cis.spatialanalytics.sensorreader.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import unimelb.cis.spatialanalytics.sensorreader.R;

/**
 * Created by hanl4 on 25/03/2015.
 */
public class DialogPhoneOrientationView extends LinearLayout {

    public DialogPhoneOrientationView(Context context) {
        super(context);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.dialog_phone_orientation, this, true);
    }
}
