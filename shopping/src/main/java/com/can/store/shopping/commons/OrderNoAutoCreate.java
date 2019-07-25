package com.can.store.shopping.commons;

import com.can.store.shopping.commons.kiss.db.DBResource;
import com.can.store.shopping.commons.kizz.db.DataObject;
import com.can.store.shopping.commons.kizz.db.mysql.MysqlDB;
import com.can.store.shopping.commons.kizz.http.response.ResponsePaginate;

import java.util.List;
import java.util.Random;

/**
 * 自动生成唯一订单编号
 */
public class OrderNoAutoCreate {
    public  long orderNo;

    public static  OrderNoAutoCreate getInstance(){
        OrderNoAutoCreate orderNoAutoCreate = new OrderNoAutoCreate();
        orderNoAutoCreate.orderNo = 0;
        return orderNoAutoCreate;
    }

    public static OrderNoAutoCreate CreateOrderNo(){
        Random random = new Random();
        long temp = random.nextLong();
        MysqlDB db = DBResource.get();
        OrderNoAutoCreate o = OrderNoAutoCreate.getInstance();
        if(0 < temp){
            List<DataObject> res = db.clear().select().from("user_order").where("order_no",temp).get();

            if(null == res){
                o.orderNo = temp;
            } else {
                OrderNoAutoCreate.CreateOrderNo();
            }
        }
        else {
            OrderNoAutoCreate.CreateOrderNo();
        }

        return o;
    }

    public long getOrderNo() {
        return orderNo;
    }
}
