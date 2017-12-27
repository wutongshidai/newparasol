package com.wutong.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.parasol.core.bid.Bid_order;
import com.parasol.core.bid.TenderBid;
import com.parasol.core.service.BidService;
import com.parasol.core.service.TenderService;
import com.parasol.core.service.UserService;
import com.parasol.core.tender.Tender;
import com.parasol.core.user.User;
import com.wutong.framework.core.web.common.http.ResponseResult;

import cn.itcast.common.page.Pagination;

@RestController
@RequestMapping("/userManage")
public class UserManageController {

	@Reference
	private TenderService tenderService;
	@Reference
	private UserService userService;
	@Reference
	private BidService bidService;

	/**
	 * 我的发布(带分页)
	 * @param request
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/alltenders")
//  @AuthLogin
	public ResponseResult alltenders(HttpServletRequest request , String page , String count) throws Exception{
	 ResponseResult<Object> result = new ResponseResult<>();
	 Map<String, Object> map = new HashMap<String, Object>();
	 User user = (User) request.getSession().getAttribute("user");
	  if(page == null){
		 page= "1";
	  }
	 Pagination pagination = tenderService.tenderAll(user.getId() , Integer.parseInt(page) , Integer.parseInt(count));
	 map.put("pagination", pagination);
	 result.addData(map);
	 return result;
   }
	
	/**
	 * 我的投标查询(带条数)
	 * @param com_userId
	 * @return
	 */
    @RequestMapping(value = "/getMyBid")
//  @AuthLogin
    public ResponseResult getMyBid (String id) {
        int userId = Integer.parseInt(id);
        Map map = new HashMap();
        List<Bid_order> list = bidService.getMyBids(userId);
        int count = bidService.countByUserId(userId);
        List  arr= new ArrayList();
        for (Bid_order bidOrder: list) {
            TenderBid tenderBid =new TenderBid();
            Integer tenderId = bidOrder.getTenderid();
            Tender tender = tenderService.selectByPrimaryKey(tenderId);
            if (tender !=null){
                String projectName = tender.getProjectName();
                tenderBid.setBidOrder(bidOrder);
                tenderBid.setProjectName(projectName);
                arr.add(tenderBid);
            }
        }
        map.put("Bid", arr);
        map.put("count", count);
        ResponseResult responseResult = new ResponseResult();
        responseResult.addData(map);
        return responseResult;
    }
	
	/**
	 * 修改用户信息
	 * @param companyName
	 * @param id
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/updateMessage")
//  @AuthLogin
	public Map<String, Object> updateMessage(String companyName, String id , HttpServletRequest request) {
		User user = (User) request.getSession().getAttribute("user");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("success", false);
		try {
			if (companyName.trim() != null && companyName.length() != 0 && id != null && id != "") {
				user.setCompanyName(companyName);
				Integer parseInt = Integer.parseInt(id);
				user.setId(parseInt);
				Boolean updateByNickNames = userService.updateByNickNames(user);
				map.put("success", updateByNickNames);
				map.put("name", user.getCompanyName());
				return map;
			} else {
				map.put("success", false);
				map.put("message", "昵称或Id为空，修改失败！");
				return map;
			}

		} catch (Exception e) {
			// throw new BusinessException("修改信息异常！");
			e.printStackTrace();
			map.put("message", "修改信息出现异常，请检查用户是否存在！");
			return map;
		}
	}
}
