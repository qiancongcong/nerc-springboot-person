package com.bjnlmf.nerc.zhihu.controller;

import com.bjnlmf.nerc.zhihu.enumeration.OperationType;
import com.bjnlmf.nerc.zhihu.pojo.UserVO;
import com.bjnlmf.nerc.zhihu.pojo.answer.AnswerDTO;
import com.bjnlmf.nerc.zhihu.pojo.answer.AnswerVO;
import com.bjnlmf.nerc.zhihu.pojo.operation.OperationLogDO;
import com.bjnlmf.nerc.zhihu.service.AnswerService;
import com.bjnlmf.nerc.zhihu.service.OperationLogService;
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
@RequestMapping("/admin/answer")
@Api(description = "管理员管理回答列表")
public class AdminAnswerController {

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

            @ApiParam(value = "开始时间")
            @RequestParam Optional<String> startDate,

            @ApiParam(value = "结束时间")
            @RequestParam Optional<String> endDate,

            @ApiParam(value = "回答状态")
            @RequestParam Optional<Integer> answerState,

            @ApiParam(value = "根据用户账号搜索")
            @RequestParam Optional<String> phone,
            @ApiParam(value = "根据问题题目搜索关键字")
            @RequestParam Optional<String> keyword){

        AnswerDTO answerDTO = new AnswerDTO();
        if(!StringUtils.isEmpty(userToken)){
           /* UserVO userVO = userService.queryByUserToken(userToken);
            answerDTO.setUserID(userVO.getUserID());*/
        }
        pageNumber.ifPresent(answerDTO::setPageNumber);
        pageSize.ifPresent(answerDTO::setPageSize);
        questionID.ifPresent(answerDTO::setQuestionID);
        startDate.ifPresent(answerDTO::setStartDate);
        endDate.ifPresent(answerDTO::setEndDate);
        answerState.ifPresent(answerDTO::setAnswerState);
        keyword.ifPresent(answerDTO::setKeyword);
        phone.ifPresent(answerDTO::setPhone);

        Page<AnswerVO> page = answerService.queryAdminAnswerList(answerDTO);
        return ResponseJson.ok(page);
    }


    @RequestMapping(value = "shielding",method = RequestMethod.GET)
    @ApiOperation("修改屏蔽回答内容和状态")
    public ResponseJson<Integer> shielding(
            @ApiParam(value = "userToken",required = true)
            @RequestHeader String userToken,
            @ApiParam(value = "回答Id")
            @RequestParam Optional<Long> id,
            @ApiParam(value = "是否屏蔽")
            @RequestParam Optional<Boolean> shielding,
            @ApiParam(value = "屏蔽后的内容")
            @RequestParam Optional<String> answerContent){
        UserVO user = userService.queryByUserToken(userToken);
        AnswerVO answerVO = new AnswerVO();
        id.ifPresent(answerVO::setId);
        shielding.ifPresent(answerVO::setShielding);
        answerContent.ifPresent(answerVO::setAnswerContent);
        OperationLogDO operationLogDO = new OperationLogDO();
        operationLogDO.setUserId(user.getUserID());
        operationLogDO.setRelationId(id.get());
        operationLogDO.setOperationType(OperationType.questions);
        operationLogService.save(operationLogDO);
        return ResponseJson.ok(answerService.shielding(answerVO));
    }

    @RequestMapping(value = "/delete",method = RequestMethod.POST)
    @ApiOperation("删除回答")
    public ResponseJson<Integer> deleteQuestionById(
            @ApiParam(value = "用户userToken",required = true)
            @RequestHeader String userToken,
            @ApiParam(value = "回答ID",required = true)
            @RequestParam Optional<String> answerID){
        UserVO user = userService.queryByUserToken(userToken);
        AnswerVO answerVO = new AnswerVO();
        answerID.ifPresent(answerVO::setIds);
        Integer count = answerService.deleteQuestionById(answerVO);
        String [] id = answerID.get().split(",");
        for(int i=0;i<id.length;i++){
            OperationLogDO operationLogDO = new OperationLogDO();
            operationLogDO.setUserId(user.getUserID());
            operationLogDO.setRelationId(Long.parseLong(id[i]));
            operationLogDO.setOperationType(OperationType.answer);
            operationLogService.save(operationLogDO);
        }
        return ResponseJson.ok(count);
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
        Page<AnswerVO> page = answerService.adminQueryUserAnswerList(answerDTO);
        return ResponseJson.ok(page);
    }
}
