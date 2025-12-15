package com.inventory.service;

import com.inventory.entity.Sale;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 销售出库记录 服务类
 * </p>
 *
 * @author deriou
 * @since 2025-12-15
 */
public interface ISaleService extends IService<Sale> {
    boolean createSale(Sale sale);
}
