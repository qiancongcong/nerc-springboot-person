package com.bjnlmf.nerc.zhihu.service;

import com.bjnlmf.nerc.zhihu.enumeration.ImageType;
import com.bjnlmf.nerc.zhihu.mapper.QuestionMapper;
import com.bjnlmf.nerc.zhihu.pojo.UserVO;
import com.bjnlmf.nerc.zhihu.pojo.area.AreaVO;
import com.bjnlmf.nerc.zhihu.pojo.image.ImageDO;
import com.bjnlmf.nerc.zhihu.pojo.image.ImageDTO;
import com.bjnlmf.nerc.zhihu.pojo.question.QuestionObjectDO;
import com.bjnlmf.nerc.zhihu.pojo.question.QuestionQuery;
import com.bjnlmf.nerc.zhihu.pojo.question.QuestionTypeVO;
import com.bjnlmf.nerc.zhihu.pojo.question.QuestionVO;
import com.bjnlmf.nerc.zhihu.util.Page;
import com.mysql.cj.core.conf.PropertyDefinitions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class QuestionService {

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private AreaService areaService;

    @Autowired
    private ImageService imageService;

    /**
     * 查询大分类(父级类型)
     * @return
     */
    public List<QuestionTypeVO> queryParentType() {
        return questionMapper.queryParentType();
    }

    /**
     * 根据父级类型查询对应问题类型
     * @param id 夫类型ID
     * @return
     */
    public List<QuestionTypeVO> ueryTypeByParentId(Long id) {
        List<QuestionTypeVO> list = questionMapper.queryTypeByParentId(id);
        List<QuestionTypeVO> newList = new ArrayList<>();
        for (QuestionTypeVO questionTypeVO : list) {
            if (questionTypeVO.getParentId() != null){
                QuestionTypeVO typeVO = questionMapper.getParentType(questionTypeVO.getParentId());
                questionTypeVO.setParentClassify(typeVO.getClassify());
            }
            newList.add(questionTypeVO);
        }
        return newList;
    }

    /**
     * 提交保存问题
     * @param questionDO 问题对象
     */
    @Transactional
    public void save(QuestionObjectDO questionDO) {
        questionMapper.save(questionDO);
        List<String> images = questionDO.getImages();
        if (images != null && images.size() > 0) {
            String ids = "";
            for (String id : images) {
                ids += id + ",";
            }
            if (ids.length() > 0) {
                ids = ids.substring(0, ids.length() - 1);
                questionDO.setImageIds(ids);
                imageService.updateRelationId(questionDO.getId(), ids);
            }
        }
    }

    /**
     * 更新问题回答数
     * @param questionTypeID 问题ID
     */
    public void addAnswerNum(Long questionTypeID) {
        questionMapper.addAnswerNum(questionTypeID);
    }


    /**
     * 更新问题浏览数
     * @param id 问题ID
     */
    public void updateReadNumById(Long id) {
        questionMapper.updateReadNumById(id);
    }

    /**
     * 首页问题列表
     * @param questionQuery 查询条件
     * @return
     */
    public Page<QuestionVO> queryQuestionList(QuestionQuery questionQuery) {
        //将父级类型ID转换为对应的问题类型Id并设置到查询对象
        List<String> questionTypeIds = questionQuery.getQuestionTypeIds();
        if (questionTypeIds != null && questionTypeIds.size() > 0) {
            String ids = "";
            for (String questionTypeId : questionTypeIds) {
                List<QuestionTypeVO> questionTypeVOS = questionMapper.queryTypeByParentId(new Long(questionTypeId));
                if (questionTypeVOS.size() > 0 && questionTypeVOS != null) {
                    for (QuestionTypeVO questionTypeVO : questionTypeVOS) {
                        ids += questionTypeVO.getId() + ",";
                    }
                } else {
                    ids += questionTypeId + ",";
                }
            }
            if (ids.length() > 0) {
                ids = ids.substring(0, ids.length() - 1);
                questionQuery.setIds(ids);
            }
        }
        Page<QuestionVO> page = new Page<>(questionQuery.getPageNumber(), questionQuery.getPageSize());
        if (null != questionQuery.getKeyword() && !"".equals(questionQuery.getKeyword())) {
            questionQuery.setKeyword("%" + questionQuery.getKeyword() + "%");
        }
        Integer count = questionMapper.queryQuestionCount(questionQuery);
        page.setEntityTotal(count);
        List<QuestionVO> newQuestionVOList = new ArrayList<>();
        if (count != null && count > 0) {
            List<QuestionVO> questionVOList = questionMapper.queryQuestionList(questionQuery);
            for (QuestionVO questionVO : questionVOList) {
                //查询获取问题分类
                Long questionTypeID = questionVO.getQuestionTypeID();
                //查询父级分类以及分类
                QuestionTypeVO questionTypeVO = questionMapper.queryQuestionTypeAndParentType(questionTypeID);
                if (questionTypeVO != null) {
                    List<String> questionTag = new ArrayList<>();
                    questionTag.add(questionTypeVO.getParentClassify());
                    questionTag.add(questionTypeVO.getClassify());
                    questionVO.setQuestionTag(questionTag);
                }
                //设置用户
                UserVO user = userService.query(questionVO.getUserID());
                //获取用户头像
                ImageDO imageDO = new ImageDO();
                imageDO.setImageType(ImageType.imageHead);
                imageDO.setIds(user.getUserID() + "");
                List<ImageDTO> imageDTOList = imageService.select(imageDO);
                if (imageDTOList != null && imageDTOList.size() > 0) {
                    user.setImageHead(imageDTOList.get(0).getThumbnailPath());
                }
                questionVO.setUser(user);
                //获取问题图片
                ImageDO imageDO2 = new ImageDO();
                imageDO2.setImageType(ImageType.question);
                imageDO2.setIds(questionVO.getId() + "");
                List<ImageDTO> imageDTOList2 = imageService.select(imageDO2);
                for(int i = 0;i < imageDTOList2.size();i++){
                    if(imageDTOList2.get(i).getShielding()){
                        imageDTOList2.remove(i);
                        i = i-1;
                    }
                }
                questionVO.setContentImg(imageDTOList2);
                //拼接地址信息
                String position = "";
                if (questionVO.getProvinceCode() != null && !"".equals(questionVO.getProvinceCode())) {
                    AreaVO area = areaService.queryByAreaCode(questionVO.getProvinceCode());
                    questionVO.setProvinceText(area.getAreaName());
                    position = position + area.getAreaName();
                }
                if (questionVO.getCityCode() != null && !"".equals(questionVO.getCityCode())) {
                    AreaVO area = areaService.queryByAreaCode(questionVO.getCityCode());
                    questionVO.setCityText(area.getAreaName());
                    position = position + area.getAreaName();
                }
                if (questionVO.getCountyCode() != null && !"".equals(questionVO.getCountyCode())) {
                    AreaVO area = areaService.queryByAreaCode(questionVO.getCountyCode());
                    questionVO.setCountyText(area.getAreaName());
                    position = position + area.getAreaName();
                }
                questionVO.setPosition(position);
                newQuestionVOList.add(questionVO);
            }
        }
        page.setData(newQuestionVOList);
        return page;
    }

    /**
     * 关键字搜索
     * @param s 搜索关键字
     * @return
     */
    public List<QuestionVO> queryTitleByKeyword(String s) {
        if (null != s && !"".equals(s)) {
            s = "%" + s + "%";
        }
        return questionMapper.queryTitleByKeyword(s);
    }

    /**
     * 查询问题详情
     * @param id 问题ID
     * @return
     */
    public QuestionVO adminQueryQuestionDetails(Long id) {
        //更新浏览数
        //this.updateReadNumById(id);
        //查询获取问题详情
        QuestionVO questionVO = questionMapper.queryQuestionDetails(id);
        if (questionVO != null) {
            //设置用户
            UserVO user = userService.query(questionVO.getUserID());
            //获取用户头像
            ImageDO imageDO = new ImageDO();
            imageDO.setImageType(ImageType.imageHead);
            imageDO.setIds(user.getUserID() + "");
            List<ImageDTO> imageDTOList = imageService.select(imageDO);
            if (imageDTOList != null && imageDTOList.size() > 0) {

                user.setImageHead(imageDTOList.get(0).getThumbnailPath());
            }
            questionVO.setUser(user);
            //查询获取问题分类
            Long questionTypeID = questionVO.getQuestionTypeID();
            //查询父级分类以及分类
            QuestionTypeVO questionTypeVO = questionMapper.queryQuestionTypeAndParentType(questionTypeID);
            List<String> questionTag = new ArrayList<>();
            questionTag.add(questionTypeVO.getParentClassify());
            questionTag.add(questionTypeVO.getClassify());
            questionVO.setQuestionTag(questionTag);
            //获取问题图片
            ImageDO imageDO2 = new ImageDO();
            imageDO2.setImageType(ImageType.question);
            imageDO2.setIds(questionVO.getId() + "");
            List<ImageDTO> imageDTOList2 = imageService.select(imageDO2);
            questionVO.setContentImg(imageDTOList2);
            //拼接地址信息
            String position = "";
            if (questionVO.getProvinceCode() != null && !"".equals(questionVO.getProvinceCode())) {
                AreaVO area = areaService.queryByAreaCode(questionVO.getProvinceCode());
                questionVO.setProvinceText(area.getAreaName());
                position = position + area.getAreaName();
            }
            if (questionVO.getCityCode() != null && !"".equals(questionVO.getCityCode())) {
                AreaVO area = areaService.queryByAreaCode(questionVO.getCityCode());
                questionVO.setCityText(area.getAreaName());
                position = position + area.getAreaName();
            }
            if (questionVO.getCountyCode() != null && !"".equals(questionVO.getCountyCode())) {
                AreaVO area = areaService.queryByAreaCode(questionVO.getCountyCode());
                questionVO.setCountyText(area.getAreaName());
                position = position + area.getAreaName();
            }
            questionVO.setPosition(position);
        }
        return questionVO;
    }

    /**
     * 查询问题详情
     * @param id 问题ID
     * @return
     */
    public QuestionVO queryQuestionDetails(Long id) {
        //更新浏览数
        //this.updateReadNumById(id);
        //查询获取问题详情
        QuestionVO questionVO = questionMapper.queryQuestionDetails(id);
        if (questionVO != null) {
            //设置用户
            UserVO user = userService.query(questionVO.getUserID());
            //获取用户头像
            ImageDO imageDO = new ImageDO();
            imageDO.setImageType(ImageType.imageHead);
            imageDO.setIds(user.getUserID() + "");
            List<ImageDTO> imageDTOList = imageService.select(imageDO);
            if (imageDTOList != null && imageDTOList.size() > 0) {

                user.setImageHead(imageDTOList.get(0).getThumbnailPath());
            }
            questionVO.setUser(user);
            //查询获取问题分类
            Long questionTypeID = questionVO.getQuestionTypeID();
            //查询父级分类以及分类
            QuestionTypeVO questionTypeVO = questionMapper.queryQuestionTypeAndParentType(questionTypeID);
            List<String> questionTag = new ArrayList<>();
            questionTag.add(questionTypeVO.getParentClassify());
            questionTag.add(questionTypeVO.getClassify());
            questionVO.setQuestionTag(questionTag);
            //获取问题图片
            ImageDO imageDO2 = new ImageDO();
            imageDO2.setImageType(ImageType.question);
            imageDO2.setIds(questionVO.getId() + "");
            List<ImageDTO> imageDTOList2 = imageService.select(imageDO2);
            for(int i = 0;i < imageDTOList2.size();i++){
                if(imageDTOList2.get(i).getShielding()){
                    imageDTOList2.remove(i);
                    i = i-1;
                }
            }
            questionVO.setContentImg(imageDTOList2);
            //拼接地址信息
            String position = "";
            if (questionVO.getProvinceCode() != null && !"".equals(questionVO.getProvinceCode())) {
                AreaVO area = areaService.queryByAreaCode(questionVO.getProvinceCode());
                questionVO.setProvinceText(area.getAreaName());
                position = position + area.getAreaName();
            }
            if (questionVO.getCityCode() != null && !"".equals(questionVO.getCityCode())) {
                AreaVO area = areaService.queryByAreaCode(questionVO.getCityCode());
                questionVO.setCityText(area.getAreaName());
                position = position + area.getAreaName();
            }
            if (questionVO.getCountyCode() != null && !"".equals(questionVO.getCountyCode())) {
                AreaVO area = areaService.queryByAreaCode(questionVO.getCountyCode());
                questionVO.setCountyText(area.getAreaName());
                position = position + area.getAreaName();
            }
            questionVO.setPosition(position);
        }
        return questionVO;
    }

    /**
     * 用户问题列表
     * @param questionQuery 查询条件对象
     * @return
     */
    public Page<QuestionVO> queryQuestionListAsUser(QuestionQuery questionQuery) {
        Page<QuestionVO> page = new Page<>(questionQuery.getPageNumber(), questionQuery.getPageSize());
        //查询当前用户提问数量
        Integer count = questionMapper.queryQuestionCountAsUser(questionQuery);
        page.setEntityTotal(count);
        List<QuestionVO> questionVOList = new ArrayList<>();
        if (count != null && count > 0) {
            questionVOList = questionMapper.queryQuestionListAsUser(questionQuery);
        }
        page.setData(questionVOList);
        return page;
    }

    /**
     * 用户问题列表
     * @param questionQuery 查询条件对象
     * @return
     */
    public Page<QuestionVO> adminQueryQuestionListAsUser(QuestionQuery questionQuery) {
        Page<QuestionVO> page = new Page<>(questionQuery.getPageNumber(), questionQuery.getPageSize());
        //查询当前用户提问数量
        Integer count = questionMapper.adminQueryQuestionCountAsUser(questionQuery);
        page.setEntityTotal(count);
        List<QuestionVO> questionVOList = new ArrayList<>();
        if (count != null && count > 0) {
            questionVOList = questionMapper.adminQueryQuestionListAsUser(questionQuery);
        }
        page.setData(questionVOList);
        return page;
    }

    /**
     * 同类问题排行榜
     * @param questionTypeId 问题类型ID
     * @return
     */
    public List<QuestionVO>  queryKing(Long questionTypeId, Long questionId) {
        return questionMapper.queryKing(questionTypeId,questionId);
    }

    /**
     * 查询问题总数量
     * @param userID 用户ID
     * @return
     */
    public Integer queryQuestionCount(Long userID) {
        QuestionQuery questionQuery = new QuestionQuery();
        questionQuery.setUserID(userID);
        return questionMapper.queryQuestionCount(questionQuery);
    }

    public QuestionVO queryById(Long id) {
        return questionMapper.queryById(id);
    }

    public Integer queryQuestionCountByUserID(Long userID) {

        return questionMapper.queryQuestionCountByUserID(userID);
    }

    public List<QuestionVO> queryquestionReadNumByUserId(Long userId) {
        return questionMapper.queryquestionReadNumByUserId(userId);
    }

    /**
     * 修改屏蔽内容及状态
     */
    public Integer updateShielding(QuestionVO QuestionVO){
        return questionMapper.shielding(QuestionVO);
    }


    /**
     * 管理员提问页面
     * @param query 查询条件对象
     * @return
     */
    public Page<QuestionVO> queryAdminQuestionList(QuestionQuery query) {
        if(null != query.getKeyword() && !"".equals(query.getKeyword())){
            String keyWord = query.getKeyword();
            query.setKeyword("%"+keyWord+"%");
        }
        if (null != query.getStartDate() && "" != query.getStartDate()) {
            String startDate = query.getStartDate();
            query.setStartDate(startDate + " 00:00:00");
        }
        if (null != query.getEndDate() && "" != query.getEndDate()) {
            String endDate = query.getEndDate();
            query.setEndDate(endDate + " 23:59:59");
        }

        Page<QuestionVO> page = new Page<>(query.getPageNumber(), query.getPageSize());
        Integer count = questionMapper.queryAdminQuestionCount(query);
        page.setEntityTotal(count);
        if (count > 0){
            List<QuestionVO> questionVOS = questionMapper.queryAdminQuestionList(query);
            List<QuestionVO> newList = new ArrayList<>();
            for (QuestionVO questionVO : questionVOS) {
                //查询获取问题分类
                Long questionTypeID = questionVO.getQuestionTypeID();
                //查询父级分类以及分类
                QuestionTypeVO questionTypeVO = questionMapper.queryQuestionTypeAndParentType(questionTypeID);
                if (questionTypeVO != null) {
                    List<String> questionTag = new ArrayList<>();
                    questionTag.add(questionTypeVO.getParentClassify());
                    questionTag.add(questionTypeVO.getClassify());
                    questionVO.setQuestionTag(questionTag);
                }
                newList.add(questionVO);
            }
            page.setData(newList);
        }
        return page;
    }

    public Integer deleteQuestionById(QuestionVO questionVO) {
       return questionMapper.updateDeletedById(questionVO);
    }

    public Map<String,Object> queryTypes() {
        //查询所有分类
        List<QuestionTypeVO> types = questionMapper.queryByType();
        Map<String, Object> typeMap = new HashMap<>();
        List<Map<String, Object>> typeList = new ArrayList<>();
        List<QuestionTypeVO> otherTypes = new ArrayList<>();
        List<QuestionTypeVO> parentTypes = new ArrayList<>();  //父级分类的List
        for (QuestionTypeVO type : types) {
            //当类型父级ID为空时,说明该类型是父级类型
            if (type.getParentId() == null){
                parentTypes.add(type);
            }else{
                //把不是父级类型的对象放到一个新及集合中
                otherTypes.add(type);
            }
        }
        for (QuestionTypeVO questionTypeVO : parentTypes) {
            Map<String, Object> parentTypesMap = new HashMap<>();
            parentTypesMap.put("id",questionTypeVO.getId());
            parentTypesMap.put("classify",questionTypeVO.getClassify());
            List<Map<String, Object>> childTypes = new ArrayList<>();  //子级类型的List
            for (QuestionTypeVO otherType : otherTypes) {
                Map<String, Object> childTypesMap = new HashMap<>();
                //如果parentId等于父级类型的Id  则说明这是这个父级类型的一个子级分类
                if (otherType.getParentId().equals(questionTypeVO.getId())){
                    childTypesMap.put("id",otherType.getId());
                    childTypesMap.put("classify",otherType.getClassify());
                    childTypes.add(childTypesMap);
                }
            }
            //把子级分类的list放入父级分类的Map中
            parentTypesMap.put("types",childTypes);
            typeList.add(parentTypesMap);
        }
        typeMap.put("all",typeList);
        return typeMap;
    }

}
