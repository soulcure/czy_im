package com.youmai.hxsdk.loader;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;

import com.youmai.hxsdk.ProtoCallback;
import com.youmai.hxsdk.data.ExCacheMsgBean;

import java.util.List;

public class ChatMsgLoader implements LoaderManager.LoaderCallbacks<List<ExCacheMsgBean>> {
    private static final String TAG = ChatMsgLoader.class.getSimpleName();

    private Context mContext;
    private int mGroupType;
    private ProtoCallback.CacheMsgCallBack callback;

    public ChatMsgLoader(Context context, int groupType, ProtoCallback.CacheMsgCallBack callback) {
        mContext = context;
        mGroupType = groupType;
        this.callback = callback;
    }


    @NonNull
    @Override
    public Loader<List<ExCacheMsgBean>> onCreateLoader(int id, @Nullable Bundle args) {
        if (mGroupType == 2) {
            return new CommMsgAsyncTaskLoader(mContext);
        } else if (mGroupType == 101) {
            return new OwnerMsgAsyncTaskLoader(mContext);
        } else {
            return new MsgAsyncTaskLoader(mContext);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<ExCacheMsgBean>> loader, List<ExCacheMsgBean> data) {
        Log.d(TAG, "onLoadFinished" + data.toString());
        /*if (data.isEmpty()) {
            return;
        }*/
        if (callback != null) {
            callback.result(data);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<ExCacheMsgBean>> loader) {
        Log.d(TAG, "onLoaderReset");
    }
}
