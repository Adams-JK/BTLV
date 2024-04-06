package com.zykj.tool;

import lombok.extern.slf4j.Slf4j;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Sign;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.util.Arrays;

/**
 * @author
 *
 */
@Slf4j
public class Web3jTool {
    /**
     * 签名
     *
     * @param content    原文信息
     * @param privateKey 私钥
     */
    public static String signPrefixedMessage(String content, String privateKey) {

        // todo 如果验签不成功，就不需要Hash.sha3 直接content.getBytes()就可以了
        // 原文信息字节数组
//        byte[] contentHashBytes = Hash.sha3(content.getBytes());
        byte[] contentHashBytes = content.getBytes();
        // 根据私钥获取凭证对象
        Credentials credentials = Credentials.create(privateKey);
        //
        Sign.SignatureData signMessage = Sign.signPrefixedMessage(contentHashBytes, credentials.getEcKeyPair());

        byte[] r = signMessage.getR();
        byte[] s = signMessage.getS();
        byte[] v = signMessage.getV();

        byte[] signByte = Arrays.copyOf(r, v.length + r.length + s.length);
        System.arraycopy(s, 0, signByte, r.length, s.length);
        System.arraycopy(v, 0, signByte, r.length + s.length, v.length);

        return Numeric.toHexString(signByte);
    }

    /**
     * 验证签名
     *
     * @param signature     验签数据
     * @param content       原文数据
     * @param walletAddress 钱包地址
     * @return 结果
     */
    public static Boolean validate(String signature, String content, String walletAddress){
        Boolean flang = false;
        try {
            // todo 如果验签不成功，就不需要Hash.sha3 直接content.getBytes()就可以了
            byte[] msgHash = content.getBytes();
            // 签名数据
            byte[] signatureBytes = Numeric.hexStringToByteArray(signature);
            byte v = signatureBytes[64];
            if (v < 27) {
                v += 27;
            }

            //通过摘要和签名后的数据，还原公钥
            Sign.SignatureData signatureData = new Sign.SignatureData(
                    v,
                    Arrays.copyOfRange(signatureBytes, 0, 32),
                    Arrays.copyOfRange(signatureBytes, 32, 64));
            // 签名的前缀消息到密钥
            BigInteger publicKey = Sign.signedPrefixedMessageToKey(msgHash, signatureData);
            // 得到公钥(私钥对应的钱包地址)
            String parseAddress = "0x" + Keys.getAddress(publicKey);
            // 将钱包地址进行比对
            flang = parseAddress.equalsIgnoreCase(walletAddress);

        }catch (Exception e) {
            log.error("Web3jTool.validate错误啦：{}",e.getMessage());
        }
        return flang;
    }

}
