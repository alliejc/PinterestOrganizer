package com.allie.pinterestorganizer;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
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
import com.pinterest.android.pdk.PDKResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//TODO: Add ability to loadmore based off offset
//TODO: Handle favorite click
public class AllPinsFragment extends Fragment {

    private OnPinFragmentInteractionListener mListener;
    private RecyclerView mRecyclerView;
    private List<PDKResponse> pinList = new ArrayList<>();
    private static final String BOARDNAME = "boardName";
    private String boardName;

    private static final String USERNAME = "userName";
    private String userName;
    private Boolean hasNext;
    private int mOffset = 0;


    public AllPinsFragment() {
        // Required empty public constructor
    }

    public static AllPinsFragment newInstance(String boardName, String userName) {
        AllPinsFragment fragment = new AllPinsFragment();
        Bundle args = new Bundle();
        args.putString(BOARDNAME, boardName);
        args.putString(USERNAME, userName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        if (savedInstanceState == null) {
            getUserPins();
        }
    }

    private void setRecyclerView() {

        StaggeredGridLayoutManager mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);

        PinsRecyclerViewAdapter mAdapter= new PinsRecyclerViewAdapter(pinList, getContext());

        mRecyclerView.setAdapter(mAdapter);

    }

    private String removeSpaces(String string) {
        String noSpacesString = "";
        if(string != null){
            noSpacesString = string.replaceAll("\\s+","-");
        }
        return noSpacesString;
    }

    private void getUserPins() {
        String pathA = "boards/";
        String pathB = "/pins/";
        String output = String.format("%s%s/%s%s", pathA, removeSpaces(userName), removeSpaces(boardName), pathB);

        HashMap<String, String> params = new HashMap();
        params.put("fields", "image, link, note");

        PDKClient.getInstance().getPath(output, params, new PDKCallback() {
            @Override
            public void onSuccess(PDKResponse response){

                for(int i = 0; i < response.getPinList().size(); i++){
                    pinList.add(response);
                }

                hasNext = response.hasNext();

                setRecyclerView();
            }

            @Override
            public void onFailure(PDKException exception) {
                Log.d("Failure", "getUserPins");
            }
        });
    }

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
