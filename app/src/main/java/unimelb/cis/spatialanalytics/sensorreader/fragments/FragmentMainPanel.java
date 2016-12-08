package unimelb.cis.spatialanalytics.sensorreader.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.List;

import unimelb.cis.spatialanalytics.sensorreader.R;
import unimelb.cis.spatialanalytics.sensorreader.alarm.Alarm;
import unimelb.cis.spatialanalytics.sensorreader.config.ConstantConfig;
import unimelb.cis.spatialanalytics.sensorreader.config.SettingConfig;
import unimelb.cis.spatialanalytics.sensorreader.data.FileListManager;
import unimelb.cis.spatialanalytics.sensorreader.data.MakeNotes;
import unimelb.cis.spatialanalytics.sensorreader.data.SensorData;
import unimelb.cis.spatialanalytics.sensorreader.data.Users;
import unimelb.cis.spatialanalytics.sensorreader.io.FileIO;
import unimelb.cis.spatialanalytics.sensorreader.io.FileWalker;
import unimelb.cis.spatialanalytics.sensorreader.sensors.SensingService;
import unimelb.cis.spatialanalytics.sensorreader.sensors.SensingService.OnSensingServiceResult;
import unimelb.cis.spatialanalytics.sensorreader.views.CustomRadioGroup;
import unimelb.cis.spatialanalytics.sensorreader.views.CustomizedButton;
import unimelb.cis.spatialanalytics.sensorreader.views.NoteFragmentDialog;
import unimelb.cis.spatialanalytics.sensorreader.views.UploadFileListAdapter;

/**
 * Sensor control panel
 */
public class FragmentMainPanel extends Fragment implements NoteFragmentDialog.NoteDialogListener, OnSensingServiceResult {
    //UI components
    private CustomizedButton bStart;
    private CustomizedButton bRecord;
    private CustomizedButton bStop;

    private TextView textRecordTime;
    private TextView textFileName;
    private TextView textStatus;

    Alarm alarm;

    private CustomRadioGroup radioGroupMode;

    private String[] statuses = {"Sensors Stopped", "Sensors Running"};

    public static boolean isSuccessBySystem = false;//the sensor collection is performed right or not
    private boolean isSuccessByUser = false;
    public static boolean isStart = false;//is the sensor started?
    //private int leavingCounts = 0;//the number of leaving home within one day!
    private int timeRecordCounts = 0;// the total number of recording time of ONE leaving home action.

    private final String TAG = this.getClass().getSimpleName();

    private final FileIO fileIO = new FileIO();

    private NoteFragmentDialog noteFragmentDialog;
    private FragmentSetting fragmentSetting;

    String dateFolder = SensorData.dateFolder;

    public static SensingService.OnSensingServiceResult onSensingServerResult;


    //flags
    // private boolean isCollectSensorData=false;//indicates whether the sensor is running or not.

    public FragmentMainPanel() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "OnCreate");
        View rootView = inflater.inflate(R.layout.fragment_main_panel, container, false);


        //UI component initialization
        bStart = new CustomizedButton((Button) rootView.findViewById(R.id.btnStart), Color.parseColor("#ff009688"));
        bStop = new CustomizedButton((Button) rootView.findViewById(R.id.btnStop), Color.parseColor("#C11B17"));
        bRecord = new CustomizedButton((Button) rootView.findViewById(R.id.btnRecord), Color.parseColor("#FF6F42"));

        textRecordTime = (TextView) rootView.findViewById(R.id.txtRecordTimes);
        textRecordTime.setText("");
        textFileName = (TextView) rootView.findViewById(R.id.txtFileName);
        textFileName.setText("");
        textStatus = (TextView) rootView.findViewById(R.id.txtStatus);
        textStatus.setText(statuses[0]);

        radioGroupMode = new CustomRadioGroup((RadioGroup) rootView.findViewById(R.id.radioGroup_mode));

        radioGroupMode.check(SettingConfig.getSensingMode());



        onSensingServerResult = this;

        setButtons(false);

        registerAllButtonListeners();

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        alarm = new Alarm(getActivity(), getFragmentManager());
        setSensingMode(SettingConfig.getSensingMode());

    }

    /**
     * set alarm mode
     *
     * @param modeId
     */
    public void setSensingMode(int modeId) {
        //first we need to cancel the delay alarm.
        if(alarm!=null)
            alarm.cancelAlarm(ConstantConfig.ALARM_CODE_DELAY);
        switch (modeId) {
            case ConstantConfig.SENSING_MODE_AUTO:
                //bStop.setEnabled(false);
                if (alarm != null)
                    alarm.setAlarm(ConstantConfig.ALARM_CODE_REPEAT);
                else
                    Log.e(TAG, "alarm is null at setSensingMode");

                break;
            case ConstantConfig.SENSING_MODE_MANUAL:
                if (alarm != null)
                    alarm.cancelAlarm(ConstantConfig.ALARM_CODE_REPEAT);
                else
                    Log.e(TAG, "alarm is null at setSensingMode");
                break;

        }


    }

    public void registerAllButtonListeners() {

        //start recording sensors
        bStart.getButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initializeSensing();
            }
        });


        //record leaving home time
        bRecord.getButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showLeavingHomeDialog();
            }
        });


        //stop sensor collection
        bStop.getButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*NotesDialog notesDialog=new NotesDialog(getActivity());
                notesDialog.showNotesConfig();*/
                //showNoticeDialog();
                if (alarm != null)
                    alarm.cancelAlarm(ConstantConfig.ALARM_CODE_DELAY);
                else
                    Log.e(TAG, "alarm at the bStop is null");
                showDialogStop(ConstantConfig.STOP_SENSING_MODE_MANUAL);
            }
        });


        //radio group
        radioGroupMode.getRadioGroup().setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radio_auto:
                        setSensingMode(ConstantConfig.SENSING_MODE_AUTO);
                        SettingConfig.setSensingMode(checkedId);
                        break;
                    case R.id.radio_manual:
                        setSensingMode(ConstantConfig.SENSING_MODE_MANUAL);
                        SettingConfig.setSensingMode(checkedId);
                        break;

                }
            }
        });


    }

    /**
     * initialize to prepare for the sensing
     */

    public void initializeSensing() {
        SensorData.hasPressedRecordButton = false;
        //start sensors to collect the data
        Log.d(TAG, "start Sensing...");
        isSuccessBySystem = false;

        timeRecordCounts = 0;
        Users.record_times = Users.record_times + 1;


        Intent mIntent = new Intent(getActivity(), SensingService.class);
        getActivity().startService(mIntent);
        setButtons(true);
    }

    /**
     * Show leaving home dialog to record the leaving home time
     *
     * @throws Exception
     */

    public void showLeavingHomeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        if (timeRecordCounts == 0)
            builder.setMessage("Are you about leaving home?");
        else if (timeRecordCounts == 1)
            builder.setMessage("Are you leaving home AND at the door BUT still inside the room?");
        else if (timeRecordCounts == 2)
            builder.setMessage("Are you leaving home AND at the door BUT already outside the room?");
        else if (timeRecordCounts == 3)
            builder.setMessage("Are you leaving home AND walking away from your home?");
        else
            builder.setMessage("It seems like you have made a mistake, please correct it using the note later when you are stopping the collection.");


        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (isStart) {
                    if (timeRecordCounts == 0) {
                        isSuccessBySystem = true;

                        bStop.setEnabled(true);

                        // if (SettingConfig.getSensingMode() == ConstantConfig.SENSING_MODE_AUTO) {
                        setStopAlarm();
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), "Sensing will automatically close in " + SettingConfig.getDelayTime() + " mins. If you want to stop it earlier, you can press the 'Stop' button above.", Toast.LENGTH_SHORT).show();
                        // }
                        //else
                        //bStop.setEnabled(true);

                    }

                    File file;

                    timeRecordCounts = timeRecordCounts + 1;

                    //File file=SensorData.hashSensorDataFiles.get(SensorData.LEAVING_HOME);
                    file = SensorData.hashSensorDataFiles.get(ConstantConfig.KEY_LEAVING_HOME_TIMES);
                    if (fileIO != null)
                        fileIO.recordCurrentTime(file, timeRecordCounts);
                    else
                        Log.e(TAG, "fileIO is null");
                    if (!fileIO.checkFile(file, TAG))
                        return;


                    String[] s = file.getName().split(ConstantConfig.FILE_NAME_SPLIT_SYMBOL);
                    textRecordTime.setText(s[1] + "\n" + fileIO.presentLeavingTime(file));


                    //stopService();
                } else {
                    textRecordTime.setText("service not start yet...");

                }


                //setupAlarm();
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });


        builder.show();
    }


    /**
     * Control the buttons status: enabled or disabled
     *
     * @param isCollectSensorData indicates whether the sensor is running or not.
     */
    public void setButtons(boolean isCollectSensorData) {
        isStart = isCollectSensorData;
        Log.d(TAG, "set buttons to be " + isCollectSensorData);
        if (isCollectSensorData) {
            textStatus.setText(statuses[1]);
            bStart.setEnabled(false);

            bRecord.setEnabled(true);

            if (isSuccessBySystem) {
                bStop.setEnabled(true);
            }




           /*bRecord.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
            bStart.getBackground().setColorFilter(null);
            bStop.getBackground().setColorFilter(null);*/

            /*
            block the control of sensing mode and note method
             */
            radioGroupMode.setEnabled(false);


        } else {
            textStatus.setText(statuses[0]);
            bStart.setEnabled(true);
            bRecord.setEnabled(false);
            bStop.setEnabled(false);
          /*  bRecord.getBackground().setColorFilter(null);
            bStart.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
            bStop.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);*/


             /*
            enable the control of sensing mode and note method
             */
            radioGroupMode.setEnabled(true);


        }


    }


    /**
     * show dialog to user to confirm whether to stop the data collection or not
     *
     * @throws Exception
     */
    public void showDialogStop(int mode) {
        switch (mode) {
            case ConstantConfig.STOP_SENSING_MODE_MANUAL:
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());


                builder.setMessage("Do you want to stop this data collection?");

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //setupAlarm();
                        dialog.dismiss();
                        stopSensingService();
                        confirmDataCollectionResult();

                    }
                });

                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builder.setCancelable(false);

                builder.show();
                break;
            case ConstantConfig.STOP_SENSING_MODE_ALARM_DELAY:
                stopSensingService();
                confirmDataCollectionResult();
                break;

        }


    }


    /**
     * confirm the data collection is success or not by the user
     *
     * @throws Exception
     */
    public void confirmDataCollectionResult() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Is this data set successfully collected? ");
        builder.setMessage("If the choice is no, this set of data will be immediately deleted by the system, and can't be recovered anyhow, and this data collection is finished.");


        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                isSuccessByUser = true;

                //setupAlarm();
                dialog.dismiss();
                showNoticeDialog();

            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                isSuccessByUser = false;
                dialog.dismiss();
                if (SettingConfig.getUserExternalStoragePath() == null) {
                    Log.e(TAG, "SettingConfig.getUserExternalStoragePath() is null");
                    return;
                }
                if (SettingConfig.getUserExternalStoragePath() == null) {
                    Log.e(TAG, "SettingConfig.getUserExternalStoragePath() is null");
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), "The data set delete failed! No such path!", Toast.LENGTH_SHORT).show();
                    return;
                }
                File currentFolder = new File(SettingConfig.getUserExternalStoragePath() + "/" + SensorData.dateFolder);

                if (new FileWalker().deleteWalker(currentFolder)) {
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), "The data set has been deleted successfully", Toast.LENGTH_SHORT).show();

                } else {
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), "The data set delete failed!", Toast.LENGTH_SHORT).show();

                }

                setButtons(false);


                //showNoticeDialog();
            }
        });

        builder.setCancelable(false);

        builder.show();
    }


    /**
     * build the dialog for making some notes
     */
    public void showNoticeDialog() {


       noteFragmentDialog = new NoteFragmentDialog();
        noteFragmentDialog.setCancelable(false);
        // dialog.show();
        if (getActivity() != null)
            noteFragmentDialog.show(getActivity().getSupportFragmentManager(), "NoteFragmentDialog");
        else
            Log.e(TAG, "showNoticeDialog wrong because getActivity() is null!");


     /*   final View dialogNoteEditView = new DialogNoteEditView(getActivity(), null);


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setView(dialogNoteEditView);
        builder.setTitle("Change Settings");

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (SettingConfig.getNotePhoneOrientation() == null || SettingConfig.getNotePhoneOrientation().equals("")) {
                    ((DialogNoteEditView) dialogNoteEditView).setTextViewError("Phone Orientation is a mandatory field, and can't leave null.");
                    return;
                }
                dialog.dismiss();
                sensorCollectionEndClearance();

            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });


        AlertDialog dialog = builder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        dialog.setCancelable(false);
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(Color.parseColor(ConstantConfig.COLOR_DIALOG_BUTTON_OK));
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setBackgroundColor(Color.parseColor(ConstantConfig.COLOR_DIALOG_BUTTON_CANCEL));
        *//*dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });*/






/*        // Create an instance of the dialog fragment and show it
        noticeDialogFragment = new NoticeDialogFragment();
        noticeDialogFragment.setCancelable(false);
        // dialog.show();
        if (getActivity() != null)
            noticeDialogFragment.show(getActivity().getSupportFragmentManager(), "NoticeDialogFragment");
        else
            Log.e(TAG, "showNoticeDialog wrong because getActivity() is null!");*/


    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "onStart");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, "onStop");
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {

        /**
         * Validate the input information
         */
        if (SettingConfig.getNotePhoneOrientation() == null || SettingConfig.getNotePhoneOrientation().equals("")) {
            noteFragmentDialog.getNoteEditView().setTextViewError("Phone Orientation Setting Can NOT be empty");
            return;
        }else if(MakeNotes.getIsRequireModify() && (MakeNotes.getS_notes()==null || MakeNotes.getS_notes().equals("")))
        {
            noteFragmentDialog.getNoteEditView().setTextViewError("Note can't be empty if you want to make some modifications about your data collection to fix the mistakes during the collection.");
            return;
        }
        if (noteFragmentDialog != null)
            noteFragmentDialog.dismiss();
        else
            Log.e(TAG, "noticeDialogFragment is null");

        sensorCollectionEndClearance();


    }

    /**
     * wrap up things after stop sensing and note dialog confirmation.
     */

    public void sensorCollectionEndClearance() {
        //write the sensor data attributes information to local txt file
        File file = SensorData.hashSensorDataFiles.get(ConstantConfig.KEY_NOTES);

        if (fileIO != null)
            //fileIO.writeNotes(file, MakeNotes.formJsonData(), isSuccessBySystem, isSuccessByUser);
            fileIO.writeNotes(file, MakeNotes.formJsonData(), isSuccessBySystem, !MakeNotes.getIsRequireModify());
        else
            Log.e(TAG, "fileIO is null");

        //reset button statuses
        setButtons(false);
        //update the view of UploadFragmentView
        updateFileUploadFragmentView();
    }


    /**
     * Stop sensing service
     */

    public void stopSensingService() {
        dateFolder = SensorData.dateFolder;
        Intent mIntent = new Intent(getActivity(), SensingService.class);
        getActivity().stopService(mIntent);
        textStatus.setText(statuses[0]);

    }


    /**
     * Stop sensor data collection and update
     */
    public void updateFileUploadFragmentView() {


        /*
        Update folder list in file uploading
         */

        if (dateFolder != null && !dateFolder.equals("")) {
            //get the instance of UploadFileFragment
            FragmentUploadFile fragment;
            Fragment temp;
            if (getFragmentManager() != null)
                temp = getFragmentManager().findFragmentByTag(String.valueOf(ConstantConfig.FRAGMENT_UPLOAD_FILE));
            else {
                Log.e(TAG, "getFragmentManager() is null");
                return;
            }

            if (temp == null) {
                Log.e(TAG, "UploadFileFragment hasn't been initialized yet");

                return;

            } else
                fragment = (FragmentUploadFile) temp;

            //get new files under the new folder
            if (SettingConfig.getUserExternalStoragePath() == null) {
                Log.e(TAG, "SettingConfig.getUserExternalStoragePath() is null");
                return;
            }
            File newFolder = new File(SettingConfig.getUserExternalStoragePath() + "/" + dateFolder);
            List<File> fileList = new FileWalker().walk(newFolder, ConstantConfig.KEY_WALK_FILE);

            //get the current values in UploadFileListAdapter


            // UploadFileFragment fragment=((UploadFileFragment)fragmentList.get(ConstantConfig.FRAGMENT_UPLOAD_FILE));
            UploadFileListAdapter adapter = fragment.getExpListAdapter();

            FileListManager.addBoth(adapter, newFolder, fileList);

        }

    }


    /**
     * set up alarm clock to stop the sensing
     */

    public void setStopAlarm() {
        //if (SettingConfig.getSensingMode() == R.id.radio_auto) {
            Log.i(TAG, "setStopAlarm");
            alarm.setAlarm(ConstantConfig.ALARM_CODE_DELAY);
        //}
    }

    @Override
    public void onSensingServiceResult() {

        //   setStopAlarm();
           /* setButtons(false);

            //first we need to record the pressing stop button time
            File fileStopTime = SensorData.hashSensorDataFiles.get(ConstantConfig.KEY_PRESS_STOP_BUTTON_TIME);
            fileIO.writePressStopButtonTime(fileStopTime);
            showDialogStop();*/
    }

    @Override
    public void onResume() {
        super.onResume();

    }
}



