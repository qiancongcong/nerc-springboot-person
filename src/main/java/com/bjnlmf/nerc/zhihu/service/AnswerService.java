package com.bjnlmf.nerc.zhihu.service;

import com.bjnlmf.nerc.zhihu.enumeration.BehaviorType;
import com.bjnlmf.nerc.zhihu.enumeration.ImageType;
import com.bjnlmf.nerc.zhihu.enumeration.MessageType;
import com.bjnlmf.nerc.zhihu.exception.BusinessException;
import com.bjnlmf.nerc.zhihu.mapper.AnswerMapper;
import com.bjnlmf.nerc.zhihu.pojo.BehaviorDO;
import com.bjnlmf.nerc.zhihu.pojo.UserVO;
import com.bjnlmf.nerc.zhihu.pojo.answer.AnswerDTO;
import com.bjnlmf.nerc.zhihu.pojo.answer.AnswerVO;
import com.bjnlmf.nerc.zhihu.pojo.image.ImageDO;
import com.bjnlmf.nerc.zhihu.pojo.image.ImageDTO;
import com.bjnlmf.nerc.zhihu.pojo.message.MessageDO;
import com.bjnlmf.nerc.zhihu.pojo.question.QuestionVO;
import com.bjnlmf.nerc.zhihu.util.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class AnswerService {

    static Object lock = new Object();

    private Logger logger = LoggerFactory.getLogger(AnswerService.class);

    @Autowired
    private AnswerMapper answerMapper;
    @Autowired
    private ImageService imageService;
    @Autowired
    private QuestionService questionService;
    @Autowired
    private BehaviorService behaviorService;
    @Autowired
    private MessageService messageService;
    @Autowired
    private UserService userService;
    @Autowired
    private CommentService commentService;

    //回答列表分页显示
    public Page<AnswerVO> queryAnswerList(AnswerDTO answerDTO) {
        Integer count = answerMapper.count(answerDTO);
        Page<AnswerVO> page = new Page<>(answerDTO.getPageNumber(),answerDTO.getPageSize());
        page.setEntityTotal(count);
        if(count!=null&&count.intValue()>0){
            List<AnswerVO> list = answerMapper.queryAnswerList(answerDTO);
            String userIds = "";
            String answerIds= "";
            for (AnswerVO answerVO:list) {
                userIds += answerVO.getUserID() +",";
                answerIds += answerVO.getId() + ",";
            }
            if (userIds.length()>0){
                userIds = userIds.substring(0,userIds.length()-1);
                ImageDO imageDO = new ImageDO();
                imageDO.setImageType(ImageType.imageHead);
                imageDO.setIds(userIds);
                List<ImageDTO> imageDTOList = imageService.select(imageDO);
                List<AnswerVO> newList = new ArrayList<>();
                for (AnswerVO answerVO:list) {
                    for (ImageDTO imageDTO:imageDTOList) {
                        if(answerVO.getUserID().equals(imageDTO.getRelationId())){
                            answerVO.setImageHead(imageDTO.getThumbnailPath());
                            break;
                        }
                    }
                    newList.add(answerVO);
                }
                page.setData(newList);
            }
            if(answerDTO.getUserID()!=null){
                if (answerIds.length()>0){
                    answerIds = answerIds.substring(0,answerIds.length()-1);
                }
                List<BehaviorDO> behaviorLists = behaviorService.select(answerDTO.getUserID(),answerIds);
                List<AnswerVO> newList = new ArrayList<>();
                for (AnswerVO answerVO:page.getData()) {
                    for (BehaviorDO behaviorDO:behaviorLists) {
                        if(answerVO.getId().equals(behaviorDO.getAnswerID())){
                            answerVO.setBehaviorType(behaviorDO.getBehaviorType());
                            break;
                        }
                    }
                    newList.add(answerVO);
                }
                page.setData(newList);
            }
            if(list!=null){
                List<AnswerVO> newList1 = new ArrayList<>();
                for (AnswerVO answerVO:list) {
                    ImageDO imageDO = new ImageDO();
                    imageDO.setImageType(ImageType.answer);
                    imageDO.setIds(answerVO.getId().toString());
                    List<ImageDTO> imageDTOList = imageService.select(imageDO);
                    answerVO.setImages(imageDTOList);
                    newList1.add(answerVO);
                }
                page.setData(newList1);
            }
        }
        return page;
    }

    //增加回答
    @Transactional
    public void addAnswer(AnswerDTO answerDTO) {
        answerMapper.insert(answerDTO);
        List<String> images =  answerDTO.getImages();
        if(images!=null&&images.size()>0){
            String ids = "";
            for (String id:images) {
                 ids += id +",";
            }
            if(ids.length()>0){
                ids = ids.substring(0,ids.length()-1);
                answerDTO.setImageIds(ids);
                imageService.saveRelationId(answerDTO);
            }
        }
        questionService.addAnswerNum(answerDTO.getQuestionID());
        MessageDO messageDO = new MessageDO();
        messageDO.setSpecies(MessageType.answer);
        messageDO.setAffiliatedQuestion(answerDTO.getAnswerContent());
        messageDO.setAffiliatedID(answerDTO.getUserID());
        QuestionVO questionVO =  questionService.queryById(answerDTO.getQuestionID());
        messageDO.setUserID(questionVO.getUserID());
        messageDO.setQuestionId(questionVO.getId());
        messageService.save(messageDO);
    }


    public void addCommentNum(Long answerId) {
        answerMapper.addCommentNum(answerId);
    }

    @Transactional
    public void priseAnswer(AnswerDTO answerDTO) {
        Integer count = answerMapper.priseAnswer(answerDTO);
        if(count!=null&&count.intValue()==1){
            BehaviorDO behaviorDO = new BehaviorDO();
            behaviorDO.setUserID(answerDTO.getUserID());
            behaviorDO.setBehaviorType(BehaviorType.praise);
            behaviorDO.setAnswerID(answerDTO.getId());
            synchronized (lock) {
                BehaviorDO behavior = behaviorService.queryByAnswerIdAndUserId(behaviorDO);
                if(behavior!=null){
                    throw  new BusinessException("您对此回答已经进行过操作！");
                }else {
                    behaviorService.insert(behaviorDO);
                }
            }
        }else {
            throw  new BusinessException("没有此回答");
        }
    }

    public List<UserVO> queryUserKing() {
        List<UserVO> list = answerMapper.queryUserKing();
        List<UserVO> newList = new ArrayList<>();
        for (UserVO userVO : list) {
            //获取用户头像
            ImageDO imageDO = new ImageDO();
            imageDO.setImageType(ImageType.imageHead);
            imageDO.setIds(userVO.getUserID()+"");
            List<ImageDTO> imageDTOList = imageService.select(imageDO);
            if (imageDTOList != null && imageDTOList.size() > 0){
                userVO.setImageHead(imageDTOList.get(0).getThumbnailPath());
            }
            newList.add(userVO);
        }
        return newList;
    }

    public AnswerVO queryById(Long answerID) {
        return answerMapper.queryById(answerID);
    }

    public Page<AnswerVO> queryUserAnswerList(AnswerDTO answerDTO) {
        Page<AnswerVO> page = new Page<>(answerDTO.getPageNumber(),answerDTO.getPageSize());
        Integer count = answerMapper.countByUserId(answerDTO.getUserID());
        page.setEntityTotal(count);
        List<AnswerVO> newList = new ArrayList<>();
        if(count!=null&&count.intValue()>0){
            List<AnswerVO> list = answerMapper.queryUserAnswerList(answerDTO);
            for (AnswerVO answerVO : list) {
                QuestionVO questionVO = questionService.queryQuestionDetails(answerVO.getQuestionID());
                answerVO.setQuestionTitle(questionVO.getQuestionTitle());
                answerVO.setQuestionTime(questionVO.getCreateTime());
                answerVO.setQuestionArea(questionVO.getProvinceText());
                UserVO user = questionVO.getUser();
                answerVO.setQuestionerImageHead(user.getImageHead());
                answerVO.setQuestionerNickName(user.getNickName());

                newList.add(answerVO);
            }
        }
        page.setData(newList);
        return page;
    }

    public Page<AnswerVO> adminQueryUserAnswerList(AnswerDTO answerDTO) {
        Page<AnswerVO> page = new Page<>(answerDTO.getPageNumber(),answerDTO.getPageSize());
        Integer count = answerMapper.adminCountByUserId(answerDTO.getUserID());
        page.setEntityTotal(count);
        List<AnswerVO> newList = new ArrayList<>();
        if(count!=null&&count.intValue()>0){
            List<AnswerVO> list = answerMapper.adminQueryUserAnswerList(answerDTO);
            for (AnswerVO answerVO : list) {
                QuestionVO questionVO = questionService.queryQuestionDetails(answerVO.getQuestionID());
                answerVO.setQuestionTitle(questionVO.getQuestionTitle());
                answerVO.setQuestionTime(questionVO.getCreateTime());
                answerVO.setQuestionArea(questionVO.getProvinceText());
                UserVO user = questionVO.getUser();
                answerVO.setQuestionerImageHead(user.getImageHead());
                answerVO.setQuestionerNickName(user.getNickName());

                newList.add(answerVO);
            }
        }
        page.setData(newList);
        return page;
    }
    public Integer countByUserId(Long userID) {
        return answerMapper.countByUserId(userID);
    }

    public List<AnswerVO> queryAnswerPraiseByUserId(Long userID) {
        return answerMapper.queryAnswerPraiseByUserId(userID);
    }

    public AnswerVO queryByAnswerId(Long answerId) {
        AnswerVO answerVO = answerMapper.queryById(answerId);
        UserVO userVO = userService.query(answerVO.getUserID());
        answerVO.setNickName(userVO.getNickName());
        QuestionVO questionVO = questionService.queryById(answerVO.getQuestionID());
        answerVO.setQuestionTitle(questionVO.getQuestionTitle());
        Integer comment = commentService.countByAnswerId(answerId);
        answerVO.setCommentNum(comment);
        //查询回答的图片
        ImageDO imageDO = new ImageDO();
        imageDO.setIds(answerVO.getId().toString());
        imageDO.setImageType(ImageType.answer);
        List<ImageDTO> imageVOS = imageService.select(imageDO);
        answerVO.setImages(imageVOS);
        //查询用户的头像
        imageDO.setIds(answerVO.getUserID().toString());
        imageDO.setImageType(ImageType.imageHead);
        List<ImageDTO> imageHead = imageService.select(imageDO);
        if(imageHead!=null&&imageHead.size()>0){
            answerVO.setImageHead(imageHead.get(0).getThumbnailPath());
        }
        return  answerVO;
    }

    /**
     * 修改回答屏蔽状态和内容
     * @param answerVO
     * @return
     */
    public Integer shielding(AnswerVO answerVO){
        return answerMapper.shielding(answerVO);
    }

    /**
     * 管理员管理回答页面
     * @param query
     * @return
     */
    public Page<AnswerVO> queryAdminAnswerList(AnswerDTO query) {
        //搜索关键字
        if(null != query.getKeyword() && !"".equals(query.getKeyword())){
            String keyWord = query.getKeyword();
            query.setKeyword("%"+keyWord+"%");
        }
        //开始时间
        if (null != query.getStartDate() && "" != query.getStartDate()) {
            String startDate = query.getStartDate();
            query.setStartDate(startDate + " 00:00:00");
        }
        //结束时间
        if (null != query.getEndDate() && "" != query.getEndDate()) {
            String endDate = query.getEndDate();
            query.setEndDate(endDate + " 23:59:59");
        }


        Page<AnswerVO> page = new Page<>(query.getPageNumber(), query.getPageSize());
        Integer count = answerMapper.queryAdminAnswerCount(query);
        page.setEntityTotal(count);
        if (count != null && count > 0) {
            List<AnswerVO> answerVOS = answerMapper.queryAdminAnswerList(query);
            page.setData(answerVOS);
        }
        return page;
    }
    public Integer deleteQuestionById(AnswerVO answerVO) {
        return answerMapper.updateDeletedById(answerVO);
    }

}
