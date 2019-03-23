package com.kakaopay.homework.support;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("TEST")
public class FileTextReaderTest {

    @Autowired
    private FileTextReader fileTextReader;


    @Test
    public void readTestData() throws IOException {
        String path = "classpath:test-data.csv";
        Stream<List<String>> dataFromFile = fileTextReader.read(path);
        List<List<String>> listedData = dataFromFile.collect(Collectors.toList());
        Assert.assertThat(listedData.size(), Matchers.not(0));
    }
}
