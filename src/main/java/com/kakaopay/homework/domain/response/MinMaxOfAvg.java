package com.kakaopay.homework.domain.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class MinMaxOfAvg {

    private String name;
    private Pair minOfAvg;
    private Pair maxOfAvg;

    @Data
    @AllArgsConstructor
    @Builder
    public static class Pair {
        private Integer year;
        private Double average;
    }
}
