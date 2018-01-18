package com.wutong.tender;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.fusesource.hawtbuf.ByteArrayInputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.dubbo.config.annotation.Reference;
import com.parasol.common.load.Files_Utils_DG;
import com.parasol.common.load.fileUpload;
import com.parasol.common.oss.OSSObjectUtils;
import com.parasol.common.utils.PropertiesUtils;
import com.parasol.common.utils.encoder.BASE64Decoder;
import com.parasol.core.myclass.TenderName;
import com.parasol.core.service.TenderService;
import com.parasol.core.service.UserService;
import com.parasol.core.tender.Tender;
import com.parasol.core.user.User;
import com.wutong.common.OrderUtil;
import com.wutong.config.AppProperties;
import com.wutong.framework.core.web.auth.aop.annotation.AuthLogin;
import com.wutong.framework.core.web.common.http.ResponseResult;

@RestController
@RequestMapping("/tender")
public class TenderController {
	
	private Logger logger = Logger.getLogger(this.getClass());
	
	@Autowired
	private AppProperties appProperties;
	
	@Reference
	private TenderService tenderService;
	@Reference
	private UserService userService;
	
	
	/**
	 * 发布招标信息
	 * @param request
	 * @param
	 * @return 0添加失败，1添加成功
	 */
	@RequestMapping("/tenderNeed")
	@AuthLogin
	public ResponseResult tenderNeed(HttpServletRequest request){
		ResponseResult responseResult =new ResponseResult();
		String flag = "0";
		Tender tender = new Tender();
		//  HttpServletRequest request , @RequestParam("file_upload") MultipartFile[] multipartFile   @ModelAttribute("form") Tender tender1 ,
		try {
			User user = (User) request.getSession().getAttribute("user");
//			User user = null;
//			System.out.println(tender1);
//			User user = userService.selectByPrimaryKey(1);
			logger.info("用户信息......：" + user);
			
		    Map<String, String[]> map = request.getParameterMap();
		    String[] str = map.get("endDate1");
			  StringBuffer sb = new StringBuffer();
			   for(int i = 0; i < str.length; i++){
			     sb. append(str[i]);
			     }
			   System.out.println("获取日期.................." + sb);
			String s = sb.toString();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			System.out.println(sdf.parse(s)+"shijian");	
			
			BeanUtils.populate(tender, request.getParameterMap());
			tender.setEndDate(sdf.parse(s));
			String split = tender.getBidFile().split(",")[1];
			
			File filePath = new File(appProperties.getUploadPath() + getDataPath());
			if (!filePath.exists()) {
				filePath.mkdir();
			}
			String path =  filePath + File.separator +tender.getTenderFile() ;
//			fileUpload.saveImg(split, "/opt/filesOut/Upload/" + path);
			byte[] buffer = split.getBytes();
			FileOutputStream out = new FileOutputStream(path);
			out.write(buffer);
			out.close();
/*	   		InputStream inputStream = new ByteArrayInputStream(bs);
	   		System.out.println(inputStream.toString().length()+"aaa00000000000000000000000000000000000000000000000");
	   		OSSObjectUtils.uploadFile("wut1/aaaaaa", inputStream);*/
			tender.setTenderFile(path);
			tender.setBidFile("1");
			logger.info("投标信息......：" + tender);
			Date date = new Date();
			tender.setStartTime(date);
			tender.setUserid(user.getId());			
			tender.setProjectType(OrderUtil.getBidOrderId(user.getId(), tender.getId()));
			int i = tenderService.insert(tender);
			if(i != 0){
				logger.info("招标信息发布成功！");
				flag = String.valueOf(i);
			}else{
				logger.info("信息发布失败！");
			}
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
		responseResult.addData(flag);
		return responseResult ;
	}
	
	/**
	 * 显示投标信息详情
	 * @param
	 * @return
	 */
	@RequestMapping(value="/selectByPrimaryName")
	public ResponseResult selectByPrimaryName(String tenderId){
		System.out.println(tenderId);
//		String name = urlDecode(projectName);
		Map<Object,Object> map = new HashMap<>();
		ResponseResult result = new ResponseResult();
		Tender tender = tenderService.selectByPrimaryKey(Integer.parseInt(tenderId));
//		Tender tender = tenderService.selectByPrimaryName(name);
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
	   
	 /**
	  * 投标信息列表
	  * @param classification 分类
	  * @param userId  用户Id
	  * @param count   每页条数
	  * @param page    页码
	  * @return
	  */
	 @RequestMapping("/selectListTender")
	 public ResponseResult selectListTender(String classification , String userId , String count , String page){
		 ResponseResult<Object> result = new ResponseResult<>();
		 Map<?, ?> map = tenderService.selectListTender(classification , userId , count , page); 	
		 result.addData(map);
		 return result ;
	 }
	 
	 
	 /**
	  * 投标信息列表
	  * @param count   每页条数
	  * @param page    页码
	  * @return
	  */
	 @RequestMapping("/selectListTenderin")
	 public ResponseResult selectListTender(String count , String page){
		 ResponseResult<Object> result = new ResponseResult<>();
		 Map<?, ?> map = tenderService.selectListTender1(count , page); 	
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
	
    @RequestMapping(value = "/fileDownload_servlet")
    public void fileDownload_servlet(HttpServletRequest request, HttpServletResponse response , String filePath) {
  	  Files_Utils_DG.FilesDownload_stream(request , response , filePath);
      }
	
    public static String getDataPath() {
        return new SimpleDateFormat("yyyyMMdd").format(new Date());
    }
}



