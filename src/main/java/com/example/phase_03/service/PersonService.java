package com.example.phase_03.service;

import com.example.phase_03.baseService.BaseService;
import com.example.phase_03.entity.Person;

public interface PersonService extends BaseService<Person> {

    Person findByUsername(String username);
}
