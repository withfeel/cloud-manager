package com.cloudmanager.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("resource_type")
public class ResourceType {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String unit;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}