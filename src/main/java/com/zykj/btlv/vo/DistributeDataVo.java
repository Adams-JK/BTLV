package com.zykj.btlv.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DistributeDataVo {
    private List<String> userAddr;//用户地址
    private List<BigInteger> quota;//额度
}
