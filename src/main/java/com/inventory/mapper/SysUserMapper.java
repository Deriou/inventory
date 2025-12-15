package com.inventory.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.inventory.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 系统用户表 Mapper 接口
 * </p>
 *
 * @author deriou
 * @since 2025-12-15
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

}
