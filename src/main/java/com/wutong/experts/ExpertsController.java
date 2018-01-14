package com.wutong.experts;

import com.alibaba.dubbo.config.annotation.Reference;
import com.parasol.common.load.fileUpload;
import com.parasol.common.oss.OSSObjectUtils;
import com.parasol.common.utils.PropertiesUtils;
import com.parasol.common.utils.encoder.BASE64Decoder;
import com.parasol.core.experts.Expertindex;
import com.parasol.core.experts.Experts;
import com.parasol.core.experts.ExpertsA;
import com.parasol.core.experts.ExpertsB;
import com.parasol.core.service.ExpertsService;
import com.parasol.core.service.UserService;
import com.parasol.core.user.User;
import com.wutong.framework.core.web.auth.aop.annotation.AuthLogin;
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
import org.apache.log4j.Logger;
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

	private Logger logger = Logger.getLogger(this.getClass());
	
    @Reference
    ExpertsService expertsService;
    @Reference
    UserService userService;


    /**
     * 录入 专家
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/experts")
//	@AuthLogin
    public String experts(HttpServletRequest request ) throws Exception {
    	String flag = "";
        Map<String, String> adminMap = new HashMap<String, String>();
//        User user = (User) request.getSession().getAttribute("user");
        User user = userService.selectByPrimaryKey(6);
        logger.info("用户信息："+ user);
        Experts experts = new Experts();
        experts.setUserId(user.getId());
        BeanUtils.populate(experts, request.getParameterMap());
            if (null != experts.getPhoto() && "" != experts.getPhoto()) {
                String a = "/imgl/" + getUUIDName() + ".png";
//                "/usr/local/tomcat/webapps/parasol-controller-0.0.1-SNAPSHOT/imgl/" + getUUIDName() + ".png"
                fileUpload.saveImg(experts.getPhoto(), PropertiesUtils.getStringValue("tender_file__url") + a);
                experts.setPhoto(".." + a);
            }
         Integer in = expertsService.saveExperts(experts);
         logger.info("experts:"+ experts);
         if(in == 1){
        	 flag = "保存专家信息成功！";
         }else{
        	 flag = "保存失败！";
         }
        return flag;
    }

    /**
     * 专家列表
     * @param page
     * @param education_number
     * @param major_number
     * @param title
     * @param field
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/expertsListQuery")
    public ResponseResult expertsListQuery(Integer page, Integer education_number,
                                           Integer major_number, Integer title, Integer field) throws Exception {
        ResponseResult responseResult = new ResponseResult();
//		 if(page != 0 && education_number != 0 && major_number != 0 && title != 0 && field != 0){ }
        List<ExpertsA> expertsa = expertsService.expertsList(title, field, education_number, major_number, page);
        Map map = new HashMap();
        map.put("expertsa", expertsa);
        logger.info("expertsa:" + expertsa);
        responseResult.addData(map);
        return responseResult;
    }

    /**
     * 查询专家个人信息
     * @param id
     * @return
     * @throws Exception
     */
	 @RequestMapping(value="/expertsDetails")
		public ResponseResult expertsDetails(Integer id) throws Exception{
	        ResponseResult re = new ResponseResult();
		 if(id != 0){
			 ExpertsB expertsB = expertsService.expertsDetail(id);
			 re.addData(expertsB);
			 logger.info("专家个人信息："+expertsB);
		 }else{
			 re.addData("获取专家信息失败！");
			 logger.info("获取专家信息失败！");
		 }
		 return re;
	 }
    
	 /**
	  * 首页专家展示
	  * @return
	  * @throws Exception
	  */
	 @RequestMapping(value="/expertsFour")
		public ResponseResult expertsFour() throws Exception{
		 ResponseResult re = new ResponseResult();
		List<Expertindex> expertindexs = expertsService.selectExpertindex();
		re.addData(expertindexs);
		logger.info("首页专家："+ expertindexs);
		return re;
	 }
	 
    public static String getUUIDName() {
        return UUID.randomUUID().toString();
    }
}
