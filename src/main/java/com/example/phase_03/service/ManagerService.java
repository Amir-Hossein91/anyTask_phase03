package com.example.phase_03.service;

import com.example.phase_03.baseService.BaseService;
import com.example.phase_03.entity.Manager;


public interface ManagerService extends BaseService<Manager> {

    boolean doesManagerExist();
    Manager findByUsername(String managerUsername);
}
