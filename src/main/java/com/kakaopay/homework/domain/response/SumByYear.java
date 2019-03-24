package com.kakaopay.homework.domain.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
public class SumByYear {
    private Integer year;
    private Integer totalAmount;
    private Map<String, Integer> detailedByInstitute;
}
