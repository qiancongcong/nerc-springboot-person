package com.bjnlmf.nerc.zhihu.mapper;

import com.bjnlmf.nerc.zhihu.pojo.UserVO;
import com.bjnlmf.nerc.zhihu.pojo.answer.AnswerDTO;
import com.bjnlmf.nerc.zhihu.pojo.answer.AnswerVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AnswerMapper {

    @Insert("insert into nerc_answer(userID,questionID,answerContent,shieldingAnswerContent) values(#{userID},#{questionID},#{answerContent},#{shieldingAnswerContent})")
    @Options(keyColumn="id",useGeneratedKeys=true)
    void insert(AnswerDTO answerDTO);

    @Select("select count(*) from nerc_answer where questionID = #{questionID} and deleted = 0")
    Integer count(AnswerDTO answerDTO);

    List<AnswerVO> queryAnswerList(AnswerDTO answerDTO);

    @Update("update nerc_answer set commentNum = commentNum+1 where id = #{answerId} and deleted = 0")
    void addCommentNum(@Param("answerId") Long answerId);

    @Update("update nerc_answer set praise = praise+1 where questionID =#{questionID} and id =#{id} and deleted = 0" )
    Integer priseAnswer(AnswerDTO answerDTO);

    @Select("SELECT u.userID,u.nickName,COUNT(a.id) as answerTotal ,MAX(a.createTime) as createTime" +
            " FROM acc_user u" +
            " LEFT JOIN nerc_answer a ON u.userID = a.userID" +
            " where u.deleted = 0" +
            " GROUP BY u.userID HAVING answerTotal > 0" +
            " ORDER BY answerTotal DESC , createTime ASC" +
            " LIMIT 0,10")
    List<UserVO> queryUserKing();

    @Select("select an.id,an.userID,an.answerContent,an.questionID,an.praise,an.createTime,an.shielding,an.shieldingAnswerContent,u.phone,an.deleted from nerc_answer an left join acc_user u on an.userId = u.userId where an.id = #{answerID} ")
    AnswerVO queryById(Long answerID);

    @Select("select count(id) from nerc_answer where userID = #{userID}")
    Integer countByUserId(Long userID);

    @Select("select count(id) from nerc_answer where userID = #{userID}")
    Integer adminCountByUserId(Long userID);

    @Select("select * from nerc_answer where  userID =#{userID} and deleted =0  ORDER BY createTime DESC limit #{rowStart} , #{pageSize} ")
    List<AnswerVO> queryUserAnswerList(AnswerDTO answerDTO);

    @Select("select * from nerc_answer where  userID =#{userID} ORDER BY createTime DESC limit #{rowStart} , #{pageSize} ")
    List<AnswerVO> adminQueryUserAnswerList(AnswerDTO answerDTO);
    @Select("select id,userID,praise from nerc_answer where userID = #{userID} and deleted = 0")
    List<AnswerVO> queryAnswerPraiseByUserId(Long userID);

    /**
     * 修改回答内容屏蔽状态
     * @param answerVO
     * @return
     */
    Integer shielding(AnswerVO answerVO);

    Integer queryAdminAnswerCount(AnswerDTO query);

    List<AnswerVO> queryAdminAnswerList(AnswerDTO query);
    @Update(" UPDATE nerc_answer SET deleted = 1 WHERE id in (${ids})")
    Integer updateDeletedById(AnswerVO answerVO);
}
