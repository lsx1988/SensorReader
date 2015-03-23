package unimelb.cis.spatialanalytics.sensorreader.helps;

import android.app.Application;
import android.content.Context;

/**
 * Created by hanl4 on 22/03/2015.
 */
public class MyApp extends Application {

    private static Context mContext;
    @Override
    public void onCreate() {
        mContext = getApplicationContext();
        super.onCreate();
    }

    public static Context getContext() {
        return mContext;
    }
}
