package unimelb.cis.spatialanalytics.sensorreader.helps;

import unimelb.cis.spatialanalytics.sensorreader.data.Users;

/**
 * Created by hanl4 on 18/03/2015.
 * Solely used to generate unique file names
 */
public class GenerateFileNames {


    public static String getFileNames()
    {
        if(Users.record_times==0)
            return CustomizedTime.getToday();
        else
            return CustomizedTime.getToday()+"_"+String.format("%03d",Users.record_times);
    }
}
