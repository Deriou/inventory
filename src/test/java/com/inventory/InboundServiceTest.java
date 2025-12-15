package com.inventory;

import com.inventory.entity.Inbound;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class InboundServiceTest {

    @Autowired
    private com.inventory.service.IInboundService inboundService;
    @Test
    void textInboundProcess(){
        Inbound newOrder = new Inbound();
        newOrder.setProductId(1L);
        newOrder.setQuantity(10);
        newOrder.setPurchasePrice(new java.math.BigDecimal("2.00"));

        inboundService.createInbound(newOrder);
        System.out.println("1. 下单成功！订单ID: " + newOrder.getId() + "，当前状态: " + newOrder.getStatus());
        inboundService.confirmInbound(newOrder.getId());
    }
}
