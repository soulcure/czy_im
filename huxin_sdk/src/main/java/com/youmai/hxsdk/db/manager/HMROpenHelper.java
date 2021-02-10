package com.youmai.hxsdk.db.manager;

import android.content.Context;

import com.youmai.hxsdk.db.dao.CacheMsgBeanDao;
import com.youmai.hxsdk.db.dao.ContactBeanDao;
import com.youmai.hxsdk.db.dao.DaoMaster;
import com.youmai.hxsdk.db.dao.GroupInfoBeanDao;

import org.greenrobot.greendao.database.Database;

/**
 * 作者：create by YW
 * 日期：2017.03.27 16:57
 * 描述：GreenDao helper
 */
public class HMROpenHelper extends DaoMaster.DevOpenHelper {

    /**
     * 初始化一个AbSDDBHelper.
     *
     * @param context 应用context
     * @param name    数据库名
     */
    public HMROpenHelper(Context context, String name) {
        super(context, name);
    }

    /**
     * 数据库升级
     *
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        //操作数据库的更新
        MigrationHelper.migrate(db, CacheMsgBeanDao.class, ContactBeanDao.class, GroupInfoBeanDao.class);
    }

}
