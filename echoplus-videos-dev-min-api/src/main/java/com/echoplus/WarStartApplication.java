package com.echoplus;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

/**
 * 打包成war包的设置
 * 继承SpringBootServletInitializer就是，相当于使用web.xml文件的形式去启动部署
 * @author Liupeng
 * @createTime 2018-12-18 23:42
 **/
public class WarStartApplication extends SpringBootServletInitializer {

    /**
     * 重写配置
     * @param builder
     * @return
     */
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        //使用web.xml运行引用程序，并执行Application，最后启动springboot

        return builder.sources(Application.class);
    }
}