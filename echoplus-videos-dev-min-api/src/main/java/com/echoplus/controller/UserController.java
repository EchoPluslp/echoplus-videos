package com.echoplus.controller;

import com.echoplus.pojo.Users;
import com.echoplus.pojo.UsersReport;
import com.echoplus.pojo.Videos;
import com.echoplus.service.IUserService;
import com.echoplus.utils.EchoPlusJSONResult;
import com.echoplus.vo.PublisherVideo;
import com.echoplus.vo.UsersVo;
import com.echoplus.vo.VideosVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.List;

/**
 * @author Liupeng
 * @createTime 2018-11-05 23:33
 **/
@RestController
@Api(value = "用户相关业务的接口",tags = {"用户相关业务的接口"})
@RequestMapping("/user")
public class UserController {
    @Autowired
    private IUserService userService;

    @ApiOperation(value = "用户上传头像", notes = "用户上传图片的接口")
    @ApiImplicitParam(name = "userId",value = "用户Id",required = true,
            dataType = "String",paramType = "query")
    @PostMapping("/uploadFace")
    public EchoPlusJSONResult uploadFile(String userId,MultipartFile[] files) throws IOException {
        //拼接文件夹路径
        String dirFilePath = System.lineSeparator()+"user"+System.lineSeparator()+"echoplus-video-dev";
        //拼接文件路径
        String fileDbPath = System.lineSeparator() + userId + System.lineSeparator();
        if (userId == null) {
            return EchoPlusJSONResult.errorMsg("用户Id为空，请校验");
        }
        //整个全路径
        FileOutputStream fileOutputStream = null;
        InputStream inputStream = null;
        try {
        if (files != null || files.length > 0) {
            String filename = files[0].getOriginalFilename();
            if (StringUtils.isNotBlank(filename)) {
                String totalFileName = dirFilePath + fileDbPath + filename;
                //设置db存储路径
                fileDbPath += (filename);
                //判断当前路径是否存在，如果不存在则生成
                File file = new File(totalFileName);
                if (file.getParentFile() != null || file.getParentFile().isDirectory()) {
                    //当前路径不存在，生成改文件
                    file.getParentFile().mkdirs();
                }
                fileOutputStream = new FileOutputStream(file);
                inputStream = files[0].getInputStream();
                //拷贝文件，输入输出流
                IOUtils.copy(inputStream, fileOutputStream);
            }}else {
                return EchoPlusJSONResult.errorMsg("图片上传失败，请重新上传...");
        }
        } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
            e.printStackTrace();
            return EchoPlusJSONResult.errorMsg("图片上传失败，请重新上传...");
        }finally{
            if (fileOutputStream != null) {
                fileOutputStream.flush();
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStream != null) {
                inputStream.close();
            }}
            //用户上传文件成功
        Users users = new Users();
        users.setId(userId);
        users.setFaceImage(fileDbPath);
        if (userService.updateUserInfo(users) <= 0) {
            return EchoPlusJSONResult.errorMsg("保存图片失败...请重新上传");
        }
        return EchoPlusJSONResult.ok(fileDbPath);
    }


    @ApiOperation(value = "查询用户信息",notes = "查询用户信息的接口")

    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId",value = "用户Id",required = true,
                    dataType = "String",paramType = "query"),
            @ApiImplicitParam(name = "fanId",value = "粉丝Id",
                    dataType = "String",paramType = "query")
    })
    @PostMapping("/query")
    public EchoPlusJSONResult queryUser(String userId,String fanId) {
        if (StringUtils.isBlank(userId)) {
            return EchoPlusJSONResult.errorMsg("用户Id为空，请校验");
        }
        List<Users> users =   userService.queryUserInfo(userId);
        if (users.size() == 0) {
            return EchoPlusJSONResult.errorMsg("没有该用户");
        }
        Users usersBean = users.get(0);
        UsersVo usersVo = new UsersVo();
        BeanUtils.copyProperties(usersBean,usersVo);
        usersVo.setFollow(userService.checkIfFans(userId, fanId));
        return EchoPlusJSONResult.ok(usersVo);
    }

    @ApiOperation(value = "查询视频所属用户的详情信息",notes = "查询视频所属用户的详情信息的接口")
    @PostMapping("/query/publisher")
    public EchoPlusJSONResult queryPublisher(String loginUserId,String videoId,
                                             String publisherId) {
        if (StringUtils.isBlank(publisherId)) {
            return EchoPlusJSONResult.errorMsg("参数异常，请校验");
        }

        //查询视频发布者的信息
        List<Users> users = userService.queryUserInfo(publisherId);
        if (users.size() <= 0 || users == null) {
            return EchoPlusJSONResult.errorMsg("未找到视频所属者");

        }
        UsersVo videosVo = new UsersVo();
        BeanUtils.copyProperties(users.get(0), videosVo);

        //查询当前登录者和视频点赞的关系
        Boolean flag = userService.userLikeVideos(loginUserId, videoId);

        PublisherVideo publisherVideo = new PublisherVideo();
        publisherVideo.setPublisher(videosVo);
        publisherVideo.setUserLikeVideo(flag);

        return EchoPlusJSONResult.ok(publisherVideo);
    }

    @ApiOperation(value = "关注用户",notes = "关注用户的接口")
    @PostMapping("/query/beYourFans")
    public EchoPlusJSONResult beYourFans(String publisherId,String fanId) {
        if (StringUtils.isBlank(publisherId) || StringUtils.isBlank(fanId)) {
            return EchoPlusJSONResult.errorMsg("参数异常，请校验");
        }
        userService.addUserFansRelation(publisherId,fanId);

        return EchoPlusJSONResult.ok("关注成功...");
    }

    @ApiOperation(value = "取消关注用户",notes = "取消关注用户的接口")
    @PostMapping("/query/notBeYourFans")
    public EchoPlusJSONResult notBeYourFansd(String publisherId,String fanId) {
        if (StringUtils.isBlank(publisherId) || StringUtils.isBlank(fanId)) {
            return EchoPlusJSONResult.errorMsg("参数异常，请校验");
        }
        userService.removeUserFansRelation(publisherId,fanId);
        return EchoPlusJSONResult.ok("取消关注成功...");
    }

    @ApiOperation(value = "举报当前用户",notes = "举报当前用户的接口")
    @PostMapping("/reportUser")
    public EchoPlusJSONResult reportUser(@RequestBody UsersReport usersReport) {
        if (usersReport == null) {
            return EchoPlusJSONResult.ok("参数异常...");
        }
        userService.reportUser(usersReport);
        return EchoPlusJSONResult.ok("举报当前用户成功...");
    }

}