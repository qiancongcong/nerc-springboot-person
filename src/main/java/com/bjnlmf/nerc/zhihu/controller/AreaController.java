package com.bjnlmf.nerc.zhihu.controller;

import com.bjnlmf.nerc.zhihu.pojo.area.AreaVO;
import com.bjnlmf.nerc.zhihu.service.AreaService;
import com.bjnlmf.nerc.zhihu.util.ResponseJson;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("area")
@Api(description = "地区")
public class AreaController {

    @Autowired
    private AreaService areaService;

    @RequestMapping(value = "/query" , method = RequestMethod.POST)
    @ApiOperation(value = "查询全国所有的地区")
    public ResponseJson<Map<String, Object>> queryArea(){
        Map<String, Object> map = areaService.queryAreas();
        return ResponseJson.ok(map);
    }

    @RequestMapping(value = "query_List_Province",method=RequestMethod.GET)
    @ApiOperation("获取所有省")
    public ResponseJson<List<AreaVO>> queryListProvince(){
        List<AreaVO> provinces = areaService.queryListProvince();
        return ResponseJson.ok(provinces);
    }

    @RequestMapping(value = "query_List_city",method=RequestMethod.GET)
    @ApiOperation("根据省ID获取所有市")
    public ResponseJson<List<AreaVO>> queryListCity(
            @ApiParam(value = "省ID",required = true)
            @RequestParam Optional<Long> id){
        List<AreaVO> provinces = areaService.queryListCityOrCounty(id.get());
        return ResponseJson.ok(provinces);
    }

    @RequestMapping(value = "query_list_county" , method = RequestMethod.GET)
    @ApiOperation("根据市ID获取所有区县")
    public ResponseJson<List<AreaVO>> queryListCounty(
            @ApiParam(value = "市ID",required = true)
            @RequestParam Optional<Long> id){
        List<AreaVO> provinces = areaService.queryListCityOrCounty(id.get());
        return ResponseJson.ok(provinces);
    }
}
