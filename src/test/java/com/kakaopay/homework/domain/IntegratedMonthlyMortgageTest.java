package com.kakaopay.homework.domain;

import com.google.common.collect.Lists;
import com.kakaopay.homework.dao.InstituteRepository;
import com.kakaopay.homework.dao.MonthlyMortgageRepository;
import com.kakaopay.homework.domain.entity.Institute;
import com.kakaopay.homework.domain.entity.MonthlyMortgage;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class IntegratedMonthlyMortgageTest {

    @Autowired
    private MonthlyMortgageRepository monthlyMortgageRepository;

    @Autowired
    private InstituteRepository instituteRepository;


    @Test
    public void saveAndFind() {
        // setup
        Institute institute = Institute.builder()
                .name("busung")
                .code("bs")
                .build();
        MonthlyMortgage monthlyMortgage = MonthlyMortgage.builder()
                .year(2019)
                .month(3)
                .amount100M(105)
                .institute(institute)
                .build();

        // when
        instituteRepository.save(institute);
        monthlyMortgageRepository.save(monthlyMortgage);
        List<MonthlyMortgage> ret = Lists.newArrayList(monthlyMortgageRepository.findAll());

        // then
        Assert.assertThat(ret.size(), Matchers.is(1));
        Assert.assertThat(ret.get(0).getYear(), Matchers.is(2019));
    }
}
