package com.haoyd.printerlib.utils;

import android.text.TextUtils;

import java.io.UnsupportedEncodingException;

public class SubByteString {

    public static String subStr(String str, int subSLength) throws UnsupportedEncodingException {
        if (str == null)
            return "";
        else{
            int tempSubLength = subSLength;//截取字节数
            String subStr = str.substring(0, str.length()<subSLength ? str.length() : subSLength);//截取的子串
            int subStrByetsL = subStr.getBytes("GBK").length;//截取子串的字节长度
            //int subStrByetsL = subStr.getBytes().length;//截取子串的字节长度
            // 说明截取的字符串中包含有汉字
            while (subStrByetsL > tempSubLength){
                int subSLengthTemp = --subSLength;
                subStr = str.substring(0, subSLengthTemp>str.length() ? str.length() : subSLengthTemp);
                subStrByetsL = subStr.getBytes("GBK").length;
                //subStrByetsL = subStr.getBytes().length;
            }
            return subStr;
        }
    }

    public static String[] getSubedStrings(String string, int unitLength) {
        if (TextUtils.isEmpty(string)) {
            return null;
        }

        String str = new String(string);

        int arraySize = 0;
        try {
            arraySize = str.getBytes("GBK").length / unitLength;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if (str.getBytes().length % unitLength > 0) {
            arraySize++;
        }

        String[] result = new String[arraySize];

        for (int i = 0; i < arraySize; i++) {
            try {
                result[i] = subStr(str, unitLength);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            str = str.replace(result[i], "");
        }

        return result;
    }
}
