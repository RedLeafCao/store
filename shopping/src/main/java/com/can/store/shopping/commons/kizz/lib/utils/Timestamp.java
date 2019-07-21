/**
 * 时间戳计算
 *
 * @Author kjlin
 * @Date 11/14/18 5:39 PM
 * <p>
 * 协议：
 * 1.时区为Asia/Shanghai。
 * 2.以周日为每个星期的第一天。
 * 3.获取时间边界的方法get*side()返回的都是以秒为单位的Unix时间戳。
 * 注意，右边界值为之后临近一天的0点，所以使用时不应该包含在范围内。表示形式为[begin, end)
 * <p>
 * Usage:
 * // 任意一种实例化
 * Timestamp ts=new Timestamp();
 * Timestamp ts=new Timestamp(1541834465000l);
 * Timestamp ts=new Timestamp("2018-11-10 13:32:00");
 * <p>
 * // 使用结果
 * System.out.println("当天0点时间戳: "+ts.getDayLside());
 * System.out.println("当天最后一秒时间戳: "+ts.getDayRside());
 * <p>
 * System.out.println("所在星期第一天0点时间戳: "+ts.getWeekLside());
 * System.out.println("所在星期最后一天最后一秒时间戳: "+ts.getWeekRside());
 * <p>
 * System.out.println("所在月份第一天0点时间戳: "+ts.getMonthLside());
 * System.out.println("所在月份最后一天最后一秒时间戳: "+ts.getMonthRside());
 */
package com.can.store.shopping.commons.kizz.lib.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @copyright 中山市五象中土科技有限公司
 * @auth kj.林
 * @time 2019-07-06 23:44
 */
public class Timestamp {
    protected long _timestamp = 0;
    protected Map<String, Long> _timeside;
    protected Map<String, Integer> _days9month;

    protected Calendar _calendar;
    protected SimpleDateFormat _dateFormat;
    protected Date _date;

    public Timestamp() {
        init();
        setTime(now());
    }

    public Timestamp(long time) {
        init();
        setTime(time);
    }

    public Timestamp(String time) {
        init();
        setTime(time);
    }

    public void init() {
        _dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        _calendar = Calendar.getInstance();
        _timeside = new HashMap<>();
        _days9month = new HashMap<>();
        _date = new Date();

        _timeside.put("day_0", 0l);
        _timeside.put("day_1", 0l);

        _timeside.put("week_0", 0l);
        _timeside.put("week_1", 0l);

        _timeside.put("month_0", 0l);
        _timeside.put("month_1", 0l);

        _timeside.put("year_0", 0l);
        _timeside.put("year_1", 0l);

        _days9month.put("1", 31);
        _days9month.put("2", 29);
        _days9month.put("3", 31);
        _days9month.put("4", 30);
        _days9month.put("5", 31);
        _days9month.put("6", 30);
        _days9month.put("7", 31);
        _days9month.put("8", 31);
        _days9month.put("9", 30);
        _days9month.put("10", 31);
        _days9month.put("11", 30);
        _days9month.put("12", 31);
    }

    protected void _normalsize() {
        if (!isLeap(getYear())) {
            _days9month.put("2", 28);
        }

        // 当天的时间戳边界
        String time = getYear() + "-";
        int m = getMonth();
        if (10 > m) {
            time += "0" + m;
        } else {
            time += m;
        }
        int d = getDay();
        if (10 > d) {
            time += "-0" + d;
        } else {
            time += "-" + d;
        }

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = dateFormat.parse(time + " 00:00:00");
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            long lside = calendar.getTimeInMillis() / 1000;
            long rside = lside + 86400;
            _timeside.put("day_0", lside);
            _timeside.put("day_1", rside);

            // 当周的时间戳边界
            lside = getDayLside();
            long N = calendar.get(Calendar.DAY_OF_WEEK);
            lside = lside - 86400 * (N - 1);
            rside = lside + 86400 * 7;
            _timeside.put("week_0", lside);
            _timeside.put("week_1", rside);

            // 当月的时间戳边界
            lside = getDayLside();
            long j = calendar.get(Calendar.DAY_OF_MONTH) - 1;
            long n = getMonth();
            lside = lside - 86400 * j;
            rside = lside + 86400 * _days9month.get("" + n);
            _timeside.put("month_0", lside);
            _timeside.put("month_1", rside);

            date = dateFormat.parse(getYear() + "-01-01 00:00:00");
            calendar = Calendar.getInstance();
            calendar.setTime(date);
            lside = calendar.getTimeInMillis() / 1000;
            rside = lside + 365 * 86400;
            if (this.isLeap(getYear())) {
                rside += 86400;
            }
            this._timeside.put("year_0", lside);
            this._timeside.put("year_1", rside);

            if (isLeap(date.getYear())) {
                this._days9month.put("2", 29);
            } else {
                this._days9month.put("2", 28);
            }
        } catch (ParseException e) {

        }
    }

    public int getYear() {
        return _calendar.get(Calendar.YEAR);
    }

    public int getMonth() {
        return _calendar.get(Calendar.MONTH) + 1;
    }

    public int getDay() {
        return _calendar.get(Calendar.DATE);
    }

    public Calendar getCalendar() {
        return _calendar;
    }

    /**
     * 读取该日期当天的时间左临界值。使用时应该包含该值
     */
    public long getDayLside() {
        return _timeside.get("day_0");
    }

    /**
     * 读取该日期当天的时间左临界值。使用时应该不包含该值
     */
    public long getDayRside() {
        return _timeside.get("day_1");
    }

    /**
     * 读取该日期当月的时间左临界值。使用时应该包含该值
     */
    public long getMonthLside() {
        return _timeside.get("month_0");
    }

    /**
     * 读取该日期当月的时间右临界值。使用时应该不包含该值
     */
    public long getMonthRside() {
        return _timeside.get("month_1");
    }

    public long getYearLside() {
        return this._timeside.get("year_0");
    }

    public long getYearRside() {
        return this._timeside.get("year_1");
    }

    /**
     * 读取该日期当周的时间左临界值。使用时应该包含该值
     */
    public long getWeekLside() {
        return _timeside.get("week_0");
    }

    /**
     * 读取该日期当周的时间右临界值。使用时应该不包含该值
     */
    public long getWeekRside() {
        return _timeside.get("week_1");
    }

    /**
     * 断言year为润年
     *
     * @param    year
     */
    public boolean isLeap(int year) {
        if (0 != year % 100) {
            if (0 == year % 4) {
                return true;
            }
        } else {
            if (0 == year % 400) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获得当前时间的毫秒数
     */
    public long now() {
        return System.currentTimeMillis();
    }

    /**
     * @param    milliseconds    设置时间。时间毫秒数
     */
    public boolean setTime(long milliseconds) {
        _date.setTime(milliseconds);
        _calendar.setTime(_date);
        _timestamp = milliseconds / 1000;
        _normalsize();
        return true;
    }

    /**
     * @param   time    设置时间。格式yyyy-MM-dd HH:mm:ss
     */
    public boolean setTime(String time) {
        try {
            _date = _dateFormat.parse(time);
            _calendar.setTime(_date);
            _timestamp = _calendar.getTimeInMillis() / 1000;
            _normalsize();
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    /**
     * 时间往前移动num天
     *
     * @param num 可以为负整数，负整数向后移动
     */
    public void dayForward(int num) {
        setTime((_timeside.get("day_0") + num * 86400) * 1000);
    }

    /**
     * 时间戳所在日号在当月中的序号
     * @return
     */
    public int dayOnMonth() {
        return 0;
    }

    /**
     * 时间戳所在日号在当周中的序号
     *
     * @return
     */
    public int dayOnWeek() {
        return 0;
    }

    /**
     * 时间戳所在日号在当年中的序号
     *
     * @return
     */
    public int dayOnYear() {
        return 0;
    }

    /**
     * 时间戳所在的当年中的月份
     * @return
     */
    public int monthOrderNum() {
        return 0;
    }

    /**
     * 时间戳所在的当年中的第几周
     *
     * @return
     */
    public int weekOrderNum() {
        return 0;
    }

    public void weekForward(int num) {
        setTime((_timeside.get("day_0") + num * 604800) * 1000);
    }

    public void monthForward(int num) {
        if (0 == num) {
            return;
        }
        int year = getYear();
        int m = getMonth();
        int d = getDay();
        if (0 > num) {
            int y0 = (-1 * num) / 12;
            year -= y0;
            num = (-1 * num) % 12;
            m -= num;
        } else {
            int y0 = num / 12;
            year += y0;
            num = num % 12;
            m += num;
        }

        int end = this._days9month.get(m + "");
        if (2 == m) {
            if (isLeap(year)) {
                end = 29;
            } else {
                end = 28;
            }
        }
        if (d > end) {
            d = end;
        }

        String time = year + "-";
        if (10 > m) {
            time += "0" + m;
        } else {
            time += m;
        }
        if (10 > d) {
            time += "-0" + d;
        } else {
            time += "-" + d;
        }

        setTime(time + " 00:00:00");
    }

    public void lastWeekEnd() {

    }

    public void lastMonthEnd() {

    }

    public void fixedMonthEnd() {
        int year = getYear();
        int m = getMonth();
        String time = year + "-";
        if (10 > m) {
            time += "0" + m;
        } else {
            time += m;
        }
        int d = _days9month.get(m + "");
        if (2 == m) {
            if (isLeap(year)) {
                d = 29;
            } else {
                d = 28;
            }
        }
        if (10 > d) {
            time += "-0" + d;
        } else {
            time += "-" + d;
        }
        setTime(time + " 00:00:00");
    }

    public void fixedMonthFirst() {
        int year = getYear();
        int m = getMonth();
        String time = year + "-";
        if (10 > m) {
            time += "0" + m;
        } else {
            time += m;
        }

        time += "-01";
        setTime(time + " 00:00:00");
    }

    public void fixedWeekEnd() {

    }

    public void fixedWeekFirst() {
        setTime(_timeside.get("week_0"));
    }
}