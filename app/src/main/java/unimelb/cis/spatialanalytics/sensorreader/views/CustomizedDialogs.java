package unimelb.cis.spatialanalytics.sensorreader.views;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by hanl4 on 21/03/2015.
 * Assemble different kinds of dialogs
 */
public class CustomizedDialogs {

    /**
     * simple dialgo with Dismiss only
     *
     * @return alter dialog
     */
    public  AlertDialog buildSimpleDismissDialog(Context context) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder
                //.setTitle(title)
                .setPositiveButton("DISMISS", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE ZE MISSILES!
                        dialog.dismiss();
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();


    }
}
