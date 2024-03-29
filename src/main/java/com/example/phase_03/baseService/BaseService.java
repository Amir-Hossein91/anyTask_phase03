package com.example.phase_03.baseService;

import com.example.phase_03.entity.base.BaseEntity;

import java.util.List;

public interface BaseService<T extends BaseEntity> {
    T saveOrUpdate(T t);
    void delete(T t);
    T findById (long id);
    List<T> findAll();
}
