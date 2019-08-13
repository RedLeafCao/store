package com.can.store.shopping.commons.mybat.service.impl;

import com.can.store.shopping.commons.mybat.mapper.ValidatorMapper;
import com.can.store.shopping.commons.mybat.model.Validator;
import com.can.store.shopping.commons.mybat.service.ValidateService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class ValidateServiceImpl implements ValidateService {
    @Autowired
    private ValidatorMapper validate;

    @Override
    public int insert(Validator validator) { return validate.insert(validator);}

    @Override
    public List<Validator> SelectBySessionIdAndValidatorCode(String sessionId, String validatorCode) {
        return validate.SelectBySessionIdAndValidatorCode(sessionId,validatorCode);
    }

    @Override
    public int DeleteBySessionIdAndValidatorCode(String sessionId, String validatoCode) {
        return validate.DeleteBySessionIdAndValidatorCode(sessionId,validatoCode);
    }
}
