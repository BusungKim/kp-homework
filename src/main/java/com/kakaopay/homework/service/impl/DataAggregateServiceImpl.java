package com.kakaopay.homework.service.impl;

import com.kakaopay.homework.dao.InstituteRepository;
import com.kakaopay.homework.dao.MonthlyMortgageRepository;
import com.kakaopay.homework.domain.entity.Institute;
import com.kakaopay.homework.domain.entity.MonthlyMortgage;
import com.kakaopay.homework.domain.response.AggregatedByYearAndName;
import com.kakaopay.homework.domain.response.MinMaxOfAvg;
import com.kakaopay.homework.domain.response.SumByYear;
import com.kakaopay.homework.exception.client.UnregisteredInstituteException;
import com.kakaopay.homework.exception.server.AggregateOperationFailException;
import com.kakaopay.homework.service.DataAggregateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
    public List<SumByYear> aggregateSum() throws AggregateOperationFailException {
        Stream<MonthlyMortgage> monthlyMortgageStream =
                StreamSupport.stream(monthlyMortgageRepository.findAll().spliterator(), false);

        return monthlyMortgageStream.collect(Collectors.groupingBy(MonthlyMortgage::getYear))
                .entrySet()
                .stream()
                .map(entry -> {
                    Integer year = entry.getKey();
                    List<MonthlyMortgage> mortgagesOfYear = entry.getValue();

                    Integer totalAmount = mortgagesOfYear.stream()
                            .collect(Collectors.summingInt(MonthlyMortgage::getAmount100M));
                    Map<String, Integer> detailedAmount = mortgagesOfYear.stream()
                            .collect(Collectors.groupingBy(mortgage -> mortgage.getInstitute().getName()))
                            .entrySet()
                            .stream()
                            .collect(Collectors.toMap(Map.Entry::getKey, e -> totalAmountOfMortgages(e.getValue())));

                    return SumByYear.builder()
                            .year(year)
                            .totalAmount(totalAmount)
                            .detailedByInstitute(detailedAmount)
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    public AggregatedByYearAndName aggregateMax() throws AggregateOperationFailException {
        Stream<MonthlyMortgage> monthlyMortgageStream =
                StreamSupport.stream(monthlyMortgageRepository.findAll().spliterator(), false);
        Map<Integer, Map<String, Integer>> groupedByYearAndName =
                monthlyMortgageStream.collect(Collectors.groupingBy(
                        MonthlyMortgage::getYear, Collectors.groupingBy(m -> m.getInstitute().getName(),
                                Collectors.summingInt(MonthlyMortgage::getAmount100M))));
        Stream<AggregatedByYearAndName> triplets = groupedByYearAndName.entrySet().stream().flatMap(entry -> {
            Integer year = entry.getKey();

            return entry.getValue()
                    .entrySet()
                    .stream()
                    .map(innerEntry -> AggregatedByYearAndName.builder()
                            .year(year)
                            .instituteName(innerEntry.getKey())
                            .amount(innerEntry.getValue())
                            .build());
        });
        Optional<AggregatedByYearAndName> t = triplets.max(Comparator.comparingInt(AggregatedByYearAndName::getAmount));
        if (!t.isPresent()) {
            log.error("There is no max aggregated");
            throw new AggregateOperationFailException();
        }
        return t.get();
    }

    @Override
    public MinMaxOfAvg findMinAndMaxOfAvg(String instituteCode)
            throws AggregateOperationFailException, UnregisteredInstituteException {
        Institute institute = instituteRepository.findByCode(instituteCode);
        if (institute == null) {
            log.error("Unregistered institute. {}", instituteCode);
            throw new UnregisteredInstituteException();
        }
        Map<Integer, Double> avgByYear = monthlyMortgageRepository.findByInstitute(institute)
                .stream()
                .collect(Collectors.groupingBy(MonthlyMortgage::getYear))
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, m -> avgAmountOfMortgages(m.getValue())));
        List<MinMaxOfAvg.Pair> flattend = avgByYear.entrySet()
                .stream()
                .map(entry -> MinMaxOfAvg.Pair.builder()
                        .year(entry.getKey())
                        .average(entry.getValue())
                        .build())
                .collect(Collectors.toList());
        Optional<MinMaxOfAvg.Pair> minAverage = flattend.stream()
                .min(Comparator.comparingDouble(MinMaxOfAvg.Pair::getAverage));
        Optional<MinMaxOfAvg.Pair> maxAverage = flattend.stream()
                .max(Comparator.comparingDouble(MinMaxOfAvg.Pair::getAverage));

        if (!minAverage.isPresent() || !maxAverage.isPresent()) {
            log.error("There is no min or max average");
            throw new AggregateOperationFailException();
        }

        return MinMaxOfAvg.builder()
                .name(institute.getName())
                .minOfAvg(minAverage.get())
                .maxOfAvg(maxAverage.get())
                .build();
    }

    private Integer totalAmountOfMortgages(List<MonthlyMortgage> mortgages) {
        return mortgages.stream().collect(Collectors.summingInt(MonthlyMortgage::getAmount100M));
    }

    private Double avgAmountOfMortgages(List<MonthlyMortgage> mortgages) {
        return mortgages.stream().collect(Collectors.averagingInt(MonthlyMortgage::getAmount100M));
    }
}
