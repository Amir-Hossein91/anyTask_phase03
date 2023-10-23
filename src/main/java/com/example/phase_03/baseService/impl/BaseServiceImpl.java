package com.example.phase_03.baseService.impl;

import com.example.phase_03.entity.base.BaseEntity;
import com.example.phase_03.utility.ApplicationContext;
import com.example.phase_03.utility.Printer;
import com.example.phase_03.validation.EntityValidator;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.stereotype.Service;

import java.util.Scanner;
import java.util.Set;

@Service
public class BaseServiceImpl<T extends BaseEntity> {

    protected Validator validator;
    protected Printer printer;
    protected Scanner input;

    public BaseServiceImpl(){
        validator = EntityValidator.validator;
        printer = ApplicationContext.printer;
        input = ApplicationContext.input;
    }

    public boolean isValid(T t) {
        Validator validator = EntityValidator.validator;
        Set<ConstraintViolation<T>> violations = validator.validate(t);
        if(!violations.isEmpty()){
            for(ConstraintViolation<T> c : violations)
                printer.printError(c.getMessage());
            return false;
        }
        return true;
    }
}
