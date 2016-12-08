package unimelb.cis.spatialanalytics.sensorreader.http;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import unimelb.cis.spatialanalytics.sensorreader.config.ConstantConfig;
import unimelb.cis.spatialanalytics.sensorreader.config.URLConfig;
import unimelb.cis.spatialanalytics.sensorreader.data.Users;


/**
 * Make HTTP Request to upload file only
 * Not used in this project but may be useful for future use
 */
public class HTTPFileUploader extends AsyncTask<Object, Void, Object> {
    private final String TAG = this.getClass().getSimpleName();
    private MyExceptionHandler exceptionHandler;

    private File file;
    private String stringPart;
    private int requestCode;
    private OnResponse response;



    public HTTPFileUploader(
            OnResponse response,//passing from host
            File file,//file part of data
            String stringPart,//string part of data
            int requestCode// request code
    )
    {
        this.response=response;
        this.file=file;
        this.stringPart=stringPart;
        this.requestCode=requestCode;
        execute();


    }





    public interface OnResponse
    {
        public void onResponse(Object response, int requestCode);
    }



    public JSONObject uploadFileToServer(File file) {
        try {
            if(file==null)
            {
                return exceptionHandler.getJsonError("file is null");


            }
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(URLConfig.getUploadFileServletURL());
            MultipartEntityBuilder mpEntity = MultipartEntityBuilder.create();
            mpEntity.addPart(ConstantConfig.KEY_FILE_UPLOAD_FILE_DATA, new FileBody(file, ContentType.MULTIPART_FORM_DATA, file.getName()));
            mpEntity.addTextBody(ConstantConfig.KEY_FILE_UPLOAD_STRING_DATA, Users.username);
            post.setEntity(mpEntity.build());
            HttpResponse response = null;
            response = client.execute(post);
            HttpEntity resEntity = response.getEntity();
            String json = EntityUtils.toString(resEntity);
            Log.d("Response", json);
            client.getConnectionManager().shutdown();
            return new JSONObject(json);

        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, e.toString());
            return exceptionHandler.getJsonError(e.toString());
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, e.toString());
            return exceptionHandler.getJsonError(e.toString());


        }


    }


    @Override
    protected Object doInBackground(Object... params) {
        JSONObject json=uploadFileToServer(file);

        return json;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        response.onResponse(o,requestCode);
    }
}
