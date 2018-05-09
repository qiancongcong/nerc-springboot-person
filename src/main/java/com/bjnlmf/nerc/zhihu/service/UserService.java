package com.bjnlmf.nerc.zhihu.service;

import com.bjnlmf.nerc.zhihu.enumeration.ImageType;
import com.bjnlmf.nerc.zhihu.pojo.answer.AnswerVO;
import com.bjnlmf.nerc.zhihu.pojo.image.ImageDO;
import com.bjnlmf.nerc.zhihu.pojo.image.ImageDTO;
import com.bjnlmf.nerc.zhihu.pojo.question.QuestionVO;
import com.bjnlmf.nerc.zhihu.util.HttpClientUtil;
import com.bjnlmf.nerc.zhihu.exception.BusinessException;
import com.bjnlmf.nerc.zhihu.mapper.UserMapper;
import com.bjnlmf.nerc.zhihu.pojo.UserDTO;
import com.bjnlmf.nerc.zhihu.pojo.UserVO;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    static Object lock = new Object();
    @Value("${merc.path}")
    private  String path;

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ImageService imageService;
    @Autowired
    private QuestionService questionService;
    @Autowired
    private AnswerService answerService;

    private Logger logger = LoggerFactory.getLogger(UserService.class);

    //用户登录
    @Transactional
    public UserVO login(UserDTO userDTO){
        JSONObject params = new JSONObject();
        params.put("phone",userDTO.getUserName());
        params.put("password",userDTO.getPassWord());
        UserVO userVO = new UserVO();
        String string = null;
        logger.info("phone:"+userDTO.getUserName());
        logger.info("password:"+userDTO.getPassWord());
        try {
            //HttpClient请求登录数据
            string = HttpClientUtil.postParameters(path+"/user/login",params.toString(),600000,600000);
        } catch (Exception e) {
            try {
                string = HttpClientUtil.postParameters(path+"/user/login",params.toString(),600000,600000);
            } catch (Exception e1) {
                logger.error(e1.getMessage());
                throw new RuntimeException(e1.getMessage());
                //BusinessException(e1.getMessage());
            }
        }
        if(!StringUtils.isEmpty(string)){
            params = JSONObject.fromObject(string);
            //判断是否登录成功
            Object object =params.get("retCode");
            if(object!=null){
                if(!object.toString().equals("200")){
                    throw new BusinessException(params.get("message").toString());
                }
            }else {
                throw new BusinessException("登录接口链接超时");
            }
            //登录成功后添加返回实体
            params = params.getJSONObject("data");
            userVO.setUserID(Long.valueOf(params.get("id").toString()));
            userVO.setUserToken(params.get("userToken").toString());
            userVO.setPhone(params.get("phone").toString());
            userVO.setAgent(params.get("agent").toString());
            object = params.get("nickName");
            if(object!=null&&!StringUtils.isEmpty(object.toString())){
                userVO.setNickName(object.toString());
            }else {
                String phone =params.get("phone").toString();
                String nickName = phone;
                if(phone.length() >= 11){
                    nickName = phone.substring(0,3)+"****"+phone.substring(7,11);
                }
                userVO.setNickName(nickName);
            }
            object = params.get("thumbnailPath");

            if(object!=null){
                userVO.setImageHead(object.toString());
            }
            //检测本数据是否有此用户信息
            UserVO user= new UserVO();
            synchronized (lock) {
                user = query(userVO.getUserID());
                if (user == null) {
                    insert(userVO);
                    if (!StringUtils.isEmpty(userVO.getImageHead())) {
                        ImageDO imageDO = new ImageDO();
                        imageDO.setRelationId(userVO.getUserID());
                        imageDO.setThumbnailPath(userVO.getImageHead());
                        imageDO.setPath(userVO.getImageHead());
                        imageDO.setImageType(ImageType.imageHead);
                        imageService.insert(imageDO);
                    }
                }
            }
            if (user != null) {
                //如果用户的nickName有变化更改
                if (!userVO.getNickName().equals(user.getNickName())) {
                    updateNickName(userVO);
                }
                if (!StringUtils.isEmpty(userVO.getImageHead())) {
                    ImageDO imageDO = new ImageDO();
                    imageDO.setImageType(ImageType.imageHead);
                    imageDO.setIds(user.getUserID() + "");
                    List<ImageDTO> list = imageService.select(imageDO);
                    if (list != null && list.size() > 0) {
                        if (!list.get(0).getThumbnailPath().equals(userVO.getImageHead())) {
                            imageDO.setId(list.get(0).getId());
                            imageDO.setThumbnailPath(userVO.getImageHead());
                            imageDO.setPath(userVO.getImageHead());
                            imageService.updateThumbnailPathById(imageDO);
                        }
                    } else {
                        imageDO.setRelationId(userVO.getUserID());
                        imageDO.setThumbnailPath(userVO.getImageHead());
                        imageDO.setPath(userVO.getImageHead());
                        imageService.insert(imageDO);
                    }

                }
            }
        }else {
            throw new BusinessException("登录失败，请重新登录！");
        }

        return userVO;
    }


    public UserVO query(Long userID){
        UserVO userVO = userMapper.queryByUserID(userID);
        return userVO;
    }

    //向本数据库插入用户数据
    private void insert(UserVO userVO){
        userMapper.insert(userVO);
    }

    //根据用户的ID修改nickName
    private void updateNickName(UserVO userVO){
        userMapper.updateNickName(userVO);
    }

    //根据用户的ID修改用户的电话
    private void updatePhone(UserVO userVO) {
        userMapper.updatePhone(userVO);
    }

    //根据userToken去能魔取用户的Id
    @Transactional
    public UserVO queryByUserToken(String  userToken){
        UserVO userVO = new UserVO();
        String string = "{}";
        try {
            //HttpClient请求登录数据
            string = HttpClientUtil.get(path+"/rest/v1/security/check_token?userToken="+userToken,600000,600000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        JSONObject result = JSONObject.fromObject(string);
        if(result!=null){
            //判断是否登录成功
            Object object =result.get("retCode");
            if(object!=null){
                if(!object.toString().equals("200")){
                    throw new BusinessException(result.get("message").toString());
                }
                result = result.getJSONObject("data");
                userVO.setUserID(Long.valueOf(result.get("id").toString()));
                userVO.setUserToken(result.get("userToken").toString());
                userVO.setPhone(result.get("phone").toString());
                userVO.setAgent(result.get("agent").toString());
                object = result.get("nickName");
                if(object!=null&&!StringUtils.isEmpty(object.toString())){
                    userVO.setNickName(object.toString());
                }else {
                    String phone =result.get("phone").toString();

                    String nickName = phone;
                    if(phone.length() >= 11){
                        nickName = phone.substring(0,3)+"****"+phone.substring(7,11);
                    }
                    userVO.setNickName(nickName);
                }

                JSONObject params = new JSONObject();
                params.put("imageType","profile");
                params.put("relationId",userVO.getUserID());
                String images = "";
                try {
                    //HttpClient请求登录数据
                    images = HttpClientUtil.post(path+"/userInfo/query_image/",params.toString(),600000,600000);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                JSONObject result1 = JSONObject.fromObject(images);
                if(result1!=null) {
                    //判断是否登录成功
                    Object retCode = result1.get("retCode");
                    if (retCode != null) {
                        if(!retCode.toString().equals("200")){
                            throw new BusinessException(result1.get("message").toString());
                        }
                        result1 = result1.getJSONObject("data");
                        if (result1 != null&&result1.size()>0) {
                            object = result1.get("thumbnailPath");
                            if(object!=null){
                                userVO.setImageHead(object.toString());
                            }
                        }
                    }
                }
                //检测本数据是否有此用户信息
                UserVO user = new UserVO();
                synchronized (lock) {
                    user = query(userVO.getUserID());
                    userVO.setState(user.getState());
                    if (user == null) {
                        insert(userVO);
                        if (!StringUtils.isEmpty(userVO.getImageHead())) {
                            ImageDO imageDO = new ImageDO();
                            imageDO.setRelationId(userVO.getUserID());
                            imageDO.setThumbnailPath(userVO.getImageHead());
                            imageDO.setPath(userVO.getImageHead());
                            imageDO.setImageType(ImageType.imageHead);
                            imageService.insert(imageDO);
                        }
                    }
                }
                if(user!=null){
                    //如果用户的nickName有变化更改
                    if(!userVO.getNickName().equals(user.getNickName())){
                        updateNickName(userVO);
                    }
                    if(!StringUtils.isEmpty(userVO.getImageHead())){
                        ImageDO imageDO = new ImageDO();
                        imageDO.setImageType(ImageType.imageHead);
                        imageDO.setIds(user.getUserID()+"");
                        List<ImageDTO> list = imageService.select(imageDO);
                        if (list!=null&&list.size()>0){
                            if(!list.get(0).getThumbnailPath().equals(userVO.getImageHead())){
                                imageDO.setId(list.get(0).getId());
                                imageDO.setThumbnailPath(userVO.getImageHead());
                                imageDO.setPath(userVO.getImageHead());
                                imageService.updateThumbnailPathById(imageDO);
                            }
                        }else {
                            imageDO.setRelationId(userVO.getUserID());
                            imageDO.setThumbnailPath(userVO.getImageHead());
                            imageDO.setPath(userVO.getImageHead());
                            imageService.insert(imageDO);
                        }
                    }
                }
            }else {
                throw new BusinessException("登录接口链接超时");
            }
        }else {
            throw new BusinessException("登录接口链接超时");
        }
        return userVO;
    }

    public UserVO queryUserDetail(Long userId) {
        UserVO user = userMapper.queryByUserID(userId);
        //设置用户昵称
        if (user.getNickName() == null || "".equals(user.getNickName())){
            user.setNickName(user.getPhone().substring(0,3)+"****"+user.getPhone().substring(7,11));
        }
        //查询获取用户头像
        ImageDO imageDO = new ImageDO();
        imageDO.setImageType(ImageType.imageHead);
        imageDO.setIds(user.getUserID()+"");
        List<ImageDTO> list = imageService.select(imageDO);
        if (list!=null&&list.size()>0){
            user.setImageHead(list.get(0).getThumbnailPath());
        }
        //查询用户所有提交问题的浏览数之和
        List<QuestionVO> readNumList = questionService.queryquestionReadNumByUserId(userId);
        Integer obtainBrowsedNum = 0;
        if (readNumList != null && readNumList.size() > 0) {
            for (QuestionVO questionVO : readNumList) {
                obtainBrowsedNum = obtainBrowsedNum + questionVO.getReadNum();
            }
        }
        user.setObtainBrowsedNum(obtainBrowsedNum);
        //查询用户所有回答被点赞数
        List<AnswerVO> praiseNumList = answerService.queryAnswerPraiseByUserId(userId);
        Integer obtainPraiseNum = 0;
        if (praiseNumList != null && praiseNumList.size() > 0){
            for (AnswerVO answerVO : praiseNumList) {
                obtainPraiseNum = obtainPraiseNum + answerVO.getPraise();
            }
        }
        user.setObtainPraiseNum(obtainPraiseNum);
        return  user;
    }


    public  void modifyPassword(String password,String newPassword,String userToken){
        UserVO userVO = new UserVO();
        String string = "{}";
        JSONObject params = new JSONObject();
        params.put("newPassword",newPassword);
        params.put("password",password);

        try {
            //HttpClient请求登录数据
            string = HttpClientUtil.postParameters(path+"/user/password?newPassword="+newPassword+"&password="+password,params.toString(),600000,600000,userToken);
        } catch (Exception e) {
            e.printStackTrace();
        }
        params = JSONObject.fromObject(string);
        //判断是否登录成功
        Object object =params.get("retCode");
        if(object!=null){
            if(!object.toString().equals("200")){
                throw new BusinessException(params.get("message").toString());
            }
        }else {
            throw new BusinessException("修改密码接口链接超时");
        }
    }
}
