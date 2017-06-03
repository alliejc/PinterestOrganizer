package com.allie.pinterestorganizer;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pinterest.android.pdk.PDKCallback;
import com.pinterest.android.pdk.PDKClient;
import com.pinterest.android.pdk.PDKException;
import com.pinterest.android.pdk.PDKResponse;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AllPinsFragment extends Fragment {

    private OnPinFragmentInteractionListener mListener;
    private List mBoards = new ArrayList();
    private RecyclerView mRecyclerView;
    private List<PDKResponse> pinList = new ArrayList<>();

    public AllPinsFragment() {
        // Required empty public constructor
    }

    public static AllPinsFragment newInstance() {
        AllPinsFragment fragment = new AllPinsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_item_list, container, false);
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
        Drawable dividerDrawable = ContextCompat.getDrawable(getContext(), R.drawable.recycler_divider);
        RecyclerView.ItemDecoration dividerItemDecoration = new RecyclerDivider(dividerDrawable);
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);

        PinsRecyclerViewAdapter mAdapter= new PinsRecyclerViewAdapter(pinList, getContext());

        mRecyclerView.setAdapter(mAdapter);

    }

    private void getUserPins() {
        PDKClient.getInstance().getPath("me/boards", null, new PDKCallback() {
            @Override
            public void onSuccess(PDKResponse response){
                Log.d("boards", response.getBoardList().toString());

                for(int i = 0; i < response.getBoardList().size(); i++){
//                    mBoards.add(response.getData());
                    pinList.add(response);
//                    mBoards.add(response.getBoardList().get(i).getImageUrl());
//                    mBoards.add(response.getBoardList().get(i).getName());
                }
//                mBoards.addAll(response.getBoardList());

                setRecyclerView();
            }

            @Override
            public void onFailure(PDKException exception) {
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
