package com.cloudmanager.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloudmanager.entity.CostCalculation;
import com.cloudmanager.entity.ResourceApplication;
import com.cloudmanager.entity.ResourceConfig;
import com.cloudmanager.mapper.CostCalculationMapper;
import com.cloudmanager.mapper.ResourceApplicationMapper;
import com.cloudmanager.mapper.ResourceConfigMapper;
import com.cloudmanager.service.CostCalculationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;

@Service
public class CostCalculationServiceImpl extends ServiceImpl<CostCalculationMapper, CostCalculation> implements CostCalculationService {

    @Autowired
    private ResourceApplicationMapper resourceApplicationMapper;
    
    @Autowired
    private ResourceConfigMapper resourceConfigMapper;

    @Override
    public CostCalculation calculateAnnualCost(Long applicationId, Integer year) {
        // 获取资源申请信息
        ResourceApplication application = resourceApplicationMapper.selectById(applicationId);
        if (application == null) {
            return null;
        }
        
        // 获取本年总天数
        int totalDays = getDaysInYear(year);
        
        // 计算总月单价
        BigDecimal totalMonthlyPrice = calculateTotalMonthlyPrice(application);
        
        // 计算每日单价
        BigDecimal dailyUnitPrice = calculateDailyPrice(totalMonthlyPrice, year);
        
        // 计算年总费用
        BigDecimal totalCost = dailyUnitPrice.multiply(new BigDecimal(totalDays));
        
        // 保存费用核算记录
        CostCalculation calculation = new CostCalculation();
        calculation.setApplicationId(applicationId);
        calculation.setYear(year);
        calculation.setTotalDays(totalDays);
        calculation.setDailyUnitPrice(dailyUnitPrice);
        calculation.setTotalCost(totalCost);
        calculation.setCalculationTime(LocalDate.now().atStartOfDay());
        
        this.save(calculation);
        return calculation;
    }

    @Override
    public BigDecimal calculateDailyPrice(BigDecimal monthlyPrice, Integer year) {
        // 每月按30天计算
        BigDecimal daysPerMonth = new BigDecimal(30);
        // 计算每日单价
        return monthlyPrice.divide(daysPerMonth, 4, BigDecimal.ROUND_HALF_UP);
    }

    @Override
    public Integer getDaysInYear(Integer year) {
        Year yearObj = Year.of(year);
        return yearObj.isLeap() ? 366 : 365;
    }
    
    private BigDecimal calculateTotalMonthlyPrice(ResourceApplication application) {
        // 获取各资源类型的单价
        ResourceConfig cpuConfig = resourceConfigMapper.selectById(1L); // CPU配置ID=1
        ResourceConfig memoryConfig = resourceConfigMapper.selectById(2L); // 内存配置ID=2
        ResourceConfig diskConfig = resourceConfigMapper.selectById(3L); // 硬盘配置ID=3
        
        // 计算总月单价
        BigDecimal cpuPrice = cpuConfig.getMonthlyPrice().multiply(new BigDecimal(application.getTotalCpu()));
        BigDecimal memoryPrice = memoryConfig.getMonthlyPrice().multiply(new BigDecimal(application.getTotalMemory()));
        BigDecimal diskPrice = diskConfig.getMonthlyPrice().multiply(new BigDecimal(application.getTotalDisk()));
        
        return cpuPrice.add(memoryPrice).add(diskPrice);
    }
}