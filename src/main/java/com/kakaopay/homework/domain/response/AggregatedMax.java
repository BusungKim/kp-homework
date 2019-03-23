package com.kakaopay.homework.domain.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class AggregatedMax {
    private Integer year;
    private String instituteName;
    private Integer amount;
}
