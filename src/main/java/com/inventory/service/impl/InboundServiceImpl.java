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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.math.RoundingMode;

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
    // 找到 createInbound 方法，替换为以下内容
    @Override
    public boolean createInbound(Inbound inbound) {
        inbound.setStatus(0);
        inbound.setCreateTime(LocalDateTime.now());

        // === 新增：自动计算总金额 ===
        if (inbound.getPurchasePrice() != null && inbound.getQuantity() != null) {
            BigDecimal total = inbound.getPurchasePrice().multiply(new BigDecimal(inbound.getQuantity()));
            inbound.setTotalAmount(total);
        }
        // =========================

        return this.save(inbound);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirmInbound(long inboundId) {
        Inbound inbound = this.getById(inboundId);
        if(inbound==null) throw new RuntimeException("找不到进货单");
        if(inbound.getStatus()!=0) throw new RuntimeException("单据状态错误");

        // 更新进货单状态
        inbound.setStatus(1);
        inbound.setUpdateTime(LocalDateTime.now());
        this.updateById(inbound);

        // 更新商品库存和成本
        Product product = productMapper.selectById(inbound.getProductId());
        if(product != null){
            // 1. 计算加权平均成本
            // 公式：(旧库存金额 + 本次进货金额) / 总数量

            BigDecimal oldStock = new BigDecimal(product.getStock()); // 旧库存
            BigDecimal oldCost = product.getCostPrice();              // 旧成本
            BigDecimal newQty = new BigDecimal(inbound.getQuantity()); // 新进货量
            BigDecimal newCost = inbound.getPurchasePrice();           // 新进货价

            // 旧总值 = 旧库存 * 旧成本
            BigDecimal oldValue = oldStock.multiply(oldCost);
            // 新总值 = 新进货量 * 新进货价
            BigDecimal newValue = newQty.multiply(newCost);

            // 总数量
            BigDecimal totalQty = oldStock.add(newQty);

            // 防止除以0（虽然逻辑上确认收货时肯定有数量，但为了安全）
            if (totalQty.compareTo(BigDecimal.ZERO) > 0) {
                // 计算平均价，保留2位小数，四舍五入
                BigDecimal avgPrice = oldValue.add(newValue).divide(totalQty, 2, RoundingMode.HALF_UP);
                product.setCostPrice(avgPrice); // 设置新的加权平均成本
            }

            // 2. 更新库存数量
            product.setStock(product.getStock() + inbound.getQuantity());

            // 3. 保存
            productMapper.updateById(product);
        }
    }

}
