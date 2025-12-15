package com.inventory.service.impl;

import com.inventory.entity.Inbound;
import com.inventory.mapper.InboundMapper;
import com.inventory.service.IInboundService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 进货入库单 服务实现类
 * </p>
 *
 * @author deriou
 * @since 2025-12-15
 */
@Service
public class InboundServiceImpl extends ServiceImpl<InboundMapper, Inbound> implements IInboundService {

}
