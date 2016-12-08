package unimelb.cis.spatialanalytics.sensorreader.views;

import android.widget.TimePicker;

import unimelb.cis.spatialanalytics.sensorreader.config.SettingConfig;

/**
 * Created by hanl4 on 24/03/2015.
 */
public class CustomizedTimePicker {
    private TimePicker timePicker;
    private static int hour;
    private static int minute;
    public CustomizedTimePicker(TimePicker timePicker)
    {
        this.timePicker=timePicker;



        ini();
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int min) {
                hour=hourOfDay;
                minute=min;
            }
        });

    }


    /**
     * save the times to the pref
     */
    public static boolean setClockTimes()
    {

        return SettingConfig.setClock(hour, minute);
    }

    public int getHour()
    {
        return  hour;
    }

    public int getMinute()
    {
        return minute;
    }

    public TimePicker getTimePicker()
    {
        return this.timePicker;
    }

    /**
     * initialize the values of the clock
     */
    public void ini()
    {
        timePicker.setCurrentHour(SettingConfig.getClockHour());
        timePicker.setCurrentMinute(SettingConfig.getClockMinute());
    }





}


