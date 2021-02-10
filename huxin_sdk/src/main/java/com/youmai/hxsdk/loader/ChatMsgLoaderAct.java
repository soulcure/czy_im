package com.youmai.hxsdk.loader;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.youmai.hxsdk.ProtoCallback;
import com.youmai.hxsdk.data.ExCacheMsgBean;

import java.util.List;

public class ChatMsgLoaderAct implements LoaderManager.LoaderCallbacks<List<ExCacheMsgBean>> {
    private static final String TAG = ChatMsgLoaderAct.class.getSimpleName();

    private Context mContext;
    private ProtoCallback.CacheMsgCallBack callback;

    public ChatMsgLoaderAct(Context context, ProtoCallback.CacheMsgCallBack callback) {
        mContext = context;
        this.callback = callback;
    }


    @NonNull
    @Override
    public Loader<List<ExCacheMsgBean>> onCreateLoader(int id, @Nullable Bundle args) {
        return new MsgAsyncTaskLoaderAct(mContext);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<ExCacheMsgBean>> loader, List<ExCacheMsgBean> data) {
        Log.d(TAG, "onLoadFinished" + data.toString());
        if (data.isEmpty()) {
            return;
        }
        if (callback != null) {
            callback.result(data);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<ExCacheMsgBean>> loader) {
        Log.d(TAG, "onLoaderReset");
    }
}
