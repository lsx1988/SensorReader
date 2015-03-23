package unimelb.cis.spatialanalytics.sensorreader.http;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import unimelb.cis.spatialanalytics.sensorreader.config.ConstantConfig;
import unimelb.cis.spatialanalytics.sensorreader.data.FileListManager;
import unimelb.cis.spatialanalytics.sensorreader.data.Users;
import unimelb.cis.spatialanalytics.sensorreader.io.FileWalker;
import unimelb.cis.spatialanalytics.sensorreader.views.CustomizedDialogs;
import unimelb.cis.spatialanalytics.sensorreader.views.UploadFileListAdapter;

/**
 * Created by hanl4 on 20/03/2015.
 *
 * upload files to the server.
 */
public class UploadFileToServer {

    //indicates upload all children files under a given root have been successfully uploaded to the server.
    private boolean isUploadAllSuccess = true;

    private final String TAG = this.getClass().getSimpleName();

    private UploadFileListAdapter expListAdapter;


    private ExpandableListView expListView;
    int groupPosition = -1;
    private MyExceptionHandler exceptionHandler;


    private File folder;
    private int fileCounts = 0;
    private int fileNum;
    private List<File> files;
    private Context context;
    ProgressDialog pDialog;
    private TextView textViewError;

    private boolean isUploadAll=true;

    private FileWalker fileWalker = new FileWalker();


    private CustomizedDialogs customizedDialogs = new CustomizedDialogs();


    public UploadFileToServer(Context context, TextView textViewError,ExpandableListView expListView,  UploadFileListAdapter expListAdapter,int groupPosition) {
        this.context = context;
        this.expListView=expListView;
        this.expListAdapter = expListAdapter;
        this.groupPosition=groupPosition;
        this.textViewError=textViewError;

        folder = FileListManager.folderList.get(groupPosition);
        files = FileListManager.fileCollections.get(folder);
        fileNum = files.size();
        fileCounts = 0;


        if (fileNum == 0) {
            Log.e(TAG, "no files!");

            return;
        }

        fileCounts = 0;
        pDialog = new ProgressDialog(context);
        pDialog.setTitle("Uploading all files under folder "+folder.getName()+" to Server");
        pDialog.setCancelable(false);
        pDialog.setMessage(setProcessDialogMessage(fileCounts, fileNum));
        pDialog.show();

        this.exceptionHandler = new MyExceptionHandler(TAG, context);

        ApplicationController.getInstance().getRequestQueue().start();
        isUploadAll=false;
        uploadOneGroupFiles();


    }


    public UploadFileToServer(Context context, TextView textViewError, UploadFileListAdapter expListAdapter) {
        this.context = context;
        this.expListAdapter = expListAdapter;
        isUploadAll=true;
        this.exceptionHandler = new MyExceptionHandler(TAG, context);
        this.textViewError=textViewError;
        uploadAllFiles();

    }

    private int getTotalFileNum() {
        int count = 0;
        for (File folder : FileListManager.fileCollections.keySet()) {
            List<File> childrenFiles = FileListManager.fileCollections.get(folder);
            count += childrenFiles.size();


        }
        return count;


    }


    private void uploadAllFiles() {
        fileNum = getTotalFileNum();
        if (FileListManager.fileCollections.isEmpty() || fileNum == 0) {
            Log.e(TAG, "no files!");

            return;
        }

        fileCounts = 0;
        pDialog = new ProgressDialog(context);
        pDialog.setTitle("Uploading All Files to Server");
        pDialog.setCancelable(false);
        pDialog.setMessage(setProcessDialogMessage(fileCounts, fileNum));
        pDialog.show();

        ApplicationController.getInstance().getRequestQueue().start();


        groupPosition = -1;
        Set<File> keySet = FileListManager.fileCollections.keySet();


        for (File folderTemp : keySet) {
            groupPosition++;
            folder = folderTemp;
            files = FileListManager.fileCollections.get(folder);

            uploadOneGroupFiles();


        }


    }


    private void uploadOneGroupFiles() {

        Iterator<File> iterator = files.iterator();

        isUploadAllSuccess = true;

        while (iterator.hasNext()) {
            final File file = iterator.next();

            float fileSize = (float) file.length() / ConstantConfig.MB_UNIT;
            if (fileSize > ConstantConfig.MAXIMUM_UPLOAD_FILE_SIZE) {
                fileCounts++;

                String strFileSize = String.format("%.2f", fileSize);
                String alterMssg = file.getName() + " size is " + strFileSize + " exceeds the limit " + ConstantConfig.MAXIMUM_UPLOAD_FILE_SIZE + ", and can't be uploaded to the server!";
                Log.w(TAG, alterMssg);

                closeResources();

                showAlertMessage(alterMssg);
                //showAlertDialog(file.getName() +" size is "+strFileSize +" MB");
                continue;
            }

            //My own defined uploading file HTTP request
            //new CustomizedHTTPRequest(response,file, Users.username,HTTP_REQUEST_CODE);

            //Use google Volley lib to upload an image

            // Tag used to cancel the request

            MultiPartRequest multiPartRequest = new MultiPartRequest(file, Users.username, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    Log.d(TAG, response.toString());

                    fileCounts++;

                    pDialog.setMessage(setProcessDialogMessage(fileCounts, fileNum));

                    if (!response.has(ConstantConfig.KEY_ERROR)) {

                        //if success then delete the local file!
                        //if(false){
                        if (file.delete()) {
                            /*files.remove(file);
                            expListAdapter.notifyDataSetChanged();*/
                            Log.d(TAG, "upload file " + file.getAbsolutePath() + " success and delete success!");


                        } else {
                            isUploadAllSuccess = false;
                            Log.w(TAG, "upload file " + file.getAbsolutePath() + " success and delete failed!");

                        }


                    } else {
                        //mRequestQueue.cancelAll(TAG);
                        //mRequestQueue.stop();
                        exceptionHandler.getToastError("upload file" + file.getName() + " failed!" + response.toString());
                        isUploadAllSuccess = false;


                    }

                    closeResources();
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    //mRequestQueue.cancelAll(TAG);
                    //mRequestQueue.stop();
                    fileCounts++;
                    closeResources();
                    isUploadAllSuccess = false;
                    exceptionHandler.getVolleyError("upload file" + file.getName() + " failed!", error,textViewError);


                }
            });

            //set time out
            int socketTimeout = ConstantConfig.MAXIMUM_TIME_OUT;
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            multiPartRequest.setRetryPolicy(policy);
            multiPartRequest.setTag(TAG);
            // Adding request to request queue
            ApplicationController.getInstance().addToRequestQueue(multiPartRequest, TAG);

        }


    }

    /**
     * will be performed after all the files have been explored. The operations include
     * close progress dialog, check the request queue, etc.
     */
    public void closeResources() {
        if (fileCounts == fileNum) {
            int increase=0;

            pDialog.dismiss();
            ApplicationController.getInstance().getRequestQueue().stop();
            if (isUploadAll) {
                int original=FileListManager.folderList.size();

                //file the file name that matches today!

                for(File file:FileListManager.folderList )
                {

                    Users.updateUserRecords(file);

                }

                FileListManager.folderList = fileWalker.createGroupList();
                int now=FileListManager.folderList.size();
                increase=original-now;
                FileListManager.fileCollections = fileWalker.createCollection();
                expListAdapter.setListValues(FileListManager.folderList, FileListManager.fileCollections);           // expListAdapter.notifyDataSetChanged();

            } else {

                if (folder != null && folder.delete())
                {


                    Users.updateUserRecords(folder);


                    increase=1;
                    FileListManager.folderList.remove(folder);
                    FileListManager.fileCollections.remove(folder);

                    expListAdapter.notifyDataSetChanged();
                    expListView.collapseGroup(groupPosition);

                    Log.d(TAG, "all the children files have been successfully uploaded to the server and the root is deleted!");
                } else
                    Log.w(TAG, "error occurs neither in uploading the children files to server nor root folder deletion!");


            }

            Users.setUpload_times(increase,context,textViewError);


        }

    }




    /**
     * set processing dialog message
     *
     * @return
     */
    public String setProcessDialogMessage(int i, int total) {
        return "Uploading " + i + "/" + total + " Files.....";
    }


    public void showAlertMessage(String title) {

        if (context != null)
            Toast.makeText(context, title, Toast.LENGTH_SHORT).show();

    }

}
