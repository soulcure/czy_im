package com.youmai.hxsdk.utils;


import java.util.Locale;

public class CodingUtils {

    private CodingUtils() {
        throw new AssertionError();
    }

    public static String getMsgId(long msgid) {
        String szData;
        long tmp_msgid = 0;
        long randnum = 19834;
        int hash_key = 0;

        tmp_msgid = ((msgid ^ (randnum << 32)) & 0xFFFFFFFF00000000L) | (((msgid ^ randnum) & 0x00000000FFFFFFFFL));

        if (tmp_msgid < 0) {
            tmp_msgid = getUnSignedLong(tmp_msgid);
        }

        szData = "19834" + tmp_msgid;
        hash_key = get_crc16(szData);

        //bug here fixed by zhangzh
        //return "" + (int) (hash_key % 100) + tmp_msgid;
        return String.format(Locale.CHINA, "%02d", hash_key % 100) + tmp_msgid;
    }


    public static char get_crc16(String _data) {
        char crc = (char) 0xFFFF;
        int i = 0;

        for (i = 0; i < _data.length(); i++) {
            int j = 0;

            for (j = 0; j < 8; j++) {
                int c15 = ((crc >> 15 & 1) & 1);
                int bit = ((((int) _data.charAt(i)) >> (7 - j) & 1) & 1);

                crc <<= 1;

                if ((c15 ^ bit) != 0) {
                    crc ^= 0x1021;
                }
            }
        }

        return (crc);
    }

    public static long getUnSignedLong(long l) {
        return getLong(longToDword(l), 0);
    }

    // 将long型数据转换为Dword的字节数组（C/C++的无符号整数）
    private static byte[] longToDword(long value) {

        byte[] data = new byte[4];

        for (int i = 0; i < data.length; i++) {
            data[i] = (byte) (value >> (8 * i));
        }

        return data;
    }

    // 将C/C++的无符号 DWORD类型转换为java的long型
    private static long getLong(byte buf[], int index) {

        int firstByte = (0x000000FF & ((int) buf[index]));
        int secondByte = (0x000000FF & ((int) buf[index + 1]));
        int thirdByte = (0x000000FF & ((int) buf[index + 2]));
        int fourthByte = (0x000000FF & ((int) buf[index + 3]));

        long unsignedLong = ((long) (firstByte | secondByte << 8 | thirdByte << 16 | fourthByte << 24)) & 0xFFFFFFFFL;

        return unsignedLong;
    }
}
