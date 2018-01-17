package com.wutong.announcement;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import com.parasol.core.announcement.Announcement;
import com.parasol.core.service.AnnouncementService;
import com.wutong.framework.core.web.common.http.ResponseResult;

@RestController
@RequestMapping("/announcement")
public class AnnouncementController {

	private static final int DEFAULT_PAGE_SIZE = 10;
	
	@Reference
	private AnnouncementService announcementService;
	
	/**
	 * 查询公告列表
	 * @param columnId
	 * @param status
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	@RequestMapping(path="")
	public ResponseResult<PageInfo<Announcement>> index(Integer columnId, Integer status, Integer pageNo, Integer pageSize) {
		columnId = (columnId == null ? -1 : columnId);
		pageSize = (pageSize == null || pageSize == 0? DEFAULT_PAGE_SIZE : pageSize);
		pageNo = (pageNo == null || pageNo == 0 ? 1 : pageNo);
		status = (status == null ? -1 : status);
		PageInfo<Announcement> announcements = announcementService.list(columnId, status, -1, pageNo, pageSize);
		ResponseResult<PageInfo<Announcement>> pageInfo = new ResponseResult<>();
		pageInfo.addData(announcements);
		return pageInfo;
	}
	
	@RequestMapping("/findone")
	public ResponseResult<Announcement> findone(Integer announcementId) {
		Announcement announcement = announcementService.findById(announcementId);
		System.out.println(announcement);
		ResponseResult<Announcement> result = new ResponseResult<>();
		result.addData(announcement);
		return result;
	}
}
