/**
 * @Author kjlin
 * @Date 11/14/18 5:39 PM
 */
package com.can.store.shopping.commons.kizz.db.mysql;

/**
 * @copyright 中山市五象中土科技有限公司
 * @auth kj.林
 * @time 2019-07-06 23:44
 */
public class UpdateObject {
    /**
     * 字段
     */
    public String field = "";
    public Object value = "";

    /**
     * 操作符
     * +,-,/,*
     */
    public String exp = "";

    /**
     * 操作对象字段。默认为作用于field本身
     */
    public String exp_field = "";

    public UpdateObject() {
    }

    public UpdateObject(String field, Object value, String exp, String exp_field) {
        this.field = field;
        this.value = value;
        this.exp = exp;
        this.exp_field = exp_field;
    }
}
