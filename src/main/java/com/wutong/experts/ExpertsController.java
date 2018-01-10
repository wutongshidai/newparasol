package com.wutong.experts;

import com.alibaba.dubbo.config.annotation.Reference;
import com.parasol.common.load.fileUpload;
import com.parasol.common.oss.OSSObjectUtils;
import com.parasol.common.utils.encoder.BASE64Decoder;
import com.parasol.core.experts.Experts;
import com.parasol.core.experts.ExpertsA;
import com.parasol.core.experts.ExpertsB;
import com.parasol.core.service.ExpertsService;
import com.parasol.core.user.User;
import com.wutong.framework.core.web.common.http.ResponseResult;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.codec.binary.Base64;
import org.fusesource.hawtbuf.ByteArrayInputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/experts")
public class ExpertsController {


    @Reference
    ExpertsService expertsService;


    @RequestMapping(value = "/experts", method = RequestMethod.POST)
    public String experts(Integer expertId, HttpServletRequest request,
                          HttpServletResponse response) throws Exception {
        Map<String, String> adminMap = new HashMap<String, String>();
        String saveExperts = "";
        User user = (User) request.getSession().getAttribute("user");
        System.out.println(user.toString());
        Experts experts = new Experts();
        experts.setUserId(user.getId());
        BeanUtils.populate(experts, request.getParameterMap());
        if (expertId == null) {
            if (null != experts.getPhoto() && "" != experts.getPhoto()) {
                InputStream inputStream = new ByteArrayInputStream(Base64.decodeBase64(experts.getPhoto()));
                OSSObjectUtils.uploadFile("wut1/1111.png", inputStream);
                String a = getUUIDName();
//    	    		fileUpload.saveImg(experts.getPhoto(), a);
                experts.setPhoto("../imgl/" + a + ".png");
            }
            saveExperts = expertsService.saveExperts(experts);
            if (saveExperts != "0") {
                Experts selectByUserId = expertsService.selectByUserId(user.getId());
                saveExperts = selectByUserId.getId().toString();
                //    		  adminMap.put("saveExperts", saveExperts);
                //    		  adminMap.put("id", experts.getId());
            }
            System.out.println("1111111111111111");
        } else {
            experts.setId(expertId);
            Experts experts2 = expertsService.selectByUserId(user.getId());


            //部分url部分二进制图判定
            if (!experts.getPhoto().equals(experts2.getPhoto())) {
                System.out.println(experts.getPhoto() + "-----------------------" + experts.getPhoto().toString());
                if (null != experts.getPhoto() && "" != experts.getPhoto()) {
                    String a = getUUIDName();
                    fileUpload.saveImg(experts.getPhoto(), a);
                    experts.setPhoto("../imgl/" + a + ".png");
                }
            }
            if (!experts.getShow0().equals(experts2.getShow0())) {
                if (null != experts.getShow0() && "" != experts.getShow0()) {
                    String a = getUUIDName();
                    fileUpload.saveImg(experts.getShow0(), a);
                    experts.setShow0("../imgl/" + a + ".png");
                }
            }
            if (!experts.getShow1().equals(experts2.getShow1())) {
                if (null != experts.getShow1() && "" != experts.getShow1()) {
                    String a = getUUIDName();
                    fileUpload.saveImg(experts.getShow1(), a);
                    experts.setShow1("../imgl/" + a + ".png");
                }
            }
            if (!experts.getShow2().equals(experts2.getShow2())) {
                if (null != experts.getShow2() && "" != experts.getShow2()) {
                    String a = getUUIDName();
                    fileUpload.saveImg(experts.getShow2(), a);
                    experts.setShow2("../imgl/" + a + ".png");
                }
            }
            if (!experts.getShow3().equals(experts2.getShow3())) {
                if (null != experts.getShow3() && "" != experts.getShow3()) {
                    String a = getUUIDName();
                    fileUpload.saveImg(experts.getShow3(), a);
                    experts.setShow3("../imgl/" + a + ".png");
                }
            }
            if (!experts.getShow4().equals(experts2.getShow4())) {
                if (null != experts.getShow4() && "" != experts.getShow4()) {
                    String a = getUUIDName();
                    fileUpload.saveImg(experts.getShow4(), a);
                    experts.setShow4("../imgl/" + a + ".png");
                }
            }
            if (!experts.getShow5().equals(experts2.getShow5())) {
                if (null != experts.getShow5() && "" != experts.getShow5()) {
                    String a = getUUIDName();
                    fileUpload.saveImg(experts.getShow5(), a);
                    experts.setShow5("../imgl/" + a + ".png");
                }
            }
            if (!experts.getShow6().equals(experts2.getShow6())) {
                if (null != experts.getShow6() && "" != experts.getShow6()) {
                    String a = getUUIDName();
                    fileUpload.saveImg(experts.getShow6(), a);
                    experts.setShow6("../imgl/" + a + ".png");
                }
            }
            if (!experts.getShow7().equals(experts2.getShow7())) {
                if (null != experts.getShow7() && "" != experts.getShow7()) {
                    String a = getUUIDName();
                    fileUpload.saveImg(experts.getShow7(), a);
                    experts.setShow7("../imgl/" + a + ".png");
                }
            }
            Integer.toString(expertsService.updateByPrimaryKey(experts));
            saveExperts = expertId + "";
            System.out.println(experts);
            adminMap.put("saveExperts", saveExperts);
            System.out.println("2222222222222222222");
        }

        System.out.println(request.toString());
        System.out.println(saveExperts);
        return saveExperts;
    }

    @RequestMapping(value = "/expertsList")
    public ResponseResult expertsList(String page) throws Exception {
        ResponseResult responseResult = new ResponseResult();
        if (page != null) {
            int i = Integer.parseInt(page);
            List<ExpertsA> expertsa = expertsService.expertsList(null, null, null, null, i);
            Map map = new HashMap();
            map.put("expertsa", expertsa);
            responseResult.addData(map);
        }
        return responseResult;
    }

    @RequestMapping(value = "/expertsListQuery")
    public ResponseResult expertsListQuery(Integer page, Integer education_number,
                                           Integer major_number, Integer title, Integer field) throws Exception {
        ResponseResult responseResult = new ResponseResult();
//		 if(page != 0 && education_number != 0 && major_number != 0 && title != 0 && field != 0){ }
        List<ExpertsA> expertsa = expertsService.expertsList(title, field, education_number, major_number, page);
        Map map = new HashMap();
        map.put("expertsa", expertsa);
        responseResult.addData(map);
        return responseResult;
    }

    @RequestMapping(value = "/expertsDetails.do")
    public ResponseResult expertsDetails(Integer id) throws Exception {
//		 Experts experts =  expertsService.expertsDetail(id);
        ResponseResult responseResult = new ResponseResult();
        if (id != 0) {
            ExpertsB expertsB = expertsService.expertsDetail(id);
            System.out.println(expertsB);
            Map map = new HashMap();
            map.put("expertsB", expertsB);
            responseResult.addData(map);
            return responseResult;
        } else {
            return responseResult;
        }

    }


    //专家跳转
    @ResponseBody
    @RequestMapping(value = "/expertsJump.do", method = {RequestMethod.GET, RequestMethod.POST})
    public Map<String, Object> liumu(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        Map<String, Object> map = new HashMap<>();
        if (user != null) {
            Experts experts = expertsService.selectByUserId(user.getId());
            if (experts == null) {
                map.put("redirects", "/html/Expert_infor.html");
                return map;
            } else {
                String redirects = "/html/zjXq1.html?id=" + experts.getId();
                map.put("redirects", redirects);
                return map;
            }
        } else {
            map.put("redirects", "dengL.do");
            return map;
        }
    }


    /*
     * 编辑专家
     */
    @ResponseBody
    @RequestMapping(value = "/updateByPrimaryKey.do")
    public String updateByPrimaryKey(Experts record) throws Exception {
//			 Experts experts =  expertsService.expertsDetail(id);
        if (record != null) {
            String key = updateByPrimaryKey(record).toString();
            return key;
        } else {
            return "0";
        }
    }


    public static String getUUIDName() {
        return UUID.randomUUID().toString();// make new file name
    }


    /**
     * 删除专家
     *
     * @param id
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping(value = "/deleteExpert.do")
    public Map<String, Object> deleteExpert(Integer id) throws Exception {
        // Experts experts = expertsService.expertsDetail(id);
        System.out.println(id);
        Map<String, Object> map = expertsService.deleteExpert(id);
        System.out.println(map.toString());
        return null;
    }

    /**
     * 查询专家个人信息
     *
     * @param id
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping(value = "/queryExpert.do")
    public Experts queryExpert(Integer id) throws Exception {
        // Experts experts = expertsService.expertsDetail(id);
        Experts experts = expertsService.selectByUserId(id);
        System.out.println(experts);
        return experts;
    }
}
