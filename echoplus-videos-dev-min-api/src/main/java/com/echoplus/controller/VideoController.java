package com.echoplus.controller;

import com.echoplus.pojo.Bgm;
import com.echoplus.pojo.Comments;
import com.echoplus.pojo.Videos;
import com.echoplus.service.IBgmService;
import com.echoplus.service.IVideoService;
import com.echoplus.utils.*;
import io.swagger.annotations.*;
import io.swagger.models.auth.In;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Date;
import java.util.UUID;

/**
 * 视频上传接口
 *
 * @author Liupeng
 * @createTime 2018-11-12 22:47
 **/
@RestController
@RequestMapping("/video")
@Api(value = "上传视频接口",tags = {"用户上传视频的接口"})
public class VideoController extends RedisBasicController{

    @Autowired
    private IBgmService bgmService;

    @Autowired
    private IVideoService videoService;


@PostMapping(value = "/upload",headers = "content-type=multipart/form-data")
@ApiOperation(value = "用户上传视频", notes = "用户上传视频的接口")
@ApiImplicitParams({
        @ApiImplicitParam(name = "userId", value = "用户Id", required=true,
                dataType = "String", paramType = "form"),
        @ApiImplicitParam(name = "bgmId", value = "选择的bgmId", required=false,
                dataType = "String", paramType = "form"),
        @ApiImplicitParam(name = "videoSeconds", value = "视频秒速", required=true,
                dataType = "String", paramType = "form"),
        @ApiImplicitParam(name = "videoWidth", value = "视频宽度", required=true,
                dataType = "String", paramType = "form"),
        @ApiImplicitParam(name = "videoHeight", value = "视频高度", required=true,
                dataType = "String", paramType = "form"),
        @ApiImplicitParam(name = "desc", value = "视频描述", required=false,
                dataType = "String", paramType = "form")
}
)
    public EchoPlusJSONResult uploadFile(String userId,String bgmId,
                                         double videoSeconds,int videoWidth,
                                         int videoHeight,String desc,
                                         @ApiParam(value = "短视频文件",required = true)
                                         MultipartFile file
                                         ) throws IOException {
    String filePath = "/" + userId + "/video/";
    String coverDbPath = "/" + userId + "/video/";
    if (userId == null) {
        return EchoPlusJSONResult.errorMsg("userId为null，请校验");
    }
    FileOutputStream fileOutputStream = null;
     InputStream inputStream = null;
    //获取上传文件名称
    String filename;
    String totalFilePath = null;
    try {
        if (file != null || file.getSize() > 0) {
            //拼接路径
            filename = file.getOriginalFilename();
            if (StringUtils.isNotBlank(filename)) {
                //文件全路径
                String filenamePrefix = filename.split("\\.")[0];

                //文件总路径
                totalFilePath = FILE_STACE + filePath + filename;
                //视频的相对路径
                filePath += filename;
                //截图的相对路径
                coverDbPath = coverDbPath + "/" + filenamePrefix + ".jpg";

                File currentFile = new File(totalFilePath);
                currentFile.getParentFile();
                if (currentFile.getParentFile() != null || currentFile.getParentFile().isDirectory()) {
                    currentFile.getParentFile().mkdirs();//没有该文件，创建新的文件
                }
                 fileOutputStream = new FileOutputStream(totalFilePath);
                inputStream = file.getInputStream();
                IOUtils.copy(inputStream, fileOutputStream);//输入流和输入流拷贝
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
    }finally {
        if (fileOutputStream != null) {
            fileOutputStream.flush();
            fileOutputStream.close();
        }
        if (inputStream != null) {
            inputStream.close();
        }
    }
    //处理上传的视频
     if (StringUtils.isNotBlank(bgmId)) {
        //查询bgm
        Bgm bgm = bgmService.queryBgmById(bgmId);
        //Mp3文件路径
        String mp3FilePath = FILE_STACE + bgm.getPath();
        //原mp4文件路径
        String mp4FilePath = totalFilePath;

        //目标文件路径
        String outputVideoName = UUID.randomUUID().toString() + ".mp4";
        filePath = "/" + userId + "/video/" + outputVideoName;

        totalFilePath = FILE_STACE + filePath;

        FfmpegMerge ffmpegMerge = new FfmpegMerge(FFMPEG_EXE);
        ffmpegMerge.convertor(mp4FilePath,mp3FilePath,videoSeconds,totalFilePath);
    }
    System.out.println("totalFilePath="+totalFilePath);
    System.out.println("filePath=" + filePath);

    //对视频进行截图

    FfmpegCover ffmpegUtils = new FfmpegCover(FFMPEG_EXE);
    ffmpegUtils.convertor(totalFilePath, FILE_STACE + coverDbPath);

    //保存视频视频信息到数据库
    Videos videos = new Videos();
    videos.setAudioId(bgmId);
    videos.setUserId(userId);
    videos.setVideoDesc(desc);
    videos.setVideoPath(filePath);
    videos.setVideoWidth(videoWidth);
    videos.setVideoHeight(videoHeight);
    videos.setStatus(VideoStatus.SUCCESS.getValue());
    videos.setCreateTime(new Date());
    videos.setVideoSeconds((float)videoSeconds);
    videos.setCoverPath(coverDbPath);//截图的相对路径
    String videoid = videoService.saveVideos(videos);

    //返回VideoId，处理封面问题
    return EchoPlusJSONResult.ok(videoid);
    }


    @ApiOperation(value = "上传封面",notes = "上传封面的接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "videoId",value = "视频主键Id",required = true,
                    dataType = "String",paramType = "form"),
            @ApiImplicitParam(name = "userId",value = "用户Id",required = true,
                    dataType = "String",paramType = "form"),
    })


    @PostMapping("/uploadCover")
    public EchoPlusJSONResult uploadCover(@ApiParam(value = "视频封面",required = true) MultipartFile file,
                                                      String videoId,String userId) {
        if (StringUtils.isBlank(videoId) || StringUtils.isBlank(userId)) {
            return EchoPlusJSONResult.errorMsg("视频Id或者用户Id不能为null");
        }
        //保存在数据库中的名称

        String coverInDbName = "/" + userId + "/";
        String finalOverPath ;
        FileOutputStream fileOutputStream = null;
        InputStream inputStream = null;
        try {
        if (file != null) {
            String originalFilename = file.getOriginalFilename();
            //获得总路径
            finalOverPath = FILE_STACE + coverInDbName + originalFilename;
            coverInDbName += originalFilename;
            File outFile = new File(finalOverPath);
            if (outFile.getParentFile() != null || !outFile.getParentFile().isDirectory()) {
                outFile.getParentFile().mkdirs();
            }
                fileOutputStream = new FileOutputStream(outFile);
                inputStream = file.getInputStream();
            IOUtils.copy(inputStream, fileOutputStream);
            }
        }catch (FileNotFoundException e) {
                e.printStackTrace();
            return EchoPlusJSONResult.errorMsg("上传文件出错");
            } catch (IOException e) {
            e.printStackTrace();
            return EchoPlusJSONResult.errorMsg("上传文件出错");
        }finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.flush();
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        videoService.updateCoverPath(videoId, coverInDbName);
        return EchoPlusJSONResult.ok();
    }

    //
    //isSaveRecord :1 需要保存，0 不需要保存或者无参数
    @ApiOperation(value = "查询所有视频", notes = "查询所有视频的接口")
    @PostMapping("/showAllVideos")
    public EchoPlusJSONResult showAllVideos(@RequestBody Videos videos,
                                            Integer isSaveRecord,Integer pageSize,Integer page) {
        page = page == null ? 1 : page;
        pageSize = pageSize==null?PAGE_SIZE:pageSize;
        videos.setUserId(videos.getUserId());
        PagesResult allvideos = videoService.getAllvideos(videos,isSaveRecord,page, pageSize);
        return EchoPlusJSONResult.ok(allvideos);
    }

    //查看收藏视频
    @ApiOperation(value = "查看收藏视频", notes = "查看收藏视频的接口")
    @PostMapping("/showMyLike")
    public EchoPlusJSONResult showMyLike(String userId,Integer pageSize,Integer page) {
        page = page == null ? 1 : page;
        pageSize = pageSize == null ? 6 : pageSize;
        PagesResult result = videoService.queryMyLikeVideos(userId, page, pageSize);
        return EchoPlusJSONResult.ok(result);
    }

    //查看关注的视频
    @ApiOperation(value = "查看关注视频", notes = "查看关注视频的接口")
    @PostMapping("/showMyFollow")
    public EchoPlusJSONResult showMyFollow(String userId,Integer pageSize,Integer page) {
        page = page == null ? 1 : page;
        pageSize = pageSize == null ? 6 : pageSize;
        PagesResult result = videoService.queryMyFollowVideos(userId, page, pageSize);
        return EchoPlusJSONResult.ok(result);
    }


    @ApiOperation(value = "查询排序后的热搜词", notes = "查询排序后的热搜词")
    @PostMapping("/selectHotWord")
    public EchoPlusJSONResult hotWord() {
        return EchoPlusJSONResult.ok(videoService.selectHotWord());
    }


    @ApiOperation(value = "用户喜欢视频",notes = "用户喜欢视频的接口")
    @PostMapping("/userLike")
    public EchoPlusJSONResult userLikeVideo(String userId, String videoId, String videoCreateId) {
        videoService.userLikeVideo(userId, videoId, videoCreateId);
        return EchoPlusJSONResult.ok();
    }

    @ApiOperation(value = "用户不喜欢视频",notes = "用户不喜欢视频的接口")
    @PostMapping("/userUnLike")
    public EchoPlusJSONResult userUnLikeVideo(String userId, String videoId, String videoCreateId) {
        videoService.userUnLikeVideo(userId, videoId, videoCreateId);
        return EchoPlusJSONResult.ok();
    }

    @ApiOperation(value = "保存用户留言",notes = "保存用户留言的接口")
    @PostMapping("/saveComment")
    public EchoPlusJSONResult saveComment(@RequestBody Comments comment,
                                       String fatherCommentId, String toUserId) {

        comment.setFatherCommentId(fatherCommentId);
        comment.setToUserId(toUserId);

        videoService.insertComments(comment);
        return EchoPlusJSONResult.ok();
    }


    @ApiOperation(value ="获取视频留言",notes = "获取视频留言的接口")
    @PostMapping("/getVideoComments")
    public EchoPlusJSONResult getVideoComments(String videoId,Integer page,Integer pageSize) {
        if (StringUtils.isBlank(videoId)) {
            return EchoPlusJSONResult.ok();
        }

        if (page == null) {
            page = 1;
        }
        if (pageSize == null) {
            pageSize = 10;
        }

        PagesResult list = videoService.getAllComments(videoId, page, pageSize);
        return EchoPlusJSONResult.ok(list);

    }
}