package com.kakaopay.homework.dao;


import com.kakaopay.homework.domain.Institute;
import com.kakaopay.homework.domain.MonthlyMortgage;
import org.springframework.data.repository.CrudRepository;

import java.util.List;


public interface MonthlyMortgageRepository extends CrudRepository<MonthlyMortgage, Long> {
    List<MonthlyMortgage> findByInstitute(Institute institute);
}
