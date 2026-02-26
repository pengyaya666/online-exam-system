package com.exam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.exam.dto.LoginDTO;
import com.exam.dto.RegisterDTO;
import com.exam.entity.User;
import com.exam.mapper.UserMapper;
import com.exam.service.UserService;
import com.exam.util.JwtUtil;
import com.exam.vo.LoginVO;
import com.exam.vo.PageResult;
import com.exam.vo.UserVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Override
    public LoginVO login(LoginDTO loginDTO) {
        User user = baseMapper.selectByUsername(loginDTO.getUsername());
        if (user == null) {
            throw new RuntimeException("用户名或密码错误");
        }
        
        if (user.getStatus() == User.STATUS_DISABLE) {
            throw new RuntimeException("账号已被禁用");
        }
        
        String encryptedPassword = DigestUtils.md5DigestAsHex(
                loginDTO.getPassword().getBytes(StandardCharsets.UTF_8));
        
        if (!encryptedPassword.equals(user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }
        
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        String refreshToken = jwtUtil.generateRefreshToken(user.getId());
        
        LoginVO loginVO = new LoginVO();
        loginVO.setToken(token);
        loginVO.setRefreshToken(refreshToken);
        loginVO.setExpireTime(System.currentTimeMillis() + jwtUtil.getExpiration());
        loginVO.setUserInfo(convertToVO(user));
        
        return loginVO;
    }
    
    @Override
    public void register(RegisterDTO registerDTO) {
        if (!registerDTO.getPassword().equals(registerDTO.getConfirmPassword())) {
            throw new RuntimeException("两次密码输入不一致");
        }
        
        User existUser = baseMapper.selectByUsername(registerDTO.getUsername());
        if (existUser != null) {
            throw new RuntimeException("用户名已存在");
        }
        
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(DigestUtils.md5DigestAsHex(
                registerDTO.getPassword().getBytes(StandardCharsets.UTF_8)));
        user.setRealName(registerDTO.getRealName());
        user.setEmail(registerDTO.getEmail());
        user.setPhone(registerDTO.getPhone());
        user.setRole(registerDTO.getRole() != null ? registerDTO.getRole() : User.ROLE_STUDENT);
        user.setStatus(User.STATUS_ENABLE);
        
        baseMapper.insert(user);
    }
    
    @Override
    public UserVO getUserInfo(Long userId) {
        User user = baseMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        return convertToVO(user);
    }
    
    @Override
    public PageResult<UserVO> getUserPage(Integer page, Integer size, Integer role, String keyword) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getDeleted, 0);
        
        if (role != null) {
            wrapper.eq(User::getRole, role);
        }
        
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(User::getUsername, keyword)
                    .or()
                    .like(User::getRealName, keyword));
        }
        
        wrapper.orderByDesc(User::getCreateTime);
        
        Page<User> pageResult = baseMapper.selectPage(new Page<>(page, size), wrapper);
        
        List<UserVO> records = pageResult.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        
        return new PageResult<>(pageResult.getTotal(), records, pageResult.getCurrent(), pageResult.getSize());
    }
    
    @Override
    public void addUser(User user) {
        User existUser = baseMapper.selectByUsername(user.getUsername());
        if (existUser != null) {
            throw new RuntimeException("用户名已存在");
        }
        
        user.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes(StandardCharsets.UTF_8)));
        user.setStatus(User.STATUS_ENABLE);
        baseMapper.insert(user);
    }
    
    @Override
    public void updateUser(User user) {
        User existUser = baseMapper.selectById(user.getId());
        if (existUser == null) {
            throw new RuntimeException("用户不存在");
        }
        
        if (!existUser.getUsername().equals(user.getUsername())) {
            User userWithSameName = baseMapper.selectByUsername(user.getUsername());
            if (userWithSameName != null) {
                throw new RuntimeException("用户名已存在");
            }
        }
        
        baseMapper.updateById(user);
    }
    
    @Override
    public void deleteUser(Long id) {
        baseMapper.deleteById(id);
    }
    
    @Override
    public void resetPassword(Long id, String newPassword) {
        User user = baseMapper.selectById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        user.setPassword(DigestUtils.md5DigestAsHex(newPassword.getBytes(StandardCharsets.UTF_8)));
        baseMapper.updateById(user);
    }
    
    @Override
    public void changeStatus(Long id, Integer status) {
        User user = baseMapper.selectById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        user.setStatus(status);
        baseMapper.updateById(user);
    }
    
    @Override
    public List<UserVO> getUserListByRole(Integer role) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getRole, role);
        wrapper.eq(User::getStatus, User.STATUS_ENABLE);
        wrapper.eq(User::getDeleted, 0);
        
        return baseMapper.selectList(wrapper).stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }
    
    private UserVO convertToVO(User user) {
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        
        switch (user.getRole()) {
            case 0:
                vo.setRoleName("管理员");
                break;
            case 1:
                vo.setRoleName("教师");
                break;
            case 2:
                vo.setRoleName("学生");
                break;
            default:
                vo.setRoleName("未知");
        }
        
        vo.setStatusName(user.getStatus() == 1 ? "启用" : "禁用");
        
        return vo;
    }
}
