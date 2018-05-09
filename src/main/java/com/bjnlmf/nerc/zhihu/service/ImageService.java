package com.bjnlmf.nerc.zhihu.service;

import com.bjnlmf.nerc.zhihu.enumeration.ImageType;
import com.bjnlmf.nerc.zhihu.mapper.ImageMapper;
import com.bjnlmf.nerc.zhihu.pojo.answer.AnswerDTO;
import com.bjnlmf.nerc.zhihu.pojo.image.ImageDO;
import com.bjnlmf.nerc.zhihu.pojo.image.ImageDTO;
import com.bjnlmf.nerc.zhihu.pojo.image.ImageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ImageService {

    @Autowired
    private ImageMapper imageMapper;

    public void saveRelationId(AnswerDTO answerDTO){

        imageMapper.saveRelationId(answerDTO);
    }

    public List<ImageDTO> select(ImageDO imageDO) {
        return  imageMapper.selectByRelationId(imageDO);
    }

    public void updateRelationId(String id, String ids) {
        imageMapper.updateRelationId(id,ids);
    }

    public void insert(ImageDO imageDO) {
        imageMapper.insert(imageDO);
    }

    public void updateThumbnailPathById(ImageDO imageDO) {
        imageMapper.updateThumbnailPathById(imageDO);
    }

    public ImageVO save(ImageDO imageDO) {
        imageMapper.insert(imageDO);
        return imageMapper.selectImageByImageId(imageDO.getId());
    }

    public ImageDTO selectLogo(ImageType logo) {
        return imageMapper.selectLogo(logo);
    }

    /**
     * 修改图片屏蔽状态
     * @param imageDO
     * @return
     */
    public  Integer shielding(ImageDO imageDO){
        return imageMapper.shielding(imageDO);
    }


    /**
     * 修改图片屏蔽状态
     * @param imageDO
     * @return
     */
    public  Integer updateShielding(ImageDO imageDO){
        return imageMapper.updateShielding(imageDO);
    }
}
