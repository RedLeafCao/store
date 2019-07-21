package com.can.store.shopping.commons.kizz.http.response;

import java.util.HashMap;
import java.util.Map;

/**
 * 响应的数据字段对象
 *
 * @copyright 中山市五象中土科技有限公司
 * @auth kj.林
 * @time 2019-07-06 23:44
 */
public class Data {
    //@ApiModelProperty("数据字段")
    public Map<String, Object> data;

    public static Data getInstance() {
        Data resObj = new Data();
        resObj.data = new HashMap<>();
        return resObj;
    }
}
