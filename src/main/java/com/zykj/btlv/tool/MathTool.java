package com.zykj.btlv.tool;

import cn.hutool.core.util.StrUtil;

import java.math.BigInteger;

public class MathTool {
    public static BigInteger getHex16ToHex10(String s) {
        BigInteger hex = new BigInteger("0");
        if (StrUtil.isNotEmpty(s)) {
            hex = new BigInteger(s, 16);
        }
        return hex;
    }

    public static String getHex16ToAddress(String s) {
        return "0x" + s;
    }

    public static String formatAddr(String addr) {
        Integer a = 40 - addr.length();
        String s = "";
        for (int i = 0; i < a; i++) {
            s = s + "0";
        }
        s = s + addr;
        return s;
    }

    public static String formatAddr2(String addr) {
        Integer a = 40 - addr.length();
        String s = "0x";
        for (int i = 0; i < a; i++) {
            s = s + "0";
        }
        s = s + addr;
        return s;
    }

    public static String[] str_split(String str, int length) {

        int len = str.length();

        String[] arr = new String[(len + length - 1) / length];
        for (int i = 0; i < len; i += length) {
            int n = len - i;
            if (n > length)
                n = length;
            String t = str.substring(i, i + n);
            arr[i / length] = formatAddr(t.replaceAll("^(0+)", ""));
        }
        return arr;
    }

}
