package com.wutong.user;

import java.lang.reflect.InvocationTargetException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.codec.binary.Hex;
import org.apache.log4j.Logger;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.parasol.common.converter.Validator;
import com.parasol.core.Enum.TenderStatusEnum;
import com.parasol.core.bid.Bid_info;
import com.parasol.core.bid.Bid_order;
import com.parasol.core.bid.OrderInfo;
import com.parasol.core.bid.TenderBid;
import com.parasol.core.service.BidService;
import com.parasol.core.service.TenderService;
import com.parasol.core.service.UserService;
import com.parasol.core.tender.Tender;
import com.parasol.core.user.User;
import com.wutong.framework.core.web.auth.aop.annotation.AuthLogin;
import com.wutong.framework.core.web.common.http.ResponseResult;

import cn.itcast.common.page.Pagination;

@RestController
@RequestMapping("/userManage")
public class UserManageController {
	
	private Logger logger = Logger.getLogger(this.getClass());

	@Reference
	private TenderService tenderService;
	@Reference
	private UserService userService;
	@Reference
	private BidService bidService;

	 /**
	  * 我的发布
	  * @param classification 分类
	  * @param userId  用户Id
	  * @param count   每页条数
	  * @param page    页码
	  * @return
	  */
	 @RequestMapping("/selectListTender")
	 @AuthLogin
	 public ResponseResult selectListTender(String classification  , String count , String page , HttpServletRequest request){
//		 User user = userService.selectByPrimaryKey(1);
		 User user = (User) request.getSession().getAttribute("user");
		 ResponseResult<Object> result = new ResponseResult<>();
		 Map<?, ?> map = tenderService.selectListTender(classification , user.getId().toString() , count , page); 	
		 result.addData(map);
		 return result ;
	 }
	 
	 /**
	  * 修改我的发布日期
	  * @param id
	  * @param endTime
	  * @return
	  */
	 @RequestMapping("/updateEndtime")
//	 @AuthLogin
	 public String updateEndtime(String id,String endTime){
		 String flag = "";
		 Tender key = tenderService.selectByPrimaryKey(Integer.parseInt(id));
		try {
				Date c = new Date();//变更时日期
				Date b = key.getEndDate();//截止日期
				if(c.before(b) || c.equals(b)){
					 Integer integer = tenderService.updateEndtime(id, endTime);
					 if(integer == 1){
						 flag = "修改成功！";
					 }else{
						 flag = "修改失败！";
					 }
				}else{
					flag = "不可更改！";
				} 
		} catch (Exception e) {
			e.printStackTrace();
		}
		 return flag ;
	 }
	
	/**
	 * 删除发标信息
	 * @param projectName
	 * @return 0删除失败 ,1删除成功,2无删除权限
	 */
	 @RequestMapping("/deleteByneed")
	 @AuthLogin
	 public String deleteByneed(HttpServletRequest request , String tenderId){
		 String flag = "0";
//		 User user = (User) request.getSession().getAttribute("user");
		 User user = userService.selectByPrimaryKey(1);
		 Tender tender = tenderService.selectByPrimaryKey(Integer.parseInt(tenderId));
//		 Tender tender = tenderService.selectByPrimaryName(urlDecode(projectName));
		 if(tender.getUserid() == user.getId()){
			 Boolean booleans = tenderService.deleteByPrinaryId(Integer.parseInt(tenderId));
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
	 
	/**
	 * 我的投标查询(带条数)
	 * @param com_userId
	 * @return
	 */
    @RequestMapping(value = "/getMyBid")
    @AuthLogin
    public ResponseResult getMyBid (HttpServletRequest request ,String count , String page) {
    	 User user = (User) request.getSession().getAttribute("user");
//		 User user = userService.selectByPrimaryKey(1);
        Map map = new HashMap();
        List<Bid_order> list = bidService.getMyBids(user.getId().toString(),count,page);
        System.out.println(list);
        int count1 = bidService.countByUserId(user.getId());
        List  arr= new ArrayList();
        for (Bid_order bidOrder: list) {
            TenderBid tenderBid =new TenderBid();
            Integer tenderId = bidOrder.getTenderid();
            Tender tender = tenderService.selectByPrimaryKey(tenderId);
            if (tender !=null){
                String projectName = tender.getProjectName();
                Date endDate = tender.getEndDate();
                tenderBid.setBidOrder(bidOrder);
                tenderBid.setProjectName(projectName);
                tenderBid.setEndDate(endDate);
                arr.add(tenderBid);
            }
        }
        map.put("Bid", arr);
        map.put("count", count1);
        ResponseResult responseResult = new ResponseResult();
        responseResult.addData(map);
        return responseResult;
    }
    
   /**
    * 修改密码
    * @param password
    * @param newpassword
    * @param request
    * @return
    */
	@RequestMapping(value = "/updatePassword")
    @AuthLogin
	public ResponseResult updatePassword(String password, String newpassword ,
			HttpServletRequest request) {
		ResponseResult re = new ResponseResult();
		User user = (User) request.getSession().getAttribute("user");
		Map<String, Object> map = new HashMap<>();
		String message="";
			if (encodePassword(password).equals(user.getPassword())) {
				user.setPassword(encodePassword(newpassword));
				Boolean updateByNickNames = userService.updateByNickNames(user);
				if (updateByNickNames){
					message= "修改成功";
					map.put("success", user.getUserName());
					logger.info("密码修改成功！");
				}else{
					message = "未知错误,修改失败";
					map.put("success", false);
					logger.info("未知错误,修改失败！");
				}
				re.addData(map);
			} else {
				map.put("message", "初始密码错误");
				map.put("success", false);
				logger.info("初始密码错误！");
				re.addData(map);
			}
			return re;	
	}
	
	/**
	 * 修改用户信息
	 * @param companyName
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/updateMessage")
    @AuthLogin
	public ResponseResult<Object> updateMessage( HttpServletRequest request) {
		User user = (User) request.getSession().getAttribute("user");
//		User user = userService.selectByPrimaryKey(1);
		ResponseResult<Object> result = new ResponseResult<>();
//		User user = new User();
		try {
			BeanUtils.populate(user, request.getParameterMap());
			Boolean boolean1 = userService.updateByNickNames(user);
			if(boolean1){
				User key = userService.selectByPrimaryKey(user.getId());
				result.addData(key);
				logger.info("更新成功！");
			}else{
				result.addData("更新失败！");
				logger.info("更新失败！");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 用户信息返回
	 * @param id
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/userMessage")
    @AuthLogin
	public ResponseResult<Object> userMessage(String id , HttpServletRequest request){
		ResponseResult<Object> result = new ResponseResult<>();
		User user = userService.selectByPrimaryKey(Integer.parseInt(id));
//		User user = (User) request.getSession().getAttribute("user");
		logger.info("user:"+user);
		result.addData(user);
		return result;
	}
	
    /**
     * 我的投标查看
     */
    @RequestMapping(value = "/watchMyBid")
    @AuthLogin
    public ResponseResult watchMyBid (String tenderId) {
        Map map = new HashMap();
        Tender tender = tenderService.selectByPrimaryKey(Integer.parseInt(tenderId));
//        Bid_order bidOrder = (Bid_order) tender;
        map.put("tender", tender);
        ResponseResult responseResult = new ResponseResult();
        responseResult.addData(map);
        return responseResult;
    }
    
    /**
     * 我的发布（带订单）
     * @param tenderId
     * @return
     */
    @RequestMapping("/selectByName")
    public ResponseResult selectByPrimaryName(String tenderId) {
        Map<String, Object> result = new HashMap<>();
        Tender tender = tenderService.selectByPrimaryKey(Integer.parseInt(tenderId));
        result.put("tender", tender);
        System.out.println(tender);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd ");
        String date = formatter.format(tender.getEndDate());//格式化数据
        result.put("endDate", date);
        Integer id = tender.getId();
        List<Bid_order> list = bidService.selectOrderByTid(id);
        List<OrderInfo> list1 = new ArrayList<>();
        for (int i =0; i<list.size(); i++){
            Integer bidInfoid = list.get(i).getBidInfoid();
            Bid_info bid_info =  bidService.selectInfoById(bidInfoid);
            OrderInfo orderInfo = new OrderInfo();
            orderInfo.setBid_info(bid_info);
            orderInfo.setBid_order(list.get(i));
            list1.add(orderInfo);
        }
        result.put("olist",list1);
        TenderStatusEnum code = TenderStatusEnum.getByCode(tender.getClassification());
        result.put("explainl", tender.getExplainl().replaceAll("", "&nbsp;").replaceAll("\r", "<br/>"));
        ResponseResult responseResult = new ResponseResult();
        responseResult.addData(result);
        return responseResult;
    }
    
	/**
	 * 加密 MD5 + 十六进制 + 盐 password = "safqwgnetrygfhehn123456j7efwhtreyguyu6y";
	 * 
	 * @param
	 * @return
	 */
	public String encodePassword(String password) {
		String algorithm = "MD5";
		char[] encodeHex = null;
		try {
			MessageDigest instance = MessageDigest.getInstance(algorithm);
			// MD5加密后密文
			byte[] digest = instance.digest(password.getBytes());
			// 十六进制再加密一次
			encodeHex = Hex.encodeHex(digest);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return new String(encodeHex);
	}
}
