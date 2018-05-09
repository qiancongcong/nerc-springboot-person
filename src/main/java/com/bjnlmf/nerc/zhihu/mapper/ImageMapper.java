package com.bjnlmf.nerc.zhihu.mapper;

import com.bjnlmf.nerc.zhihu.enumeration.ImageType;
import com.bjnlmf.nerc.zhihu.pojo.answer.AnswerDTO;
import com.bjnlmf.nerc.zhihu.pojo.image.ImageDO;
import com.bjnlmf.nerc.zhihu.pojo.image.ImageDTO;
import com.bjnlmf.nerc.zhihu.pojo.image.ImageVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ImageMapper {

    @Update("update sys_images set relationId = #{id} where id in (${imageIds}) and deleted = 0")
    void saveRelationId(AnswerDTO answerDTO);

    @Select("select id, relationId,imageType,path,thumbnailPath,imageName,shielding from sys_images where imageType = #{imageType} and relationId in (${ids}) and deleted = 0")
    List<ImageDTO> selectByRelationId(ImageDO imageDO);

    @Update("update sys_images set relationId = #{id} where id in (${imageIds}) and deleted = 0")
    void updateRelationId(@Param("id") String id, @Param("imageIds") String imageIds);

    @Insert("insert into sys_images(imageType,path,relationId,thumbnailPath,imageName,creator,updator) " +
            "values(#{imageType},#{path},#{relationId},#{thumbnailPath},#{imageName},#{creator},#{updator})")
    @Options(keyColumn="id",useGeneratedKeys=true)
    void insert(ImageDO imageDO);

    @Update("update sys_images set path = #{path},thumbnailPath=#{thumbnailPath} where id = #{id} and deleted = 0")
    void updateThumbnailPathById(ImageDO imageDO);

    @Select("select id,imageType,path,relationId,thumbnailPath,imageName from sys_images where id = #{id} and deleted = 0")
    ImageVO selectImageByImageId(Long id);

    @Select("select id, relationId,imageType,path,thumbnailPath,imageName from sys_images where imageType = #{imageType} and deleted = 0")
    ImageDTO selectLogo(ImageType logo);

    /**
     * 修改图片屏蔽状态
     * @param imageDO
     * @return
     */
    @Update("update sys_images set shielding = #{shielding} where id in (${ids}) and imageType = 'question' and relationId= #{relationId}")
    Integer shielding(ImageDO imageDO);

    @Update("update sys_images set shielding = #{shielding} where imageType = 'question' and relationId= #{relationId}")
    Integer updateShielding(ImageDO imageDO);
}
