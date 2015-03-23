package unimelb.cis.spatialanalytics.sensorreader.io;

import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import unimelb.cis.spatialanalytics.sensorreader.config.ConstantConfig;
import unimelb.cis.spatialanalytics.sensorreader.config.SettingConfig;
import unimelb.cis.spatialanalytics.sensorreader.data.SensorData;


/**
 * @author Han Li
 *         traverse folders or files given a root
 */
public class FileWalker {

    private String TAG = this.getClass().getSimpleName();


    /**
     * walk all the files under the root
     *
     * @param root
     * @param requestCode 0: walk for folder; 1: work for files
     * @return
     */
    public ArrayList<File> walk(File root, int requestCode) {

        int fileCounts = 0;
        ArrayList<File> listAll = new ArrayList<File>();
        if (root == null)
            return listAll;
        File[] list = root.listFiles();
        if (list == null)
            return listAll;


        for (File f : list) {
            if (f.isDirectory()) {

                if (SensorData.isSensorRunning) {
                    if (!f.getName().contains(SensorData.dateFolder)) {
                        exploreFolder(requestCode, f, listAll);

                    }
                } else {
                    exploreFolder(requestCode, f, listAll);


                }
            } else {
                if (requestCode == ConstantConfig.KEY_WALK_FILE) {
                    Log.d(TAG, "file: " + f.getAbsolutePath());
                    listAll.add(f);
                }

            }
        }

        return listAll;


    }

    public void exploreFolder(int requestCode, File f, ArrayList<File> listAll) {
        walk(f, requestCode);
        File[] tempList = f.listFiles();
        if (requestCode == ConstantConfig.KEY_WALK_FOLDER) {
            if (tempList.length > 0) {
                Log.d(TAG, "path: " + f.getAbsolutePath());
                listAll.add(f);
            } else {
                Log.d(TAG, "folder " + f.getAbsolutePath() + " is empty and delete it!");
                f.delete();
            }
        }


    }


    /**
     * Delete all the files and folders under the given root
     *
     * @param root root path/folder
     * @return boolean
     */

    public boolean deleteWalker(File root) {
        if (root == null) {
            Log.e(TAG, "root can not be null!");
            return false;
        }

        boolean flag = true;
        File[] list = root.listFiles();
        if (list == null)
            return flag;

        for (File f : list) {
            if (f.isDirectory()) {

                if (SensorData.isSensorRunning) {
                    if (!f.getName().contains(SensorData.dateFolder)) {
                        deleteWalker(f);

                        if (!f.delete()) {
                            Log.e(TAG, "path " + f.getAbsolutePath() + " deletion failed!");
                            return false;
                        } else
                            Log.d(TAG, "path " + f.getAbsolutePath() + " has been deleted.");


                    }
                } else {
                    deleteWalker(f);

                    if (!f.delete()) {
                        Log.e(TAG, "path " + f.getAbsolutePath() + " deletion failed!");
                        return false;
                    } else
                        Log.d(TAG, "path " + f.getAbsolutePath() + " has been deleted.");


                }
            } else {

                if (!f.delete()) {
                    Log.e(TAG, "file " + f.getAbsolutePath() + " deletion failed!");
                    return false;
                } else
                    Log.d(TAG, "file " + f.getAbsolutePath() + " has been deleted.");


            }
        }

        if (!root.delete()) {
            Log.d(TAG, "root " + root.getAbsolutePath() + " has been deleted.");
            return false;
        } else
            Log.e(TAG, "root " + root.getAbsolutePath() + " deletion failed!");


        return true;

    }


    /**
     * return folder list
     *
     * @return
     */
    public List<File> createGroupList() {

        if (SettingConfig.getUserExternalStoragePath() == null)
            return new ArrayList<File>();
        else

            return walk(new File(SettingConfig.getUserExternalStoragePath()), ConstantConfig.KEY_WALK_FOLDER);


    }

    /**
     * generate collections
     *
     * @return
     */
    public Map<File, List<File>> createCollection() {

        Map<File, List<File>> collections = new LinkedHashMap<File, List<File>>();
        ArrayList<File> folders = walk(new File(SettingConfig.getUserExternalStoragePath()), ConstantConfig.KEY_WALK_FOLDER);

        for (File file : folders) {
            List<File> fileList = walk(file, ConstantConfig.KEY_WALK_FILE);
            collections.put(file, fileList);
        }
        return collections;


    }


}
