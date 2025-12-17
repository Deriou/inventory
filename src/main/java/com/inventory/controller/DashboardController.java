package com.inventory.controller;

import com.inventory.entity.Product;
import com.inventory.entity.Sale;
import com.inventory.service.IProductService;
import com.inventory.service.ISaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class DashboardController {
    @Autowired
    private IProductService productService;
    private ISaleService saleService;

    @GetMapping("/dashboard/data")
    public Map<String, Object> getDashboardData() {
        Map<String, Object> result = new HashMap<>();

        // 统计缺货商品数量
        List <Product> products =productService.list();
        long lowStockCount = products.stream()
                .filter(p -> p.getStock() < p.getWarningNum())
                .count();
        result.put("lowStockCount", lowStockCount);

        //统计今日销售额
        List<Sale> allSales = saleService.list();
        BigDecimal todayIncome = allSales.stream()
                .filter(s -> s.getSaleTime().toString().startsWith(LocalDate.now().toString()))
                .map(Sale::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        result.put("todayIncome", todayIncome);

        // 最近 7 天销售数据准备
        List<String> dates = new ArrayList<>();
        List<BigDecimal> values = new ArrayList<>();

        // 倒推过去 7 天
        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            String dateStr = date.toString();
            dates.add(dateStr);

            // 算出这一天的总销售额
            BigDecimal dailyTotal = allSales.stream()
                    .filter(s -> s.getSaleTime().toString().startsWith(dateStr))
                    .map(Sale::getTotalAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            values.add(dailyTotal);
        }

        result.put("chartDates", dates);
        result.put("chartValues", values);

        return result;
    }
}
