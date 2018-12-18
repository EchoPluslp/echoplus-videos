package com.echoplus;

import com.alibaba.druid.util.StringUtils;
import com.echoplus.config.ResourceConfig;
import com.echoplus.utils.JsonUtils;
import org.apache.commons.io.FileUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

/**
 * @author Liupeng
 * @createTime 2018-12-11 23:41
 **/
@Component
public class ZKCuratorClient {
    /**
     * 	zk客户端
     */
    private CuratorFramework client;
    final static Logger log = LoggerFactory.getLogger(ZKCuratorClient.class);
//    public static final String ZOOKEEPER = "132.232.244.172:2181";

    //统一资源路径设置
    @Autowired
    private ResourceConfig resourceConfig;


    public void init() {
        if (client == null) {

            log.info("zk初始化中>。。。。");
            ExponentialBackoffRetry retry = new ExponentialBackoffRetry(1000,5);

            //创建客户端
            client = CuratorFrameworkFactory.builder().connectString(resourceConfig.getZookeeperServer())
                    .sessionTimeoutMs(1000).connectionTimeoutMs(1000).namespace("admin").retryPolicy(retry).build();
            //启动客户端
            client.start();

            try {
                addChildWatch("/bgm");
            } catch (Exception e) {
                e.printStackTrace();
                log.info("zk初始化异常。。。。");
            }
            log.info("zk初始化完成。。。。");

        }
    }
    public void addChildWatch(String nodePath) throws Exception {
        final PathChildrenCache pathChildrenCache = new PathChildrenCache(client,nodePath, true);
        pathChildrenCache.start();
        //-->获取监听列表--->添加监听器-->设置监听的情况
        pathChildrenCache.getListenable().addListener((client, event) -> {
            //设置监听事件
            if (event != null && event.getType().equals(PathChildrenCacheEvent.Type.CHILD_ADDED)) {
                //设置总路径
                log.info("监听到zk节点改变事件。。。。");
                String path = event.getData().getPath();
                String totalPath = new String(event.getData().getData());
                Map<String,String> map = JsonUtils.jsonToPojo(totalPath, Map.class);

                //代码迭代后，路径由后端直接传过来
                //避免当使用分布式系统时， springboot端查询bgmPath出现null的情况
                String songPath = map.get("path");
                String operatorType = map.get("operType");

                //获取操作数0添加还是删除
//                String operator = new String(event.getData().getData());
                //获取bgm路径
//                String[] split = totalPath.split("/");
//                String bgmId = split[split.length - 1];
                //从数据库中查询bgm对象，获取数据库存储的相对路径
//                Bgm bgm = bgmService.queryBgmById(bgmId);
//
//                //2. 定义保存到本地的路径
//                String songPath = bgm.getPath();

//                String filePath = "E:\\Wx\\echoplus-video-dev\\bgm" + songPath;
                String filePath = resourceConfig.getFileSpace() + songPath;

                StringBuilder stringBuilder = new StringBuilder(filePath);
                //3.定义下载的路径 播放路径
//                设置浏览器上面的播放路径-->将\\转换为/
                //注意设置路径的编码格式，避免出现乱码问题
                String forxPath = URLEncoder.encode(stringBuilder.toString().replaceAll("\\\\","/"),"UTF-8");

//                String urlPath = "http://127.0.0.1:8080/mvc" + forxPath;
                String urlPath = resourceConfig.getBgmServer()+ forxPath;

                String operaTypeADD = "1";

                //通过operatorType判断是添加或者删除
                if (StringUtils.equals(operatorType,operaTypeADD)) {
                    //下载bgm到spring boot服务器
                    URL url = new URL(urlPath);
                    File file = new File(filePath);
                    FileUtils.copyURLToFile(url, file);
                    //删除节点上的数据
                    client.delete().forPath(path);
                }else{
                    File file = new File(filePath);
                    FileUtils.forceDeleteOnExit(file);
                    client.delete().forPath(path);
                }
                log.info("监听到zk节点完成。。。。");
            }
        });
    }

}