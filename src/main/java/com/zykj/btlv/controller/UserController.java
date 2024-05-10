package com.zykj.btlv.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zykj.btlv.domain.AwardRecord;
import com.zykj.btlv.domain.User;
import com.zykj.btlv.result.Result;
import com.zykj.btlv.result.ResultUtil;
import com.zykj.btlv.service.UserService;
import com.zykj.btlv.vo.DistributeDataVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

@Slf4j
@RestController
@CrossOrigin
@Api(value = "用户",tags = "用户")
@RequestMapping(value = "user")
public class UserController {
    @Resource
    private UserService userService;

    @Value("${batchTransferERC20}")
    public String batchTransferERC20;

    @Value("${contract}")
    public String contract;

    @Value("${node}")
    public String node;

    @Value("${market}")
    public String market;

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

    @ApiOperation("获取可升级用户信息")
    @SaCheckLogin
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page",value = "页码", required = false, dataType = "int"),
            @ApiImplicitParam(name = "offset",value = "记录条数", required = false, dataType = "int"),
    })
    @RequestMapping(value = "/getSJUser", method = RequestMethod.GET)
    public Result<Page<User>> getSJUser(@RequestParam(required = false) Integer page, @RequestParam(required = false) Integer offset) throws Exception {
        return userService.getSJUser(page,offset);
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
    public Result<BigDecimal> getPool(@RequestParam Integer type) throws Exception {
        return userService.getPool(type);
    }

    @ApiOperation("获取分配合约")
    @SaCheckLogin
    @RequestMapping(value = "/getDistribute", method = RequestMethod.GET)
    public Result<String> getDistribute() throws Exception {
        return ResultUtil.success(batchTransferERC20);
    }

    @ApiOperation("获取地址")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type",value = "1：LP地址，2：持币地址，3：btlv地址", required = true, dataType = "int")
    })
    @SaCheckLogin
    @RequestMapping(value = "/getAddr", method = RequestMethod.GET)
    public Result<String> getAddr(@RequestParam Integer type) throws Exception {
        String addr = "";
        if(type.equals(1)){
            addr = node;
        }else if(type.equals(2)){
            addr = market;
        }else if(type.equals(3)){
            addr = contract;
        }
        return ResultUtil.success(addr);
    }

    @ApiOperation("获取分配数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type",value = "1：LP，2：持币", required = true, dataType = "int")
    })
    @SaCheckLogin
    @RequestMapping(value = "/getDistributeData", method = RequestMethod.GET)
    public Result<DistributeDataVo> getDistributeData(@RequestParam Integer type) throws Exception {
        return userService.getDistributeData(type);
    }

    @ApiOperation("分配数据展示")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type",value = "1：LP，2：持币", required = true, dataType = "int")
    })
    @RequestMapping(value = "/getDistributeDataV2", method = RequestMethod.GET)
    public ModelAndView getDistributeDataV2(@RequestParam Integer type) throws Exception {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("sortedPage"); // 设置视图名称为 sortedPage.html
        modelAndView.addObject("sortedData", userService.getDistributeDataV2(type)); // 添加模型数据
        return modelAndView;
    }
}
