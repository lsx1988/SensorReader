package unimelb.cis.spatialanalytics.sensorreader.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;

import unimelb.cis.spatialanalytics.sensorreader.R;
import unimelb.cis.spatialanalytics.sensorreader.data.FileListManager;
import unimelb.cis.spatialanalytics.sensorreader.http.UploadFileToServer;
import unimelb.cis.spatialanalytics.sensorreader.io.FileWalker;
import unimelb.cis.spatialanalytics.sensorreader.views.UploadFileListAdapter;


public class FragmentUploadFile extends Fragment {


    ExpandableListView expListView;
    UploadFileListAdapter expListAdapter;

    private final String TAG=this.getClass().getSimpleName();
    private FileWalker fileWalker = new FileWalker();

    private int lastExpandedGroupPosition;
    private Button buttonUploadAll;
    private TextView textViewError;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_upload_file, container, false);
        buttonUploadAll=(Button)rootView.findViewById(R.id.statement_title);
        textViewError=(TextView)rootView.findViewById(R.id.textView_errors);
        textViewError.setText("");


        FileListManager.createBoth();



        expListView = (ExpandableListView) rootView.findViewById(R.id.file_list);
        expListAdapter = new UploadFileListAdapter(
                getActivity(),textViewError,expListView);
        expListView.setAdapter(expListAdapter);

        //setGroupIndicatorToRight();

        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                final String fileName = (String) expListAdapter.getChild(
                        groupPosition, childPosition);
             /*   Toast.makeText(getActivity().getBaseContext(), selected, Toast.LENGTH_LONG)
                        .show();*/
                expListAdapter.showFileInfo(groupPosition, childPosition, getActivity().getLayoutInflater(), fileName);

                return true;
            }
        });

        expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {

                if(groupPosition != lastExpandedGroupPosition){
                    expListView.collapseGroup(lastExpandedGroupPosition);
                }

                //super.onGroupExpanded(groupPosition);
                lastExpandedGroupPosition = groupPosition;

            }
        });


        buttonUploadAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new UploadFileToServer(getActivity(), textViewError,expListAdapter);

            }
        });


        return rootView;
    }




    private void setGroupIndicatorToRight() {
        /* Get the screen width */
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;

        expListView.setIndicatorBounds(width - getDipsFromPixel(35), width
                - getDipsFromPixel(5));
    }

    // Convert pixel to dip
    public int getDipsFromPixel(float pixels) {
        // Get the screen's density scale
        final float scale = getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (pixels * scale + 0.5f);
    }


    public UploadFileListAdapter getExpListAdapter() {
        return expListAdapter;
    }


/*    public void setListValues(List<File> folderList,
                              Map<File, List<File>> fileCollections) {
        FileListManager.fileCollections = fileCollections;
        FileListManager.folderList = folderList;
        expListAdapter.notifyDataSetInvalidated();
    }*/

}