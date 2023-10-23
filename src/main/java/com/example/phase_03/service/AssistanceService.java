package com.example.phase_03.service;

import com.example.phase_03.baseService.BaseService;
import com.example.phase_03.entity.Assistance;


public interface AssistanceService extends BaseService<Assistance> {

    Assistance findAssistance(String assistanceName);
}
