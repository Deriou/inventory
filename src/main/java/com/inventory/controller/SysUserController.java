package com.inventory.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.inventory.entity.SysUser;
import com.inventory.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

        QueryWrapper<SysUser> query = new QueryWrapper<>();
        query.eq("username", loginUser.getUsername());
        SysUser dbUser = sysUserService.getOne(query);

        if (dbUser == null) {
            result.put("success", false);
            result.put("msg", "账号不存在");
            return result;
        }

        if (!dbUser.getPassword().equals(loginUser.getPassword())) {
            result.put("success", false);
            result.put("msg", "密码错误");
            return result;
        }

        session.setAttribute("currentUser", dbUser);
        result.put("success", true);
        result.put("msg", "登录成功");
        result.put("role", dbUser.getRole());
        return result;
    }

    // === 2. 注销接口 ===
    @GetMapping("/logout")
    public void logout(HttpSession session) {
        session.invalidate();
    }

    // === 3. 用户列表 (支持模糊搜索) ===
    @GetMapping("/list")
    public Object list(@RequestParam(required = false) String name) {
        QueryWrapper<SysUser> query = new QueryWrapper<>();
        // 如果传了 name 参数，就模糊匹配账号或姓名
        if (StringUtils.isNotBlank(name)) {
            query.like("username", name).or().like("real_name", name);
        }
        return sysUserService.list(query);
    }

    // === 4. 新增用户 ===
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

    // === 5. 修改用户 (新功能) ===
    // === 5. 修改用户 (修改版：只改姓名和角色，不再处理密码) ===
    @PostMapping("/update")
    public Map<String, Object> update(@RequestBody SysUser user) {
        Map<String, Object> result = new HashMap<>();

        // 强制把密码设为 null，防止前端误传密码导致被意外修改
        user.setPassword(null);

        boolean success = sysUserService.updateById(user);
        result.put("success", success);
        result.put("msg", success ? "信息修改成功" : "修改失败");
        return result;
    }

    // === 6. 删除用户 ===
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

    // === 7. 单独修改密码接口 (新增) ===
    @PostMapping("/changePassword")
    public Map<String, Object> changePassword(@RequestBody Map<String, String> params) {
        Map<String, Object> result = new HashMap<>();

        String idStr = params.get("id");
        String oldPassword = params.get("oldPassword");
        String newPassword = params.get("newPassword");

        // 1. 基本校验
        if (StringUtils.isBlank(oldPassword) || StringUtils.isBlank(newPassword)) {
            result.put("success", false);
            result.put("msg", "原密码和新密码不能为空");
            return result;
        }

        // 2. 查询用户
        SysUser user = sysUserService.getById(idStr);
        if (user == null) {
            result.put("success", false);
            result.put("msg", "用户不存在");
            return result;
        }

        // 3. 校验原密码是否正确
        if (!user.getPassword().equals(oldPassword)) {
            result.put("success", false);
            result.put("msg", "❌ 原密码错误，无法修改！");
            return result;
        }

        // 4. 校验新密码是否和旧密码相同
        if (user.getPassword().equals(newPassword)) {
            result.put("success", false);
            result.put("msg", "⚠️ 新密码不能与旧密码相同！");
            return result;
        }

        // 5. 更新密码
        user.setPassword(newPassword);
        // 既然是改密码，通常不需要改 updateTime，或者看你需求
        // user.setUpdateTime(LocalDateTime.now());

        boolean success = sysUserService.updateById(user);
        result.put("success", success);
        result.put("msg", success ? "密码修改成功，请重新登录" : "修改失败");
        return result;
    }
}