package com.example.phase_03.service;

import com.example.phase_03.baseService.BaseService;
import com.example.phase_03.entity.Order;
import com.example.phase_03.entity.TechnicianSuggestion;

import java.util.List;

public interface TechnicianSuggestionService extends BaseService<TechnicianSuggestion> {
    List<TechnicianSuggestion> getSuggestionsOrderedByPrice(Order order);
    List<TechnicianSuggestion> getSuggestionsOrderedByScore(Order order);
}
