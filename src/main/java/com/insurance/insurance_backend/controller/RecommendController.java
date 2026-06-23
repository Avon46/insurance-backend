package com.insurance.insurance_backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.insurance.insurance_backend.dto.RecommendReqDTO;
import com.insurance.insurance_backend.dto.RecommendRespDTO;
import com.insurance.insurance_backend.service.RecommendService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class RecommendController {
    private final RecommendService recommendService;

    // @PostMapping("recommend")
    // @Operation(summary = "智慧推薦保單")
    // public ResponseEntity<List<RecommendRespDTO>> recommend(@RequestBody
    // RecommendReqDTO req) {
    // return ResponseEntity.ok(recommendService.recommend(req));
    // }

    @PostMapping("/recommend")
    public ResponseEntity<List<RecommendRespDTO>> recommend(@Valid @RequestBody RecommendReqDTO req) {
        return ResponseEntity.ok(recommendService.recommend(req));
    }

    @PostMapping("/debug")
    public RecommendReqDTO debug(@RequestBody RecommendReqDTO req) {
        System.out.println("收到的原始物件: " + req);
        return req; // 直接回傳，看看 primaryNeeds 是不是真的有值
    }
}
