package com.exam.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.exam.dto.LoginDTO;
import com.exam.dto.RegisterDTO;
import com.exam.entity.User;
import com.exam.vo.LoginVO;
import com.exam.vo.PageResult;
import com.exam.vo.UserVO;

import java.util.List;

public interface UserService extends IService<User> {
    
    LoginVO login(LoginDTO loginDTO);
    
    void register(RegisterDTO registerDTO);
    
    UserVO getUserInfo(Long userId);
    
    PageResult<UserVO> getUserPage(Integer page, Integer size, Integer role, String keyword);
    
    void addUser(User user);
    
    void updateUser(User user);
    
    void deleteUser(Long id);
    
    void resetPassword(Long id, String newPassword);
    
    void changeStatus(Long id, Integer status);
    
    List<UserVO> getUserListByRole(Integer role);
}
