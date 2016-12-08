package unimelb.cis.spatialanalytics.sensorreader.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import unimelb.cis.spatialanalytics.sensorreader.R;

/**
 * Created by hanl4 on 26/03/2015.
 */
public class DialogButtonHorizontalView extends LinearLayout {

    public DialogButtonHorizontalView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.dialog_phone_orientation, this, true);
    }
}
