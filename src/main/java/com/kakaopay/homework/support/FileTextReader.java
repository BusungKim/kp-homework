package com.kakaopay.homework.support;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Stream;

@Component
@Slf4j
public class FileTextReader implements TextReader {

    private static final String COMMA_DELIMITER = ",";

    private final ResourceLoader resourceLoader;

    public FileTextReader(final ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public Stream<List<String>> read(String path, String charset) throws IOException {
        Stream<List<String>> ret = Stream.empty();
        Resource resource = resourceLoader.getResource(path);

        try (Scanner scanner = new Scanner(resource.getFile(), charset)) {
            while (scanner.hasNextLine()) {
                ret = Stream.concat(ret, Stream.of(getRecordFromLine(scanner.nextLine())));
            }
        }

        return ret;
    }

    private List<String> getRecordFromLine(String line) {
        List<String> values = Lists.newArrayList();
        try (Scanner rowScanner = new Scanner(line)) {
            rowScanner.useDelimiter(COMMA_DELIMITER);
            while (rowScanner.hasNext()) {
                values.add(rowScanner.next());
            }
        }
        return values;
    }
}
