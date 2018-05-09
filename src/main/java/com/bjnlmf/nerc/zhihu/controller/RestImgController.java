package com.bjnlmf.nerc.zhihu.controller;

import com.bjnlmf.nerc.zhihu.config.OSSConfig;
import com.bjnlmf.nerc.zhihu.enumeration.ImageType;
import com.bjnlmf.nerc.zhihu.exception.BusinessException;
import com.bjnlmf.nerc.zhihu.pojo.UserVO;
import com.bjnlmf.nerc.zhihu.pojo.image.ImageDO;
import com.bjnlmf.nerc.zhihu.pojo.image.ImageVO;
import com.bjnlmf.nerc.zhihu.service.ImageService;
import com.bjnlmf.nerc.zhihu.service.OperationLogService;
import com.bjnlmf.nerc.zhihu.service.UserService;
import com.bjnlmf.nerc.zhihu.util.OSSImgUitl;
import com.bjnlmf.nerc.zhihu.util.ResponseJson;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * @Prject: nmPerson
 * @Package: com.bjnlmf.nerc.zhihu.controller
 * @Description: 图片上传,下载
 * @author: cfQiao
 * @date: 2018/1/18 16:45
 */

@Api(description = "图片")
@RestController
@RequestMapping("/image")
public class RestImgController {
    
    @Autowired
    private OSSConfig ossConfig;

    @Autowired
    private ImageService imageService;

    @Autowired
    private UserService userService;

    @Autowired
    private OperationLogService operationLogService;

    @ApiOperation(value = "上传图片")
    @PostMapping(value = "/upload", consumes = "multipart/*", headers = "content-type=multipart/form-data")
    public ResponseJson<ImageVO> uploadImg(
            @ApiParam(value = "userToken",required = true)
            @RequestHeader String userToken,
            @ApiParam(value = "图片类型",required = true)
            @RequestParam ImageType imageType,
            @ApiParam(value = "图片",required = true) MultipartFile image) {

        UserVO user = userService.queryByUserToken(userToken);

        //获得文件后缀
        String fileName = image.getOriginalFilename();
        String suffix = image.getOriginalFilename().substring(fileName.lastIndexOf(".") + 1);

        if("PNG".equalsIgnoreCase(suffix) || "JPG".equalsIgnoreCase(suffix) || "JPEG".equalsIgnoreCase(suffix)){
            //上传
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
            String namePath = user.getUserID() +"_"+ simpleDateFormat.format(new Date()) + "_" + fileName;
            String imageName = imageType.name() +"/" + namePath;
            OSSImgUitl.uploadImg(image, imageName, ossConfig);
            
            //获取原图路径
            //String path = OSSImgUitl.getImg(imageName, null, ossConfig);

            //获取缩略图路径
            //图片处理样式
            /*String style="";
            if (imageType.equals(ImageType.imageHead)) {
                style = "image/resize,w_40,p_50";
            }else{
                style = "image/resize,w_200,p_50";
            }
            String thumbnailPath = OSSImgUitl.getImg(imageName, style, ossConfig);*/
            
            //保存数据库
            
            //原图路径
            String path = imageType.name() + "?" + "image_name=" + namePath;
            
            //缩略图路径
            String imageStyle="";
            if (imageType.equals(ImageType.imageHead)) {
                imageStyle = "resize,w_40,p_50";
            }else{
                imageStyle = "resize,w_200,p_50";
            }
            String thumbnailPath = imageType.name() + "?" + "image_name=" + namePath + "&image_style=" + imageStyle;
            
            ImageDO imageDO = new ImageDO();
            imageDO.setImageType(imageType);
            imageDO.setPath(path);
            imageDO.setThumbnailPath(thumbnailPath);
            imageDO.setImageName(fileName);
            imageDO.setCreator(user.getNickName());
            //imageDO.setCreator("system");
            imageDO.setUpdator(user.getNickName());
            //imageDO.setUpdator("system");
            ImageVO imageVO = imageService.save(imageDO);
            return ResponseJson.ok(imageVO);
        }else {
            throw new BusinessException("只能上传PNG,JPG,JPEG格式的文件");
        }
        
    }

    @ApiOperation(value = "获取图片")
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public String getImg(
            @ApiParam(value = "userToken",required = true)
            @RequestHeader String userToken,
            @ApiParam(value = "图片名称", required = true)
            @RequestParam String key){

        return OSSImgUitl.getImg(key, null, ossConfig);
    }


}
