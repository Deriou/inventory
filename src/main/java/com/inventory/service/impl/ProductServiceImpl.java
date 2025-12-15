package com.inventory.service.impl;

import com.inventory.entity.Product;
import com.inventory.mapper.ProductMapper;
import com.inventory.service.IProductService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 商品信息表 服务实现类
 * </p>
 *
 * @author deriou
 * @since 2025-12-15
 */
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements IProductService {

}
