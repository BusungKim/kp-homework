package com.kakaopay.homework.service;


import com.kakaopay.homework.domain.response.AggregatedByYearAndName;
import com.kakaopay.homework.domain.response.MinMaxOfAvg;
import com.kakaopay.homework.domain.response.SumByYear;

import java.util.List;

public interface DataAggregateService {
    List<SumByYear> aggregateSum() throws Exception;

    AggregatedByYearAndName aggregateMax() throws Exception;

    MinMaxOfAvg findMinAndMaxOfAvg(String instituteNameOrCode) throws Exception;
}
