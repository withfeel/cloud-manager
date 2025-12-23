package com.cloudmanager.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("resource_instance")
public class ResourceInstance {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long applicationId;
    private String instanceName;
    private Long resourceTypeId;
    private String specification;
    private Integer quantity;
    private BigDecimal unitPrice;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime releaseTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}