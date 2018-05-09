package com.bjnlmf.nerc.zhihu.mapper;

import com.bjnlmf.nerc.zhihu.pojo.area.AreaVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AreaMapper {

    @Select(" select id,areaName,areaCode,parentId from conf_area where parentId is null")
    List<AreaVO> queryListProvince();

    @Select("select id,areaName,areaCode,parentId from conf_area where parentId = #{id}")
    List<AreaVO> queryListCityOrCounty(Long id);

    @Select("select id,areaName,areaCode,parentId from conf_area where areaCode = #{areaCode}")
    AreaVO queryByAreaCode(String areaCode);

    @Select("select id,areaName,areaCode,parentId from conf_area")
    List<AreaVO> selectByArea();
}
