package com.bjnlmf.nerc.zhihu.pojo.image;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter@Getter
public class ImageVO implements Serializable{
    private Long id ;
    private String imageType;
    private String path;
    private String thumbnailPath;
    private Long relationId;
    private String imageName;
    private Boolean shielding;//是否屏蔽
}
