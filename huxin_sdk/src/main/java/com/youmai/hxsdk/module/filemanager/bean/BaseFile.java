package com.youmai.hxsdk.module.filemanager.bean;

/**
 * Created by yw on 28/08/17.
 */
public class BaseFile {

    protected int id;
    protected String name; //文件名
    protected String path; //文件路径

    public BaseFile() {

    }

    public BaseFile(int id, String name, String path) {
        this.id = id;
        this.name = name;
        this.path = path;
    }

    public boolean isImage() {
        String[] types = {"jpg", "jpeg", "png", "gif"};
        for (String string : types) {
            if (path.toLowerCase().endsWith(string)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseFile)) return false;

        BaseFile baseFile = (BaseFile) o;

        return id == baseFile.id;
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

}
