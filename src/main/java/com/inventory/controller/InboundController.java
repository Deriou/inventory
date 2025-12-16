package com.inventory.controller;

import com.inventory.entity.Inbound;
import com.inventory.service.IInboundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 进货入库单 前端控制器
 * </p>
 *
 * @author deriou
 * @since 2025-12-15
 */
@RestController
@RequestMapping("/inbound")
public class InboundController {
    @Autowired
    private IInboundService inboundService;

    //提交进货单接口
    @PostMapping("/add")
    public Map<String,Object> add(@RequestBody Inbound inbound){
        Map<String,Object> result = new HashMap<>();
        try {
            inboundService.createInbound(inbound);
            result.put("success",true);
            result.put("msg","下单成功");
        }catch (Exception e){
            result.put("success",false);
            result.put("msg","下单失败："+e.getMessage());
        }
        return result;
    }

    //确认收货接口
    @PostMapping("/confirm/{id}")
    public Map<String,Object> confirm(@PathVariable long id){
        Map<String,Object> result=new HashMap<>();
        try{
            inboundService.confirmInbound(id);
            result.put("success",true);
            result.put("msg","入库成功");
        }catch (Exception e){
            result.put("success",false);
            result.put("msg","入库失败："+e.getMessage());
        }
        return result;
    }

    //查询列表
    @GetMapping("/list")
    public Object list(){
        return inboundService.list();
    }
}
