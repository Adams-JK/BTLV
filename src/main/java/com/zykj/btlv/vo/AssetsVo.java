package com.zykj.btlv.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AssetsVo {
    private String userAddr;//用户地址
    private BigDecimal totalQuota;//总额度
    private BigDecimal surplusQuota;//剩余额度
    private BigDecimal totalMarket;//总市值额度
    private BigDecimal surplusMarket;//剩余市值额度
    private BigDecimal collected;//待领取
    private BigDecimal received;//已领取
    private BigDecimal receivedUsdt;//已领取USDT
    private BigDecimal receivedBtlv;//已领取BTLV
    private BigDecimal drawUsdt;//出金比例（USDT）
    private BigDecimal drawBtl;//出金比例（BTLV）
    private Long lastTime;//上次领取时间
}
