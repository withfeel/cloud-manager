package com.cloudmanager.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloudmanager.entity.ResourceApplication;
import com.cloudmanager.service.ResourceApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/resource/application")
public class ResourceApplicationController {

    @Autowired
    private ResourceApplicationService resourceApplicationService;

    // 提交资源申请
    @PostMapping("/submit")
    public ResponseEntity<?> submitApplication(@RequestBody ResourceApplication application) {
        boolean success = resourceApplicationService.submitApplication(application);
        return ResponseEntity.ok().body(success ? "申请提交成功" : "申请提交失败");
    }

    // 获取申请列表
    @GetMapping("/list")
    public ResponseEntity<IPage<ResourceApplication>> getApplicationList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<ResourceApplication> pageParam = new Page<>(page, size);
        IPage<ResourceApplication> applicationPage = resourceApplicationService.page(pageParam);
        return ResponseEntity.ok(applicationPage);
    }

    // 审批资源申请
    @PostMapping("/approve")
    public ResponseEntity<?> approveApplication(
            @RequestParam Long applicationId,
            @RequestParam Long approverId,
            @RequestParam Integer status,
            @RequestParam(required = false) String remark) {
        boolean success = resourceApplicationService.approveApplication(applicationId, approverId, status, remark);
        return ResponseEntity.ok().body(success ? "审批成功" : "审批失败");
    }

    // 发放资源
    @PostMapping("/allocate")
    public ResponseEntity<?> allocateResources(@RequestParam Long applicationId) {
        boolean success = resourceApplicationService.allocateResources(applicationId);
        return ResponseEntity.ok().body(success ? "资源发放成功" : "资源发放失败");
    }

    // 变更资源
    @PostMapping("/update")
    public ResponseEntity<?> updateResources(
            @RequestParam Long applicationId,
            @RequestParam Integer cpu,
            @RequestParam Integer memory,
            @RequestParam Integer disk) {
        boolean success = resourceApplicationService.updateResources(applicationId, cpu, memory, disk);
        return ResponseEntity.ok().body(success ? "资源变更成功" : "资源变更失败");
    }

    // 释放资源
    @PostMapping("/release")
    public ResponseEntity<?> releaseResources(@RequestParam Long applicationId) {
        boolean success = resourceApplicationService.releaseResources(applicationId);
        return ResponseEntity.ok().body(success ? "资源释放成功" : "资源释放失败");
    }
}