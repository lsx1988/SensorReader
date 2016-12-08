package unimelb.cis.spatialanalytics.sensorreader.helps;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import unimelb.cis.spatialanalytics.sensorreader.config.ConstantConfig;

/**
 * Self defined customised time format
 */
public class CustomizedTime {

	public static long getCurrentTimeMillis(){
		return System.currentTimeMillis();
	}

	public static String getToday(){
	    SimpleDateFormat sdf = new SimpleDateFormat(ConstantConfig.KEY_DATE_FORMAT);
		String today = sdf.format(Calendar.getInstance().getTime());

		return today;
	}

    public static String getMillisToDateTime(long milliseconds)
    {

        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,HH:mm:ss");
        Date date = new Date(milliseconds);
        return sdf.format(milliseconds);

    }


}
