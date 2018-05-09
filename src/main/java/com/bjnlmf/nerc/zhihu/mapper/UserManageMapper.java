package com.bjnlmf.nerc.zhihu.mapper;

import com.bjnlmf.nerc.zhihu.pojo.UserManageQuery;
import com.bjnlmf.nerc.zhihu.pojo.UserVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserManageMapper {

    @Select("select id,userID,nickName,createTime,state from acc_user where deleted = 0")
    List<UserVO> queryUserManageList(UserManageQuery userManageQuery);

    List<UserVO> queryList(UserManageQuery userManageQuery);
    Integer queryListCount(UserManageQuery userManageQuery);

    void updateState(UserManageQuery userManageQuery);

}
