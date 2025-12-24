package com.inventory.controller;

import com.inventory.service.ISaleService;
import com.inventory.entity.Sale;
import com.inventory.entity.SysUser;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 销售出库记录 前端控制器
 * </p>
 *
 * @author deriou
 * @since 2025-12-15
 */
@RestController
@RequestMapping("/sale")
public class SaleController {

    @Autowired
    private ISaleService saleService;

    @PostMapping("/add")
    public Map<String, Object> add(@RequestBody Sale sale) {
        Map<String, Object> result = new HashMap<>();
        try {
            saleService.createSale(sale);
            result.put("success", true);
            result.put("msg", "销售成功！利润已记录。");
        } catch (Exception e) {
            result.put("success", false);
            result.put("msg", "销售失败：" + e.getMessage());
        }
        return result;
    }

    @GetMapping("/list")
    public Object list() {
        return saleService.list();
    }
}
