package com.insurance.insurance_backend.controller;

import com.insurance.insurance_backend.dto.PlanRequest;
import com.insurance.insurance_backend.entity.InsurancePlan;
import com.insurance.insurance_backend.service.InsurancePlanService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 後台保單管理 API。本階段為純 CRUD，不做後端認證（僅前端 Route Guard）。
 * 分頁交由前端 QTable 處理，列表一次回傳全部。
 * 成功直接回傳資料物件，錯誤由 GlobalExceptionHandler 統一包成 ErrorResponse。
 * TODO 製作後端的JWT驗證
 */
@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/v1")
public class InsurancePlanController {

    private final InsurancePlanService planService;

    public InsurancePlanController(InsurancePlanService planService) {
        this.planService = planService;
    }

    /** 取得保單列表，category 選填。 */
    @GetMapping("/plans")
    public List<InsurancePlan> list(@RequestParam(required = false) String category) {
        return planService.findAll(category);
    }

    /** 取得單筆保單（供修改視窗預填）。 */
    @GetMapping("/{id}")
    public InsurancePlan get(@PathVariable Integer id) {
        return planService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InsurancePlan create(@Valid @RequestBody PlanRequest request) {
        return planService.create(request);
    }

    @PutMapping("/{id}")
    public InsurancePlan update(
            @PathVariable Integer id,
            @Valid @RequestBody PlanRequest request) {
        return planService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        planService.delete(id);
    }
}
