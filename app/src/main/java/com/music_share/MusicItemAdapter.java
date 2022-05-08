package com.music_share;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Random;

public class MusicItemAdapter extends RecyclerView.Adapter<MusicItemAdapter.ViewHolder> implements Filterable {
    private static final String LOG_TAG = MusicItemAdapter.class.getName();
    private ArrayList<SongItem> mSongItemsData = new ArrayList<>();
    private ArrayList<SongItem> mSongItemsDataAll = new ArrayList<>();
    private Context mContext;
    private int lastPosition = -1;

    MusicItemAdapter(Context context, ArrayList<SongItem> itemsData) {
        this.mSongItemsData = itemsData;
        this.mSongItemsDataAll = itemsData;
        this.mContext = context;
    }

    @Override
    public MusicItemAdapter.ViewHolder onCreateViewHolder(
            ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(MusicItemAdapter.ViewHolder holder, int position) {
        SongItem currentItem = mSongItemsData.get(position);

        holder.bindTo(currentItem);
        Random rand = new Random();
        int random_number = rand.nextInt(2);

        if (holder.getAdapterPosition() > lastPosition) {
            Animation animation;
            if (random_number == 0) {
                animation = AnimationUtils.loadAnimation(mContext, R.anim.slide_in_row);
            } else {
                animation = AnimationUtils.loadAnimation(mContext, R.anim.slide_in_column);
            }
            holder.itemView.startAnimation(animation);
            lastPosition = holder.getAdapterPosition();

        }
    }

    @Override
    public int getItemCount() {
        return mSongItemsData.size();
    }

    @Override
    public Filter getFilter() {
        return musicFilter;
    }

    private Filter musicFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<SongItem> filteredList = new ArrayList<>();
            FilterResults results = new FilterResults();

            if (charSequence == null || charSequence.length() == 0) {
                results.count = mSongItemsDataAll.size();
                results.values = mSongItemsDataAll;
            } else {
                String filterPattern = charSequence.toString().toLowerCase().trim();
                for (SongItem item : mSongItemsDataAll) {
                    if (item.getTitle().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
                results.count = filteredList.size();
                results.values = filteredList;
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults filterResults) {
            mSongItemsData = (ArrayList) filterResults.values;
            notifyDataSetChanged();
        }
    };

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mTitle;
        private TextView mDescription;
        private TextView mUrl;
        private ImageView mItemImage;


        public ViewHolder(View itemView) {
            super(itemView);

            mTitle = itemView.findViewById(R.id.Title);
            mDescription = itemView.findViewById(R.id.songDescription);
            mUrl = itemView.findViewById(R.id.url);
            mItemImage = itemView.findViewById(R.id.song_picture);

            itemView.findViewById(R.id.share).setOnClickListener(v -> Log.d(LOG_TAG, "Share has been clicked!"));
            itemView.findViewById(R.id.delete).setOnClickListener(v -> Log.d(LOG_TAG, "Delete has been clicked!"));
        }

        public void bindTo(SongItem currentItem) {
            mTitle.setText(currentItem.getTitle());
            mUrl.setText(currentItem.getUrl());
            mDescription.setText(currentItem.getDescription());

            Glide.with(mContext).load(currentItem.getImageResource()).into(mItemImage);

            //itemView.findViewById(R.id.delete).setOnClickListener(view -> ((MusicShareActivity) mContext).deleteFile(currentItem));

        }
    }
}

