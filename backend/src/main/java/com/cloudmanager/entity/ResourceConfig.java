package com.cloudmanager.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("resource_config")
public class ResourceConfig {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long resourceTypeId;
    private String specification;
    private BigDecimal monthlyPrice;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}