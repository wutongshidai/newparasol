package com.wutong.shop;

import com.alibaba.dubbo.config.annotation.Reference;
import com.parasol.common.converter.Validator;
import com.parasol.core.mall.*;
import com.parasol.core.service.*;
import com.parasol.core.user.User;
import com.wutong.framework.core.web.common.http.ResponseResult;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * @author aliang
 * @since 2017/9/14
 */
@RestController
public class WtindexController {

    @Reference
    private WtadminexpendService wtadminexpendService;
    @Reference
    private WtgoodsService wtgoodsService;
    @Reference
    private WtcollectionService wtcollectionService;
    @Reference
    private WtfavouriteService wtfavouriteService;
    @Reference
    private WtadminexpendService wt_admin_expendService;
    @Reference
    private WtnavigationService wtnavigationService;
    @Reference
    private UserService userService;
/*	
	 * 商品录入
	 
	@ResponseBody
	@RequestMapping("/insertSelectiveGoods.do")
	public int insertSelectiveGoods(Wt_goods record) {
		int i = wtgoodsService.insertSelective(record);
		return i;
	}*/

    /*
     * 搜索条（模糊查询商品或店铺）
     */
    @ResponseBody
    @RequestMapping("/selectiveByName.do")
    public Map<String, Object> selectiveByName(Integer page, String name) {
        Map<String, Object> map = new HashMap<>();
//		List<Wt_admin_expend> list = wtadminexpendService.selectByName(name);
        List<Wt_goods> list2 = wtgoodsService.selectByName(page, name);
        Integer count = wtgoodsService.selectByNameCount(name);
        map.put("count", count);
        map.put("goods", list2);
        return map;
    }

    /*
     * 获取全部商品
     */
    @ResponseBody
    @RequestMapping("/selectGoodsAll.do")
    public Map<String, Object> selectGoodsAll(Integer page) {
        Map<String, Object> map = new HashMap<>();
        List<Wt_goods> list = wtgoodsService.selectGoodsAll(page);
        Integer count = wtgoodsService.selectGoodsCount();
        map.put("list", list);
        map.put("count", count);
        return map;
    }
	
/*	
	 * 获取分类商品(8个，一级)
	 
	@ResponseBody
	@RequestMapping("/selectGoodsByClass.do")
	public List<Wt_goods> selectGoodsByClass(Integer catPatentid) {
	 List<Wt_goods> list = wtgoodsService.selectGoodsByClass(catPatentid);
	 return list;
	}*/

    /*
     * 获取分类商品(全部,一级)
     */
    @ResponseBody
    @RequestMapping("/selectOneClass.do")
    public Map<String, Object> selectOneClass(Integer page, Integer catParentid) {
        Map<String, Object> map = new HashMap<>();
        List<Wt_goods> list = wtgoodsService.selectOneClass(page, catParentid);
        Integer count = wtgoodsService.selectGoodsOneCount(catParentid);
        map.put("list", list);
        map.put("count", count);
        return map;
    }

    /*
     * 获取分类商品(全部,二级)
     */
    @ResponseBody
    @RequestMapping("/selectGoodsTwoClass.do")
    public Map<String, Object> selectTwoClass(Integer page, Integer catId) {
        Map<String, Object> map = new HashMap<>();
        List<Wt_goods> list = wtgoodsService.selectGoodsByAllClass(page, catId);
        Integer count = wtgoodsService.selectGoodsTwoCount(catId);
        map.put("list", list);
        map.put("count", count);
        return map;
    }

    /*
     * 获取分类商品(全部,三级)
     */
    @ResponseBody
    @RequestMapping("/selectGoodsByThreeClass.do")
    public Map<String, Object> selectGoodsByThreeClass(Integer page, Integer catSonid) {
        Map<String, Object> map = new HashMap<>();
        List<Wt_goods> list = wtgoodsService.selectGoodsByThreeClass(page, catSonid);
        Integer count = wtgoodsService.selectGoodsThreeCount(catSonid);
        map.put("list", list);
        map.put("count", count);
        return map;
    }

    /*
     * 获取分类商品(12,三级)
     */
    @ResponseBody
    @RequestMapping("/selectThreeClass.do")
    public List<Wt_goods> selectThreeClass(Integer catSonid) {
        List<Wt_goods> list = wtgoodsService.selectThreeClass(catSonid);
        return list;
    }

    /*
     * 获取分类商品（价格排序)
     */
    @ResponseBody
    @RequestMapping("/selectGoodsByPrice.do")
    public Map<String, Object> selectGoodsByPrice(Integer page, Integer catSonid, Integer catId, Integer catParentid) {
        Map<String, Object> map = new HashMap<>();
        List<Wt_goods> list = wtgoodsService.selectGoodsByPrice(page, catSonid, catId, catParentid);
        Integer count = wtgoodsService.selectGradeCount(page, catSonid, catId, catParentid);
        map.put("list", list);
        map.put("count", count);
        return map;
    }

    /*
     * 获取分类商品(时间排序)
     */
    @ResponseBody
    @RequestMapping("/selectGoodsByTime.do")
    public Map<String, Object> selectGoodsByTime(Integer page, Integer catSonid, Integer catId, Integer catParentid) {
        Map<String, Object> map = new HashMap<>();
        List<Wt_goods> list = wtgoodsService.selectGoodsByTime(page, catSonid, catId, catParentid);
        Integer count = wtgoodsService.selectGradeCount(page, catSonid, catId, catParentid);
        map.put("list", list);
        map.put("count", count);
        return map;
    }

    /*
     * 商家首页
     */
    @ResponseBody
    @RequestMapping("/selectGoodsShop.do")
    public ResponseResult selectGoodsShop(Integer page, Integer adExId) {
        Map<String, Object> map = new HashMap<>();
        List<Wt_goods> list = wtgoodsService.selectGoodsShop(page, adExId);
        Integer count = wtgoodsService.selectShopCount(adExId);
        Wt_admin_expend wt_admin_expend = wtadminexpendService.selectByPrimaryKey(adExId);
        List<Wt_navigation> wt_navigations = wtnavigationService.selectByAd(adExId);
        List tjList = getRandomList(list, 4);
        if (list.size() < 4) {
            map.put("tjList", list);
        } else {
            map.put("tjList", tjList);
        }
        map.put("shop", wt_admin_expend);
        map.put("list", list);
        map.put("count", count);
        map.put("nav", wt_navigations);
        ResponseResult responseResult =new ResponseResult();
        responseResult.addData(map);
        return responseResult;
    }

    /*
     * 商家详情
     */
    @ResponseBody
    @RequestMapping("/selectShop.do")
    public Wt_admin_expend selectShop(Integer adExId) {
        Wt_admin_expend wt_admin_expend = wtadminexpendService.selectByPrimaryKey(adExId);
        return wt_admin_expend;
    }

    /*
     * 我的收藏（店铺）
     */
    @ResponseBody
    @RequestMapping("/selectCollectionShop.do")
    public List<Wt_admin_expend> selectByCollection(Integer userId) {
        List<Wt_collection> list = wtcollectionService.selectByUserId(userId);
        List<Wt_admin_expend> list2 = new ArrayList<>();
        for (Wt_collection wt_collection : list) {
            Wt_admin_expend key = wt_admin_expendService.selectByPrimaryKey(wt_collection.getAdExId());
            list2.add(key);
        }
        return list2;
    }

    /*
     * 我的收藏（商品）
     */
    @ResponseBody
    @RequestMapping("/selectGoods.do")
    public List<Wt_goods> selectByGoods(Integer userId) {
        List<Wt_favourite> list = wtfavouriteService.selectByUserId(userId);
        List<Wt_goods> list2 = new ArrayList<>();
        for (Wt_favourite wt_favourite : list) {
            Wt_goods key = wtgoodsService.selectByPrimaryKey(wt_favourite.getGdId());
            list2.add(key);
        }
        return list2;
    }

    /*
     * 删除我的收藏（商品）0为失败1为成功
     */
    @ResponseBody
    @RequestMapping("/deleteByUserIdFa.do")
    public Integer deleteByUserIdFa(Integer gdId, Integer userId) {
        Integer i = wtfavouriteService.deleteByGdId(gdId, userId);
        return i;
    }

    /*
     * 删除我的收藏（店铺）0为失败1为成功
     */
    @ResponseBody
    @RequestMapping("/deleteByUserIdCo.do")
    public Integer deleteByUserIdCo(Integer adExId, Integer userId) {
        Integer i = wtcollectionService.delectByAdExId(adExId, userId);
        return i;
    }

    /*
     * 添加我的收藏（商品）0为失败1为成功
     */
    @ResponseBody
    @RequestMapping("/insertFavourite.do")
    public Integer insertFavourite(Integer gdId, Integer userId) {
        wtfavouriteService.deleteByGdId(gdId, userId);
        Integer i = wtfavouriteService.insertFavourite(userId, gdId);
        return i;
    }

    /*
     * 添加我的收藏（商铺）0为失败1为成功
     */
    @ResponseBody
    @RequestMapping("/insertCollection.do")
    public Integer insertCollection(Integer adExId, Integer userId) {
        wtcollectionService.delectByAdExId(adExId, userId);
        Integer i = wtcollectionService.insertCollection(userId, adExId);

        return i;
    }

    /*
     * 添加导航
     */
    @ResponseBody
    @RequestMapping("/insertNavigation.do")
    public Integer insertNavigation(Wt_navigation record) {
        Integer insert = wtnavigationService.insert(record);
        return insert;
    }

    /*
     * 导航列表
     */
    @ResponseBody
    @RequestMapping("/selectByNavigation.do")
    public List<Wt_navigation> navigationList(Integer adExId) {
        List<Wt_navigation> ad = wtnavigationService.selectByAd(adExId);
        return ad;
    }

    /*
     * 删除导航
     */
    @ResponseBody
    @RequestMapping("/deleteNavigation.do")
    public Integer deleteNavigation(Integer naId) {
        Integer key = wtnavigationService.deleteByPrimaryKey(naId);
        return key;
    }

    /*
     * 编辑导航
     */
    @ResponseBody
    @RequestMapping("/updateNavigation.do")
    public Integer updateNavigation(Wt_navigation record) {
        Integer key = wtnavigationService.updateByPrimaryKey(record);
        return key;
    }

    /*
     * 商城首页
     */
    @ResponseBody
    @RequestMapping("/selectIndexs.do")
    public Map<String, Object> selectIndexs() {
        Map<String, Object> map = wtgoodsService.selectGoodsByClass();
        return map;
    }

    /**
     * 注册
     */
    @ResponseBody
    @RequestMapping(value = "/userLogin.do")
    public Map<String, Object> userLogin(String userName, String password
            , HttpServletRequest request, HttpServletResponse response) throws Exception {
        User user = new User();
        Map<String, Object> map = new HashMap<>();
        BeanUtils.populate(user, request.getParameterMap());
        if (Validator.isUsername(userName) || Validator.isPassword(password) || Validator.isEmail(user.getUserEmail()) ||
                Validator.isMobile(user.getMobile())) {
            Date date = new Date();
            user.setCreateTime(date);
            user.setPassword(encodePassword(password));
            System.out.println(user);
            userService.insert(user);
            map.put("user", user);
            map.put("message", "注册成功！");
        } else {
            map.put("message", "注册信息有误！");
        }
        return map;
    }

    /**
     * 登录
     *
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping(value = "/newSign.do")
    public Map<String, Object> newSign(String userName, String password
            , HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> map = new HashMap<>();
        User user = userService.selectUserByUsername(userName);
        if (null != user) {
            if (user.getPassword().equals(encodePassword(password))) {
                map.put("message", "登陆成功！");
                map.put("flag", "0");
                map.put("user", user);
                request.getSession().setAttribute("user", user);
            } else {
                map.put("message", "密码有误！");
                map.put("flag", "2");
            }
        } else {
            map.put("message", "用户信息有误！");
            map.put("flag", "1");
        }
        return map;
    }

    /**
     * 加密 MD5
     *
     * @param
     * @return
     */
    public String encodePassword(String password) {
        String algorithm = "MD5";
        char[] encodeHex = null;
        try {
            MessageDigest instance = MessageDigest.getInstance(algorithm);
            byte[] digest = instance.digest(password.getBytes());
            encodeHex = Hex.encodeHex(digest);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return new String(encodeHex);
    }

    public List getRandomList(List paramList, int count) {
        if (paramList.size() < count) {
            return paramList;
        }
        Random random = new Random();
        List<Integer> tempList = new ArrayList<Integer>();
        List<Object> newList = new ArrayList<Object>();
        int temp = 0;
        for (int i = 0; i < count; i++) {
            temp = random.nextInt(paramList.size());//将产生的随机数作为被抽list的索引
            if (!tempList.contains(temp)) {
                tempList.add(temp);
                newList.add(paramList.get(temp));
            } else {
                i--;
            }
        }
        return newList;
    }

}
