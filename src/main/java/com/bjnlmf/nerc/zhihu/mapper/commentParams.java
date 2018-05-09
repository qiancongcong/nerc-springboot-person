package com.bjnlmf.nerc.zhihu.mapper;

import com.bjnlmf.nerc.zhihu.pojo.comment.CommentDO;
import org.apache.ibatis.jdbc.SQL;

public class commentParams {
    public String queryOrderByParam(CommentDO param) {
        SQL sql = new SQL().SELECT("count(*)").FROM("nerc_comments");
        Long answerId = param.getAnswerID();
        sql.WHERE("answerID = "+ answerId);
        sql.WHERE("deleted = 0");
       /* if(param.getShielding() == null){
            sql.WHERE("shielding = 0");
        }*/
        return sql.toString();
    }
}
