package com.can.store.shopping.commons.kizz.http.response;

import com.can.store.shopping.commons.kizz.lib.utils.Paginate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 接口响应数据列表
 *
 * @copyright 中山市五象中土科技有限公司
 * @auth kj.林
 * @time 2019-07-06 23:44
 */
public class DataPaginate {
    public Map<String, Object> data;
    public List<Map<String, Object>> datalist;
    public Paginate paginate;

    public static DataPaginate getInstance() {
        DataPaginate resObj = new DataPaginate();
        resObj.data = new HashMap<>();
        resObj.datalist = new ArrayList<>();
        resObj.paginate = new Paginate();

        return resObj;
    }
}
