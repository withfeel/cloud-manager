package com.cloudmanager.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloudmanager.entity.ResourceApplication;
import com.cloudmanager.entity.ApprovalRecord;
import com.cloudmanager.entity.ResourceInstance;
import com.cloudmanager.mapper.ResourceApplicationMapper;
import com.cloudmanager.mapper.ApprovalRecordMapper;
import com.cloudmanager.mapper.ResourceInstanceMapper;
import com.cloudmanager.service.ResourceApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ResourceApplicationServiceImpl extends ServiceImpl<ResourceApplicationMapper, ResourceApplication> implements ResourceApplicationService {

    @Autowired
    private ApprovalRecordMapper approvalRecordMapper;
    
    @Autowired
    private ResourceInstanceMapper resourceInstanceMapper;

    @Override
    @Transactional
    public boolean submitApplication(ResourceApplication application) {
        // 生成申请单号
        String applicationNumber = "APP" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        application.setApplicationNumber(applicationNumber);
        application.setStatus(0); // 待审批
        application.setApplyTime(LocalDateTime.now());
        return this.save(application);
    }

    @Override
    @Transactional
    public boolean approveApplication(Long applicationId, Long approverId, Integer status, String remark) {
        ResourceApplication application = this.getById(applicationId);
        if (application == null) {
            return false;
        }
        
        // 更新申请状态
        application.setStatus(status);
        application.setApprovedTime(LocalDateTime.now());
        application.setApprovedBy(approverId);
        application.setApprovedRemark(remark);
        this.updateById(application);
        
        // 保存审批记录
        ApprovalRecord record = new ApprovalRecord();
        record.setApplicationId(applicationId);
        record.setApproverId(approverId);
        record.setApprovalStatus(status);
        record.setApprovalRemark(remark);
        record.setApprovalTime(LocalDateTime.now());
        approvalRecordMapper.insert(record);
        
        return true;
    }

    @Override
    @Transactional
    public boolean allocateResources(Long applicationId) {
        ResourceApplication application = this.getById(applicationId);
        if (application == null || application.getStatus() != 1) { // 1-审批通过
            return false;
        }
        
        // 模拟创建资源实例
        createResourceInstance(applicationId, 1L, "通用型CPU", application.getTotalCpu(), 100.00);
        createResourceInstance(applicationId, 2L, "DDR4内存", application.getTotalMemory(), 50.00);
        createResourceInstance(applicationId, 3L, "SSD硬盘", application.getTotalDisk(), 20.00);
        
        // 更新申请状态为已发放
        application.setStatus(3); // 3-已发放
        this.updateById(application);
        
        return true;
    }

    @Override
    @Transactional
    public boolean updateResources(Long applicationId, Integer cpu, Integer memory, Integer disk) {
        ResourceApplication application = this.getById(applicationId);
        if (application == null || application.getStatus() != 3) { // 3-已发放
            return false;
        }
        
        // 更新资源申请信息
        application.setTotalCpu(cpu);
        application.setTotalMemory(memory);
        application.setTotalDisk(disk);
        application.setStatus(4); // 4-已变更
        this.updateById(application);
        
        // 更新资源实例
        // 实际项目中应该根据类型更新对应实例，这里简化处理
        return true;
    }

    @Override
    @Transactional
    public boolean releaseResources(Long applicationId) {
        ResourceApplication application = this.getById(applicationId);
        if (application == null) {
            return false;
        }
        
        // 更新资源实例状态为已释放
        // 实际项目中应该更新所有关联的资源实例，这里简化处理
        
        // 更新申请状态为已释放
        application.setStatus(5); // 5-已释放
        this.updateById(application);
        
        return true;
    }
    
    private void createResourceInstance(Long applicationId, Long resourceTypeId, String specification, Integer quantity, double unitPrice) {
        ResourceInstance instance = new ResourceInstance();
        instance.setApplicationId(applicationId);
        instance.setInstanceName(specification + "实例");
        instance.setResourceTypeId(resourceTypeId);
        instance.setSpecification(specification);
        instance.setQuantity(quantity);
        instance.setUnitPrice(java.math.BigDecimal.valueOf(unitPrice));
        instance.setStatus(1); // 1-运行中
        instance.setCreateTime(LocalDateTime.now());
        resourceInstanceMapper.insert(instance);
    }
}