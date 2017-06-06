//package com.allie.pinterestorganizer;
//
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.util.Log;
//import android.widget.Toast;
//
//import com.pinterest.android.pdk.PDKCallback;
//import com.pinterest.android.pdk.PDKClient;
//import com.pinterest.android.pdk.PDKException;
//import com.pinterest.android.pdk.PDKResponse;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//
//public class PinterestService {
//
//    private String mToken = "";
//    private String mUserName = "";
//    public SharedPreferences myPrefs;
//    private static final String appID = "4903389675355384750";
//
//    public void loginWithPinterest(Context context) {
//
//        List scopes = new ArrayList<String>();
//        scopes.add(PDKClient.PDKCLIENT_PERMISSION_READ_PUBLIC);
//        scopes.add(PDKClient.PDKCLIENT_PERMISSION_WRITE_PUBLIC);
//
//        PDKClient.getInstance().login(context, scopes, new PDKCallback(){
//            @Override
//            public void onSuccess(PDKResponse response) {
//                getPinterestUser(context);
//            }
//
//            @Override
//            public void onFailure(PDKException exception) {
//                Log.e(getClass().getName(), exception.getDetailMessage());
//            }
//        });
//    }
//
//    private void getPinterestUser(Context context) {
//        HashMap<String, String> params = new HashMap();
//        params.put("fields", "id, username");
//        PDKClient.getInstance().getPath("me/", params, new PDKCallback(){
//            @Override
//            public void onSuccess(PDKResponse response){
//                mUserName = response.getUser().getUsername().toString();
//                setUserName(mUserName, context);
//            }
//
//            @Override
//            public void onFailure(PDKException exception) {
//            }
//        });
//    }
//    public void onOAuthResponse(int requestCode, int resultCode, Intent data, Context context){
//        PDKClient.getInstance().onOauthResponse(requestCode, resultCode, data);
//        mToken = data.getExtras().toString();
//        setToken(mToken, context);
//    }
//
//    public List<PDKResponse> getUserBoards() {
//        List<PDKResponse> boardList = new ArrayList<>();
//
//        HashMap<String, String> params = new HashMap();
//        params.put("fields", "id, name, description, image");
//
//        PDKClient.getInstance().getPath("me/boards", params , new PDKCallback() {
//            @Override
//            public void onSuccess(PDKResponse response){
//
//                for(int i = 0; i < response.getBoardList().size(); i++){
//                    boardList.add(response);
//                }
//            }
//
//            @Override
//            public void onFailure(PDKException exception) {
//                PDKException pdkException = exception;
//            }
//        });
//
//        return  boardList;
//    }
//
//    private void setUserName(String userName, Context context) {
//
//        mUserName = userName;
//        myPrefs = context.getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
//        myPrefs.edit().putString("userName", mUserName).apply();
//    }
//
//    private void setToken(String token, Context context) {
//
//        mToken = token;
//        myPrefs = context.getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
//        myPrefs.edit().putString("token", mToken).apply();
//    }
//}
