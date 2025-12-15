package com.inventory.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 商品信息表
 * </p>
 *
 * @author deriou
 * @since 2025-12-15
 */
@Getter
@Setter
@ToString
@TableName("product")
public class Product implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 商品ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 商品名称
     */
    @TableField("name")
    private String name;

    /**
     * 条形码(模拟扫码用)
     */
    @TableField("barcode")
    private String barcode;

    /**
     * 供应商
     */
    @TableField("supplier")
    private String supplier;

    /**
     * 当前库存数量
     */
    @TableField("stock")
    private Integer stock;

    /**
     * 库存预警阈值(低于此值标红)
     */
    @TableField("warning_num")
    private Integer warningNum;

    /**
     * 进货成本价
     */
    @TableField("cost_price")
    private BigDecimal costPrice;

    /**
     * 销售零售价
     */
    @TableField("sale_price")
    private BigDecimal salePrice;

    /**
     * 备注/描述
     */
    @TableField("description")
    private String description;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;
}
