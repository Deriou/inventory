package com.inventory;

import com.inventory.entity.Sale;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class saleServiceTest {
    @Autowired
    private com.inventory.service.ISaleService saleService;

    @Test
    void testSaleProcess() {

        Sale sale = new Sale();
        sale.setProductId(1L);
        sale.setQuantity(5);
        sale.setSalePrice(new java.math.BigDecimal("3.00"));

        try {
            saleService.createSale(sale);
            System.out.println("销售成功");
            System.out.println("这一单赚了: " + sale.getProfit() + "元");
        } catch (Exception e) {
            System.out.println("销售失败: " + e.getMessage());
        }
    }
}
