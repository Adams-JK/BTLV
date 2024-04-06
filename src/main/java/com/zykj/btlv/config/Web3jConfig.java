package com.zykj.btlv.config;

import cn.hutool.core.util.ObjectUtil;
import com.google.common.collect.EvictingQueue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthChainId;
import org.web3j.protocol.http.HttpService;

import javax.annotation.PostConstruct;
import java.math.BigInteger;
import java.util.*;

import static com.zykj.btlv.constant.ChainConstant.bscURL;

@Slf4j
@Service
public class Web3jConfig {
    public final static int NORMAL = 1;

    public final BigInteger bsc =  new BigInteger("56");

    private final Map<BigInteger, Queue<String>> chainToURL = new HashMap<>(1);

    public Map<BigInteger, Web3j> RPCs = new HashMap<>(1);


    @PostConstruct
    private void init(){

        EvictingQueue<String> bscQueue = EvictingQueue.create(bscURL.size());
        bscQueue.addAll(bscURL);
        chainToURL.put(bsc, bscQueue);

        setRpc(bsc, NORMAL);
    }


    public void setRpcV2(){
        setRpc(bsc, NORMAL);
    }

    public Web3j setRpc(BigInteger chainId, Integer type){
        List<String> rpcs = new ArrayList<>();
        if(chainId.toString().equals(bsc.toString())){
            rpcs = bscURL;
        }
        if(ObjectUtil.isNotEmpty(rpcs)){
            for(String url : rpcs){
                try {
                    Web3j web3j = Web3j.build(new HttpService(url));
                    EthChainId send = web3j.ethChainId().send();
                    if(!send.hasError()){
                        if (type == NORMAL){
                            RPCs.put(chainId, web3j);
                            log.info("CHAIN_ID：{}, RPC_URL: {}", chainId, url);
                        }
                        return web3j;
                    }
                    log.error("异常节点{}", url);
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
}
