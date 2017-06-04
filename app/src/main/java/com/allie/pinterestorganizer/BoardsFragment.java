package com.allie.pinterestorganizer;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
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

public class BoardsFragment extends Fragment {

    private OnBoardFragmentInteractionListener mListener;
    private RecyclerView mRecyclerView;
    private List<PDKResponse> boardList = new ArrayList<>();
    private BoardsRecyclerViewAdapter mAdapter;
    private String mSelectedBoardId;

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
        getUserBoards();
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
            getUserBoards();
        }
    }

    private void setRecyclerView() {
        Drawable dividerDrawable = ContextCompat.getDrawable(getContext(), R.drawable.recycler_divider);
        RecyclerView.ItemDecoration dividerItemDecoration = new RecyclerDivider(dividerDrawable);
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        GridLayoutManager mLayoutManager = new GridLayoutManager(getContext(), 2);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);

        mAdapter= new BoardsRecyclerViewAdapter(boardList, getContext(), new BoardsRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String boardId) {
                mSelectedBoardId = boardId;
                mListener.onBoardFragmentInteraction(mSelectedBoardId);
            }
        });

        mRecyclerView.setAdapter(mAdapter);

    }

    private void getUserBoards() {
        HashMap<String, String> params = new HashMap();
        params.put("fields", "id, name, description, image");
        PDKClient.getInstance().getPath("me/boards", params , new PDKCallback() {
            @Override
            public void onSuccess(PDKResponse response){
                Log.d("boards", response.getBoardList().toString());

                for(int i = 0; i < response.getBoardList().size(); i++){
                    boardList.add(response);
                }

                setRecyclerView();
            }

            @Override
            public void onFailure(PDKException exception) {
            }
        });
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(String boardId) {
        if (mListener != null) {
            mListener.onBoardFragmentInteraction(boardId);
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
        void onBoardFragmentInteraction(String boardId);
    }
}
