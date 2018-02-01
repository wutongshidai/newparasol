package com.wutong.shop;

import com.alibaba.dubbo.config.annotation.Reference;
import com.parasol.core.mall.Wt_admin;
import com.parasol.core.mall.Wt_admin_expend;
import com.parasol.core.mall.Wt_navigation;
import com.parasol.core.service.WtadminService;
import com.parasol.core.service.WtadminexpendService;
import com.parasol.core.service.WtnavigationService;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.codec.binary.Hex;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.lang.reflect.InvocationTargetException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/***
 * 
 * @author aliang
 * @since 2017/8/28
 */
@RestController
public class WtadminController {

	@Reference
	private WtadminService wtadminService;
	@Reference
	private WtadminexpendService wtadminexpendService;

	@Reference
	private WtnavigationService wtnavigationService;
	
	/**
	 * 添加商家
	 */
	public Integer insertSelective(Wt_admin admin){
		int i = wtadminService.insertSelective(admin);
		return i;
	}
	
	
	public Integer deleteByPrimaryKey(Integer id) {
		int i = wtadminService.deleteByPrimaryKey(id);
		return i;
	}

	
	public Map insert(Wt_admin record) {
		Map i = wtadminService.insert(record);
		return i;
	}
	
	public Wt_admin selectByPrimaryKey(Integer id) {
		Wt_admin key = wtadminService.selectByPrimaryKey(id);
		return key;
	} 

	
	public int updateByPrimaryKeySelective(Wt_admin record) {
		int i = wtadminService.updateByPrimaryKeySelective(record);
		return i;
	}

	
	public int updateByPrimaryKey(Wt_admin record) {
		int i = wtadminService.updateByPrimaryKey(record);
		return i;
	}
	

	/**
	 * @author biao
	 * @since 2017/10/9
	 */
	
	/*
	 *修改店铺logo
	 */
	@ResponseBody
	@RequestMapping("/shopLogo.do")
	public String shopLogo(Integer adExId , String adShopLogo) {
		  Wt_admin_expend key = wtadminexpendService.selectByPrimaryKey(adExId);
		  key.setAdShopLogo(adShopLogo);
		  int i = wtadminexpendService.updateByPrimaryKeySelective(key);
		  if(i == 1){
			  return "success";
		  }
		return "fail";
	}
	Logger logger = Logger.getLogger(WtadminController.class);

	/**
     * 校验账户名接口
     * @param usermap 用户名的json串
     * @return
     */
	@ResponseBody
	@RequestMapping("/selectByUserName.do")
	public Boolean selectUsername(@RequestBody Map usermap){
		String userName = (String)usermap.get("userName");
		Boolean b =wtadminService.selectByUserName(userName);
		return b;
	}

    /**
     * 校验手机号接口
     * @param
     * @return
     */
    @ResponseBody
    @RequestMapping("/selectByPhone.do")
    public Boolean selectPhone(@RequestBody Map phoneMap){
		String phone = (String) phoneMap.get("phone");
		logger.debug(phone);
        Boolean b =wtadminService.selectByPhone(phone);
        return b;
    }

    /**
     * 校验验证码是否正确
     * @param codeMap
     * @param
     * @param request
     * @param
     * @return
     * @throws Exception
     */
	@ResponseBody
	@RequestMapping(value="/checkCodesss2.do")
	public Map<String,Object> checkCodesss2(@RequestBody Map<String,String> codeMap,  HttpServletRequest request ) throws Exception{
		Map<String, Object> adminMaps= new HashMap<String,Object>();
		boolean b = false;
		String mobileyan = codeMap.get("checkCode");
		if(null != mobileyan){
			System.out.println(mobileyan);
			String code = (String)request.getSession().getAttribute("yancode");
			System.out.println("code="+ code);
			if(null != code){
				if(code.equals(mobileyan)){
					System.out.println("验证成功");
                    b = true;
				}else{
					System.out.println("验证失败");
                    b = false;
				}
			}else{
                b = false;
			}
		}else{
            b = false;//验证码不能为空
		}
		System.out.println(b);
		adminMaps.put("admin",b);
		System.out.println(adminMaps);
		System.out.println("短信验证...");
		return adminMaps;
	}

    /**
     * 注册接口
     * @param
     * @return
     */
	@ResponseBody
    @RequestMapping(value="/signUp.do")
    public Map insert(@RequestBody Map<String,Object> signMap) {
		Map map = new HashMap();
	    Wt_admin wt_admin =new Wt_admin();
		map.put("status",false);
		map.put("msg","注册失败，请检查输入的数据稍后再试");
        try {
            String password = "";
            BeanUtils.populate(wt_admin,signMap);
            if (wt_admin.getPassword() !=null){
                password = encodePassword(wt_admin.getPassword());
                wt_admin.setPassword(password);
            }else {
                return map;
            }
            if (wt_admin.getCompanyName()!=null && wt_admin.getMobilePhone()!=null && wt_admin.getUserName()!=null  && wt_admin.getPassword() !=null && wt_admin.getCatId()!=null){
                map = wtadminService.insert(wt_admin);
				Object adId = map.get("adId");
				if (adId != null){
					map.put("status", true);
					map.put("msg", "注册成功");
				}
				return map;
            }else {
                return map;
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return map;
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            return map;
        }

    }

    /**
     * 完善信息接口
     * @param
     * @return
     */
	@ResponseBody
    @RequestMapping(value="/upShop.do")
    public Map upShop(@RequestBody Map<String,Object> shopMap) {
		Map map = new HashMap();
		map.put("status",false);
		map.put("msg","更新信息失败,请稍后再试");
	    Wt_admin_expend wt_admin_expend =new Wt_admin_expend();
        try {
            BeanUtils.populate(wt_admin_expend,shopMap);
			System.out.println(wt_admin_expend);
			wt_admin_expend.setAdState(1);
			int i = wtadminexpendService.upShop(wt_admin_expend);
			if (i == 1) {
				map.put("status",true);
				map.put("msg","更新信息成功");
			}
			return map;
		} catch (IllegalAccessException e) {
            e.printStackTrace();
            return map;
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            return map;
        }

    }

	/**
	 * 登录
	 * @param map
	 * @param request
	 * @return
	 */
    @ResponseBody
    @RequestMapping(value="/signIn.do")
    public Map signIn(@RequestBody Map<String,Object> map, HttpServletRequest request) {
        Wt_admin wt_admin =new Wt_admin();
        Map<String,Object> maps = new HashMap();
		String password = (String)map.get("password");
		String s = encodePassword(password);
		map.put("password",s);
		maps.put("status",false);
		maps.put("msg","登录出现异常，请稍后再试");
        try {
            BeanUtils.populate(wt_admin,map);
            Boolean aBoolean = wtadminService.selectByUserName(wt_admin.getUserName());
            if (!aBoolean){
                // 用户存在
				Map map2=wtadminService.login(wt_admin);
				if (map2 == null) {
					maps.put("status",false);
					maps.put("msg","密码错误，请重新输入");
					System.out.println(maps);
					return maps;
				}else {
					maps = map2;
					HttpSession session = request.getSession();
					session.setAttribute("userMap", maps);
					maps.put("status",true);
					maps.put("msg","登录成功");
					return maps;
				}
            }else {
                // 用户名不存在
				maps.put("status",false);
				maps.put("msg","用户名不存在");
            	return maps;
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return maps;
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            return maps;
        }

    }

	@ResponseBody
	@RequestMapping(value="/isSignIn.do")
	public Map isSignIn(@RequestBody Map<String,Object> map, HttpServletRequest request) {
		Wt_admin wt_admin =new Wt_admin();
		Map<String,Object> maps = new HashMap();
		String password = (String)map.get("password");
		String s = encodePassword(password);
		map.put("password",s);
		maps.put("status",false);
		maps.put("msg","登录出现异常，请稍后再试");
		return maps;
	}

	/**
	 *更新拓展表信息接口
	 * @param adExMap
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/upAdExpend.do")
	public Map upAdExpend(@RequestBody Map<String,Object> adExMap) {
		Map map =new HashMap();
		map.put("status",false);
		map.put("msg","更新失败。请稍后再试");
		Wt_admin_expend wt_admin_expend =new Wt_admin_expend();
		try {
			BeanUtils.populate(wt_admin_expend, adExMap);
			int i = wtadminexpendService.updateByPrimaryKeySelective(wt_admin_expend);
			if (i == 1){
				map.put("status",true);
				map.put("msg","更新成功");
			}
			return map;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return map;
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			return map;
		}
	}
	/**
	 *查询拓展表信息接口
	 * @param adExMap
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/selectInfo.do")
	public Map selectInfo(@RequestBody Map<String,Object> adExMap) {
		Map map =new HashMap();
		map.put("status",false);
		map.put("msg","更新失败。请稍后再试");
		Integer adExId = (Integer) adExMap.get("adExId");
		Map<String, Object> stringObjectMap = wtadminService.selectInfo(adExId);
		if (stringObjectMap != null) {
			map.put("status", true);
			map.put("company", stringObjectMap);
		}
		return map;
	}

	/**
	 * 查询导航信息
	 * @param adExMap
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/selectNav.do")
	public List<Wt_navigation> selectNav(@RequestBody Map<String,Object> adExMap) {
		Integer adExId = (Integer) adExMap.get("adExId");
		List<Wt_navigation> wt_navigations = wtnavigationService.selectByAd(adExId);
		return wt_navigations;
	}

	/**
	 * 添加导航信息
	 * @param navMap
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/addNavigation.do")
	public Map addNav(@RequestBody Map<String,Object> navMap) {
		Wt_navigation wt_navigation = new Wt_navigation();
		Map map = new HashMap();
		map.put("status",false);
		map.put("msg","添加失败。请稍后再试");
		try {
			Integer adExId = (Integer)navMap.get("adExId");
			Integer naDisplays = Integer.parseInt((String)navMap.get("naDisplay"));
			if (naDisplays == 1) {
				List<Wt_navigation> list= wtnavigationService.selectByAd(adExId);
				int flag = 0;
				for (int i=0; i<list.size(); i++) {
					Wt_navigation nav = list.get(i);
					Integer naDisplay = nav.getNaDisplay();
					if (naDisplay == 1) {
						flag++;
						if (flag >=3) {
							map.put("status",false);
							map.put("msg","能显示的导航已经设置超过了5个，添加失败");
							return map;
						}
					}
				}
			}
			BeanUtils.populate(wt_navigation, navMap);
			int i = wtnavigationService.insertSelective(wt_navigation);
			if (i == 1) {
				map.put("status",true);
				map.put("msg","添加成功");
			}
			return map;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return map;
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			return map;
		}
	}

	/**
	 * 编辑导航信息
	 * @param navMap
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/upNavigation.do")
	public Map upNav(@RequestBody Map<String,Object> navMap) {
		Wt_navigation wt_navigation = new Wt_navigation();
		Map map = new HashMap();
		map.put("status",false);
		map.put("msg","修改失败。请稍后再试");
		try {
			Integer adExId = (Integer)navMap.get("adExId");
			Integer naDisplays = Integer.parseInt((String)navMap.get("naDisplay"));
			Integer naId1 = (Integer)navMap.get("naId");
			if (naDisplays == 1) {
				List<Wt_navigation> list= wtnavigationService.selectByAd(adExId);
				int flag = 0;
				for (int i=0; i<list.size(); i++) {
					Wt_navigation nav = list.get(i);
					Integer naDisplay = nav.getNaDisplay();
					Integer naId = nav.getNaId();
					if (naDisplay == 1 && naId != naId1) {
						flag++;
						if (flag >=3) {
							map.put("status",false);
							map.put("msg","能显示的导航已经设置超过了5个，编辑失败");
							return map;
						}
					}
				}
			}
			BeanUtils.populate(wt_navigation, navMap);
			int i = wtnavigationService.updateByPrimaryKeySelective(wt_navigation);
			if (i == 1) {
				map.put("status",true);
				map.put("msg","修改成功");
			}
			return map;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return map;
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			return map;
		}
	}

	

    /**
     * MD5加密
     * @param password
     * @return
     */
    public String encodePassword(String password){
        String algorithm = "MD5";
        char[] encodeHex  = null;
        try {
            MessageDigest instance = MessageDigest.getInstance(algorithm);
            //MD5加密后密文
            byte[] digest = instance.digest(password.getBytes());
            //十六进制再加密一次
            encodeHex = Hex.encodeHex(digest);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return new String(encodeHex);
    }
}
