package com.bjnlmf.nerc.zhihu.controller;

import com.bjnlmf.nerc.zhihu.pojo.UserManageQuery;
import com.bjnlmf.nerc.zhihu.pojo.UserVO;
import com.bjnlmf.nerc.zhihu.service.UserManageService;
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
@RequestMapping(value = "/admin/user_manage")
@Api(description = "用户管理")
public class UserManageController {

    @Autowired
    private UserManageService userManageService;
    @Autowired
    private UserService userService;

    @RequestMapping(value = "/query", method = RequestMethod.GET)
    @ApiOperation(value = "用户管理列表")
    public ResponseJson<Page<UserVO>> queryList(
            @ApiParam(value = "userToken")
            @RequestHeader String userToken,
            @ApiParam(value = "userID")
            @RequestParam Optional<Long> userID,
            @ApiParam(value = "nickName")
            @RequestParam Optional<String> nickName,
            @ApiParam(value = "开始时间")
            @RequestParam Optional<String> startTime,
            @ApiParam(value = "结束时间")
            @RequestParam Optional<String> endTime,
            @ApiParam(value = "状态")
            @RequestParam Optional<Integer> state,
            @ApiParam(value = "页码",defaultValue = "1")
            @RequestParam Optional<Integer> pageNumber,
            @ApiParam(value = "页容量",defaultValue = "5")
            @RequestParam Optional<Integer> pageSize
    ) {
        UserVO userVO = userService.queryByUserToken(userToken);
        UserManageQuery userManageQuery = new UserManageQuery();
        nickName.ifPresent(userManageQuery::setNickName);
        userID.ifPresent(userManageQuery::setUserID);
        state.ifPresent(userManageQuery::setState);
        startTime.ifPresent(userManageQuery::setStartTimeStr);
        endTime.ifPresent(userManageQuery::setEndTimeStr);
        pageNumber.ifPresent(userManageQuery::setPageNumber);
        pageSize.ifPresent(userManageQuery::setPageSize);
        userManageQuery.setRowStart();
        Page<UserVO> list = userManageService.queryUserManageList(userManageQuery);
        return ResponseJson.ok(list);
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ApiOperation(value = "更改用户状态")
    public ResponseJson<String> updateState(
            @ApiParam(value = "userToken")
            @RequestHeader String userToken,
            @ApiParam(value = "userID", required = true)
            @RequestParam Optional<String> userID,
            @ApiParam(value = "状态", required = true)
            @RequestParam Optional<Integer> state
    ) {
        UserVO userVO = userService.queryByUserToken(userToken);
        if(null != userID){
            String [] s = userID.get().split(",");
            if(1 == s.length){
                UserManageQuery userManageQuery = new UserManageQuery();
                state.ifPresent(userManageQuery::setState);
                userManageQuery.setUserID(Long.parseLong(userID.get()));
                userManageService.updateState(userManageQuery);
            } else {
              for(String a : s){
                  UserManageQuery userManageQuery = new UserManageQuery();
                  state.ifPresent(userManageQuery::setState);
                  userManageQuery.setUserID(Long.parseLong(a));
                  userManageService.updateState(userManageQuery);
              }
            }
        }
        return ResponseJson.ok();
    }


}
