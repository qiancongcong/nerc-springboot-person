package com.bjnlmf.nerc.zhihu.mapper;

import com.bjnlmf.nerc.zhihu.pojo.BehaviorDO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface BehaviorMapper {

    @Insert("insert into sys_behavior_relation(userID,behaviorType,answerID,commentID) " +
            "values(#{userID},#{behaviorType},#{answerID},#{commentID})")
    void insert(BehaviorDO behaviorDO);

    @Select("select userID,behaviorType,answerID from sys_behavior_relation where userID = #{userID} and answerID in (${answerIds})")
    List<BehaviorDO> selectlist(@Param("userID") Long userID, @Param("answerIds") String answerIds);

    @Select("select userID,behaviorType,answerID from sys_behavior_relation where userID = #{userID} and answerID =#{answerID} and behaviorType= #{behaviorType}")
    BehaviorDO queryByAnswerIdAndUserId(BehaviorDO behaviorDO);

    @Select("select userID,behaviorType,answerID from sys_behavior_relation where userID = #{userID} and commentID =#{commentID} and behaviorType= #{behaviorType}")
    BehaviorDO queryByCommentIdAndUserId(BehaviorDO behaviorDO);
}
