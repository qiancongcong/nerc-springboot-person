package com.bjnlmf.nerc.zhihu.controller;

import com.bjnlmf.nerc.zhihu.annotation.Behavior;
import com.bjnlmf.nerc.zhihu.annotation.BehaviorContent;
import com.bjnlmf.nerc.zhihu.pojo.UserVO;
import com.bjnlmf.nerc.zhihu.pojo.answer.AnswerDTO;
import com.bjnlmf.nerc.zhihu.pojo.answer.AnswerVO;
import com.bjnlmf.nerc.zhihu.service.AnswerService;
import com.bjnlmf.nerc.zhihu.service.OperationLogService;
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
@RequestMapping("answer")
@Api(description = "回答")
public class AnswerController {

    @Autowired
    private AnswerService answerService;

    @Autowired
    private UserService userService;

    @Autowired
    private OperationLogService operationLogService;

    @RequestMapping(value = "list",method = RequestMethod.POST)
    @ApiOperation("问题回答列表")
    public ResponseJson<Page> queryAnswerList(
            @ApiParam(value = "userToken")
            @RequestHeader(required = false) String userToken,
            @ApiParam(value = "页码",defaultValue = "1")
            @RequestParam Optional<Integer> pageNumber,
            @ApiParam(value = "页容量",defaultValue = "10")
            @RequestParam Optional<Integer> pageSize,
            @ApiParam(value = "问题Id")
            @RequestParam Optional<Long> questionID,
            @ApiParam(value = "最新（true正序，false倒叙）")
            @RequestParam Optional<Boolean> dateSort){
        AnswerDTO answerDTO = new AnswerDTO();
        if(!StringUtils.isEmpty(userToken)){
            UserVO userVO = userService.queryByUserToken(userToken);
            answerDTO.setUserID(userVO.getUserID());
        }
        pageNumber.ifPresent(answerDTO::setPageNumber);
        pageSize.ifPresent(answerDTO::setPageSize);
        questionID.ifPresent(answerDTO::setQuestionID);
        dateSort.ifPresent(answerDTO::setDateSort);
        Page<AnswerVO> page = answerService.queryAnswerList(answerDTO);
        return ResponseJson.ok(page);
    }

    @RequestMapping(value = "add",method = RequestMethod.POST)
    @ApiOperation("添加问题回答")
    @Behavior(u_act="answer_add")
    public ResponseJson addAnswer(
            @BehaviorContent(userToken= "userToken")
            @ApiParam(value = "userToken",required = true)
            @RequestHeader String userToken,
            @BehaviorContent(u_ip= "ip")
            @ApiParam(value = "ip")
            @RequestHeader Map<String,String> ip,
            @BehaviorContent(act_obj= "questionID",act_cont = "answerContent")
            @RequestBody AnswerDTO answerDTO){
        UserVO userVO = userService.queryByUserToken(userToken);
        UserStateUtil.checkUserState(userVO);
        answerDTO.setUserID(userVO.getUserID());
        answerDTO.setShieldingAnswerContent(answerDTO.getAnswerContent());
        answerService.addAnswer(answerDTO);
        return ResponseJson.ok();
    }

    @RequestMapping(value = "prise",method = RequestMethod.POST)
    @ApiOperation("对回答点赞")
    public ResponseJson priseAnswer(
            @ApiParam(value = "userToken",required = true)
            @RequestHeader String userToken,
            @ApiParam(value = "问题ID",required = true)
            @RequestParam Optional<Long> questionID,
            @ApiParam(value = "回答ID",required = true)
            @RequestParam Optional<Long> answerId){

        UserVO userVO = userService.queryByUserToken(userToken);
        UserStateUtil.checkUserState(userVO);
        AnswerDTO answerDTO = new AnswerDTO();
        answerDTO.setUserID(userVO.getUserID());
        questionID.ifPresent(answerDTO::setQuestionID);
        answerId.ifPresent(answerDTO::setId);
        answerService.priseAnswer(answerDTO);
        return ResponseJson.ok();
    }

    @RequestMapping(value = "user_king",method = RequestMethod.GET)
    @ApiOperation("答人排行榜")
    public ResponseJson<List<UserVO>> userKing(){
        List<UserVO> userVOList = answerService.queryUserKing();
        return ResponseJson.ok(userVOList);
    }

    @RequestMapping(value = "count",method = RequestMethod.GET)
    @ApiOperation("用户回答数")
    public ResponseJson<Integer> userKing(
            @ApiParam(value = "userToken",required = true)
            @RequestHeader String userToken){
        UserVO userVO = userService.queryByUserToken(userToken);
        Integer count =  answerService.countByUserId(userVO.getUserID());
        return ResponseJson.ok(count);
    }

    @RequestMapping(value = "user_list",method = RequestMethod.POST)
    @ApiOperation("查询用户的回答列表")
    public ResponseJson<Page<AnswerVO>> userAnswerList(
            @ApiParam(value = "userToken",required = true)
            @RequestHeader String userToken,
            @ApiParam(value = "页码",defaultValue = "1")
            @RequestParam Optional<Integer> pageNumber,
            @ApiParam(value = "页容量",defaultValue = "10")
            @RequestParam Optional<Integer> pageSize){

        UserVO userVO = userService.queryByUserToken(userToken);
        AnswerDTO answerDTO =new AnswerDTO();
        pageNumber.ifPresent(answerDTO::setPageNumber);
        pageSize.ifPresent(answerDTO::setPageSize);
        answerDTO.setUserID(userVO.getUserID());
        Page<AnswerVO> page = answerService.queryUserAnswerList(answerDTO);
        return ResponseJson.ok(page);
    }

    @RequestMapping(value = "user_list_as_id",method = RequestMethod.POST)
    @ApiOperation("查询用户的回答列表")
    public ResponseJson<Page<AnswerVO>> otherAnswerList(
            @ApiParam(value = "userId",required = true)
            @RequestParam Long userId,
            @ApiParam(value = "页码",defaultValue = "1")
            @RequestParam Optional<Integer> pageNumber,
            @ApiParam(value = "页容量",defaultValue = "10")
            @RequestParam Optional<Integer> pageSize){

        AnswerDTO answerDTO =new AnswerDTO();
        pageNumber.ifPresent(answerDTO::setPageNumber);
        pageSize.ifPresent(answerDTO::setPageSize);
        answerDTO.setUserID(userId);
        Page<AnswerVO> page = answerService.queryUserAnswerList(answerDTO);
        return ResponseJson.ok(page);
    }

    @RequestMapping(value = "by_id",method = RequestMethod.GET)
    @ApiOperation("查询某个回答")
    public ResponseJson<AnswerVO> queryById(
            @ApiParam(value = "回答id",required = true)
            @RequestParam Long answerId){
        AnswerVO answerVO = answerService.queryByAnswerId(answerId);
        return ResponseJson.ok(answerVO);
    }

}
