package com.zykj.btlv.task;


import cn.hutool.core.util.ObjectUtil;
import com.zykj.btlv.config.Web3jConfig;
import com.zykj.btlv.constant.RedisKey;
import com.zykj.btlv.domain.User;
import com.zykj.btlv.mapper.UserMapper;
import com.zykj.btlv.service.WalletTradeService;
import com.zykj.btlv.tool.RedisTool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.web3j.protocol.Web3j;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class WalletTradeTask {

    @Value("${chainId}")
    public String chainIds;

    @Value("${contract}")
    public String contract;

    @Autowired
    public Web3jConfig web3jConfig;


    @Autowired
    private RedisTool redisTool;

    @Autowired
    private WalletTradeService walletTradeService;

    @Autowired
    private UserMapper userMapper;

    @Scheduled(fixedDelay = 5000)
    public void topUpMonitor(){
        BigInteger chainId =  new BigInteger(chainIds);
        Web3j web3j = web3jConfig.getWeb3jById(chainId);
        String redisKey = RedisKey.bscLastBlock;
        List<String> addressList = new ArrayList<>();
        addressList.add(contract);
        try {
            //当前区块高度
            BigInteger currentBlockNumber = null;
            currentBlockNumber = web3j.ethBlockNumber().send().getBlockNumber();
            //待扫描的区块最高高度为当前区块高度-1 避免出现漏单的情况
            long lCurrentBlockNumber = currentBlockNumber.longValue() - 1;
            String strLastBlock = redisTool.getString(redisKey, 0);
            if (ObjectUtils.isEmpty(strLastBlock)) {
                redisTool.setString(redisKey, String.valueOf(0), null, 0);
                return;
            }

            //待扫描的区块起始高度为当前区块高度-2 避免出现漏单的情况
            Long startBlockNum = Long.valueOf(strLastBlock);
            if (startBlockNum.longValue() >= lCurrentBlockNumber) { //同一个区块高度的不扫描,要等区块执行完了才可以
                return;
            }
            if(lCurrentBlockNumber >= startBlockNum + 40000){
                startBlockNum = lCurrentBlockNumber - 40000;
            }
            if(ObjectUtil.isNotEmpty(WalletTradeService.disposable)){
                WalletTradeService.disposable.dispose();
            }
            Boolean flang = walletTradeService.customMonitor(chainId.toString(),web3j,addressList,BigInteger.valueOf(startBlockNum), BigInteger.valueOf(lCurrentBlockNumber));;
            if(flang){
                redisTool.setString(redisKey, String.valueOf(lCurrentBlockNumber + 1), null, 0);
            }
        } catch (IOException e) {
            log.info("=============>topUpMonitor监听合约异常事件:{}", e.getMessage());
        } catch (Exception e) {
            log.info("=============>topUpMonitor监听合约异常事件:{}", e.getMessage());
        }
    }

    @Scheduled(fixedDelay = 1000 * 6)
    public void digital(){
        List<User> list = userMapper.selectList(null);
        if(ObjectUtil.isNotEmpty(list)){
            for(User user : list){

            }
        }
    }
}
