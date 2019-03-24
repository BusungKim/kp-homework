package com.kakaopay.homework.service;


import com.kakaopay.homework.domain.response.AggregatedByYearAndName;
import com.kakaopay.homework.domain.response.MinMaxOfAvg;
import com.kakaopay.homework.domain.response.SumByYear;
import com.kakaopay.homework.exception.client.UnregisteredInstituteException;
import com.kakaopay.homework.exception.server.AggregateOperationFailException;

import java.util.List;

public interface DataAggregateService {
    List<SumByYear> aggregateSum() throws AggregateOperationFailException;

    AggregatedByYearAndName aggregateMax() throws AggregateOperationFailException;

    MinMaxOfAvg findMinAndMaxOfAvg(String instituteNameOrCode)
            throws AggregateOperationFailException, UnregisteredInstituteException;
}
