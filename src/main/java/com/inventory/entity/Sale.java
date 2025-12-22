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
 * 销售出库记录
 * </p>
 *
 * @author deriou
 * @since 2025-12-15
 */
@Getter
@Setter
@ToString
@TableName("sale")
public class Sale implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 记录ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 关联商品ID
     */
    @TableField("product_id")
    private Long productId;

    /**
     * 销售数量
     */
    @TableField("quantity")
    private Integer quantity;

    /**
     * 销售时的单价
     */
    @TableField("sale_price")
    private BigDecimal salePrice;

    /**
     * 销售总金额
     */
    @TableField("total_amount")
    private BigDecimal totalAmount;

    /**
     * 本单利润 (总额 - 成本*数量)
     */
    @TableField("profit")
    private BigDecimal profit;

    /**
     * 收银员ID
     */
    @TableField("operator_id")
    private Long operatorId;

    /**
     * 销售时间
     */
    @TableField("sale_time")
    private LocalDateTime saleTime;

    // 在类的最后，Getter/Setter 之前加入
    @TableField(exist = false) // 表示数据库表中不存在此列
    private String productName;
}
