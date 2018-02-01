package com.wutong.common;

import com.parasol.common.load.CCPRestSDK;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


@RestController
public class SDKTestSendTemplateSMS {

	/**
	 * @param args
	 */
	@ResponseBody
	@RequestMapping("/sdkTestSend.do")
	public void sdkTestSend(String mobile,HttpServletRequest request,HttpServletResponse response) {
		HashMap<String, Object> result = null;

		CCPRestSDK restAPI = new CCPRestSDK();
/*		restAPI.init("app.cloopen.com", "8883");// 初始化服务器地址和端口，格式如下，服务器地址不需要写https://
		restAPI.setAccount("8a216da8594d554f01596724627a7d90", "405374c0d79a4b8db0d1189590fd5ece");// 初始化主帐号和主帐号TOKEN
		restAPI.setAppId("8a216da8594d554f01596724629e7d91");// 初始化应用ID
		result = restAPI.sendTemplateSMS("15210291517","1" ,new String[]{});*/
		
		restAPI.init("app.cloopen.com", "8883");// 初始化服务器地址和端口，格式如下，服务器地址不需要写https://
		restAPI.setAccount("8aaf07085b5fee9a015b661ebb2b03f7", "3cba3e8d5fef40d9beace42065ba9f22");// 初始化主帐号和主帐号TOKEN
		restAPI.setAppId("8aaf07085b5fee9a015b661ebcd403fe");// 初始化应用ID

		int i =(int)((Math.random()*9+1)*100000);
		String yancode = i + "";
		HttpSession session = request.getSession();
		session.setAttribute("yancode", yancode);
		System.out.println(mobile);
		System.out.println(yancode);
		result = restAPI.sendTemplateSMS(mobile,"168213" ,new String[]{yancode,"1"});

		System.out.println("SDKTestSendTemplateSMS result=" + result);
		
		if("000000".equals(result.get("statusCode"))){
			//正常返回输出data包体信息（map）
			HashMap<String,Object> data = (HashMap<String, Object>) result.get("data");
			Set<String> keySet = data.keySet();
			for(String key:keySet){
				Object object = data.get(key);
				System.out.println(key +" = "+object);
			}
		}else{
			//异常返回输出错误码和错误信息
			System.out.println("错误码=" + result.get("statusCode") +" 错误信息= "+result.get("statusMsg"));
		}
	}
	
	/*
	 * 商城后台用户注册
	 */
	@ResponseBody
	@RequestMapping("/sdkTestSend2.do")
	public String sdkTestSend2(@RequestBody Map map, HttpServletRequest request, HttpServletResponse response) {
		HashMap<String, Object> result = null;
		String mobile = (String) map.get("phone");
		System.out.println(mobile);
		CCPRestSDK restAPI = new CCPRestSDK();
/*		restAPI.init("app.cloopen.com", "8883");// 初始化服务器地址和端口，格式如下，服务器地址不需要写https://
		restAPI.setAccount("8a216da8594d554f01596724627a7d90", "405374c0d79a4b8db0d1189590fd5ece");// 初始化主帐号和主帐号TOKEN
		restAPI.setAppId("8a216da8594d554f01596724629e7d91");// 初始化应用ID
		result = restAPI.sendTemplateSMS("15210291517","1" ,new String[]{});*/

		restAPI.init("app.cloopen.com", "8883");// 初始化服务器地址和端口，格式如下，服务器地址不需要写https://
		restAPI.setAccount("8aaf07085b5fee9a015b661ebb2b03f7", "3cba3e8d5fef40d9beace42065ba9f22");// 初始化主帐号和主帐号TOKEN
		restAPI.setAppId("8aaf07085b5fee9a015b661ebcd403fe");// 初始化应用ID

		int i =(int)((Math.random()*9+1)*100000);
		String yancode = i + "";

		HttpSession session = request.getSession();
		session.setAttribute("yancode", yancode);
		System.out.println(mobile);
		System.out.println(yancode);
		result = restAPI.sendTemplateSMS(mobile,"168213" ,new String[]{yancode,"1"});

		System.out.println("SDKTestSendTemplateSMS result=" + result);

		if("000000".equals(result.get("statusCode"))){
			//正常返回输出data包体信息（map）
			HashMap<String,Object> data = (HashMap<String, Object>) result.get("data");
			Set<String> keySet = data.keySet();
			for(String key:keySet){
				Object object = data.get(key);
				System.out.println(key +" = "+object);
			}
			return "success";
		}else{
			//异常返回输出错误码和错误信息
			System.out.println("错误码=" + result.get("statusCode") +" 错误信息= "+result.get("statusMsg"));
			return "fail";
		}
	}
	
	
	
	


}
