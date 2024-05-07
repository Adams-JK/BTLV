package com.zykj.btlv.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zykj.btlv.domain.AwardRecord;
import com.zykj.btlv.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zykj.btlv.result.Result;
import com.zykj.btlv.vo.DistributeDataVo;
import org.springframework.ui.Model;

import java.math.BigDecimal;
import java.util.List;

/**
* @author argo
* @description 针对表【user】的数据库操作Service
* @createDate 2024-04-03 17:23:13
*/
public interface UserService extends IService<User> {

    Result<Page<User>> getUser(String userAddr, String parentAddress, Integer grade, Integer sort, Integer page, Integer offset);

    Result<Page<AwardRecord>> getRecord(String userAddr, Integer type, Integer page, Integer offset);

    Result<BigDecimal> getPool(Integer type);

    Result<DistributeDataVo> getDistributeData(Integer type);

    List<String> getDistributeDataV2(Integer type);

    Result<List<User>> getSJUser();
}
