package com.youmai.hxsdk.picker.adapters;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.youmai.hxsdk.picker.FilePickerConst;
import com.youmai.hxsdk.picker.PickerManager;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.picker.models.Document;
import com.youmai.hxsdk.picker.utils.FileUtils;
import com.youmai.hxsdk.picker.views.SmoothCheckBox;

/**
 * Created by droidNinja on 29/07/16.
 */
public class FileListAdapter extends SelectableAdapter<FileListAdapter.FileViewHolder, Document> {

    private final Context mContext;

    public FileListAdapter(Context context, List<Document> items, List<String> selectedPaths) {
        super(items, selectedPaths);
        this.mContext = context.getApplicationContext();
    }

    @Override
    public FileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.hx_item_picker_doc_layout, parent, false);

        return new FileViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final FileViewHolder holder, int position) {
        final Document document = getItems().get(position);

        String path = document.getPath();
        int drawable = FileUtils.getTypeDrawable(document.getPath());
        holder.imageView.setImageResource(drawable);
        holder.fileNameTextView.setText(document.getTitle());
        holder.fileSizeTextView.setText(Formatter.formatShortFileSize(mContext, Long.parseLong(document.getSize())));

        if (path.toLowerCase().endsWith(".mp4") || path.toLowerCase().endsWith(".rmvb")
                || path.toLowerCase().endsWith(".avi") || path.toLowerCase().endsWith(".3gp")) {
            
            Glide.with(mContext)
                    .load(Uri.fromFile(new File(document.getPath())))
                    .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                            .error(R.drawable.hx_icon_rd)
                            .fitCenter())
                    .into(holder.imageView);

        } else if (path.toLowerCase().endsWith(".apk")) {
            Log.e("Yw", "path: " + path);
            try {
                PackageManager pm = mContext.getPackageManager();
                PackageInfo pi = pm.getPackageArchiveInfo(path, 0);
                //String appNam = (String) pi.applicationInfo.loadLabel(pm);
                String appName = pi.packageName;
                Drawable applicationIcon = pm.getApplicationIcon(appName);
                holder.imageView.setImageDrawable(applicationIcon);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PickerManager.getInstance().getMaxCount() == 1) {
                    PickerManager.getInstance().add(document.getPath(), FilePickerConst.FILE_TYPE_DOCUMENT);
                } else {
                    onItemClicked(document, holder);
                }
            }
        });

        //in some cases, it will prevent unwanted situations
        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClicked(document, holder);
            }
        });

        //if true, your checkbox will be selected, else unselected
        holder.checkBox.setChecked(isSelected(document));

        holder.itemView.setBackgroundResource(isSelected(document) ? R.color.hxs_picker_gray : android.R.color.white);
        holder.checkBox.setVisibility(isSelected(document) ? View.VISIBLE : View.GONE);

        holder.checkBox.setOnCheckedChangeListener(new SmoothCheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SmoothCheckBox checkBox, boolean isChecked) {
                toggleSelection(document);
                holder.itemView.setBackgroundResource(isChecked ? R.color.hxs_picker_gray : android.R.color.white);
            }
        });
    }

    private void onItemClicked(Document document, FileViewHolder holder) {
        if (holder.checkBox.isChecked()) {
            holder.checkBox.setChecked(!holder.checkBox.isChecked(), true);
            holder.checkBox.setVisibility(View.GONE);
            PickerManager.getInstance().remove(document.getPath(), FilePickerConst.FILE_TYPE_DOCUMENT);
        } else if (PickerManager.getInstance().shouldAdd()) {
            holder.checkBox.setChecked(!holder.checkBox.isChecked(), true);
            holder.checkBox.setVisibility(View.VISIBLE);
            PickerManager.getInstance().add(document.getPath(), FilePickerConst.FILE_TYPE_DOCUMENT);
        }
    }

    @Override
    public int getItemCount() {
        return getItems().size();
    }

    public static class FileViewHolder extends RecyclerView.ViewHolder {
        SmoothCheckBox checkBox;

        ImageView imageView;

        TextView fileNameTextView;

        TextView fileSizeTextView;

        public FileViewHolder(View itemView) {
            super(itemView);
            checkBox = (SmoothCheckBox) itemView.findViewById(R.id.checkbox);
            imageView = (ImageView) itemView.findViewById(R.id.file_iv);
            fileNameTextView = (TextView) itemView.findViewById(R.id.file_name_tv);
            fileSizeTextView = (TextView) itemView.findViewById(R.id.file_size_tv);
        }
    }
}
