package com.bjnlmf.nerc.zhihu.service;

import com.bjnlmf.nerc.zhihu.mapper.AreaMapper;
import com.bjnlmf.nerc.zhihu.pojo.area.AreaVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AreaService {

    @Autowired
    private AreaMapper areaMapper;

    public List<AreaVO> queryListProvince() {
        return areaMapper.queryListProvince();
    }

    public List<AreaVO> queryListCityOrCounty(Long id) {
        return areaMapper.queryListCityOrCounty(id);
    }

    public AreaVO queryByAreaCode(String areaCode) {
        return areaMapper.queryByAreaCode(areaCode);
    }

    public Map<String,Object> queryAreas() {
        // TODO 查询全国的所有地区
        List<AreaVO> areas = areaMapper.selectByArea();
        Map<String, Object> areaMap = new HashMap<>();
        List<Map<String, Object>> areaList = new ArrayList<>();
        List<AreaVO> remaining = new ArrayList<>();
        List<AreaVO> provinces = new ArrayList<>();  //省的List
        for (AreaVO area : areas) {
            if(area.getParentId() == null){  //当parentd为空的时候 说明area为省的信息
                provinces.add(area);
            }else {
                remaining.add(area); //把不是省的添加到一个新的List中
            }
        }
        for (AreaVO province : provinces) {
            Map<String, Object> provincesMap = new HashMap<>();
            provincesMap.put("province", province.getAreaName());
            provincesMap.put("areaCode", province.getAreaCode());
            List<Map<String, Object>> city = new ArrayList<>();  //市的List
            for (AreaVO area : remaining) {

                Map<String, Object> cityMap = new HashMap<>();
                //如果parentId等于省的Id  则说明这是这个省的一个市
                if(area.getParentId().equals(province.getId())){
                    List<Map<String, Object>> country;  //区县的List
                    country = new ArrayList<>();
                    cityMap.put("city", area.getAreaName());
                    cityMap.put("areaCode", area.getAreaCode());
                    //如果parentId等于市的Id  则说明这是这个市的一个区
                    remaining.stream().filter(area2 -> area2.getParentId().equals(area.getId())).forEach(area2 -> {
                        Map<String, Object> countryMap = new HashMap<>();
                        countryMap.put("area", area2.getAreaName());
                        countryMap.put("areaCode", area2.getAreaCode());
                        country.add(countryMap);
                    });
                    //把区的List添加打市的map中
                    cityMap.put("areas", country);
                }
                //把市的List添加打市的map中
                if(!cityMap.isEmpty()){
                    city.add(cityMap);
                }
            }
            provincesMap.put("citys", city);
            areaList.add(provincesMap);
        }
        areaMap.put("all", areaList);
        return areaMap;
    }
}
