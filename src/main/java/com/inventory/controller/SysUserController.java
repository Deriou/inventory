package com.inventory.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.inventory.entity.SysUser;
import com.inventory.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

// 关键点：Spring Boot 3 必须使用 jakarta.servlet
import jakarta.servlet.http.HttpSession;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/sysUser")
public class SysUserController {

    @Autowired
    private ISysUserService sysUserService;

    // === 1. 登录接口 ===
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody SysUser loginUser, HttpSession session) {
        Map<String, Object> result = new HashMap<>();

        // 查询用户名是否存在
        QueryWrapper<SysUser> query = new QueryWrapper<>();
        query.eq("username", loginUser.getUsername());
        SysUser dbUser = sysUserService.getOne(query);

        if (dbUser == null) {
            result.put("success", false);
            result.put("msg", "账号不存在");
            return result;
        }

        // 简单比对密码
        if (!dbUser.getPassword().equals(loginUser.getPassword())) {
            result.put("success", false);
            result.put("msg", "密码错误");
            return result;
        }

        // 登录成功，存入 Session
        session.setAttribute("currentUser", dbUser);

        result.put("success", true);
        result.put("msg", "登录成功");
        result.put("role", dbUser.getRole());
        return result;
    }

    // === 2. 注销接口 ===
    @GetMapping("/logout")
    public void logout(HttpSession session) {
        session.invalidate(); // 销毁 Session
    }

    // === 3. 用户列表 (查) ===
    @GetMapping("/list")
    public Object list() {
        return sysUserService.list();
    }

    // === 4. 新增用户 (增) ===
    @PostMapping("/add")
    public Map<String, Object> add(@RequestBody SysUser user) {
        Map<String, Object> result = new HashMap<>();
        QueryWrapper<SysUser> query = new QueryWrapper<>();
        query.eq("username", user.getUsername());
        if(sysUserService.count(query) > 0){
            result.put("success", false);
            result.put("msg", "用户名已存在！");
            return result;
        }

        user.setCreateTime(LocalDateTime.now());
        boolean success = sysUserService.save(user);
        result.put("success", success);
        return result;
    }

    // === 5. 修改用户 (改) ===
    @PostMapping("/update")
    public Map<String, Object> update(@RequestBody SysUser user) {
        Map<String, Object> result = new HashMap<>();
        boolean success = sysUserService.updateById(user);
        result.put("success", success);
        return result;
    }

    // === 6. 删除用户 (删) ===
    @PostMapping("/delete/{id}")
    public Map<String, Object> delete(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        if (id == 1) {
            result.put("success", false);
            result.put("msg", "超级管理员不能删除");
            return result;
        }
        boolean success = sysUserService.removeById(id);
        result.put("success", success);
        return result;
    }
}