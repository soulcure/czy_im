package com.youmai.hxsdk.module.picker.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.module.filemanager.constant.FilePickerConst;
import com.youmai.hxsdk.module.picker.PhotoPickerManager;
import com.youmai.hxsdk.module.picker.PhotoPreviewActivity;
import com.youmai.hxsdk.module.picker.PreviewImageActivity;
import com.youmai.hxsdk.module.picker.PreviewVideoActivity;
import com.youmai.hxsdk.module.picker.model.LocalImage;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by colin on 2017/10/17.
 */

public class AlbumContentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public final static int MAX_IMAGE_COUNT = 5;

    private Context mContext;
    private boolean isVideo = false;

    private List<LocalImage> dataList = new ArrayList<>();

    private List<CompoundButton> checkBoxList = new ArrayList<>();


    //private List<LocalImage> choiceList = new ArrayList<>();

    public AlbumContentAdapter(Context mContext, List<LocalImage> dataList) {
        this.dataList = dataList;
        this.mContext = mContext;

        if (dataList != null && dataList.size() > 0) {
            final String path = dataList.get(0).getPath();
            if (path.toLowerCase().endsWith(".mp4") || path.toLowerCase().endsWith(".rmvb")
                    || path.toLowerCase().endsWith(".avi") || path.toLowerCase().endsWith(".3gp")) {
                isVideo = true;
            }
        }

    }

    /*public void setList(List<LocalImage> dataList) {
        this.dataList.clear();
        this.dataList.addAll(dataList);

        if (dataList != null && dataList.size() > 0) {
            final String path = dataList.get(0).getPath();
            if (path.toLowerCase().endsWith(".mp4") || path.toLowerCase().endsWith(".rmvb")
                    || path.toLowerCase().endsWith(".avi") || path.toLowerCase().endsWith(".3gp")) {
                isVideo = true;
            }
        }

        notifyDataSetChanged();
    }*/

    public void clearCheckView() {
        for (CompoundButton item : checkBoxList) {
            item.setChecked(false);
        }
        checkBoxList.clear();
    }


    public boolean isVideo() {
        return isVideo;
    }

    public void setVideo(boolean video) {
        isVideo = video;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.view_album_item, parent, false);

        int rest = mContext.getResources().getDisplayMetrics().widthPixels - PhotoPreviewActivity.DRI_WIDTH * 4;

        int SIDE = rest / PhotoPreviewActivity.GIRD_COUNT;
        contentView.setLayoutParams(new RelativeLayout.LayoutParams(SIDE, SIDE));

        return new ThumbViewHolder(contentView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        final LocalImage item = dataList.get(position);

        final ThumbViewHolder vh = (ThumbViewHolder) holder;

        //Uri uri = localImage.getOriginalUri();
        final String path = item.getPath();
        final String playTime = item.getPlayTime();

        if (isVideo()) {
            Glide.with(mContext)
                    .load(path)
                    .thumbnail(0.5f)
                    .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                            .error(R.drawable.hx_icon_rd)
                            .centerCrop())
                    .into(vh.img_item);


            vh.tv_video_time.setVisibility(View.VISIBLE);
            vh.cb_item.setVisibility(View.GONE);

            vh.tv_video_time.setText(playTime);

        } else {
            Glide.with(mContext)
                    .load(item.getOriginalUri())
                    .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                            .error(R.drawable.hx_icon_rd)
                            .centerCrop())
                    .into(vh.img_item);

            vh.tv_video_time.setVisibility(View.GONE);
            vh.cb_item.setVisibility(View.VISIBLE);
        }

        vh.cb_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isVideo()) {
                    //do nothing
                } else {
                    if (PhotoPickerManager.getInstance().getPaths().size() > MAX_IMAGE_COUNT) {
                        Toast.makeText(mContext, "最多只能选择5张图片", Toast.LENGTH_SHORT).show();
                        vh.cb_item.setChecked(false);
                    }
                }
            }
        });
        vh.cb_item.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isVideo()) {
                    if (isChecked) {
                        PhotoPickerManager.getInstance().clear();
                        PhotoPickerManager.getInstance().addPath(item.getPath());

                        for (CompoundButton item : checkBoxList) {
                            item.setChecked(false);
                        }
                        checkBoxList.clear();
                        checkBoxList.add(buttonView);

                    } else {
                        PhotoPickerManager.getInstance().removePath(item.getPath());

                        if (checkBoxList.contains(buttonView)) {
                            checkBoxList.remove(buttonView);
                        }
                    }
                } else {
                    if (isChecked) {
                        PhotoPickerManager.getInstance().addPath(item.getPath());

                        if (!checkBoxList.contains(buttonView)) {
                            checkBoxList.add(buttonView);
                        }

                    } else {
                        PhotoPickerManager.getInstance().removePath(item.getPath());

                        if (checkBoxList.contains(buttonView)) {
                            checkBoxList.remove(buttonView);
                        }
                    }
                }

                if (mContext instanceof PhotoPreviewActivity) {
                    PhotoPreviewActivity act = (PhotoPreviewActivity) mContext;
                    act.setSendText(PhotoPickerManager.getInstance().getPaths());
                }

            }
        });

        vh.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isVideo()) {
                    if (playTime.equals("00:00")) {
                        Toast.makeText(mContext, "视频文件已经损坏", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Intent intent = new Intent(mContext, PreviewVideoActivity.class);
                    intent.putExtra("video", path);
                    intent.putExtra("time", playTime);
                    mContext.startActivity(intent);

                } else {
                    ArrayList<String> arrayList = new ArrayList<>();
                    for (LocalImage item : dataList) {
                        arrayList.add(item.getPath());
                    }
                    //String[] array = new String[]{path};
                    Intent intent = new Intent(mContext, PreviewImageActivity.class);
                    intent.putStringArrayListExtra("image", arrayList);
                    //intent.putExtra("image", array);
                    intent.putExtra("index", position);
                    intent.putExtra(FilePickerConst.KEY_IS_ORIGINAL, ((PhotoPreviewActivity) mContext).getIsOriginal());
                    ((PhotoPreviewActivity) mContext).startActivityForResult(intent, PhotoPreviewActivity.REQUEST_CODE_PHOTO);
                }
            }
        });

        ArrayList<String> list = PhotoPickerManager.getInstance().getPaths();
        if (list != null && list.contains(path)) {
            vh.cb_item.setChecked(true);
        } else {
            vh.cb_item.setChecked(false);
        }

    }

    @Override
    public int getItemCount() {
        return dataList == null ? 0 : dataList.size();
    }

    private class ThumbViewHolder extends RecyclerView.ViewHolder {

        private View view;
        private ImageView img_item;
        private CheckBox cb_item;
        private TextView tv_video_time;


        public ThumbViewHolder(View itemView) {
            super(itemView);
            view = itemView.findViewById(R.id.album_holder);
            img_item = (ImageView) itemView.findViewById(R.id.img_item);
            cb_item = (CheckBox) itemView.findViewById(R.id.cb_item);
            tv_video_time = (TextView) itemView.findViewById(R.id.tv_video_time);
        }
    }

}
