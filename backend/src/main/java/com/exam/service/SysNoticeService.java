package com.exam.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.exam.dto.SysNoticeDTO;
import com.exam.entity.SysNotice;
import com.exam.vo.PageResult;
import com.exam.vo.SysNoticeVO;

public interface SysNoticeService extends IService<SysNotice> {

    PageResult<SysNoticeVO> getNoticePage(Integer page, Integer size, Integer status, String keyword);

    SysNoticeVO getNoticeById(Long id);

    void saveNotice(SysNoticeDTO dto, Long userId);

    void updateNotice(SysNoticeDTO dto);

    void deleteNotice(Long id);

    SysNoticeVO getCurrentNotice();
}