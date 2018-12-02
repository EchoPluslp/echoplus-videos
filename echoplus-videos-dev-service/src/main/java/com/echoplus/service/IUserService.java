package com.echoplus.service;

import com.echoplus.pojo.Users;
import com.echoplus.pojo.UsersReport;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 用户Service
 *
 * @author Liupeng
 * @createTime 2018-11-04 16:56
 **/
public interface IUserService {

    boolean queryUsernameIsExist(String userName);

    int saveUser(Users users);

    List<Users> checkUsernameAndPassword(Users users);

    @Transactional(propagation = Propagation.SUPPORTS)
    Integer updateUserInfo(Users users);

    @Transactional(propagation = Propagation.SUPPORTS)
    List<Users> queryUserInfo(String userId);

    @Transactional(propagation = Propagation.SUPPORTS)
    Boolean userLikeVideos(String userId, String videoId);

    void addUserFansRelation(String publisherId, String fansId);


    void removeUserFansRelation(String publisherId, String fansId);

    boolean checkIfFans(String publisherId, String fansId);

    void reportUser(UsersReport usersReport);
}