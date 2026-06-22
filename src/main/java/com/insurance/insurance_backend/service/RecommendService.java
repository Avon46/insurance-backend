package com.insurance.insurance_backend.service;

import java.util.List;

import com.insurance.insurance_backend.dto.RecommendReqDTO;
import com.insurance.insurance_backend.dto.RecommendRespDTO;

public interface RecommendService {
 
    /**
     * 依使用者輸入計算並回傳分數最高的前三筆保單推薦。
     */
    List<RecommendRespDTO> recommend(RecommendReqDTO req);
}