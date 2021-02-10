package com.youmai.hxsdk.db.helper;

import android.content.Context;
import android.text.TextUtils;

import com.google.protobuf.InvalidProtocolBufferException;
import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.db.bean.GroupInfoBean;
import com.youmai.hxsdk.db.dao.GroupInfoBeanDao;
import com.youmai.hxsdk.db.manager.GreenDBIMManager;
import com.youmai.hxsdk.entity.GroupAndMember;
import com.youmai.hxsdk.proto.YouMaiBasic;
import com.youmai.hxsdk.proto.YouMaiGroup;
import com.youmai.hxsdk.socket.PduBase;
import com.youmai.hxsdk.socket.ReceiveListener;
import com.youmai.hxsdk.utils.GsonUtil;
import com.youmai.hxsdk.utils.ListUtils;

import org.greenrobot.greendao.query.DeleteQuery;
import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.List;


/**
 * Author:  colin
 * Date:    2018-4-20 14:35
 * Description:
 */
public class GroupInfoHelper {

    private static GroupInfoHelper instance;


    public static GroupInfoHelper instance() {
        if (instance == null) {
            instance = new GroupInfoHelper();
        }
        return instance;
    }

    private GroupInfoHelper() {

    }


    /**
     * 查询所有群组的群信息和群成员信息
     *
     * @param context
     * @return
     */
    public List<GroupInfoBean> toQueryGroupList(Context context) {
        GroupInfoBeanDao dao = GreenDBIMManager.instance(context).getGroupInfoDao();
        QueryBuilder<GroupInfoBean> qb = dao.queryBuilder();
        return qb.list();
    }


    /**
     * 查询某个群组的群信息和成员信息
     *
     * @param context
     * @return
     */
    public GroupInfoBean toQueryGroupById(Context context, int groupId) {
        GroupInfoBean bean = null;
        GroupInfoBeanDao dao = GreenDBIMManager.instance(context).getGroupInfoDao();
        QueryBuilder<GroupInfoBean> qb = dao.queryBuilder();
        List<GroupInfoBean> list = qb.where(GroupInfoBeanDao.Properties.Group_id.eq(groupId))
                .orderDesc(GroupInfoBeanDao.Properties.Id).list();
        if (list != null && list.size() > 0) {
            bean = list.get(0);
        }
        return bean;
    }


    /**
     * 删除某个人的所有消息记录
     *
     * @param groupId
     * @return
     */
    public void delGroupInfo(Context context, int groupId) {
        GroupInfoBeanDao dao = GreenDBIMManager.instance(context).getGroupInfoDao();
        QueryBuilder<GroupInfoBean> qb = dao.queryBuilder();
        DeleteQuery<GroupInfoBean> dq = qb.where(GroupInfoBeanDao.Properties.Group_id.eq(groupId))
                .buildDelete();
        dq.executeDeleteWithoutDetachingEntities();
    }


    /**
     * 添加或者更新
     *
     * @param context
     * @param list
     */
    public void insertOrUpdate(Context context, List<GroupInfoBean> list) {
        if (list != null && list.size() > 0) {
            GroupInfoBeanDao dao = GreenDBIMManager.instance(context).getGroupInfoDao();
            dao.insertOrReplaceInTx(list);
        }
    }


    /**
     * 更新
     *
     * @param context
     * @param bean
     */
    public void insertOrUpdate(Context context, GroupInfoBean bean) {
        GroupInfoBeanDao dao = GreenDBIMManager.instance(context).getGroupInfoDao();
        dao.insertOrReplace(bean);
    }


    /**
     * 按照groupId 查询
     *
     * @param context
     */
    public GroupInfoBean toQueryByGroupId(Context context, int groupId) {
        GroupInfoBeanDao dao = GreenDBIMManager.instance(context).getGroupInfoDao();
        List<GroupInfoBean> list = dao.queryBuilder()
                .where(GroupInfoBeanDao.Properties.Group_id.eq(groupId))
                .orderDesc(GroupInfoBeanDao.Properties.Id)
                .list();
        if (!ListUtils.isEmpty(list)) {
            return list.get(0);
        }
        return null;
    }


    /**
     * 按照groupId 查询
     *
     * @param context
     */
    public List<GroupInfoBean> toQueryListByGroupId(Context context, int groupId) {
        GroupInfoBeanDao dao = GreenDBIMManager.instance(context).getGroupInfoDao();
        List<GroupInfoBean> list = dao.queryBuilder()
                .where(GroupInfoBeanDao.Properties.Group_id.eq(groupId))
                .orderDesc(GroupInfoBeanDao.Properties.Id)
                .list();

        return list;
    }


    /**
     * 按照groupId 查询
     *
     * @param context
     */
    public void toQueryByGroupId(final Context context, int groupId, final OnResultCallBack callBack) {
        GroupInfoBeanDao dao = GreenDBIMManager.instance(context).getGroupInfoDao();
        List<GroupInfoBean> list = dao.queryBuilder().where(GroupInfoBeanDao.Properties.Group_id.eq(groupId))
                .orderDesc(GroupInfoBeanDao.Properties.Id).list();
        if (!ListUtils.isEmpty(list)) {
            final GroupInfoBean groupInfoBean = list.get(0);
            String json = groupInfoBean.getGroupMemberJson();
            if (!TextUtils.isEmpty(json)) {
                List<GroupAndMember> members = GsonUtil.parseToArray(json, GroupAndMember[].class);
                if (callBack != null && !ListUtils.isEmpty(members)) {
                    callBack.onMembers(members);
                }
            } else {
                HuxinSdkManager.instance().reqGroupMember(groupId, new ReceiveListener() {
                    @Override
                    public void OnRec(PduBase pduBase) {
                        try {
                            YouMaiGroup.GroupMemberRsp ack = YouMaiGroup.GroupMemberRsp.parseFrom(pduBase.body);
                            if (ack.getResult() == YouMaiBasic.ResultCode.RESULT_CODE_SUCCESS) {
                                GroupInfoBean bean = updateGroupInfo(groupInfoBean, ack, callBack);
                                GroupInfoHelper.instance().insertOrUpdate(context, bean);
                            }

                        } catch (InvalidProtocolBufferException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        } else {
            HuxinSdkManager.instance().reqGroupMember(groupId, new ReceiveListener() {
                @Override
                public void OnRec(PduBase pduBase) {
                    try {
                        YouMaiGroup.GroupMemberRsp ack = YouMaiGroup.GroupMemberRsp.parseFrom(pduBase.body);
                        if (ack.getResult() == YouMaiBasic.ResultCode.RESULT_CODE_SUCCESS) {
                            GroupInfoBean bean = updateGroupInfo(null, ack, callBack);
                            GroupInfoHelper.instance().insertOrUpdate(context, bean);
                        }

                    } catch (InvalidProtocolBufferException e) {
                        e.printStackTrace();
                    }
                }
            });
        }


    }

    private GroupInfoBean updateGroupInfo(GroupInfoBean bean, YouMaiGroup.GroupMemberRsp ack,
                                          OnResultCallBack callBack) {
        List<YouMaiGroup.GroupMemberItem> memberListList = ack.getMemberListList();
        if (bean == null) {
            bean = new GroupInfoBean();
            bean.setGroup_id(ack.getGroupId());
        }

        List<GroupAndMember> list = new ArrayList<>();
        for (YouMaiGroup.GroupMemberItem item : memberListList) {
            GroupAndMember member = new GroupAndMember();
            member.setMember_id(item.getMemberId());
            member.setMember_name(item.getMemberName());
            member.setMember_role(item.getMemberRole());
            member.setUser_name(item.getUserName());
            list.add(member);
        }
        bean.setGroupMemberJson(GsonUtil.format(list));


        if (callBack != null && !ListUtils.isEmpty(list)) {
            callBack.onMembers(list);
        }
        return bean;

    }


    public interface OnResultCallBack {
        void onMembers(List<GroupAndMember> list);
    }

}
