package com.inventory.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.inventory.entity.Inbound;
import com.inventory.entity.Product;
import com.inventory.entity.Sale;
import com.inventory.entity.SysUser;
import com.inventory.service.IInboundService;
import com.inventory.service.IProductService;
import com.inventory.service.ISaleService;
import com.inventory.service.ISysUserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class PageController {

    @Autowired
    private IProductService productService;
    @Autowired
    private IInboundService inboundService;
    @Autowired
    private ISaleService saleService;
    @Autowired
    private ISysUserService sysUserService;

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/")
    public String index(Model model,HttpSession session) {
        if (session.getAttribute("currentUser") == null)
            return "redirect:/login";
        model.addAttribute("activeUri", "index");
        return "index";
    }

    //商品模块
    @GetMapping("/page/product/list")
    public String productList(Model model) {
        model.addAttribute("products",productService.list());
        model.addAttribute("activeGroup", "product");
        model.addAttribute("activeUri", "product_list");
        return "product_list";
    }

    @GetMapping("/page/product/add")
    public String productAdd(Model model) {
        model.addAttribute("products", productService.list());
        model.addAttribute("activeGroup", "inbound");
        model.addAttribute("activeUri", "inbound_create");
        return "inbound_create";
    }

    //采购模块
    @GetMapping("/page/inbound/create")
    public String inboundCreate(Model model) {
        model.addAttribute("products", productService.list());
        model.addAttribute("activeGroup", "inbound");
        model.addAttribute("activeUri", "inbound_create");
        return "inbound_create";
    }

    @GetMapping("/page/inbound/receive")
    public String inboundReceive(Model model) {
        QueryWrapper<Inbound> query = new QueryWrapper<>();
        query.eq("status", 0);
        List<Inbound> list = inboundService.list(query);
        fillInboundProductName(list);
        model.addAttribute("inbounds", list);
        model.addAttribute("activeGroup", "inbound");
        model.addAttribute("activeUri", "inbound_receive");
        return "inbound_receive";
    }

    @GetMapping("/page/inbound/list")
    public String inboundList(Model model) {
        List<Inbound> list = inboundService.list();
        fillInboundProductName(list); // 填充商品名称

        model.addAttribute("inbounds", list);
        model.addAttribute("activeGroup", "inbound");
        model.addAttribute("activeUri", "inbound_list");
        return "inbound_list";
    }

    //销售模块
    @GetMapping("/page/sale/create")
    public String saleCreate(Model model) {
        model.addAttribute("products", productService.list());
        model.addAttribute("activeGroup", "sale");
        model.addAttribute("activeUri", "sale_create");
        return "sale_create";
    }

    @GetMapping("/page/sale/list")
    public String saleList(Model model) {
        List<Sale> list = saleService.list();

        List<Product> products = productService.list();
        Map<Long, String> productMap = products.stream()
                .collect(Collectors.toMap(Product::getId, Product::getName));
        for (Sale s : list) {
            s.setProductName(productMap.getOrDefault(s.getProductId(), "未知商品"));
        }

        model.addAttribute("sales", list);
        model.addAttribute("activeGroup", "sale");
        model.addAttribute("activeUri", "sale_list");
        return "sale_list";
    }

    //库存模块
    @GetMapping("/page/stock/list")
    public String stockList(Model model) {
        model.addAttribute("products", productService.list());
        model.addAttribute("activeGroup", "stock");
        model.addAttribute("activeUri", "stock_list");
        return "stock_list";
    }

    @GetMapping("/page/stock/warning")
    public String stockWarning(Model model) {
        model.addAttribute("products", productService.list());
        model.addAttribute("activeGroup", "stock");
        model.addAttribute("activeUri", "stock_warning");
        return "stock_warning";
    }

    //财务模块
    @GetMapping("/page/finance/flow")
    public String financeFlow(Model model, HttpSession session) {
        if (!checkPermission(session, 1, 3)) return "redirect:/";

        model.addAttribute("sales", saleService.list());
        model.addAttribute("inbounds", inboundService.list());
        model.addAttribute("activeGroup", "finance");
        model.addAttribute("activeUri", "finance_flow");
        return "finance_flow";
    }

    @GetMapping("/page/finance/chart")
    public String financeChart(Model model, HttpSession session) {
        if (!checkPermission(session, 1, 3)) return "redirect:/";

        model.addAttribute("activeGroup", "finance");
        model.addAttribute("activeUri", "finance_chart");
        return "finance_chart";
    }

    //系统管理
    @GetMapping("/page/system/user/list")
    public String userList(Model model, HttpSession session) {
        if (!checkPermission(session, 1)) return "redirect:/"; // 只有管理员能进

        model.addAttribute("users", sysUserService.list());
        model.addAttribute("activeGroup", "system");
        model.addAttribute("activeUri", "user_list");
        return "user_list";
    }

    @GetMapping("/page/system/user/add")
    public String userAdd(Model model, HttpSession session) {
        if (!checkPermission(session, 1)) return "redirect:/"; // 只有管理员能进

        model.addAttribute("activeGroup", "system");
        model.addAttribute("activeUri", "user_add");
        return "user_add";
    }

    //辅助方法
    //填充进货单
    private void fillInboundProductName(List<Inbound> inbounds) {
        List<Product> products = productService.list();
        Map<Long, String> productMap = products.stream()
                .collect(Collectors.toMap(Product::getId, Product::getName));
        for (Inbound i : inbounds) {
            i.setProductName(productMap.getOrDefault(i.getProductId(), "未知商品"));
        }
    }

    //权限检验
    private boolean checkPermission(HttpSession session, int... allowedRoles) {
        SysUser user = (SysUser) session.getAttribute("currentUser");
        if (user == null) return false;
        for (int role : allowedRoles) {
            if (user.getRole() == role) return true;
        }
        return false;
    }
}
