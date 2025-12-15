package com.inventory.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.inventory.entity.Sale;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 销售出库记录 Mapper 接口
 * </p>
 *
 * @author deriou
 * @since 2025-12-15
 */
@Mapper
public interface SaleMapper extends BaseMapper<Sale> {

}
