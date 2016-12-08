package unimelb.cis.spatialanalytics.sensorreader.views;

import android.graphics.Color;
import android.widget.Button;

/**
 * Created by hanl4 on 25/03/2015.
 */
public class CustomizedButton {
    private Button button;
    private int disabledColor = Color.parseColor("#BDBDBD");
    private int enabledColor;

    public CustomizedButton(Button button, int enabledColor) {
        this.button = button;
        this.enabledColor = enabledColor;
    }

    public Button getButton() {
        return button;
    }

    public void setEnabled(boolean flag) {
        button.setEnabled(flag);
        if (flag)
            button.setBackgroundColor(enabledColor);
        else
            button.setBackgroundColor(disabledColor);

    }
}
