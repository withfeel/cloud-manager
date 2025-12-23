package com.cloudmanager.controller;

import com.cloudmanager.entity.CostCalculation;
import com.cloudmanager.service.CostCalculationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cost")
public class CostCalculationController {

    @Autowired
    private CostCalculationService costCalculationService;

    // 核算年用云费用
    @PostMapping("/calculate")
    public ResponseEntity<CostCalculation> calculateAnnualCost(
            @RequestParam Long applicationId,
            @RequestParam Integer year) {
        CostCalculation calculation = costCalculationService.calculateAnnualCost(applicationId, year);
        if (calculation != null) {
            return ResponseEntity.ok(calculation);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    // 获取某一年的自然天数
    @GetMapping("/days/{year}")
    public ResponseEntity<Integer> getDaysInYear(@PathVariable Integer year) {
        Integer days = costCalculationService.getDaysInYear(year);
        return ResponseEntity.ok(days);
    }
}