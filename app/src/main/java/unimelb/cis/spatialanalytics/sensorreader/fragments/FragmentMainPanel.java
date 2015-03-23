package unimelb.cis.spatialanalytics.sensorreader.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.util.List;
import java.util.Map;

import unimelb.cis.spatialanalytics.sensorreader.R;
import unimelb.cis.spatialanalytics.sensorreader.config.ConstantConfig;
import unimelb.cis.spatialanalytics.sensorreader.config.SettingConfig;
import unimelb.cis.spatialanalytics.sensorreader.data.SensorData;
import unimelb.cis.spatialanalytics.sensorreader.data.Users;
import unimelb.cis.spatialanalytics.sensorreader.helps.GenerateFileNames;
import unimelb.cis.spatialanalytics.sensorreader.io.FileIO;
import unimelb.cis.spatialanalytics.sensorreader.io.FileWalker;
import unimelb.cis.spatialanalytics.sensorreader.views.NoticeDialogFragment;
import unimelb.cis.spatialanalytics.sensorreader.services.SensingService;
import unimelb.cis.spatialanalytics.sensorreader.views.UploadFileListAdapter;

/**
 * Sensor control panel
 */
public class FragmentMainPanel extends Fragment implements NoticeDialogFragment.NoticeDialogListener {
    //UI components
    private Button bStart;
    private Button bRecord;
    private Button bStop;

    private TextView textRecordTime;
    private TextView textFileName;
    private TextView textStatus;

    private String[] statuses = {"Sensors Stopped", "Sensors Running"};

    private boolean isSuccessBySystem = false;//the sensor collection is performed right or not
    private boolean isSuccessByUser = false;
    private boolean isStart = false;//is the sensor started?
    private int leavingCounts = 0;//the number of leaving home within one day!
    private int timeRecordCounts = 0;// the total number of recording time of ONE leaving home action.

    private final String TAG = this.getClass().getSimpleName();

    private final FileIO fileIO = new FileIO();

    private NoticeDialogFragment noticeDialogFragment;

    String dateFolder=SensorData.dateFolder;



    //flags
    // private boolean isCollectSensorData=false;//indicates whether the sensor is running or not.

    public FragmentMainPanel() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main_panel, container, false);

        leavingCounts= Users.record_times;
        //UI component initialization
        bStart = (Button) rootView.findViewById(R.id.btnStart);
        bStop = (Button) rootView.findViewById(R.id.btnStop);
        bRecord = (Button) rootView.findViewById(R.id.btnRecord);

        textRecordTime = (TextView) rootView.findViewById(R.id.txtRecordTimes);
        textRecordTime.setText("");
        textFileName = (TextView) rootView.findViewById(R.id.txtFileName);
        textFileName.setText("");
        textStatus = (TextView) rootView.findViewById(R.id.txtStatus);
        textStatus.setText(statuses[0]);
        setButtons(false);

        registerAllButtonListeners();

        return rootView;
    }


    public void registerAllButtonListeners() {

        //start recording sensors
        bStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //start sensors to collect the data
                Log.d(TAG, "start Sensing...");
                isSuccessBySystem = false;
                isStart = true;
                timeRecordCounts = 0;
                GenerateFileNames.leavingCounts = leavingCounts;
                leavingCounts = leavingCounts + 1;


                Intent mIntent = new Intent(getActivity(), SensingService.class);
                getActivity().startService(mIntent);
                setButtons(true);
            }
        });


        //record leaving home time
        bRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showLeavingHomeDialog();
            }
        });


        //stop sensor collection
        bStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*NotesDialog notesDialog=new NotesDialog(getActivity());
                notesDialog.showNotesConfig();*/
                //showNoticeDialog();
                showDialogStop();
            }
        });
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
                    if (timeRecordCounts >= 0 && timeRecordCounts < 4)
                        isSuccessBySystem = true;

                    File file;

                    timeRecordCounts = timeRecordCounts + 1;

                    //File file=SensorData.hashSensorDataFiles.get(SensorData.LEAVING_HOME);
                    file = SensorData.hashSensorDataFiles.get(ConstantConfig.KEY_LEAVING_HOME_TIMES);
                    fileIO.recordCurrentTime(file, timeRecordCounts);


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
        int disabledColor = Color.parseColor("#BDBDBD");
        Log.d(TAG, "set buttons to be " + isCollectSensorData);
        if (isCollectSensorData) {
            textStatus.setText(statuses[1]);
            bStart.setEnabled(false);

            bRecord.setEnabled(true);
            bStop.setEnabled(true);

            bStart.setBackgroundColor(disabledColor);
            bRecord.setBackgroundColor(Color.parseColor("#FF6F42"));
            bStop.setBackgroundColor(Color.parseColor("#C11B17"));

           /*bRecord.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
            bStart.getBackground().setColorFilter(null);
            bStop.getBackground().setColorFilter(null);*/


        } else {
            textStatus.setText(statuses[0]);
            bStart.setEnabled(true);
            bRecord.setEnabled(false);
            bStop.setEnabled(false);
            bStart.setBackgroundColor(Color.parseColor("#ff009688"));
            bRecord.setBackgroundColor(disabledColor);
            bStop.setBackgroundColor(disabledColor);
          /*  bRecord.getBackground().setColorFilter(null);
            bStart.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
            bStop.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);*/

        }


    }


    /**
     * confirm the data collection is success or not by the user
     *
     * @throws Exception
     */
    public void showDialogStop() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());


        builder.setMessage("Is this data set successfully collected?");

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
                showNoticeDialog();
            }
        });


        builder.show();
    }


    public void showNoticeDialog() {


        //first we need to record the pressing stop button time
        File file = SensorData.hashSensorDataFiles.get(ConstantConfig.KEY_PRESS_STOP_BUTTON_TIME);
        fileIO.writePressStopButtonTime(file);


        // Create an instance of the dialog fragment and show it
        noticeDialogFragment = new NoticeDialogFragment();
        // dialog.show();
        noticeDialogFragment.show(getActivity().getSupportFragmentManager(), "NoticeDialogFragment");
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        if (noticeDialogFragment.saveSettingInputs()) {
            File file = SensorData.hashSensorDataFiles.get(ConstantConfig.KEY_NOTES);
            fileIO.writeNotes(file, noticeDialogFragment.getJsonValues(), isSuccessBySystem, isSuccessByUser);
            noticeDialogFragment.dismiss();
            setButtons(false);
            stopCollectData();
            isStart = false;
        }

    }


    public void onCheckboxClicked(View view) {
        if (noticeDialogFragment != null)
            noticeDialogFragment.onCheckboxClicked(view);
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        noticeDialogFragment.dismiss();

    }


    /**
     * Stop sensor data collection
     */
    public void stopCollectData() {

        dateFolder=SensorData.dateFolder;
        Intent mIntent = new Intent(getActivity(), SensingService.class);
        getActivity().stopService(mIntent);

        /*
        Update folder list in file uploading
         */

        if(dateFolder!=null && !dateFolder.equals(""))
        {
            //get the instance of UploadFileFragment
            FragmentUploadFile fragment;
            Fragment temp=getFragmentManager().findFragmentByTag(String.valueOf(ConstantConfig.FRAGMENT_UPLOAD_FILE));

            if(temp==null)
            {
                Log.e(TAG,"UploadFileFragment hasn't been initialized yet");

                return;

            }else
                fragment=(FragmentUploadFile)temp;

            //get new files under the new folder
            File newFolder=new File(SettingConfig.getUserExternalStoragePath()+"/"+dateFolder);
            List<File> fileList=new FileWalker().walk(newFolder,ConstantConfig.KEY_WALK_FILE);

            //get the current values in UploadFileListAdapter


           // UploadFileFragment fragment=((UploadFileFragment)fragmentList.get(ConstantConfig.FRAGMENT_UPLOAD_FILE));
            UploadFileListAdapter adapter=fragment.getExpListAdapter();
            Map<File, List<File>> collections=adapter.getFileCollections();
            List<File> folderList=adapter.getFolderList();
            collections.put(newFolder,fileList);
            folderList.add(newFolder);
            adapter.setListValues(folderList,collections);

        }

    }

}
