package com.zykj.btlv.task;

import com.zykj.btlv.config.Web3jConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RpcTask {

    @Autowired
    public Web3jConfig web3jConfig;

    @Scheduled(fixedDelay = 1000 * 60 * 3)
    public void onRpcMonitor(){
        log.info("RPCs----节点重载");
        web3jConfig.setRpcV2();
    }
}
