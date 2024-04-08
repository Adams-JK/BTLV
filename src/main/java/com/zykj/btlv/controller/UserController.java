package com.zykj.btlv.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zykj.btlv.domain.AwardRecord;
import com.zykj.btlv.domain.User;
import com.zykj.btlv.result.Result;
import com.zykj.btlv.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;

@Slf4j
@RestController
@CrossOrigin
@Api(value = "用户",tags = "用户")
@RequestMapping(value = "user")
public class UserController {
    @Resource
    private UserService userService;

    @ApiOperation("获取用户信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userAddr",value = "用户地址", required = false, dataType = "string"),
            @ApiImplicitParam(name = "parentAddress",value = "上级地址", required = false, dataType = "string"),
            @ApiImplicitParam(name = "grade",value = "管理奖等级", required = false, dataType = "int"),
            @ApiImplicitParam(name = "sort",value = "排序，1：lp，2：balance，3：totalQuota，4：surplusQuota，5：received，6：grade，7：people", required = false, dataType = "int"),
            @ApiImplicitParam(name = "page",value = "页码", required = false, dataType = "int"),
            @ApiImplicitParam(name = "offset",value = "记录条数", required = false, dataType = "int"),
    })
    @SaCheckLogin
    @RequestMapping(value = "/getUser", method = RequestMethod.GET)
    public Result<Page<User>> getUser(@RequestParam(required = false) String userAddr, @RequestParam(required = false) String parentAddress,
                                      @RequestParam(required = false) Integer grade, @RequestParam(required = false) Integer sort,
                                      @RequestParam(required = false) Integer page, @RequestParam(required = false) Integer offset) throws Exception {
        return userService.getUser(userAddr,parentAddress,grade,sort,page,offset);
    }

    @ApiOperation("获取奖励记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userAddr",value = "用户地址", required = false, dataType = "string"),
            @ApiImplicitParam(name = "type",value = "1：LP，2：持币", required = false, dataType = "int"),
            @ApiImplicitParam(name = "page",value = "页码", required = false, dataType = "int"),
            @ApiImplicitParam(name = "offset",value = "记录条数", required = false, dataType = "int"),
    })
    @SaCheckLogin
    @RequestMapping(value = "/getRecord", method = RequestMethod.GET)
    public Result<Page<AwardRecord>> getRecord(@RequestParam(required = false) String userAddr,
                                               @RequestParam(required = false) Integer type,
                                               @RequestParam(required = false) Integer page, @RequestParam(required = false) Integer offset) throws Exception {
        return userService.getRecord(userAddr,type,page,offset);
    }

    @ApiOperation("获取资金池")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type",value = "1：LP，2：持币", required = true, dataType = "int")
    })
    @SaCheckLogin
    @RequestMapping(value = "/getPool", method = RequestMethod.GET)
    public Result<BigDecimal> getPool(@RequestParam(required = true) Integer type) throws Exception {
        return userService.getPool(type);
    }
}
