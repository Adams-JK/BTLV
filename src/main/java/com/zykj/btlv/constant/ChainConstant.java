package com.zykj.btlv.constant;

import java.util.Arrays;
import java.util.List;

/**
 * 常量定义
 */
public class ChainConstant {

    //默认地址
    public static String DEFAULT_OWNER = "0x0000000000000000000000000000000000000000";

    public static List<String> bscURL = Arrays.asList(
            "https://bsc-dataseed4.ninicoin.io",
            "https://bsc-dataseed4.defibit.io",
            "https://bsc-dataseed3.ninicoin.io",
            "https://bsc-dataseed1.defibit.io",
            "https://bsc-dataseed2.defibit.io",
            "https://bsc-dataseed1.ninicoin.io"
    );

    public static List<String> mtcURL = Arrays.asList(
            "http://8.217.4.29:13568/",
            "http://8.217.1.140:13568/",
            "http://8.217.4.246:13568/",
            "http://8.210.91.136:13568/",
            "https://seed1.metachain1.com",
            "http://8.217.194.100:13568"
    );
}
