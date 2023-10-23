package com.example.phase_03.service;

import com.example.phase_03.baseService.BaseService;
import com.example.phase_03.entity.Technician;

import java.util.List;

public interface TechnicianService extends BaseService<Technician> {

    Technician findByUsername (String technicianUsername);

    List<Technician> saveOrUpdate(List<Technician> technicians);
}
