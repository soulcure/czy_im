package com.youmai.hxsdk.entity.cn;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by srsm on 2017/8/31.
 */

public class DuoYinZi implements Parcelable {
    private ArrayList<String> mDuoYinZi; // 当前字的拼音
    private DuoYinZi mNextDuoYinZi;     // 下个字的拼音

    public DuoYinZi() {
    }
    
    protected DuoYinZi(Parcel in) {
        mDuoYinZi = in.createStringArrayList();
        mNextDuoYinZi = in.readParcelable(DuoYinZi.class.getClassLoader());
    }

    public static final Creator<DuoYinZi> CREATOR = new Creator<DuoYinZi>() {
        @Override
        public DuoYinZi createFromParcel(Parcel in) {
            return new DuoYinZi(in);
        }

        @Override
        public DuoYinZi[] newArray(int size) {
            return new DuoYinZi[size];
        }
    };

    public ArrayList<String> getDuoYinZi() {
        return mDuoYinZi;
    }

    public void setDuoYinZi(ArrayList<String> duoYinZi) {
        this.mDuoYinZi = duoYinZi;
    }

    public DuoYinZi getNextDuoYinZi() {
        return mNextDuoYinZi;
    }

    public void setNextDuoYinZi(DuoYinZi nextDuoYinZi) {
        this.mNextDuoYinZi = nextDuoYinZi;
    }


    public boolean find(String key, int[] result) {
       return find(key,this,result);
    }
    
    public boolean find(String key, DuoYinZi duoYinZi, int[] result) {
        boolean finded = false;
        DuoYinZi currDuoYinZi = duoYinZi;
            
        String afterKey;
        int firstZi = 0;
        int lastZi = 0;
        int findIndex = -1;

        String pinyin = "";
        boolean firstZiFinded = false;
        //匹配第一个字
        do {
            
            for(int i =0;i<currDuoYinZi.getDuoYinZi().size();i++){
                pinyin = currDuoYinZi.getDuoYinZi().get(i);
                findIndex = key.indexOf(pinyin);
                // 只要匹配改成必须从第一个字,加了 ==-1 改成 ==0 的条件
                if(findIndex==0){
                    break;
                }
            }
            
            if(findIndex==0) {
                firstZiFinded = true;
                findIndex = findIndex + pinyin.length();
                currDuoYinZi = currDuoYinZi.getNextDuoYinZi();
                break;
            }
            firstZi++;

            currDuoYinZi = currDuoYinZi.getNextDuoYinZi();
        } while (currDuoYinZi!=null);

        lastZi = firstZi;
        
        //第一字找到，后面的字必须是连续的了
        if(firstZiFinded) {
            finded = true;
            afterKey = key.substring(findIndex);
            if(!afterKey.equals("")) {
                //关键字还有，记录没有了
                if(currDuoYinZi==null){
                    return false;
                }
                
                for (; currDuoYinZi != null && findIndex != -1; currDuoYinZi = currDuoYinZi.getNextDuoYinZi()) {
                    for (int i = 0; i < currDuoYinZi.getDuoYinZi().size(); i++) {
                        pinyin = currDuoYinZi.getDuoYinZi().get(i);
                        findIndex = afterKey.indexOf(pinyin);
                        if (findIndex == 0) {
                            lastZi++;
                            findIndex = findIndex + pinyin.length();
                            afterKey = afterKey.substring(findIndex);
                            break;
                        }else {
                            //后面的任何一个字不匹配
                            return false;
                        }
                    }
                }
                
                //关键字还有，记录没有了
                if(!afterKey.equals("")&&currDuoYinZi==null) {
                    return false;
                }
            }
        }

        result[0] = firstZi;
        result[1] = lastZi;    
        return finded;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringList(mDuoYinZi);
        dest.writeParcelable(mNextDuoYinZi, flags);
    }
}