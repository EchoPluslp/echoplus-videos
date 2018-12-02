package com.echoplus.controller;

import com.echoplus.service.IBgmService;
import com.echoplus.utils.EchoPlusJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * BgmController
 *
 * @author Liupeng
 * @createTime 2018-11-08 22:29
 **/
@RestController
@RequestMapping("/bgm")
@Api(value = "背景音乐" ,tags = {"用户相关的背景音乐Controller"})
public class BgmController {


    @Autowired
    private IBgmService bgmService;

    @ApiOperation(value = "获取所有BGM列表",notes = "BGM列表")
    @PostMapping("/bgmList")
    public EchoPlusJSONResult bgmList() {
        return bgmService.bgmList();
    }
}