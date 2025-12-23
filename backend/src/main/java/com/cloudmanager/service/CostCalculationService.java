package com.cloudmanager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cloudmanager.entity.CostCalculation;
import java.math.BigDecimal;

public interface CostCalculationService extends IService<CostCalculation> {
    // 核算单个申请的年费用
    CostCalculation calculateAnnualCost(Long applicationId, Integer year);
    
    // 计算每日单价
    BigDecimal calculateDailyPrice(BigDecimal monthlyPrice, Integer year);
    
    // 获取某一年的自然天数
    Integer getDaysInYear(Integer year);
}