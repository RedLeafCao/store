package com.can.store.shopping.controller;

import com.can.store.shopping.commons.ChangeOrderStatus;
import com.can.store.shopping.commons.kiss.db.DBResource;
import com.can.store.shopping.commons.kizz.db.DataObject;
import com.can.store.shopping.commons.kizz.db.mysql.MysqlDB;
import com.can.store.shopping.commons.kizz.db.mysql.WhereBuilder;
import com.can.store.shopping.commons.kizz.http.response.Response;
import com.can.store.shopping.commons.kizz.http.response.ResponsePaginate;
import com.can.store.shopping.dto.MerchantOrderDelivery;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商户管理订单
 * 2019.08.03
 */
@Controller
@Api(tags ="商户管理订单",description = "商户管理订单发货，退货")
@RequestMapping("/store/merchant")
public class CMSShooppingController {

    @ApiOperation("展示已支付订单")
    @RequestMapping(value = "/show_order",method = RequestMethod.POST)
    @ResponseBody
    public ResponsePaginate show(
    ){
        MysqlDB db = DBResource.get();
        String fields[] = {"order_no","user_id","status"};
        ResponsePaginate res = db.clear().select().from("user_order").where("status",1).paginate(null,null);
        if(db.queryIsFalse()){
            DBResource.returnResource(db);
            return ResponsePaginate.failed(601,601,"当前无订单已支付");
        }
        DBResource.returnResource(db);
        return res;
    }

    @ApiOperation("获取订单详情")
    @RequestMapping(value = "/get_info",method = RequestMethod.POST)
    @ResponseBody
    public Response get_info(
            @RequestParam(required = true) @ApiParam("订单号") Long orderNo
    ){
        MysqlDB db = DBResource.get();
        WhereBuilder wb = WhereBuilder.getInstance();
        WhereBuilder wb1 = WhereBuilder.getInstance();
        wb.whereOr("order_no",orderNo);
        wb1.whereOr("status",1);
        wb.subWhere(null,wb1.buildWhere());
        List<DataObject> order = db.clear().select().from("user_order").subWhere(null,wb.buildWhere()).get();
        if(db.queryIsFalse()){
            DBResource.returnResource(db);
            return Response.failed(601,601,"获取订单详情失败");
        }
        DBResource.returnResource(db);
        return Response.success(order.get(0).data);
    }


    @ApiOperation("订单发货")
    @RequestMapping(value = "/send",method = RequestMethod.POST)
    @ResponseBody
    public Response send(
            @RequestParam(required = true) @ApiParam("订单号") Long orderNo,
            @RequestParam(required = true) @ApiParam("物流单号") Long deliverNo
    ){
        MysqlDB db = DBResource.get();
        WhereBuilder wb = WhereBuilder.getInstance();
        WhereBuilder wb1 = WhereBuilder.getInstance();
        wb.whereOr("order_no",orderNo);
        wb1.whereOr("status",1);
        wb.subWhere(null,wb1.buildWhere());
        List<DataObject> order = db.clear().select().from("user_order").subWhere(null,wb.buildWhere()).get();
        if(db.queryIsFalse()){
            DBResource.returnResource(db);
            return Response.failed(601,601,"不存在该订单");
        }
        Map<String,Object> orderMap = new HashMap<>();
        orderMap.put("order_no",orderNo);
        orderMap.put("deliver_no",deliverNo);
        orderMap.put("status",2);
        orderMap.put("update_at",System.currentTimeMillis());
        db.clear().update("user_order").data(orderMap).subWhere(null,wb.buildWhere()).save();
        if(db.queryIsFalse()){
            DBResource.returnResource(db);
            return Response.failed(602,602,"订单更新异常");
        }
        DBResource.returnResource(db);
        return Response.success();
    }

    @ApiOperation("查看申请退货的订单")
    @RequestMapping(value = "/list_return",method = RequestMethod.POST)
    @ResponseBody
    public ResponsePaginate listReturn(){
        MysqlDB db = DBResource.get();
        ResponsePaginate res = db.clear().select().from("user_order").where("return_status",1).paginate(null,null);
        if(res.assertNullDatalist()){
            DBResource.returnResource(db);
            return ResponsePaginate.failed(603,603,"当前无订单申请退货");
        }
        DBResource.returnResource(db);
        return res;
    }

    @ApiOperation("查看申请退货的订单详情")
    @RequestMapping(value = "/return_detail",method = RequestMethod.POST)
    @ResponseBody
    public Response returnDetail(
            @RequestParam(required = true) @ApiParam("订单编号") Long orderNo
    ){
        MysqlDB db = DBResource.get();
        List<DataObject> returnOrder = db.clear().select().from("user_order").where("order_no",orderNo).get();
        if(db.queryIsFalse()){
            DBResource.returnResource(db);
            return Response.failed(601,601,"无此订单");
        }
        if(1 != returnOrder.get(0).getInteger("return_status")){
            DBResource.returnResource(db);
            return Response.failed(602,602,"订单退货状态异常");
        }
        DBResource.returnResource(db);
        return Response.success(returnOrder.get(0).data);
    }

    // 过渡，具体实现 TODO:

    @ApiOperation("退货退款")
    @RequestMapping(value = "/refund",method = RequestMethod.POST)
    @ResponseBody
    public Response refund(
            @RequestParam(required = true) @ApiParam("订单编号") Long orderNo
    ){
        // TODO: alipay.trade.refund或alipay.trade.refund.apply提交的退款请求(统一收单交易退款接口)
        // 转账是否成功：
        /*
         * 1、同步响应参数。退款接口调用成功即 alipay_trade_refund_response返回10000，仅表示接口调用成功。退款是否成功可以根据同步响应的fund_change参数来判断，fund_change表示本次退款是否发生了资金变化，返回“Y”表示退款成功，返回“N”则表示退款未成功
         * 2、退款查询接口。商户可使用退款查询接口查询自已通过alipay.trade.refund提交的退款请求是否执行成功。 该接口的返回码10000，仅代表本次查询操作成功，不代表退款成功。 如果该接口返回了查询数据，则代表退款成功，如果没有查询到则代表未退款成功，可以调用退款接口进行重试。重试时请务必保证退款请求号一致。
         * 3、异步通知。异步通知返回退款信息， 不建议参考异步通知，
         * */
        return Response.success();
    }

    @ApiOperation("退货处理，通知,已发货，或已签收")
    @RequestMapping(value = "/return",method = RequestMethod.POST)
    @ResponseBody
    public String  orderReturn(
            @RequestParam(required = true) @ApiParam("订单号") Long orderNo,
            @RequestParam(required = true) @ApiParam("拥有者id") Long userId,
            @RequestParam(required = false) @ApiParam("物流单号") Long deliverNo
    ){
        MysqlDB db = DBResource.get();
        WhereBuilder wb = WhereBuilder.getInstance();
        WhereBuilder wb1 = WhereBuilder.getInstance();
        wb.whereOr("order_no",orderNo);
        wb1.whereOr("user_id",userId);
        wb.subWhere(null,wb1.buildWhere());
        List<DataObject> order = db.clear().select().from("user_order").subWhere(null,wb.buildWhere()).get();
        Map<String,Object> ret = new HashMap<>();
        if(2 == order.get(0).getInteger("status")){
           // 已发货 TODO: 通知，快递公司，用户 将货物发回发货地址
            return "通知，快递公司，用户 将货物发回发货地址";

        } else if (3 == order.get(0).getInteger("status")){
            // 已签收
            // TODO:通知用户返回货物
            return "通知用户返回货物到发货地址";
        }
        return "success";
    }
}
