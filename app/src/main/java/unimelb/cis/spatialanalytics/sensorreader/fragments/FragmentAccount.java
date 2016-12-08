package unimelb.cis.spatialanalytics.sensorreader.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import unimelb.cis.spatialanalytics.sensorreader.R;
import unimelb.cis.spatialanalytics.sensorreader.data.Users;


public class FragmentAccount extends Fragment {
    private Button btnLogout;
    private TextView textViewUsername;
    private TextView textViewUploadTimes;
    public FragmentAccount(){}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_account, container, false);

        textViewUsername=(TextView)rootView.findViewById(R.id.text_user_name);
        textViewUploadTimes=(TextView)rootView.findViewById(R.id.text_times);
        textViewUploadTimes.setText(String.valueOf(Users.getUpload_times()));
        textViewUsername.setText(Users.username);
        btnLogout=(Button)rootView.findViewById(R.id.logout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Users.logOut(getActivity());

            }
        });

        return rootView;
    }

    public void setUploadTimes()
    {
        textViewUploadTimes.setText(String.valueOf(Users.getUpload_times()));
    }

}
