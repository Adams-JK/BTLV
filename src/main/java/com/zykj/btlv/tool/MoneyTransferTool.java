package com.zykj.btlv.tool;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @AUTHOR lp
 * @TIME: 2022-05-2022/5/11 12:07
 * @DESCRIPTION: 金额转换工具
 **/
public class MoneyTransferTool {

    private static BigDecimal decimals = BigDecimal.valueOf(100000000000000000L);

    public static String transfer(Long price) {
        return BigDecimal.valueOf(price).subtract(decimals).toString();
    }

    public static BigDecimal transfer(BigInteger price){
        return new BigDecimal(price).divide(BigDecimal.TEN.pow(18),18,BigDecimal.ROUND_DOWN);
    }

    public static String transfer(String amount){
        return new BigDecimal(amount).divide(BigDecimal.TEN.pow(18),18,BigDecimal.ROUND_DOWN).toPlainString();
    }

    public static BigInteger transfer(BigDecimal amount){
        return amount.multiply(BigDecimal.TEN.pow(18)).toBigInteger();
    }

    public static BigDecimal transferLong(String price){
        return new BigDecimal(price).divide(BigDecimal.TEN.pow(18),18,BigDecimal.ROUND_DOWN);
    }

}
