package com.bjnlmf.nerc.zhihu.controller;

import com.bjnlmf.nerc.zhihu.pojo.UserVO;
import com.bjnlmf.nerc.zhihu.pojo.comment.CommentDO;
import com.bjnlmf.nerc.zhihu.pojo.comment.CommentVO;
import com.bjnlmf.nerc.zhihu.service.CommentService;
import com.bjnlmf.nerc.zhihu.service.UserService;
import com.bjnlmf.nerc.zhihu.util.Page;
import com.bjnlmf.nerc.zhihu.util.ResponseJson;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping(value = "/admin/comment")
@Api(description = "评论")
public class AdminCommentController {

    @Autowired
    private CommentService commentService;
    @Autowired
    private UserService userService;



    @RequestMapping(value =  "shielding" ,method = RequestMethod.GET)
    @ApiOperation("修改评论屏蔽状态")
    public ResponseJson<Integer> shielding(
            @ApiParam(value = "userToken",required = true)
            @RequestHeader String userToken,
            @ApiParam(value = "评论Id")
            @RequestParam Optional<String> ids,
            @ApiParam(value = "是否屏蔽")
            @RequestParam Optional<Boolean> shielding
            ){
        CommentDO commentDO = new CommentDO();
        ids.ifPresent(commentDO::setIds);
        shielding.ifPresent(commentDO::setShielding);
        Integer count = commentService.shielding(commentDO);
        return ResponseJson.ok(count);
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
        Page<CommentVO> page = commentService.adminListComment(commentDO);
        return ResponseJson.ok(page);
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
        Page<CommentVO> list = commentService.adminListCommentManage(commentDO);
        return ResponseJson.ok(list);
    }
}
