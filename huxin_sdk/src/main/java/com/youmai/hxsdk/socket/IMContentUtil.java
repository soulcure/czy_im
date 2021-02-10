package com.youmai.hxsdk.socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class IMContentUtil {

    private String msgBody;
    /**
     * start from 0.
     */
    private int position = 0;
    private List<IMContentItem> contentList;


    public IMContentUtil() {
        contentList = new ArrayList<>();
        msgBody = "";
    }

    public IMContentUtil(String msg) {
        contentList = new ArrayList<>();
        msgBody = msg;
    }

    public void parseBody(String msg) {
        msgBody = msg;
        parseBody();
    }

    public void parseBody() {
        try {
            JSONArray array = new JSONArray(msgBody);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);

                Iterator it = obj.keys();
                while (it.hasNext()) {
                    String key = (String) it.next();
                    String value = obj.getString(key);
                    IMContentItem item = new IMContentItem();
                    item.item = value;
                    item.itemType = IMContentType.valueOf(key);
                    contentList.add(item);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public String serializeToString() {
        JSONArray arr = new JSONArray();
        try {
            for (int i = 0; i < contentList.size(); i++) {
                IMContentItem item = contentList.get(i);
                JSONObject obj = new JSONObject();
                obj.put(item.itemType.toString(), item.item);
                arr.put(obj);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return arr.toString();
    }


    private void addItem(IMContentType type, String value) {
        IMContentItem item = new IMContentItem();
        item.itemType = type;
        item.item = value;
        contentList.add(item);
    }

    /**
     * insert some text into IM Body.
     *
     * @param text
     */
    public void appendText(String text) {
        addItem(IMContentType.CONTENT_TEXT, text);
    }


    public void appendPictureId(String fileId) {
        addItem(IMContentType.CONTEXT_PICTURE_ID, fileId);
    }

    public void appendAudioId(String fileId) {
        addItem(IMContentType.CONTEXT_AUDIO_ID, fileId);
    }

    public void appendVideoId(String fileId) {
        addItem(IMContentType.CONTEXT_VIDEO_ID, fileId);
    }

    public void appendBigFileId(String fileId, String fileName, String fileSize) {
        addItem(IMContentType.CONTENT_FILE, fileId);
        addItem(IMContentType.CONTENT_FILE_NAME, fileName);
        addItem(IMContentType.CONTENT_FILE_SIZE, fileSize);
    }


    public void appendDescribe(String describe) {
        addItem(IMContentType.CONTEXT_DESCRIBE, describe);
    }

    public void appendImgWidth(String imgWidth) {
        addItem(IMContentType.CONTEXT_IMAGE_W, imgWidth);
    }

    public void appendImgHeight(String imgHeight) {
        addItem(IMContentType.CONTEXT_IMAGE_H, imgHeight);
    }


    public void appendLongitude(String _longitude) {
        addItem(IMContentType.CONTEXT_LONGITUDE, _longitude);
    }

    public void appendLaitude(String laitude) {
        addItem(IMContentType.CONTEXT_LAITUDE, laitude);
    }

    public void appendScale(String scale) {
        addItem(IMContentType.CONTEXT_SCALE, scale);
    }

    public void appendLabel(String label) {
        addItem(IMContentType.CONTEXT_LABEL, label);
    }


    public void appendBarTime(String barTime) { //  Burn after reading time
        addItem(IMContentType.CONTEXT_BAR_TIME, barTime);
    }

    public void appendFileName(String fileName) { //  Burn after reading time
        addItem(IMContentType.CONTENT_FILE_NAME, fileName);
    }

    public void appendFileSize(String fileSize) { //  Burn after reading time
        addItem(IMContentType.CONTENT_FILE_SIZE, fileSize);
    }

    public void appendSourcePhone(String sourcePhone) {
        addItem(IMContentType.CONTEXT_SOURCE_PHONE, sourcePhone);
    }

    public void appendForwardCount(String forwardCount) {
        addItem(IMContentType.CONTEXT_FORWARD_COUNT, forwardCount);
    }

    public void appendRedPackageValue(String value) {
        addItem(IMContentType.CONTEXT_RED_PACKAGE, value);
    }

    public void appendRedPackageTitle(String title) {
        addItem(IMContentType.CONTEXT_RED_TITLE, title);
    }


    public void appendRedPackageUuid(String redUuid) {
        addItem(IMContentType.CONTEXT_RED_UUID, redUuid);
    }

    public void appendRedPackageReceiveName(String receiveName) {
        addItem(IMContentType.CONTEXT_RED_RECEIVE_NAME, receiveName);
    }

    public void appendRedPackageDone(String done) {
        addItem(IMContentType.CONTEXT_RED_RECEIVE_DONE, done);
    }


    public void addVideo(String videoId, String frameId, String name, String size, String time) {
        appendVideoId(videoId);
        appendPictureId(frameId);
        appendFileName(name);
        appendFileSize(size);
        appendBarTime(time);
    }


    public void appendUrl(String url) {
        addItem(IMContentType.CONTENT_URL, url);
    }

    public void appendTitle(String title) {
        addItem(IMContentType.CONTEXT_TITLE, title);
    }

    /**
     * set current position 0.
     */
    public void reset() {
        position = 0;
    }

    /**
     * @return if, has unread IMContent, return next contentType.
     * else return null.
     */
    public IMContentType hasNext() {
        if (contentList.size() > position) {
            return contentList.get(position).itemType;
        }
        return null;
    }


    /**
     * read next element. current position will actual move to next.
     *
     * @return if has return element.
     * else return null.
     */
    public String readNext() {
        if (contentList.size() > position) {
            return contentList.get(position++).item;
        }
        return null;
    }

}
