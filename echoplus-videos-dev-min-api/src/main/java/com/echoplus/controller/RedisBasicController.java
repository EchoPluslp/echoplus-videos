package com.echoplus.controller;

import com.echoplus.utils.RedisOperator;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class RedisBasicController {

	@Resource
	protected RedisOperator redis;

	public static final String USER_REDIS_SESSION = "user-redis-session";


	//文件保存目录
	public static final String FILE_STACE = "D:/Wx/echoplus-video-dev/";

	//ffmpeg所在目录
	public static final String FFMPEG_EXE = "D:\\Wx\\ffempg\\bin\\ffmpeg.exe";

	public static final Integer PAGE_SIZE = 5;
}
