package com.inventory.service.impl;

import com.inventory.entity.SysUser;
import com.inventory.mapper.SysUserMapper;
import com.inventory.service.ISysUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 系统用户表 服务实现类
 * </p>
 *
 * @author deriou
 * @since 2025-12-15
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements ISysUserService {

}
