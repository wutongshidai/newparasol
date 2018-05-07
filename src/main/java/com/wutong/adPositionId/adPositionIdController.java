package com.wutong.adPositionId;

import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import com.parasol.core.adPositionId.AdPositionId;
import com.parasol.core.service.AdPositionIdService;
import com.wutong.framework.core.web.common.http.ResponseResult;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/adPositionId")
public class adPositionIdController {



    @Reference
    private AdPositionIdService adPositionIdService;

    @RequestMapping(path="/getAdList")
    public ResponseResult getAdList(Integer adId,String levels,Integer pageNo,Integer pageSize) {
        ResponseResult result = new ResponseResult();
        PageInfo<AdPositionId> list = adPositionIdService.list(adId, levels, pageNo, pageSize);
        result.addData(list);
        return result;
    }
    @RequestMapping(path="/addAd")
    public ResponseResult addAd(AdPositionId adPositionId) {
        ResponseResult result = new ResponseResult();
        Map  map1 = adPositionIdService.addAd(adPositionId);
        result.addData(map1);
        return result;
    }
    @RequestMapping(path="/deleteAd")
    public ResponseResult deleteAd(Integer adId) {
        ResponseResult result = new ResponseResult();
        boolean b = adPositionIdService.deleteAd(adId);
        Map map = new HashMap();
        map.put("success",b);
        result.addData(map);
        return result;
    }

    @RequestMapping(path="/upAd")
    public ResponseResult upAd(AdPositionId adPositionId) {
        ResponseResult result = new ResponseResult();
        boolean b = adPositionIdService.upAd(adPositionId);
        Map map1 = new HashMap();
        map1.put("success",b);
        result.addData(map1);
        return result;
    }
}
