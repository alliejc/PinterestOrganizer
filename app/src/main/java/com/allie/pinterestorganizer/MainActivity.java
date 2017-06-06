package com.allie.pinterestorganizer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.pinterest.android.pdk.PDKCallback;
import com.pinterest.android.pdk.PDKClient;
import com.pinterest.android.pdk.PDKException;
import com.pinterest.android.pdk.PDKResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener,BoardsFragment.OnBoardFragmentInteractionListener, AllPinsFragment.OnPinFragmentInteractionListener, SavedPinsFragment.OnFragmentInteractionListener {

    private ActionBar mActionBar;
    private static final String appID = "4903389675355384750";
    private String mUserName;
    private String mToken;
    private static final String BACK_STACK_ROOT_TAG = "root_fragment";
    private static final String ROOT = "root";
    private String mBoardName;

    //This is our tablayout
    private TabLayout tabLayout;

//    //This is our viewPager
//    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PDKClient.configureInstance(this, appID);
        PDKClient.getInstance().onConnect(this);
//        Pager adapter = new Pager(getSupportFragmentManager());

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        mActionBar = getSupportActionBar();
        mActionBar.setTitle(R.string.app_name);
        mActionBar.setDisplayShowTitleEnabled(true);
        mActionBar.setHomeButtonEnabled(true);

        tabLayout = (TabLayout) findViewById(R.id.tablayout);

//        viewPager = (ViewPager) findViewById(R.id.viewpager);

//        viewPager.setAdapter(adapter);
        tabLayout.addOnTabSelectedListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_login:
                loginWithPinterest();
                return true;

            case android.R.id.home:
//                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.overflow, menu);
        return true;
    }

    private void loginWithPinterest() {

        List scopes = new ArrayList<String>();
        scopes.add(PDKClient.PDKCLIENT_PERMISSION_READ_PUBLIC);
        scopes.add(PDKClient.PDKCLIENT_PERMISSION_WRITE_PUBLIC);

        PDKClient.getInstance().login(this, scopes, new PDKCallback(){

            @Override
            public void onSuccess(PDKResponse response) {
                HashMap<String, String> params = new HashMap();
                params.put("fields", "id, username");

                PDKClient.getInstance().getPath("me/", params, new PDKCallback(){
                    @Override
                    public void onSuccess(PDKResponse response){
                        mUserName = response.getUser().getUsername().toString();
                    }

                    @Override
                    public void onFailure(PDKException exception) {
                    }
                });
                addFragmentOnTop(BoardsFragment.newInstance());
            }

            @Override
            public void onFailure(PDKException exception) {
                Log.e(getClass().getName(), exception.getDetailMessage());
            }
        });
        Toast.makeText(getApplicationContext(), "login", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        PDKClient.getInstance().onOauthResponse(requestCode, resultCode, data);
        mToken = data.getExtras().toString();

        Toast.makeText(getApplicationContext(), "result", Toast.LENGTH_SHORT).show();

    }

    private void addFragmentOnTop(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStack(BACK_STACK_ROOT_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        fragmentManager
                .beginTransaction()
                .replace(R.id.main_framelayout, fragment)
                .addToBackStack(BACK_STACK_ROOT_TAG)
                .commit();
    }


//    @Override
//    public void onBackPressed() {
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        if (fragmentManager.getBackStackEntryCount() > 1) {
//            fragmentManager.popBackStackImmediate();
//
//        } else if (fragmentManager.getBackStackEntryCount() < 1) {
//            moveTaskToBack(true);
//
//        } else {
//            super.onBackPressed();
//        }
//    }

    @Override
    public void onBoardFragmentInteraction(String boardName) {
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tablayout);

        tabLayout.setVisibility(View.VISIBLE);

        mBoardName = boardName;

        addFragmentOnTop(AllPinsFragment.newInstance(boardName, mUserName));

    }

    @Override
    public void onPinFragmentInteraction(Uri uri) {

    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        if(tab.getPosition() == 0) {
            addFragmentOnTop(AllPinsFragment.newInstance(mBoardName, mUserName));
        } else {
            addFragmentOnTop(SavedPinsFragment.newInstance(mBoardName, mUserName));
        }

    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

//    public class Pager extends FragmentPagerAdapter {
//
//        FragmentManager fragmentManager;
//
//        //Constructor to the class
//        public Pager(FragmentManager fm) {
//            super(fm);
//            this.fragmentManager = fm;
//        }
//
//        @Override
//        public Fragment getItem(int position) {
//            switch (position){
//                case 0:
//            }
//        }
//
//        //        //Overriding method getItem
////        @Override
////        public Fragment getItem(int position) {
////            fragmentManager =getSupportFragmentManager();
////            //Returning the current tabs
////            switch (position) {
////                case 0:
////                   AllPinsFragment pinsFragment = AllPinsFragment.newInstance(mBoardName, mUserName);
////                    addFragmentOnTop(pinsFragment);
////                   return pinsFragment;
////                case 1:
////                    SavedPinsFragment savedPinsFragment = SavedPinsFragment.newInstance(mBoardName, mUserName);
////                    addFragmentOnTop(savedPinsFragment);
////                    return savedPinsFragment;
////                default:
////                     return null;
////            }
////        }
//
//        //Overriden method getCount to get the number of tabs
//        @Override
//        public int getCount() {
//            return 2;
//        }
//    }
}
