package unimelb.cis.spatialanalytics.sensorreader;


import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import unimelb.cis.spatialanalytics.sensorreader.config.ConstantConfig;
import unimelb.cis.spatialanalytics.sensorreader.fragments.FragmentAccount;
import unimelb.cis.spatialanalytics.sensorreader.fragments.FragmentMainPanel;
import unimelb.cis.spatialanalytics.sensorreader.fragments.FragmentStatement;
import unimelb.cis.spatialanalytics.sensorreader.fragments.FragmentUploadFile;
import unimelb.cis.spatialanalytics.sensorreader.views.NoticeDialogFragment;

/**
 * This example illustrates a common usage of the DrawerLayout widget
 * in the Android support library.
 * <p/>
 * <p>When a navigation (left) drawer is present, the host activity should detect presses of
 * the action bar's Up affordance as a signal to open and close the navigation drawer. The
 * ActionBarDrawerToggle facilitates this behavior.
 * Items within the drawer should fall into one of two categories:</p>
 * <p/>
 * <ul>
 * <li><strong>View switches</strong>. A view switch follows the same basic policies as
 * list or tab navigation in that a view switch does not create navigation history.
 * This pattern should only be used at the root activity of a task, leaving some form
 * of Up navigation active for activities further down the navigation hierarchy.</li>
 * <li><strong>Selective Up</strong>. The drawer allows the user to choose an alternate
 * parent for Up navigation. This allows a user to jump across an app's navigation
 * hierarchy at will. The application should treat this as it treats Up navigation from
 * a different task, replacing the current task stack using TaskStackBuilder or similar.
 * This is the only form of navigation drawer that should be used outside of the root
 * activity of a task.</li>
 * </ul>
 * <p/>
 * <p>Right side drawers should be used for actions, not navigation. This follows the pattern
 * established by the Action Bar that navigation should be to the left and actions to the right.
 * An action should be an operation performed on the current contents of the window,
 * for example enabling or disabling a data overlay on top of the current content.</p>
 */
public class MainActivity extends ActionBarActivity implements NoticeDialogFragment.NoticeDialogListener {
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mDrawerItemStrings;


    private static int PRESENT_FRAGMENT_ID = 0;


    private Fragment fragment;
    private Fragment mainPanelFragment;
    private Fragment accountFragment;
    private Fragment uploadFileFragment;
    private Fragment fileFragment;

    FragmentTransaction fragmentTransaction;


    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTitle = mDrawerTitle = getTitle();
        mDrawerItemStrings = getResources().getStringArray(R.array.nav_drawer_items);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, mDrawerItemStrings));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // enable ActionBar app icon to behave as action to toggle nav drawer
        actionBar = this.getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);


        //fragments
        fragment = new Fragment();
        mainPanelFragment = new FragmentMainPanel();
        accountFragment = new FragmentAccount();
        uploadFileFragment = new FragmentUploadFile();
        fileFragment = new FragmentStatement();

        Bundle bundle = new Bundle();
    /*    bundle.putInt("TAG", ConstantConfig.FRAGMENT_MAIN_PANEL);
        mainPanelFragment.setArguments(bundle);
        bundle.clear();*/
        bundle.putInt("TAG", ConstantConfig.FRAGMENT_UPLOAD_FILE);
        uploadFileFragment.setArguments(bundle);



        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                //R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                actionBar.setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                actionBar.setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            selectItem(0);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
     /*   MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);*/
        return super.onCreateOptionsMenu(menu);
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        //menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action buttons
        switch (item.getItemId()) {
            case R.id.radio:
                // create intent to perform web search for this planet


                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }


    /**
     * switch the views between different fragments
     *
     * @param openedFragment
     */
    private void switchFragments(Fragment openedFragment,int position) {
        if (openedFragment.isAdded())
            fragmentTransaction.show(openedFragment);
        else
            fragmentTransaction.add(R.id.content_frame, openedFragment,String.valueOf(position));

        if (!mainPanelFragment.equals(openedFragment) && mainPanelFragment.isAdded())
            fragmentTransaction.hide(mainPanelFragment);
        if (!fileFragment.equals(openedFragment) && fileFragment.isAdded())
            fragmentTransaction.hide(fileFragment);
        if (!accountFragment.equals(openedFragment) && accountFragment.isAdded())
        {
            fragmentTransaction.hide(accountFragment);
            ((FragmentAccount)accountFragment).setUploadTimes();
        }
        if (!uploadFileFragment.equals(openedFragment) && uploadFileFragment.isAdded())
            fragmentTransaction.hide(uploadFileFragment);


        fragment = openedFragment;
    }


    private void selectItem(int position)

    {

        // update the main content by replacing fragments

        // Han Li and Yu Sun 26/02/2015: close the soft keypad
        if (getCurrentFocus() != null && getCurrentFocus().getWindowToken() != null) {
            InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            keyboard.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }

        PRESENT_FRAGMENT_ID = position;


        if (mainPanelFragment != null && fileFragment != null
                && uploadFileFragment != null && accountFragment != null) {

            //FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentTransaction = getSupportFragmentManager().beginTransaction();

            // update selected item and title, then close the drawer
            mDrawerList.setItemChecked(position, true);
            setTitle(mDrawerItemStrings[position]);
            mDrawerLayout.closeDrawer(mDrawerList);

            fragment = null;
            switch (position) {
                case ConstantConfig.FRAGMENT_MAIN_PANEL:
                    switchFragments(mainPanelFragment,position);
                    break;
                case ConstantConfig.FRAGMENT_FILE_MANAGEMENT:
                    switchFragments(fileFragment,position);
                    break;
                case ConstantConfig.FRAGMENT_ACCOUNT:
                    switchFragments(accountFragment,position);
                    break;
                case ConstantConfig.FRAGMENT_UPLOAD_FILE:
                    switchFragments(uploadFileFragment,position);
                    break;

                default:
                    break;
            }

            fragmentTransaction.commit();
            // update selected item and title, then close the drawer
            mDrawerList.setItemChecked(position, true);
            setTitle(mDrawerItemStrings[position]);
            mDrawerLayout.closeDrawer(mDrawerList);
        }
    }


    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        actionBar.setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }


    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {

        if (fragment != null && fragment == mainPanelFragment) {
            ((FragmentMainPanel) fragment).onDialogPositiveClick(dialog);
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

        if (fragment != null && fragment == mainPanelFragment) {
            ((FragmentMainPanel) fragment).onDialogNegativeClick(dialog);
        }

    }


    public void onCheckboxClicked(View view) {

        if (fragment != null && fragment == mainPanelFragment) {
            ((FragmentMainPanel) fragment).onCheckboxClicked(view);
        }
    }





    /**************************************************************/
   /* private void selectItem_Original(int position) {
        // update the main content by replacing fragments
        Fragment fragment = new PlanetFragment();
        Bundle args = new Bundle();
        args.putInt(PlanetFragment.ARG_PLANET_NUMBER, position);
        fragment.setArguments(args);

        FragmentManager fragmentManager = getFragmentManager();
        // fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);
        setTitle(mDrawerItemStrings[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }


    *//**
     * Fragment that appears in the "content_frame", shows a planet
     *//*
    public static class PlanetFragment extends Fragment {
        public static final String ARG_PLANET_NUMBER = "planet_number";

        public PlanetFragment() {
            // Empty constructor required for fragment subclasses
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_planet, container, false);
            int i = getArguments().getInt(ARG_PLANET_NUMBER);
            String planet = getResources().getStringArray(R.array.nav_drawer_items)[i];

            int imageId = getResources().getIdentifier(planet.toLowerCase(Locale.getDefault()),
                    "drawable", getActivity().getPackageName());
            ((ImageView) rootView.findViewById(R.id.image)).setImageResource(imageId);
            getActivity().setTitle(planet);
            return rootView;
        }
    }
*/
}