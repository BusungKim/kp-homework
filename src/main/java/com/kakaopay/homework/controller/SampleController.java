package com.kakaopay.homework.controller;

import com.kakaopay.homework.domain.Institute;
import com.kakaopay.homework.domain.MonthlyMortgage;
import com.kakaopay.homework.domain.request.LocalFileReadRequest;
import com.kakaopay.homework.domain.response.AggregatedByYear;
import com.kakaopay.homework.service.DataAggregateService;
import com.kakaopay.homework.service.DataReadService;
import com.kakaopay.homework.service.SampleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.Callable;

@Slf4j
@RestController
@RequestMapping("/v1")
public class SampleController {

    private final SampleService sampleService;
    private final DataReadService dataReadService;
    private final DataAggregateService dataAggregateService;

    public SampleController(final SampleService sampleService,
                            final DataReadService dataReadService,
                            final DataAggregateService dataAggregateService) {
        this.sampleService = sampleService;
        this.dataReadService = dataReadService;
        this.dataAggregateService = dataAggregateService;
    }


    @PostMapping("/run")
    public void run() {
        sampleService.test();
    }

    @PostMapping("/persist-csv")
    public Callable<Void> persistLocalCsv(
            @RequestBody final LocalFileReadRequest localFileReadRequest) {
        return () -> {
            dataReadService.readAndStoreData(localFileReadRequest.getFileName(),
                    localFileReadRequest.getCharset());
            return null;
        };
    }

    @GetMapping("/institutes")
    public Callable<List<Institute>> getAllInstitute() {
        return sampleService::getAllInstitutes;
    }

    @GetMapping("/mortgages/monthly")
    public Callable<List<MonthlyMortgage>> getAllMortgages() {
        return sampleService::getAllMortgages;
    }

    @PostMapping("/mortgages/aggregate")
    public Callable<List<AggregatedByYear>> aggregateByYear() {
        return dataAggregateService::aggregateByYear;
    }
}
