package com.zykj.btlv.service;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zykj.btlv.domain.User;
import com.zykj.btlv.mapper.UserMapper;
import com.zykj.btlv.tool.MathTool;
import com.zykj.btlv.tool.MoneyTransferTool;
import io.reactivex.disposables.Disposable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.EthLog;
import org.web3j.protocol.core.methods.response.Log;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;


@Service
@Slf4j
public class WalletTradeService {

    @Autowired
    private UserMapper userMapper;

    public static Disposable disposable;

    public Boolean customMonitor(
            String chainId,
            Web3j web3j,
            List<String> addressList,
            BigInteger startBlock,
            BigInteger stopBlock) throws Exception {
        AtomicReference<Boolean> flang = new AtomicReference<>(true);
        try {
            if (addressList.isEmpty()) {
                return false;
            }
            log.info("监听合约事件---customMonitor chainId:{},startBlock:{},stopBlock:{},addressList:{}", chainId, startBlock, stopBlock, addressList);
            EthFilter filter = new EthFilter(
                    DefaultBlockParameter.valueOf(startBlock),
                    DefaultBlockParameter.valueOf(stopBlock),
                    addressList);
            subEvent(filter);
            EthLog logs = web3j.ethGetLogs(filter).send();
            if(!logs.hasError()){
                List<EthLog.LogResult> logResults = logs.getLogs();
                for (EthLog.LogResult logResult : logResults) {
                    if (logResult instanceof EthLog.LogObject) {
                        EthLog.LogObject logObject = (EthLog.LogObject) logResult;
                        String data = logObject.getData();
                        log.info("监听合约事件 input:{}", data);
                        dealEvent(logObject.getAddress(), logObject,logObject.getTransactionHash());
                    }
                }
            }else {
                flang.set(false);
                log.error("监听合约事件--- error啦--{}",logs.getError().getMessage());
            }
        } catch (Exception | Error e) {
            flang.set(false);
            log.error("监听合约事件:{}", e.getMessage());
        }
        return flang.get();
    }

    private static String removeLeadingZeros(String hexString) {
        // 将十六进制字符串转换为 BigInteger
        BigInteger bigInteger = new BigInteger(hexString.substring(2), 16);

        // 将 BigInteger 转换为字符串，去掉前导零
        return bigInteger.toString(16);
    }

    public static String getHex16ToAddress(String s) {
        return "0x"+formatAddr(removeLeadingZeros(s));
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


    /**
     * 注册事件*
     *
     * @param filter
     */
    private void subEvent(EthFilter filter) {

        Event transferEvent = new Event("Transfer",
                Arrays.<TypeReference<?>>asList(
                        new TypeReference<Address>(true) { //type_
                        },
                        new TypeReference<Address>(true) { //account
                        },
                        new TypeReference<Uint256>(true) { //amount
                        }
                )
        );
        String topicTransfer = EventEncoder.encode(transferEvent);


        filter.addOptionalTopics(
                topicTransfer
        );
    }


    /**
     * 处理事件*
     */
    @Transactional
    public void dealEvent(String address, Log loggg, String txId) {
        String from = loggg.getTopics().get(1);
        from = getHex16ToAddress(from);
        String to = loggg.getTopics().get(2);
        to = getHex16ToAddress(to);
        BigInteger value = new BigInteger(loggg.getData().substring(2), 16);
        if(value.compareTo(new BigInteger("1000000000000")) <= 0){
            return;
        }
        QueryWrapper<User> wrapperFrom = new QueryWrapper<>();
        wrapperFrom.eq("address",from);
        User userFrom = userMapper.selectOne(wrapperFrom);

        QueryWrapper<User> wrapperTo = new QueryWrapper<>();
        wrapperTo.eq("address",to);
        User userTo = userMapper.selectOne(wrapperTo);

        if(ObjectUtil.isNotEmpty(userFrom) && ObjectUtil.isNotEmpty(userTo)){
            log.info("监听合约事件处理事件:{}，FROM地址：{}，TO地址：{}，data:{}", "Transfer", from, to, "重复");
            return;
        }
        String data = dealTransfer(loggg,txId,address);
        log.info("监听合约事件处理事件:{}，FROM地址：{}，TO地址：{}，data:{}", "Transfer", from, to, data);
    }


    public String dealTransfer(Log loggg, String txId, String address) {
        String from = loggg.getTopics().get(1);
        from = getHex16ToAddress(from);
        String to = loggg.getTopics().get(2);
        to = getHex16ToAddress(to);

        String hexValue = loggg.getData();
        // 移除 "0x" 前缀
        if (hexValue.startsWith("0x")) {
            hexValue = hexValue.substring(2);
        }

        QueryWrapper<User> wrapperFrom = new QueryWrapper<>();
        wrapperFrom.eq("address",from);
        User userFrom = userMapper.selectOne(wrapperFrom);
        if(ObjectUtil.isEmpty(userFrom)){
            BigDecimal decimal = new BigDecimal("0");
            userFrom = User.builder()
                    .address(from)
                    .parentAddress("")
                    .lp(decimal)
                    .balance(decimal)
                    .totalQuota(decimal)
                    .surplusQuota(decimal)
                    .received(decimal)
                    .grade(0)
                    .usdtPrice(decimal)
                    .people(0)
                    .build();
            userMapper.insert(userFrom);
        }

        QueryWrapper<User> wrapperTo = new QueryWrapper<>();
        wrapperTo.eq("address",to);
        User userTo = userMapper.selectOne(wrapperTo);
        if(ObjectUtil.isEmpty(userTo)){
            BigDecimal decimal = new BigDecimal("0");
            userTo = User.builder()
                    .address(to)
                    .parentAddress("")
                    .lp(decimal)
                    .balance(decimal)
                    .totalQuota(decimal)
                    .surplusQuota(decimal)
                    .received(decimal)
                    .grade(0)
                    .usdtPrice(decimal)
                    .people(0)
                    .build();
            userMapper.insert(userTo);
        }

        return JSONUtil.toJsonStr(txId);
    }
}
