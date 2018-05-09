package com.bjnlmf.nerc.zhihu.controller;

import com.bjnlmf.nerc.zhihu.annotation.Behavior;
import com.bjnlmf.nerc.zhihu.annotation.BehaviorContent;
import com.bjnlmf.nerc.zhihu.pojo.UserVO;
import com.bjnlmf.nerc.zhihu.pojo.comment.CommentDO;
import com.bjnlmf.nerc.zhihu.pojo.comment.CommentVO;
import com.bjnlmf.nerc.zhihu.service.CommentService;
import com.bjnlmf.nerc.zhihu.service.UserService;
import com.bjnlmf.nerc.zhihu.util.Page;
import com.bjnlmf.nerc.zhihu.util.ResponseJson;
import com.bjnlmf.nerc.zhihu.util.UserStateUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping(value = "comment")
@Api(description = "评论")
public class CommentController {

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    @RequestMapping(value =  "add" ,method = RequestMethod.POST)
    @ApiOperation("添加评论")
    @Behavior(u_act="comment_add")
    public ResponseJson addComment(
            @BehaviorContent(userToken= "userToken")
            @ApiParam(value = "userToken",required = true)
            @RequestHeader String userToken,
            @BehaviorContent(u_ip= "ip")
            @ApiParam(value = "ip")
            @RequestHeader Map<String,String> ip,
            @BehaviorContent(act_obj= "answerID",act_cont = "commentsContent")
            @RequestBody CommentDO commentDO
            ){
        UserVO user = userService.queryByUserToken(userToken);
        UserStateUtil.checkUserState(user);
        commentDO.setUserId(user.getUserID());
        commentService.addComment(commentDO);
        return ResponseJson.ok();
    }


    @RequestMapping(value =  "list" ,method = RequestMethod.POST)
    @ApiOperation("评论列表")
    public ResponseJson<Page<CommentVO>> listComment(
            @ApiParam(value = "userToken")
            @RequestHeader(required = false) String userToken,
            @ApiParam(value = "页码",defaultValue = "1")
            @RequestParam Optional<Integer> pageNumber,
            @ApiParam(value = "页容量",defaultValue = "10")
            @RequestParam Optional<Integer> pageSize,
            @ApiParam(value = "问题ID",required = true)
            @RequestParam Optional<Long> questionID,
            @ApiParam(value = "回答",required = true)
            @RequestParam Optional<Long> answerID,
            @ApiParam(value = "最新（1正序，-1倒叙）")
            @RequestParam Optional<Boolean> dateSort,
            @ApiParam(value = "最热（1正序，-1倒叙）")
            @RequestParam Optional<Boolean> heatSort,
            @ApiParam(value = "是否查询屏蔽的评论")
            @RequestParam Optional<Boolean> shielding
        ){
        CommentDO commentDO = new CommentDO();
        if(!StringUtils.isEmpty(userToken)){
            UserVO userVO = userService.queryByUserToken(userToken);
            commentDO.setUserId(userVO.getUserID());
        }
        pageNumber.ifPresent(commentDO::setPageNumber);
        pageSize.ifPresent(commentDO::setPageSize);
        questionID.ifPresent(commentDO::setQuestionID);
        shielding.ifPresent(commentDO::setShielding);
        answerID.ifPresent(commentDO::setAnswerID);
        dateSort.ifPresent(commentDO::setDateSort);
        heatSort.ifPresent(commentDO::setHeatSort);
        commentDO.setShielding(true);
        Page<CommentVO> page = commentService.listComment(commentDO);
        return ResponseJson.ok(page);
    }

    @RequestMapping(value = "prise",method = RequestMethod.POST)
    @ApiOperation("对评论点赞")
    public ResponseJson priseAnswer(
            @ApiParam(value = "userToken",required = true)
            @RequestHeader String userToken,
            @ApiParam(value = "问题ID",required = true)
            @RequestParam Optional<Long> questionID,
            @ApiParam(value = "回答ID",required = true)
            @RequestParam Optional<Long> answerId,
            @ApiParam(value = "评论",required = true)
            @RequestParam Optional<Long> commentId){

        UserVO userVO = userService.queryByUserToken(userToken);
        UserStateUtil.checkUserState(userVO);
        CommentDO commentDO = new CommentDO();
        commentDO.setUserId(userVO.getUserID());
        questionID.ifPresent(commentDO::setQuestionID);
        answerId.ifPresent(commentDO::setAnswerID);
        commentId.ifPresent(commentDO::setId);
        commentService.priseComment(commentDO);
        return ResponseJson.ok();
    }

    @RequestMapping(value =  "dailog_list" ,method = RequestMethod.GET)
    @ApiOperation("对话列表")
    public ResponseJson<List<CommentVO>> dailogList(
            @ApiParam(value = "评论Id")
            @RequestParam Optional<Long> commentId
        ){
        CommentDO commentDO = new CommentDO();
        commentId.ifPresent(commentDO::setId);
        List<CommentVO> commentVOList = commentService.dailogList(commentDO);
        return ResponseJson.ok(commentVOList);
    }

    @RequestMapping(value = "/comment_list_userId", method = RequestMethod.GET)
    @ApiOperation(value = "查找用户对所有回答的所有评论")
    public ResponseJson<Page<CommentVO>> queryList(
            @ApiParam(value = "userToken",required = true)
            @RequestHeader String userToken,
            @ApiParam(value = "userID")
            @RequestParam Optional<Long> userId,
            @ApiParam(value = "页码",defaultValue = "1")
            @RequestParam Optional<Integer> pageNumber,
            @ApiParam(value = "页容量",defaultValue = "5")
            @RequestParam Optional<Integer> pageSize
    ) {
        UserVO userVO = userService.queryByUserToken(userToken);
        CommentDO commentDO = new CommentDO();
        userId.ifPresent(commentDO::setUserId);
        pageNumber.ifPresent(commentDO::setPageNumber);
        pageSize.ifPresent(commentDO::setPageSize);
        Page<CommentVO> list = commentService.listCommentManage(commentDO);
        return ResponseJson.ok(list);
    }

}
