package com.kakaopay.homework.service.impl;

import com.kakaopay.homework.dao.InstituteRepository;
import com.kakaopay.homework.dao.MonthlyMortgageRepository;
import com.kakaopay.homework.domain.MonthlyMortgage;
import com.kakaopay.homework.domain.response.AggregatedMax;
import com.kakaopay.homework.domain.response.AggregatedSum;
import com.kakaopay.homework.service.DataAggregateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Service
@Slf4j
public class DataAggregateServiceImpl implements DataAggregateService {

    private final MonthlyMortgageRepository monthlyMortgageRepository;
    private final InstituteRepository instituteRepository;

    public DataAggregateServiceImpl(final MonthlyMortgageRepository monthlyMortgageRepository,
                                    final InstituteRepository instituteRepository) {
        this.monthlyMortgageRepository = monthlyMortgageRepository;
        this.instituteRepository = instituteRepository;
    }

    @Override
    public List<AggregatedSum> aggregateSum() throws IOException {
        Stream<MonthlyMortgage> monthlyMortgageStream =
                StreamSupport.stream(monthlyMortgageRepository.findAll().spliterator(), false);
        Map<Integer, List<MonthlyMortgage>> mortgagesByYear =
                monthlyMortgageStream.collect(Collectors.groupingBy(MonthlyMortgage::getYear));

        return mortgagesByYear.entrySet().stream().map(entry -> {
            Integer year = entry.getKey();
            List<MonthlyMortgage> mortgagesOfYear = entry.getValue();

            Integer totalAmount = mortgagesOfYear.stream()
                    .collect(Collectors.summingInt(MonthlyMortgage::getAmount100M));
            Map<String, List<MonthlyMortgage>> groupedByInstituteName = mortgagesOfYear.stream()
                    .collect(Collectors.groupingBy(mortgage -> mortgage.getInstitute().getName()));
            Map<String, Integer> detailedAmount = groupedByInstituteName.entrySet()
                    .stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, e -> totalAmountOfMortgages(e.getValue())));
            return AggregatedSum.builder()
                    .year(year)
                    .totalAmount(totalAmount)
                    .detailedByInstitute(detailedAmount)
                    .build();
        }).collect(Collectors.toList());
    }

    @Override
    public AggregatedMax aggregateMax() throws Exception {
        Stream<MonthlyMortgage> monthlyMortgageStream =
                StreamSupport.stream(monthlyMortgageRepository.findAll().spliterator(), false);
        Map<Integer, Map<String, Integer>> a =
                monthlyMortgageStream.collect(Collectors.groupingBy(
                        MonthlyMortgage::getYear, Collectors.groupingBy(m -> m.getInstitute().getName(),
                                Collectors.summingInt(MonthlyMortgage::getAmount100M))));
        Stream<AggregatedMax> triplets = a.entrySet().stream().flatMap(entry -> {
            Integer year = entry.getKey();

            return entry.getValue()
                    .entrySet()
                    .stream()
                    .map(innerEntry -> AggregatedMax.builder()
                            .year(year)
                            .instituteName(innerEntry.getKey())
                            .amount(innerEntry.getValue())
                            .build());
        });
        Optional<AggregatedMax> t = triplets.max(Comparator.comparingInt(AggregatedMax::getAmount));
        if (!t.isPresent()) {
            throw new Exception();
        }
        return t.get();
    }

    private Integer totalAmountOfMortgages(List<MonthlyMortgage> mortgages) {
        return mortgages.stream().collect(Collectors.summingInt(MonthlyMortgage::getAmount100M));
    }
}
