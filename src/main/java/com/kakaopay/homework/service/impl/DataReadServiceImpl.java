package com.kakaopay.homework.service.impl;

import com.google.common.base.Strings;
import com.kakaopay.homework.config.InstituteConfig;
import com.kakaopay.homework.dao.InstituteRepository;
import com.kakaopay.homework.dao.MonthlyMortgageRepository;
import com.kakaopay.homework.domain.Record;
import com.kakaopay.homework.domain.entity.Institute;
import com.kakaopay.homework.domain.entity.MonthlyMortgage;
import com.kakaopay.homework.exception.server.DataReadWriteFailException;
import com.kakaopay.homework.service.DataReadService;
import com.kakaopay.homework.support.TextReader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class DataReadServiceImpl implements DataReadService {

    private final TextReader textReader;
    private final InstituteConfig instituteConfig;
    private final MonthlyMortgageRepository monthlyMortgageRepository;
    private final InstituteRepository instituteRepository;

    public DataReadServiceImpl(final TextReader textReader,
                               final InstituteConfig instituteConfig,
                               final MonthlyMortgageRepository monthlyMortgageRepository,
                               final InstituteRepository instituteRepository) {
        this.textReader = textReader;
        this.instituteConfig = instituteConfig;
        this.monthlyMortgageRepository = monthlyMortgageRepository;
        this.instituteRepository = instituteRepository;
    }

    @Override
    public void readAndStoreData(String path, String charset) throws DataReadWriteFailException {
        Stream<List<String>> data;
        try {
            data = readCsvFromFile(path, charset);
        } catch (IOException e) {
            log.error("Failed to read data from csv file. path={}, charset={}", path, charset, e);
            throw new DataReadWriteFailException();
        }
        Stream<List<Record>> recordsByRow = data.skip(1).map(line -> {
            try {
                return mapDataToRecordRow(line);
            } catch (Exception e) {
                log.error("Failed to read data. It is wrong typed. {}", line, e);
                throw new DataReadWriteFailException("Wrong typed data row.");
            }
        });
        List<Institute> institutes = instituteConfig.getNameAndCodes().stream()
                .map(csvMeta -> instituteRepository.findByName(csvMeta.getName()))
                .collect(Collectors.toList());
        recordsByRow.forEach(records -> {
            try {
                storeRecords(records, institutes);
            } catch (Exception e) {
                log.error("Failed to store data.", e);
                throw new DataReadWriteFailException();
            }
        });
    }

    List<Record> mapDataToRecordRow(List<String> data) throws Exception {
        Integer year = Integer.parseInt(data.get(0));
        Integer month = Integer.parseInt(data.get(1));

        if (year < 0 || !(1 <= month && month <= 12)) {
            throw new Exception("Invalid type of year or month");
        }

        return data.stream()
                .skip(2)
                .filter(str -> !Strings.isNullOrEmpty(str))
                .map(Integer::parseInt)
                .map(amount -> Record.builder()
                        .year(year)
                        .month(month)
                        .amount(amount)
                        .build())
                .collect(Collectors.toList());
    }

    void storeRecords(List<Record> records, List<Institute> institutes) throws Exception {
        if (records.size() != institutes.size()) {
            throw new Exception("The number of columns in data must be same with that of institutes");
        }
        for (int i = 0; i < institutes.size(); ++i) {
            Record record = records.get(i);
            Institute institute = institutes.get(i);

            MonthlyMortgage monthlyMortgage = MonthlyMortgage.builder()
                    .year(record.getYear())
                    .month(record.getMonth())
                    .amount100M(record.getAmount())
                    .institute(institute)
                    .build();
            monthlyMortgageRepository.save(monthlyMortgage);
            institute.getMonthlyMortgageList().add(monthlyMortgage);
        }
    }

    private Stream<List<String>> readCsvFromFile(String fileName, String charset) throws IOException {
        String path = "classpath:" + fileName;
        return textReader.read(path, charset);
    }
}
