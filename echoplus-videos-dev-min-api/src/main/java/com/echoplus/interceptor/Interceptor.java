package com.echoplus.interceptor;


import com.echoplus.utils.EchoPlusJSONResult;
import com.echoplus.utils.JsonUtils;
import com.echoplus.utils.RedisOperator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

/**
 * 拦截器
 *
 * @author Liupeng
 * @create 2018-11-23 0:05
 **/
//需要在WebMvcConfig中注册该拦截器，并配置需要拦截的路径
public class Interceptor  implements HandlerInterceptor{

    @Autowired
    public RedisOperator redis;

    public static final String USER_REDIS_SESSION = "user-redis-session";

    /*
         在Controller拦截之前的代码
           //false 请求被拦截，返回
        //true 请求不被拦截，可以继续执行.
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String userId = request.getHeader("headerUserId");
        String userToken = request.getHeader("headerUserToken");

        if (StringUtils.isNotBlank(userId) && StringUtils.isNotBlank(userToken)) {
            String uniqueToken = redis.get(USER_REDIS_SESSION + ":" + userId);

            //没有token信息
            if (StringUtils.isEmpty(uniqueToken) && StringUtils.isBlank(uniqueToken)) {
                returnErrorResponse(response, new EchoPlusJSONResult().errorTokenMsg("请登录账号..."));
                return false;
            } else {
                if (!StringUtils.equals(uniqueToken, userToken)) {
                    returnErrorResponse(response, new EchoPlusJSONResult().errorTokenMsg("账号被挤掉..."));
                    return false;
                }
            }
        } else {
            //未登录
            returnErrorResponse(response, new EchoPlusJSONResult().errorTokenMsg("请登录账号..."));
            return false;
        }
        //false 请求被拦截，返回
        //true 请求不被拦截，可以继续执行.
        return true;
    }

    public void returnErrorResponse(HttpServletResponse response, EchoPlusJSONResult result)
            throws IOException {
        OutputStream out=null;
        try{
            response.setCharacterEncoding("utf-8");
            response.setContentType("text/json");
            out = response.getOutputStream();
            out.write(JsonUtils.objectToJson(result).getBytes("utf-8"));
            out.flush();
        } finally{
            if(out!=null){
                out.close();
            }
        }
    }
    /*
    在Controller之后，视图渲染之前处理的代码
    * */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    /*
        在视图渲染之后的代码
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
