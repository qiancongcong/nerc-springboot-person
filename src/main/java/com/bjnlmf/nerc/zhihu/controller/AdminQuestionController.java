package com.bjnlmf.nerc.zhihu.controller;

import com.bjnlmf.nerc.zhihu.enumeration.OperationType;
import com.bjnlmf.nerc.zhihu.pojo.UserVO;
import com.bjnlmf.nerc.zhihu.pojo.image.ImageDO;
import com.bjnlmf.nerc.zhihu.pojo.operation.OperationLogDO;
import com.bjnlmf.nerc.zhihu.pojo.question.QuestionQuery;
import com.bjnlmf.nerc.zhihu.pojo.question.QuestionTypeVO;
import com.bjnlmf.nerc.zhihu.pojo.question.QuestionVO;
import com.bjnlmf.nerc.zhihu.service.ImageService;
import com.bjnlmf.nerc.zhihu.service.OperationLogService;
import com.bjnlmf.nerc.zhihu.service.QuestionService;
import com.bjnlmf.nerc.zhihu.service.UserService;
import com.bjnlmf.nerc.zhihu.util.Page;
import com.bjnlmf.nerc.zhihu.util.ResponseJson;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/admin/question")
@Api(description = "管理员管理提问")
public class AdminQuestionController {

    @Autowired
    private QuestionService questionService;


    @Autowired
    private UserService userService;

    @Autowired
    private OperationLogService operationLogService;

    @Autowired
    ImageService imageService;

    @RequestMapping(value = "/parent_type",method = RequestMethod.GET)
    @ApiOperation("获取问题大栏目")
    public ResponseJson<List<QuestionTypeVO>> queryParentType(){
        return ResponseJson.ok(questionService.queryParentType());
    }

    @RequestMapping(value = "/type",method = RequestMethod.GET)
    @ApiOperation("根据大栏目Id获取分类")
    public ResponseJson<List<QuestionTypeVO>> queryTypeByParentId(
            @ApiParam(value = "大栏目ID",required = true)
            @RequestParam Optional<Long> id){
        return ResponseJson.ok(questionService.ueryTypeByParentId(id.get()));
    }

    /**
     * 管理员查询所有提问数据
     *
     * @param pageNumber
     * @param pageSize
     * @param answerState
     * @param questionTypeId
     * @param keyword
     * @param startDate
     * @param endDate
     * @param weatherThumb
     * @return
     */
    @RequestMapping(value = "/list",method = RequestMethod.GET)
    @ApiOperation("首页问题列表展示")
    public ResponseJson<Page<QuestionVO>> getNewHot(
            @ApiParam(value = "用户userToken")
            @RequestHeader Optional<String> userToken,
            @ApiParam(value = "当前页",defaultValue = "1")
            @RequestParam Optional<Integer> pageNumber,
            @ApiParam(value = "页面容量",defaultValue = "10")
            @RequestParam Optional<Integer> pageSize,
            @ApiParam(value = "问题状态(1:已回答,0:未回答,2:已删除)")
            @RequestParam Optional<Integer> answerState,
            @ApiParam(value = "问题类型ID")
            @RequestParam Optional<Long> questionTypeId,
            @ApiParam(value = "父类型ID")
            @RequestParam Optional<Long> parentId,
            @ApiParam(value = "根据用户账号,问题题目搜索关键字")
            @RequestParam Optional<String> keyword,
            @ApiParam(value = "开始时间")
            @RequestParam Optional<String> startDate,
            @ApiParam(value = "结束时间")
            @RequestParam Optional<String> endDate,
            @ApiParam(value = "是否有图片true:有图片,flase:没有图片")
            @RequestParam Optional<Boolean> weatherThumb,
            @ApiParam(value = "提问账号")
            @RequestParam Optional<String> phone){

        QuestionQuery questionQuery = new QuestionQuery();
        pageNumber.ifPresent(questionQuery::setPageNumber);
        pageSize.ifPresent(questionQuery::setPageSize);
        parentId.ifPresent(questionQuery::setParentId);
        questionQuery.setRowStart();
        if (null == questionQuery.getSortField() && null == questionQuery.getSortOrder()){
            questionQuery.setSortField("createTime");
            questionQuery.setSortOrder(0);
        }
        answerState.ifPresent(questionQuery::setAnswerState);
        questionTypeId.ifPresent(questionQuery::setQuestionTypeId);
        keyword.ifPresent(questionQuery::setKeyword);
        startDate.ifPresent(questionQuery::setStartDate);
        endDate.ifPresent(questionQuery::setEndDate);
        weatherThumb.ifPresent(questionQuery::setWeatherThumb);
        phone.ifPresent(questionQuery::setPhone);
        Page<QuestionVO>  QuestionVOList = questionService.queryAdminQuestionList(questionQuery);
        return ResponseJson.ok(QuestionVOList);
    }


    @RequestMapping(value = "/update_shielding",method = RequestMethod.GET)
    @ApiOperation("修改问题屏蔽内容和状态")
    public ResponseJson<Integer> updateShielding(
            @ApiParam(value = "用户userToken",required = true)
            @RequestHeader String userToken,
            @ApiParam(value = "问题ID",required = true)
            @RequestParam Optional<Long> questionID,
            @ApiParam(value = "是否屏蔽标题")
            @RequestParam Optional<Boolean> shieldingTitle,
            @ApiParam(value = "屏蔽后的标题")
            @RequestParam Optional<String> questionTitle,
            @ApiParam(value = "是否屏蔽内容")
            @RequestParam Optional<Boolean> shieldingContent,
            @ApiParam(value = "屏蔽后的内容")
            @RequestParam Optional<String> questionContent,
            @ApiParam(value = "图片ID", required = true)
            @RequestParam Optional<String> imgIds,
            @ApiParam(value = "是否屏蔽图片",required = true)
            @RequestParam Optional<Boolean>shielding){
        UserVO user = userService.queryByUserToken(userToken);
        QuestionVO questionVO = new QuestionVO();
        questionID.ifPresent(questionVO::setId);
        shieldingTitle.ifPresent(questionVO::setShieldingTitle);
        questionTitle.ifPresent(questionVO::setQuestionTitle);
        shieldingContent.ifPresent(questionVO::setShieldingContent);
        questionContent.ifPresent(questionVO::setQuestionContent);
        OperationLogDO operationLogDO = new OperationLogDO();
        operationLogDO.setUserId(user.getUserID());
        operationLogDO.setRelationId(questionID.get());
        operationLogDO.setOperationType(OperationType.questions);
        operationLogService.save(operationLogDO);

        Integer count= 0;
        ImageDO imageDO = new ImageDO();
        questionID.ifPresent(imageDO::setRelationId);
        imageDO.setShielding(false);
        questionID.ifPresent(imageDO::setRelationId);
        count=imageService.updateShielding(imageDO);
        if(imgIds.isPresent()){
            shielding.ifPresent(imageDO::setShielding);
            imgIds.ifPresent(imageDO::setIds);
            count=imageService.shielding(imageDO);
        }


        if(shieldingTitle.isPresent() || questionTitle.isPresent() || shieldingContent.isPresent() || questionContent.isPresent()){
            count = questionService.updateShielding(questionVO);
        }
        return ResponseJson.ok(count);
    }

    @RequestMapping(value = "/delete",method = RequestMethod.POST)
    @ApiOperation("删除提问")
    public ResponseJson<Integer> deleteQuestionById(
            @ApiParam(value = "用户userToken",required = true)
            @RequestHeader String userToken,
            @ApiParam(value = "问题ID",required = true)
            @RequestParam Optional<String> questionID){
        UserVO user = userService.queryByUserToken(userToken);
        QuestionVO questionVO = new QuestionVO();
        questionID.ifPresent(questionVO::setIds);
           Integer count = questionService.deleteQuestionById(questionVO);
           String [] id = questionID.get().split(",");
           for(int i=0;i<id.length;i++){
               OperationLogDO operationLogDO = new OperationLogDO();
               operationLogDO.setUserId(user.getUserID());
               operationLogDO.setRelationId(Long.parseLong(id[i]));
               operationLogDO.setOperationType(OperationType.questions);
               operationLogService.save(operationLogDO);
           }
            return ResponseJson.ok(count);
    }

    @RequestMapping(value = "/details",method = RequestMethod.GET)
    @ApiOperation("查询问题详情")
    public ResponseJson<QuestionVO> questionDetails(
            @ApiParam(value = "问题ID")
            @RequestParam Optional<Long> questionId){
        QuestionVO questionVO = questionService.adminQueryQuestionDetails(questionId.get());
        return ResponseJson.ok(questionVO);
    }


    @RequestMapping(value = "/list_as_user_id",method = RequestMethod.GET)
    @ApiOperation("查询个人问题列表")
    public ResponseJson<Page<QuestionVO>> queryTitleByUserID(
            @ApiParam(value = "UserID",required = true)
            @RequestParam Optional<Long> userId,
            @ApiParam(value = "当前页",defaultValue = "1")
            @RequestParam Optional<Integer> pageNumber,
            @ApiParam(value = "页面容量",defaultValue = "10")
            @RequestParam Optional<Integer> pageSize){
        QuestionQuery questionQuery = new QuestionQuery();
        questionQuery.setUserID(userId.get());
        pageNumber.ifPresent(questionQuery::setPageNumber);
        pageSize.ifPresent(questionQuery::setPageSize);
        questionQuery.setRowStart();
        Page<QuestionVO> QuestionVOList = questionService.adminQueryQuestionListAsUser(questionQuery);
        return ResponseJson.ok(QuestionVOList);
    }
}
