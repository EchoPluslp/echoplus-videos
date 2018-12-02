package com.echoplus.service.impl;

import com.echoplus.mapper.UsersFansMapper;
import com.echoplus.mapper.UsersLikeVideosMapper;
import com.echoplus.mapper.UsersMapper;
import com.echoplus.mapper.UsersReportMapper;
import com.echoplus.pojo.*;
import com.echoplus.service.IUserService;
import com.echoplus.utils.EchoPlusJSONResult;
import com.echoplus.utils.MD5Utils;
import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @author Liupeng
 * @createTime 2018-11-04 16:57
 **/
@Service
public class UserServiceImpl implements IUserService {


    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private UsersLikeVideosMapper usersLikeVideosMapper;

    @Autowired
    private UsersFansMapper usersFansMapper;


    @Autowired
    private UsersReportMapper usersReportMapper;

    @Autowired
    private Sid sid;

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public boolean queryUsernameIsExist(String userName) {
        UsersExample usersExample = new UsersExample();
        usersExample.createCriteria().andUsernameEqualTo(userName);
        List<Users> users = usersMapper.selectByExample(usersExample);
        return users.size() == 0 ? true : false;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public int saveUser(Users users) {
        //设置用户Id
        users.setId(sid.nextShort());
        return usersMapper.insert(users);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<Users> checkUsernameAndPassword(Users users) {
        UsersExample usersExample = null;
        try {
            usersExample = new UsersExample();
            usersExample.createCriteria().andUsernameEqualTo(
                    users.getUsername()).andPasswordEqualTo(MD5Utils.getMD5Str(users.getPassword()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return usersMapper.selectByExample(usersExample);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Integer updateUserInfo(Users users) {
        UsersExample usersExample = new UsersExample();
        usersExample.createCriteria().andIdEqualTo(users.getId());
        return usersMapper.updateByExampleSelective(users, usersExample);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<Users> queryUserInfo(String userId) {
        UsersExample example = new UsersExample();
        example.createCriteria().andIdEqualTo(userId);
        return usersMapper.selectByExample(example);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Boolean userLikeVideos(String userId, String videoId) {
        if (StringUtils.isBlank(userId)||StringUtils.isBlank(videoId)) {
            return false;
        }
        UsersLikeVideosExample usersLikeVideosExample = new UsersLikeVideosExample();
        usersLikeVideosExample.createCriteria().andVideoIdEqualTo(videoId).andUserIdEqualTo(userId);

        return usersLikeVideosMapper.selectByExample(usersLikeVideosExample).size() > 0;
    }

    @Transactional(propagation = Propagation.REQUIRED) //涉及到修改，所以为Required
    //增加用户和粉丝的关系
    @Override
    public void addUserFansRelation(String publisherId,String fansId) {
        //在userFans表中创建关联关系
        UsersFans usersFans = new UsersFans();
        usersFans.setId(sid.nextShort());
        usersFans.setFanId(fansId);
        usersFans.setUserId(publisherId);
        usersFansMapper.insert(usersFans);

        //在user表中修改对应的属性，增加视频发布者的粉丝数
        usersMapper.addUserFans(publisherId);

        //增加关注数
        usersMapper.addUserFollersFans(fansId);

    }

    //删除用户和粉丝的关系
    @Transactional(propagation = Propagation.REQUIRED) //涉及到修改，所以为Required
    @Override
    public void removeUserFansRelation(String publisherId,String fansId) {
        //删除userfans表中的关系
        UsersFansExample usersFans = new UsersFansExample();
        usersFans.createCriteria().andFanIdEqualTo(fansId).andUserIdEqualTo(publisherId);
        usersFansMapper.deleteByExample(usersFans);

        //减少视频发布者的粉丝数
        usersMapper.reduceUserFans(publisherId);
        //减少用户关注数
        usersMapper.reduceUserFollersFans(fansId);
    }

    //查询是否是粉丝
    @Override
    public boolean checkIfFans(String publisherId, String fansId) {
        UsersFansExample usersFansExample = new UsersFansExample();
        usersFansExample.createCriteria().andFanIdEqualTo(fansId).andUserIdEqualTo(publisherId);

        List<UsersFans> usersFans = usersFansMapper.selectByExample(usersFansExample);

        if (usersFans != null && !usersFans.isEmpty() && usersFans.size() > 0) {
            return true;
        }
        return false;
    }

    @Override
    public void reportUser(UsersReport usersReport) {
        String id = sid.nextShort();
        usersReport.setId(id);
        usersReport.setCreateDate(new Date());
        usersReportMapper.insert(usersReport);
    }
}