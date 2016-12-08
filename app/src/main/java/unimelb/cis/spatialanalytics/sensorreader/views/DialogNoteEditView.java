package unimelb.cis.spatialanalytics.sensorreader.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import unimelb.cis.spatialanalytics.sensorreader.R;
import unimelb.cis.spatialanalytics.sensorreader.config.SettingConfig;
import unimelb.cis.spatialanalytics.sensorreader.data.MakeNotes;

/**
 * Created by hanl4 on 25/03/2015.
 */
public class DialogNoteEditView extends LinearLayout {

    private String TAG = this.getClass().getSimpleName();
    private View rootView;
    private View dialogLayout;//the view of dialog
    private DialogButtonHorizontalView buttonView;


    private LinearLayout linearLayout0;//radioGroup
    private LinearLayout linearLayout1;
    private LinearLayout linearLayout2;
    private LinearLayout linearLayout3;
    private LinearLayout linearLayout4;
    private LinearLayout linearLayout5;

    private LinearLayout linearLayoutPhoneOrientation;
    private LinearLayout linearLayoutRoute;
    private LinearLayout linearLayoutWeather;
    private LinearLayout linearLayoutActivity;
    private LinearLayout linearLayoutNote;
    private LinearLayout linearLayoutButtons;


    private DialogPhoneOrientationView dialogPhoneOrientationView;
    private DialogRouteView dialogRouteView;
    private DialogWeatherView dialogWeatherView;
    private DialogActivityView dialogActivityView;
    private DialogNoteView dialogNoteView;

    private TextView textViewPhoneOrientation;
    private TextView textViewRoute;
    private TextView textViewWeather;
    private TextView textViewActivity;
    private TextView textViewNote;

    private TextView textViewError;

    private final int DIALOG_PHONE_ORIENTATION = 1;
    private final int DIALOG_ROUTE = 2;
    private final int DIALOG_WEATHER = 3;
    private final int DIALOG_ACTIVITY = 4;
    private final int DIALOG_NOTE = 5;

    //radio group
    private RadioGroup radioGroup;


    boolean isEditing = false;

    public boolean getIsEditing() {
        return isEditing;
    }

    /**
     * Values
     */

    String s_phone_orient;
    String s_route;
    /*JSONObject s_activity = new JSONObject();*/
    Map<String, String> s_activity = new HashMap<>();
    String s_weather;
    String s_notes;
    JSONObject json = new JSONObject();


    private Context context;


    public String getS_route() {
        return s_route;
    }

    public DialogNoteEditView(final Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rootView = inflater.inflate(R.layout.dialog_setting, this, true);

        this.context = context;

        linearLayout0 = (LinearLayout) rootView.findViewById(R.id.setting_linear_0);
        linearLayout1 = (LinearLayout) rootView.findViewById(R.id.setting_linear_1);
        linearLayout2 = (LinearLayout) rootView.findViewById(R.id.setting_linear_2);
        linearLayout3 = (LinearLayout) rootView.findViewById(R.id.setting_linear_3);
        linearLayout4 = (LinearLayout) rootView.findViewById(R.id.setting_linear_4);
        linearLayout5 = (LinearLayout) rootView.findViewById(R.id.setting_linear_5);


        linearLayoutPhoneOrientation = (LinearLayout) rootView.findViewById(R.id.setting_linear_layout_phone_orientation);
        linearLayoutRoute = (LinearLayout) rootView.findViewById(R.id.setting_linear_layout_routes);
        linearLayoutWeather = (LinearLayout) rootView.findViewById(R.id.setting_linear_weather);
        linearLayoutActivity = (LinearLayout) rootView.findViewById(R.id.setting_linear_activities);
        linearLayoutNote = (LinearLayout) rootView.findViewById(R.id.setting_linear_notes);
        linearLayoutButtons = (LinearLayout) rootView.findViewById(R.id.linear_layout_buttons);

        dialogPhoneOrientationView = (DialogPhoneOrientationView) rootView.findViewById(R.id.dialog_phone_orientation);
        dialogRouteView = (DialogRouteView) rootView.findViewById(R.id.dialog_route);
        dialogWeatherView = (DialogWeatherView) rootView.findViewById(R.id.dialog_weather);
        dialogActivityView = (DialogActivityView) rootView.findViewById(R.id.dialog_activity);
        dialogNoteView = (DialogNoteView) rootView.findViewById(R.id.dialog_note);

        textViewPhoneOrientation = (TextView) rootView.findViewById(R.id.setting_right_phone_orientation);
        textViewRoute = (TextView) rootView.findViewById(R.id.setting_right_route);
        textViewWeather = (TextView) rootView.findViewById(R.id.setting_right_weather);
        textViewActivity = (TextView) rootView.findViewById(R.id.setting_right_activity);
        textViewNote = (TextView) rootView.findViewById(R.id.setting_right_notes);

        textViewError = (TextView) rootView.findViewById(R.id.textView_error);

        radioGroup=(RadioGroup)rootView.findViewById(R.id.radioGroup);


        setViews();


        linearLayoutPhoneOrientation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /**
                 * Build up dialog for phone orientation
                 */
                buttonView = new DialogButtonHorizontalView(context, null);
                dialogPhoneOrientationView.addView(buttonView);
                presentEditModel(DIALOG_PHONE_ORIENTATION, dialogPhoneOrientationView, textViewPhoneOrientation);


            }
        });

        linearLayoutRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /**
                 * build the dialog for routes
                 */
                buttonView = new DialogButtonHorizontalView(context, null);
                dialogRouteView.addView(buttonView);
                presentEditModel(DIALOG_ROUTE, dialogRouteView, textViewRoute);
            }
        });

        linearLayoutWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonView = new DialogButtonHorizontalView(context, null);
                dialogWeatherView.addView(buttonView);
                presentEditModel(DIALOG_WEATHER, dialogWeatherView, textViewWeather);
            }
        });

        linearLayoutActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * build dialog for activities
                 */

                buttonView = new DialogButtonHorizontalView(context, null);
                dialogActivityView.addView(buttonView);
                presentEditModel(DIALOG_ACTIVITY, dialogActivityView, textViewActivity);
            }
        });

        linearLayoutNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /**
                 * build dialog for notes
                 */
                buttonView = new DialogButtonHorizontalView(context, null);
                dialogNoteView.addView(buttonView);
                presentEditModel(DIALOG_NOTE, dialogNoteView, textViewNote);
            }
        });

        /**
         * radio group listener
         */
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                MakeNotes.setIsRequireModify(checkedId==R.id.radio_yes);

            }
        });


    }

    /**
     * Reset view values
     */
    public void setViews() {

        //clear MakeNote one field of data
        if(MakeNotes.getIsRequireModify())
            MakeNotes.setS_notes("");
        MakeNotes.setIsRequireModify(false);

        textViewPhoneOrientation.setText(SettingConfig.getNotePhoneOrientation());
        textViewRoute.setText(MakeNotes.getS_route());
        textViewWeather.setText(MakeNotes.getS_weather());
        textViewActivity.setText(MakeNotes.getS_activity().toString());
        textViewNote.setText(MakeNotes.getS_notes());
        textViewError.setText("");

        radioGroup.check(R.id.radio_no);



        dialogPhoneOrientationView.setVisibility(View.GONE);
        dialogRouteView.setVisibility(View.GONE);
        dialogWeatherView.setVisibility(View.GONE);
        dialogActivityView.setVisibility(View.GONE);
        dialogNoteView.setVisibility(View.GONE);

    }

    public void setEditingMode(boolean flag) {

        isEditing = flag;

        if (flag) {
            linearLayout0.setVisibility(View.GONE);
            linearLayout1.setVisibility(View.GONE);
            linearLayout2.setVisibility(View.GONE);
            linearLayout3.setVisibility(View.GONE);
            linearLayout4.setVisibility(View.GONE);
            linearLayout5.setVisibility(View.GONE);
            linearLayoutButtons.setVisibility(View.GONE);
            textViewError.setVisibility(View.GONE);
            textViewError.setText("");


        } else {
            linearLayout0.setVisibility(View.VISIBLE);
            linearLayout1.setVisibility(View.VISIBLE);
            linearLayout2.setVisibility(View.VISIBLE);
            linearLayout3.setVisibility(View.VISIBLE);
            linearLayout4.setVisibility(View.VISIBLE);
            linearLayout5.setVisibility(View.VISIBLE);
            linearLayoutButtons.setVisibility(View.VISIBLE);
            textViewError.setVisibility(View.VISIBLE);

        }
    }


    /**
     * present the dialog for editing
     *
     * @param dialogID
     * @param dialogLayout
     * @param textView
     */

    public void presentEditModel(final int dialogID, final View dialogLayout, final TextView textView) {


        setEditingMode(true);


        Button btnOK = (Button) dialogLayout.findViewById(R.id.btn_OK);
        Button btnCancel = (Button) dialogLayout.findViewById(R.id.btn_Cancel);

        dialogLayout.setVisibility(View.VISIBLE);


        btnOK.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean check = true;
                String result = "";

                switch (dialogID) {
                    case DIALOG_PHONE_ORIENTATION:
                        check = ((DialogPhoneOrientationView) dialogLayout).validate();
                        result = SettingConfig.getNotePhoneOrientation();
                        if (check)
                            ((DialogPhoneOrientationView) dialogLayout).removeView(buttonView);
                        break;

                    case DIALOG_ROUTE:
                        check = ((DialogRouteView) dialogLayout).validate();
                        result = MakeNotes.getS_route();
                        if (check)
                            ((DialogRouteView) dialogLayout).removeView(buttonView);
                        break;

                    case DIALOG_WEATHER:
                        check = ((DialogWeatherView) dialogLayout).validate();
                        result = MakeNotes.getS_weather();
                        if (check)
                            ((DialogWeatherView) dialogLayout).removeView(buttonView);
                        break;

                    case DIALOG_ACTIVITY:
                        check = ((DialogActivityView) dialogLayout).validate();
                        result = MakeNotes.getS_activity().toString();
                        if (check)
                            ((DialogActivityView) dialogLayout).removeView(buttonView);
                        break;

                    case DIALOG_NOTE:
                        //if radioGroup is checked on NO, then note can't be empty
                        boolean isRequireModify=radioGroup.getCheckedRadioButtonId()==R.id.radio_yes;
                        MakeNotes.setIsRequireModify(isRequireModify);
                        check = ((DialogNoteView) dialogLayout).validate(isRequireModify);
                        result = MakeNotes.getS_notes();
                        if (check)
                            ((DialogNoteView) dialogLayout).removeView(buttonView);
                        break;


                    default:
                        break;
                }


                if (check) {

                    dialogLayout.setVisibility(View.GONE);
                    setEditingMode(false);
                    textView.setText(result);

                }
            }
        });

        btnCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogLayout.setVisibility(View.GONE);
                setEditingMode(false);

                switch (dialogID) {
                    case DIALOG_PHONE_ORIENTATION:
                        ((DialogPhoneOrientationView) dialogLayout).removeView(buttonView);
                        break;

                    case DIALOG_ROUTE:
                        ((DialogRouteView) dialogLayout).removeView(buttonView);
                        break;

                    case DIALOG_WEATHER:
                        ((DialogWeatherView) dialogLayout).removeView(buttonView);
                        break;

                    case DIALOG_ACTIVITY:
                        ((DialogActivityView) dialogLayout).removeView(buttonView);
                        break;

                    case DIALOG_NOTE:
                        ((DialogNoteView) dialogLayout).removeView(buttonView);
                        break;


                    default:
                        break;
                }
            }
        });


    }


    /**
     * set the error message for textViewError
     *
     * @param error
     */

    public void setTextViewError(String error) {
        if (textViewError != null)
            textViewError.setText(error);
    }


}
