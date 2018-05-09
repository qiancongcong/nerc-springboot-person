package com.bjnlmf.nerc.zhihu.service;

import com.bjnlmf.nerc.zhihu.mapper.BehaviorMapper;
import com.bjnlmf.nerc.zhihu.pojo.BehaviorDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BehaviorService {

    @Autowired
    private BehaviorMapper behaviorMapper;

    public void insert(BehaviorDO behaviorDO){
        behaviorMapper.insert(behaviorDO);
    }

    public List<BehaviorDO> select(Long userID, String answerIds) {
        List<BehaviorDO> list = behaviorMapper.selectlist(userID,answerIds);
        return  list;
    }

    public BehaviorDO queryByAnswerIdAndUserId(BehaviorDO behaviorDO) {
        return behaviorMapper.queryByAnswerIdAndUserId(behaviorDO);
    }

    public BehaviorDO queryByCommentIdAndUserId(BehaviorDO behaviorDO) {
        return behaviorMapper.queryByCommentIdAndUserId(behaviorDO);
    }
}
