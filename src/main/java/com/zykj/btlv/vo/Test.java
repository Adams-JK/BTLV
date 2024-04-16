package com.zykj.btlv.vo;

import com.blockchain.tools.eth.codec.EthAbiCodecTool;
import com.blockchain.tools.eth.contract.template.ERC20Contract;
import com.blockchain.tools.eth.contract.util.EthContractUtil;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.DynamicArray;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;
import java.util.List;

import static cn.dev33.satoken.SaManager.log;
import static com.zykj.btlv.tool.MathTool.getHex16ToHex10;
import static com.zykj.btlv.tool.MathTool.str_split;

public class Test {
    public static BigInteger getBSCPrice(String tokenA, String tokenB,BigInteger amount){
        try {
            Web3j web3j = Web3j.build(new HttpService("https://bsc-dataseed4.ninicoin.io"));
            String swap = "0x10ED43C718714eb63d5aA57B78B54704E256024E";
            EthContractUtil ethContractUtil = EthContractUtil.builder(web3j);
            List<Address> addressList = new ArrayList<>();
            addressList.add(new Address(tokenA));
            addressList.add(new Address(tokenB));
            List<Type> priceResult = ethContractUtil.select(
                    swap, // 合约地址
                    EthAbiCodecTool.getInputData(
                            "getAmountsOut",// 要调用的方法名称
                            new Uint256(amount),
                            new DynamicArray<Address>(Address.class,  addressList)
                    ),  // 要调用的方法的inputData
                    new TypeReference<DynamicArray<Uint256>>() {}
            );
            priceResult = (List<Type>) priceResult.get(0).getValue();
            BigInteger priceReal = new BigInteger(String.valueOf(priceResult.get(1).getValue()));
            return priceReal;
        } catch (Exception e) {
            log.error("getBSCPrice--错误");
        }
        return null;
    }

    public static BigDecimal getBSCPrice2(String pair){
        BigDecimal price = new BigDecimal("0");
        try {
            Web3j web3j = Web3j.build(new HttpService("https://bsc-dataseed4.ninicoin.io")); // 这里使用币安智能链的节点
            String pairAddress = Numeric.prependHexPrefix(pair);
            String routerAddress = Numeric.prependHexPrefix("0x10ED43C718714eb63d5aA57B78B54704E256024E");

            // 从交易对合约中读取储备量
            EthCall ethCall = web3j.ethCall(org.web3j.protocol.core.methods.request.Transaction.createEthCallTransaction(
                    routerAddress, pairAddress, "0x0902f1ac"), DefaultBlockParameterName.LATEST).send();

            String result = ethCall.getResult();

            result = result.substring(2);//去掉ox
            String[] params = str_split(result, 64);

            BigInteger reserve0 = getHex16ToHex10(params[0]); // 第一个代币的储备量
            BigInteger reserve1 = getHex16ToHex10(params[1]); // 第二个代币的储备量

            // 根据储备量计算代币价格
            BigInteger tokenAmount = new BigInteger("1000000000000000000"); // 你想要查询价格的代币数量
            BigInteger pr = reserve1.multiply(tokenAmount).divide(reserve0);
            price =new BigDecimal(pr).divide(BigDecimal.TEN.pow(18));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return price;
    }


    public static void main(String[] args) {
        BigInteger wbnbPrice = getBSCPrice("0x941d049e23c1ca0769443d13a2f6b777c0d3e77c","0xbb4cdb9cbd36b01bd1cbaebf2de08d9173bc095c",new BigInteger("1000000000000000000"));
        BigInteger usdtPrice = getBSCPrice("0xbb4cdb9cbd36b01bd1cbaebf2de08d9173bc095c","0x55d398326f99059ff775485246999027b3197955",wbnbPrice);
        BigDecimal price = new BigDecimal(usdtPrice).divide(BigDecimal.TEN.pow(18));
        System.out.println(price.toPlainString());
    }
}
