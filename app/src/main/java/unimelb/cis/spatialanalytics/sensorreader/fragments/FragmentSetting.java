package unimelb.cis.spatialanalytics.sensorreader.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;

import org.json.JSONObject;

import unimelb.cis.spatialanalytics.sensorreader.R;
import unimelb.cis.spatialanalytics.sensorreader.alarm.Alarm;
import unimelb.cis.spatialanalytics.sensorreader.config.ConstantConfig;
import unimelb.cis.spatialanalytics.sensorreader.config.SettingConfig;
import unimelb.cis.spatialanalytics.sensorreader.data.MakeNotes;
import unimelb.cis.spatialanalytics.sensorreader.views.CustomizedTimePicker;
import unimelb.cis.spatialanalytics.sensorreader.views.DialogActivityView;
import unimelb.cis.spatialanalytics.sensorreader.views.DialogNoteView;
import unimelb.cis.spatialanalytics.sensorreader.views.DialogPhoneOrientationView;
import unimelb.cis.spatialanalytics.sensorreader.views.DialogRouteView;
import unimelb.cis.spatialanalytics.sensorreader.views.DialogWeatherView;


public class FragmentSetting extends Fragment {


    private LinearLayout linearLayoutClockTime;
    private LinearLayout linearLayoutDelayTime;
    private LinearLayout linearLayoutPhoneOrientation;
    private LinearLayout linearLayoutRoute;
    private LinearLayout linearLayoutWeather;
    private LinearLayout linearLayoutActivity;
    private LinearLayout linearLayoutNote;

    private TextView textViewClockTime;
    private TextView textViewDelayTime;


    private View dialoglayout;

    private final String TAG = this.getClass().getSimpleName();

    private final int DIALOG_CLOCK_TIME = 0;
    private final int DIALOG_PHONE_ORIENTATION = 1;
    private final int DIALOG_ROUTE = 2;
    private final int DIALOG_WEATHER = 3;
    private final int DIALOG_ACTIVITY = 4;
    private final int DIALOG_NOTE = 5;


    private int progressBarValue;


    Alarm alarm;

    public FragmentSetting() {
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        alarm = new Alarm(getActivity(), getFragmentManager());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.fragment_setting, container, false);


        linearLayoutClockTime = (LinearLayout) rootView.findViewById(R.id.setting_linear_layout_clock_time);
        linearLayoutDelayTime = (LinearLayout) rootView.findViewById(R.id.setting_linear_layout_delay_time);
        linearLayoutPhoneOrientation = (LinearLayout) rootView.findViewById(R.id.setting_linear_layout_phone_orientation);
        linearLayoutRoute = (LinearLayout) rootView.findViewById(R.id.setting_linear_layout_routes);
        linearLayoutWeather = (LinearLayout) rootView.findViewById(R.id.setting_linear_weather);
        linearLayoutActivity = (LinearLayout) rootView.findViewById(R.id.setting_linear_activities);
        linearLayoutNote = (LinearLayout) rootView.findViewById(R.id.setting_linear_notes);

        textViewClockTime = (TextView) rootView.findViewById(R.id.setting_right_clock_time);
        textViewDelayTime = (TextView) rootView.findViewById(R.id.setting_right_delay_time);
        MakeNotes.setTextViewPhoneOrientation((TextView) rootView.findViewById(R.id.setting_right_phone_orientation));
        MakeNotes.setTextViewRoute((TextView) rootView.findViewById(R.id.setting_right_route));
        MakeNotes.setTextViewWeather((TextView) rootView.findViewById(R.id.setting_right_weather));
        MakeNotes.setTextViewActivity((TextView) rootView.findViewById(R.id.setting_right_activity));
        MakeNotes.setTextViewNote((TextView) rootView.findViewById(R.id.setting_right_notes));

        reSetViews();


        //set up listeners
        linearLayoutClockTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildDialogClockTime();
            }
        });

        linearLayoutDelayTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildDialogDelayTime();
            }
        });

        linearLayoutPhoneOrientation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /**
                 * Build up dialog for phone orientation
                 */
                buildEditingDialog(DIALOG_PHONE_ORIENTATION);
            }
        });

        linearLayoutRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /**
                 * build the dialog for routes
                 */
                buildEditingDialog(DIALOG_ROUTE);
            }
        });

        linearLayoutWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildEditingDialog(DIALOG_WEATHER);
            }
        });

        linearLayoutActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildEditingDialog(DIALOG_ACTIVITY);
            }
        });

        linearLayoutNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /**
                 * build dialog for notes
                 */
                buildEditingDialog(DIALOG_NOTE);
            }
        });


        return rootView;
    }


    /**
     * Reset view values
     */
    public void reSetViews() {

        textViewClockTime.setText(SettingConfig.getClockTimeString());
        textViewDelayTime.setText(SettingConfig.getDelayTime() + " mins");
        MakeNotes.initializeViews();


    }


    /**
     * build dialog for clock
     */

    public void buildDialogClockTime() {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        dialoglayout = inflater.inflate(R.layout.dialog_time_picker, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialoglayout);
        builder.setTitle("Change Clock");

        TimePicker timePicker = (TimePicker) dialoglayout.findViewById(R.id.time_picker);
        CustomizedTimePicker customizedTImePicker = new CustomizedTimePicker(timePicker);


        final Dialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();

        Button btnOK = (Button) dialoglayout.findViewById(R.id.btn_OK);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TextView errText = (TextView) dialoglayout.findViewById(R.id.textView_errors);
                errText.setText("");
                JSONObject err = new JSONObject();
                boolean flag = CustomizedTimePicker.setClockTimes();
                if (!flag) {
                    errText.setText("Set time error! Probably because that pref is null");
                    return;
                }

                textViewClockTime.setText(SettingConfig.getClockTimeString());
                //revise alarm
                if (alarm != null && SettingConfig.getSensingMode()==ConstantConfig.SENSING_MODE_AUTO) {
                    alarm.cancelAlarm(ConstantConfig.ALARM_CODE_REPEAT);
                    alarm.setAlarm(ConstantConfig.ALARM_CODE_REPEAT);
                } else
                    Log.e(TAG, "alarm is null");
                if (dialog != null)
                    dialog.dismiss();
                else
                    Log.e(TAG, "dialog is null");

            }
        });


        Button btnCancel = (Button) dialoglayout.findViewById(R.id.btn_Cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });
    }


    /**
     * build dialog for delay time after pressing recording button
     */

    public void buildDialogDelayTime() {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        dialoglayout = inflater.inflate(R.layout.dialog_delay_time, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialoglayout);
        builder.setTitle("Change Delay Time");

        final SeekBar seekBar = (SeekBar) dialoglayout.findViewById(R.id.seekbar_delay_time);
        seekBar.setMax(ConstantConfig.MAXIMUM_DELAY_TIME - ConstantConfig.MINIMUM_DELAY_TIME);

        seekBar.setProgress(SettingConfig.getDelayTime() - ConstantConfig.MINIMUM_DELAY_TIME);
        final TextView textView = (TextView) dialoglayout.findViewById(R.id.textView_info);
        progressBarValue = 0;
        textView.setText("Current delay time is: " + String.valueOf(progressBarValue + SettingConfig.getDelayTime()) + "/" + ConstantConfig.MAXIMUM_DELAY_TIME + " mins.");

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {


            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                progressBarValue = progresValue;

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                textView.setText("Current delay time is: " + String.valueOf(progressBarValue + ConstantConfig.MINIMUM_DELAY_TIME) + "/" + ConstantConfig.MAXIMUM_DELAY_TIME + " mins.");

            }
        });


        final Dialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();

        Button btnOK = (Button) dialoglayout.findViewById(R.id.btn_OK);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                SettingConfig.setDelayTime(progressBarValue + ConstantConfig.MINIMUM_DELAY_TIME);
                textViewDelayTime.setText(SettingConfig.getDelayTime() + " mins");

                dialog.dismiss();

            }
        });


    }


    /**
     * build dialog for all other left fields
     *
     * @param dialogID
     */
    public void buildEditingDialog(final int dialogID) {
        LayoutInflater inflater = getActivity().getLayoutInflater();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());


        String title;
        switch (dialogID) {
            case DIALOG_PHONE_ORIENTATION:
                title = "Change Phone Orientation";
                dialoglayout = new DialogPhoneOrientationView(getActivity(), null);
                break;

            case DIALOG_ROUTE:
                dialoglayout = new DialogRouteView(getActivity(), null);
                title = "Change Route";
                break;

            case DIALOG_WEATHER:
                dialoglayout = new DialogWeatherView(getActivity(), null);
                title = "Change Weather";
                break;

            case DIALOG_ACTIVITY:
                dialoglayout = new DialogActivityView(getActivity(), null);
                title = "Change Activity";
                break;

            case DIALOG_NOTE:
                dialoglayout = new DialogNoteView(getActivity(), null);
                title = "Change Note";
                break;

            case DIALOG_CLOCK_TIME:
                title = "Change Clock Time";
                break;
            default:
                Log.e(TAG, "dialog is not matched!");
                return;

        }
        builder.setView(dialoglayout);
        builder.setTitle(title);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });


        final AlertDialog dialog = builder.create();

        dialog.setCancelable(false);
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(Color.parseColor(ConstantConfig.COLOR_DIALOG_BUTTON_OK));
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setBackgroundColor(Color.parseColor(ConstantConfig.COLOR_DIALOG_BUTTON_CANCEL));
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (dialogID) {
                    case DIALOG_PHONE_ORIENTATION:
                        if (((DialogPhoneOrientationView) dialoglayout).validate()) {
                            dialog.dismiss();
                        }
                        break;

                    case DIALOG_ROUTE:
                        if (((DialogRouteView) dialoglayout).validate()) {
                            dialog.dismiss();
                            if (MakeNotes.getTextViewRoute() != null)
                                MakeNotes.getTextViewRoute().setText(MakeNotes.getS_route());
                        }
                        break;

                    case DIALOG_WEATHER:
                        if (((DialogWeatherView) dialoglayout).validate()) {
                            dialog.dismiss();
                            if (MakeNotes.getTextViewWeather() != null)
                                MakeNotes.getTextViewWeather().setText(MakeNotes.getS_weather());
                        }
                        break;

                    case DIALOG_ACTIVITY:
                        if (((DialogActivityView) dialoglayout).validate()) {
                            dialog.dismiss();
                            if (MakeNotes.getTextViewActivity() != null)
                                MakeNotes.getTextViewActivity().setText(MakeNotes.getS_activity().toString());
                        }

                        break;

                    case DIALOG_NOTE:
                        if (((DialogNoteView) dialoglayout).validate()) {
                            dialog.dismiss();
                            if (MakeNotes.getTextViewNote() != null)
                                MakeNotes.getTextViewNote().setText(MakeNotes.getS_notes());
                        }
                        break;


                    default:
                        break;
                }
            }
        });

    }


}




