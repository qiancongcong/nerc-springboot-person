package com.bjnlmf.nerc.zhihu.mapper;

import com.bjnlmf.nerc.zhihu.pojo.comment.CommentDO;
import com.bjnlmf.nerc.zhihu.pojo.comment.CommentVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CommentMapper {

    @Insert("insert into nerc_comments(userID,commentsContent,commentsType,questionID,answerID) " +
            "values(#{userId},#{commentsContent},#{commentsType},#{questionID},#{answerID})")
    @Options(keyColumn="id",useGeneratedKeys=true)
    void insert(CommentDO commentDO);

    @Insert("insert into nerc_dialogue_relation(commentID,userID,commentsToId,commentsToBody,dialogToCommentId) " +
            "values(#{id},#{userId},#{commentsToId},#{commentsToBody},#{dialogToCommentId})")
    void insertDailog(CommentDO commentDO);

   // @Select("select count(id) from nerc_comments where answerID =#{answerID} and deleted = 0 ")
   @SelectProvider(type = commentParams.class, method = "queryOrderByParam")
    Integer count(CommentDO commentDO);

    List<CommentVO> queryCommentList(CommentDO commentDO);

    @Update("update nerc_comments set praise = praise+1 where questionID =#{questionID} and answerID =#{answerID} and id =#{id} and deleted = 0" )
    Integer priseComment(CommentDO commentDO);

    @Select("select nc.id,nc.commentsContent,nc.createTime,nc.questionID,nc.answerID,ndr.dialogToCommentId,nc.userID as userId,ndr.commentsToId,nc.shielding from nerc_comments nc " +
            "left join nerc_dialogue_relation ndr on nc.id = ndr.commentID WHERE nc.id = #{commentId} and nc.deleted = 0 ")
    CommentVO queryById(Long commentId);

    List<CommentVO> dailogList(CommentVO commentVO);

    /**
     * 修改评论的屏蔽状态
     * @param commentDO
     * @return
     */
    @Update("update nerc_comments set shielding = #{shielding} where id in (${ids})")
    Integer shielding(CommentDO commentDO);

    @Select("select count(id) from nerc_comments where userId =#{userId} and deleted = 0 ")
    Integer countCommentList(CommentDO commentDO);
    @Select("SELECT co.id, co.commentsContent, co.createTime, co.praise, ans.answerContent FROM nerc_comments co LEFT JOIN nerc_answer ans ON co.answerID = ans.id " +
            "WHERE co.userId =#{userId} AND co.deleted = 0 ORDER BY co.createTime DESC LIMIT #{rowStart},#{pageSize}")
    List<CommentVO> listCommentManage(CommentDO commentDO);

    @Select("select count(id) from nerc_comments where userId =#{userId} ")
    Integer adminCountCommentList(CommentDO commentDO);
    @Select("SELECT co.id, co.commentsContent, co.createTime, co.praise, ans.answerContent,(SELECT COUNT(re.id) FROM nerc_dialogue_relation re WHERE re.`dialogToCommentId`=co.id) AS replyNum  FROM nerc_comments co LEFT JOIN nerc_answer ans ON co.answerID = ans.id " +
            "WHERE co.userId =#{userId} ORDER BY co.createTime DESC LIMIT #{rowStart},#{pageSize}")
    List<CommentVO> adminListCommentManage(CommentDO commentDO);
}
