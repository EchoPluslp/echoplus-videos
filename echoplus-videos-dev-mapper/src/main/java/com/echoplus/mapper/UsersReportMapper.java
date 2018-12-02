package com.echoplus.mapper;

import com.echoplus.pojo.UsersReport;
import com.echoplus.pojo.UsersReportExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UsersReportMapper {
    int countByExample(UsersReportExample example);

    int deleteByExample(UsersReportExample example);

    int deleteByPrimaryKey(String id);

    int insert(UsersReport record);

    int insertSelective(UsersReport record);

    List<UsersReport> selectByExample(UsersReportExample example);

    UsersReport selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") UsersReport record, @Param("example") UsersReportExample example);

    int updateByExample(@Param("record") UsersReport record, @Param("example") UsersReportExample example);

    int updateByPrimaryKeySelective(UsersReport record);

    int updateByPrimaryKey(UsersReport record);
}