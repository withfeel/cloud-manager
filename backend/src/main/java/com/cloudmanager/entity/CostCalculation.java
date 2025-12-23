package com.cloudmanager.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("cost_calculation")
public class CostCalculation {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long applicationId;
    private Integer year;
    private Integer totalDays;
    private BigDecimal dailyUnitPrice;
    private BigDecimal totalCost;
    private LocalDateTime calculationTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}