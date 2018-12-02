package com.echoplus.mapper;

import com.echoplus.pojo.Users;
import com.echoplus.pojo.UsersExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UsersMapper {
    int countByExample(UsersExample example);

    int deleteByExample(UsersExample example);

    int deleteByPrimaryKey(String id);

    int insert(Users record);

    int insertSelective(Users record);

    List<Users> selectByExample(UsersExample example);

    Users selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") Users record, @Param("example") UsersExample example);

    int updateByExample(@Param("record") Users record, @Param("example") UsersExample example);

    int updateByPrimaryKeySelective(Users record);

    int updateByPrimaryKey(Users record);

    int addReceiveLikeCounts(String userId);

    int reduceReceiveLikeCounts(String userId);

    //增加用户粉丝的数量
    int addUserFans(String userId);

    //减少用户粉丝的数量
    int reduceUserFans(String userId);

    //增加用户关注数的数量
    int addUserFollersFans(String userId);

    //减少用户关注数的数量
    int reduceUserFollersFans(String userId);


}