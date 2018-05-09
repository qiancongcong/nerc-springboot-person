package com.bjnlmf.nerc.zhihu.controller;

import com.bjnlmf.nerc.zhihu.enumeration.ImageType;
import com.bjnlmf.nerc.zhihu.pojo.image.ImageDTO;
import com.bjnlmf.nerc.zhihu.pojo.share.ShareExcessiveVO;
import com.bjnlmf.nerc.zhihu.service.ImageService;
import com.bjnlmf.nerc.zhihu.util.ResponseJson;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/share")
@Api(description = "分享")
public class ShareController {

    @Autowired
    private ImageService imageService;

    @RequestMapping(value = "/excessive",method = RequestMethod.GET)
    @ApiOperation("分享过度页面信息")
    public ResponseJson<ShareExcessiveVO> getShareExcessiveMessage(){
        ShareExcessiveVO shareExcessiveVO = new ShareExcessiveVO();
        shareExcessiveVO.setShareTitle("能魔答人，光伏知识的智囊团！");
        shareExcessiveVO.setShareContent("来嘛，英雄！做我心目中的光伏超人，能魔答人海量光伏知识等你获取。");
        ImageDTO imageDTO = imageService.selectLogo(ImageType.logo);
        if (imageDTO != null) {
            shareExcessiveVO.setLogo(imageDTO.getPath());
        }
        return ResponseJson.ok(shareExcessiveVO);
    }
}
