package com.cloudmanager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cloudmanager.entity.ResourceApplication;

public interface ResourceApplicationService extends IService<ResourceApplication> {
    // 提交资源申请
    boolean submitApplication(ResourceApplication application);
    
    // 审批资源申请
    boolean approveApplication(Long applicationId, Long approverId, Integer status, String remark);
    
    // 发放资源
    boolean allocateResources(Long applicationId);
    
    // 变更资源
    boolean updateResources(Long applicationId, Integer cpu, Integer memory, Integer disk);
    
    // 释放资源
    boolean releaseResources(Long applicationId);
}