package com.inventory.controller;

import com.inventory.entity.Product;
import com.inventory.service.IInboundService;
import com.inventory.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @Autowired
    private IProductService productService;

    @Autowired
    private IInboundService inboundService;

    @GetMapping("/page/inbound/create")
    public String inboundCreatePage(Model model) {
        model.addAttribute("product", productService.list());
        return "inbound_add";
    }

    //首页
    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/page/inbound")
    public String inboundPage(Model model) {
        model.addAttribute("inbounds",inboundService.list());
        model.addAttribute("products",productService.list());
        return "inbound";
    }

    // 在 com.inventory.controller.PageController 中

    @Autowired
    private com.inventory.service.ISaleService saleService; // 确保注入了 SaleService

    // 1. 销售历史列表页 (新)
    @GetMapping("/page/sale")
    public String saleListPage(Model model) {
        // 获取所有销售记录
        model.addAttribute("sales", saleService.list());
        return "sale"; // 对应 templates/sale.html
    }

    // 2. 收银台/新增销售页 (原有的改为这个路径)
    @GetMapping("/page/sale/add")
    public String saleAddPage(Model model) {
        // 收银台需要选择商品，所以要传 product
        model.addAttribute("products", productService.list());
        return "sale_add"; // 对应 templates/sale_add.html
    }

    // 商品管理页
    @GetMapping("/page/product")
    public String productPage(Model model) {
        model.addAttribute("products", productService.list());
        return "product";
    }
}
