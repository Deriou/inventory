package com.inventory.controller;

import com.inventory.entity.Product;
import com.inventory.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 商品信息表 前端控制器
 * </p>
 */
@RestController
@RequestMapping("/product")
public class ProductController {
    @Autowired
    private IProductService productService;

    // 1. 新增商品
    @PostMapping("/add")
    public Map<String, Object> addProduct(@RequestBody Product product) {
        Map<String, Object> result = new HashMap<>();
        product.setCreateTime(LocalDateTime.now());
        boolean success = productService.save(product);
        result.put("success", success);
        return result;
    }

    // 2. 修改商品 (新功能)
    @PostMapping("/update")
    public Map<String, Object> updateProduct(@RequestBody Product product) {
        Map<String, Object> result = new HashMap<>();
        // updateById 会根据 product 的 id 自动更新其他字段
        product.setUpdateTime(LocalDateTime.now());
        boolean success = productService.updateById(product);

        result.put("success", success);
        result.put("msg", success ? "修改成功" : "修改失败");
        return result;
    }

    // 3. 下架/删除商品 (新功能)
    @PostMapping("/delete/{id}")
    public Map<String, Object> deleteProduct(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        // 注意：实际业务中建议使用逻辑删除（只是改状态），这里为了演示简单直接物理删除
        boolean success = productService.removeById(id);

        result.put("success", success);
        result.put("msg", success ? "下架成功" : "下架失败");
        return result;
    }
}