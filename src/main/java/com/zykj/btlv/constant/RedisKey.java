package com.zykj.btlv.constant;

public class RedisKey {

    //登录签名
    public static final String loginSignature = "CGR:LOGIN";

    //首页数据-总质押
    public static final String homePledge = "HOME:pledge";

    //首页数据-总价值
    public static final String homeValue = "HOME:value";

    //首页数据-总奖励
    public static final String homeRewards = "HOME:rewards";

    //监听合约消息当前区块-bsc链
    public static final String bscLastBlock = "LastBlock:bsc";

    //监听合约消息当前区块-mtc链
    public static final String mtcLastBlock = "LastBlock:mtc";

    //price cgr
    public static final String cgrPrice = "price:cgr";

    //price cgz
    public static final String cgzPrice = "price:cgz";

    //price gp
    public static final String gpPrice = "price:gp";

    //price CGR
    public static final String putSaleCgrPrice = "price:putSaleCgr";

    //trading 待付款订单
    public static final String tradingPendingOrder = "trading:pendingOrder";

    //直推全局分红
    public static final String dptod = "dptod:";

    //直推全局分红地址
    public static final String dptodAddr = "dptodAddr:";

    //用户新增业绩
    public static final String userNewPerformance = "userNewPerformance:";

    //全网新增业绩
    public static final String networkNewPerformance = "networkNewPerformance:";

    //全网业绩
    public static final String networkPerformance = "networkPerformance:";

    //用户业绩
    public static final String userPerformance = "userPerformance:";

    //用户团队
    public static final String userTeam = "userTeam:";

    //兑换K线图
    public static final String exchangeK = "exchangeK:";

    //兑换K线图
    public static final String c2cNotice = "c2cNotice:";

    //竞猜开奖
    public static final String gameOut = "gameOut:";

    //竞猜统计
    public static final String gameStatistics = "gameStatistics:";

    //竞猜3中奖人数
    public static final String game3OutShuLiang = "game3OutShuLiang:";

    //竞猜3参与
    public static final String game3OutCanYu = "game3OutCanYu:";

    //竞猜3订单
    public static final String game3OutDingDan = "game3OutDingDan:";

    //竞猜3警告
    public static final String game3JinGao = "game3JinGao:";

    //竞猜3用户
    public static final String game3User= "game3User:";

    //竞猜3记录
    public static final String game3JiLu= "game3JiLu:";
}
