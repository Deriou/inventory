package com.inventory.controller;

import com.inventory.entity.Inbound;
import com.inventory.entity.Sale;
import com.inventory.service.IInboundService;
import com.inventory.service.ISaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 财务管理控制器
 * 注：这里改用了 @Controller，因为我们要返回 Thymeleaf 页面，而不是 JSON 数据
 */
@Controller
public class FinanceController {

    @Autowired
    private ISaleService saleService;

    @Autowired
    private IInboundService inboundService;

    @GetMapping("/page/finance/flow")
    public String flowPage(Model model) {
        List<Sale> sales = saleService.list();

        List<Inbound> inbounds = inboundService.list();

        model.addAttribute("sales", sales);
        model.addAttribute("inbounds", inbounds);

        return "finance_flow";
    }

    @GetMapping("/page/finance/chart")
    public String chartPage(Model model) {
        List<Sale> allSales = saleService.list();
        List<Inbound> allInbounds = inboundService.list();

        List<String> months = new ArrayList<>();
        List<BigDecimal> incomes = new ArrayList<>();
        List<BigDecimal> expenses = new ArrayList<>();

        List<Map<String, Object>> tableData = new ArrayList<>();

        YearMonth current = YearMonth.now();

        for (int i = 5; i >= 0; i--) {
            YearMonth targetMonth = current.minusMonths(i);
            String monthStr = targetMonth.format(DateTimeFormatter.ofPattern("yyyy-MM"));
            months.add(monthStr);

            BigDecimal monthIncome = allSales.stream()
                    .filter(s -> s.getSaleTime() != null && YearMonth.from(s.getSaleTime()).equals(targetMonth))
                    .map(Sale::getTotalAmount)
                    .filter(amount -> amount != null)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            incomes.add(monthIncome);

            BigDecimal monthExpense = allInbounds.stream()
                    .filter(in -> in.getStatus() != null && in.getStatus() == 1) // 必须是已入库
                    .filter(in -> in.getCreateTime() != null && YearMonth.from(in.getCreateTime()).equals(targetMonth))
                    .map(in -> {
                        BigDecimal price = in.getPurchasePrice() != null ? in.getPurchasePrice() : BigDecimal.ZERO;
                        BigDecimal qty = new BigDecimal(in.getQuantity() != null ? in.getQuantity() : 0);
                        return price.multiply(qty);
                    })
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            expenses.add(monthExpense);

            Map<String, Object> row = new HashMap<>();
            row.put("month", monthStr);
            row.put("totalIncome", monthIncome);
            row.put("totalExpense", monthExpense);
            row.put("profit", monthIncome.subtract(monthExpense));
            tableData.add(0, row);
        }

        BigDecimal totalIncome = incomes.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalExpense = expenses.stream().reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Object> chartData = new HashMap<>();
        chartData.put("months", months);
        chartData.put("incomes", incomes);
        chartData.put("expenses", expenses);
        chartData.put("totalIncome", totalIncome);
        chartData.put("totalExpense", totalExpense);

        model.addAttribute("chartData", chartData);
        model.addAttribute("tableData", tableData);

        return "finance_chart";
    }
}