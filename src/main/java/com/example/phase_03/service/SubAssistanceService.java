package com.example.phase_03.service;

import com.example.phase_03.baseService.BaseService;
import com.example.phase_03.entity.Assistance;
import com.example.phase_03.entity.SubAssistance;
import com.example.phase_03.entity.Technician;

import java.util.List;
import java.util.Optional;

public interface SubAssistanceService extends BaseService<SubAssistance> {

    SubAssistance findSubAssistance(String title, Assistance assistance);

   List<SubAssistance> findByTechniciansContaining(Technician technician);
}
