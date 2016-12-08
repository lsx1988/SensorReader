package unimelb.cis.spatialanalytics.sensorreader.http;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;

import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import unimelb.cis.spatialanalytics.sensorreader.config.ConstantConfig;
import unimelb.cis.spatialanalytics.sensorreader.config.URLConfig;


/**
 * Created by hanl4 on 21/02/2015.
 * Uploads multiple part file to the server along with text data
 */
public class MultiPartRequest  extends Request<JSONObject> {


    private MultipartEntityBuilder mBuilder = MultipartEntityBuilder.create();
    private final Response.Listener<JSONObject> mListener;
    private File file=null;//File part
    private String TAG=MultiPartRequest.class.getSimpleName();

    private String mStringPart;//String data

    /**
     *
     * @param file image to upload
     * @param mStringPart String data to upload
     * @param reponseListener listener for server or internet connection response
     * @param errorListener error listener
     */
    public MultiPartRequest(
                            File file,
                            String mStringPart,
                            Listener<JSONObject> reponseListener,
                            ErrorListener errorListener
    )
    {
        //default use POST for security
        super(Method.POST, URLConfig.getUploadFileServletURL(), errorListener);
        this.file=file;
        this.mListener = reponseListener;
        this.mStringPart=mStringPart;

        buildMultipartEntity();


    }


    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = super.getHeaders();

        if (headers == null
                || headers.equals(Collections.emptyMap())) {
            headers = new HashMap<String, String>();
        }

        headers.put("Accept", "application/json");

        return headers;
    }

    /**
     * build the multipart entity.
     */
    private void buildMultipartEntity()
    {

        if (mStringPart != null && !mStringPart.equals(""))
        {
            mBuilder.addPart(ConstantConfig.KEY_FILE_UPLOAD_FILE_DATA,  new FileBody(file, ContentType.MULTIPART_FORM_DATA, file.getName()));
            mBuilder.addTextBody(ConstantConfig.KEY_FILE_UPLOAD_STRING_DATA, mStringPart);
        }
        mBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        mBuilder.setLaxMode().setBoundary("xx").setCharset(Charset.forName("UTF-8"));
    }

    @Override
    public String getBodyContentType()
    {
        String contentTypeHeader = mBuilder.build().getContentType().getValue();
        return contentTypeHeader;
    }

   @Override
    public byte[] getBody() throws AuthFailureError
    {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try
        {
            mBuilder.build().writeTo(bos);
        }

        catch (IOException e)
        {
            Log.e(TAG,e.toString());
            VolleyLog.e("IOException writing to ByteArrayOutputStream bos, building the multipart request.");
        }

        return bos.toByteArray();
    }


    /**
     * handle the response from the server / network
     * @param response response from the server / network
     * @return JSONObject
     */
    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response)
    {
        try {
            String jsonString = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers));
            return Response.success(new JSONObject(jsonString),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException je) {
            return Response.error(new ParseError(je));
        }
    }



    @Override
    protected void deliverResponse(JSONObject response)
    {
        mListener.onResponse(response);
    }
}