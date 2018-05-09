package com.bjnlmf.nerc.zhihu.mapper;

import com.bjnlmf.nerc.zhihu.pojo.message.MessageDO;
import com.bjnlmf.nerc.zhihu.pojo.message.MessageQuery;
import com.bjnlmf.nerc.zhihu.pojo.message.MessageVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface MessageMapper {

    @Insert("INSERT INTO sys_message (userID,species,affiliatedQuestion,affiliatedID,questionId,answerId) " +
            "VALUES(#{userID},#{species},#{affiliatedQuestion},#{affiliatedID},#{questionId},#{answerId})")
    @Options(keyColumn="id",useGeneratedKeys=true)
    int save(MessageDO messageDO);

    @Select("SELECT count(id) from sys_message WHERE userID = #{userID} ")
    Integer queryMessageCountAsUser(MessageQuery messageQuery);

    @Select("SELECT id,userID,species,affiliatedQuestion,affiliatedID,questionId,answerId,readState,createTime from sys_message WHERE userID = #{userID} ORDER BY createTime DESC LIMIT #{rowStart},#{pageSize}")
    List<MessageVO> queryMessageListAsUser(MessageQuery messageQuery);

    @Select("SELECT count(id) from sys_message WHERE userID = #{userID} and readState = 0")
    Integer queryUnreadMessageCount(Long userID);

    @Update("UPDATE sys_message SET readState = 1 WHERE userID = #{userID} AND id = #{messageId} AND readState = 0")
    Integer updateUnreadMessage(@Param("userID") Long userID, @Param("messageId") Long messageId);

    @Select("SELECT count(id) from sys_message WHERE userID = #{userID}")
    Integer queryMessageCount(Long userID);
}
