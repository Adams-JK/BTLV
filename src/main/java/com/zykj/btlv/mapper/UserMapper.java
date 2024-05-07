package com.zykj.btlv.mapper;

import com.zykj.btlv.domain.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
* @author argo
* @description 针对表【user】的数据库操作Mapper
* @createDate 2024-04-03 17:23:13
* @Entity generator.domain.User
*/
@Mapper
public interface UserMapper extends BaseMapper<User> {

    @Select("select * from user where lp > 0")
    List<User> getLPAddr();

    @Select("select * from user where balance > 100000000")
    List<User> getHoldAddr();

    @Select("SELECT * FROM user WHERE isGrade > grade")
    List<User> getSJUser();
}




