package com.youmai.hxsdk.module.filemanager.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.youmai.hxsdk.module.filemanager.utils.Utils;

import java.io.File;
import java.util.Comparator;

/**
 * Created by yw on 29/07/16.
 */
public class Document extends BaseFile implements Parcelable, Comparator<Document> {
    private String mimeType;
    private String size;
    private FileType fileType;
    private long modifyDate;

    public Document(int id, String title, String path) {
        super(id, title, path);
    }

    public Document() {
        super(0, null, null);
    }

    protected Document(Parcel in) {
        id = in.readInt();
        name = in.readString();
        path = in.readString();
        mimeType = in.readString();
        size = in.readString();
        fileType = in.readParcelable(FileType.class.getClassLoader());
        modifyDate = in.readLong();
    }

    public static final Creator<Document> CREATOR = new Creator<Document>() {
        @Override
        public Document createFromParcel(Parcel in) {
            return new Document(in);
        }

        @Override
        public Document[] newArray(int size) {
            return new Document[size];
        }
    };

    @Override
    public int compare(Document d1, Document d2) {
        if (d1.getModifyDate() == d2.getModifyDate()) return 0;
        return d1.getModifyDate() > d2.getModifyDate() ? -1 : 1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Document)) return false;

        Document document = (Document) o;

        return id == document.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getTitle() {
        return new File(this.path).getName();
    }

    public void setTitle(String title) {
        this.name = title;
    }

    public boolean isThisType(String[] types) {
        return Utils.contains(types, this.path);
    }

    public FileType getFileType() {
        return fileType;
    }

    public void setFileType(FileType fileType) {
        this.fileType = fileType;
    }

    public long getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(long modifyDate) {
        this.modifyDate = modifyDate;
    }

    @Override
    public String toString() {
        return "Document{" +
                "mimeType='" + mimeType + '\'' +
                ", size='" + size + '\'' +
                ", fileType=" + fileType +
                ", modifyDate=" + modifyDate +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(id);
        parcel.writeString(name);
        parcel.writeString(path);
        parcel.writeString(mimeType);
        parcel.writeString(size);
        parcel.writeParcelable(FileType.CREATOR.createFromParcel(parcel), flags);
        parcel.writeLong(modifyDate);
    }
}
