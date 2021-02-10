package com.youmai.hxsdk.utils;

import android.text.TextUtils;
import android.util.Log;

import com.youmai.hxsdk.entity.cn.DuoYinZi;
import com.youmai.hxsdk.entity.cn.pinyin.Pinyin;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fan on 2016/8/9.
 */
public class PinYinUtils {

    public static String getPinYin(String text) {
        char[] chars = text.toCharArray();
        StringBuilder sb = new StringBuilder();
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();

        //取消音调
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        //大写
        format.setCaseType(HanyuPinyinCaseType.UPPERCASE);

        for (char ch : chars) {
            if (Character.isWhitespace(ch)) {
                //如果是空格
                continue;
            }

            if (ch > 128 || ch < -127) {
                try {
                    //数组是有多音字
                    String[] array = PinyinHelper.toHanyuPinyinStringArray(ch, format);
                    if (array != null && array.length >= 0) {
                        sb.append(array[0]);
                    }


                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    e.getMessage();
                }
            } else {
                //#$%^
                return "#";
            }
        }
        return sb.toString();
    }

    public static String toPinYin(String string) {
        String str = "";
        if (string != null) {
            for (char hanzi : string.toCharArray()) {
                HanyuPinyinOutputFormat hanyuPinyin = new HanyuPinyinOutputFormat();
                String[] pinyinArray = null;
                try {
                    //是否在汉字范围内
                    if (hanzi >= 0x4e00 && hanzi <= 0x9fa5) {
                        pinyinArray = PinyinHelper.toHanyuPinyinStringArray(hanzi, hanyuPinyin);

                        if (String.valueOf(hanzi).equals("覅")) {
                            str = str + "fiao" + " ";

                        }
                        if (pinyinArray != null) {
                            for (String pinyinStr : pinyinArray) {
                                // String pinyinStr = pinyinArray[0];
                                str = str + pinyinStr.substring(0, pinyinStr.length() - 1) + " ";
                            }

                        }
                    } else {
                        str = str + Character.toString(hanzi) + " ";

                    }
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    e.printStackTrace();
                }
            }
        }

        if (!str.isEmpty()) {
            char c = str.charAt(0);
            if (((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z'))) {
                return str.toUpperCase();
            } else {
                return "#";
            }
        } else {
            return "#";
        }
    }

    /*
     string 需要转拼音的字串
     letter 需要匹配的首字母 针对多音字
     */
    public static String NameToPinYin(String string, String letter) {
        String str = "";
        if (string != null) {
            for (int i = 0; i < string.length(); i++) {
//        for (char hanzi : string.toCharArray()) {
                char hanzi = string.charAt(i);
                HanyuPinyinOutputFormat hanyuPinyin = new HanyuPinyinOutputFormat();
                String[] pinyinArray = null;
                try {
                    //是否在汉字范围内
                    if (hanzi >= 0x4e00 && hanzi <= 0x9fa5) {
                        pinyinArray = PinyinHelper.toHanyuPinyinStringArray(hanzi, hanyuPinyin);

                        if (String.valueOf(hanzi).equals("覅")) {
                            str = str + "fiao" + " ";

                        }
                        if (pinyinArray != null) {

                            String pinyinStr = pinyinArray[0];
                            if (!TextUtils.isEmpty(letter)) {
                                if (i == 0) {
                                    //多音字检索 和传入字母相同的拼音
                                    for (String py : pinyinArray) {
                                        py = py.toUpperCase();
                                        if (py.startsWith(letter)) {
                                            pinyinStr = py;
                                            break;
                                        }
                                    }
                                }
                            }

                            str = str + pinyinStr.substring(0, pinyinStr.length() - 1) + " ";
                        }
                    } else {
                        str = str + Character.toString(hanzi) + " ";

                    }
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    e.printStackTrace();
                }
            }
        }

        if (!str.isEmpty()) {
            char c = str.charAt(0);
            if (((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z'))) {
                return str.toUpperCase();
            } else {
                return "#";
            }
        } else {
            return "#";
        }
    }


    /**
     * 字串转拼音，大写存储
     *
     * @param string
     * @return
     */
    public static DuoYinZi HanziToPinYin(String string) {
        DuoYinZi duoYinZi = new DuoYinZi();

        DuoYinZi currDuoYinZi = duoYinZi;
        for (char hanzi : string.toCharArray()) {
            HanyuPinyinOutputFormat hanyuPinyin = new HanyuPinyinOutputFormat();
            hanyuPinyin.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
            hanyuPinyin.setCaseType(HanyuPinyinCaseType.UPPERCASE);
            String[] pinyinArray = null;
            ArrayList<String> pingArray = new ArrayList<String>();

            DuoYinZi nextDuoYinZi = new DuoYinZi();
            try {
                //是否在汉字范围内
                if (hanzi >= 0x4e00 && hanzi <= 0x9fa5) {
                    pinyinArray = PinyinHelper.toHanyuPinyinStringArray(hanzi, hanyuPinyin);

                    if (pinyinArray != null) {
                        for (String pinyinStr : pinyinArray) {
                            pingArray.add(pinyinStr);
                        }
                        nextDuoYinZi.setDuoYinZi(pingArray);
                    } else {
                        //解析不出的字，如生僻字
                        pingArray.add(Character.toString(hanzi));
                        nextDuoYinZi.setDuoYinZi(pingArray);
                    }
                } else {
                    pingArray.add(Character.toString(hanzi));
                    nextDuoYinZi.setDuoYinZi(pingArray);
                }
            } catch (BadHanyuPinyinOutputFormatCombination e) {
                e.printStackTrace();
            }

            if (currDuoYinZi.getDuoYinZi() == null) {
                duoYinZi.setDuoYinZi(nextDuoYinZi.getDuoYinZi());
            } else if (currDuoYinZi.getNextDuoYinZi() == null) {
                currDuoYinZi.setNextDuoYinZi(nextDuoYinZi);
                currDuoYinZi = currDuoYinZi.getNextDuoYinZi();
            }
        }
        return duoYinZi;
    }


    /**
     * str：汉字
     * key:拼音
     * @param str
     * @param key
     * @return
     */
    public static List<Integer> match2(final String str, String key) {
        System.out.println("matching: " + str + " " + key);
        if (key.length() == 0)
            new ArrayList<Integer>();
        List<Integer> matchIndex = new ArrayList<>();

        List<String> st = new ArrayList<>();
        //先把第一个汉字拿出来
        for (int k = 0; k < str.length(); k++) {
            st.add(Pinyin.toPinyin(str.charAt(k)));
        }

        int j = 0;
        //st 集合装着每个汉字的拼音
        for (int m = 0; m < st.size(); m++) {
            String s = st.get(m);        //m : 第几个汉字 对应的拼音
            for (int n = 0; n < s.length() && j < key.length(); n++) {
//                if (!s.startsWith(key)) {
//                    continue;
//                }
                Log.e("YW", "key: " + key +"\tj: " + j + "\t汉字s：" + s + "\tn: " + n);
                if (key.contains(s)) {
                    j += s.length() - 1;
                    matchIndex.add(m);
                    Log.e("YW", "n: " + n);
                    key = key.substring(s.length(), key.length());
                    j = 0;
                    continue;
                }

                //先判断第m个汉字拼音是否包含有key 如果有就继续下面的操作
                if (!s.contains(key)) {
                    continue;
                }

                if (key.charAt(j) == s.charAt(n)) { // zh  zhou   n : 第m个汉字拼音的  第n个字母
                    j++;  //1 z  2 h       j : 表示关键字的第几个字母
                    Log.e("YW", "j: " + j +"\t关键字: " /*+ key.charAt(j)*/ + "\t对应着汉字拼字： " + s.charAt(n));
                }
                //j=2
                if (j == key.length()) {
                    if (!matchIndex.contains(m))
                        matchIndex.add(m);
                }
            }
        }
        return matchIndex;
    }

}
