package com.bjnlmf.nerc.zhihu.controller;

import com.bjnlmf.nerc.zhihu.pojo.operation.OperationLogVO;
import com.bjnlmf.nerc.zhihu.service.OperationLogService;
import com.bjnlmf.nerc.zhihu.util.ResponseJson;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/admin/operation")
@Api(description = "用户操作")
public class AdminOperationLogController {
    @Autowired
    OperationLogService operationLogService;

    @RequestMapping(value = "/list",method = RequestMethod.GET)
    @ApiOperation("查询问题、回答操作记录")
    public ResponseJson<List<OperationLogVO>> operationLogList(
            @ApiParam(value = "用户userToken",required = true)
            @RequestHeader String userToken,
            @ApiParam(value = "问题ID",required = true)
            @RequestParam Optional<Long> questionID){
        List<OperationLogVO> list= operationLogService.queryQuestionList(questionID.get());
        return ResponseJson.ok(list);
    }
}
