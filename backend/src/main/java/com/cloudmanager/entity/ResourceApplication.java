package com.cloudmanager.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("resource_application")
public class ResourceApplication {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String applicationNumber;
    private Long applicantId;
    private String organization;
    private String projectName;
    private Integer totalCpu;
    private Integer totalMemory;
    private Integer totalDisk;
    private String applicationReason;
    private Integer status;
    private LocalDateTime applyTime;
    private LocalDateTime approvedTime;
    private Long approvedBy;
    private String approvedRemark;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}