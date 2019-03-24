package com.kakaopay.homework.controller;

import com.kakaopay.homework.domain.entity.Institute;
import com.kakaopay.homework.domain.request.LocalFileReadRequest;
import com.kakaopay.homework.domain.response.AggregatedByYearAndName;
import com.kakaopay.homework.domain.response.MinMaxOfAvg;
import com.kakaopay.homework.domain.response.SumByYear;
import com.kakaopay.homework.service.DataAggregateService;
import com.kakaopay.homework.service.DataReadService;
import com.kakaopay.homework.service.InstituteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.Callable;

@Slf4j
@RestController
@RequestMapping("/v1")
public class SampleController {

    private final InstituteService instituteService;
    private final DataReadService dataReadService;
    private final DataAggregateService dataAggregateService;

    public SampleController(final InstituteService instituteService,
                            final DataReadService dataReadService,
                            final DataAggregateService dataAggregateService) {
        this.instituteService = instituteService;
        this.dataReadService = dataReadService;
        this.dataAggregateService = dataAggregateService;
    }

    @GetMapping("/institutes")
    public Callable<List<Institute>> getAllInstitute() {
        return instituteService::getAllInstitutes;
    }

    @PostMapping("/local-csv")
    public Callable<Void> persistLocalCsv(
            @RequestBody final LocalFileReadRequest localFileReadRequest) {
        return () -> {
            dataReadService.readAndStoreData(localFileReadRequest.getFileName(),
                    localFileReadRequest.getCharset());
            return null;
        };
    }

    @GetMapping("/mortgages/year/sum")
    public Callable<List<SumByYear>> aggregateSum() {
        return dataAggregateService::aggregateSum;
    }

    @GetMapping("/mortgages/year/max")
    public Callable<AggregatedByYearAndName> aggregateMax() {
        return dataAggregateService::aggregateMax;
    }

    @GetMapping("/mortgages/year/average/{instituteCode}")
    public Callable<MinMaxOfAvg> aggregateAvg(@PathVariable final String instituteCode) {
        return () -> dataAggregateService.findMinAndMaxOfAvg(instituteCode);
    }
}
