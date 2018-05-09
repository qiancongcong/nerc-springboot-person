package com.bjnlmf.nerc.zhihu.service;

import com.bjnlmf.nerc.zhihu.mapper.OperationLogMapper;
import com.bjnlmf.nerc.zhihu.pojo.operation.OperationLogDO;
import com.bjnlmf.nerc.zhihu.pojo.operation.OperationLogVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OperationLogService {

    @Autowired
    private OperationLogMapper operationLogMapper;


    /**
     * 提交保存操作记录
     * @param operationLogDO 问题对象
     */
    public void save(OperationLogDO operationLogDO) {
        operationLogMapper.insert(operationLogDO);
    }


    /**
     * 首页问题列表
     * @param relationId 查询条件
     * @return
     */
    public List<OperationLogVO> queryQuestionList(Long relationId) {

        return operationLogMapper.queryById(relationId);
    }

}
