package com.can.store.shopping.commons.kiss.helper.util;

import com.can.store.shopping.commons.kiss.db.DBResource;
import com.can.store.shopping.commons.kiss.deprecated.Captcha;
import com.can.store.shopping.commons.kizz.db.Redis;
import com.can.store.shopping.commons.kizz.http.response.Response;
import com.can.store.shopping.commons.kizz.lib.utils.Func;
import org.apache.commons.lang.StringUtils;

/**
 * @copyright 中山市五象中土科技有限公司
 * @auth kj.林
 * @time 2019-07-06 23:44
 */
public class ValidateCode {
    public static Response getImageUrl() {
        Captcha captcha = new Captcha();
        String photeBase64 = captcha.createPhotoOfBase64();
        String random = captcha.getRandomString();
        //session.setAttribute(Captcha.SESSION_KEY, random);
        //System.out.println("validateCode="+random);
        //return photeBase64;

        String code_id = Func.md5(System.currentTimeMillis() + photeBase64);
        Response res = Response.success();
        res.setData("imagedata", photeBase64);
        res.setData("code_id", code_id);

        Redis redis = DBResource.getRedisResource();
        redis.setExpire(code_id, random, 180);
        return res;
    }

    public static boolean verify(String code, String code_id) {
        //System.out.println("validateCode_verifi="+code);
        if (StringUtils.isBlank(code)) {
            return false;
        }

        Redis redis = DBResource.getRedisResource();
        String sessionCode = redis.get(code_id);
        //String sessionCode = (String)session.getAttribute(Captcha.SESSION_KEY);
        //System.out.println("sessionCode="+sessionCode);
        if (StringUtils.isBlank(sessionCode)) {
            return false;
        }
        redis.delete(code_id);
        //session.removeAttribute(Captcha.SESSION_KEY);
        return sessionCode.equalsIgnoreCase(code.trim());
    }
}
