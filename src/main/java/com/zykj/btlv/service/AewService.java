package com.zykj.btlv.service;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zykj.btlv.domain.AwardRecord;
import com.zykj.btlv.domain.User;
import com.zykj.btlv.mapper.AwardRecordMapper;
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
public class AewService {
    @Autowired
    private AwardRecordMapper recordMapper;


    public static String formatAddr(String addr) {
        Integer a = 40 - addr.length();
        String s = "";
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
                        String[] params = str_split(data, 64);
                        log.info("监听合约事件 input:{}", data);
                        dealEvent(logObject.getAddress(), params,logObject.getTransactionHash());
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
        return "0x" + bigInteger.toString(16);
    }


    /**
     * 注册事件*
     *
     * @param filter
     */
    private void subEvent(EthFilter filter) {

        Event batchTransferEvent = new Event("BatchTransfer",
                Arrays.<TypeReference<?>>asList(
                        new TypeReference<Address>(true) { //account
                        },
                        new TypeReference<Uint256>(true) { //amount
                        },
                        new TypeReference<Uint256>(true) { //amount
                        },
                        new TypeReference<Uint256>(true) { //amount
                        }
                )
        );
        String topicBatchTransfer = EventEncoder.encode(batchTransferEvent);


        filter.addOptionalTopics(
                topicBatchTransfer
        );
    }


    /**
     * 处理事件*
     */
    @Transactional
    public void dealEvent(String address, String[] params, String txId) {
        String userAddress = MathTool.getHex16ToAddress(params[0]);
        Integer status = MathTool.getHex16ToHex10(params[1]).intValue();
        BigDecimal amount = MoneyTransferTool.transfer(MathTool.getHex16ToHex10(params[2]));
        BigDecimal time = MoneyTransferTool.transfer(MathTool.getHex16ToHex10(params[3]));
        QueryWrapper<AwardRecord> wrapper= new QueryWrapper<>();
        wrapper.eq("address",userAddress);
        wrapper.eq("hash",txId);
        wrapper.eq("type",status);
        AwardRecord record = recordMapper.selectOne(wrapper);

        if(ObjectUtil.isNotEmpty(record)){
            log.info("监听合约事件处理事件:{}，地址：{}，哈希：{}，data:{}", "BatchTransfer", userAddress, txId, "重复");
            return;
        }
        String data = dealTransfer(params,txId,address);
        log.info("监听合约事件处理事件:{}，地址：{}，哈希：{}，data:{}", "BatchTransfer", userAddress, txId, params);
    }


    public String dealTransfer(String[] params, String txId, String address) {
        String userAddress = MathTool.getHex16ToAddress(params[0]);
        Integer status = MathTool.getHex16ToHex10(params[1]).intValue();
        BigDecimal amount = MoneyTransferTool.transfer(MathTool.getHex16ToHex10(params[2]));
        BigInteger time = MathTool.getHex16ToHex10(params[3]);
        QueryWrapper<AwardRecord> wrapper= new QueryWrapper<>();
        wrapper.eq("address",userAddress);
        wrapper.eq("hash",txId);
        wrapper.eq("type",status);
        AwardRecord record = recordMapper.selectOne(wrapper);
        if(ObjectUtil.isEmpty(record)){
            record = AwardRecord.builder()
                    .hash(txId)
                    .type(status)
                    .amount(amount)
                    .time(new Date(time.longValue() * 1000L))
                    .address(userAddress)
                    .build();
            recordMapper.insert(record);
        }
        return JSONUtil.toJsonStr(txId);
    }
}
