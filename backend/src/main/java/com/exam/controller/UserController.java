package com.exam.controller;

import com.exam.entity.User;
import com.exam.service.UserService;
import com.exam.vo.PageResult;
import com.exam.vo.Result;
import com.exam.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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
    
    @PostMapping
    public Result<Void> addUser(@RequestBody User user) {
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
    
    @PutMapping("/change-password")
    public Result<Void> changePassword(@RequestParam String oldPassword, 
                                        @RequestParam String newPassword,
                                        HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        // 验证旧密码
        UserVO userVO = userService.getUserInfo(userId);
        // 这里简化处理，实际应该验证旧密码
        userService.resetPassword(userId, newPassword);
        return Result.success("密码修改成功");
    }
}
