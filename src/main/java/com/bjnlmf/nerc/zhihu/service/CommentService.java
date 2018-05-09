package com.bjnlmf.nerc.zhihu.service;

import com.bjnlmf.nerc.zhihu.enumeration.BehaviorType;
import com.bjnlmf.nerc.zhihu.enumeration.ImageType;
import com.bjnlmf.nerc.zhihu.enumeration.MessageType;
import com.bjnlmf.nerc.zhihu.exception.BusinessException;
import com.bjnlmf.nerc.zhihu.mapper.CommentMapper;
import com.bjnlmf.nerc.zhihu.pojo.BehaviorDO;
import com.bjnlmf.nerc.zhihu.pojo.UserVO;
import com.bjnlmf.nerc.zhihu.pojo.answer.AnswerVO;
import com.bjnlmf.nerc.zhihu.pojo.comment.CommentDO;
import com.bjnlmf.nerc.zhihu.pojo.comment.CommentVO;
import com.bjnlmf.nerc.zhihu.pojo.image.ImageDO;
import com.bjnlmf.nerc.zhihu.pojo.image.ImageDTO;
import com.bjnlmf.nerc.zhihu.pojo.message.MessageDO;
import com.bjnlmf.nerc.zhihu.util.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Service
public class CommentService {

    static Object Lock = new Object();

    @Autowired
    private UserService userService;
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private AnswerService answerService;
    @Autowired
    private ImageService imageService;
    @Autowired
    private BehaviorService behaviorService;
    @Autowired
    private MessageService messageService;

    @Transactional
    public void addComment(CommentDO commentDO){
        commentMapper.insert(commentDO);
        Long commentToId = commentDO.getCommentsToId();
        answerService.addCommentNum(commentDO.getAnswerID());
        if(commentToId!=null){
            UserVO userVO = userService.query(commentToId);
            commentDO.setCommentsToBody(userVO.getNickName());
            CommentVO commentVO = commentMapper.queryById(commentDO.getCommentId());
            if(commentVO.getDialogToCommentId()!=null){
                if(commentDO.getUserId().equals(commentVO.getCommentsToId())){
                    commentDO.setDialogToCommentId(commentVO.getDialogToCommentId());
                }else {
                    commentDO.setDialogToCommentId(commentVO.getId());
                }
            }else {
                commentDO.setDialogToCommentId(commentVO.getId());
            }
            commentMapper.insertDailog(commentDO);
        }

        MessageDO messageDO = new MessageDO();
        messageDO.setAffiliatedQuestion(commentDO.getCommentsContent());
        messageDO.setAffiliatedID(commentDO.getUserId());
        if(commentDO.getCommentsToId()!=null){
            messageDO.setUserID(commentDO.getCommentsToId());
            messageDO.setSpecies(MessageType.reply);
        }else {
            AnswerVO answerVO =  answerService.queryById(commentDO.getAnswerID());
            messageDO.setSpecies(MessageType.comment);
            messageDO.setUserID(answerVO.getUserID());
        }
        messageDO.setQuestionId(commentDO.getQuestionID());
        messageDO.setAnswerId(commentDO.getAnswerID());
        messageService.save(messageDO);
    }

    public Page<CommentVO> adminListComment(CommentDO commentDO) {
        Integer count = commentMapper.count(commentDO);
        Page<CommentVO> page = new Page<>(commentDO.getPageNumber(),commentDO.getPageSize());
        page.setEntityTotal(count);
        if(count!=null&&count.intValue()>0){
            List<CommentVO> list = commentMapper.queryCommentList(commentDO);
            String userIds = "";
            String commentIds= "";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            for (CommentVO commentVO:list) {
                userIds += commentVO.getUserId() +",";
                commentIds +=  commentVO.getId() +",";
                commentVO.setCreateTimeStr(simpleDateFormat.format(commentVO.getCreateTime()));
            }
            if (userIds.length()>0){
                userIds = userIds.substring(0,userIds.length()-1);
                ImageDO imageDO = new ImageDO();
                imageDO.setImageType(ImageType.imageHead);
                imageDO.setIds(userIds);
                List<ImageDTO> imageDTOList = imageService.select(imageDO);
                List<CommentVO> newList = new ArrayList<>();
                for (CommentVO commentVO:list) {
                    for (ImageDTO imageDTO:imageDTOList) {
                        if(commentVO.getUserId().equals(imageDTO.getRelationId())){
                            commentVO.setImageHead(imageDTO.getThumbnailPath());
                            break;
                        }
                    }
                    newList.add(commentVO);
                }
                page.setData(newList);
            }
            if(commentDO.getUserId()!=null){
                if (commentIds.length()>0){
                    commentIds = commentIds.substring(0,commentIds.length()-1);
                }
                List<BehaviorDO> behaviorLists = behaviorService.select(commentDO.getUserId(),commentIds);
                List<CommentVO> newList = new ArrayList<>();
                for (CommentVO commentVO:page.getData()) {
                    for (BehaviorDO behaviorDO:behaviorLists) {
                        if(commentVO.getId().equals(behaviorDO.getAnswerID())){
                            commentVO.setBehaviorType(behaviorDO.getBehaviorType());
                            break;
                        }
                    }
                    newList.add(commentVO);
                }
                page.setData(newList);
            }
        }
        return page;
    }

    public Page<CommentVO> listComment(CommentDO commentDO) {
        Integer count = commentMapper.count(commentDO);
        Page<CommentVO> page = new Page<>(commentDO.getPageNumber(),commentDO.getPageSize());
        page.setEntityTotal(count);
        if(count!=null&&count.intValue()>0){
            List<CommentVO> list = commentMapper.queryCommentList(commentDO);
            String userIds = "";
            String commentIds= "";
            for (CommentVO commentVO:list) {
                userIds += commentVO.getUserId() +",";
                commentIds +=  commentVO.getId() +",";
            }
            if (userIds.length()>0){
                userIds = userIds.substring(0,userIds.length()-1);
                ImageDO imageDO = new ImageDO();
                imageDO.setImageType(ImageType.imageHead);
                imageDO.setIds(userIds);
                List<ImageDTO> imageDTOList = imageService.select(imageDO);
                List<CommentVO> newList = new ArrayList<>();
                for (CommentVO commentVO:list) {
                    for (ImageDTO imageDTO:imageDTOList) {
                        if(commentVO.getUserId().equals(imageDTO.getRelationId())){
                            commentVO.setImageHead(imageDTO.getThumbnailPath());
                            break;
                        }
                    }
                    if (commentVO.getShielding()){
                        commentVO.setCommentsContent("该内容已被屏蔽");
                    }
                    newList.add(commentVO);
                }
                page.setData(newList);
            }
            if(commentDO.getUserId()!=null){
                if (commentIds.length()>0){
                    commentIds = commentIds.substring(0,commentIds.length()-1);
                }
                List<BehaviorDO> behaviorLists = behaviorService.select(commentDO.getUserId(),commentIds);
                List<CommentVO> newList = new ArrayList<>();
                for (CommentVO commentVO:page.getData()) {
                    for (BehaviorDO behaviorDO:behaviorLists) {
                        if(commentVO.getId().equals(behaviorDO.getAnswerID())){
                            commentVO.setBehaviorType(behaviorDO.getBehaviorType());
                            break;
                        }
                    }
                    newList.add(commentVO);
                }
                page.setData(newList);
            }
        }
        return page;
    }

    @Transactional
    public void priseComment(CommentDO commentDO) {
        Integer count = commentMapper.priseComment(commentDO);
        if(count!=null&&count.intValue()==1){
            BehaviorDO behaviorDO = new BehaviorDO();
            behaviorDO.setUserID(commentDO.getUserId());
            behaviorDO.setBehaviorType(BehaviorType.praise);
            behaviorDO.setAnswerID(commentDO.getId());
            synchronized (Lock){
                BehaviorDO behavior = behaviorService.queryByCommentIdAndUserId(behaviorDO);
                if(behavior!=null){
                    throw  new BusinessException("您对此评论已经进行过操作！");
                }else {
                    behaviorService.insert(behaviorDO);
                }
            }
        }else {
            throw  new BusinessException("没有此评论");
        }
    }

    public List<CommentVO> dailogList(CommentDO commentDO) {
        CommentVO commentVO = commentMapper.queryById(commentDO.getId());
        List<CommentVO> list = commentMapper.dailogList(commentVO);
        //获取对话源的上级评论
        CommentVO commentVO1 = commentMapper.queryById(list.get(0).getDialogToCommentId());
        UserVO userVO = userService.query(commentVO1.getUserId());
        commentVO1.setNickName(userVO.getNickName());
        list.add(0,commentVO1);
        String userIds = "";
        for (CommentVO comment:list) {
            userIds += comment.getUserId() + ",";
        }
        userIds = userIds.substring(0,userIds.length()-1);
        ImageDO imageDO = new ImageDO();
        imageDO.setImageType(ImageType.imageHead);
        imageDO.setIds(userIds);
        List<ImageDTO> imageDTOList = imageService.select(imageDO);
        List<CommentVO> newList = new ArrayList<>();
        for (CommentVO comment1:list) {
            for (ImageDTO imageDTO:imageDTOList) {
                if(comment1.getUserId().equals(imageDTO.getRelationId())){
                    comment1.setImageHead(imageDTO.getThumbnailPath());
                    break;
                }
            }
            if(comment1 != null){
                if(comment1.getShielding()){
                    comment1.setCommentsContent("该内容已被屏蔽");
                }
            }

            newList.add(comment1);
        }
        return newList;
    }

    public Integer countByAnswerId(Long answerId) {
        CommentDO commentDO = new CommentDO();
        commentDO.setAnswerID(answerId);
        return commentMapper.count(commentDO);
    }

    /**
     * 修改评论屏蔽状态
     * @param commentDO
     * @return
     */
    public Integer shielding(CommentDO commentDO){
        return commentMapper.shielding(commentDO);
    }

    public Page<CommentVO> listCommentManage(CommentDO commentDO){
        Page<CommentVO> page = new Page<>(commentDO.getPageNumber(), commentDO.getPageSize());
        Integer count = commentMapper.countCommentList(commentDO);
        if(0 < count){
            List<CommentVO> list = commentMapper.listCommentManage(commentDO);
            if(null != list && 0 < list.size()){
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                for (CommentVO commentVO : list){
                    commentVO.setCreateTimeStr(simpleDateFormat.format(commentVO.getCreateTime()));
                }
            }
            page.setEntityTotal(count);
            page.setData(list);
        }
        return page;
    }
    public Page<CommentVO> adminListCommentManage(CommentDO commentDO){
        Page<CommentVO> page = new Page<>(commentDO.getPageNumber(), commentDO.getPageSize());
        Integer count = commentMapper.adminCountCommentList(commentDO);
        if(0 < count){
            List<CommentVO> list = commentMapper.adminListCommentManage(commentDO);
            if(null != list && 0 < list.size()){
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                for (CommentVO commentVO : list){
                    commentVO.setCreateTimeStr(simpleDateFormat.format(commentVO.getCreateTime()));
                }
            }
            page.setEntityTotal(count);
            page.setData(list);
        }
        return page;
    }
}
