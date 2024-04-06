package com.zykj.btlv.controller;

import com.zykj.btlv.req.LoginReq;
import com.zykj.btlv.result.Result;
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
    @ApiOperation("验证签名")
    @RequestMapping(value = "validate", method = RequestMethod.POST)
    public Result<Boolean> validate(@RequestBody LoginReq loginReq) {
        Boolean flang = com.zykj.tool.Web3jTool.validate(loginReq.getSign(), loginSignature,loginReq.getUserAddr());
        return ResultUtil.success(flang);
    }
}
