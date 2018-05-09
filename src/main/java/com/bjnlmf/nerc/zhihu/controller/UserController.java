package com.bjnlmf.nerc.zhihu.controller;

import com.bjnlmf.nerc.zhihu.pojo.UserDTO;
import com.bjnlmf.nerc.zhihu.pojo.UserVO;
import com.bjnlmf.nerc.zhihu.service.UserService;
import com.bjnlmf.nerc.zhihu.util.ResponseJson;
import com.bjnlmf.nerc.zhihu.util.UserStateUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping(value = "user")
@Api(description = "登录")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "login",method= RequestMethod.POST)
    @ApiOperation(value = "登录")
    public ResponseJson<UserVO> login(
            @ApiParam(value = "登录名")
            @RequestParam Optional<String> userName,
            @ApiParam(value = "密码")
            @RequestParam Optional<String> passWord
        ){
        UserDTO userDTO = new UserDTO();
        userName.ifPresent(userDTO::setUserName);
        passWord.ifPresent(userDTO::setPassWord);
        UserVO userVO = userService.login(userDTO);
        return ResponseJson.ok(userVO);
    }


    @RequestMapping(value = "check_token",method= RequestMethod.POST)
    @ApiOperation(value = "检测token是否存在")
    public ResponseJson<UserVO> checkToken(
            @ApiParam(value = "userToken")
            @RequestHeader String userToken){
        UserVO userVO = userService.queryByUserToken(userToken);
        return ResponseJson.ok(userVO);
    }

    @RequestMapping(value = "check_token_state",method= RequestMethod.POST)
    @ApiOperation(value = "检测token是否存在")
    public ResponseJson<UserVO> checkTokenState(
            @ApiParam(value = "userToken")
            @RequestHeader String userToken){
        UserVO userVO = userService.queryByUserToken(userToken);
        UserStateUtil.checkUserState(userVO);
        return ResponseJson.ok();
    }

    @RequestMapping(value = "detail",method= RequestMethod.POST)
    @ApiOperation(value = "获取用户信息")
    public ResponseJson<UserVO> checkToken(
            @ApiParam(value = "userID")
            @RequestParam Long userId){
        UserVO userVO = userService.queryUserDetail(userId);
        return ResponseJson.ok(userVO);
    }
    @RequestMapping(value = "modifyPassword",method= RequestMethod.POST)
    @ApiOperation(value = "修改用户密码")
    public ResponseJson modifyPassword(
            @ApiParam(value = "newPassword")
            @RequestParam String newPassword,
            @ApiParam(value = "password")
            @RequestParam String password,
            @ApiParam(value = "userToken")
            @RequestHeader(required = false) String userToken){
        userService.modifyPassword(password,newPassword,userToken);
                return ResponseJson.ok();
    }
}
