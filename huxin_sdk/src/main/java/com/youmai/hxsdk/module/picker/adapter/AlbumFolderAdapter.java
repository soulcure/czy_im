package com.youmai.hxsdk.module.picker.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.module.picker.PhotoDirectoryActivity;
import com.youmai.hxsdk.module.picker.model.LocalImage;
import com.youmai.hxsdk.module.picker.model.LocalImageAlbum;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Created by colin on 2017/10/17.
 */

public class AlbumFolderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private List<LocalImageAlbum> dataList = new ArrayList<>();
    private Map<String, ArrayList<LocalImage>> albums;

    public AlbumFolderAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setData(Map<String, ArrayList<LocalImage>> data) {
        albums = data;
        dataList.clear();
        for (String key : data.keySet()) {
            LocalImageAlbum albumItem = new LocalImageAlbum();
            albumItem.setAlbumName(key);
            albumItem.setCount(data.get(key).size());
            dataList.add(albumItem);
        }
        notifyDataSetChanged();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.view_album_folder_item, parent, false);
        return new ThumbViewHolder(contentView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final LocalImageAlbum item = dataList.get(position);
        ThumbViewHolder vh = (ThumbViewHolder) holder;

        final String key = item.getAlbumName();
        ArrayList<LocalImage> paths = albums.get(key);

        if (paths != null && paths.size() > 0) {
            String path = paths.get(0).getPath();
            if (path.toLowerCase().endsWith(".mp4") || path.toLowerCase().endsWith(".rmvb")
                    || path.toLowerCase().endsWith(".avi") || path.toLowerCase().endsWith(".3gp")) {
                Glide.with(mContext)
                        .load(path)
                        .thumbnail(0.5f)
                        .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                .error(R.drawable.hx_icon_rd)
                                .centerCrop())
                        .into(vh.img_thumb);
            } else {
                Glide.with(mContext)
                        .load(paths.get(0).getOriginalUri())
                        .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                .error(R.drawable.hx_icon_rd)
                                .centerCrop())
                        .into(vh.img_thumb);
            }
        }


        vh.tv_dir_name.setText(item.getAlbumName());
        vh.tv_count.setText(String.valueOf(item.getCount()));

        vh.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mContext instanceof PhotoDirectoryActivity) {
                    PhotoDirectoryActivity act = (PhotoDirectoryActivity) mContext;
                    act.setPhotoView(key);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return dataList == null ? 0 : dataList.size();
    }

    private class ThumbViewHolder extends RecyclerView.ViewHolder {
        private View itemView;
        private ImageView img_thumb;
        private TextView tv_dir_name;
        private TextView tv_count;
        private ImageView img_next;


        public ThumbViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            img_thumb = (ImageView) itemView.findViewById(R.id.img_thumb);
            tv_dir_name = (TextView) itemView.findViewById(R.id.tv_dir_name);
            tv_count = (TextView) itemView.findViewById(R.id.tv_count);
            img_next = (ImageView) itemView.findViewById(R.id.img_next);
        }
    }

}
