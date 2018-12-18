package com.echoplus.mapper.mt;

import com.echoplus.vo.CommentsVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MtCommentsCustMapper {
    List<CommentsVo> queryComments(@Param("videoId") String videoId);
}