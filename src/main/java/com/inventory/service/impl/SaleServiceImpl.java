package com.inventory.service.impl;

import com.inventory.entity.Product;
import com.inventory.entity.Sale;
import com.inventory.mapper.ProductMapper;
import com.inventory.mapper.SaleMapper;
import com.inventory.service.ISaleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
    @Autowired
    private ProductMapper productMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createSale(Sale sale) {
        Product product = productMapper.selectById(sale.getProductId());
        //检查
        if(product == null){
            throw new RuntimeException("商品不存在");
        }
        if(product.getStock()<sale.getQuantity()){
            throw new RuntimeException("库存不足，当前仅剩："+product.getStock());
        }

        //减库存
        product.setStock(product.getStock()-sale.getQuantity());
        productMapper.updateById(product);

        //计算利润
        BigDecimal costPrice = product.getCostPrice();
        BigDecimal salePrice = sale.getSalePrice();
        BigDecimal profitPerUnit=salePrice.subtract(costPrice);//单件利润
        BigDecimal totalProfit = profitPerUnit.multiply(BigDecimal.valueOf(sale.getQuantity()));

        sale.setProfit(totalProfit);
        BigDecimal totalAmount = salePrice.multiply(BigDecimal.valueOf(sale.getQuantity()));
        sale.setTotalAmount(totalAmount);
        sale.setSaleTime(LocalDateTime.now());
        return save(sale);
    }
}
