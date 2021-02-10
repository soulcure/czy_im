package com.youmai.hxsdk.entity;

import android.support.annotation.NonNull;


/**
 * Created by colin on 2017/12/12.
 */

public class EmoItem implements Comparable<EmoItem> {

    private int rank;
    private String fid;
    private int isGif;          // 1 为gif
    private boolean isCheck;


    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getFid() {
        return fid;
    }

    public void setFid(String fid) {
        this.fid = fid;
    }

    public int getIsGif() {
        return isGif;
    }

    public void setIsGif(int isGif) {
        this.isGif = isGif;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    @Override
    public int compareTo(@NonNull EmoItem o) {
        return this.getRank() - o.getRank();
    }
}
