package com.kakaopay.homework.service.impl;

import com.google.common.collect.Lists;
import com.kakaopay.homework.config.InstituteConfig;
import com.kakaopay.homework.dao.InstituteRepository;
import com.kakaopay.homework.dao.MonthlyMortgageRepository;
import com.kakaopay.homework.domain.Record;
import com.kakaopay.homework.domain.entity.Institute;
import com.kakaopay.homework.domain.entity.MonthlyMortgage;
import com.kakaopay.homework.support.TextReader;
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
public class DataReadServiceImplTest {

    @Mock
    private TextReader textReader;
    @Mock
    private InstituteConfig instituteConfig;
    @Mock
    private MonthlyMortgageRepository monthlyMortgageRepository;
    @Mock
    private InstituteRepository instituteRepository;
    @InjectMocks
    private DataReadServiceImpl dataReadService;

    @Test
    public void success_mapDataToRecordRow() throws Exception {
        // setup
        List<String> dataRow = Lists.newArrayList("2019", "3", "3434", "466", "2244");

        // when
        List<Record> ret = dataReadService.mapDataToRecordRow(dataRow);

        // then
        List<Record> expected = Lists.newArrayList(
                Record.builder().year(2019).month(3).amount(3434).build(),
                Record.builder().year(2019).month(3).amount(466).build(),
                Record.builder().year(2019).month(3).amount(2244).build()
        );
        Assert.assertThat(expected, Matchers.is(ret));
    }

    @Test(expected = Exception.class)
    public void fail_mapDataToRecordRow_nonIntegerData() throws Exception {
        // setup
        List<String> dataRow = Lists.newArrayList("2019", "3", "aa3", "466", "2244");

        // when
        List<Record> ret = dataReadService.mapDataToRecordRow(dataRow);

        // then
    }

    @Test(expected = Exception.class)
    public void fail_mapDataToRecordRow_invalidYearOrMonth() throws Exception {
        // setup
        List<String> dataRow = Lists.newArrayList("2019", "13", "3434", "466", "2244");

        // when
        List<Record> ret = dataReadService.mapDataToRecordRow(dataRow);

        // then
    }

    @Test
    public void success_storeRecords() throws Exception {
        // setup
        List<Record> records = Lists.newArrayList(
                Record.builder().year(2015).month(1).amount(3434).build(),
                Record.builder().year(2016).month(2).amount(466).build(),
                Record.builder().year(2017).month(3).amount(2244).build()
        );
        List<Institute> institutes = Lists.newArrayList(
                Institute.builder().name("kookmin").build(),
                Institute.builder().name("woori").build(),
                Institute.builder().name("hana").build()
        );

        // when
        dataReadService.storeRecords(records, institutes);

        // then
        Mockito.verify(monthlyMortgageRepository, Mockito.times(3))
                .save(Mockito.any(MonthlyMortgage.class));
    }

    @Test(expected = Exception.class)
    public void fail_storeRecords_invalidDataFormat() throws Exception {
        // setup
        List<Record> records = Lists.newArrayList(
                Record.builder().year(2015).month(1).amount(3434).build(),
                Record.builder().year(2016).month(2).amount(466).build(),
                Record.builder().year(2017).month(3).amount(2244).build()
        );
        List<Institute> institutes = Lists.newArrayList(
                Institute.builder().name("kookmin").build(),
                Institute.builder().name("woori").build()
        );

        // when
        dataReadService.storeRecords(records, institutes);

        // then
    }
}
