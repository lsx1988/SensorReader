package unimelb.cis.spatialanalytics.sensorreader.config;

public class URLConfig {


/*  private static String ip = "http://128.250.26.36";
    private static String tomPort = "8080";*/

 /*   private static String ip = "http://spatialanalytics.cis.unimelb.edu.au";
    private static String tomPort = "8082";*/

    private static String ip = "http://192.168.56.1";
    private static String tomPort = "8080";

    private static String serverProjectName = "SensorReader";

    private static String loginServlet = "LoginServlet";
    private static String uploadFileServlet = "UploadFileServlet";
    private static String couchDBHandlerServlet = "CouchDBHandlerServlet";


    public static String getUploadFileServletURL() {
        return ip + ":" + tomPort + "/" + serverProjectName + "/" + uploadFileServlet;

    }


    public static String getCouchDBAPI() {
        return ip + ":" + tomPort + "/" + serverProjectName + "/" + couchDBHandlerServlet;

    }

    public static String getLoginServlet()
    {

        return ip + ":" + tomPort + "/" + serverProjectName + "/" + loginServlet;

    }

}
