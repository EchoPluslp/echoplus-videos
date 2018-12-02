package com.echoplus.service;

import com.echoplus.pojo.Comments;
import com.echoplus.pojo.Videos;
import com.echoplus.utils.PagesResult;

import java.util.List;

/**
 * 用户上传视频的接口
 *
 * @author Liupeng
 * @create 2018-11-12 22:49
 **/
public interface IVideoService {
    String saveVideos(Videos videozs);

    int updateCoverPath(String videoId, String coverPath);

    /*
        分页查询视频列表
     */
    PagesResult getAllvideos(Videos videos, Integer isSaveRecord, Integer page, Integer pageSize);

    List<String> selectHotWord();

    void userLikeVideo(String userId, String videoId, String videoCreateId);

    void userUnLikeVideo(String userId,String videoId,String videoCreateId);

    PagesResult queryMyLikeVideos(String uesrId, Integer page, Integer pageSize);

    PagesResult queryMyFollowVideos(String userId, Integer page, Integer pageSize);

    void insertComments(Comments comments);

    PagesResult getAllComments(String videoId, Integer page, Integer pageSize);

}
