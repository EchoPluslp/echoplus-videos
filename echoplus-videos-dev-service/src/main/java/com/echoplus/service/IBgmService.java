package com.echoplus.service;

import com.echoplus.pojo.Bgm;
import com.echoplus.utils.EchoPlusJSONResult;

/**
 * Bgm管理
 *
 * @author Liupeng
 * @create 2018-11-08 22:19
 **/
public interface IBgmService {
    EchoPlusJSONResult bgmList();

    Bgm queryBgmById(String bgmId);

}
