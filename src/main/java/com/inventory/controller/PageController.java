package com.inventory.controller;

import com.inventory.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @Autowired
    private IProductService productService;

    //首页
    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/page/inbound")
    public String inboundPage(){
        return "inbound";
    }

    @GetMapping("/page/sale")
    public String salePage() {
        return "sale";
    }

    // 商品管理页
    @GetMapping("/page/product")
    public String productPage(Model model) {
        model.addAttribute("products", productService.list());
        return "product";
    }
}
