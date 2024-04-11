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
            "https://bsc-dataseed1.ninicoin.io",
            "https://services.tokenview.io/vipapi/nodeservice/bsc?apikey=W2kFupq7FkGGVYjZbbM3"
    );

    public static List<String> bscURLForSub = Arrays.asList(
            "https://services.tokenview.io/vipapi/nodeservice/bsc?apikey=W2kFupq7FkGGVYjZbbM3",
            "https://bsc-dataseed2.defibit.io",
            "https://bsc-dataseed1.ninicoin.io",
            "https://bsc-dataseed4.ninicoin.io",
            "https://bsc-dataseed4.defibit.io",
            "https://bsc-dataseed3.ninicoin.io",
            "https://bsc-dataseed1.defibit.io"
    );
}
