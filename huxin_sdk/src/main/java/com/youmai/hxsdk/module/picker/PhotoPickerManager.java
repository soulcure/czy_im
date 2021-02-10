package com.youmai.hxsdk.module.picker;


import com.youmai.hxsdk.module.picker.model.LocalImage;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by colin on 2017/10/19.
 */

public class PhotoPickerManager {

    private static PhotoPickerManager instance = null;

    private ArrayList<String> paths;
    private HashMap<String, ArrayList<LocalImage>> albums;
    private boolean isOriginal;


    private PhotoPickerManager() {
        paths = new ArrayList<>();
        albums = new HashMap<>();
    }

    public static PhotoPickerManager getInstance() {
        if (instance == null) {
            instance = new PhotoPickerManager();
        }
        return instance;
    }


    public ArrayList<String> getPaths() {
        return paths;
    }

    public void setPaths(ArrayList<String> paths) {
        this.paths.clear();
        this.paths.addAll(paths);
    }


    public void addPath(String path) {
        if (!paths.contains(path)) {
            paths.add(path);
        }
    }

    public void removePath(String path) {
        if (paths.contains(path)) {
            paths.remove(path);
        }

    }


    public void clear() {
        this.paths.clear();
    }

    public boolean isOriginal() {
        return isOriginal;
    }

    public void setOriginal(boolean original) {
        isOriginal = original;
    }


    public void putAll(HashMap<String, ArrayList<LocalImage>> data) {
        albums.putAll(data);
    }


    public void put(String key, ArrayList<LocalImage> data) {
        albums.put(key, data);
    }

    public ArrayList<LocalImage> get(String key) {
        return albums.get(key);
    }

    public HashMap<String, ArrayList<LocalImage>> getAlbums() {
        return albums;
    }

    public void clearMap() {
        albums.clear();
    }
}
