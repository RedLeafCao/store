package com.can.store.shopping.controller;

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
        if(1 != returnOrder.get(0).getInteger("status")){
            DBResource.returnResource(db);
            return Response.failed(602,602,"订单退货状态异常");
        }
        DBResource.returnResource(db);
        return Response.success(returnOrder.get(0).data);
    }

    @ApiOperation("退货申请处理")
    @RequestMapping(value = "/return",method = RequestMethod.POST)
    @ResponseBody
    public Response orderReturn(
            @RequestParam(required = true) @ApiParam("订单号") Long orderNo,
            @RequestParam(required = true) @ApiParam("拥有者id") Long userId
    ){
        MysqlDB db = DBResource.get();
        WhereBuilder wb = WhereBuilder.getInstance();
        WhereBuilder wb1 = WhereBuilder.getInstance();
        wb.whereOr("order_no",orderNo);
        wb1.whereOr("user_id",userId);
        wb.subWhere(null,wb1.buildWhere());
        List<DataObject> order = db.clear().select().from("user_order").subWhere(null,wb.buildWhere()).get();
        Map<String,Object> ret = new HashMap<>();
        if(1 == order.get(0).getInteger("status")){
            // 未发货,退钱，取消订单
            // TODO: 调用原支付方式退款会用户账户,原返
            // 退款失败

            // 退款成功,取消订单
            ret.put("status",4);
            ret.put("return_status",3);
            ret.put("create_at",System.currentTimeMillis());
            db.clear().update("user_order").data(ret).where("order_no",orderNo).save();
        }
        return Response.success();
    }
}
