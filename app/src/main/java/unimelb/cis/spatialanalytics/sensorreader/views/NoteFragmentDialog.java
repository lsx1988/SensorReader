package unimelb.cis.spatialanalytics.sensorreader.views;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import unimelb.cis.spatialanalytics.sensorreader.R;

/**
 * Created by hanl4 on 18/03/2015.
 * Make notes about sensor collection
 */
public class NoteFragmentDialog extends DialogFragment {


    // Use this instance of the interface to deliver action events
    NoteDialogListener mListener;
    DialogNoteEditView noteEditView;

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface NoteDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);

        // public void onDialogNegativeClick(DialogFragment dialog);
    }


    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (NoteDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Build the dialog and set up the button click handlers


        LayoutInflater inflater = getActivity().getLayoutInflater();

        noteEditView = new DialogNoteEditView(getActivity(), null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Change Settings");
        builder.setView(noteEditView);


        Button btnOK = (Button) noteEditView.findViewById(R.id.all_btn_OK);

        // Login button Click Event
        btnOK.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                mListener.onDialogPositiveClick(NoteFragmentDialog.this);


            }
        });


        Button btnCancel = (Button) noteEditView.findViewById(R.id.all_btn_Cancel);

        // Login button Click Event
        btnCancel.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                //mListener.onDialogNegativeClick(NoteFragmentDialog.this);

            }
        });


        return builder.create();


    }


    /**
     * get   DialogNoteEditView noteEditView  from other classes
     * @return
     */

    public DialogNoteEditView getNoteEditView() {
        return noteEditView;
    }

}