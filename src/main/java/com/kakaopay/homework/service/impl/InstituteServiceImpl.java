package com.kakaopay.homework.service.impl;

import com.google.common.collect.Lists;
import com.kakaopay.homework.dao.InstituteRepository;
import com.kakaopay.homework.domain.entity.Institute;
import com.kakaopay.homework.service.InstituteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class InstituteServiceImpl implements InstituteService {

    private final InstituteRepository instituteRepository;

    public InstituteServiceImpl(final InstituteRepository instituteRepository) {
        this.instituteRepository = instituteRepository;
    }

    @Override
    public List<Institute> getAllInstitutes() {
        return Lists.newArrayList(instituteRepository.findAll());
    }
}
