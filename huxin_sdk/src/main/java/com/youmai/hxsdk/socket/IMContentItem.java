package com.youmai.hxsdk.socket;

/**
 * @author xiesong
 * @time 4.29.2016
 */
public class IMContentItem {
    /**
     * if itemType is not ContentText, then remove will remove item whole.
     * if is ContentText, remove function just remove one char.
     */
    IMContentType itemType;
    String item;
}
