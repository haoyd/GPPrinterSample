package com.haoyd.printerlib.utils;

import java.io.UnsupportedEncodingException;

public class StringLengthUtil {

    /**
     * 获取字符串的宽度
     * @param text
     * @return
     */
    public static int getTextLength(String text) {
        int result = 0;

        try {
            result = text.getBytes("GBK").length;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return result;
    }

}
