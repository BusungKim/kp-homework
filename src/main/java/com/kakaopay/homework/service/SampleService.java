package com.kakaopay.homework.service;

import com.google.common.collect.Lists;
import com.kakaopay.homework.dao.InstituteRepository;
import com.kakaopay.homework.dao.MonthlyMortgageRepository;
import com.kakaopay.homework.domain.Institute;
import com.kakaopay.homework.domain.MonthlyMortgage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class SampleService {

    private final InstituteRepository instituteRepository;
    private final MonthlyMortgageRepository monthlyMortgageRepository;

    public SampleService(final InstituteRepository instituteRepository,
                         final MonthlyMortgageRepository monthlyMortgageRepository) {
        this.instituteRepository = instituteRepository;
        this.monthlyMortgageRepository = monthlyMortgageRepository;
    }

    public void test() {
        log.info("scheduled");
        Institute institute = Institute.builder()
                .name("busung")
                .code("bs")
                .build();
        instituteRepository.save(institute);
    }

    public List<Institute> getAllInstitutes() {
        return Lists.newArrayList(instituteRepository.findAll());
    }

    public List<MonthlyMortgage> getAllMortgages() {
        return Lists.newArrayList(monthlyMortgageRepository.findAll());
    }
}
