package com.youmai.hxsdk.module.filemanager.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.module.filemanager.constant.FilePickerConst;
import com.youmai.hxsdk.module.filemanager.PickerManager;
import com.youmai.hxsdk.module.filemanager.bean.Document;
import com.youmai.hxsdk.module.filemanager.utils.DateUtils;
import com.youmai.hxsdk.module.filemanager.utils.FileUtils;
import com.youmai.hxsdk.module.filemanager.utils.Utils;

import java.io.File;
import java.util.ArrayList;

/**
 * 作者：create by YW
 * 日期：2017.08.28 15:02
 * 描述：
 */
public class FileClassifyAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private ArrayList<Document> mListApps;

    public FileClassifyAdapter(Context context, ArrayList<Document> list) {
        this.mContext = context;
        this.mListApps = list;
    }

    public void updateListAndNotifyDataChanged(ArrayList<Document> list) {
        this.mListApps = list;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //创建Holder
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.hx_item_picker_file_layout, parent, false);
        return new FileManagerHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final Document document = mListApps.get(position);

        String path = document.getPath();
        File file = new File(path);
        int drawable = FileUtils.getTypeDrawable(document.getPath());
        ((FileManagerHolder) holder).mIvLogo.setImageResource(drawable);
        ((FileManagerHolder) holder).mTitle.setText(document.getTitle());
        ((FileManagerHolder) holder).mTvSize.setText(Formatter.formatShortFileSize(mContext, Long.parseLong(document.getSize())));
        ((FileManagerHolder) holder).mTvTime.setText(DateUtils.getStringByFormat(file.lastModified(), "yyyy-MM-dd HH:mm"));

        if (path.toLowerCase().endsWith(".mp4") || path.toLowerCase().endsWith(".rmvb")
                || path.toLowerCase().endsWith(".avi") || path.toLowerCase().endsWith(".3gp")) {

            Glide.with(mContext)
                    .load(Uri.fromFile(new File(document.getPath())))
                    .thumbnail(0.5f)
                    .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE).error(R.drawable.hx_icon_rd))
                    .into(((FileManagerHolder) holder).mIvLogo);

        } else if (path.toLowerCase().endsWith(".apk")) {
            try {
                Drawable dbe = Utils.getUninstallAPKIcon(mContext, path);
                ((FileManagerHolder) holder).mIvLogo.setImageDrawable(dbe);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (path.toLowerCase().endsWith(".png") || path.toLowerCase().endsWith(".jpg")) {
            Glide.with(mContext)
                    .load(Uri.fromFile(new File(document.getPath())))
                    .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE).error(R.drawable.hx_icon_rd))
                    .thumbnail(0.5f)
                    .into(((FileManagerHolder) holder).mIvLogo);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(mContext, "click position " + position, Toast.LENGTH_LONG).show();
                PickerManager.getInstance().setMaxCount(1); // 后续优化
                PickerManager.getInstance().add(document.getPath(), FilePickerConst.FILE_TYPE_DOCUMENT);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mListApps == null ? 0 : mListApps.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    private class FileManagerHolder extends RecyclerView.ViewHolder {
        private final ImageView mIvLogo;
        private final TextView mTitle;
        private final TextView mTvSize;
        private final TextView mTvTime;

        public FileManagerHolder(View itemView) {
            super(itemView);
            mIvLogo = (ImageView) itemView.findViewById(R.id.iv_logo);
            mTitle = (TextView) itemView.findViewById(R.id.file_name_tv);
            mTvTime = (TextView) itemView.findViewById(R.id.file_time_tv);
            mTvSize = (TextView) itemView.findViewById(R.id.file_size_tv);
            itemView.setTag(this);
        }
    }

}
