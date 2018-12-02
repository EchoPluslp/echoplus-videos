package com.echoplus.mapper;

import com.echoplus.pojo.Videos;
import com.echoplus.vo.VideosVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface VideosMapperCustom {
    List<VideosVo> queryAllVideos(@Param("videoDesc") String desc, @Param("userId") String userId);

    //增加视频信息列表喜欢的数量
    Integer addVideosLikeCounts(@Param("videoId") String videoId);

    //减少视频信息列表喜欢的数量
    Integer reduceVideosLikeCounts(@Param("videoId") String videoId);

    List<Videos> queryMyLikes(@Param("userId") String userId);

    List<Videos> queryMyFollowVideos(@Param("userId") String userId);
}
