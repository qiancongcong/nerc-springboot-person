package com.bjnlmf.nerc.zhihu.pojo.image;

import com.bjnlmf.nerc.zhihu.enumeration.ImageType;
import lombok.Data;

import java.io.Serializable;

@Data
public class ImageDO implements Serializable{
    private static final long serialVersionUID = 4958614025381258310L;

    private Long id ;
    private ImageType imageType;
    private String path;
    private String thumbnailPath;
    private Long relationId;
    private String imageName;
    private String creator;
    private String updator;
    private Integer deleted;
    private String ids;
    private Boolean shielding;//是否屏蔽
}
