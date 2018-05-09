package com.bjnlmf.nerc.zhihu.controller;

import com.bjnlmf.nerc.zhihu.annotation.Behavior;
import com.bjnlmf.nerc.zhihu.annotation.BehaviorContent;
import com.bjnlmf.nerc.zhihu.pojo.UserVO;
import com.bjnlmf.nerc.zhihu.pojo.area.AreaVO;
import com.bjnlmf.nerc.zhihu.pojo.question.QuestionObjectDO;
import com.bjnlmf.nerc.zhihu.pojo.question.QuestionQuery;
import com.bjnlmf.nerc.zhihu.pojo.question.QuestionTypeVO;
import com.bjnlmf.nerc.zhihu.pojo.question.QuestionVO;
import com.bjnlmf.nerc.zhihu.service.AreaService;
import com.bjnlmf.nerc.zhihu.service.QuestionService;
import com.bjnlmf.nerc.zhihu.service.UserService;
import com.bjnlmf.nerc.zhihu.util.Page;
import com.bjnlmf.nerc.zhihu.util.ResponseJson;
import com.bjnlmf.nerc.zhihu.util.UserStateUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;


@RestController
@RequestMapping("question")
@Api(description = "问题")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private UserService userService;

    @Autowired
    private AreaService areaService;


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

    @RequestMapping(value = "/types_query",method = RequestMethod.GET)
    @ApiOperation("查询获取所有分类")
    public  ResponseJson<Map<String, Object>> queryTypes(){
        Map<String, Object> map = questionService.queryTypes();
        return ResponseJson.ok(map);
    }

    @RequestMapping(value = "/submit_question",method = RequestMethod.POST)
    @ApiOperation("提交问题")
    @Behavior(u_act="question_add")
    public ResponseJson queryAnswerList(
            @BehaviorContent(userToken= "userToken")
            @ApiParam(value = "用户userToken",required = true)
            @RequestHeader String userToken,
            @BehaviorContent(u_ip= "ip")
            @ApiParam(value = "ip")
            @RequestHeader Map<String,String> ip,
            @BehaviorContent(act_obj= "questionTitle",act_cont = "questionContent")
            @RequestBody QuestionObjectDO questionObjectDO){

        UserVO user = userService.queryByUserToken(userToken);
        UserStateUtil.checkUserState(user);
        questionObjectDO.setUserID(user.getUserID());
        if (questionObjectDO.getProvinceCode() != null && !"".equals(questionObjectDO.getProvinceCode())){
            AreaVO province = areaService.queryByAreaCode(questionObjectDO.getProvinceCode());
            if (province == null){
                questionObjectDO.setProvinceCode(null);
            }
        }
        if (questionObjectDO.getCityCode() != null && !"".equals(questionObjectDO.getCityCode())){
            AreaVO city = areaService.queryByAreaCode(questionObjectDO.getCityCode());
            if (city == null){
                questionObjectDO.setCityCode(null);
            }
        }
        if (questionObjectDO.getAreasCode() != null && !"".equals(questionObjectDO.getAreasCode())){
            AreaVO county = areaService.queryByAreaCode(questionObjectDO.getAreasCode());
            if (county == null){
                questionObjectDO.setAreasCode(null);
            }
        }
        questionObjectDO.setShieldingTitleKeyWord(questionObjectDO.getQuestionTitle());
        if (questionObjectDO.getQuestionContent() != null && !"".equals(questionObjectDO.getQuestionContent())){
            questionObjectDO.setShieldingContentKeyWord(questionObjectDO.getQuestionContent());
        }
        questionService.save(questionObjectDO);
        return ResponseJson.ok();
    }

    @RequestMapping(value = "/list",method = RequestMethod.GET)
    @ApiOperation("首页问题列表展示")
    @Behavior(u_act="question_query")
    public ResponseJson<Page<QuestionVO>> getNewHot(
            @BehaviorContent(userToken= "userToken")
            @ApiParam(value = "用户userToken")
            @RequestHeader(required = false) String userToken,
            @ApiParam(value = "当前页",defaultValue = "1")
            @RequestParam Optional<Integer> pageNumber,
            @ApiParam(value = "页面容量",defaultValue = "10")
            @RequestParam Optional<Integer> pageSize,
            @ApiParam(value = "排序字段(createTime:时间,hot:热度)")
            @RequestParam Optional<String> sortField,
            @ApiParam(value = "排序方式(1:正序,0:倒序)")
            @RequestParam Optional<Integer> sortOrder,
            @ApiParam(value = "回答状态(1:已回答,-1:未回答)")
            @RequestParam Optional<Integer> answerState,
            @BehaviorContent(act_cont = "questionTypeIds")
            @ApiParam(value = "筛选条件(选择不限时传父级类型ID)")
            @RequestParam Optional<List<String>> questionTypeIds,
            @BehaviorContent(u_ip= "ip")
            @ApiParam(value = "ip")
            @RequestHeader Map<String,String> ip,
            @BehaviorContent(act_cont = "keyword")
            @ApiParam(value = "查询关键字")
            @RequestParam Optional<String> keyword){
        QuestionQuery questionQuery = new QuestionQuery();
        pageNumber.ifPresent(questionQuery::setPageNumber);
        pageSize.ifPresent(questionQuery::setPageSize);
        questionQuery.setRowStart();
        sortField.ifPresent(questionQuery::setSortField);
        sortOrder.ifPresent(questionQuery::setSortOrder);
        if (null == questionQuery.getSortField() && null == questionQuery.getSortOrder()){
            questionQuery.setSortField("createTime");
            questionQuery.setSortOrder(0);
        }
        answerState.ifPresent(questionQuery::setAnswerState);
        questionTypeIds.ifPresent(questionQuery::setQuestionTypeIds);
        keyword.ifPresent(questionQuery::setKeyword);
        if(keyword.isPresent()){
            questionQuery.setSortField("hot");
            questionQuery.setSortOrder(null);
        }
        Page<QuestionVO> QuestionVOList = questionService.queryQuestionList(questionQuery);
        return ResponseJson.ok(QuestionVOList);
    }

    @RequestMapping(value = "/add_browse_number",method = RequestMethod.POST)
    @ApiOperation("增加浏览数")
    public ResponseJson addBrowseNumber(
            @ApiParam(value = "问题ID",required = true)
            @RequestParam Optional<Long> questionId){
        questionService.updateReadNumById(questionId.get());
        return ResponseJson.ok();
    }

    @RequestMapping(value = "/query_as_title",method = RequestMethod.GET)
    @ApiOperation("关键字查询关联问题")
    public ResponseJson<List<QuestionVO>> queryTitleByKeyword(
            @ApiParam(value = "关键字")
            @RequestParam Optional<String> keyword){
        List<QuestionVO> questionVOList = questionService.queryTitleByKeyword(keyword.get());
        return ResponseJson.ok(questionVOList);
    }

    @RequestMapping(value = "/details",method = RequestMethod.GET)
    @ApiOperation("查询问题详情")
    public ResponseJson<QuestionVO> questionDetails(
            @ApiParam(value = "问题ID")
            @RequestParam Optional<Long> questionId){
        QuestionVO questionVO = questionService.queryQuestionDetails(questionId.get());
        return ResponseJson.ok(questionVO);
    }

    @RequestMapping(value = "/list_as_user",method = RequestMethod.GET)
    @ApiOperation("查询个人问题列表")
    public ResponseJson<Page<QuestionVO>> queryTitleByKeyword(
            @ApiParam(value = "用户userToken",required = true)
            @RequestHeader Optional<String> userToken,
            @ApiParam(value = "当前页",defaultValue = "1")
            @RequestParam Optional<Integer> pageNumber,
            @ApiParam(value = "页面容量",defaultValue = "10")
            @RequestParam Optional<Integer> pageSize){
        QuestionQuery questionQuery = new QuestionQuery();
        UserVO user = userService.queryByUserToken(userToken.get());
        questionQuery.setUserID(user.getUserID());
        pageNumber.ifPresent(questionQuery::setPageNumber);
        pageSize.ifPresent(questionQuery::setPageSize);
        questionQuery.setRowStart();
        Page<QuestionVO> QuestionVOList = questionService.queryQuestionListAsUser(questionQuery);
        return ResponseJson.ok(QuestionVOList);
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
        Page<QuestionVO> QuestionVOList = questionService.queryQuestionListAsUser(questionQuery);
        return ResponseJson.ok(QuestionVOList);
    }

    @RequestMapping(value = "/same_king",method = RequestMethod.GET)
    @ApiOperation("同类问题排行榜")
    public ResponseJson<List<QuestionVO>> queryKing(
            @ApiParam(value = "问题类型ID",required = true)
            @RequestParam Optional<Long> questionTypeID,
            @ApiParam(value = "问题ID",required = true)
            @RequestParam Optional<Long> questionId){
        return ResponseJson.ok(questionService.queryKing(questionTypeID.get(),questionId.get()));
    }

    @RequestMapping(value = "/count",method = RequestMethod.GET)
    @ApiOperation("问题总数")
    public ResponseJson<Integer> queryQuestionCount(
            @ApiParam(value = "用户userToken",required = true)
            @RequestHeader Optional<String> userToken){
        UserVO user = userService.queryByUserToken(userToken.get());
        return ResponseJson.ok(questionService.queryQuestionCountByUserID(user.getUserID()));
    }
}
