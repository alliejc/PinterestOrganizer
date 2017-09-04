package com.allie.pinterestorganizer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.pinterest.android.pdk.PDKBoard;
import com.pinterest.android.pdk.PDKCallback;
import com.pinterest.android.pdk.PDKClient;
import com.pinterest.android.pdk.PDKException;
import com.pinterest.android.pdk.PDKPin;
import com.pinterest.android.pdk.PDKResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PinterestService {

    private String mToken = "";
    private String mUserName = "";
    private List<PDKPin> mPinsList = new ArrayList<>();
    private List<PDKBoard> mBoardList = new ArrayList<>();
    public SharedPreferences myPrefs;
    private static final String appID = "4903389675355384750";
    private Context mContext;

    public PinterestService(Context context) {
        this.mContext = context;
        myPrefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void loginWithPinterest() {

       if(myPrefs.getString("token", "").isEmpty() || myPrefs.getString("username", "").isEmpty()) {

           PDKClient.configureInstance(mContext, appID);
           PDKClient.getInstance().onConnect(mContext);

           List scopes = new ArrayList<String>();
           scopes.add(PDKClient.PDKCLIENT_PERMISSION_READ_PUBLIC);
           scopes.add(PDKClient.PDKCLIENT_PERMISSION_WRITE_PUBLIC);
           scopes.add(PDKClient.PDKCLIENT_PERMISSION_READ_PRIVATE);
           scopes.add(PDKClient.PDKCLIENT_PERMISSION_WRITE_PRIVATE);

           PDKClient.getInstance().login(mContext, scopes, new PDKCallback() {

               @Override
               public void onSuccess(PDKResponse response) {
                   HashMap<String, String> params = new HashMap();
                   params.put("fields", "id, username");

                   PDKClient.getInstance().getPath("me/", params, new PDKCallback() {
                       @Override
                       public void onSuccess(PDKResponse response) {
                           getPinterestUser();
//                           mUserName = response.getUser().getUsername().toString();
//                           setUserName(mUserName, mContext);
                           Toast.makeText(mContext, R.string.login, Toast.LENGTH_SHORT).show();
                       }

                       @Override
                       public void onFailure(PDKException exception) {
                           Toast.makeText(mContext, R.string.errorMessage, Toast.LENGTH_SHORT).show();
                       }
                   });
               }

               @Override
               public void onFailure(PDKException exception) {
                   Log.e(getClass().getName(), exception.getDetailMessage());
                   Toast.makeText(mContext, R.string.errorMessage, Toast.LENGTH_SHORT).show();
               }
           });
       }
    }

    public void logoutWithPinterest() {
        myPrefs.edit().clear().apply();
        PDKClient.getInstance().logout();
    }

    public void getPinterestUser() {
        HashMap<String, String> params = new HashMap();
        params.put("fields", "id, username");
        PDKClient.getInstance().getPath("me/", params, new PDKCallback(){
            @Override
            public void onSuccess(PDKResponse response){
                mUserName = response.getUser().getUsername().toString();
                setUserName(mUserName, mContext);
            }

            @Override
            public void onFailure(PDKException exception) {
            }
        });
    }

    public List<PDKBoard> getUserBoards() {

//        if(mBoardList == null || mBoardList.size() < 0) {
            HashMap<String, String> params = new HashMap();
            params.put("fields", "id, name, description, image");

            PDKClient.getInstance().getPath("me/boards", params, new PDKCallback() {
                @Override
                public void onSuccess(PDKResponse response) {

                    for (int i = 0; i < response.getBoardList().size(); i++) {
                        mBoardList.add(response.getBoardList().get(i));
                    }
                }

                @Override
                public void onFailure(PDKException exception) {
                    PDKException pdkException = exception;
                }
            });
//        }

        return  mBoardList;
    }


    public List<PDKPin> getUserPins(String boardName) {

//        if(mPinsList == null || mPinsList.size() < 0) {
            String pathA = "boards/";
            String pathB = "/pins/";
            String output = String.format("%s%s/%s%s", pathA, removeSpaces(mUserName), removeSpaces(boardName), pathB);

            HashMap<String, String> params = new HashMap();
            params.put("fields", "image, link, note");

            PDKClient.getInstance().getPath(output, params, new PDKCallback() {
                @Override
                public void onSuccess(PDKResponse response) {

                    for (int i = 0; i < response.getPinList().size(); i++) {
                        mPinsList.add(response.getPinList().get(i));
                    }
                }

                @Override
                public void onFailure(PDKException exception) {
                    Log.d("Failure", "getUserPins");
                }
            });
//        }
        return mPinsList;
    }

    private String removeSpaces(String string) {
        String noSpacesString = "";
        String finalString = "";
        if (string != null) {
            noSpacesString = string.replaceAll("[^a-zA-Z0-9-\\s]", "");
            finalString = noSpacesString.replaceAll("\\s+", "-");

        }
        return finalString;
    }

    private void setUserName(String userName, Context context) {

        mUserName = userName;
        myPrefs = context.getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        myPrefs.edit().putString("username", mUserName).apply();
    }

    public void setToken(String token, Context context) {

        mToken = token;
        myPrefs = context.getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        myPrefs.edit().putString("token", mToken).apply();
    }
}
