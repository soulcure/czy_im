package com.youmai.hxsdk.db.manager;

import android.content.Context;
import android.text.TextUtils;

import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.db.dao.CacheMsgBeanDao;
import com.youmai.hxsdk.db.dao.ContactBeanDao;
import com.youmai.hxsdk.db.dao.DaoMaster;
import com.youmai.hxsdk.db.dao.DaoSession;
import com.youmai.hxsdk.db.dao.GroupInfoBeanDao;

import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.query.QueryBuilder;

/**
 * 作者：create by YW
 * 日期：2017.03.28 19:17
 * 描述：DB IM Manager
 */
public class GreenDBIMManager {

    // 数据库名
    private static final String DB_NAME = "hx_im_";

    private static GreenDBIMManager instance = null;

    private Context mContext;
    private DaoSession mDaoSession;
    private String mUuid;

    public static GreenDBIMManager instance(Context context) {
        if (instance == null) {
            instance = new GreenDBIMManager(context);
        }
        return instance;
    }


    private GreenDBIMManager(Context context) {
        mContext = context.getApplicationContext();
        mUuid = HuxinSdkManager.instance().getUuid();
        /*if (!TextUtils.isEmpty(mUuid)) {
            String dbName = DB_NAME + mUuid + ".db";
            initDBDao(dbName);
        }*/
        String dbName = DB_NAME + mUuid + ".db";
        initDBDao(dbName);
    }

    public void initUuid(String uuid) {
        if (TextUtils.isEmpty(mUuid) || !mUuid.equals(uuid)) {
            mUuid = uuid;
            String dbName = DB_NAME + uuid + ".db";
            genDBDao(dbName);
        }
    }


    private void genDBDao(String dbName) {
        try {
            HMROpenHelper helper = new HMROpenHelper(mContext, dbName);
            Database db = helper.getWritableDb();
            mDaoSession = new DaoMaster(db).newSession();
            QueryBuilder.LOG_SQL = true;
            QueryBuilder.LOG_VALUES = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void initDBDao(String dbName) {
        try {
            if (mDaoSession == null) {
                HMROpenHelper helper = new HMROpenHelper(mContext, dbName);
                Database db = helper.getWritableDb();
                mDaoSession = new DaoMaster(db).newSession();
                QueryBuilder.LOG_SQL = true;
                QueryBuilder.LOG_VALUES = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public CacheMsgBeanDao getCacheMsgDao() {
        return mDaoSession.getCacheMsgBeanDao();
    }

    public GroupInfoBeanDao getGroupInfoDao() {
        return mDaoSession.getGroupInfoBeanDao();
    }


    public ContactBeanDao getContactDao() {
        return mDaoSession.getContactBeanDao();
    }

}
