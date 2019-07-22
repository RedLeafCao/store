/**
 * 常用函数
 *
 * @Author kjlin
 * @Date 11/14/18 5:39 PM
 */
package com.can.store.shopping.commons.kizz.lib.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.can.store.shopping.commons.kizz.db.DataObject;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @copyright 中山市五象中土科技有限公司
 * @auth kj.林
 * @time 2019-07-06 23:44
 */
public class Func {
    /**
     * 提取多维数组某一列的值。
     *
     * @param field 目标字段
     * @param data  数据集
     * @return
     */
    public static ArrayList array_field_obj(String field, List<DataObject> data) {
        if (null == data) {
            return null;
        }
        ArrayList result = new ArrayList();
        for (int i = 0; i < data.size(); i++) {
            if (null == data.get(i).data) {
                return null;
            }
            Object v = data.get(i).data.get(field);
            if (null == v) {
                return null;
            }
            if (-1 == result.indexOf(v)) {
                result.add(v);
            }
        }

        return result;
    }

    public static ArrayList array_field(String field, List<Map<String, Object>> data) {
        ArrayList result = new ArrayList();
        for (int i = 0; i < data.size(); i++) {
            Map<String, Object> one = data.get(i);
            if (null == one.get(field)) {
                return null;
            }
            Object v = one.get(field);
            if (-1 != result.indexOf(v)) {
                continue;
            }
            result.add(v);
        }

        return result;
    }

    /**
     * 提取多维数组某一列的值,并以同一条记录的key_field字段的值作为键名
     *
     * @param field     目标字段
     * @param data      数据集
     * @param key_field 其值将作为键名的字段名
     * @return
     */
    public static List<Map<String, String>> array_field_obj(String field, List<DataObject> data, String key_field) {
        if (null == data) {
            return null;
        }
        List<Map<String, String>> result = new ArrayList<Map<String, String>>();
        for (int i = 0; i < data.size(); i++) {
            Object ko = data.get(i).getObject(key_field);
            Object vo = data.get(i).getObject(field);
            if (null == ko || null == vo) {
                return null;
            }
            String key = ko.toString();
            String value = vo.toString();
            Map<String, String> one = new HashMap<String, String>();
            one.put(key, value);
            result.add(one);
        }

        return result;
    }

    public static List<Map<String, Object>> array_field(
            String field,
            List<Map<String, Object>> data,
            String key_field
    ) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            Map<String, Object> one = data.get(i);
            if (null == one.get(field) || null == one.get(key_field)) {
                return null;
            }
            String key = one.get(key_field).toString();
            Object value = one.get(field);

            Map<String, Object> kv = new HashMap<>();
            one.put(key, value);
            result.add(kv);
        }

        return result;
    }

    /**
     * 对结果集按某个字段的值分组，返回分组后的索引集
     *
     * @param data
     * @param key_field
     * @return
     */
    public static Map<String, ArrayList> array_group_index_obj(List<DataObject> data, String key_field) {
        if (null == data || StringUtils.isBlank(key_field)) {
            return null;
        }

        Map<String, ArrayList> result = new HashMap<String, ArrayList>();
        for (int i = 0; i < data.size(); i++) {
            Map<String, Object> one = data.get(i).data;
            if (null == data) {
                return null;
            }

            Object kv = one.get(key_field);
            if (null == kv) {
                return null;
            }

            String key = kv.toString();
            if (null == result.get(key)) {
                result.put(key, new ArrayList());
            }
            result.get(key).add(i);
        }
        return result;
    }

    public static Map<String, ArrayList> array_group_value_obj(
            List<DataObject> data,
            String key_field,
            String val_field
    ) {
        if (null == data || StringUtils.isBlank(key_field) || StringUtils.isBlank(val_field)) {
            return null;
        }

        Map<String, ArrayList> result = new HashMap<String, ArrayList>();
        for (int i = 0; i < data.size(); i++) {
            Map<String, Object> one = data.get(i).data;
            if (null == data) {
                return null;
            }

            Object kv = one.get(key_field);
            if (null == kv) {
                return null;
            }

            Object vv = one.get(val_field);
            if (null == vv) {
                return null;
            }

            String key = kv.toString();
            if (null == result.get(key)) {
                result.put(key, new ArrayList());
            }
            result.get(key).add(vv);
        }
        return result;
    }

    public static Map<String, ArrayList> array_group_index(
            List<Map<String, Object>> data,
            String key_field
    ) {
        if (null == data || StringUtils.isBlank(key_field)) {
            return null;
        }

        Map<String, ArrayList> result = new HashMap<>();
        for (int i = 0; i < data.size(); i++) {
            Object kv = data.get(i).get(key_field);
            if (null == key_field) {
                return null;
            }
            String key = kv.toString();
            if (null == result.get(key)) {
                result.put(key, new ArrayList());
            }
            result.get(key).add(i);
        }
        return result;
    }

    public static Map<String, ArrayList> array_group_value(
            List<Map<String, Object>> data,
            String key_field,
            String val_field
    ) {
        if (null == data || StringUtils.isBlank(key_field) || StringUtils.isBlank(val_field)) {
            return null;
        }

        Map<String, ArrayList> result = new HashMap<>();
        for (int i = 0; i < data.size(); i++) {
            Object kv = data.get(i).get(key_field);
            if (null == kv) {
                return null;
            }

            Object vv = data.get(i).get(val_field);
            if (null == vv) {
                return null;
            }

            String key = kv.toString();
            if (null == result.get(key)) {
                result.put(key, new ArrayList());
            }
            result.get(key).add(vv);
        }
        return result;
    }

    public static Map<String, Integer> array_key_index_obj(List<DataObject> data, String key_field) {
        return array_record_index_obj(data, key_field);
    }

    /**
     * 对数据集为其值具有唯一性的字段建立索引
     *
     * @param data
     * @param key_field
     * @return
     */
    public static Map<String, Integer> array_record_index_obj(List<DataObject> data, String key_field) {
        if (null == data || 0 == data.size()) {
            return null;
        }
        Map<String, Integer> result = new HashMap<>();
        for (int i = 0; i < data.size(); i++) {
            String key = data.get(i).data.get(key_field).toString();
            if (null == key) {
                return null;
            } else {
                result.put(key, i);
            }
        }
        return result;
    }

    public static Map<String, Integer> array_record_index(List<Map<String, Object>> data, String key_field) {
        if (null == data || 0 == data.size()) {
            return null;
        }
        Map<String, Integer> result = new HashMap<>();
        for (int i = 0; i < data.size(); i++) {
            String key = data.get(i).get(key_field).toString();
            if (null == key) {
                return null;
            } else {
                result.put(key, i);
            }
        }
        return result;
    }

    /**
     * 剪去数组的部分字段
     *
     * @param fields
     * @param data
     * @return
     */
    public static Map<String, String> array_cut_field(String[] fields, Map<String, String> data) {
        if (null == data || 0 == data.size() || 0 == fields.length) {
            return data;
        }

        Map<String, String> ret = new HashMap<String, String>();
        for (String key : data.keySet()) {
            boolean ignore = false;
            for (int i = 0; i < fields.length; i++) {
                if (key.equals(fields[i])) {
                    ignore = true;
                    break;
                }
            }
            if (ignore) {
                continue;
            }

            ret.put(key, data.get(key));
        }

        return ret;
    }

    public static List<Map<String, String>> array_cut_field(String[] fields, List<Map<String, String>> data) {
        if (null == data || 0 == data.size() || 0 == fields.length) {
            return data;
        }

        List<Map<String, String>> ret = new ArrayList<Map<String, String>>();
        for (int i = 0; i < data.size(); i++) {
            ret.add(array_cut_field(fields, data.get(i)));
        }

        return ret;
    }

    /**
     * 手机号码格式化，同时检查手机号合法性
     *
     * @param mobile
     * @return
     */
    public static String fm_mobile(String mobile) {
        if (StringUtils.isBlank(mobile)) {
            return null;
        }

        //去除全部非数字字符
        Pattern p0 = Pattern.compile("\\D");
        Matcher m0 = p0.matcher(mobile);
        mobile = m0.replaceAll("");

        // 匹配模式
        Pattern pattern = Pattern.compile("^(13[0-9]|14[0-9]|15[0-9]|17[0-9]|18[0-9])(\\d{4,4})(\\d{4,4})$");
        Matcher m = pattern.matcher(mobile);
        if (!m.find()) {
            return null;
        }

        // 每个分段的分隔符号
        String spr = "";
        return m.group(1) + spr + m.group(2) + spr + m.group(3);
    }

    /**
     * 解析时间区间参数。请求有时间区间筛选的情况，格式设为?time={begin},{end}。此函数解析出这两个参数的可用形式。
     *
     * @return array (
     * 0	=> '区间左端点，可能为0',
     * 1	=> '区间右端点，可能为0',
     * )
     * @param    param_time    unix时间戳时间区间。支持4种情形： 149888888,1508888888|1498888888|1498888888,|,1508888888
     */
    public static long[] parse_param_time(String param_time) {
        Pattern p = Pattern.compile("\\D");
        Matcher m = p.matcher(param_time.trim());

        long begin = 0l;
        long end = 0l;

        if (m.find()) {
            String str = m.replaceAll(",");
            String[] r = str.split(",");
            begin = Long.parseLong(r[0]);
            end = Long.parseLong(r[1]);
        } else {
            begin = Long.parseLong(param_time);
        }

        long[] ret = new long[2];
        ret[0] = begin;
        ret[1] = end;
        return ret;
    }

    /**
     * 解析时间区间参数。请求有时间区间筛选的情况，格式设为?time={begin},{end}。此函数解析出这两个参数的可用形式。
     *
     * @param param_time unix时间戳时间区间。支持4种情形： 149888888,1508888888|1498888888|1498888888,|,1508888888
     * @param format     日期时间格式。默认yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static String[] parse_param_time(String param_time, String format) {
        long[] ss = parse_param_time(param_time);
        String[] ret = new String[2];

        ret[0] = timestamp2datetime(ss[0], format);
        ret[1] = timestamp2datetime(ss[1], format);
        return ret;
    }

    public static String timestamp2datetime(Long timestamp, String format) {
        return timemillis2datetime(timestamp * 1000, format);
    }

    public static String timemillis2datetime(Long timemillis, String format) {
        if (null == timemillis || 0l == timemillis) {
            return "";
        }
        return timemillis2datetime(new Date(timemillis), format);
        /*
        dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        Calendar calendar=dateFormat.getCalendar();

        String yyyy=""+calendar.get(Calendar.YEAR);
        String mm="0"+calendar.get(Calendar.MONTH);
        String dd="0"+calendar.get(Calendar.DAY_OF_WEEK);
        String HH="0"+calendar.get(Calendar.HOUR);
        String ii="0"+calendar.get(Calendar.MINUTE);
        String ss="0"+calendar.get(Calendar.SECOND);
        return yyyy+"-"+mm+"-"+dd+" "+HH+":"+ii+":"+ss;
        */
    }

    public static String timemillis2datetime(Date date, String format) {
        if ("" == format || null == format) {
            format = "yyyy-MM-dd HH:mm:ss";
        }
        return new SimpleDateFormat(format).format(date);
    }

    /**
     * 检查输入数据是否为整数类表。可用来处理接口接收到的参数
     *
     * @param input
     * @return
     */
    public static String param_integer_list(String input) {
        Pattern p = Pattern.compile("^\\D+|\\D+$");
        Matcher m = p.matcher(input);
        if (m.find()) {
            input = m.replaceAll("");
        }

        Pattern p0 = Pattern.compile("\\D+");
        Matcher m0 = p.matcher(input);
        if (m0.find()) {
            input = m0.replaceAll(",");
        }

        Pattern p1 = Pattern.compile("^(?:,\\d+)+$");
        Matcher m1 = p1.matcher("," + input);
        if (!m1.find()) {
            return null;
        }

        return input;
    }

    /**
     * 将时间类型转换为时间戳
     *
     * @param s
     * @return
     */
    public static long datetime2timemillis(String s) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = simpleDateFormat.parse(s);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 日期格式转日期格式
     *
     * @param s
     * @param srcFormat
     * @param endFormat
     * @return
     */
    public static String dateTimeStr(String s, String srcFormat, String endFormat) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(srcFormat);
        try {
            Date data = simpleDateFormat.parse(s);
            return new SimpleDateFormat(endFormat).format(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }

    /**
     * 输入2018-10-16 得到这个日期的开始时间
     *
     * @param s
     * @return
     */
    public static long datetimeDateBegin(String s, String format) {
        if (StringUtils.isBlank(format)) {
            format = "yyyy-MM-dd";
        }
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
            Date date = simpleDateFormat.parse(s);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 获取当天零点
     *
     * @param time
     * @return
     */
    public static long datetimeDateBegin(long time) {
        return datetimeDateBegin(new Date(time));
    }

    /**
     * 获取当天零点
     *
     * @param time
     * @return
     */
    public static long datetimeDateBegin(Date time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime().getTime();
    }

    /**
     * 时间推移天数的零点
     *
     * @param time
     * @param day  负数往前推，正数往后推
     * @return
     */
    public static long datetimeDateBegin(Date time, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.DATE, day);
        return calendar.getTime().getTime();
    }

    /**
     * 时间转化
     *
     * @param year   年
     * @param month  月
     * @param day    日
     * @param hour   时
     * @param min    分
     * @param second 秒
     * @return
     */
    public static Date getDate(int year, int month, int day, int hour, int min, int second) {
        Calendar calendar = Calendar.getInstance();
        //calendar的月份值是从0开始，所以要把month-1
        calendar.set(year, month - 1, day, hour, min, second);
        return calendar.getTime();
    }

    public static long datetime2timestamp(String s) {
        return datetime2timemillis(s);
    }

    /**
     * 计算md5校验和
     *
     * @param content
     * @return
     */
    public static String md5sum(String content) {
        String hash = "";
        try {
            return DigestUtils.md5Hex(content);
        } catch (Exception exception) {

        }

        return hash;
    }

    public static List<Map<String, Object>> json_decode2list(String rsContent) {
        JSONArray arry = JSON.parseArray(rsContent);
        List<Map<String, Object>> rsList = new ArrayList<>();
        for (int i = 0; i < arry.size(); i++) {
            JSONObject jsonObject = arry.getJSONObject(i);
            Map<String, Object> map = new HashMap<>();
            for (String key : jsonObject.keySet()) {
                Object value = jsonObject.getObject(key, Object.class);
                map.put(key, value);
            }
            rsList.add(map);
        }
        return rsList;
    }

    public static JSONObject json_decode(String jsonString) {
        return JSON.parseObject(jsonString);
    }

    public static Map<String, Object> json_decode2map(String jsonString) {
        JSONObject obj = JSON.parseObject(jsonString);
        Map<String, Object> data = new HashMap<>();
        for (String key : obj.keySet()) {
            data.put(key, obj.get(key));
        }
        return data;
    }

    public static String json_encode(Object data) {
        return JSON.toJSONString(data);
    }

    public static Integer toInteger(Object v) {
        if (null == v) {
            return null;
        }
        if (v instanceof Integer) {
            return (Integer) v;
        }

        try {
            return Integer.parseInt(v.toString());
        } catch (Exception e) {

        }
        return null;
    }

    public static int toIntegerDefault(Object v, int default_i) {
        Integer i = toInteger(v);
        if (null == i) {
            i = default_i;
        }
        return i;
    }

    public static Short toShort(Object v) {
        if (null == v) {
            return null;
        }
        if (v instanceof Short) {
            return (Short) v;
        }

        try {
            return Short.parseShort(v.toString());
        } catch (Exception e) {

        }
        return null;
    }

    public static short toShortDefault(Object v, short default_i) {
        Short i = toShort(v);
        if (null == i) {
            i = default_i;
        }
        return i;
    }

    public static Long toLong(Object v) {
        if (null == v) {
            return null;
        }
        if (v instanceof Long) {
            return (long) v;
        } else if (v instanceof Integer) {
            return (long) ((int) v);
        }

        try {
            return Long.parseLong(v.toString());
        } catch (Exception e) {

        }
        return null;
    }

    public static long toLongDefault(Object v, long default_i) {
        Long i = toLong(v);
        if (null == i) {
            i = default_i;
        }
        return i;
    }

    public static Double toDouble(Object v) {
        if (null == v) {
            return null;
        }
        if (v instanceof Double) {
            return (Double) v;
        }

        try {
            return Double.parseDouble(v.toString());
        } catch (Exception e) {

        }
        return null;
    }

    public static double toDoubleDefault(Object v, double default_i) {
        Double i = toDouble(v);
        if (null == i) {
            i = default_i;
        }
        return i;
    }

    public static boolean toBooleanDefault(Object v, boolean default_i) {
        Boolean i = toBoolean(v);
        if (null == i) {
            i = default_i;
        }
        return i;
    }

    public static Boolean toBoolean(Object v) {
        if (null == v) {
            return null;
        }
        if (v instanceof Boolean) {
            return (Boolean) v;
        }

        try {
            return Boolean.parseBoolean(v.toString());
        } catch (Exception e) {

        }
        return null;
    }

    public static Float toFloat(Object v) {
        if (null == v) {
            return null;
        }
        if (v instanceof Float) {
            return (Float) v;
        }

        try {
            return Float.parseFloat(v.toString());
        } catch (Exception e) {

        }
        return null;
    }

    public static float toFloatDefault(Object v, float default_i) {
        Float i = toFloat(v);
        if (null == i) {
            i = default_i;
        }
        return i;
    }

    public static String toString(Object v) {
        if (null == v) {
            return null;
        }

        return v.toString();
    }

    public static String toStringDefault(Object v, String default_i) {
        if (null == v) {
            return default_i;
        }
        return v.toString();
    }

    public static String trim(Object v) {
        if (null == v) {
            return null;
        }

        return v.toString().trim();
    }

    public static String trimDefault(Object v, String default_i) {
        if (null == v) {
            return default_i;
        }
        return v.toString().trim();
    }

    public static String base64_decode(String inputStr) {
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] compressed = decoder.decode(inputStr);
        return new String(compressed);
    }

    public static byte[] base64_decode2bytes(String inputStr) {
        Base64.Decoder decoder = Base64.getDecoder();
        return decoder.decode(inputStr);
    }

    public static String base64_encode(String inputStr) {
        Base64.Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(inputStr.getBytes());
    }

    public static String md5(String in) {
        try {
            return DigestUtils.md5Hex(in);
        } catch (Exception e) {

        }
        return null;
    }

    public static String implode(String needle, String[] arr) {
        if (null == arr || 0 == arr.length) {
            return "";
        }
        String out = "";
        if (null == needle) {
            needle = ",";
        }
        String spr = "";
        for (int i = 0; i < arr.length; i++) {
            out += spr + arr[i];
            spr = needle;
        }
        return out;
    }

    public static String implode(String needle, Map<String, Object> arr, String kspera) {
        String content = "";
        String spr = "&";
        if (null != needle && 0 < needle.length()) {
            spr = needle;
        }
        if (null != kspera && kspera.length() > 0) {
            kspera = "=";
        }
        for (String kk : arr.keySet()) {
            Object vv = arr.get(kk);
            if (null == vv) {
                vv = "";
            }
            content += spr + kk + kspera + vv;
        }
        return content;
    }

    public static String implode(Map<String, Object> arr) {
        return implode("&", arr, "=");
    }

    public static String implode(String needle, Map<String, Object> arr) {
        return implode(needle, arr, "=");
    }

    public static String[] array_unique(String[] a) {
        Set<String> set = new HashSet<String>();
        set.addAll(Arrays.asList(a));
        return set.toArray(new String[0]);
    }

    /**
     * 获取用户真实IP地址，不使用request.getRemoteAddr()的原因是有可能用户使用了代理软件方式避免真实IP地址,
     * 可是，如果通过了多级反向代理的话，X-Forwarded-For的值并不止一个，而是一串IP值
     *
     * @return ip
     */
    public static String get_ip_addr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
            // 多次反向代理后会有多个ip值，第一个ip才是真实ip
            if (ip.indexOf(",") != -1) {
                ip = ip.split(",")[0];
            }
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    /**
     * 获取随机数
     *
     * @param length 随机数长度
     * @return
     */
    public static String get_random_number(int length) {
        StringBuffer sb = new StringBuffer();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    public static String makeOrderNo() {
        String suffix = "";
        // 如果访问量较大，应该增加随机数
        suffix = String.format("%08X", (long) (Math.random() * 100000000l));
        long timemillis = System.currentTimeMillis();
        String order_no = Func.timemillis2datetime(timemillis, "yyyyMMddHHmmssSSSS") + suffix;
        return order_no;
    }
}