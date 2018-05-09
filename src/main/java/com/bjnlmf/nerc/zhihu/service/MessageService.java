package com.bjnlmf.nerc.zhihu.service;

import com.bjnlmf.nerc.zhihu.enumeration.MessageType;
import com.bjnlmf.nerc.zhihu.mapper.MessageMapper;
import com.bjnlmf.nerc.zhihu.pojo.UserVO;
import com.bjnlmf.nerc.zhihu.pojo.message.MessageDO;
import com.bjnlmf.nerc.zhihu.pojo.message.MessageQuery;
import com.bjnlmf.nerc.zhihu.pojo.message.MessageVO;
import com.bjnlmf.nerc.zhihu.pojo.question.QuestionVO;
import com.bjnlmf.nerc.zhihu.util.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MessageService {

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private QuestionService questionService;

    public int save(MessageDO messageDO){
        return messageMapper.save(messageDO);
    }

    public Page<MessageVO> queryMessageAsUser(MessageQuery messageQuery) {
        Page<MessageVO> page = new Page<>(messageQuery.getPageNumber(), messageQuery.getPageSize());
        Integer count = messageMapper.queryMessageCountAsUser(messageQuery);
        page.setEntityTotal(count);
        List<MessageVO> messageVOList = new ArrayList<>();
        if (count != null && count > 0){
            List<MessageVO> list = messageMapper.queryMessageListAsUser(messageQuery);
            for (MessageVO messageVO : list) {
                UserVO user = userService.query(messageVO.getAffiliatedID());
                messageVO.setNickName(user.getNickName());
                String species = messageVO.getSpecies();
                if (MessageType.answer.name().equals(species)){
                    messageVO.setStateContent("回答了您的问题");
                    QuestionVO questionVO = questionService.queryById(messageVO.getQuestionId());
                    messageVO.setAffiliatedQuestion(questionVO.getQuestionTitle());
                }else if (MessageType.comment.name().equals(species)){
                    messageVO.setStateContent("评论了您的答案");
                }else if (MessageType.reply.name().equals(species)){
                    messageVO.setStateContent("回复了您的评论");
                }else if (MessageType.adopt.name().equals(species)){
                    messageVO.setStateContent("采纳了您的答案");
                }
                messageVOList.add(messageVO);
            }
        }
        page.setData(messageVOList);
        return page;
    }

    public Integer queryUnreadMessageCount(Long userID) {
        return messageMapper.queryUnreadMessageCount(userID);
    }

    public Integer updateUnreadMessage(Long userID,Long messageId) {
         return messageMapper.updateUnreadMessage(userID,messageId);
    }

    public Integer queryMessageCount(Long userID) {
        return messageMapper.queryMessageCount(userID);
    }
}
