package com.inventory.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.inventory.entity.Product;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 商品信息表 Mapper 接口
 * </p>
 *
 * @author deriou
 * @since 2025-12-15
 */
@Mapper
public interface ProductMapper extends BaseMapper<Product> {

}
