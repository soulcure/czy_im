package com.youmai.hxsdk.module.photo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.module.photo.activity.PhotoActivity;
import com.youmai.hxsdk.module.photo.bean.Photo;
import com.youmai.hxsdk.utils.ScreenUtils;

import java.io.File;
import java.util.List;

/**
 * 作者：create by YW
 * 日期：2017.09.08 11:48
 * 描述：图片适配器
 */
public class PhotoAdapter extends BaseAdapter {

    private static final int TYPE_CAMERA = 0;
    private static final int TYPE_PHOTO = 1;

    private List<Photo> mDatas;
    private Context mContext;
    private int mWidth;
    //是否显示相机，默认不显示
    private boolean mIsShowCamera = false;
    //照片选择模式，默认单选
    private int mSelectMode = PhotoActivity.MODE_SINGLE;

    private PhotoClickCallBack mCallBack;

    public PhotoAdapter(Context context, List<Photo> mDatas) {
        this.mDatas = mDatas;
        this.mContext = context;
        int screenWidth = ScreenUtils.getWidthPixels(mContext);
        mWidth = (screenWidth - ScreenUtils.dipTopx(mContext, 16)) / 4;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && mIsShowCamera) {
            return TYPE_CAMERA;
        } else {
            return TYPE_PHOTO;
        }
    }

    @Override
    public int getCount() {
        return mIsShowCamera ? mDatas.size() + 1 : mDatas.size();
    }

    @Override
    public Photo getItem(int position) {
        if (mIsShowCamera) {
            if (position == 0 || position == -1) {
                return null;
            }
            return mDatas.get(position - 1);
        } else {
            if (position == -1) {
                return null;
            }
            return mDatas.get(position);
        }
    }

    @Override
    public long getItemId(int position) {
        //无实际意义
        return position;
    }

    public void setDatas(List<Photo> mDatas) {
        this.mDatas = mDatas;
    }

    public void setIsShowCamera(boolean isShowCamera) {
        this.mIsShowCamera = isShowCamera;
    }

    public boolean isShowCamera() {
        return mIsShowCamera;
    }

    public void setPhotoClickCallBack(PhotoClickCallBack callback) {
        mCallBack = callback;
    }

    public void setSelectMode(int selectMode) {
        this.mSelectMode = selectMode;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (getItemViewType(position) == TYPE_CAMERA) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.hx_photo_item_camera, null);
            convertView.setTag(null);
            //设置高度等于宽度
            GridView.LayoutParams lp = new GridView.LayoutParams(mWidth, mWidth);
            convertView.setLayoutParams(lp);
        } else {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();

                convertView = LayoutInflater.from(mContext).inflate(R.layout.hx_photo_item_layout, null);
                GridView.LayoutParams lp = new GridView.LayoutParams(mWidth, mWidth);
                convertView.setLayoutParams(lp);
                holder.wrapLayout = (FrameLayout) convertView.findViewById(R.id.wrap_parent);
                holder.photoImageView = (ImageView) convertView.findViewById(R.id.iv_photo);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            //holder.photoImageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.hx_pp_ic_photo_loading));//setImageResource(R.drawable.pp_ic_photo_loading);
            //holder.selectView.setImageResource(R.drawable.hx_btn_pic_pressed);
            //holder.selectView.setBackgroundColor(Color.parseColor("#99000000"));
            //holder.selectView.setVisibility(View.VISIBLE);

            final Photo photo = getItem(position);

            holder.wrapLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mCallBack != null) {
                        mCallBack.onPhotoClick(photo);
                    }
                }
            });

            Glide.with(mContext)
                    .load(new File(photo.getPath()))
                    .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE).centerCrop()
                            .placeholder(R.drawable.hx_pp_ic_photo_loading))
                    .into(holder.photoImageView);

        }
        return convertView;
    }

    private class ViewHolder {
        private ImageView photoImageView;
        //private ImageView selectView;
        private FrameLayout wrapLayout;
    }

    /**
     * 多选时，点击相片的回调接口
     */
    public interface PhotoClickCallBack {
        void onPhotoClick(Photo photo);
    }
}
