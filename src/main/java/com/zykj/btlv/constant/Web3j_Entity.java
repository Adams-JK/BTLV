package com.zykj.btlv.constant;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.web3j.protocol.Web3j;

@Data
@AllArgsConstructor
public class Web3j_Entity {
    private String url;
    private Web3j web3j;
}
