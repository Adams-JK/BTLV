package com.zykj.btlv.config;

import cn.hutool.core.util.ObjectUtil;
import com.google.common.collect.EvictingQueue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.Response;
import org.web3j.protocol.core.methods.response.EthChainId;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.http.HttpService;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.math.BigInteger;
import java.util.*;

import static com.zykj.btlv.constant.ChainConstant.bscURL;
import static com.zykj.btlv.constant.ChainConstant.bscURLForSub;

@Slf4j
@Service
public class Web3jConfig {
    public final static int NORMAL = 1;
    public final static int LISTENER = 2;


    public final BigInteger bsc =  new BigInteger("56");

    private final Map<BigInteger, Queue<String>> chainToURL = new HashMap<>(1);

    public Map<BigInteger, Web3j> RPCs = new HashMap<>(1);

    private final Map<BigInteger, Web3j> listenerRPCs = new HashMap<>(1);



    @PostConstruct
    private void init(){

        EvictingQueue<String> bscQueue = EvictingQueue.create(bscURL.size());
        bscQueue.addAll(bscURL);
        chainToURL.put(bsc, bscQueue);

        setRpc(bsc, NORMAL);

        setRpc(bsc, LISTENER);
    }


    public void setRpcV2(){
        setRpc(bsc, NORMAL);

        setRpc(bsc, LISTENER);
    }

    public Web3j setRpc(BigInteger chainId, Integer type){
        List<String> rpcs = new ArrayList<>();
        if(type == NORMAL){
            rpcs = bscURL;
        }else {
            rpcs = bscURLForSub;
        }

        if(ObjectUtil.isNotEmpty(rpcs)){
            for(String url : rpcs){
                try {
                    Web3j web3j = Web3j.build(new HttpService(url));
                    // 发送 web3_clientVersion 请求
                    Request<?, Web3ClientVersion> request =
                            web3j.web3ClientVersion();
                    Web3ClientVersion response = request.send();
                    if (response != null && response.getResult() != null) {
                        String clientVersion = response.getWeb3ClientVersion();
                        if (type == NORMAL){
                            RPCs.put(chainId, web3j);
                            log.info("CHAIN_ID：{}, RPC_URL: {}", chainId, url);
                        } else {
                            listenerRPCs.put(chainId, web3j);
                            log.info("CHAIN_ID：{}, 链监RPC_URL: {}", chainId, url);
                        }
                        return web3j;
                    } else {
                        log.error("异常节点{}", url);
                    }
                } catch (Exception e) {
                    log.error("异常节点{}", url);
                }
            }
        }
        return null;
    }


    public Web3j getWeb3jById(BigInteger chainId){
        Web3j web3j = RPCs.get(chainId);
        try {
            if(ObjectUtil.isNotEmpty(web3j)){
                EthChainId send = web3j.ethChainId().send();
                if(send.hasError()){
                    log.error("{}链：异常节点重载，error--{}", chainId,send.getError().getMessage());
                    setRpc(chainId, NORMAL);
                }
            }else {
                log.error("{}链：异常节点重载，error--{}", chainId,"web3j is null");
                setRpc(chainId, NORMAL);
            }
        } catch (Exception e) {
            log.error("{}链：异常节点重载，error--{}", chainId,e.getMessage());
            setRpc(chainId, NORMAL);
        }

        return RPCs.get(chainId);
    }

    public Web3j getListenerById(BigInteger chainId){
        Web3j web3j = listenerRPCs.get(chainId);
        try {
            if(ObjectUtil.isNotEmpty(web3j)){
                EthChainId send = web3j.ethChainId().send();
                if(send.hasError()){
                    log.error("{}链：异常节点重载，error--{}", chainId,send.getError().getMessage());
                    setRpc(chainId, LISTENER);
                }
            }else {
                log.error("{}链：异常节点重载，error--{}", chainId,"web3j is null");
                setRpc(chainId, LISTENER);
            }
        } catch (Exception e) {
            log.error("{}链：异常节点重载，error--{}", chainId,e.getMessage());
            setRpc(chainId, LISTENER);
        }

        return listenerRPCs.get(chainId);
    }



}
