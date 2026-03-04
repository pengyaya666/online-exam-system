package com.exam.controller;

import com.alibaba.excel.EasyExcel;
import com.exam.dto.UserImportDTO;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.exam.entity.User;
import com.exam.service.UserService;
import com.exam.vo.PageResult;
import com.exam.vo.Result;
import com.exam.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/page")
    public Result<PageResult<UserVO>> getUserPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer role,
            @RequestParam(required = false) String keyword) {

        PageResult<UserVO> result = userService.getUserPage(page, size, role, keyword);
        return Result.success(result);
    }

    @GetMapping("/list")
    public Result<List<UserVO>> getUserList(@RequestParam(required = false) Integer role) {
        List<UserVO> list = userService.getUserListByRole(role);
        return Result.success(list);
    }

    @GetMapping("/{id}")
    public Result<UserVO> getUserById(@PathVariable Long id) {
        UserVO userVO = userService.getUserInfo(id);
        return Result.success(userVO);
    }

    // 添加用户时检查是否有未删除的同名用户
    @PostMapping
    public Result<Void> addUser(@RequestBody User user) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, user.getUsername())
                .eq(User::getDeleted, 0);

        if (userService.count(wrapper) > 0) {
            return Result.error("用户名已存在");
        }

        user.setDeleted(0);
        userService.addUser(user);
        return Result.success("添加成功");
    }

    @PutMapping
    public Result<Void> updateUser(@RequestBody User user) {
        userService.updateUser(user);
        return Result.success("更新成功");
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return Result.success("删除成功");
    }

    @PutMapping("/{id}/reset-password")
    public Result<Void> resetPassword(@PathVariable Long id, @RequestParam String newPassword) {
        userService.resetPassword(id, newPassword);
        return Result.success("密码重置成功");
    }

    @PutMapping("/{id}/status")
    public Result<Void> changeStatus(@PathVariable Long id, @RequestParam Integer status) {
        userService.changeStatus(id, status);
        return Result.success("状态修改成功");
    }

    @PostMapping("/import")
    public Result<Map<String, Integer>> importUsers(@RequestParam("file") MultipartFile file) {
        Map<String, Integer> result = new HashMap<>();
        try {
            // 创建监听器（无参构造）
            com.exam.listener.UserImportListener listener = new com.exam.listener.UserImportListener();
            // 手动设置userService
            listener.setUserService(userService);

            // 读取Excel
            EasyExcel.read(file.getInputStream(), com.exam.dto.UserImportDTO.class, listener)
                    .sheet()
                    .doRead();

            result.put("success", listener.getSuccessCount());
            result.put("fail", listener.getFailCount());

            return Result.success(result);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error("文件读取失败");
        }
    }

    @GetMapping("/template")
    public void downloadTemplate(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-disposition", "attachment;filename=用户导入模板.xlsx");

        List<UserImportDTO> templateData = new ArrayList<>();
        // 可以加一行示例数据
        UserImportDTO example = new UserImportDTO();
        example.setUsername("示例用户");
        example.setPassword("123456");
        example.setRealName("张三");
        example.setEmail("zhangsan@test.com");
        example.setPhone("13888888888");
        example.setRole(2);
        example.setStatus(1);
        templateData.add(example);

        EasyExcel.write(response.getOutputStream(), UserImportDTO.class)
                .sheet("用户导入模板")
                .doWrite(templateData);
    }
}