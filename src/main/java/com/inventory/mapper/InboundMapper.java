package com.inventory.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.inventory.entity.Inbound;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 进货入库单 Mapper 接口
 * </p>
 *
 * @author deriou
 * @since 2025-12-15
 */
@Mapper
public interface InboundMapper extends BaseMapper<Inbound> {

}
