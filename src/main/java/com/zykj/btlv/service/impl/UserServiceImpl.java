package com.zykj.btlv.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blockchain.tools.eth.contract.template.ERC20Contract;
import com.zykj.btlv.config.Web3jConfig;
import com.zykj.btlv.domain.AwardRecord;
import com.zykj.btlv.domain.User;
import com.zykj.btlv.mapper.AwardRecordMapper;
import com.zykj.btlv.result.Result;
import com.zykj.btlv.result.ResultEnum;
import com.zykj.btlv.result.ResultUtil;
import com.zykj.btlv.service.UserService;
import com.zykj.btlv.mapper.UserMapper;
import com.zykj.btlv.tool.MoneyTransferTool;
import com.zykj.btlv.vo.DistributeDataVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.web3j.protocol.Web3j;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author argo
* @description 针对表【user】的数据库操作Service实现
* @createDate 2024-04-03 17:23:13
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{
    @Resource
    private UserMapper userMapper;

    @Resource
    private AwardRecordMapper recordMapper;

    @Value("${node}")
    public String node;

    @Value("${market}")
    public String market;

    @Value("${contract}")
    public String contract;

    @Value("${chainId}")
    public String chainIds;

    @Autowired
    public Web3jConfig web3jConfig;

    @Override
    public Result<Page<User>> getUser(String userAddr, String parentAddress, Integer grade, Integer sort, Integer page, Integer offset) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        if(ObjectUtil.isNotEmpty(userAddr)){
            wrapper.eq("address",userAddr);
        }
        if(ObjectUtil.isNotEmpty(parentAddress)){
            wrapper.eq("parentAddress",parentAddress);
        }
        if(ObjectUtil.isNotEmpty(grade)){
            wrapper.eq("grade",grade);
        }
        if(ObjectUtil.isNotEmpty(sort)){
            if(sort.equals(1)){
                wrapper.orderByDesc("lp");
            }else if(sort.equals(2)){
                wrapper.orderByDesc("balance");
            }else if(sort.equals(3)){
                wrapper.orderByDesc("totalQuota");
            }else if(sort.equals(4)){
                wrapper.orderByDesc("surplusQuota");
            }else if(sort.equals(5)){
                wrapper.orderByDesc("received");
            }else if(sort.equals(6)){
                wrapper.orderByDesc("grade");
            }else if(sort.equals(7)){
                wrapper.orderByDesc("people");
            }
        }
        if(ObjectUtil.isEmpty(page) || page < 0){
            page = 1;
        }
        if(ObjectUtil.isEmpty(offset) || offset < 0){
            offset = 10;
        }
        Page<User> iPage = new Page<User>(page, offset);
        Page<User> selectPage = userMapper.selectPage(iPage,wrapper);
        return ResultUtil.success(selectPage);
    }

    @Override
    public Result<Page<AwardRecord>> getRecord(String userAddr, Integer type, Integer page, Integer offset) {
        QueryWrapper<AwardRecord> wrapper = new QueryWrapper<>();
        if(ObjectUtil.isNotEmpty(userAddr)){
            wrapper.eq("address",userAddr);
        }

        if(ObjectUtil.isNotEmpty(type)){
            wrapper.eq("type",type);
        }
        wrapper.orderByDesc("time");
        if(ObjectUtil.isEmpty(page) || page < 0){
            page = 1;
        }
        if(ObjectUtil.isEmpty(offset) || offset < 0){
            offset = 10;
        }
        Page<AwardRecord> iPage = new Page<AwardRecord>(page, offset);
        Page<AwardRecord> selectPage = recordMapper.selectPage(iPage,wrapper);
        return ResultUtil.success(selectPage);
    }

    @Override
    public Result<BigDecimal> getPool(Integer type) {
        Web3j web3j = web3jConfig.getWeb3jById(new BigInteger(chainIds));
        ERC20Contract btlv = ERC20Contract.builder(web3j, contract);
        BigDecimal btlvBalance = new BigDecimal("0");
        try {
            if (type.equals(1)) {
                btlvBalance = MoneyTransferTool.transferLong(btlv.balanceOf(node).toString());
            }else {
                btlvBalance = MoneyTransferTool.transferLong(btlv.balanceOf(market).toString());
            }
        }catch(Exception e){
            throw new RuntimeException(e);
        }
        return ResultUtil.success(btlvBalance);
    }

    @Override
    public Result<DistributeDataVo> getDistributeData(Integer type) {
        Web3j web3j = web3jConfig.getWeb3jById(new BigInteger(chainIds));
        ERC20Contract btlv = ERC20Contract.builder(web3j, contract);
        BigInteger btlvBalance = new BigInteger("0");
        List<User> users = new ArrayList<>();
        try {
            if (type.equals(1)) {
                btlvBalance = new BigInteger(btlv.balanceOf(node).toString());
                users = userMapper.getLPAddr();
            }else {
                btlvBalance = new BigInteger(btlv.balanceOf(market).toString());
                users = userMapper.getHoldAddr();
            }
        }catch(Exception e){
            throw new RuntimeException(e);
        }
        if(btlvBalance.compareTo(new BigInteger("10000000000000000000")) < 0 || users.size() < 1){
            return ResultUtil.error(ResultEnum.FAIL);
        }
        List<String> address = users.stream().map(s -> s.getAddress()).collect(Collectors.toList());
        List<BigDecimal> ss = new ArrayList<>();
        if (type.equals(1)) {
            ss = users.stream().map(s -> s.getLp()).collect(Collectors.toList());
        }else {
            ss = users.stream().map(s -> s.getBalance()).collect(Collectors.toList());
        }
        BigDecimal sum = ss.stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        List<String> quota = new ArrayList<>();
        for(BigDecimal decimal : ss){
            BigInteger a = new BigDecimal(btlvBalance).multiply(decimal.divide(sum,8,RoundingMode.HALF_DOWN)).toBigInteger();
            quota.add(a.toString());
        }
        DistributeDataVo dataVo = DistributeDataVo.builder()
                .userAddr(address)
                .quota(quota)
                .build();
        return ResultUtil.success(dataVo);
    }

    @Override
    public List<String> getDistributeDataV2(Integer type) {
        Web3j web3j = web3jConfig.getWeb3jById(new BigInteger(chainIds));
        ERC20Contract btlv = ERC20Contract.builder(web3j, contract);
        BigInteger btlvBalance = new BigInteger("0");
        List<User> users = new ArrayList<>();
        try {
            if (type.equals(1)) {
                btlvBalance = new BigInteger(btlv.balanceOf(node).toString());
                users = userMapper.getLPAddr();
            }else {
                btlvBalance = new BigInteger(btlv.balanceOf(market).toString());
                users = userMapper.getHoldAddr();
            }
        }catch(Exception e){
            throw new RuntimeException(e);
        }
        if(btlvBalance.compareTo(new BigInteger("10000000000000000000")) < 0 || users.size() < 1){
            return null;
        }
        List<String> address = users.stream().map(s -> s.getAddress()).collect(Collectors.toList());
        List<BigDecimal> ss = new ArrayList<>();
        if (type.equals(1)) {
            ss = users.stream().map(s -> s.getLp()).collect(Collectors.toList());
        }else {
            ss = users.stream().map(s -> s.getBalance()).collect(Collectors.toList());
        }
        BigDecimal sum = ss.stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        List<String> quota = new ArrayList<>();
        for(BigDecimal decimal : ss){
            BigInteger a = new BigDecimal(btlvBalance).multiply(decimal.divide(sum,8,RoundingMode.HALF_DOWN)).toBigInteger();
            quota.add(a.toString());
        }
        List<String> ssss = new ArrayList<>();
        for(int i = 0;i<address.size();i++){
            String aa = address.get(i) + "-----" + new BigDecimal(quota.get(i)).divide(BigDecimal.TEN.pow(18),6,RoundingMode.HALF_DOWN);
            ssss.add(aa);
        }
        return ssss;
    }

    @Override
    public Result<List<User>> getSJUser() {

        List<User> list = userMapper.getSJUser();
        return ResultUtil.success(list);
    }
}




