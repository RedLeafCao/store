package com.can.store.shopping.commons;

import com.can.store.shopping.commons.kiss.db.DBResource;
import com.can.store.shopping.commons.kizz.db.DataObject;
import com.can.store.shopping.commons.kizz.db.mysql.MysqlDB;

import java.util.List;

/**
 * 改变订单的状态，交易状态，退货状态
 * 2019.08.06
 */
public class ChangeOrderStatus {
    private String status;
    private boolean type; // true 交易状态，false 退货状态
    private Long orderNO;
    private int errorCode = 0; // 错误码 0 成功，601 查无此单 602 订单状态异常，订单处于当前状态或当前状态之上，无须修改，603 订单修改异常 604 支付宝回调交易信息
    public ChangeOrderStatus(Long orderNO,String tradeStatus,boolean type){
        this.orderNO = orderNO;
        this.status = tradeStatus;
        this.type = type;
    }

    public int changeStatus(){
        MysqlDB db = DBResource.get();
        String fields[] = {"order_no","status","return_status"};
        List<DataObject> statuses = db.clear().select().from("user_order").where("order_no",orderNO).get();
        if(db.queryIsFalse()){
            DBResource.returnResource(db);
            errorCode = 601;
            return errorCode;
        }
        if(type){
            if(0 != statuses.get(0).getInteger("status")){
                DBResource.returnResource(db);
                errorCode = 602;
                return errorCode;
            }
            if(!(status.equals("TRADE_SUCCESS") || status.equals("TRADE_FINISHED"))){
                DBResource.returnResource(db);
                errorCode = 604;
                return errorCode;
            }
            db.clear().update("user_order").data("status",1).where("order_no",orderNO).save();
            if(db.queryIsFalse()){
                DBResource.returnResource(db);
                errorCode = 603;
                return errorCode;
            }
        } else {
            if(1 < statuses.get(0).getInteger("return_status")){
                DBResource.returnResource(db);
                errorCode = 602;
                return errorCode;
            }
            // 判断支付宝退款交易状态,接口查询退款，返回的数据不为空，包含refund_stauts字段为REFUND_SUCCESS,或者为空，表明退款成功.
            if(!(status.equals("REFUND_SUCCESS") || status.equals(""))){
                DBResource.returnResource(db);
                errorCode = 604;
                return errorCode;
            }
            db.clear().update("user_order").data("return_status",3).where("order_no",orderNO).select();
            if (db.queryIsFalse()){
                DBResource.returnResource(db);
                errorCode = 603;
                return errorCode;
            }
        }
        DBResource.returnResource(db);
        return errorCode;
    }
}
