package com.can.store.shopping.commons.kizz.http.response;

import java.util.HashMap;

/**
 * @copyright 中山市五象中土科技有限公司
 * @auth kj.林
 * @time 2019-07-06 23:44
 */
public class ResponsePaginate extends Abstract {
    //@ApiModelProperty("数据字段")
    public DataPaginate data;

    public static ResponsePaginate getInstance() {
        ResponsePaginate res = new ResponsePaginate();
        res.data = DataPaginate.getInstance();
        return res;
    }

    /**
     * 成功，但数据字段为空时，可用此方法快速获取可响应的对象
     *
     * @param message
     * @return
     */
    public static ResponsePaginate success(String message) {
        ResponsePaginate res = new ResponsePaginate();
        res.setMessage(message);
        res.setStatus(0);
        res.setCode(0);
        return res;
    }

    /**
     * 失败时，可用此方法获取可响应的对象
     *
     * @param status
     * @param code
     * @param message
     * @return
     */
    public static ResponsePaginate failed(int status, int code, String message) {
        ResponsePaginate res = new ResponsePaginate();
        res.setMessage(message);
        res.setStatus(status);
        res.setCode(code);
        return res;
    }

    public boolean assertNullData() {
        return null == data || null == data.data || 0 == data.data.size();
    }

    public boolean assertNullDatalist() {
        return null == data || null == data.datalist || 0 == data.datalist.size();
    }

    public void setData(String key, Object value) {
        if (null == data) {
            return;
        }
        if (null == data.data) {
            data.data = new HashMap<String, Object>();
        }
        if (!(data.data instanceof HashMap)) {
            return;
        }

        data.data.put(key, value);
    }
}