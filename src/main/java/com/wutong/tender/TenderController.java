package com.wutong.tender;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.URLDecoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.dubbo.config.annotation.Reference;
import com.parasol.common.load.Files_Utils_DG;
import com.parasol.core.myclass.TenderName;
import com.parasol.core.service.TenderService;
import com.parasol.core.tender.Tender;
import com.parasol.core.user.User;
import com.wutong.common.OrderUtil;
import com.wutong.framework.core.web.common.http.ResponseResult;

@RestController
@RequestMapping("/tender")
public class TenderController {
	
	private Logger logger = Logger.getLogger(this.getClass());
	
	static String flag = "0";
	
	@Reference
	private TenderService tenderService;
	
	/**
	 * 发布招标信息
	 * @param request
	 * @param multipartFile
	 * @return 0添加失败，1添加成功
	 */
	@RequestMapping("/tenderNeed")
//  @AuthLogin
	public String tenderNeed(HttpServletRequest request , @RequestParam("file_upload") MultipartFile[] multipartFile){
		Tender tender = new Tender();
		try {
			User user = (User) request.getSession().getAttribute("user");
			logger.info("用户信息......：" + user);
			BeanUtils.populate(tender, request.getParameterMap());
			logger.info("投标信息......：" + tender);
			tender.setProjectName(urlDecode(tender.getProjectName()));
			Date date = new Date();
			tender.setStartTime(date);
			tender.setUserid(user.getId());			
			 if (multipartFile != null && multipartFile.length > 0) {
				  tender.setTenderFile(Files_Utils_DG.FilesUpload_transferTo_spring(request, multipartFile[0], "/filesOut/Upload"));
			 }
			tender.setProjectType(OrderUtil.getBidOrderId(user.getId(), tender.getId()));
			int i = tenderService.insert(tender);
			if(i == 1){
				logger.info("招标信息发布成功！");
				flag = "1";
			}else{
				logger.info("信息发布失败！");
			}
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return flag ; 
	}
	
	/**
	 * 显示投标信息详情
	 * @param projectName
	 * @return
	 */
	@RequestMapping(value="/selectByPrimaryName")
	public ResponseResult selectByPrimaryName(String projectName){
		String name = urlDecode(projectName);
		Map<Object,Object> map = new HashMap<>();
		ResponseResult result = new ResponseResult();
		Tender tender = tenderService.selectByPrimaryName(name);
		if(null != tender){
			String substring = tender.getTenderFile().substring(tender.getTenderFile().lastIndexOf("/")+1);
			map.put("tender", tender);
			map.put("tenderFile", substring);			
			result.addData(map);
			logger.info("标书信息：" + tender);
		}else{
			result.addData("此标不存在！");
		}
		return result;
	}
	
	/**
	 * 删除发标信息
	 * @param projectName
	 * @return 0删除失败 ,1删除成功,2无删除权限
	 */
	 @RequestMapping("/deleteByneed")
//   @AuthLogin
	 public String deleteByneed(HttpServletRequest request , String projectName){
		 User user = (User) request.getSession().getAttribute("user");
		 Tender tender = tenderService.selectByPrimaryName(urlDecode(projectName));
		 if(tender.getUserid() == user.getId()){
			 Boolean booleans = tenderService.deleteByPrinaryName(urlDecode(projectName));
			 if(booleans == true){
				 flag = "1";
				 logger.info("标信息删除成功！");
			 }
		 }else{
			 flag = "2";
			 logger.info("无删除权限！");
		 }
		 return flag;
	 }
	  
/*	 @RequestMapping("/selectTenderName")
	 public List<TenderName> selectTenderName(){
			List<Tender> lists = tenderService.selectTender();
			tenderService
			List<TenderName> tenders = new ArrayList<>();			
			for (Tender list : lists) {
				TenderName tenderName = new TenderName();
				tenderName.setProjectName(list.getProjectName());	
				tenderName.setStartTime(list.getStartTime());
				tenderName.setEndDate(list.getEndDate());
				tenders.add(tenderName);0
			}
		 return tenders;
	 }*/
	   
	 @RequestMapping("/selectListTender")
	 public ResponseResult selectListTender(String classification , String userId , String count , String page){
		 ResponseResult<Object> result = new ResponseResult<>();
		 Map<?, ?> map = tenderService.selectListTender(classification , userId , count , page); 	
		 result.addData(map);
		 return result ;
	 }
	 
	public String urlDecode(String encode) {
		try {
			String decode = URLDecoder.decode(encode,"UTF-8");
			System.out.println(decode);
			return  decode;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return encode;
	}
}



