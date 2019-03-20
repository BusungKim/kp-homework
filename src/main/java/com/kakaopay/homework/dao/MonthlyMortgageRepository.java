package com.kakaopay.homework.dao;


import com.kakaopay.homework.domain.MonthlyMortgage;
import org.springframework.data.repository.CrudRepository;


public interface MonthlyMortgageRepository extends CrudRepository<MonthlyMortgage, Long> {
}
