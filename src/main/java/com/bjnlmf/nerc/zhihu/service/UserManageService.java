package com.bjnlmf.nerc.zhihu.service;

import com.bjnlmf.nerc.zhihu.mapper.AnswerMapper;
import com.bjnlmf.nerc.zhihu.mapper.QuestionMapper;
import com.bjnlmf.nerc.zhihu.mapper.UserManageMapper;
import com.bjnlmf.nerc.zhihu.pojo.UserManageQuery;
import com.bjnlmf.nerc.zhihu.pojo.UserVO;
import com.bjnlmf.nerc.zhihu.util.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.List;

@Service
public class UserManageService {

    @Autowired
    UserManageMapper userManageMapper;
    @Autowired
    QuestionMapper questionMapper;
    @Autowired
    AnswerMapper answerMapper;

    public Page<UserVO> queryUserManageList(UserManageQuery userManageQuery) {
        Page<UserVO> page = new Page<UserVO>(userManageQuery.getPageNumber(), userManageQuery.getPageSize());
        if(null != userManageQuery.getNickName()){
            userManageQuery.setNickName("%"+ userManageQuery.getNickName() +"%");
        }
        List<UserVO> list = userManageMapper.queryList(userManageQuery);
        if(null != list && 0 < list.size()){
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            for (UserVO userVO : list){
                Long id = userVO.getUserID();
                Integer questionCount = questionMapper.queryQuestionCountByUserID(id);
                userVO.setQuestionCount(questionCount);
                Integer answerCount = answerMapper.countByUserId(id);
                userVO.setAnswerCount(answerCount);
                userVO.setCreateTimeStr(simpleDateFormat.format(userVO.getCreateTime()));
            }
        }
        page.setData(list);
        page.setEntityTotal(userManageMapper.queryListCount(userManageQuery));
        return page;
    }

    public void updateState(UserManageQuery userManageQuery){
        userManageMapper.updateState(userManageQuery);
    }

}
