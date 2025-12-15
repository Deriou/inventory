package com.inventory.service.impl;

import com.inventory.entity.Sale;
import com.inventory.mapper.SaleMapper;
import com.inventory.service.ISaleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 销售出库记录 服务实现类
 * </p>
 *
 * @author deriou
 * @since 2025-12-15
 */
@Service
public class SaleServiceImpl extends ServiceImpl<SaleMapper, Sale> implements ISaleService {

}
