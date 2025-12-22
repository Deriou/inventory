package com.inventory.controller;

import com.inventory.entity.Product;
import com.inventory.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 商品管理控制器
 * </p>
 */
@Controller
public class ProductController {

    @Autowired
    private IProductService productService;

    @GetMapping("/page/product/list")
    public String listPage(Model model) {
        List<Product> list = productService.list();
        model.addAttribute("products", list);
        return "product_list";
    }

    @GetMapping("/page/product/add")
    public String addPage() {
        return "product_add";
    }

    @PostMapping("/product/add")
    @ResponseBody
    public Map<String, Object> addProduct(@RequestBody Product product) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (product.getStock() == null) {
                product.setStock(0);
            }
            product.setCreateTime(LocalDateTime.now());
            boolean success = productService.save(product);

            result.put("success", success);
            result.put("msg", success ? "添加成功" : "添加失败");
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("msg", "系统异常：" + e.getMessage());
        }
        return result;
    }

    @PostMapping("/product/update")
    @ResponseBody
    public Map<String, Object> updateProduct(@RequestBody Product product) {
        Map<String, Object> result = new HashMap<>();
        try {
            product.setUpdateTime(LocalDateTime.now());
            boolean success = productService.updateById(product);

            result.put("success", success);
            result.put("msg", success ? "修改成功" : "修改失败");
        } catch (Exception e) {
            result.put("success", false);
            result.put("msg", "修改异常");
        }
        return result;
    }


    @PostMapping("/product/delete/{id}")
    @ResponseBody
    public Map<String, Object> deleteProduct(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            boolean success = productService.removeById(id);
            result.put("success", success);
            result.put("msg", success ? "删除成功" : "删除失败");
        } catch (Exception e) {
            result.put("success", false);
            result.put("msg", "删除异常");
        }
        return result;
    }
}