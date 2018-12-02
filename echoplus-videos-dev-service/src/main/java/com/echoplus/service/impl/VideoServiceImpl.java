package com.echoplus.service.impl;

import com.echoplus.mapper.*;
import com.echoplus.mapper.mt.MtCommentsCustMapper;
import com.echoplus.pojo.*;
import com.echoplus.service.IVideoService;
import com.echoplus.utils.PagesResult;
import com.echoplus.utils.TimeAgoUtils;
import com.echoplus.vo.CommentsVo;
import com.echoplus.vo.VideosVo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @author Liupeng
 * @createTime 2018-11-12 22:50
 **/
@Service
public class VideoServiceImpl implements IVideoService {

    @Autowired
    private VideosMapper videosMapper;

    @Autowired
    private VideosMapperCustom videosMapperCustom;

    @Autowired
    private SearchRecordsMapper searchRecordsMapper;

    @Autowired
    private UsersLikeVideosMapper usersLikeVideosMapper;

    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private CommentsMapper commentsMapper;
    @Autowired
    private Sid sid;

    @Autowired
    private MtCommentsCustMapper mtCommentsCustMapper;


    //当前没有事务，新建事务，如果有事务，则加入
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public String saveVideos(Videos videos) {
        String videoId = sid.next();
        videos.setId(videoId);
         videosMapper.insertSelective(videos);
        return videoId;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public int updateCoverPath(String videoId, String coverPath) {
        Videos videos = new Videos();
        videos.setId(videoId);
        videos.setCoverPath(coverPath);
        return videosMapper.updateByPrimaryKeySelective(videos);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagesResult getAllvideos(Videos videos, Integer isSaveRecord, Integer page, Integer pageSize) {
       //保存热搜词
        String desc = videos.getVideoDesc();
        if (isSaveRecord != null && isSaveRecord == 1) {
            SearchRecords searchRecords = new SearchRecords();
            searchRecords.setContent(desc);
            searchRecords.setId(sid.next());
            searchRecordsMapper.insert(searchRecords);
        }

        //查找视频
        PageHelper.startPage(page, pageSize);
        List<VideosVo> videosVos = videosMapperCustom.queryAllVideos(desc,videos.getUserId());
        PageInfo pageInfo = new PageInfo(videosVos);
        PagesResult result = new PagesResult();
        result.setPage(page);
        result.setRows(videosVos);
        result.setRecords(pageInfo.getTotal());
        result.setTotal(pageInfo.getPages());
        return result;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<String> selectHotWord() {
       return searchRecordsMapper.selectHotWords();
    }


    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void userLikeVideo(String userId, String videoID, String videoCreateId) {
        //插入对应的记录到userLikeVideos表中
        UsersLikeVideos usersLikeVideos = new UsersLikeVideos();
        String ulvId = sid.next();
        usersLikeVideos.setId(ulvId);
        usersLikeVideos.setUserId(userId);
        usersLikeVideos.setVideoId(videoID);
        int insert = usersLikeVideosMapper.insertSelective(usersLikeVideos);

        //添加用户表（视频创建者）中的receive_like_counts
        int i = usersMapper.addReceiveLikeCounts(videoCreateId);

        //修改video表中的like_counts的数量
        int x = videosMapperCustom.addVideosLikeCounts(videoID);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void userUnLikeVideo(String userId, String videoID, String videoCreateId) {
        //插入对应的记录到userLikeVideos表中
        UsersLikeVideosExample usersLikeVideosExample = new UsersLikeVideosExample();
        usersLikeVideosExample.createCriteria().andUserIdEqualTo(userId).andVideoIdEqualTo(videoID);
        usersLikeVideosMapper.deleteByExample(usersLikeVideosExample);
        //添加用户表（视频创建者）中的receive_like_counts
        usersMapper.reduceReceiveLikeCounts(videoCreateId);

        //修改video表中的like_counts的数量
        videosMapperCustom.reduceVideosLikeCounts(videoID);
    }

    //查询自己点赞的视频
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagesResult queryMyLikeVideos(String userId, Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        List<Videos> videos = videosMapperCustom.queryMyLikes(userId);

        PageInfo pageInfo = new PageInfo(videos);
        PagesResult result = new PagesResult();
        result.setPage(page);
        result.setRows(videos);
        result.setRecords(pageInfo.getTotal());
        result.setTotal(pageInfo.getPages());
        return result;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public PagesResult queryMyFollowVideos(String userId, Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        List<Videos> videos = videosMapperCustom.queryMyFollowVideos(userId);

        PageInfo pageInfo = new PageInfo(videos);
        PagesResult result = new PagesResult();
        result.setPage(page);
        result.setRows(videos);
        result.setRecords(pageInfo.getTotal());
        result.setTotal(pageInfo.getPages());
        return result;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void insertComments(Comments comments) {
        String id = sid.nextShort();
        comments.setCreateTime(new Date());
        comments.setId(id);
        commentsMapper.insertSelective(comments);
    }

    @Override
    public PagesResult getAllComments(String videoId, Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);
         List<CommentsVo> list = commentsMapper.queryComments(videoId);

            for (CommentsVo c :
                    list) {
                //设置时间格式
                c.setTimeAgoStr(TimeAgoUtils.format(c.getCreateTime()));
            }

            PageInfo<CommentsVo> pageInfo = new PageInfo<>(list);

            PagesResult pagesResult = new PagesResult();
            pagesResult.setRows(list);
            pagesResult.setPage(page);
            pagesResult.setTotal(pageInfo.getSize());
            pagesResult.setRecords(pageInfo.getTotal());
        return pagesResult ;
    }

}