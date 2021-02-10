package com.youmai.hxsdk.loader;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.text.TextUtils;

import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.data.ExCacheMsgBean;
import com.youmai.hxsdk.data.SortComparator;
import com.youmai.hxsdk.db.bean.CacheMsgBean;
import com.youmai.hxsdk.db.bean.GroupInfoBean;
import com.youmai.hxsdk.db.helper.CacheMsgHelper;
import com.youmai.hxsdk.db.helper.GroupInfoHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
 * Created by Gary on 18/3/28 15:53
 */
public class MsgAsyncTaskLoaderAct extends AsyncTaskLoader<List<ExCacheMsgBean>> {

    private final String TAG = MsgAsyncTaskLoaderAct.class.getSimpleName();


    public MsgAsyncTaskLoaderAct(Context context) {
        super(context);
    }

    @Override
    public List<ExCacheMsgBean> loadInBackground() {
        return getCacheMsgFromDBDesc();
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }


    private List<ExCacheMsgBean> getCacheMsgFromDBDesc() {
        List<CacheMsgBean> msgBeanList = CacheMsgHelper.instance().toQueryMsgListDistinctTargetUuid(getContext());

        SortComparator comp = new SortComparator();
        Collections.sort(msgBeanList, comp);

        List<ExCacheMsgBean> tempList = new ArrayList<>();
        for (CacheMsgBean bean : msgBeanList) {
            ExCacheMsgBean exBean = new ExCacheMsgBean(bean);

            String targetName = bean.getTargetName();
            String targetAvatar = bean.getTargetAvatar();
            String targetUuid = bean.getTargetUuid();
            int groupId = bean.getGroupId();

            boolean isTop;
            if (groupId > 0) {
                isTop = HuxinSdkManager.instance().getMsgTop(groupId);
            } else {
                isTop = HuxinSdkManager.instance().getMsgTop(targetUuid);
            }

            if (isTop) {
                exBean.setTop(true);
            }

            if (TextUtils.isEmpty(targetName) && groupId > 0) {
                List<GroupInfoBean> list = GroupInfoHelper.instance().toQueryListByGroupId(getContext(), groupId);
                for (GroupInfoBean item : list) {
                    String groupName = item.getGroup_name();
                    if (!TextUtils.isEmpty(groupName)) {
                        exBean.setDisplayName(groupName);
                        break;
                    }
                }
            } else {
                exBean.setDisplayName(targetName);
            }

            if (TextUtils.isEmpty(targetAvatar)
                    && groupId == 0
                    && !TextUtils.isEmpty(targetUuid)) {
                List<CacheMsgBean> list = CacheMsgHelper.instance().toQueryCacheMsgList(getContext(), targetUuid);
                for (CacheMsgBean item : list) {
                    String avatar = item.getSenderAvatar();
                    if (!TextUtils.isEmpty(avatar)) {
                        exBean.setTargetAvatar(avatar);
                        break;
                    }
                }
            }

            tempList.add(exBean);
        }

        return tempList;
    }
}
