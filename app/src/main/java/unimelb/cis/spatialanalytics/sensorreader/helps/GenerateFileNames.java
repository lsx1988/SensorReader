package unimelb.cis.spatialanalytics.sensorreader.helps;

/**
 * Created by hanl4 on 18/03/2015.
 */
public class GenerateFileNames {
    public static int leavingCounts=0;

    public static String getFileNames()
    {
        if(leavingCounts==0)
            return CustomizedTime.getToday();
        else
            return CustomizedTime.getToday()+"_"+String.valueOf(leavingCounts);
    }
}
