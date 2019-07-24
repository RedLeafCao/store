package com.can.store.shopping.commons.kizz.http.request;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.can.store.shopping.commons.kizz.http.response.DataPaginate;
import com.can.store.shopping.commons.kizz.http.response.Response;
import com.can.store.shopping.commons.kizz.http.response.ResponsePaginate;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @copyright 中山市五象中土科技有限公司
 * @auth kj.林
 * @time 2019-07-06 23:44
 */
public class Request {
    private boolean contentType_is_json = false;
    private boolean AsynRequest = false;

    private HttpResponse httpResponse;

    private String ResponseText = "";

    private Response response = null;

    /**
     * 以json的格式发送参数
     *
     * @param type 1为json
     * @return
     */
    public Request setContentType(int type) {
        this.contentType_is_json = (1 == type);
        return this;
    }

    public Request enableAsyn(boolean yes) {
        AsynRequest = yes;
        return this;
    }

    public boolean isAsyn() {
        return AsynRequest;
    }

    public HttpResponse sendGetFunc(String url) {
        if (isAsyn()) {
            AsynGetRequest(url);
            return null;
        }

        return GetRequest(url);
    }

    protected HttpResponse GetRequest(String url) {
        DefaultHttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);
        httpResponse = null;
        try {
            httpResponse = client.execute(httpGet);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return httpResponse;
    }

    protected void AsynGetRequest(String url) {
        ExecutorService executor = Executors.newFixedThreadPool(1);
        executor.submit(new Runnable() {
            @Override
            public void run() {
                DefaultHttpClient client = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(url);
                HttpResponse res = null;
                try {
                    res = client.execute(httpGet);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static String parseResponseTest(HttpResponse res) {
        if (null == res) {
            return null;
        }

        HttpEntity entity = res.getEntity();
        String responseText = null;
        if (entity != null) {
            //把返回的结果转换为JSON对象
            try {
                responseText = EntityUtils.toString(entity, "UTF-8");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return responseText;
    }

    public String getResponseText() {
        return parseResponseTest(httpResponse);
    }

    public HttpResponse sendPostFunc(String url, Map<String, Object> params) {
        if (isAsyn()) {
            AsynPostRequest(url, params);
            return null;
        }

        DefaultHttpClient client = new DefaultHttpClient();
        HttpPost httpRequst = new HttpPost(url);
        httpResponse = null;
        try {
            if (null != params) {
                if (contentType_is_json) {
                    JSONObject jsonParam = new JSONObject();
                    for (String key : params.keySet()) {
                        jsonParam.put(key, params.get(key));
                    }
                    StringEntity entity = new StringEntity(jsonParam.toString(), "utf-8");
                    entity.setContentEncoding("UTF-8");
                    entity.setContentType("application/json");
                    httpRequst.setEntity(entity);
                } else {
                    List<BasicNameValuePair> paramsKVPairs = new ArrayList<>();
                    for (String key : params.keySet()) {
                        paramsKVPairs.add(new BasicNameValuePair(key, params.get(key).toString()));
                    }
                    httpRequst.setEntity(new UrlEncodedFormEntity(paramsKVPairs, HTTP.UTF_8));
                }
            }

            httpResponse = client.execute(httpRequst);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return httpResponse;
    }

    protected void AsynPostRequest(String url, Map<String, Object> params) {
        ExecutorService executor = Executors.newFixedThreadPool(1);
        executor.submit(new Runnable() {
            @Override
            public void run() {
                DefaultHttpClient client = new DefaultHttpClient();
                HttpPost httpRequst = new HttpPost(url);
                HttpResponse res = null;
                try {
                    if (null != params) {
                        if (contentType_is_json) {
                            JSONObject jsonParam = new JSONObject();
                            for (String key : params.keySet()) {
                                jsonParam.put(key, params.get(key));
                            }
                            StringEntity entity = new StringEntity(jsonParam.toString(), "utf-8");
                            entity.setContentEncoding("UTF-8");
                            entity.setContentType("application/json");
                            httpRequst.setEntity(entity);
                        } else {
                            List<BasicNameValuePair> paramsKVPairs = new ArrayList<>();
                            for (String key : params.keySet()) {
                                paramsKVPairs.add(new BasicNameValuePair(key, params.get(key).toString()));
                            }
                            httpRequst.setEntity(new UrlEncodedFormEntity(paramsKVPairs, HTTP.UTF_8));
                        }
                    }
                    res = client.execute(httpRequst);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //if(isDebugFlag()){
                //System.out.println("Request::AsynPostRequest:responseText>>> "+Request.parseResponseTest(res));
                //}
            }
        });
    }

    public JSONObject toJson() {
        if (null == httpResponse) {
            return null;
        }

        JSONObject jsonObject = null;
        HttpEntity entity = httpResponse.getEntity();
        if (entity != null) {
            String result = null;
            try {
                result = EntityUtils.toString(entity, "UTF-8");
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                jsonObject = JSON.parseObject(result);
            } catch (JSONException e) {
                e.printStackTrace();
                //System.out.println("http.Request.toJSON>>>"+result);
            }
        }

        return jsonObject;
    }

    /**
     * 转换为标准响应字段结构。kizz系统的接口都应该符合这个规范
     *
     * @return
     */
    public Response response() {
        if (response == null) {
            response = Response.getInstance();

            JSONObject obj = this.toJson();
            if (null == obj) {
                response.setStatus(400);
                response.setCode(400);
                response.setMessage("无响应");
                return response;
            }

            Integer status = obj.getInteger("status");
            if (null == status) {
                status = 21;
            }
            response.setStatus(status);

            Integer code = obj.getInteger("code");
            if (null == code) {
                code = 21;
            }
            response.setCode(code);

            response.setMessage(obj.getString("message"));

            //Data data=obj.getObject("data", Data.class);
            //res.setData(data);
            Map<String, Object> data = obj.getObject("data", Map.class);
            if (null != data) {
                response.data = data;
            }
            return response;
        }
        return response;
    }

    public ResponsePaginate responsePaginate() {
        ResponsePaginate res = ResponsePaginate.getInstance();

        JSONObject obj = this.toJson();
        if (null == obj) {
            res.setStatus(400);
            res.setCode(400);
            res.setMessage("无响应");
            return res;
        }

        Integer status = obj.getInteger("status");
        if (null == status) {
            status = 21;
        }
        res.setStatus(status);

        Integer code = obj.getInteger("code");
        if (null == code) {
            code = 21;
        }
        res.setCode(code);

        res.setMessage(obj.getString("message"));

        DataPaginate data = obj.getObject("data", DataPaginate.class);
        res.data = data;
        return res;
    }
}