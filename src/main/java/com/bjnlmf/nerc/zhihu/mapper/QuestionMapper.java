package com.bjnlmf.nerc.zhihu.mapper;

import com.bjnlmf.nerc.zhihu.pojo.question.QuestionObjectDO;
import com.bjnlmf.nerc.zhihu.pojo.question.QuestionQuery;
import com.bjnlmf.nerc.zhihu.pojo.question.QuestionTypeVO;
import com.bjnlmf.nerc.zhihu.pojo.question.QuestionVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface QuestionMapper {

    @Insert(" INSERT INTO nerc_question (userID,questionTypeID,provinceCode,cityCode,countyCode,questionTitle,questionContent,shieldingTitleKeyWord,shieldingContentKeyWord)\n" +
            "  VALUES (#{userID},#{questionTypeID},#{provinceCode},#{cityCode},#{areasCode},#{questionTitle},#{questionContent},#{shieldingTitleKeyWord},#{shieldingContentKeyWord})")
    @Options(keyColumn="id",useGeneratedKeys=true)
    void save(QuestionObjectDO questionDO);

    @Select("select id,classify,parentId from conf_quest_type WHERE parentId IS null and deleted = 0")
    List<QuestionTypeVO> queryParentType();

    @Select(" select id,classify,parentId from conf_quest_type WHERE parentId = #{id} and deleted = 0")
    List<QuestionTypeVO> queryTypeByParentId(Long id);

    @Update(" UPDATE nerc_question SET readNum = readNum + 1  WHERE id = #{id}")
    void updateReadNumById(Long id);

    @Update(" UPDATE nerc_question SET deleted = 1 WHERE id in (${ids})")
    Integer updateDeletedById(QuestionVO questionVO);



    @Update("update nerc_question set answerNum = answerNum +1 where id = #{questionTypeID}")
    void addAnswerNum(@Param("questionTypeID") Long questionTypeID);

    @Select("SELECT qt.classify as parentClassify,q.classify as classify FROM conf_quest_type qt\n" +
            "LEFT JOIN conf_quest_type q\n" +
            "ON qt.id = q.parentId\n" +
            "where q.id = #{questionTypeID}")
    QuestionTypeVO queryQuestionTypeAndParentType(Long questionTypeID);

    @Select("select id,classify from conf_quest_type WHERE id = #{parentId} and deleted = 0")
    QuestionTypeVO getParentType(Long parentId);

    @Select("SELECT id,questionTitle FROM nerc_question where questionTitle LIKE #{s} ORDER BY answerNum DESC , readNum DESC , createTime ASC LIMIT 0,10")
    List<QuestionVO> queryTitleByKeyword(String s);

    @Select("SELECT q.id,q.userID,q.questionTitle,q.questionContent,q.questionTypeID,q.answerNum,q.readNum,q.createTime,q.provinceCode,q.cityCode,q.countyCode,case q.deleted when 1 then '已删除' else case q.answerNum when 0 then '未回答' else '已回答' end end as answerState,q.shieldingTitle,q.shieldingTitleKeyWord,q.shieldingContent,q.shieldingContentKeyWord,u.phone FROM nerc_question q left join acc_user u on q.userId = u.userId WHERE q.id = #{id}")
    QuestionVO queryQuestionDetails(Long id);

    @Select("SELECT count(id) from nerc_question WHERE userID = #{userID} and deleted = 0 ")
    Integer queryQuestionCountAsUser(QuestionQuery questionQuery);

    @Select("SELECT id,questionTitle,questionContent,createTime,answerNum,readNum from nerc_question WHERE userID = #{userID} and deleted = 0 ORDER BY createTime DESC LIMIT #{rowStart},#{pageSize}")
    List<QuestionVO> queryQuestionListAsUser(QuestionQuery questionQuery);

    @Select("SELECT count(id) from nerc_question WHERE userID = #{userID}")
    Integer adminQueryQuestionCountAsUser(QuestionQuery questionQuery);

    @Select("SELECT id,questionTitle,questionContent,createTime,answerNum,readNum from nerc_question WHERE userID = #{userID} ORDER BY createTime DESC LIMIT #{rowStart},#{pageSize}")
    List<QuestionVO> adminQueryQuestionListAsUser(QuestionQuery questionQuery);
    @Select("SELECT id,questionTitle,answerNum,readNum,questionTypeID from nerc_question WHERE questionTypeID = #{questionTypeId} and id != #{questionId} and deleted = 0 ORDER BY answerNum DESC , readNum DESC , createTime ASC LIMIT 0,10")
    List<QuestionVO> queryKing(@Param("questionTypeId") Long questionTypeId, @Param("questionId") Long questionId);

    Integer queryQuestionCount(QuestionQuery questionQuery);

    List<QuestionVO> queryQuestionList(QuestionQuery questionQuery);

    @Select("select id,questionTitle,userID from nerc_question where id = #{id}")
    QuestionVO queryById(Long id);

    @Select("select count(id) from nerc_question where userID = #{userID}")
    Integer queryQuestionCountByUserID(Long userID);

    @Select("select id,userID,readNum from nerc_question where userID = #{userID} and deleted = 0")
    List<QuestionVO> queryquestionReadNumByUserId(Long userId);

    /**
     * 修改问题屏蔽状态及内容
     * @param questionVO
     * @return
     */
    Integer shielding(QuestionVO questionVO);


    Integer queryAdminQuestionCount(QuestionQuery query);

    List<QuestionVO> queryAdminQuestionList(QuestionQuery query);

    @Select("select u.id,n.nickName,count(*) as questionCount from nerc_question n left join acc_user u on u.id=n.userID where userID = #{userID} and n.createTime #{startDate} value1 AND #{endDate} group by n.userId")
    List<QuestionVO> getQuestionCount(QuestionQuery questionQuery);

    @Select("select count(*) from (select count(*) as questionCount from nerc_question where userID = #{userID} and createTime #{startDate} value1 AND #{endDate} group by userId) c")
    Integer getCount(QuestionQuery questionQuery);

    @Select("select id,classify,parentId from conf_quest_type WHERE deleted = 0")
    List<QuestionTypeVO> queryByType();
}
