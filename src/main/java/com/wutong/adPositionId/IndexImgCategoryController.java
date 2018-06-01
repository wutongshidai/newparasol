package com.wutong.adPositionId;

import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import com.parasol.core.adPositionId.AdPositionId;
import com.parasol.core.adPositionId.IndexImgCategory;
import com.parasol.core.service.AdPositionIdService;
import com.parasol.core.service.IndexImgCategoryService;
import com.wutong.framework.core.web.common.http.ResponseResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/indexImgCategory")
public class IndexImgCategoryController {

    @Reference
    private IndexImgCategoryService indexImgCategoryService;


    @RequestMapping(path="/addId")
    public ResponseResult addAd(IndexImgCategory record) {
        ResponseResult result = new ResponseResult();
        int i = indexImgCategoryService.addAd(record);
        Map map = new HashMap();
        map.put("success",i);
        result.addData(map);
        return result;
    }
    @RequestMapping(path="/deleteId")
    public ResponseResult deleteAd(Integer id) {
        ResponseResult result = new ResponseResult();
        int i = indexImgCategoryService.deleteId(id);
        Map map = new HashMap();
        map.put("success",i);
        result.addData(map);
        return result;
    }

    @RequestMapping(path="/updateId")
    public ResponseResult upAd(IndexImgCategory record) {
        ResponseResult result = new ResponseResult();
        int i = indexImgCategoryService.updateByExample(record);
        Map map = new HashMap();
        map.put("success",i);
        result.addData(map);
        return result;
    }

    @RequestMapping(path="/getIdList")
    public ResponseResult getAdList(Integer adId,Integer gdId,Integer pageNo,Integer pageSize) {
        ResponseResult result = new ResponseResult();
        PageInfo<IndexImgCategory> list = indexImgCategoryService.list(adId, gdId, pageNo, pageSize);
        result.addData(list);
        return result;
    }
}
