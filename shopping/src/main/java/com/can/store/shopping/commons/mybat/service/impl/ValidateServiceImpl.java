package com.can.store.shopping.commons.mybat.service.impl;

import com.can.store.shopping.commons.mybat.mapper.ValidatorMapper;
import com.can.store.shopping.commons.mybat.model.Validator;
import com.can.store.shopping.commons.mybat.service.ValidateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 2019.08.13
 */
@Service(value = "validatorService")
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
    public int DeleteBySessionIdAndValidatorCode(String sessionId, String validatorCode) {
        return validate.DeleteBySessionIdAndValidatorCode(sessionId,validatorCode);
    }
}
