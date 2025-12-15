package com.inventory.service;

import com.inventory.entity.Inbound;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 进货入库单 服务类
 * </p>
 *
 * @author deriou
 * @since 2025-12-15
 */
public interface IInboundService extends IService<Inbound> {
    boolean createInbound(Inbound inbound);

    void confirmInbound(long inbound);
}
