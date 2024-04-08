package com.zykj.btlv.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.zykj.btlv.req.LoginReq;
import com.zykj.btlv.result.Result;
import com.zykj.btlv.result.ResultEnum;
import com.zykj.btlv.result.ResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@CrossOrigin
@Api(value = "登录",tags = "登录")
@RequestMapping(value = "login")
public class LoginController {

    public static final String loginSignature = "BTLV:LOGIN";

    public static final String name = "admin123";

    public static final String pwd = "adminBTLV1159";
    @ApiOperation("验证签名")
    @RequestMapping(value = "validate", method = RequestMethod.POST)
    public Result<Boolean> validate(@RequestBody LoginReq loginReq) {
        Boolean flang = com.zykj.tool.Web3jTool.validate(loginReq.getSign(), loginSignature,loginReq.getUserAddr());
        return ResultUtil.success(flang);
    }

    @ApiOperation("后台登录")
    @RequestMapping(value = "login", method = RequestMethod.POST)
    public Result<Boolean> login(@RequestBody LoginReq loginReq) {
        if(loginReq.getUserAddr().equals(name) && loginReq.getSign().equals(pwd)){
            StpUtil.login(loginReq.getUserAddr());
            String token = StpUtil.getTokenValue();
            return ResultUtil.success(token);
        }else {
            return ResultUtil.error(ResultEnum.FAIL);
        }

    }
}
