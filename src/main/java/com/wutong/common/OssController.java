package com.wutong.common;

import com.parasol.common.oss.OSSObjectUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author aliang
 * @since 2017/8/30
 *
 */

@RestController
public class OssController {
	
	/*
	 * oss文件上传
	 * 商品上传
	 */
	@ResponseBody
	@RequestMapping(value="/uploadOss.do")
 	public Map<String, String> uploadOss(MultipartRequest request){	
		try {
			MultipartFile multipartFile = request.getFileMap().get("file");
			String filename = OSSObjectUtils.uploadMultipartFile(multipartFile,"","wut1");
			System.out.println("oss上传成功！");
			Map<String, String> map = new HashMap<>();
			map.put("filename", filename);
			map.put("success", "true");
			return map;
		} catch (Exception e) {
			Map<String, String> map = new HashMap<>();
			map.put("success", "false");
			return map;
		}	
	}
	
	/*
	 * oss文件删除
	 */
	@ResponseBody
	@RequestMapping(value="/deleteOss.do")
 	public void deleteOss(String buketName , String fileName){
		OSSObjectUtils.deleteObject(buketName, fileName);
		System.out.println("oss删除成功！");
	}
	
	
	/*
	 * oss文件上传
	 * 证件上传
	 * 2017/10/12
	 */
	@ResponseBody
	@RequestMapping(value="/uploadOssCard.do")
 	public Map<String, String> uploadOssCard(MultipartRequest request){	
		try {
			MultipartFile multipartFile = request.getFileMap().get("file");
			String filename = OSSObjectUtils.uploadMultipartFile(multipartFile,"","wut3");
			System.out.println("oss上传成功！");
			Map<String, String> map = new HashMap<>();
			map.put("filename", filename);
			map.put("success", "true");
			return map;
		} catch (Exception e) {
			Map<String, String> map = new HashMap<>();
			map.put("success", "false");
			return map;
		}	
	}
}
