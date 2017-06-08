package com.allie.pinterestorganizer;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.ivbaranov.mfb.MaterialFavoriteButton;
import com.pinterest.android.pdk.PDKPin;
import com.pinterest.android.pdk.PDKResponse;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Map;

//TODO: Add ability to loadmore based off offset
public class PinsRecyclerViewAdapter extends RecyclerView.Adapter<PinsRecyclerViewAdapter.ViewHolder> {

    private final List<PDKPin> mList;
    private Context mContext;
    private final OnSaveClickListener listener;
    private SharedPreferences preferences;
    private Map<String,?> keys;

    public interface OnSaveClickListener {
        void onSaveClick(PDKPin savedPin, Boolean favorite);
    }


    public PinsRecyclerViewAdapter(List<PDKPin> items, Context context, OnSaveClickListener listener) {
        this.mList = items;
        this.mContext = context;
        this.listener = listener;
    }

    @Override
    public PinsRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.pin_item, parent, false);
        PinsRecyclerViewAdapter.ViewHolder holder = new PinsRecyclerViewAdapter.ViewHolder(v);
        preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        keys = preferences.getAll();


        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if(keys.containsKey(mList.get(position).getUid())){
            holder.mFavoriteButton.setFavorite(true);
        }
        holder.mTitle.setText(mList.get(position).getNote());
        Picasso.with(mContext).load(mList.get(position).getImageUrl()).into(holder.mImageView);
    }

    public void updateAdapter(List<PDKPin> pins) {

        mList.addAll(pins);
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

            mFavoriteButton.setOnFavoriteChangeListener((materialFavoriteButton, b) -> {
                if(b){
                    mFavoriteButton.setFavorite(true);
                } else {
                    mFavoriteButton.setFavorite(false);
                }
                listener.onSaveClick(mList.get(getAdapterPosition()), b);
            });

        }

    }
}
