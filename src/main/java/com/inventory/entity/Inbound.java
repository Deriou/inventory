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
 * 进货入库单
 * </p>
 *
 * @author deriou
 * @since 2025-12-15
 */
@Getter
@Setter
@ToString
@TableName("inbound")
public class Inbound implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 单据ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 关联商品ID
     */
    @TableField("product_id")
    private Long productId;

    /**
     * 进货数量
     */
    @TableField("quantity")
    private Integer quantity;

    /**
     * 进货单价
     */
    @TableField("purchase_price")
    private BigDecimal purchasePrice;

    /**
     * 进货总金额
     */
    @TableField("total_amount")
    private BigDecimal totalAmount;

    /**
     * 状态: 0-待入库(已下单), 1-已入库(库存已加), 2-已取消
     */
    @TableField("status")
    private Integer status;

    /**
     * 操作员ID
     */
    @TableField("operator_id")
    private Long operatorId;

    /**
     * 制单时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 入库/更新时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;

    // 在类的最后，Getter/Setter 之前加入
    @TableField(exist = false) // 表示数据库表中不存在此列
    private String productName;
}
