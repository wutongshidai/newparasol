package com.wutong.user;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Hex;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.parasol.core.service.UserService;
import com.parasol.core.user.User;

@RestController
@RequestMapping("/user")
public class UserController {
	
	@Reference 
	private UserService userService;
	
	static String flag = "0";
	
	/**
	 * 注册
	 * @param userName
	 * @param password
	 * @return 0失败 ，1成功
	 */
	@RequestMapping("/register")
	public String register(String userName , String password){
		User user = new User();
		Date date = new Date();
		user.setUserName(userName);
		user.setPassword(encodePassword(password));
		user.setCreateTime(date);
		int i = userService.insert(user);
		if(i == 1){
			flag = "1";
		}
		return flag;
	}
	
	/**
	 * 校验用户名
	 * @param userName
	 * @return 0不可用 ，1可用
	 */
	@RequestMapping("/repeatName")
	public String repeatName(String userName){
		User user = userService.selectUserByUsername(userName);
		if(null == user){
			flag = "1";
		}
		return flag;
	}
	
	/**
	 * 登录
	 * @param userName
	 * @param password
	 * @return 0用户不存在,1成功,2密码错误
	 */
	@RequestMapping("/login")
	public String login(String userName , String password ,HttpServletRequest request){
		User user = userService.selectUser(userName);
		if(null != user){
			if(user.getPassword().equals(encodePassword(password))){
				request.getSession().setAttribute("user", user);
				flag = "1" ;
			}else{
				flag = "2" ;
			}
		}
		return flag;
	} 
	
	/**
	 * 退出
	 * @param 
     * @return
	 */
    @RequestMapping("/loginOut")
    public void loginOut(HttpServletRequest request){
    	HttpSession session = request.getSession();
    	session.removeAttribute("user");
    }
    
	/**
     * 加密 MD5  
     * @param 
     * @return
     */
	public String encodePassword(String password){
		String algorithm = "MD5";
		char[] encodeHex  = null;	
		try {
			MessageDigest instance = MessageDigest.getInstance(algorithm);
			byte[] digest = instance.digest(password.getBytes());
			encodeHex = Hex.encodeHex(digest);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return new String(encodeHex);
	}
}
