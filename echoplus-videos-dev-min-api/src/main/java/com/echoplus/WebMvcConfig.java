package com.echoplus;


import com.echoplus.interceptor.Interceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

/**
 * 设置 tomcat虚拟目录，用于展示照片
 *
 * 使用改方式，可以完成图片的网页展示
 * @author Liupeng
 * @createTime 2018-11-06 23:08
 **/
@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                    .addResourceLocations("classpath:/META-INF/resources/")
                        .addResourceLocations("file:C:/Wx/echoplus-video-dev/");
    }


    /**
     * 使得ZKCuratorClient新建zookeeper，并运行
     * @return
     */
    @Bean(initMethod = "init")
    public ZKCuratorClient zkCuratorClient() {
        return new ZKCuratorClient();
    }

    @Bean
    public Interceptor getInterceptor() {
        return new Interceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //registry.拦截中心   拦截user下的所有路径
        registry.addInterceptor(getInterceptor())
                .addPathPatterns("/user/**")
                .addPathPatterns("/bgm/**")
                .addPathPatterns("/video/upload","/video/uploadCover","/video/userLike","/video/userUnLike"
                ,"/video/saveComments")
                .excludePathPatterns("/user/query/publisher");
        super.addInterceptors(registry);
    }
}