package com.youmai.hxsdk.view.chat.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.youmai.hxsdk.view.chat.emoticon.bean.EmoticonBean;
import com.youmai.hxsdk.view.chat.emoticon.bean.EmoticonEntity;
import com.youmai.hxsdk.view.chat.emoticon.bean.EmoticonSetBean;
import com.youmai.hxsdk.view.chat.emoticon.db.EmoticonDBHelper;
import com.youmai.hxsdk.view.chat.emoticon.utils.EmoticonBase;
import com.youmai.hxsdk.view.chat.emoticon.utils.EmoticonHandler;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * 表情数据初始化
 * Created by fylder on 2017/3/8.
 */
public class EmotionInit {

    /**
     * 表情数据初始化
     */
    public static void init(Context context) {
        if (!isEmoticonInitSuccess(context)) {
            List<EmoticonEntity> entities = new ArrayList<>();
            entities.add(new EmoticonEntity("emoticons/huxin", EmoticonBase.Scheme.ASSETS));
            initEmoticonsDB(context, entities);
        }
    }

    /**
     * 是否成功插入数据
     */
    public static boolean isEmoticonInitSuccess(Context context) {
        return Utils.isInitDb(context);
    }

    /**
     * 恢复表情数据的状态，允许重新初始化
     */
    public static void resetEmoticonInitStatus(Context context) {
        Utils.setIsInitDb(context, false);
    }

    private static void initEmoticonsDB(final Context context, List<EmoticonEntity> emoticonEntities) {
        EmoAsyncTask task = new EmoAsyncTask(context, emoticonEntities);
        task.execute();
    }


    private static class EmoAsyncTask extends AsyncTask<Void, Void, Void> {

        private Context mContext;
        private List<EmoticonEntity> emoticonEntities;

        public EmoAsyncTask(Context context, List<EmoticonEntity> emoticonEntities) {
            mContext = context;
            this.emoticonEntities = emoticonEntities;
        }

        @Override
        protected Void doInBackground(Void... params) {
            EmoticonDBHelper emoticonDbHelper = EmoticonHandler.getInstance(mContext.getApplicationContext()).getEmoticonDbHelper();

            //导入定义表情，有emoji
            List<EmoticonSetBean> emoticonSetBeans = new ArrayList<>();
            for (EmoticonEntity entity : emoticonEntities) {
                try {
                    EmoticonSetBean bean = Utils.ParseEmoticons(mContext, entity.getPath(), entity.getScheme());
                    emoticonSetBeans.add(bean);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("emotion", String.format("read %s config.xml error" + entity.getPath()));
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                    Log.e("emotion", String.format("parse %s config.xml error" + entity.getPath()));
                }
            }

            for (EmoticonSetBean setBean : emoticonSetBeans) {
                emoticonDbHelper.insertEmoticonSet(setBean);
            }

            emoticonDbHelper.cleanup();

            if (emoticonSetBeans.size() == emoticonEntities.size()) {
                Utils.setIsInitDb(mContext, true);
            }
            return null;
        }


        @Override
        protected void onPostExecute(Void result) {

        }
    }


}
