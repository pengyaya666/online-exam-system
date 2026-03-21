package com.exam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.exam.dto.SysNoticeDTO;
import com.exam.entity.SysNotice;
import com.exam.entity.User;
import com.exam.mapper.SysNoticeMapper;
import com.exam.mapper.UserMapper;
import com.exam.service.SysNoticeService;
import com.exam.vo.PageResult;
import com.exam.vo.SysNoticeVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SysNoticeServiceImpl extends ServiceImpl<SysNoticeMapper, SysNotice> implements SysNoticeService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public PageResult<SysNoticeVO> getNoticePage(Integer page, Integer size, Integer status, String keyword) {
        LambdaQueryWrapper<SysNotice> wrapper = new LambdaQueryWrapper<>();

        if (status != null) {
            wrapper.eq(SysNotice::getStatus, status);
        }

        if (StringUtils.hasText(keyword)) {
            wrapper.like(SysNotice::getTitle, keyword)
                    .or()
                    .like(SysNotice::getContent, keyword);
        }

        wrapper.orderByDesc(SysNotice::getCreateTime);

        Page<SysNotice> pageResult = baseMapper.selectPage(new Page<>(page, size), wrapper);

        List<SysNoticeVO> records = pageResult.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return new PageResult<>(pageResult.getTotal(), records, pageResult.getCurrent(), pageResult.getSize());
    }

    @Override
    public SysNoticeVO getNoticeById(Long id) {
        SysNotice notice = baseMapper.selectById(id);
        if (notice == null) {
            throw new RuntimeException("公告不存在");
        }
        return convertToVO(notice);
    }

    @Override
    public void saveNotice(SysNoticeDTO dto, Long userId) {
        SysNotice notice = new SysNotice();
        BeanUtils.copyProperties(dto, notice);
        notice.setCreateBy(userId);
        baseMapper.insert(notice);
    }

    @Override
    public void updateNotice(SysNoticeDTO dto) {
        SysNotice existNotice = baseMapper.selectById(dto.getId());
        if (existNotice == null) {
            throw new RuntimeException("公告不存在");
        }

        BeanUtils.copyProperties(dto, existNotice);
        baseMapper.updateById(existNotice);
    }

    @Override
    public void deleteNotice(Long id) {
        baseMapper.deleteById(id);
    }

    @Override
    public SysNoticeVO getCurrentNotice() {
        LocalDateTime now = LocalDateTime.now();
        LambdaQueryWrapper<SysNotice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysNotice::getStatus, 1)
                .le(SysNotice::getStartTime, now)
                .ge(SysNotice::getEndTime, now)
                .orderByDesc(SysNotice::getCreateTime)
                .last("limit 1");

        SysNotice notice = baseMapper.selectOne(wrapper);
        if (notice == null) {
            return null;
        }
        return convertToVO(notice);
    }

    private SysNoticeVO convertToVO(SysNotice notice) {
        SysNoticeVO vo = new SysNoticeVO();
        BeanUtils.copyProperties(notice, vo);

        // 设置类型名称
        if (notice.getType() != null) {
            switch (notice.getType()) {
                case 1:
                    vo.setTypeName("系统公告");
                    break;
                case 2:
                    vo.setTypeName("考试通知");
                    break;
                default:
                    vo.setTypeName("未知");
            }
        }

        // 设置状态名称
        if (notice.getStatus() != null) {
            vo.setStatusName(notice.getStatus() == 1 ? "启用" : "禁用");
        }

        // 设置创建人名称
        if (notice.getCreateBy() != null) {
            User user = userMapper.selectById(notice.getCreateBy());
            if (user != null) {
                vo.setCreateByName(user.getRealName());
            }
        }

        return vo;
    }
}