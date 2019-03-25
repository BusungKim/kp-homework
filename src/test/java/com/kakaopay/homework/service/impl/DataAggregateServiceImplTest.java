package com.kakaopay.homework.service.impl;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.kakaopay.homework.dao.InstituteRepository;
import com.kakaopay.homework.dao.MonthlyMortgageRepository;
import com.kakaopay.homework.domain.entity.Institute;
import com.kakaopay.homework.domain.entity.MonthlyMortgage;
import com.kakaopay.homework.domain.response.AggregatedByYearAndName;
import com.kakaopay.homework.domain.response.MinMaxOfAvg;
import com.kakaopay.homework.domain.response.SumByYear;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

@RunWith(MockitoJUnitRunner.class)
@Slf4j
public class DataAggregateServiceImplTest {

    @Mock
    private MonthlyMortgageRepository monthlyMortgageRepository;

    @Mock
    private InstituteRepository instituteRepository;

    @InjectMocks
    private DataAggregateServiceImpl dataAggregateService;


    @Test
    public void getTotalAmounts() {
        // setup
        List<MonthlyMortgage> mortgages = Lists.newArrayList(
                MonthlyMortgage.builder().amount100M(100).build(),
                MonthlyMortgage.builder().amount100M(200).build(),
                MonthlyMortgage.builder().amount100M(300).build()
        );

        // when
        Integer sum = dataAggregateService.totalAmountOfMortgages(mortgages);

        // then
        Assert.assertThat(sum, Matchers.is(100 + 200 + 300));
    }

    @Test
    public void getAverageAmounts() {
        // setup
        List<MonthlyMortgage> mortgages = Lists.newArrayList(
                MonthlyMortgage.builder().amount100M(100).build(),
                MonthlyMortgage.builder().amount100M(200).build(),
                MonthlyMortgage.builder().amount100M(300).build()
        );

        // when
        Double average = dataAggregateService.avgAmountOfMortgages(mortgages);

        // then
        Assert.assertThat(average, Matchers.is(200.0));
    }

    @Test
    public void success_aggregateSum() {
        // setup
        Institute i1 = Institute.builder().name("우리은행").code("woori").build();
        Institute i2 = Institute.builder().name("국민은행").code("kookmin").build();
        Institute i3 = Institute.builder().name("하나은행").code("hana").build();

        MonthlyMortgage m1 = MonthlyMortgage.builder().year(2010).month(3).amount100M(100).institute(i1).build();
        MonthlyMortgage m2 = MonthlyMortgage.builder().year(2010).month(1).amount100M(200).institute(i2).build();
        MonthlyMortgage m3 = MonthlyMortgage.builder().year(2011).month(10).amount100M(300).institute(i2).build();
        MonthlyMortgage m4 = MonthlyMortgage.builder().year(2012).month(8).amount100M(400).institute(i3).build();

        Mockito.when(monthlyMortgageRepository.findAll())
                .thenReturn(Lists.newArrayList(m1, m2, m3, m4));

        // when
        List<SumByYear> ret = dataAggregateService.aggregateSum();

        // then
        SumByYear s1 = SumByYear.builder()
                .year(2010)
                .totalAmount(300)
                .detailedByInstitute(ImmutableMap.of("우리은행", 100, "국민은행", 200))
                .build();
        SumByYear s2 = SumByYear.builder()
                .year(2011)
                .totalAmount(300)
                .detailedByInstitute(ImmutableMap.of("국민은행", 300))
                .build();
        SumByYear s3 = SumByYear.builder()
                .year(2012)
                .totalAmount(400)
                .detailedByInstitute(ImmutableMap.of("하나은행", 400))
                .build();
        List<SumByYear> expected = Lists.newArrayList(s1, s2, s3);

        Assert.assertThat(ret, Matchers.is(expected));
    }

    @Test
    public void success_aggregateMax() {
        // setup
        Institute i1 = Institute.builder().name("우리은행").code("woori").build();
        Institute i2 = Institute.builder().name("국민은행").code("kookmin").build();
        Institute i3 = Institute.builder().name("하나은행").code("hana").build();

        MonthlyMortgage m1 = MonthlyMortgage.builder().year(2010).month(3).amount100M(100).institute(i1).build();
        MonthlyMortgage m2 = MonthlyMortgage.builder().year(2010).month(1).amount100M(200).institute(i2).build();
        MonthlyMortgage m3 = MonthlyMortgage.builder().year(2011).month(10).amount100M(300).institute(i2).build();
        MonthlyMortgage m4 = MonthlyMortgage.builder().year(2012).month(8).amount100M(400).institute(i3).build();

        Mockito.when(monthlyMortgageRepository.findAll())
                .thenReturn(Lists.newArrayList(m1, m2, m3, m4));

        // when
        AggregatedByYearAndName ret = dataAggregateService.aggregateMax();

        // then
        AggregatedByYearAndName expected = AggregatedByYearAndName.builder()
                .year(2012)
                .amount(400)
                .instituteName("하나은행")
                .build();
        Assert.assertThat(ret, Matchers.is(expected));
    }

    @Test
    public void success_findMinAndMaxOfAvg() {
        // setup
        Institute i1 = Institute.builder().name("우리은행").code("woori").build();
        Mockito.when(instituteRepository.findByCode("woori"))
                .thenReturn(i1);

        MonthlyMortgage m1 = MonthlyMortgage.builder().year(2010).month(3).amount100M(100).institute(i1).build();
        MonthlyMortgage m2 = MonthlyMortgage.builder().year(2010).month(1).amount100M(200).institute(i1).build();
        MonthlyMortgage m3 = MonthlyMortgage.builder().year(2011).month(10).amount100M(300).institute(i1).build();

        i1.setMonthlyMortgageList(Lists.newArrayList(m1, m2, m3));

        // when
        MinMaxOfAvg ret = dataAggregateService.findMinAndMaxOfAvg("woori");

        // then
        MinMaxOfAvg expected = MinMaxOfAvg.builder().name("우리은행")
                .minOfAvg(MinMaxOfAvg.Pair.builder().year(2010).average(150.0).build())
                .maxOfAvg(MinMaxOfAvg.Pair.builder().year(2011).average(300.0).build())
                .build();
        Assert.assertThat(ret, Matchers.is(expected));
    }
}
