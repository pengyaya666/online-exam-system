package com.exam.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.util.ListUtils;
import com.exam.dto.UserImportDTO;
import com.exam.entity.User;
import com.exam.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

public class UserImportListener implements ReadListener<UserImportDTO> {

    private static final Logger log = LoggerFactory.getLogger(UserImportListener.class);
    private static final int BATCH_COUNT = 100;

    private UserService userService;
    private List<UserImportDTO> cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
    private int successCount = 0;
    private int failCount = 0;

    // 无参构造函数
    public UserImportListener() {
    }

    // setter方法
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void invoke(UserImportDTO dto, AnalysisContext context) {
        cachedDataList.add(dto);
        if (cachedDataList.size() >= BATCH_COUNT) {
            saveData();
            cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        saveData();
        log.info("所有数据解析完成！成功{}条，失败{}条", successCount, failCount);
    }

    private void saveData() {
        for (UserImportDTO dto : cachedDataList) {
            try {
                User user = new User();
                user.setUsername(dto.getUsername());
                user.setPassword(dto.getPassword());
                user.setRealName(dto.getRealName());
                user.setEmail(dto.getEmail());
                user.setPhone(dto.getPhone());
                user.setRole(dto.getRole());
                user.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
                user.setDeleted(0);

                userService.addUser(user);
                successCount++;
            } catch (Exception e) {
                log.error("导入失败: {}", e.getMessage());
                failCount++;
            }
        }
    }

    public int getSuccessCount() { return successCount; }
    public int getFailCount() { return failCount; }
}