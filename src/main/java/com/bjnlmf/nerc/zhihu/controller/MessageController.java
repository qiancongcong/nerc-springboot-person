package com.bjnlmf.nerc.zhihu.controller;

import com.bjnlmf.nerc.zhihu.enumeration.MessageType;
import com.bjnlmf.nerc.zhihu.pojo.UserVO;
import com.bjnlmf.nerc.zhihu.pojo.message.MessageDO;
import com.bjnlmf.nerc.zhihu.pojo.message.MessageQuery;
import com.bjnlmf.nerc.zhihu.pojo.message.MessageVO;
import com.bjnlmf.nerc.zhihu.service.MessageService;
import com.bjnlmf.nerc.zhihu.service.UserService;
import com.bjnlmf.nerc.zhihu.util.Page;
import com.bjnlmf.nerc.zhihu.util.ResponseJson;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("message")
@Api(description = "系统消息")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/save",method = RequestMethod.POST)
    @ApiOperation("生成消息")
    public ResponseJson saveMessage(
            @ApiParam(value = "用户id")
            @RequestParam Optional<Long> userID,
            @ApiParam(value = "消息种类")
            @RequestParam Optional<MessageType> species,
            @ApiParam(value = "关联问题或回答的消息主题(回答:问题头,评论:回答内容,回复:评论内容,采纳:回答内容)")
            @RequestParam Optional<String> affiliatedQuestion,
            @ApiParam(value = "关联人id")
            @RequestParam Optional<Long> affiliatedID,
            @ApiParam(value = "关联问题ID")
            @RequestParam Optional<Long> questionId,
            @ApiParam(value = "关联回答的ID")
            @RequestParam Optional<Long> answerId){
        MessageDO messageDO = new MessageDO();
        userID.ifPresent(messageDO::setUserID);
        species.ifPresent(messageDO::setSpecies);
        affiliatedQuestion.ifPresent(messageDO::setAffiliatedQuestion);
        affiliatedID.ifPresent(messageDO::setAffiliatedID);
        questionId.ifPresent(messageDO::setQuestionId);
        answerId.ifPresent(messageDO::setAnswerId);
        return ResponseJson.ok(messageService.save(messageDO));
    }

    @RequestMapping(method = RequestMethod.GET)
    @ApiOperation("查询用户消息列表")
    public ResponseJson<Page<MessageVO>> queryMessage(
            @ApiParam(value = "userToken")
            @RequestHeader Optional<String> userToken,
            @ApiParam(value = "当前页",defaultValue = "1")
            @RequestParam Optional<Integer> pageNumber,
            @ApiParam(value = "页面容量",defaultValue = "10")
            @RequestParam Optional<Integer> pageSize){
        MessageQuery messageQuery = new MessageQuery();
        UserVO user = userService.queryByUserToken(userToken.get());
        messageQuery.setUserID(user.getUserID());
        pageNumber.ifPresent(messageQuery::setPageNumber);
        pageSize.ifPresent(messageQuery::setPageSize);
        messageQuery.setRowStart();
        return ResponseJson.ok(messageService.queryMessageAsUser(messageQuery));
    }

    @RequestMapping(value = "/count",method = RequestMethod.GET)
    @ApiOperation("查询消息总数")
    public ResponseJson<Integer> queryMessageCount(
            @ApiParam(value = "userToken")
            @RequestHeader Optional<String> userToken){
        UserVO user = userService.queryByUserToken(userToken.get());
        return ResponseJson.ok(messageService.queryMessageCount(user.getUserID()));
    }

    @RequestMapping(value = "/unread_count",method = RequestMethod.GET)
    @ApiOperation("查询未读消息数")
    public ResponseJson<Integer> queryUnreadMessageCount(
            @ApiParam(value = "userToken")
            @RequestHeader Optional<String> userToken){
        UserVO user = userService.queryByUserToken(userToken.get());
        return ResponseJson.ok(messageService.queryUnreadMessageCount(user.getUserID()));
    }

    @RequestMapping(value = "/update_unread",method = RequestMethod.GET)
    @ApiOperation("更新未读消息为已读")
    public ResponseJson updateUnread(
            @ApiParam(value = "userToken")
            @RequestHeader Optional<String> userToken,
            @ApiParam(value = "消息ID")
            @RequestParam Optional<Long> messageId){
        UserVO user = userService.queryByUserToken(userToken.get());
        return ResponseJson.ok(messageService.updateUnreadMessage(user.getUserID(),messageId.get()));
    }
}
