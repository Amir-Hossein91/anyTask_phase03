package com.example.phase_03.service;

import com.example.phase_03.baseService.BaseService;
import com.example.phase_03.entity.Order;
import com.example.phase_03.entity.TechnicianSuggestion;
import com.example.phase_03.entity.dto.TechnicianSuggestionDTO;

import java.util.List;

public interface TechnicianSuggestionService extends BaseService<TechnicianSuggestion> {
    List<TechnicianSuggestionDTO> getSuggestionsOrderedByPrice(Order order);
    List<TechnicianSuggestionDTO> getSuggestionsOrderedByScore(Order order);
}
