package com.can.store.shopping.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.response.AlipayTradeAdvanceConsultResponse;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.can.store.shopping.commons.ConvertRequestParamsToMap;
import com.can.store.shopping.commons.kiss.db.DBResource;
import com.can.store.shopping.commons.kiss.helper.session.UserSession;
import com.can.store.shopping.commons.kizz.db.DataObject;
import com.can.store.shopping.commons.kizz.db.mysql.MysqlDB;
import com.can.store.shopping.commons.kizz.http.response.Response;
import com.can.store.shopping.dto.AlipayNotifyParam;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.can.store.shopping.commons.AlipayConfig;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 支付，支付宝支付，微信支付
 * 2019.08.05
 */
@Controller
@Api(tags = "支付相关",description = "实现支付宝，微信支付的支付过程，支付后，以及退款等操作")
@RequestMapping("/store/pay")
public class PayController {

    // TODO:未完成
    @ApiOperation("支付宝支付")
    @RequestMapping(value = "/alipay",method = RequestMethod.POST)
    @ResponseBody
    public Response aliPay(
            @RequestParam(required = true) @ApiParam("订单编号") Long orderNo
    ) throws AlipayApiException {
        UserSession us = UserSession.getInstance();
        Long user_id = us.getUserId();
        MysqlDB db = DBResource.get();
        List<DataObject> order = db.clear().select().from("user_order").where("order_no",orderNo).get();
        if(db.queryIsFalse()){
            DBResource.returnResource(db);
            return Response.failed(601,601,"当前订单不存在,或超时取消" );
        }
        if(user_id != order.get(0).getLong("user_id")){
            DBResource.returnResource(db);
            return Response.failed(602,602,"用户不一致");
        }
        if(0 != order.get(0).getInteger("status")){
            DBResource.returnResource(db);
            return Response.failed(603,603,"订单异常");
        }
        if(System.currentTimeMillis()-order.get(0).getLong("create_at") > 1800000){
            db.clear().update("user_order").data("status",4).where("order_no",orderNo).save();
            DBResource.returnResource(db);
            return Response.failed(604,604,"支付时间超过30分钟,订单自动取消");
        }
        List<DataObject> goods = db.clear().select().from("user_order_goods").where("order_no",orderNo).get();
        if(db.queryIsFalse()){
            DBResource.returnResource(db);
            return Response.failed(605,605,"订单异常");
        }
        StringBuffer goodStr = new StringBuffer();
        goodStr.append("|");
        for(int i = 0; i<goods.size();i++){
            goodStr.append(goods.get(i).getString("goods_name"));
            goodStr.append("|");
        }
        String subject = goodStr.toString();

        //  实例化客户端，填入参数
        AlipayClient alipayClient = new DefaultAlipayClient(AlipayConfig.GATEWAY_URL,AlipayConfig.APP_ID,
                AlipayConfig.MERCHANT_PRIVATE_KEY,AlipayConfig.FORMAT,AlipayConfig.CHARSET,
                AlipayConfig.ALIPAY_PUBLIC_KEY,AlipayConfig.SIGN_TYPE);
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        request.setBizContent
                ("{"+
                "\"out_trade_no\":\""+orderNo.toString()+"\","+
                "\"product_code\":\"FAST_INSTANT_TRADE_PAY\","+
                "\"total_amount\":\""+order.get(0).getDouble("total_price")+"\","+
                "\"subject\":\""+subject+"\"," +
                "\"timeout_express\":\"30m\"," +
                "}");
        AlipayTradePagePayResponse response = alipayClient.pageExecute(request);
        if(!response.isSuccess()){
            // 用户支付失败
            return Response.failed(606,606,"支付失败");
        } else {
            return Response.success();
        }
    }

    // 暂时不实现
    @ApiOperation("支付异步回调处理")
    @RequestMapping(value = "/notify_url",method = RequestMethod.POST)
    @ResponseBody
    public String notifyUrl(
            @RequestParam(required = true) @ApiParam("回调request") HttpServletRequest request
    ) {
        ConvertRequestParamsToMap con = ConvertRequestParamsToMap.getInstance(request);
        Map<String,String> params  = con.getParams();
        MysqlDB db = DBResource.get();
        String fields[] ={"order_no","total_price"};

        try {
            boolean signVerified = AlipaySignature.rsaCheckV1(params,AlipayConfig.ALIPAY_PUBLIC_KEY,AlipayConfig.CHARSET,AlipayConfig.SIGN_TYPE);
            if(signVerified){
//                List<DataObject> order = db.clear().fields(fields).where("order_no",params.get("out_trade_no")).get();
//                if(db.queryIsFalse()){
//                    DBResource.returnResource(db);
//                    throw new AlipayApiException("没有这个订单");
//                }
//                if(!order.get(0).getDouble("total_price").equals(params.get("total_amount"))){
//                    DBResource.returnResource(db);
//                    throw new AlipayApiException("价格不一致");
//                }
//                if(!params.get("app_id").equals(AlipayConfig.APP_ID)){
//                    DBResource.returnResource(db);
//                    throw new AlipayApiException("商户id异常");
//                }
//                // 修改订单状态
//                if(0 != order.get(0).getInteger("status")){
//                    DBResource.returnResource(db);
//                    throw new AlipayApiException("订单状态异常1");
//                }
                // 交易状态
//                AlipayNotifyParam param = AlipayNotifyParam.buildAlipayNotifyParam(params);
//                String trade_status = param.getTradeStatus();
                // TODO: 未完待续，if(trade_status.equals(AlipayTradeStatus.TRADE_SUCCESS.getStatus()))
//                db.clear().update("user_order").data("status",1).where("order_no",order.get(0).getLong("order_no")).save();
//                if(db.getLastAffectedRows() < 1){
//                    DBResource.returnResource(db);
//                    throw new AlipayApiException("订单状态异常2");
//                }
                return "success";
            } else {
                throw new AlipayApiException("支付宝回调签名认证失败");
            }
        } catch (AlipayApiException e){
            return "failure";
        }
    }

}
