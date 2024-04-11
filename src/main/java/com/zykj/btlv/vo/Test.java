package com.zykj.btlv.vo;

import com.blockchain.tools.eth.codec.EthAbiCodecTool;
import com.blockchain.tools.eth.contract.template.ERC20Contract;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;
import java.util.List;

public class Test {
    public static String generateContractAddresses(BigInteger x,ERC20Contract btlv) {
        List<Type> userInfo = null;
        try {
            userInfo = btlv.otherSelect(
                    EthAbiCodecTool.getInputData(
                            "calculateAddr",// 要调用的方法名称
                            new Uint256(x)
                    ),  // 要调用的方法的inputData
                    new TypeReference<Address>() {
                    }
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return userInfo.get(0).toString();
    }


    public static void main(String[] args) {
        Web3j web3j = Web3j.build(new HttpService("https://bsc-dataseed4.ninicoin.io"));
        ERC20Contract btlv = ERC20Contract.builder(web3j, "0x3799E388933021A29d8a5180824225A2f593ae54");
        for(int i = 2583;i<99999999;i++){
            if(i % 1000 == 0){
                web3j = Web3j.build(new HttpService("https://bsc-dataseed4.ninicoin.io"));
                btlv = ERC20Contract.builder(web3j, "0x3799E388933021A29d8a5180824225A2f593ae54");
            }
            String aa = generateContractAddresses(new BigInteger(String.valueOf(i)),btlv);
            System.out.println(i+aa);
            String cc = aa.substring(aa.length()-3);
            if(cc.equals("999")
                    || cc.equals("888")
                    || cc.equals("666")){
                return;
            }
        }
    }
}
