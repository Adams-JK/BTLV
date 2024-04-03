package com.zykj.btlv.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zykj.btlv.domain.User;
import com.zykj.btlv.service.UserService;
import com.zykj.btlv.mapper.UserMapper;
import org.springframework.stereotype.Service;

/**
* @author argo
* @description 针对表【user】的数据库操作Service实现
* @createDate 2024-04-03 17:23:13
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

}




