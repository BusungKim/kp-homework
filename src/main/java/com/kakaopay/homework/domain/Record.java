package com.kakaopay.homework.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class Record {
    private Integer year;
    private Integer month;
    private Integer amounts;
}
