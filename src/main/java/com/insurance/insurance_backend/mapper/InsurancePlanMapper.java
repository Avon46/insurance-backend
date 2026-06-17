package com.insurance.insurance_backend.mapper;

import com.insurance.insurance_backend.entity.InsurancePlan;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 保單資料存取。SQL 寫在 resources/mapper/InsurancePlanMapper.xml。
 */
@Mapper
public interface InsurancePlanMapper {

    /**
     * 取得保單列表。category 為 null 時回傳全部。
     * 預設排序：sort_order ASC、id DESC。
     */
    List<InsurancePlan> findAll(@Param("category") String category);

    InsurancePlan findById(@Param("id") Integer id);

    /** 新增後 id 會回填到傳入的 plan 物件。 */
    int insert(InsurancePlan plan);

    int update(InsurancePlan plan);

    int deleteById(@Param("id") Integer id);
}
