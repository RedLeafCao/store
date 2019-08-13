package com.can.store.shopping.commons.mybat.service;

import com.can.store.shopping.commons.mybat.model.Validator;

import java.util.List;

/**
 * 验证码插入删除查询操作Service
 * 2019.08.13
 */
public interface ValidateService {
    int insert(Validator validator);
    List<Validator> SelectBySessionIdAndValidatorCode(String sessionId, String validatorCode);
    int DeleteBySessionIdAndValidatorCode(String sessionId,String validatoCode);
}
