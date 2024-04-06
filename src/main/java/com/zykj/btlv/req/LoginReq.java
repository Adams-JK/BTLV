package com.zykj.btlv.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel("登录REQ")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginReq {
    @ApiModelProperty(value = "钱包地址")
    private String userAddr;

    @ApiModelProperty(value = "钱包签名")
    private String sign;
}
