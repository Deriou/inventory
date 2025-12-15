package com.inventory.service.impl;

import com.inventory.entity.Inbound;
import com.inventory.entity.Product;
import com.inventory.mapper.InboundMapper;
import com.inventory.mapper.ProductMapper;
import com.inventory.service.IInboundService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

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
    @Autowired
    private ProductMapper productMapper;

    //创建进货单
    @Override
    public boolean createInbound(Inbound inbound) {
        inbound.setStatus(0);
        inbound.setCreateTime(LocalDateTime.now());
        return this.save(inbound);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirmInbound(long inboundId) {
        Inbound inbound = this.getById(inboundId);
        if(inbound==null){
            throw new RuntimeException("找不到进货单");
        }
        if(inbound.getStatus()!=0){
            throw new RuntimeException("操作失败，只有待入库的单据才可收货");
        }
        inbound.setStatus(1);
        inbound.setUpdateTime(LocalDateTime.now());
        this.updateById(inbound);

        Product product = productMapper.selectById(inbound.getProductId());
        if(product!=null){
            product.setStock(product.getStock()+inbound.getQuantity());
            product.setCostPrice(inbound.getPurchasePrice());
            productMapper.updateById(product);
        }
    }

}
