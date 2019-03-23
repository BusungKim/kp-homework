package com.kakaopay.homework.service;


import com.kakaopay.homework.domain.response.AggregatedMax;
import com.kakaopay.homework.domain.response.AggregatedSum;

import java.util.List;

public interface DataAggregateService {
    List<AggregatedSum> aggregateSum() throws Exception;

    AggregatedMax aggregateMax() throws Exception;
}
