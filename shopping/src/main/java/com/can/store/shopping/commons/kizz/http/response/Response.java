/**
 * @Author kjlin
 * @Date 11/14/18 5:39 PM
 * <p>
 * 接口响应标准结构
 * {
 * status:
 * code:
 * message:
 * data:{
 * data:
 * datalist:
 * paginate:
 * }
 * }
 */
package com.can.store.shopping.commons.kizz.http.response;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @copyright 中山市五象中土科技有限公司
 * @auth kj.林
 * @time 2019-07-06 23:44
 */
public class Response extends Abstract {
    public Object data;

    /**
     * 获取实例
     *
     * @return
     */
    public static Response getInstance() {
        Response res = new Response();
        res.data = new HashMap<>();
        return res;
    }

    /**
     * 成功，但数据字段为空时，可用此方法快速获取可响应的对象
     *
     * @param message
     * @return
     */
    public static Response success(String message) {
        Response res = Response.getInstance();
        res.setMessage(message);
        res.setStatus(0);
        res.setCode(0);
        return res;
    }

    public static Response success() {
        return Response.success("成功");
    }

    public static Response success(List<Map<String, Object>> datalist) {
        Response res = Response.success();
        Map<String, List> data = new HashMap<>();
        data.put("datalist", datalist);
        res.data = data;
        return res;
    }

    public static Response success(Map<String, Object> data) {
        Response res = Response.success();
        res.data = data;
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
    public static Response failed(int status, int code, String message) {
        Response res = Response.getInstance();
        res.setMessage(message);
        res.setStatus(status);
        res.setCode(code);
        return res;
    }

    public void setData(Data data) {
        if (null != data.data) {
            this.data = data;
        }
    }

    public void setData(Object data) {
        this.data = data;
    }

    public void setData(String key, Object value) {
        if (null == data) {
            data = new HashMap<String, Object>();
        } else {
            if (!(data instanceof Map)) {
                return;
            }
        }

        Map<String, Object> dataObj = (Map<String, Object>) data;
        dataObj.put(key, value);
        data = dataObj;
    }

    public boolean assertNullData() {
        if (data instanceof Map) {
            if (0 == ((Map) data).size()) {
                return true;
            }
        }
        return null == data;
    }

    public Object getDataValue(String key) {
        try {
            Object v = ((Map) data).get(key);
            return v;
        } catch (Exception e) {

        }
        return null;
    }

    public Object getDataValueDefault(String key, Object value) {
        Object v = getDataValue(key);
        return v == null ? value : v;
    }
}
