package com.allie.pinterestorganizer;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.ivbaranov.mfb.MaterialFavoriteButton;
import com.pinterest.android.pdk.PDKResponse;
import com.squareup.picasso.Picasso;

import java.util.List;

//TODO: Add ability to loadmore based off offset
public class PinsRecyclerViewAdapter extends RecyclerView.Adapter<PinsRecyclerViewAdapter.ViewHolder> {

        private final List<PDKResponse> mList;
        private Context mContext;

        public PinsRecyclerViewAdapter(List<PDKResponse> items, Context context) {
            this.mList = items;
            this.mContext = context;
        }

        @Override
        public PinsRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            mContext = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View v = inflater.inflate(R.layout.pin_item, parent, false);
            PinsRecyclerViewAdapter.ViewHolder holder = new PinsRecyclerViewAdapter.ViewHolder(v);

            return holder;
        }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mTitle.setText(mList.get(position).getPinList().get(position).getNote());
        Picasso.with(mContext).load(mList.get(position).getPinList().get(position).getImageUrl()).into(holder.mImageView);
    }

    public void updateAdapter(List<PDKResponse> responses) {

        mList.addAll(responses);
        notifyDataSetChanged();
    }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView mTitle;
            ImageView mImageView;
            MaterialFavoriteButton mFavoriteButton;

            public ViewHolder(View view) {
                super(view);

                mTitle = (TextView) view.findViewById(R.id.title);
                mImageView = (ImageView) view.findViewById(R.id.image);
                mFavoriteButton = (MaterialFavoriteButton) view.findViewById(R.id.favorite);
            }

        }

    }
