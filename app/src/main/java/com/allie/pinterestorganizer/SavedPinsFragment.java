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

import com.pinterest.android.pdk.PDKCallback;
import com.pinterest.android.pdk.PDKClient;
import com.pinterest.android.pdk.PDKException;
import com.pinterest.android.pdk.PDKPin;
import com.pinterest.android.pdk.PDKResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SavedPinsFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private RecyclerView mRecyclerView;
    private List<PDKPin> pinList = new ArrayList();
    private SharedPreferences mPreferences;
    private Map<String,?> keys;
    private SharedPreferences.Editor editor;

    private static final String USERNAME = "userName";
    private String userName;

    private static final String BOARDNAME = "boardName";
    private String boardName;

    public SavedPinsFragment() {
        // Required empty public constructor
    }

    public static SavedPinsFragment newInstance(String boardName, String userName) {
        SavedPinsFragment fragment = new SavedPinsFragment();
        Bundle args = new Bundle();
        args.putString(BOARDNAME, boardName);
        args.putString(USERNAME, userName);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        keys = mPreferences.getAll();
        editor = mPreferences.edit();

        if (getArguments() != null) {
            boardName = getArguments().getString(BOARDNAME);
            userName = getArguments().getString(USERNAME);
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

            getSavedUserPins();
    }

    private String removeSpaces(String string) {
        String noSpacesString = "";
        if(string != null){
            noSpacesString = string.replaceAll("\\s+","-");
        }
        return noSpacesString;
    }

    private void getSavedUserPins() {
        String pathA = "boards/";
        String pathB = "/pins/";
        String output = String.format("%s%s/%s%s", pathA, removeSpaces(userName), removeSpaces(boardName), pathB);

        HashMap<String, String> params = new HashMap();
        params.put("fields", "image, link, note");


        PDKClient.getInstance().getPath(output, params, new PDKCallback() {
            @Override
            public void onSuccess(PDKResponse response) {

                for (int i = 0; i < response.getPinList().size(); i++) {
                    if (keys.containsKey(response.getPinList().get(i).getUid())){
                        pinList.add(response.getPinList().get(i));
                    }
                }
                setRecyclerView();

            }
            @Override
            public void onFailure(PDKException exception) {
                Log.d("Failure", "getUserPins");
            }
        });
    }

    private void setRecyclerView() {

        StaggeredGridLayoutManager mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);

        SavedPinsRecyclerAdapter mAdapter = new SavedPinsRecyclerAdapter(pinList, getContext(), (savedPin, favorite) -> {
            if(favorite){
                editor.putString(savedPin.getUid(), savedPin.getUid()).apply();
            } else {
                editor.remove(savedPin.getUid()).apply();
            }
        });
        mRecyclerView.setAdapter(mAdapter);

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
