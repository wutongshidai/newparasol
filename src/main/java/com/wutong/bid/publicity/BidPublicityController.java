package com.wutong.bid.publicity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import com.parasol.core.bid.BidPublicity;
import com.parasol.core.service.BidPublicityService;
import com.wutong.framework.core.web.common.http.ResponseResult;

@RestController
@RequestMapping("/publicity")
public class BidPublicityController {
	
	private static final int DEFAULT_PAGE_SIZE = 10;

	@Reference
	private BidPublicityService bidPublicityService;
	
	@PostMapping(path="")
	public ResponseResult<PageInfo<BidPublicity>> list(Integer industry, Integer pageNo, Integer pageSize) {
		industry = (industry == null ? -1 : industry);
		pageSize = (pageSize == null || pageSize == 0? DEFAULT_PAGE_SIZE : pageSize);
		pageNo = (pageNo == null || pageNo == 0 ? 1 : pageNo);
		PageInfo<BidPublicity> bidPublicities = bidPublicityService.list(industry, 1, pageNo, pageSize);
		ResponseResult<PageInfo<BidPublicity>> result = new ResponseResult<>();
		result.addData(bidPublicities);
		return result;
	}
	
	@GetMapping("/findone")
	public ResponseResult<BidPublicity> findone(int id) {
		ResponseResult<BidPublicity> result = new ResponseResult<>();
		result.addData(bidPublicityService.findById(id));
		return result;
	}
}
