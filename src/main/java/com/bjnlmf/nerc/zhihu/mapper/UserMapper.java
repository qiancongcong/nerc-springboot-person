package com.bjnlmf.nerc.zhihu.mapper;

import com.bjnlmf.nerc.zhihu.pojo.UserVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper {

    @Select("select id,userID,nickName,phone,state from acc_user where userID = #{userID} and deleted = 0")
    UserVO queryByUserID(Long userID);

    @Insert("insert into acc_user(userID,nickName,phone) values(#{userID},#{nickName},#{phone})")
    void insert(UserVO userVO);

    @Update("update acc_user set nickName = #{nickName} where userID = #{userID} and deleted = 0")
    void updateNickName(UserVO userVO);

    @Update("update acc_user set phone = #{phone} where userID = #{userID} and deleted = 0")
    void updatePhone(UserVO userVO);
}
