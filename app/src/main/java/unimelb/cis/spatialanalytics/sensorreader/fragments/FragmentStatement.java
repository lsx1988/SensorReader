package unimelb.cis.spatialanalytics.sensorreader.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import unimelb.cis.spatialanalytics.sensorreader.R;
import unimelb.cis.spatialanalytics.sensorreader.data.SensorData;


public class FragmentStatement extends Fragment {
    private List<String> sensorList;
    private ListView listView;
    private TextView textViewSensorList;
    public FragmentStatement(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_about, container, false);
        listView=(ListView)rootView.findViewById(R.id.sensor_list);
        textViewSensorList=(TextView)rootView.findViewById(R.id.text_sensor_list);


        sensorList=SensorData.getAllSensorList(getActivity());
        int count=0;
        String text="";
        for(String str:sensorList)
        {
            count++;
            text+=count+". "+str+"\n";


        }
        textViewSensorList.setText(text);


    /*    // Defined Array values to show in ListView
        String[] values = new String[] { "Android List View",
                "Adapter implementation",
                "Simple List View In Android",
                "Create List View Android",
                "Android Example",
                "List View Source Code",
                "List View Array Adapter",
                "Android Example List View"
        };


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1, sensorList);


        // Assign adapter to ListView
        listView.setAdapter(adapter);
*/
        return rootView;
    }

}
