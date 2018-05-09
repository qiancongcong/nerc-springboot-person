package com.bjnlmf.nerc.zhihu.mapper;

import com.bjnlmf.nerc.zhihu.pojo.operation.OperationLogDO;
import com.bjnlmf.nerc.zhihu.pojo.operation.OperationLogVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OperationLogMapper {

    @Insert("insert into sys_operation_log(userId,relationId,operationType) values(#{userId},#{relationId},#{operationType})")
    @Options(keyColumn="id",useGeneratedKeys=true)
    void insert(OperationLogDO operationLogDO);

    @Select("select count(*) from sys_operation_log where relationId = #{relationId} and deleted = 0")
    Integer count(Long relationId);

    @Select("select a.id,a.userId,a.relationId,a.createTime,a.operationType,u.nickName from sys_operation_log a left join acc_user u on a.userId=u.userId where relationId = #{relationId} order by a.createTime desc")
    List<OperationLogVO> queryById(Long relationId);

}
