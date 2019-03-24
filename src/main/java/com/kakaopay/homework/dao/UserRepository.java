package com.kakaopay.homework.dao;

import com.kakaopay.homework.domain.entity.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, String> {
}
