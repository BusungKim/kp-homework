package com.kakaopay.homework.dao;


import com.kakaopay.homework.domain.Institute;
import org.springframework.data.repository.CrudRepository;

public interface InstituteRepository extends CrudRepository<Institute, Long> {
    Institute findByCode(String code);
    Institute findByName(String name);
}
