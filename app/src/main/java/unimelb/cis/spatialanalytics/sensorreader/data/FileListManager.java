package unimelb.cis.spatialanalytics.sensorreader.data;

import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import unimelb.cis.spatialanalytics.sensorreader.io.FileWalker;
import unimelb.cis.spatialanalytics.sensorreader.views.UploadFileListAdapter;

/**
 * Created by hanl4 on 21/03/2015.
 * Used to control all the files in uploading dialog queue
 */
public class FileListManager {
    private static List<File> folderList=new ArrayList<>();
    private static Map<File, List<File>> fileCollections=new HashMap<>();
    private static FileWalker fileWalker = new FileWalker();
    private static String TAG="FileListManager";


    public static void clear() {
        Log.i(TAG,"clear()");
        fileCollections = new HashMap<>();
        folderList = new ArrayList<>();

    }

    public static synchronized void setFileCollections(Map<File, List<File>> fileCollections) {
        FileListManager.fileCollections = fileCollections;
    }

    public static synchronized void setFolderList(List<File> folderList) {
        FileListManager.folderList = folderList;
    }

    public static synchronized void setBoth(List<File> folderList, Map<File, List<File>> fileCollections) {
        FileListManager.folderList = folderList;
        FileListManager.fileCollections = fileCollections;
    }


    public static synchronized void addBoth(UploadFileListAdapter adapter, File newFolder, List<File> fileList) {

        fileCollections.put(newFolder, fileList);
        folderList.add(newFolder);
        adapter.doNotifyDataSetInvalidated();
    }

    public static Map<File, List<File>> getFileCollections() {
        return fileCollections;
    }

    public static List<File> getFolderList() {
        return folderList;
    }


    public static synchronized void createBoth() {
        folderList = fileWalker.createGroupList();

        fileCollections = fileWalker.createCollection();

    }


    public static synchronized void createBoth(UploadFileListAdapter adapter) {
        createBoth();
        adapter.doNotifyDataSetInvalidated();

    }

    public static synchronized void removeBoth(UploadFileListAdapter adapter, File folder) {
        folderList.remove(folder);
        fileCollections.remove(folder);

        adapter.notifyDataSetChanged();

    }

}
