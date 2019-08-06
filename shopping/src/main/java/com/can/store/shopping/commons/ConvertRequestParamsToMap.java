package com.can.store.shopping.commons;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 将request中的参数转换，并且放到Map中
 * 2019.08.05
 */
public class ConvertRequestParamsToMap {
    private Map<String,String> params = new HashMap<>();

    public static ConvertRequestParamsToMap getInstance(HttpServletRequest request){
        ConvertRequestParamsToMap con = new ConvertRequestParamsToMap();
        con.convertTo(request);
        return con;
    }

    private void convertTo(HttpServletRequest request){
        Set<Map.Entry<String,String[]>> entrySet = request.getParameterMap().entrySet();

        for(Map.Entry<String,String[]> entry : entrySet){
            String name = entry.getKey();
            String[] values = entry.getValue();
            int valLen = values.length;
            if(1 == valLen){
                params.put(name,values[0]);
            } else if(valLen > 1){
                StringBuffer sb = new StringBuffer();
                for(String val : values){
                    sb.append(",").append(val);
                }
                params.put(name,sb.toString().substring(1));
            } else {
                params.put(name,"");
            }
        }
    }

    public Map<String, String> getParams() {
        return params;
    }
}
