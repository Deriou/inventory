package com.inventory.controller;

import com.baomidou.mybatisplus.core.conditions.query.Query;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.inventory.entity.Inbound;
import com.inventory.entity.Product;
import com.inventory.entity.Sale;
import com.inventory.entity.SysUser;
import com.inventory.service.IInboundService;
import com.inventory.service.IProductService;
import com.inventory.service.ISaleService;
import com.inventory.service.ISysUserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
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

    @GetMapping({"/", "/index"})
    public String index(Model model) {

        String today = LocalDate.now().toString();
        List<Sale> allSales = saleService.list();

        BigDecimal todaySales = allSales.stream()
                .filter(s -> s.getSaleTime().toString().startsWith(today))
                .map(Sale::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long pendingInbound = inboundService.count(new QueryWrapper<Inbound>().eq("status", 0));

        long productCount = productService.count();

        List<Product> products = productService.list();
        long warningCount = products.stream().filter(p -> p.getStock() < p.getWarningNum()).count();

        Map<String, Object> stats = new HashMap<>();
        stats.put("todaySales", todaySales);
        stats.put("pendingInbound", pendingInbound);
        stats.put("productCount", productCount);
        stats.put("warningCount", warningCount);
        model.addAttribute("stats", stats);

        List<String> dates = new ArrayList<>();
        List<BigDecimal> salesData = new ArrayList<>();

        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            String dateStr = date.toString();

            BigDecimal daySum = allSales.stream()
                    .filter(s -> s.getSaleTime().toString().startsWith(dateStr))
                    .map(Sale::getTotalAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            dates.add(date.format(DateTimeFormatter.ofPattern("MM-dd")));
            salesData.add(daySum);
        }

        model.addAttribute("chartDates", dates);
        model.addAttribute("chartValues", salesData);

        return "index";
    }

    //退出登录
    @GetMapping("/sysUser/logout")
    public String logout(HttpSession session){
        session.invalidate();
        return "redirect:/login";
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
        fillInboundProductName(list);

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
        QueryWrapper<Product> query = new QueryWrapper<>();
        query.apply("stock<warning_num");
        query.orderByAsc("stock");
        List<Product> warningProducts = productService.list(query);
        model.addAttribute("products", warningProducts);
        return "stock_warning";
    }

    //系统管理
    @GetMapping("/page/system/user/list")
    public String userList(Model model,@RequestParam(required = false) String name) {
            QueryWrapper<SysUser> query = new QueryWrapper<>();
            if(StringUtils.isNotBlank(name)){
                query.and(wrapper -> wrapper.like("username", name)
                        .or()
                        .like("real_name", name));
            }
            query.orderByDesc("create_time");
            List<SysUser> userList = sysUserService.list(query);
            model.addAttribute("users", userList);
            model.addAttribute("searchName", name);
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
