package unimelb.cis.spatialanalytics.sensorreader.views;

/**
 * Created by hanl4 on 19/03/2015.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.File;
import java.util.List;
import java.util.Map;

import unimelb.cis.spatialanalytics.sensorreader.R;
import unimelb.cis.spatialanalytics.sensorreader.config.ConstantConfig;
import unimelb.cis.spatialanalytics.sensorreader.data.FileListManager;
import unimelb.cis.spatialanalytics.sensorreader.http.MyExceptionHandler;
import unimelb.cis.spatialanalytics.sensorreader.io.FileIO;
import unimelb.cis.spatialanalytics.sensorreader.io.FileWalker;

/**
 * code reference from
 */
public class UploadFileListAdapter3 extends BaseExpandableListAdapter {

    private FileWalker fileWalker = new FileWalker();
    private FileIO fileIO = new FileIO();
    private Activity context;
/*    private Map<File, List<File>> fileCollections;
    private List<File> folderList;*/
    private ExpandableListView expListView;
    private TextView textViewError;

    private final String TAG = this.getClass().getSimpleName();
    private MyExceptionHandler exceptionHandler;

    //indicates upload all children files under a given root have been successfully uploaded to the server.
    private boolean isUploadAllSuccess = true;
    private int fileCounts = 0;
    private CustomizedDialogs customizedDialogs=new CustomizedDialogs();



    public UploadFileListAdapter3(Activity context, TextView textViewError, ExpandableListView expListView, List<File> folderList,
                                  Map<File, List<File>> fileCollections) {
        this.context = context;
 /*       this.fileCollections = fileCollections;
        this.folderList = folderList;*/
        this.expListView=expListView;
        this.textViewError=textViewError;
        this.exceptionHandler=new MyExceptionHandler(TAG,context);
    }



    public void setListValues(List<File> folderList,
                              Map<File, List<File>> fileCollections) {
/*        this.fileCollections = fileCollections;
        this.folderList = folderList;*/
        notifyDataSetInvalidated();
    }

    public Object getChild(int groupPosition, int childPosition) {


        try {
            String s = FileListManager.fileCollections.get(FileListManager.folderList.get(groupPosition)).get(childPosition).getName();

            if (s == null)
                return s;
            String str[] = s.split(ConstantConfig.FILE_NAME_SPLIT_SYMBOL);
            if (str.length > 0)
                return (Object) (str[0]);
            else
                return s;
        } catch (IndexOutOfBoundsException e) {
            Log.e(TAG, e.toString());
            return null;

        }
    }

    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }


    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final String fileName = (String) getChild(groupPosition, childPosition);
        final LayoutInflater inflater = context.getLayoutInflater();

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.child_item, null);
        }

        TextView item = (TextView) convertView.findViewById(R.id.textView_folder_name);

        ImageView viewFile = (ImageView) convertView.findViewById(R.id.view);
        viewFile.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                showFileInfo(groupPosition, childPosition, inflater, fileName);

            }
        });


        item.setText(fileName);

        /**
         * Add delete button at the very bottom
         */

        Button buttonDelete = (Button) convertView.findViewById(R.id.btn_delete);
        buttonDelete.setVisibility(View.GONE);

        if (isLastChild) {
            buttonDelete.setVisibility(View.VISIBLE);
            buttonDelete.setOnClickListener(new OnClickListener() {

                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("Do you want to delete the folder "+ FileListManager.folderList.get(groupPosition).getName()+"? \nPlease notice that, all the files under this folder will be deleted, and the operation can't be recovered anyhow.");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    File file = FileListManager.folderList.get(groupPosition);
                                    if (fileWalker.deleteWalker(file)) {
                                        FileListManager.folderList.remove(file);
                                        //onGroupCollapsed(groupPosition);
                                        expListView.collapseGroup(groupPosition);

                                    }
                                    //fileCollections.remove(folderList.get(groupPosition));
                                    notifyDataSetChanged();
                                }
                            });
                    builder.setNegativeButton("No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            });

        }


        return convertView;
    }

    /**
     * show file information dialog
     * @param groupPosition
     * @param childPosition
     * @param inflater
     * @param fileName
     */
    public void showFileInfo(int groupPosition, int childPosition, LayoutInflater inflater, String fileName) {
        List<File> child =
                FileListManager.fileCollections.get(FileListManager.folderList.get(groupPosition));
        File file = child.get(childPosition);

        //UI
        ScrollView layout = (ScrollView) inflater.inflate(R.layout.dialog_file_info, null);
        TextView textView = (TextView) layout.findViewById(R.id.text_file_info);
        textView.setText(fileIO.readTxt(file));

        final AlertDialog dialog = customizedDialogs.buildSimpleDismissDialog(context);
        dialog.setView(layout);
        dialog.setTitle(fileName);
        dialog.setCancelable(false);
        dialog.show();
        Button button = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        button.setBackgroundColor(Color.parseColor(ConstantConfig.COLOR_DIALOG_DISMISS));
        //button.setBackgroundColor(Color.BLUE);

    }

    public int getChildrenCount(int groupPosition) {
        return FileListManager.fileCollections.get(FileListManager.folderList.get(groupPosition)).size();
    }

    public Object getGroup(int groupPosition) {
        return FileListManager.folderList.get(groupPosition).getName();
    }

    public int getGroupCount() {
        return FileListManager.folderList.size();
    }

    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    public View getGroupView(final int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        final String folderName = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.group_item,
                    null);
        }
        TextView item = (TextView) convertView.findViewById(R.id.textView_folder_name);
        item.setTypeface(null, Typeface.BOLD);
        item.setText(folderName);


        ImageView upload = (ImageView) convertView.findViewById(R.id.upload);
        if (FileListManager.fileCollections.get(FileListManager.folderList.get(groupPosition)).size() > 0)
            upload.setOnClickListener(new OnClickListener() {

                public void onClick(View v) {

                    //upload the files under that folder to the server to the server


                   // UploadFileToServer uploadFileToServer = new UploadFileToServer(context, textViewError, expListView, UploadFileListAdapter.class, groupPosition);


                }


            });


        return convertView;
    }


    /**
     *set processing dialog message
     * @return
     */
    public String setProcessDialogMessage(int i,int total)
    {
        return "Uploading " + i + "/" +total+ " Files.....";
    }


    public boolean hasStableIds() {
        return true;
    }


    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }




    public Map<File, List<File>> getFileCollections() {
        return FileListManager.fileCollections;
    }

    public List<File> getFolderList() {
        return FileListManager.folderList;
    }


}