package com.echoplus.controller;

import com.echoplus.pojo.Users;
import com.echoplus.service.IUserService;
import com.echoplus.utils.EchoPlusJSONResult;
import com.echoplus.utils.MD5Utils;
import com.echoplus.vo.UsersVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@Api(value = "用户注册登录的接口",tags = {"注册和登录的controller"})
public class RegistLoginController extends RedisBasicController {

	@Autowired
	private IUserService userService;

	@ApiOperation(value = "用户注册的接口", notes = "用户注册")
	@PostMapping("/regist")
	public EchoPlusJSONResult regist(@RequestBody Users users) {
		//判空
		if (StringUtils.isBlank(users.getUsername()) || StringUtils.isBlank(users.getPassword())) {
			return EchoPlusJSONResult.errorMsg("用户名和密码不能为空，请检验");
		}
		//判断用户名是否存在
		boolean userExits = userService.queryUsernameIsExist(users.getUsername());
		if (!userExits) {
			//存在用户名
			return EchoPlusJSONResult.errorMsg("当前用户名已经存在，请重新输入");
		}
		try {
			//当前用户名不存在,保存用户名
			users.setFansCounts(0);
			users.setFollowCounts(0);
			users.setReceiveLikeCounts(0);
			users.setPassword(MD5Utils.getMD5Str(users.getPassword()));
			users.setNickname(users.getUsername());
			userService.saveUser(users);
		} catch (Exception e) {
			e.printStackTrace();
		}
		users.setPassword("");

		UsersVo usersVo = accessUserVo(users);

		return EchoPlusJSONResult.ok(usersVo);
	}

	@ApiOperation(value = "用户登录的接口", notes = "用户登录")
	@PostMapping("/login")
	public EchoPlusJSONResult login(@RequestBody Users users) {
		if (StringUtils.isBlank(users.getUsername()) || StringUtils.isBlank(users.getPassword())) {
			return EchoPlusJSONResult.errorMsg("用户名和密码不能为空");
		}
		List<Users> usersList = userService.checkUsernameAndPassword(users);
		if (usersList.size() == 0) {
			return EchoPlusJSONResult.errorMsg("用户名不存在或者密码错误");
		} else {
			Users userss = usersList.get(0);
			userss.setPassword(null);
			UsersVo usersVo = accessUserVo(userss);
			return EchoPlusJSONResult.ok(usersVo);
		}
	}

	@ApiOperation(value = "用户注销的接口", notes = "用户注销i")
	@ApiImplicitParam(name = "userId",value = "用户Id",required = true,
						dataType = "String",paramType = "query")
	@PostMapping("/logout")
	public EchoPlusJSONResult login(String  userId) {
		redis.del(USER_REDIS_SESSION + ":" + userId);
		return EchoPlusJSONResult.ok("用户注销成功");
	}

	private UsersVo accessUserVo(Users userModel) {
		String uniqueToken = UUID.randomUUID().toString();//生成用户唯一token

		redis.set(USER_REDIS_SESSION + ":" + userModel.getId(), uniqueToken, 1000 * 60 * 30);

		UsersVo usersVo = new UsersVo();
		BeanUtils.copyProperties(userModel, usersVo);
		usersVo.setUserToken(uniqueToken);
		return usersVo;
	}
}
