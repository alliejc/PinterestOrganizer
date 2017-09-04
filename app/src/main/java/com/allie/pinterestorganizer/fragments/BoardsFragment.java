package com.allie.pinterestorganizer.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.allie.pinterestorganizer.PinterestService;
import com.allie.pinterestorganizer.adapters.BoardsRecyclerViewAdapter;
import com.allie.pinterestorganizer.R;
import com.pinterest.android.pdk.PDKBoard;
import com.pinterest.android.pdk.PDKCallback;
import com.pinterest.android.pdk.PDKClient;
import com.pinterest.android.pdk.PDKException;
import com.pinterest.android.pdk.PDKResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BoardsFragment extends Fragment {

    private OnBoardFragmentInteractionListener mListener;
    private RecyclerView mRecyclerView;
    private BoardsRecyclerViewAdapter mAdapter;
    private String mSelectedBoardName;
    private PinterestService mPinterestService;

    public BoardsFragment() {
        // Required empty public constructor
    }

    public static BoardsFragment newInstance() {
        BoardsFragment fragment = new BoardsFragment();
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

        View rootView = inflater.inflate(R.layout.item_list, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.list);
        mPinterestService = new PinterestService(getContext());
        setRecyclerView();

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void setRecyclerView() {

        StaggeredGridLayoutManager mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);

        mAdapter= new BoardsRecyclerViewAdapter(getContext(), (String boardName) -> {
            mSelectedBoardName = boardName;
            mListener.onBoardFragmentInteraction(mSelectedBoardName);
        });

        mRecyclerView.setAdapter(mAdapter);
        mAdapter.updateAdapter(mPinterestService.getUserBoards());
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(String boardName) {
        if (mListener != null) {
            mListener.onBoardFragmentInteraction(boardName);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnBoardFragmentInteractionListener) {
            mListener = (OnBoardFragmentInteractionListener) context;
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

    public interface OnBoardFragmentInteractionListener {
        void onBoardFragmentInteraction(String boardName);
    }
}
