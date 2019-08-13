package com.can.store.shopping.commons.mybat.mapper;

import com.can.store.shopping.commons.mybat.model.Validator;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 验证码查询，插入，删除操作
 * 2019.08.13
 */
@Mapper
public interface ValidatorMapper {
    int insert(Validator validator);
    List<Validator> SelectBySessionIdAndValidatorCode(String sessionId, String validatorCode);
    int DeleteBySessionIdAndValidatorCode(String sessionId,String validatorCode);
}
