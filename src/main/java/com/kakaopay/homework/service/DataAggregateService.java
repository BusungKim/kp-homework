package com.kakaopay.homework.service;


import com.kakaopay.homework.domain.response.AggregatedByYear;

import java.io.IOException;
import java.util.List;

public interface DataAggregateService {
    List<AggregatedByYear> aggregateByYear() throws IOException;
}
