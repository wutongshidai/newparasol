package com.wutong.shop;

import com.alibaba.dubbo.config.annotation.Reference;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.parasol.core.mall.Wt_admin_expend;
import com.parasol.core.mall.Wt_goods;
import com.parasol.core.service.WtadminexpendService;
import com.parasol.core.service.WtgoodsService;
import net.sf.json.JSONArray;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * 
 * @author aliang
 * @since 2017/8/28
 */
@RestController
public class WtgoodsController {

    @Reference
	private WtgoodsService wtgoodsService;
//	@Autowired
//	private WtbrandService wtbrandService;
    @Reference
	private WtadminexpendService wtadminexpendService;
	
	
	 
	 
	@ResponseBody
	@RequestMapping("/deleteGoods.do")
	public int deleteByPrimaryKey(Integer gdId) {
		int i = wtgoodsService.deleteByPrimaryKey(gdId);
		return i;
	}

	/*
	 * 商品录入
	 */
	@ResponseBody
	@RequestMapping("/insertSelectiveGoods.do")
	public int insertSelective(Wt_goods record) {
		int i = wtgoodsService.insertSelective(record);
		return i;
	}
	
	/*
	 * 商品详情
	 */
	@ResponseBody
	@RequestMapping("/goodsDetail.do")
	public Map<String , Object> goodsDetail(Integer gdId) {
		Map<String , Object> map = new HashMap<>();
		Wt_goods goods = wtgoodsService.selectByPrimaryKey(gdId);
//		Wt_brand brand = wtbrandService.selectByPrimaryKey(goods.getBrandId());
		Wt_admin_expend shop = wtadminexpendService.selectByPrimaryKey(goods.getadExId());
		List<Wt_goods> list = wtgoodsService.selectGoodsGuess(gdId , goods.getadExId());
		map.put("goods", goods);
		map.put("shop", shop);
		map.put("list", list);
		return map;
	}
	
	/*
	 * 商品管理回显
	 */
	@ResponseBody
	@RequestMapping("/goodsDisplay.do")
	public Map<String , Object> goodsDisplay(Integer gdId) {
		Map<String , Object> map = new HashMap<>();
		Wt_goods goods = wtgoodsService.selectByPrimaryKey(gdId);
		map.put("goods", goods);
		return map;
	}

    /**
     * 修改上下架
     * @param map
     * @return
     */
    @ResponseBody
    @RequestMapping("/soldOut.do")
    public Map soldOut(@RequestBody Map map) {
        Map reMap = new HashMap();
        reMap.put("msg", "操作失败。请稍后");
        reMap.put("status", false);
        Integer gdId = (Integer) map.get("gdId");
        String gdDisplay = (String)map.get("gdDisplay");
        int i = wtgoodsService.soldOut(gdId, gdDisplay);
        if(i == 1){
            reMap.put("msg", "成功");
            reMap.put("status", true);
        }
        return reMap;
    }
		
	/*  
	 * 商品编辑............
	 */
/*	@ResponseBody
	@RequestMapping("/updateSelectiveGoods.do")
	public int updateKeySelective(Wt_goods record) {
		int i = wtgoodsService.updateByPrimaryKeySelective(record);
		return i;
	}*/

	/*
	 * 商品查询  
	 */
	@ResponseBody
	@RequestMapping("/updateSelectiveGoods.do")
	public int updateByPrimaryKeySelective(Wt_goods record) {
		int i = wtgoodsService.updateByPrimaryKeySelective(record);
		return i;
	}
	
	/*
	 * 本店其它商品
	 */	
	@ResponseBody
	@RequestMapping("/selectGoodGuess.do")
	public List<Wt_goods> selectGoodGuess(Integer gdId , Integer adExId) {
		List<Wt_goods> list = wtgoodsService.selectGoodsGuess(gdId , adExId);
		return list;
	}
	
	/*
	 * 店内搜索
	 */	
	@ResponseBody
	@RequestMapping("/selectByNameShop.do")
	public Map<String , Object> selectByNameShop(Integer page ,String gdName , Integer adExId) {
		Map<String,Object> map = new HashMap<>();
		List<Wt_goods> lists = wtgoodsService.selectByNameShop(page , gdName , adExId);
		Integer count = wtgoodsService.selectByNameShopCount(gdName, adExId);
		map.put("list", lists);
		map.put("count", count);
		return map;
	}
	
	
	/**
	 * @author biao
	 * @since 2017/10/9
	 */
	
    /**
     * 校验登录
     */
    @ResponseBody
    @RequestMapping("/checkLogin.do")
    public static Map checkLogin(HttpSession session) {
        Map map1 = new HashMap();
        Map userMap = (Map) session.getAttribute("userMap");
        if (userMap == null) {
            map1.put("msg", "登录失效，请重新登录");
            map1.put("status", false);
            return map1;
        } else {
            map1.put("status", true);
            return map1;
        }
    }

    /**
     * 创建商品
     *
     * @param map
     * @return 0 失败；1成功
     */
    @ResponseBody
    @RequestMapping("/insertGoods.do")
    public Map insert(@RequestBody Map map, HttpSession session) {
        Map map1 = new HashMap();
        map1.put("msg", "服务异常，请稍后再试");
        map1.put("status", false);
        if (session.getAttribute("userMap") == null) {
            map1.put("msg", "登录失效，请重新登录");
            map1.put("status", false);
            return map1;
        }
        int adExId = (Integer)map.get("adExId");
        Wt_goods record = new Wt_goods();
        try {
            // 判断是否填完整了拓展表信息                                        
            Wt_admin_expend wt_admin_expend = wtadminexpendService.selectByPrimaryKey(adExId);
            System.out.println(wt_admin_expend.toString());
            int flag = 0;
            for (Field f : wt_admin_expend.getClass().getDeclaredFields()) {
                f.setAccessible(true);
                if (("").equals(f.get(wt_admin_expend)) || f.get(wt_admin_expend) == null) { //判断字段是否为空，并且对象属性中的基本都会转为对象类型来判断
                    flag++;
                }

            }
            if (flag >1) {
                map1.put("msg", "信息未完整，请去店铺管理填写完整");
                return map1;
            }
            int i = 0;
            BeanUtils.populate(record, map);
            record.setGdTime(new Date());
            record.setGdUptime(new Date());
            record.setGdDisplay("0");
            i = wtgoodsService.insert(record);
            if (i == 1) {
                map1.put("msg", "创建商品成功");
                map1.put("status", true);
                return map1;
            } else {
                map1.put("msg", "添加失败请检查数据,稍后再试");
                map1.put("status", false);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            map1.put("msg", "服务异常，请稍后再试");
            map1.put("status", false);
            return map1;
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            map1.put("msg", "服务异常，请稍后再试");
            map1.put("status", false);
            return map1;
        }
        return map1;
    }

    /**
     * 查询商品列表
     * @param adExIdMap
     * @return
     */
    @ResponseBody
    @RequestMapping("/getGoods.do")
    public List getGoods(@RequestBody Map adExIdMap) {
        Integer adExId = (Integer)adExIdMap.get("adExId");
        Wt_goods goods = null;
        List<Wt_goods> arrayList =  wtgoodsService.selectGoods(adExId);
        return arrayList;
    }

    /**
     * 编辑商品
     * @param goodsMap
     * @return
     */
    @ResponseBody
    @RequestMapping("/editGoods.do")
    public Map editGoods(@RequestBody Map goodsMap) {
        Wt_goods wt_goods = new Wt_goods();
        Map map = new HashMap();
        map.put("status", false);
        map.put("msg", "编辑商品失败,请稍后再试");
        try {
            String gdTime =(String) goodsMap.get("gdTime");
//            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            Date date = null;
//            date = format.parse(gdTime);
//            goodsMap.put("gdTime", date);
            System.out.println(new Date());
            goodsMap.put("gdUptime",new Date());
            goodsMap.put("gdAddtime", new Date());
            BeanUtils.populate(wt_goods,goodsMap);
            int i = wtgoodsService.updateByPrimaryKey(wt_goods);
            if (i==1) {
                map.put("status", true);
                map.put("msg", "编辑商品成功");
            }
            return map;
            } catch (Exception e) {
                e.printStackTrace();
                return map;
            }
    }

    /*
     * 商品录入
     */
    @ResponseBody
    @RequestMapping(value = "/insertSelectiveGoods.do", method = {RequestMethod.POST, RequestMethod.GET})
    public int insertSelective(@RequestBody Map map) {
        System.out.println(map);
        Wt_goods goods = new Wt_goods();
        try {
            BeanUtils.populate(goods, map);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        System.out.println(goods);
//		Map<String, String[]> parameterMap = record.getParameterMap();
//		System.out.println(parameterMap);
//		String gdOrder = record.getParameter("gdOrder");
        System.out.println(map.toString());
        int i = wtgoodsService.insertSelective(goods);
        return i;
    }

    /**
     * 编辑商品
     */
    @ResponseBody
    @RequestMapping("/getWtCat")
    public JSONArray  getWtCat () {
        List list = wtgoodsService.selectCat();
        JSONArray jsonArray = JSONArray.fromObject(list);
        System.out.println(jsonArray);
        return jsonArray;
    }




}
