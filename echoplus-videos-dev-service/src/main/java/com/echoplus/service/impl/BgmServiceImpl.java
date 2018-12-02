package com.echoplus.service.impl;

import com.echoplus.mapper.BgmMapper;
import com.echoplus.pojo.Bgm;
import com.echoplus.pojo.BgmExample;
import com.echoplus.service.IBgmService;
import com.echoplus.utils.EchoPlusJSONResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Liupeng
 * @createTime 2018-11-08 22:20
 **/
@Service
public class BgmServiceImpl implements IBgmService {

    @Autowired
    private BgmMapper bgmMapper;

    @Transactional(propagation =  Propagation.SUPPORTS) //声明式事务
    @Override
    public EchoPlusJSONResult bgmList() {
        BgmExample bgmExample = new BgmExample();
        return EchoPlusJSONResult.ok(bgmMapper.selectByExample(bgmExample));
    }

    @Transactional(propagation =  Propagation.SUPPORTS) //声明式事务
    @Override
    public Bgm queryBgmById(String bgmId) {
        return bgmMapper.selectByPrimaryKey(bgmId);
    }
}