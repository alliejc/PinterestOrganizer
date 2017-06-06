package com.allie.pinterestorganizer;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pinterest.android.pdk.PDKBoard;
import com.pinterest.android.pdk.PDKResponse;
import com.squareup.picasso.Picasso;
import java.util.List;

//TODO: Make generic so other AllPinsFragments can use
public class BoardsRecyclerViewAdapter extends RecyclerView.Adapter<BoardsRecyclerViewAdapter.BoardViewHolder> {

    private final List<PDKResponse> mList;
    private Context mContext;
    private final OnItemClickListener listener;
    private String boardName;

    public interface OnItemClickListener {
        void onItemClick(String boardId);
    }

    public BoardsRecyclerViewAdapter(List<PDKResponse> items, Context context, OnItemClickListener listener) {
        this.mList = items;
        this.mContext = context;
        this.listener = listener;
    }

    @Override
    public BoardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.board_item, parent, false);
        BoardViewHolder holder = new BoardViewHolder(v);

        return holder;
    }

    @Override
    public void onBindViewHolder(final BoardViewHolder holder, int position) {
//        PDKResponse response = mList.get(position);
//        holder.mTitle.setText(response.getBoardList().get(position).getName());
//        holder.mTitle.setText(response.getBoardList().get(position).getName());


        holder.mTitle.setText(mList.get(position).getBoardList().get(position).getName());
        Picasso.with(mContext).load(mList.get(position).getPinList().get(position).getImageUrl()).into(holder.mImageView);
    }

    public void updateAdapter(List<PDKResponse> items) {

        mList.addAll(items);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class BoardViewHolder extends RecyclerView.ViewHolder {

        TextView mTitle;
        ImageView mImageView;

        public BoardViewHolder(View boardView) {
            super(boardView);

            mTitle = (TextView) boardView.findViewById(R.id.title);
            mImageView = (ImageView) boardView.findViewById(R.id.image);

            boardView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                boardName = mList.get(position).getBoardList().get(position).getName().toString();
                listener.onItemClick(boardName);
            });
        }

    }

}
