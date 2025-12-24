package com.inventory.controller;

import com.inventory.entity.Inbound;
import com.inventory.entity.Product; // 引入 Product
import com.inventory.entity.Sale;
import com.inventory.service.IInboundService;
import com.inventory.service.IProductService; // 引入 ProductService
import com.inventory.service.ISaleService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class ReportController {

    @Autowired
    private ISaleService saleService;
    @Autowired
    private IInboundService inboundService;
    @Autowired
    private IProductService productService; // 注入商品服务

    @GetMapping("/report/export")
    public void exportReport(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv; charset=UTF-8");
        String fileName = URLEncoder.encode("经营报表.csv", StandardCharsets.UTF_8);
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

        PrintWriter writer = response.getWriter();
        writer.write('\uFEFF'); // BOM 头，防止 Excel 中文乱码

        // === 0. 准备商品名称映射 (ID -> Name) ===
        // 查出所有商品，转成 Map，方便后面用 ID 直接取名字
        List<Product> products = productService.list();
        Map<Long, String> productMap = products.stream()
                .collect(Collectors.toMap(Product::getId, Product::getName));

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // === 1. 销售报表 ===
        writer.println("【销售出库记录】");
        // 修改表头：加了 "商品名称"
        writer.println("流水号,商品ID,商品名称,数量,单价,总金额,利润,时间");

        List<Sale> sales = saleService.list();
        for (Sale s : sales) {
            String pName = productMap.getOrDefault(s.getProductId(), "未知商品");

            writer.printf("%d,%d,%s,%d,%.2f,%.2f,%.2f,%s%n",
                    s.getId(),
                    s.getProductId(),
                    pName, // 写入商品名称
                    s.getQuantity(),
                    s.getSalePrice(),
                    s.getTotalAmount(),
                    s.getProfit(),
                    s.getSaleTime().format(dtf));
        }
        writer.println();

        // === 2. 进货报表 ===
        writer.println("【采购入库记录】");
        // 修改表头：加了 "商品名称"
        writer.println("单号ID,商品ID,商品名称,数量,进价,总金额,状态,时间");

        List<Inbound> inbounds = inboundService.list();
        for (Inbound i : inbounds) {
            String pName = productMap.getOrDefault(i.getProductId(), "未知商品");

            writer.printf("%d,%d,%s,%d,%.2f,%.2f,%s,%s%n",
                    i.getId(),
                    i.getProductId(),
                    pName, // 写入商品名称
                    i.getQuantity(),
                    i.getPurchasePrice(),
                    (i.getTotalAmount() != null ? i.getTotalAmount() : 0.0),
                    (i.getStatus() == 1 ? "已入库" : "待入库"),
                    i.getCreateTime().format(dtf));
        }

        writer.flush();
        writer.close();
    }
}