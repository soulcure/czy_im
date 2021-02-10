package com.youmai.hxsdk.picker.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;

import java.io.File;
import java.util.ArrayList;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.picker.models.PhotoDirectory;
import com.youmai.hxsdk.picker.utils.AndroidLifecycleUtils;

public class FolderGridAdapter extends SelectableAdapter<FolderGridAdapter.PhotoViewHolder, PhotoDirectory> {

    private final Context context;
    private final RequestManager glide;
    private final boolean showCamera;
    private int imageSize;

    public final static int ITEM_TYPE_CAMERA = 100;
    public final static int ITEM_TYPE_PHOTO = 101;
    private FolderGridAdapterListener folderGridAdapterListener;

    public interface FolderGridAdapterListener {
        void onCameraClicked();

        void onFolderClicked(PhotoDirectory photoDirectory);
    }

    public FolderGridAdapter(Context context, RequestManager requestManager, ArrayList<PhotoDirectory> photos, ArrayList<String> selectedPaths, boolean showCamera) {
        super(photos, selectedPaths);
        this.context = context;
        this.glide = requestManager;
        this.showCamera = showCamera;
        setColumnNumber(context, 3);
    }

    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.hx_item_picker_folder_layout, parent, false);

        return new PhotoViewHolder(itemView);
    }

    @Override
    public int getItemViewType(int position) {
        if (showCamera)
            return (position == 0) ? ITEM_TYPE_CAMERA : ITEM_TYPE_PHOTO;
        else
            return ITEM_TYPE_PHOTO;
    }

    @Override
    public void onBindViewHolder(final PhotoViewHolder holder, int position) {
        if (getItemViewType(position) == ITEM_TYPE_PHOTO) {

            final PhotoDirectory photoDirectory = getItems().get(showCamera ? position - 1 : position);

            if (AndroidLifecycleUtils.canLoadImage(holder.imageView.getContext())) {
                glide.load(new File(photoDirectory.getCoverPath()))
                        .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                .placeholder(R.drawable.image_placeholder)
                                .centerCrop().override(imageSize, imageSize))
                        .thumbnail(0.5f)
                        .into(holder.imageView);
            }

            holder.folderTitle.setText(photoDirectory.getName());
            holder.folderCount.setText(String.valueOf(photoDirectory.getMedias().size()));

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (folderGridAdapterListener != null)
                        folderGridAdapterListener.onFolderClicked(photoDirectory);
                }
            });
            holder.bottomOverlay.setVisibility(View.VISIBLE);
        } else {
            holder.imageView.setImageResource(R.drawable.ic_camera);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (folderGridAdapterListener != null)
                        folderGridAdapterListener.onCameraClicked();
                }
            });
            holder.bottomOverlay.setVisibility(View.GONE);
        }
    }

    private void setColumnNumber(Context context, int columnNum) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);
        int widthPixels = metrics.widthPixels;
        imageSize = widthPixels / columnNum;
    }

    @Override
    public int getItemCount() {
        if (showCamera)
            return getItems().size() + 1;
        return getItems().size();
    }

    public void setFolderGridAdapterListener(FolderGridAdapterListener onClickListener) {
        this.folderGridAdapterListener = onClickListener;
    }

    public static class PhotoViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView folderTitle;
        TextView folderCount;
        View bottomOverlay;
        View selectBg;

        public PhotoViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.iv_photo);
            folderTitle = (TextView) itemView.findViewById(R.id.folder_title);
            folderCount = (TextView) itemView.findViewById(R.id.folder_count);
            bottomOverlay = itemView.findViewById(R.id.bottomOverlay);
            selectBg = itemView.findViewById(R.id.transparent_bg);
        }
    }
}
