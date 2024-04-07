package com.zykj.btlv.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zykj.btlv.domain.User;
import com.zykj.btlv.result.Result;
import com.zykj.btlv.result.ResultUtil;
import com.zykj.btlv.service.UserService;
import com.zykj.btlv.mapper.UserMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

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
}




