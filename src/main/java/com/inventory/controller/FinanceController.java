package com.inventory.controller;

import com.inventory.entity.Inbound;
import com.inventory.entity.Sale;
import com.inventory.service.IInboundService;
import com.inventory.service.ISaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class FinanceController {

    @Autowired
    private ISaleService saleService;
    @Autowired
    private IInboundService inboundService;

    @GetMapping("/finance/data")
    public Map<String, Object> getFinanceData() {
        Map<String, Object> result = new HashMap<>();

        // 获取所有数据（实际项目中应只查最近的数据以优化性能）
        List<Sale> sales = saleService.list();
        List<Inbound> inbounds = inboundService.list();

        // --- 1. 计算总累计数据 (加入判空逻辑，防止 NPE 报错) ---

        // 总收入 (Total Revenue)
        BigDecimal totalRevenue = sales.stream()
                .map(Sale::getTotalAmount)
                .filter(amount -> amount != null) // 过滤掉空值
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 总利润 (Total Profit)
        BigDecimal totalProfit = sales.stream()
                .map(Sale::getProfit)
                .filter(profit -> profit != null) // 过滤掉空值
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 总成本 (Total Cost) - 只统计已入库(status=1)的
        BigDecimal totalCost = inbounds.stream()
                .filter(i -> i.getStatus() != null && i.getStatus() == 1)
                .map(Inbound::getTotalAmount)
                .filter(amount -> amount != null) // 过滤掉空值
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        result.put("totalRevenue", totalRevenue);
        result.put("totalProfit", totalProfit);
        result.put("totalCost", totalCost);

        // --- 2. 图表数据 (最近7天) ---
        List<String> dates = new ArrayList<>();
        List<BigDecimal> incomeList = new ArrayList<>();
        List<BigDecimal> expenseList = new ArrayList<>();

        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            String dateStr = date.toString();
            dates.add(dateStr); // 横轴日期

            // 当日收入
            BigDecimal dayIncome = sales.stream()
                    .filter(s -> s.getSaleTime() != null && s.getSaleTime().toString().startsWith(dateStr))
                    .map(Sale::getTotalAmount)
                    .filter(amount -> amount != null)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            incomeList.add(dayIncome);

            // 当日支出
            BigDecimal dayExpense = inbounds.stream()
                    .filter(in -> in.getStatus() != null && in.getStatus() == 1)
                    .filter(in -> in.getUpdateTime() != null && in.getUpdateTime().toString().startsWith(dateStr))
                    .map(Inbound::getTotalAmount)
                    .filter(amount -> amount != null)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            expenseList.add(dayExpense);
        }

        result.put("dates", dates);
        result.put("incomes", incomeList);
        result.put("expenses", expenseList);

        return result;
    }
}