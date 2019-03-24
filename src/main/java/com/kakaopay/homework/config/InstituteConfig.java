package com.kakaopay.homework.config;

import com.kakaopay.homework.dao.InstituteRepository;
import com.kakaopay.homework.domain.CsvMeta;
import com.kakaopay.homework.domain.entity.Institute;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.List;

@Configuration
@ConfigurationProperties("institute")
@Data
public class InstituteConfig {

    private final InstituteRepository instituteRepository;
    private List<CsvMeta> nameAndCodes;

    public InstituteConfig(final InstituteRepository instituteRepository) {
        this.instituteRepository = instituteRepository;
    }

    @PostConstruct
    public void init() {
        nameAndCodes.forEach(csvMeta -> {
            Institute institute = Institute.builder()
                    .name(csvMeta.getName())
                    .code(csvMeta.getCode())
                    .build();
            instituteRepository.save(institute);
        });
    }
}
