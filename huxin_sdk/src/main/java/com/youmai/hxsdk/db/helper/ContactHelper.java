package com.youmai.hxsdk.db.helper;

import android.content.Context;

import com.youmai.hxsdk.db.bean.ContactBean;
import com.youmai.hxsdk.db.dao.ContactBeanDao;
import com.youmai.hxsdk.db.manager.GreenDBIMManager;
import com.youmai.hxsdk.utils.ListUtils;

import org.greenrobot.greendao.query.DeleteQuery;
import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;


/**
 * Author:  colin
 * Date:    2018-4-20 14:35
 * Description:
 */
public class ContactHelper {

    private static ContactHelper instance;


    public static ContactHelper instance() {
        if (instance == null) {
            instance = new ContactHelper();
        }
        return instance;
    }

    private ContactHelper() {

    }


    /**
     * 查询好友成员信息
     *
     * @param context
     * @return
     */
    public List<ContactBean> toQueryContactList(Context context) {
        ContactBeanDao dao = GreenDBIMManager.instance(context).getContactDao();
        QueryBuilder<ContactBean> qb = dao.queryBuilder();
        return qb.list();
    }


    /**
     * 查询某个群组的群信息和成员信息
     *
     * @param context
     * @return
     */
    public ContactBean toQueryContactById(Context context, String uuid) {
        ContactBean bean = null;
        ContactBeanDao dao = GreenDBIMManager.instance(context).getContactDao();
        QueryBuilder<ContactBean> qb = dao.queryBuilder();
        List<ContactBean> list = qb.where(ContactBeanDao.Properties.Uuid.eq(uuid))
                .orderDesc(ContactBeanDao.Properties.Id).list();
        if (list != null && list.size() > 0) {
            bean = list.get(0);
        }
        return bean;
    }


    /**
     * 删除所有联系人记录
     *
     * @return
     */
    public void delAllContact(Context context) {
        ContactBeanDao dao = GreenDBIMManager.instance(context).getContactDao();
        dao.deleteAll();
    }


    /**
     * 添加或者更新
     *
     * @param context
     * @param list
     */
    public void insertOrUpdate(Context context, List<ContactBean> list) {
        if (list != null && list.size() > 0) {
            ContactBeanDao dao = GreenDBIMManager.instance(context).getContactDao();
            dao.insertOrReplaceInTx(list);
        }
    }


    /**
     * 更新
     *
     * @param context
     * @param bean
     */
    public void insertOrUpdate(Context context, ContactBean bean) {
        ContactBeanDao dao = GreenDBIMManager.instance(context).getContactDao();
        dao.insertOrReplace(bean);
    }

    /**
     * 更新
     *
     * @param context
     * @param uuid
     */
    public boolean queryBuddyById(Context context, String uuid) {
        boolean res = false;
        ContactBeanDao dao = GreenDBIMManager.instance(context).getContactDao();
        List<ContactBean> list = dao.queryBuilder()
                .where(ContactBeanDao.Properties.Uuid.eq(uuid))
                .orderDesc(ContactBeanDao.Properties.Id)
                .list();
        if (!ListUtils.isEmpty(list)) {
            ContactBean item = list.get(0);
            if (item.getStatus() == 1
                    || item.getStatus() == 2) { //状态（删除：0；好友：1；拉黑：2）
                res = true;
            }
        }
        return res;
    }


    /**
     * 更新
     *
     * @param context
     * @param uuid
     * @param status
     */
    public void updateStatusById(Context context, String uuid, int status) {
        ContactBeanDao dao = GreenDBIMManager.instance(context).getContactDao();
        List<ContactBean> list = dao.queryBuilder()
                .where(ContactBeanDao.Properties.Uuid.eq(uuid))
                .orderDesc(ContactBeanDao.Properties.Id)
                .list();
        if (!ListUtils.isEmpty(list)) {
            ContactBean item = list.get(0);
            item.setStatus(status);
            dao.update(item);
        }
    }


    /**
     * 按照uuid 查询
     *
     * @param context
     */
    public ContactBean toQueryById(Context context, String uuid) {
        ContactBeanDao dao = GreenDBIMManager.instance(context).getContactDao();
        List<ContactBean> list = dao.queryBuilder()
                .where(ContactBeanDao.Properties.Uuid.eq(uuid))
                .orderDesc(ContactBeanDao.Properties.Id)
                .list();
        if (!ListUtils.isEmpty(list)) {
            return list.get(0);
        }
        return null;
    }


    /**
     * 按照uuid 查询
     *
     * @param context
     */
    public List<ContactBean> toQueryListById(Context context, String uuid) {
        ContactBeanDao dao = GreenDBIMManager.instance(context).getContactDao();
        List<ContactBean> list = dao.queryBuilder()
                .where(ContactBeanDao.Properties.Uuid.eq(uuid))
                .orderDesc(ContactBeanDao.Properties.Id)
                .list();

        return list;
    }

}
