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
import static com.zykj.btlv.constant.ChainConstant.mtcURL;

@Slf4j
@Service
public class Web3jConfig {
    public final static int NORMAL = 1;

    public final BigInteger bsc =  new BigInteger("56");

    public final BigInteger mtc =  new BigInteger("20028");

    private final Map<BigInteger, Queue<String>> chainToURL = new HashMap<>(2);

    public Map<BigInteger, Web3j> RPCs = new HashMap<>(2);


    @PostConstruct
    private void init(){

        EvictingQueue<String> bscQueue = EvictingQueue.create(bscURL.size());
        bscQueue.addAll(bscURL);
        chainToURL.put(bsc, bscQueue);

        EvictingQueue<String> mumbaiQueueTest = EvictingQueue.create(mtcURL.size());
        mumbaiQueueTest.addAll(mtcURL);
        chainToURL.put(mtc, mumbaiQueueTest);

        setRpc(bsc, NORMAL);
        setRpc(mtc, NORMAL);
    }


    public void setRpcV2(){
        setRpc(bsc, NORMAL);
        setRpc(mtc, NORMAL);
    }

    public Web3j setRpc(BigInteger chainId, Integer type){
        List<String> rpcs = new ArrayList<>();
        if(chainId.toString().equals(bsc.toString())){
            rpcs = bscURL;
        }else if(chainId.toString().equals(mtc.toString())){
            rpcs = mtcURL;
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
