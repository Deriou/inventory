package com.inventory.controller;

import com.inventory.entity.Inbound;
import com.inventory.entity.Product;
import com.inventory.entity.Sale;
import com.inventory.entity.SysUser;
import com.inventory.service.IInboundService;
import com.inventory.service.IProductService;
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
    private com.inventory.service.ISysUserService sysUserService;

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
        List<Inbound> inbounds = inboundService.list();
        List<Product> products = productService.list();

        // 创建一个 Map 用于快速查找：ID -> 名称
        Map<Long, String> productMap = products.stream()
                .collect(Collectors.toMap(Product::getId, Product::getName));

        // 遍历进货单，填入商品名称
        for (Inbound i : inbounds) {
            i.setProductName(productMap.getOrDefault(i.getProductId(), "未知商品"));
        }

        model.addAttribute("inbounds", inbounds);
        model.addAttribute("products", products); // 这个留着给弹窗下拉框用
        return "inbound";
    }

    // 在 com.inventory.controller.PageController 中

    @Autowired
    private com.inventory.service.ISaleService saleService; // 确保注入了 SaleService

    // 1. 销售历史列表页 (新)
    @GetMapping("/page/sale")
    public String saleListPage(Model model) {
        List<Sale> sales = saleService.list();
        List<Product> products = productService.list();

        // 同理，创建映射
        Map<Long, String> productMap = products.stream()
                .collect(Collectors.toMap(Product::getId, Product::getName));

        // 遍历销售单，填入商品名称
        for (Sale s : sales) {
            s.setProductName(productMap.getOrDefault(s.getProductId(), "未知商品"));
        }

        model.addAttribute("sales", sales);
        return "sale";
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

    // 在 PageController 中添加
    @GetMapping("/login")
    public String loginPage() {
        return "login"; // 对应 login.html
    }

    @GetMapping("/page/user")
    public String userPage(Model model, HttpSession session) {
        // 只有管理员(role=1)能看
        SysUser user = (SysUser) session.getAttribute("currentUser");
        // 这里做个简单拦截，如果不是管理员，踢回首页 (更好的做法是用拦截器)
        if (user == null || user.getRole() != 1) {
            return "redirect:/";
        }
        model.addAttribute("users", sysUserService.list()); // 注入所有用户数据
        return "sys_user"; // 对应 sys_user.html
    }

    @GetMapping("/page/finance")
    public String financePage(jakarta.servlet.http.HttpSession session) {
        // 简单权限校验：只有管理员(1)和财务(3)能进
        com.inventory.entity.SysUser user = (com.inventory.entity.SysUser) session.getAttribute("currentUser");
        if (user == null || (user.getRole() != 1 && user.getRole() != 3)) {
            return "redirect:/"; // 没权限踢回首页
        }
        return "finance"; // 对应 templates/finance.html
    }
}
