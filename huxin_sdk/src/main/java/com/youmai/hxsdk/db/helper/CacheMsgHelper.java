package com.youmai.hxsdk.db.helper;

import android.content.Context;
import android.text.TextUtils;

import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.config.ColorsConfig;
import com.youmai.hxsdk.db.bean.CacheMsgBean;
import com.youmai.hxsdk.db.dao.CacheMsgBeanDao;
import com.youmai.hxsdk.db.manager.GreenDBIMManager;
import com.youmai.hxsdk.im.cache.CacheMsgTxt;
import com.youmai.hxsdk.utils.ListUtils;

import org.greenrobot.greendao.query.DeleteQuery;
import org.greenrobot.greendao.query.QueryBuilder;
import org.greenrobot.greendao.query.WhereCondition;

import java.util.ArrayList;
import java.util.List;


/**
 * Author:  Kevin Feng
 * Email:   597415099@qq.com
 * Date:    2016-12-06 14:35
 * Description:
 */
public class CacheMsgHelper {

    private static CacheMsgHelper instance;


    public static CacheMsgHelper instance() {
        if (instance == null) {
            instance = new CacheMsgHelper();
        }
        return instance;
    }

    private CacheMsgHelper() {

    }

    /**
     * 添加或者更新
     *
     * @param context
     * @param cacheMsgBean
     */
    public void insertOrUpdate(Context context, CacheMsgBean cacheMsgBean) {
        CacheMsgBeanDao cacheMsgBeanDao = GreenDBIMManager.instance(context).getCacheMsgDao();
        if (cacheMsgBean.getId() != null && cacheMsgBean.getId() != -1L) {
            cacheMsgBeanDao.update(cacheMsgBean);
        } else {
            cacheMsgBean.setId(null);
            cacheMsgBeanDao.insert(cacheMsgBean);
        }
    }


    /**
     * 查询与某人的聊天历史记录
     *
     * @param context
     * @param dstUuid
     * @param setRead 是否将未读消息置为已读消息
     * @return
     */
    public List<CacheMsgBean> toQueryCacheMsgListAndSetRead(Context context, String dstUuid, boolean setRead) {
        CacheMsgBeanDao cacheMsgBeanDao = GreenDBIMManager.instance(context).getCacheMsgDao();
        QueryBuilder<CacheMsgBean> qb = cacheMsgBeanDao.queryBuilder();

        List<CacheMsgBean> list = qb.where(CacheMsgBeanDao.Properties.TargetUuid.eq(dstUuid))
                .orderAsc(CacheMsgBeanDao.Properties.Id).list();

        if (setRead) {
            List<CacheMsgBean> unReadList = new ArrayList<>();
            for (CacheMsgBean checkBean : list) {
                if (checkBean.getMsgStatus() == CacheMsgBean.SEND_GOING) {
                    checkBean.setMsgStatus(CacheMsgBean.SEND_FAILED);
                } else if (checkBean.getMsgStatus() == CacheMsgBean.RECEIVE_UNREAD) {
                    checkBean.setMsgStatus(CacheMsgBean.RECEIVE_READ);
                }
                unReadList.add(checkBean);
            }
            if (unReadList.size() > 0) {
                CacheMsgHelper.instance().updateList(context, unReadList);
            }


            List<CacheMsgBean> emptyMsg = new ArrayList<>();

            for (CacheMsgBean item : list) {
                if (item.getMsgType() == CacheMsgBean.SEND_TEXT) {
                    CacheMsgTxt cacheMsgTxt = (CacheMsgTxt) item.getJsonBodyObj();
                    String txtContent = cacheMsgTxt.getMsgTxt();
                    if (txtContent.equals(ColorsConfig.GROUP_EMPTY_MSG)) {
                        emptyMsg.add(item);
                    }
                }
            }

            if (emptyMsg.size() > 0) {
                list.removeAll(emptyMsg);
            }

        }

        return list;
    }


    /**
     * 查询与某人的聊天历史记录
     *
     * @param context
     * @param groupId
     * @param setRead 是否将未读消息置为已读消息
     * @return
     */
    public List<CacheMsgBean> toQueryCacheMsgListAndSetRead(Context context, int groupId, boolean setRead) {
        CacheMsgBeanDao cacheMsgBeanDao = GreenDBIMManager.instance(context).getCacheMsgDao();
        QueryBuilder<CacheMsgBean> qb = cacheMsgBeanDao.queryBuilder();

        List<CacheMsgBean> list = qb.where(CacheMsgBeanDao.Properties.GroupId.eq(groupId))
                .orderAsc(CacheMsgBeanDao.Properties.Id).list();

        if (setRead) {
            List<CacheMsgBean> unReadList = new ArrayList<>();
            for (CacheMsgBean checkBean : list) {
                if (checkBean.getMsgStatus() == CacheMsgBean.SEND_GOING) {
                    checkBean.setMsgStatus(CacheMsgBean.SEND_FAILED);
                } else if (checkBean.getMsgStatus() == CacheMsgBean.RECEIVE_UNREAD) {
                    checkBean.setMsgStatus(CacheMsgBean.RECEIVE_READ);
                }

                unReadList.add(checkBean);
            }
            if (unReadList.size() > 0) {
                CacheMsgHelper.instance().updateList(context, unReadList);
            }
        }

        List<CacheMsgBean> emptyMsg = new ArrayList<>();

        for (CacheMsgBean item : list) {
            if (item.getMsgType() == CacheMsgBean.SEND_TEXT) {
                CacheMsgTxt cacheMsgTxt = (CacheMsgTxt) item.getJsonBodyObj();
                String txtContent = cacheMsgTxt.getMsgTxt();
                if (txtContent.equals(ColorsConfig.GROUP_EMPTY_MSG)) {
                    emptyMsg.add(item);
                }
            }
        }

        if (emptyMsg.size() > 0) {
            list.removeAll(emptyMsg);
        }


        return list;
    }


    /**
     * 查询与某人的聊天历史记录
     *
     * @param context
     * @param desUuid
     * @return
     */
    public List<CacheMsgBean> toQueryCacheMsgList(Context context, String desUuid) {
        QueryBuilder<CacheMsgBean> qb = GreenDBIMManager.instance(context).getCacheMsgDao().queryBuilder();
        return qb.where(CacheMsgBeanDao.Properties.TargetUuid.eq(desUuid))
                .orderAsc(CacheMsgBeanDao.Properties.Id).list();
    }


    /**
     * 查询 sendUuid 发送给 receiverUuid 的所有消息
     * 备注：IMMsgManager
     * 按 id升序查询
     * sender_phone=? and receiver_phone=?
     *
     * @param sendUuid
     * @param receiverUuid
     * @return
     */
    public List<CacheMsgBean> toQueryCacheMsgList(Context context, String sendUuid, String receiverUuid) {
        QueryBuilder<CacheMsgBean> qb = GreenDBIMManager.instance(context).getCacheMsgDao().queryBuilder();
        return qb.where(qb.and(CacheMsgBeanDao.Properties.ReceiverUserId.eq(receiverUuid),
                CacheMsgBeanDao.Properties.SenderUserId.eq(sendUuid)))
                .orderAsc(CacheMsgBeanDao.Properties.Id).list();
    }


    /**
     * 备注：MsgAsyncTaskLoader & MyMsgListFragment 同一线程调用
     * 按 Sql
     *
     * @param sql
     * @return
     */
    public List<CacheMsgBean> sqlToQueryList(Context context, String sql) {
        QueryBuilder<CacheMsgBean> qb = GreenDBIMManager.instance(context).getCacheMsgDao().queryBuilder();
        return qb.where(new WhereCondition.StringCondition(sql)).list();
    }

    /**
     * 删除某个人的所有消息记录
     *
     * @param dstUuid
     * @return
     */
    public void deleteAllMsg(Context context, String dstUuid) {
        CacheMsgBeanDao cacheMsgBeanDao = GreenDBIMManager.instance(context).getCacheMsgDao();
        QueryBuilder<CacheMsgBean> qb = cacheMsgBeanDao.queryBuilder();
        DeleteQuery<CacheMsgBean> dq = qb.where(CacheMsgBeanDao.Properties.TargetUuid.eq(dstUuid))
                .buildDelete();
        dq.executeDeleteWithoutDetachingEntities();
    }


    /**
     * 删除某个人的所有消息记录并插入一条空的消息记录已保存聊天历史
     *
     * @param dstUuid
     * @return
     */
    public void deleteAllMsgAndSaveEntry(Context context, String dstUuid) {
        CacheMsgBeanDao cacheMsgBeanDao = GreenDBIMManager.instance(context).getCacheMsgDao();
        QueryBuilder<CacheMsgBean> qb = cacheMsgBeanDao.queryBuilder();
        List<CacheMsgBean> list = qb.where(CacheMsgBeanDao.Properties.TargetUuid.eq(dstUuid))
                .orderAsc(CacheMsgBeanDao.Properties.Id).list();
        CacheMsgBean saveEmptyMsg = null;
        if (list != null && list.size() > 0) {
            for (CacheMsgBean item : list) {
                if (!TextUtils.isEmpty(item.getContentJsonBody())) {
                    saveEmptyMsg = item;
                    saveEmptyMsg.setId(null);
                    saveEmptyMsg.setMsgType(CacheMsgBean.SEND_TEXT)
                            .setJsonBodyObj(new CacheMsgTxt().setMsgTxt(ColorsConfig.GROUP_EMPTY_MSG));
                    break;
                }
            }
        }

        DeleteQuery<CacheMsgBean> dq = qb.where(CacheMsgBeanDao.Properties.TargetUuid.eq(dstUuid))
                .buildDelete();
        dq.executeDeleteWithoutDetachingEntities();

        if (saveEmptyMsg != null) {
            cacheMsgBeanDao.insert(saveEmptyMsg);
        }
    }


    /**
     * 删除某个人的所有消息记录
     *
     * @param selfUuid
     * @param dstUuid
     * @return
     */
    public void deleteAllMsg(Context context, String selfUuid, String dstUuid) {
        CacheMsgBeanDao cacheMsgBeanDao = GreenDBIMManager.instance(context).getCacheMsgDao();
        QueryBuilder<CacheMsgBean> qb = cacheMsgBeanDao.queryBuilder();
        DeleteQuery<CacheMsgBean> dq = qb.where(qb.or(
                qb.and(CacheMsgBeanDao.Properties.ReceiverUserId.eq(dstUuid),
                        CacheMsgBeanDao.Properties.SenderUserId.eq(selfUuid)),
                qb.and(CacheMsgBeanDao.Properties.SenderUserId.eq(dstUuid),
                        CacheMsgBeanDao.Properties.ReceiverUserId.eq(selfUuid))))
                .orderAsc(CacheMsgBeanDao.Properties.Id).buildDelete();

        dq.executeDeleteWithoutDetachingEntities();
    }


    /**
     * 删除某条记录
     *
     * @param id
     */
    public void deleteOneMsg(Context context, Long id) {
        CacheMsgBeanDao cacheMsgBeanDao = GreenDBIMManager.instance(context).getCacheMsgDao();
        QueryBuilder<CacheMsgBean> qb = cacheMsgBeanDao.queryBuilder();
        DeleteQuery<CacheMsgBean> dq = qb.where(CacheMsgBeanDao.Properties.Id.eq(id))
                .buildDelete();

        dq.executeDeleteWithoutDetachingEntities();
    }


    /**
     * 根据id查询
     */
    public CacheMsgBean queryById(Context context, long msgId) {
        CacheMsgBeanDao cacheMsgDao = GreenDBIMManager.instance(context).getCacheMsgDao();
        return cacheMsgDao.loadByRowId(msgId);
    }


    /**
     * 备注：IMMsgManager
     * 按 id升序查询
     * (receiver_phone=? and sender_phone=?) or (sender_phone=? and receiver_phone=?)
     *
     * @param dstUuid
     * @param selfUuid
     * @return
     */
    public List<CacheMsgBean> toQueryOrAscById(Context context, String dstUuid, String selfUuid) {
        QueryBuilder<CacheMsgBean> qb = GreenDBIMManager.instance(context).getCacheMsgDao().queryBuilder();

        WhereCondition condition1 = qb.and(CacheMsgBeanDao.Properties.SenderUserId.eq(dstUuid),
                CacheMsgBeanDao.Properties.ReceiverUserId.eq(selfUuid));
        WhereCondition condition2 = qb.and(CacheMsgBeanDao.Properties.ReceiverUserId.eq(dstUuid),
                CacheMsgBeanDao.Properties.SenderUserId.eq(selfUuid));

        return qb.where(qb.or(condition1, condition2))
                .orderAsc(CacheMsgBeanDao.Properties.Id).list();
    }

    /**
     * 备注：IMMsgManager
     * 按 id升序查询
     * id>? and ((receiver_phone=? and sender_phone=?) or (sender_phone=? and receiver_phone=?))
     *
     * @param statIndex
     * @param dstUuid
     * @param selfUuid
     * @return
     */
    public List<CacheMsgBean> toQueryOrAscById(Context context, long statIndex, String dstUuid, String selfUuid) {
        QueryBuilder<CacheMsgBean> qb = GreenDBIMManager.instance(context).getCacheMsgDao().queryBuilder();

        WhereCondition condition1 = qb.and(CacheMsgBeanDao.Properties.SenderUserId.eq(dstUuid),
                CacheMsgBeanDao.Properties.ReceiverUserId.eq(selfUuid));
        WhereCondition condition2 = qb.and(CacheMsgBeanDao.Properties.ReceiverUserId.eq(dstUuid),
                CacheMsgBeanDao.Properties.SenderUserId.eq(selfUuid));

        return qb.where(qb.and(CacheMsgBeanDao.Properties.Id.gt(statIndex), qb.or(condition1, condition2)))
                .orderAsc(CacheMsgBeanDao.Properties.Id).list();
    }


    /**
     * 备注：IMMsgManager
     * 按 id升序查询
     * id>? and ((receiver_phone=? and sender_phone=?) or (sender_phone=? and receiver_phone=?))
     *
     * @param statIndex
     * @param groupId
     * @param selfUuid
     * @return
     */
    public List<CacheMsgBean> toQueryOrAscById(Context context, long statIndex, int groupId, String selfUuid) {
        QueryBuilder<CacheMsgBean> qb = GreenDBIMManager.instance(context).getCacheMsgDao().queryBuilder();

        WhereCondition condition = qb.and(CacheMsgBeanDao.Properties.GroupId.eq(groupId),
                CacheMsgBeanDao.Properties.ReceiverUserId.eq(selfUuid),
                CacheMsgBeanDao.Properties.Id.gt(statIndex));

        return qb.where(condition).orderAsc(CacheMsgBeanDao.Properties.Id).list();
    }


    /**
     * 备注：IMListAdapter
     *
     * @param id
     * @return
     */
    public List<CacheMsgBean> toQueryDescById(Context context, long id) {
        CacheMsgBeanDao cacheMsgDao = GreenDBIMManager.instance(context).getCacheMsgDao();
        return cacheMsgDao.queryBuilder()
                .where(CacheMsgBeanDao.Properties.Id.eq(id))
                .orderDesc(CacheMsgBeanDao.Properties.Id).list();
    }

    /**
     * 备注：IMMsgManager
     * 批量更新
     *
     * @param msgBeanList
     */
    public void updateList(Context context, List<CacheMsgBean> msgBeanList) {
        CacheMsgBeanDao cacheMsgBeanDao = GreenDBIMManager.instance(context).getCacheMsgDao();
        cacheMsgBeanDao.updateInTx(msgBeanList);
    }


    /**
     * 备注：IMMsgManager
     * 单条更新
     *
     * @param bean
     */
    public void updateList(Context context, CacheMsgBean bean) {
        CacheMsgBeanDao cacheMsgBeanDao = GreenDBIMManager.instance(context).getCacheMsgDao();
        if (bean.getId() == null) {
            cacheMsgBeanDao.insert(bean);
        } else {
            cacheMsgBeanDao.update(bean);
        }
    }


    public List<CacheMsgBean> getCacheMsgBeanListFromStartIndex(Context context, long startIndex,
                                                                String dstUuid, boolean setRead) {
        String selfUuid = HuxinSdkManager.instance().getUuid();
        List<CacheMsgBean> list =
                CacheMsgHelper.instance().toQueryOrAscById(context, startIndex, selfUuid, dstUuid);

        if (setRead) {
            List<CacheMsgBean> unReadList = new ArrayList<>();
            for (CacheMsgBean checkBean : list) {
                if (checkBean.getMsgStatus() == CacheMsgBean.RECEIVE_UNREAD) {
                    checkBean.setMsgStatus(CacheMsgBean.RECEIVE_READ);
                    unReadList.add(checkBean);
                }
            }
            if (unReadList.size() > 0) {
                CacheMsgHelper.instance().updateList(context, unReadList);
            }
        }

        return list;
    }


    public List<CacheMsgBean> getCacheMsgBeanListFromStartIndex(Context context, long startIndex,
                                                                int groupId, boolean setRead) {
        String selfUuid = HuxinSdkManager.instance().getUuid();
        List<CacheMsgBean> list =
                CacheMsgHelper.instance().toQueryOrAscById(context, startIndex, groupId, selfUuid);

        if (setRead) {
            List<CacheMsgBean> unReadList = new ArrayList<>();
            for (CacheMsgBean checkBean : list) {
                if (checkBean.getMsgStatus() == CacheMsgBean.RECEIVE_UNREAD) {
                    checkBean.setMsgStatus(CacheMsgBean.RECEIVE_READ);
                    unReadList.add(checkBean);
                }
            }
            if (unReadList.size() > 0) {
                CacheMsgHelper.instance().updateList(context, unReadList);
            }
        }

        return list;
    }


    public int getUnreadCacheMsgBeanListCount(final Context context, String dstUuid) {
        CacheMsgBeanDao cacheMsgBeanDao = GreenDBIMManager.instance(context).getCacheMsgDao();
        QueryBuilder<CacheMsgBean> qb = cacheMsgBeanDao.queryBuilder();

        List<CacheMsgBean> list = qb.where(CacheMsgBeanDao.Properties.TargetUuid.eq(dstUuid))
                .orderDesc(CacheMsgBeanDao.Properties.Id).list();


        List<CacheMsgBean> unReadList = new ArrayList<>();
        for (CacheMsgBean checkBean : list) {
            if (checkBean.getMsgStatus() == CacheMsgBean.RECEIVE_UNREAD) {
                checkBean.setMsgStatus(CacheMsgBean.RECEIVE_READ);
                unReadList.add(checkBean);
            }
        }
        return unReadList.size();
    }

    /**
     * 清空表所有消息
     */
    public void deleteAll(Context context) {
        GreenDBIMManager.instance(context).getCacheMsgDao().deleteAll();
    }


    public List<CacheMsgBean> toQueryMsgListByGroup(Context context) {
        QueryBuilder<CacheMsgBean> qb = GreenDBIMManager.instance(context).getCacheMsgDao().queryBuilder();
        String queryString = "1=1"   //where true
                + " GROUP BY "
                + CacheMsgBeanDao.Properties.TargetUuid.columnName
                + " ORDER BY "
                + CacheMsgBeanDao.Properties.MsgTime.columnName
                + " DESC";
        return qb.where(new WhereCondition.StringCondition(queryString)).list();
    }


    /**
     * 查询所有消息（好友消息 GROUP_TYPE=0，普通群聊 GROUP_TYPE=1，社群群聊 GROUP_TYPE=2，客服消息 GROUP_TYPE=101）
     *
     * @param context
     * @return 此处返回 好友消息 普通群聊 社群群聊
     */
    public List<CacheMsgBean> toQueryMsgListDistinctTargetUuid(Context context) {
        QueryBuilder<CacheMsgBean> qb = GreenDBIMManager.instance(context).getCacheMsgDao().queryBuilder();
        String queryString = "GROUP_TYPE<=2"   //where true
                + " GROUP BY "
                + CacheMsgBeanDao.Properties.TargetUuid.columnName
                + " ORDER BY "
                + CacheMsgBeanDao.Properties.MsgTime.columnName
                + " DESC";
        return qb.where(new WhereCondition.StringCondition(queryString)).list();
    }


    /**
     * 获取消息
     *
     * @param id
     * @return
     */

    public CacheMsgBean getCacheMsgById(Context context, long id) {
        CacheMsgBeanDao cacheMsgBeanDao = GreenDBIMManager.instance(context).getCacheMsgDao();
        CacheMsgBean msgBean = cacheMsgBeanDao.queryBuilder().where(CacheMsgBeanDao.Properties.Id.eq(id)).unique();
        return msgBean;
    }


    /**
     * 获取业主消息
     *
     * @return
     */

    public List<CacheMsgBean> getAllOwnerMsg(Context context) {
        QueryBuilder<CacheMsgBean> qb = GreenDBIMManager.instance(context).getCacheMsgDao().queryBuilder();
        String queryString = "GROUP_TYPE=101"
                + " GROUP BY "
                + CacheMsgBeanDao.Properties.TargetUuid.columnName
                + " ORDER BY "
                + CacheMsgBeanDao.Properties.MsgTime.columnName
                + " DESC";
        return qb.where(new WhereCondition.StringCondition(queryString)).list();
    }


    /**
     * 获取社群消息
     *
     * @return
     */

    public List<CacheMsgBean> getAllCommMsg(Context context) {
        QueryBuilder<CacheMsgBean> qb = GreenDBIMManager.instance(context).getCacheMsgDao().queryBuilder();
        String queryString = "GROUP_TYPE=2"
                + " GROUP BY "
                + CacheMsgBeanDao.Properties.TargetUuid.columnName
                + " ORDER BY "
                + CacheMsgBeanDao.Properties.MsgTime.columnName
                + " DESC";
        return qb.where(new WhereCondition.StringCondition(queryString)).list();
    }


    /**
     * 获取最近的业主消息
     *
     * @return
     */

    public CacheMsgBean getLastOwnerMsg(Context context) {
        CacheMsgBeanDao cacheMsgBeanDao = GreenDBIMManager.instance(context).getCacheMsgDao();
        List<CacheMsgBean> list = cacheMsgBeanDao.queryBuilder()
                .where(CacheMsgBeanDao.Properties.GroupType.eq(101))
                .orderDesc(CacheMsgBeanDao.Properties.MsgTime)
                .list();
        if (!ListUtils.isEmpty(list)) {
            return list.get(0);
        }
        return null;
    }


    /**
     * 获取最近的社群消息
     *
     * @return
     */

    public CacheMsgBean getLastCommMsg(Context context) {
        CacheMsgBeanDao cacheMsgBeanDao = GreenDBIMManager.instance(context).getCacheMsgDao();
        List<CacheMsgBean> list = cacheMsgBeanDao.queryBuilder()
                .where(CacheMsgBeanDao.Properties.GroupType.eq(2))
                .orderDesc(CacheMsgBeanDao.Properties.MsgTime)
                .list();
        if (!ListUtils.isEmpty(list)) {
            return list.get(0);
        }
        return null;
    }


    /**
     * 通过群ID获取所有的聊天数据
     *
     * @param groupId
     * @return
     */
    public CacheMsgBean toQueryCacheMsgGroupId(Context context, int groupId) {
        CacheMsgBeanDao cacheMsgBeanDao = GreenDBIMManager.instance(context).getCacheMsgDao();
        List<CacheMsgBean> list = cacheMsgBeanDao.queryBuilder()
                .where(CacheMsgBeanDao.Properties.GroupId.eq(groupId))
                .orderDesc(CacheMsgBeanDao.Properties.Id)
                .list();
        if (!ListUtils.isEmpty(list)) {
            return list.get(0);
        }
        return null;
    }


    /**
     * 通过群ID获取所有的聊天数据
     *
     * @param groupId
     * @return
     */
    public List<CacheMsgBean> toQueryCacheAllMsgGroupId(Context context, int groupId) {
        CacheMsgBeanDao cacheMsgBeanDao = GreenDBIMManager.instance(context).getCacheMsgDao();
        List<CacheMsgBean> list = cacheMsgBeanDao.queryBuilder()
                .where(CacheMsgBeanDao.Properties.GroupId.eq(groupId))
                .orderDesc(CacheMsgBeanDao.Properties.Id)
                .list();
        return list;
    }


    /**
     * 通过群ID删除所有群聊天数据
     *
     * @param groupId
     * @return
     */

    public void delCacheMsgGroupId(Context context, int groupId) {
        CacheMsgBeanDao cacheMsgBeanDao = GreenDBIMManager.instance(context).getCacheMsgDao();
        QueryBuilder<CacheMsgBean> qb = cacheMsgBeanDao.queryBuilder();
        DeleteQuery<CacheMsgBean> dq = qb.where(CacheMsgBeanDao.Properties.GroupId.eq(groupId))
                .buildDelete();
        dq.executeDeleteWithoutDetachingEntities();
    }

}
