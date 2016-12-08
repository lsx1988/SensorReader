package unimelb.cis.spatialanalytics.sensorreader.views;

import android.widget.RadioGroup;

/**
 * Created by hanl4 on 23/03/2015.
 */
public class CustomRadioGroup {
    private RadioGroup radioGroup;

    public CustomRadioGroup(RadioGroup radioGroup) {
        this.radioGroup = radioGroup;
    }

    public void setEnabled(boolean flag) {
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            radioGroup.getChildAt(i).setEnabled(flag);
        }
    }

    public void check(int id)
    {
        radioGroup.check(id);
    }


    public int getCheckedRadioButtonId()
    {
        return radioGroup.getCheckedRadioButtonId();
    }

    public RadioGroup getRadioGroup()
    {
        return radioGroup;
    }

}
