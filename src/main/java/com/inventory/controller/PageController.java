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
        model.addAttribute("inbound",inboundService.list());
        model.addAttribute("product",productService.list());
        return "inbound";
    }

    @GetMapping("/page/sale")
    public String salePage(Model model) {
        model.addAttribute("product",productService.list());
        return "sale_add";
    }

    // 商品管理页
    @GetMapping("/page/product")
    public String productPage(Model model) {
        model.addAttribute("products", productService.list());
        return "product";
    }
}
