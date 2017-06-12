package com.allie.pinterestorganizer;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.ivbaranov.mfb.MaterialFavoriteButton;
import com.pinterest.android.pdk.PDKCallback;
import com.pinterest.android.pdk.PDKClient;
import com.pinterest.android.pdk.PDKException;
import com.pinterest.android.pdk.PDKPin;
import com.pinterest.android.pdk.PDKRequest;
import com.pinterest.android.pdk.PDKResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//TODO: Add ability to loadmore based off offset
//TODO: Handle favorite click
public class AllPinsFragment extends Fragment {

    private OnPinFragmentInteractionListener mListener;
    private RecyclerView mRecyclerView;
    private List<PDKPin> mPinList = new ArrayList<>();
    public SharedPreferences mSharedPreferences;
    private PinsRecyclerViewAdapter mAdapter;
    private SharedPreferences.Editor mEditor;
    private String mUserName;

    private static final String BOARDNAME = "boardName";
    private String mBoardName;


    public AllPinsFragment() {
        // Required empty public constructor
    }

    public static AllPinsFragment newInstance(String boardName) {
        AllPinsFragment fragment = new AllPinsFragment();
        Bundle args = new Bundle();
        args.putString(BOARDNAME, boardName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        mEditor = mSharedPreferences.edit();

        if (getArguments() != null) {
            mBoardName = getArguments().getString(BOARDNAME);
            mUserName = mSharedPreferences.getString("username", "").toString();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.item_list, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.list);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setRecyclerView();
        getUserPins();
    }

    private void setRecyclerView() {

        StaggeredGridLayoutManager mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);

        mAdapter = new PinsRecyclerViewAdapter(getContext(), (PDKPin savedPin, Boolean favorite) -> {
            if (favorite) {
                mEditor.putString(savedPin.getUid(), savedPin.getUid()).apply();
            } else {
                mEditor.remove(savedPin.getUid()).apply();
            }
        });

        mRecyclerView.setAdapter(mAdapter);
//        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                if(pdkResponse.hasNext()){
//                    pdkResponse.loadNext(pdkCallback);
//                }
//            }
//        });
    }

//    private void loadDataFromApi() {
//
//        if(pdkResponse.hasNext()){
//            pdkResponse.loadNext(new PDKCallback() {
//
//            });
//        }
//
//        PDKClient.getInstance().getPath(cursor, new PDKCallback() {
//            @Override
//            public void onSuccess(PDKResponse response){
//
//                for(int i = 0; i < response.getPinList().size(); i++){
//                    mPinList.add(response.getPinList().get(i));
//                }
//                mAdapter.updateAdapter(mPinList);
//            }
//
//            @Override
//            public void onFailure(PDKException exception) {
//                Log.d("Failure", "getUserPins");
//            }
//
//        });
//    }

    private String removeSpaces(String string) {
        String noSpacesString = "";
        String finalString = "";
        if (string != null) {
            noSpacesString = string.replaceAll("[^a-zA-Z0-9-\\s]", "");
            finalString = noSpacesString.replaceAll("\\s+", "-");

        }
        return finalString;
    }

    private void getUserPins() {

        String pathA = "boards/";
        String pathB = "/pins/";
        String output = String.format("%s%s/%s%s", pathA, removeSpaces(mUserName), removeSpaces(mBoardName), pathB);

        HashMap<String, String> params = new HashMap();
        params.put("fields", "image, link, note");

        PDKClient.getInstance().getPath(output, params, new PDKCallback() {
            @Override
            public void onSuccess(PDKResponse response) {

//                pdkResponse = response;

                for (int i = 0; i < response.getPinList().size(); i++) {
                    mPinList.add(response.getPinList().get(i));
                }
                mAdapter.updateAdapter(mPinList);
            }

            @Override
            public void onFailure(PDKException exception) {
                Log.d("Failure", "getUserPins");
            }
        });
    }

//    private void loadNext() {
//
//        if (!loading && pdkResponse.hasNext()) {
//            loading = true;
//            pdkResponse.loadNext(pdkCallback);
//            mAdapter.updateAdapter(mPinList);
//        }
//    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onPinFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnPinFragmentInteractionListener) {
            mListener = (OnPinFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnPinFragmentInteractionListener {
        // TODO: Update argument type and name
        void onPinFragmentInteraction(Uri uri);
    }
}
