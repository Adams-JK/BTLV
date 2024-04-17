package com.zykj.btlv.task;


import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.blockchain.tools.eth.codec.EthAbiCodecTool;
import com.blockchain.tools.eth.contract.template.ERC20Contract;
import com.zykj.btlv.config.Web3jConfig;
import com.zykj.btlv.constant.RedisKey;
import com.zykj.btlv.domain.User;
import com.zykj.btlv.mapper.UserMapper;
import com.zykj.btlv.service.AewService;
import com.zykj.btlv.service.WalletTradeService;
import com.zykj.btlv.tool.MoneyTransferTool;
import com.zykj.btlv.tool.RedisTool;
import com.zykj.btlv.vo.AssetsVo;
import com.zykj.btlv.vo.UserVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.Web3j;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class WalletTradeTask {

    @Value("${chainId}")
    public String chainIds;

    @Value("${contract}")
    public String contract;

    @Value("${batchTransferERC20}")
    public String batchTransferERC20;

    @Value("${pair}")
    public String pair;

    @Autowired
    public Web3jConfig web3jConfig;


    @Autowired
    private RedisTool redisTool;

    @Autowired
    private WalletTradeService walletTradeService;

    @Autowired
    private AewService aewService;

    @Autowired
    private UserMapper userMapper;

    @Scheduled(fixedDelay = 5000)
    public void topUpMonitor() {
        BigInteger chainId = new BigInteger(chainIds);
        Web3j web3j = web3jConfig.getListenerById(chainId);
        String redisKey = RedisKey.bscLastBlock;
        List<String> addressList = new ArrayList<>();
        addressList.add(contract);
        try {
            //当前区块高度
            BigInteger currentBlockNumber = null;
            currentBlockNumber = web3j.ethBlockNumber().send().getBlockNumber();
            //待扫描的区块最高高度为当前区块高度-1 避免出现漏单的情况
            long lCurrentBlockNumber = currentBlockNumber.longValue() - 1;
            String strLastBlock = redisTool.getString(redisKey, 0);
            if (ObjectUtils.isEmpty(strLastBlock)) {
                redisTool.setString(redisKey, String.valueOf(0), null, 0);
                return;
            }

            //待扫描的区块起始高度为当前区块高度-2 避免出现漏单的情况
            Long startBlockNum = Long.valueOf(strLastBlock);
            if (startBlockNum.longValue() >= lCurrentBlockNumber) { //同一个区块高度的不扫描,要等区块执行完了才可以
                return;
            }
            if (lCurrentBlockNumber >= startBlockNum + 40000) {
                lCurrentBlockNumber = startBlockNum + 4000;
            }
            if (ObjectUtil.isNotEmpty(WalletTradeService.disposable)) {
                WalletTradeService.disposable.dispose();
            }
            Boolean flang = walletTradeService.customMonitor(chainId.toString(), web3j, addressList, BigInteger.valueOf(startBlockNum), BigInteger.valueOf(lCurrentBlockNumber));
            ;
            if (flang) {
                redisTool.setString(redisKey, String.valueOf(lCurrentBlockNumber + 1), null, 0);
            }
        } catch (IOException e) {
            log.info("=============>topUpMonitor监听合约异常事件:{}", e.getMessage());
        } catch (Exception e) {
            log.info("=============>topUpMonitor监听合约异常事件:{}", e.getMessage());
        }
    }

    @Scheduled(fixedDelay = 5000)
    public void AewMonitor() {
        BigInteger chainId = new BigInteger(chainIds);
        Web3j web3j = web3jConfig.getListenerById(chainId);
        String redisKey = RedisKey.bscLastBlockAew;
        List<String> addressList = new ArrayList<>();
        addressList.add(batchTransferERC20);
        try {
            //当前区块高度
            BigInteger currentBlockNumber = null;
            currentBlockNumber = web3j.ethBlockNumber().send().getBlockNumber();
            //待扫描的区块最高高度为当前区块高度-1 避免出现漏单的情况
            long lCurrentBlockNumber = currentBlockNumber.longValue() - 1;
            String strLastBlock = redisTool.getString(redisKey, 0);
            if (ObjectUtils.isEmpty(strLastBlock)) {
                redisTool.setString(redisKey, String.valueOf(0), null, 0);
                return;
            }

            //待扫描的区块起始高度为当前区块高度-2 避免出现漏单的情况
            Long startBlockNum = Long.valueOf(strLastBlock);
            if (startBlockNum.longValue() >= lCurrentBlockNumber) { //同一个区块高度的不扫描,要等区块执行完了才可以
                return;
            }
            if (lCurrentBlockNumber >= startBlockNum + 40000) {
                startBlockNum = lCurrentBlockNumber - 40000;
            }
            if (ObjectUtil.isNotEmpty(WalletTradeService.disposable)) {
                WalletTradeService.disposable.dispose();
            }
            Boolean flang = aewService.customMonitor(chainId.toString(), web3j, addressList, BigInteger.valueOf(startBlockNum), BigInteger.valueOf(lCurrentBlockNumber));
            ;
            if (flang) {
                redisTool.setString(redisKey, String.valueOf(lCurrentBlockNumber + 1), null, 0);
            }
        } catch (IOException e) {
            log.info("=============>topUpMonitor监听合约异常事件:{}", e.getMessage());
        } catch (Exception e) {
            log.info("=============>topUpMonitor监听合约异常事件:{}", e.getMessage());
        }
    }

    @Scheduled(fixedDelay = 1000 * 60)
    public void digital() {
        List<User> list = userMapper.selectList(null);
        if (ObjectUtil.isNotEmpty(list)) {
            Web3j web3j = web3jConfig.getWeb3jById(new BigInteger(chainIds));
            ERC20Contract btlv = ERC20Contract.builder(web3j, contract);
            ERC20Contract lpPair = ERC20Contract.builder(web3j, pair);
            try {
                for (User user : list) {
                    log.info(user.getAddress());
                    try {
                        if(user.getAddress().equalsIgnoreCase(pair) || user.getAddress().equalsIgnoreCase(contract) || user.getAddress().equalsIgnoreCase("0x89115a6467C240a67D8a99B01A0d5baFF8001198")){
                            user.setLp(new BigDecimal("0"));
                            user.setBalance(new BigDecimal("0"));
                            user.setUsdtPrice(new BigDecimal("0"));
                            user.setParentAddress("");
                            user.setTotalQuota(new BigDecimal("0"));
                            user.setSurplusQuota(new BigDecimal("0"));
                            user.setReceived(new BigDecimal("0"));
                            user.setGrade(0);
                            user.setPeople(0);
                        }else {
                            UserVo userVo = getUser(user.getAddress());
                            AssetsVo assetsVo = getAssets(user.getAddress());
                            BigDecimal btlvBalance = new BigDecimal(btlv.balanceOf(user.getAddress())).divide(BigDecimal.TEN.pow(18), 8, RoundingMode.DOWN);
                            BigDecimal lpBalance = new BigDecimal(lpPair.balanceOf(user.getAddress())).divide(BigDecimal.TEN.pow(18), 8, RoundingMode.DOWN);
                            user.setLp(lpBalance);
                            user.setBalance(btlvBalance);
                            user.setUsdtPrice(userVo.getUsdtPrice());
                            user.setParentAddress(userVo.getParentAddress());
                            user.setTotalQuota(assetsVo.getTotalQuota());
                            user.setSurplusQuota(assetsVo.getSurplusQuota());
                            user.setReceived(assetsVo.getReceived());
                            user.setGrade(userVo.getGrade());
                            user.setPeople(userVo.getPeople());
                        }

                        userMapper.updateById(user);
                    } catch (Exception e) {
                        log.error(e.getMessage());
                    }

                }
            } catch (Exception e) {
                log.error(e.getMessage());
            }

        }
    }

    @Scheduled(fixedDelay = 1000 * 60)
    public void digital22() {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.isNull("parentAddress").or().eq("parentAddress","");
        List<User> list = userMapper.selectList(wrapper);
        if (ObjectUtil.isNotEmpty(list)) {
            Web3j web3j = web3jConfig.getWeb3jById(new BigInteger(chainIds));
            ERC20Contract btlv = ERC20Contract.builder(web3j, contract);
            ERC20Contract lpPair = ERC20Contract.builder(web3j, pair);
            try {
                for (User user : list) {
                    log.info(user.getAddress());
                    try {
                        if(user.getAddress().equalsIgnoreCase(pair) || user.getAddress().equalsIgnoreCase(contract) || user.getAddress().equalsIgnoreCase("0x89115a6467C240a67D8a99B01A0d5baFF8001198")){
                            user.setLp(new BigDecimal("0"));
                            user.setBalance(new BigDecimal("0"));
                            user.setUsdtPrice(new BigDecimal("0"));
                            user.setParentAddress("");
                            user.setTotalQuota(new BigDecimal("0"));
                            user.setSurplusQuota(new BigDecimal("0"));
                            user.setReceived(new BigDecimal("0"));
                            user.setGrade(0);
                            user.setPeople(0);
                        }else {
                            UserVo userVo = getUser(user.getAddress());
                            AssetsVo assetsVo = getAssets(user.getAddress());
                            BigDecimal btlvBalance = new BigDecimal(btlv.balanceOf(user.getAddress())).divide(BigDecimal.TEN.pow(18), 8, RoundingMode.DOWN);
                            BigDecimal lpBalance = new BigDecimal(lpPair.balanceOf(user.getAddress())).divide(BigDecimal.TEN.pow(18), 8, RoundingMode.DOWN);
                            user.setLp(lpBalance);
                            user.setBalance(btlvBalance);
                            user.setUsdtPrice(userVo.getUsdtPrice());
                            user.setParentAddress(userVo.getParentAddress());
                            user.setTotalQuota(assetsVo.getTotalQuota());
                            user.setSurplusQuota(assetsVo.getSurplusQuota());
                            user.setReceived(assetsVo.getReceived());
                            user.setGrade(userVo.getGrade());
                            user.setPeople(userVo.getPeople());
                        }

                        userMapper.updateById(user);
                    } catch (Exception e) {
                        log.error(e.getMessage());
                    }

                }
            } catch (Exception e) {
                log.error(e.getMessage());
            }

        }
    }

    public UserVo getUser(String addrss) {
        Web3j web3j = web3jConfig.getWeb3jById(new BigInteger(chainIds));
        ERC20Contract btlv = ERC20Contract.builder(web3j, contract);
        List<Type> userInfo = null;
        UserVo userVo = new UserVo();
        try {
            userInfo = btlv.otherSelect(
                    EthAbiCodecTool.getInputData(
                            "_user",// 要调用的方法名称
                            new Address(addrss)
                    ),  // 要调用的方法的inputData
                    new TypeReference<Address>() {
                    },
                    new TypeReference<Address>() {
                    },
                    new TypeReference<Uint256>() {
                    },
                    new TypeReference<Uint256>() {
                    },
                    new TypeReference<Uint256>() {
                    },
                    new TypeReference<Uint256>() {
                    },
                    new TypeReference<Uint256>() {
                    },
                    new TypeReference<Uint256>() {
                    },
                    new TypeReference<Uint256>() {
                    },
                    new TypeReference<Uint256>() {
                    },
                    new TypeReference<Uint256>() {
                    },
                    new TypeReference<Uint256>() {
                    },
                    new TypeReference<Uint256>() {
                    }
            );

        } catch (Exception e) {
            log.error("Failed to obtain user information，address:{}，reason：{}", addrss, e.getMessage());
        }
        if (ObjectUtil.isNotEmpty(userInfo)) {
            for (int i = 0; i < userInfo.size(); i++) {
                switch (i) {
                    case 0:
                        userVo.setUserAddr(userInfo.get(i).getValue().toString());
                        break;
                    case 1:
                        userVo.setParentAddress(userInfo.get(i).getValue().toString());
                        break;
                    case 2:
                        userVo.setUsdtPrice(MoneyTransferTool.transferLong(userInfo.get(i).getValue().toString()));
                        break;
                    case 3:
                        userVo.setPayUsdt(MoneyTransferTool.transferLong(userInfo.get(i).getValue().toString()));
                        break;
                    case 4:
                        userVo.setPayBtl(MoneyTransferTool.transferLong(userInfo.get(i).getValue().toString()));
                        break;
                    case 5:
                        userVo.setDailyRate(MoneyTransferTool.transferLong(userInfo.get(i).getValue().toString()));
                        break;
                    case 6:
                        userVo.setPeople(Integer.valueOf(userInfo.get(i).getValue().toString()));
                        break;
                    case 7:
                        userVo.setGrade(Integer.valueOf(userInfo.get(i).getValue().toString()));
                        break;
                    case 8:
                        userVo.setGradeRatio(MoneyTransferTool.transferLong(userInfo.get(i).getValue().toString()));
                        break;
                    case 9:
                        userVo.setAccelerate1(MoneyTransferTool.transferLong(userInfo.get(i).getValue().toString()));
                        break;
                    case 10:
                        userVo.setAccelerate2(MoneyTransferTool.transferLong(userInfo.get(i).getValue().toString()));
                        break;
                    case 11:
                        userVo.setAccelerate3(MoneyTransferTool.transferLong(userInfo.get(i).getValue().toString()));
                        break;
                    case 12:
                        userVo.setPerformance1(MoneyTransferTool.transferLong(userInfo.get(i).getValue().toString()));
                        break;
                }
            }
        }
        return userVo;
    }

    public AssetsVo getAssets(String addrss) {
        Web3j web3j = web3jConfig.getWeb3jById(new BigInteger(chainIds));
        ERC20Contract btlv = ERC20Contract.builder(web3j, contract);
        List<Type> userInfo = null;
        AssetsVo assetsVo = new AssetsVo();
        try {
            userInfo = btlv.otherSelect(
                    EthAbiCodecTool.getInputData(
                            "_assets",// 要调用的方法名称
                            new Address(addrss)
                    ),  // 要调用的方法的inputData
                    new TypeReference<Address>() {
                    },
                    new TypeReference<Uint256>() {
                    },
                    new TypeReference<Uint256>() {
                    },
                    new TypeReference<Uint256>() {
                    },
                    new TypeReference<Uint256>() {
                    },
                    new TypeReference<Uint256>() {
                    },
                    new TypeReference<Uint256>() {
                    },
                    new TypeReference<Uint256>() {
                    },
                    new TypeReference<Uint256>() {
                    },
                    new TypeReference<Uint256>() {
                    },
                    new TypeReference<Uint256>() {
                    },
                    new TypeReference<Uint256>() {
                    }
            );

        } catch (Exception e) {
            log.error("Failed to obtain user information，address:{}，reason：{}", addrss, e.getMessage());
        }
        if (ObjectUtil.isNotEmpty(userInfo)) {
            for (int i = 0; i < userInfo.size(); i++) {
                switch (i) {
                    case 0:
                        assetsVo.setUserAddr(userInfo.get(i).getValue().toString());
                        break;
                    case 1:
                        assetsVo.setTotalQuota(MoneyTransferTool.transferLong(userInfo.get(i).getValue().toString()));
                        break;
                    case 2:
                        assetsVo.setSurplusQuota(MoneyTransferTool.transferLong(userInfo.get(i).getValue().toString()));
                        break;
                    case 3:
                        assetsVo.setTotalMarket(MoneyTransferTool.transferLong(userInfo.get(i).getValue().toString()));
                        break;
                    case 4:
                        assetsVo.setSurplusMarket(MoneyTransferTool.transferLong(userInfo.get(i).getValue().toString()));
                        break;
                    case 5:
                        assetsVo.setCollected(MoneyTransferTool.transferLong(userInfo.get(i).getValue().toString()));
                        break;
                    case 6:
                        assetsVo.setReceived(MoneyTransferTool.transferLong(userInfo.get(i).getValue().toString()));
                        break;
                    case 7:
                        assetsVo.setReceivedUsdt(MoneyTransferTool.transferLong(userInfo.get(i).getValue().toString()));
                        break;
                    case 8:
                        assetsVo.setReceivedBtlv(MoneyTransferTool.transferLong(userInfo.get(i).getValue().toString()));
                        break;
                    case 9:
                        assetsVo.setDrawUsdt(MoneyTransferTool.transferLong(userInfo.get(i).getValue().toString()));
                        break;
                    case 10:
                        assetsVo.setDrawBtl(MoneyTransferTool.transferLong(userInfo.get(i).getValue().toString()));
                        break;
                    case 11:
                        assetsVo.setLastTime(Long.valueOf(userInfo.get(i).getValue().toString()));
                        break;
                }
            }
        }
        return assetsVo;
    }
}
