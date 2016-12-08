package unimelb.cis.spatialanalytics.sensorreader.alarm;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;

import unimelb.cis.spatialanalytics.sensorreader.config.ConstantConfig;
import unimelb.cis.spatialanalytics.sensorreader.config.SettingConfig;
import unimelb.cis.spatialanalytics.sensorreader.fragments.FragmentMainPanel;

/**
 * set up alarm to fire some clock events
 */
public class Alarm extends BroadcastReceiver {
    private final String TAG = this.getClass().getSimpleName();
    private Activity context;
    //private FragmentManager fragmentManager;

    public Alarm(Activity context, @Nullable FragmentManager fragmentManager) {
        this.context = context;
        // this.fragmentManager = fragmentManager;
        SettingConfig.fragmentManager = fragmentManager;
    }

    public Alarm() {

    }


    @Override
    public void onReceive(Context context, Intent intent) {


        if (SettingConfig.fragmentManager == null) {
            Log.e(TAG, "fragmentManager is null");
            return;
        }

        if (context == null) {
            Log.e(TAG, "context is null");
            return;
        }

        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
        wl.acquire();

        // Put here YOUR code.
        int requestCode = intent.getIntExtra(ConstantConfig.ALARM_TYPE, -1);
        String alarmType = "";
        switch (requestCode) {
            case ConstantConfig.ALARM_CODE_DELAY:
                alarmType = "ALARM_CODE_DELAY";
                break;
            case ConstantConfig.ALARM_CODE_REPEAT:
                alarmType = "ALARM_CODE_REPEAT";
                break;
            default:
                Log.e(TAG, "requestCode is wrong! no such case! requestCode = " + requestCode);
                break;
        }


        Log.i(TAG, "alarm is active : " + alarmType);


        FragmentMainPanel fragment;
        Fragment temp = SettingConfig.fragmentManager.findFragmentByTag(String.valueOf(ConstantConfig.FRAGMENT_MAIN_PANEL));

        if (temp == null) {
            Log.e(TAG, "FragmentMainPanel hasn't been initialized yet");

            return;

        } else
            fragment = (FragmentMainPanel) temp;


        switch (requestCode) {
            case ConstantConfig.ALARM_CODE_REPEAT:

                if (FragmentMainPanel.isStart) {
                    String msg = "Already in Sensing Mode and can't start sensing in Auto Mode";
                    Log.i(TAG, msg);
                    if (context != null)
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                    return;
                }
                fragment.initializeSensing();

                break;
            case ConstantConfig.ALARM_CODE_DELAY:
/*                fragment.setButtons(false);
                //first we need to record the pressing stop button time
                File fileStopTime = SensorData.hashSensorDataFiles.get(ConstantConfig.KEY_PRESS_STOP_BUTTON_TIME);
                new FileIO().writePressStopButtonTime(fileStopTime);*/
                //fragment.stopSensingService();
                fragment.showDialogStop(ConstantConfig.STOP_SENSING_MODE_ALARM_DELAY);
                break;
        }


        wl.release();
    }

    /**
     * Under the mode of auto, the app will automatically start collect the data.
     */
    public void setAlarm(int requestCode) {
        if (context == null) {
            Log.e(TAG, "context is null");
            return;
        }
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        PendingIntent alarmIntent;
        Intent intent;


        intent = new Intent(context, Alarm.class).putExtra(ConstantConfig.ALARM_TYPE, requestCode);
        alarmIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        Log.i(TAG, "setAlarm requestCode is : " + requestCode);
        String alarmType = "";
        switch (requestCode) {
            case ConstantConfig.ALARM_CODE_REPEAT:
                alarmType = "ALARM_CODE_REPEAT";
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                calendar.set(Calendar.HOUR_OF_DAY, SettingConfig.getClockHour());
                calendar.set(Calendar.MINUTE, SettingConfig.getClockMinute());
                calendar.set(Calendar.SECOND, 00);

                Log.i(TAG, "set up alarm calender time " + calendar.getTime().toString());
                // With setInexactRepeating(), you have to use one of the AlarmManager interval
                // constants--in this case, AlarmManager.INTERVAL_DAY.
                alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, alarmIntent); // Millisec * Second * Minute


                // Enable {@code SampleBootReceiver} to automatically restart the alarm when the
                // device is rebooted.
                ComponentName receiver = new ComponentName(context, AlarmBootReceiver.class);
                PackageManager pm = context.getPackageManager();

                pm.setComponentEnabledSetting(receiver,
                        PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                        PackageManager.DONT_KILL_APP);


                break;

            case ConstantConfig.ALARM_CODE_DELAY:
                alarmType = "ALARM_CODE_DELAY";

                alarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                        SystemClock.elapsedRealtime() +
                                SettingConfig.getDelayTime() * ConstantConfig.ALARM_DELAY_TIME_UNIT, alarmIntent);
                break;
        }

        Log.i(TAG, "set up alarm : " + alarmType + " ; requestCode : " + requestCode);
    }

    /**
     * cancel an alarm given its unique id code
     *
     * @param requestCode
     */
    public void cancelAlarm(int requestCode) {


        Intent intent = new Intent(context, Alarm.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, requestCode, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        //Log.e(TAG,alarmManager.getNextAlarmClock().toString());
        alarmManager.cancel(sender);

        String alarmType = "";
        switch (requestCode) {
            case ConstantConfig.ALARM_CODE_DELAY:
                alarmType = "ALARM_CODE_DELAY";
                break;
            case ConstantConfig.ALARM_CODE_REPEAT:
                alarmType = "ALARM_CODE_REPEAT";
                break;
            default:
                break;
        }

        Log.i(TAG, "cancel alarm : " + alarmType + ";" + requestCode);


        if (requestCode == ConstantConfig.ALARM_CODE_REPEAT) {
            // Disable {@code SampleBootReceiver} so that it doesn't automatically restart the
            // alarm when the device is rebooted.
            ComponentName receiver = new ComponentName(context, AlarmBootReceiver.class);
            PackageManager pm = context.getPackageManager();

            pm.setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP);

        }
    }
}
