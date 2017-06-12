package com.allie.pinterestorganizer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.pinterest.android.pdk.PDKCallback;
import com.pinterest.android.pdk.PDKClient;
import com.pinterest.android.pdk.PDKException;
import com.pinterest.android.pdk.PDKResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener,BoardsFragment.OnBoardFragmentInteractionListener, AllPinsFragment.OnPinFragmentInteractionListener, SavedPinsFragment.OnFragmentInteractionListener, WelcomeFragment.OnWelcomeInteractionListener {

    private ActionBar mActionBar;
    private static final String appID = "4903389675355384750";
    private String mUserName;
    private String mToken;
    private static final String BACK_STACK_ROOT_TAG = "root_fragment";
    private String mBoardName;
    private TabLayout mTabLayout;
    public SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private Toolbar mToolbar;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PDKClient.configureInstance(this, appID);
        PDKClient.getInstance().onConnect(this);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mEditor = mSharedPreferences.edit();

        setupToolBar();

        if(mSharedPreferences.getString("token", "").isEmpty() || mSharedPreferences.getString("username", "").isEmpty()){
            loginWithPinterest();
        } else {
            loadHomePage();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_login:
                loginWithPinterest();
                return true;

            case R.id.action_logout:
                logoutWithPinterest();
                return true;

            case android.R.id.home:
                onBackPressed();
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

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
       MenuItem mLogin = menu.findItem(R.id.action_login);
       MenuItem mLogout = menu.findItem(R.id.action_logout);

        if(mSharedPreferences.getString("token", "").isEmpty() || mSharedPreferences.getString("username", "").isEmpty()){
            mLogin.setVisible(true);
            mLogout.setVisible(false);
        } else {
            mLogin.setVisible(false);
            mLogout.setVisible(true);
        }
        return true;
    }

    private void setupToolBar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mActionBar = getSupportActionBar();
        mActionBar.setTitle(R.string.app_name);
        mActionBar.setDisplayShowTitleEnabled(true);
        mActionBar.setHomeButtonEnabled(true);

        mTabLayout = (TabLayout) findViewById(R.id.tablayout);
        mTabLayout.addOnTabSelectedListener(this);
    }

    private void logoutWithPinterest() {
        mEditor.clear().apply();
        PDKClient.getInstance().logout();
//        addFragmentOnTop(WelcomeFragment.newInstance());
    }

    private void loginWithPinterest() {

        List scopes = new ArrayList<String>();
        scopes.add(PDKClient.PDKCLIENT_PERMISSION_READ_PUBLIC);
        scopes.add(PDKClient.PDKCLIENT_PERMISSION_WRITE_PUBLIC);
        scopes.add(PDKClient.PDKCLIENT_PERMISSION_READ_PRIVATE);
        scopes.add(PDKClient.PDKCLIENT_PERMISSION_WRITE_PRIVATE);

        PDKClient.getInstance().login(this, scopes, new PDKCallback() {

            @Override
            public void onSuccess(PDKResponse response) {
                HashMap<String, String> params = new HashMap();
                params.put("fields", "id, username");

                PDKClient.getInstance().getPath("me/", params, new PDKCallback() {
                    @Override
                    public void onSuccess(PDKResponse response) {
                        mUserName = response.getUser().getUsername().toString();
                        mEditor.putString("username", mUserName).apply();
                        Toast.makeText(getApplicationContext(), R.string.login, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(PDKException exception) {
                        Toast.makeText(getApplicationContext(), R.string.errorMessage, Toast.LENGTH_SHORT).show();

                    }
                });
            }

            @Override
            public void onFailure(PDKException exception) {
                Log.e(getClass().getName(), exception.getDetailMessage());
                Toast.makeText(getApplicationContext(), R.string.errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadHomePage() {
            //Not added to backstack to keep as "home screen" fragment
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.main_framelayout, BoardsFragment.newInstance(), "allboards").commit();
//        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        PDKClient.getInstance().onOauthResponse(requestCode, resultCode, data);
        mToken = data.getExtras().getString("PDKCLIENT_EXTRA_RESULT");
        mEditor.putString("token", mToken).apply();

        loadHomePage();
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


    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 1) {
            fragmentManager.popBackStackImmediate();
            mTabLayout.getTabAt(fragmentManager.getBackStackEntryCount()).select();

        } else if (fragmentManager.getBackStackEntryCount() < 1) {
            moveTaskToBack(true);
            mTabLayout.getTabAt(fragmentManager.getBackStackEntryCount()).select();

        } else {
            super.onBackPressed();
            mTabLayout.getTabAt(fragmentManager.getBackStackEntryCount()).select();
        }
    }

    @Override
    public void onBoardFragmentInteraction(String boardName) {

        mBoardName = boardName;

        addFragmentOnTop(AllPinsFragment.newInstance(boardName));
        mTabLayout.getTabAt(1).select();
        onTabSelected(mTabLayout.getTabAt(1));

    }

    @Override
    public void onPinFragmentInteraction(Uri uri) {

    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        if (tab.getPosition() == 0) {
           loadHomePage();
        } else if (tab.getPosition() == 1) {
            addFragmentOnTop(AllPinsFragment.newInstance(mBoardName));
        } else {
            addFragmentOnTop(SavedPinsFragment.newInstance(mBoardName));
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

    @Override
    public void onWelcomeInteraction() {
//        loginWithPinterest();
//        loadHomePage();
    }
}