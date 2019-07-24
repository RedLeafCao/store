package com.can.store.shopping.controller;

import com.can.store.shopping.commons.GoodsSearchBy;
import com.can.store.shopping.commons.OrderNoAutoCreate;
import com.can.store.shopping.commons.kiss.db.DBResource;
import com.can.store.shopping.commons.kizz.db.mysql.MysqlDB;
import com.can.store.shopping.commons.kizz.db.mysql.WhereBuilder;
import com.can.store.shopping.commons.kizz.db.mysql.WhereClause;
import com.can.store.shopping.commons.kizz.http.response.Response;
import com.can.store.shopping.commons.kizz.http.response.ResponsePaginate;
import com.can.store.shopping.commons.kizz.lib.utils.Func;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@Api(tags = "购物流程",description = "后台管理购物流程，加购物车，下单，生成订单")
@RequestMapping("/store/shop")
public class ShoppingController {
    @ApiOperation("查询展示商品")
    @RequestMapping(value = "/get",method = RequestMethod.POST)
    @ResponseBody
    public ResponsePaginate getGoods(
            @RequestParam(required = false) @ApiParam("查询项") Integer searchId,
            @RequestParam(required = false) @ApiParam("查询id") Long id,
            @RequestParam(required = false) @ApiParam("查询字符") String str,
            @RequestParam(required = false) @ApiParam("页码") Integer page
    ){
        MysqlDB db = DBResource.get();

        WhereBuilder where = WhereBuilder.getInstance();
        WhereBuilder where1 = WhereBuilder.getInstance();
        try {
            searchId=null==searchId?7:searchId;
            if (str != null || id != null){
                if(searchId.equals(GoodsSearchBy.NAME.getSearchId())) {
                    where.whereOr("goods_name", "like", "%"+str+"%");
                }
                if(searchId.equals(GoodsSearchBy.PROVIDER.getSearchId())){
                    where.whereOr("provider_id",id);
                    where.whereOrInSubQuery("provider_id",db.clear().select("id").from("goods_provider")
                            .where("name","like","%"+str+"%").getRawSql());
                }
                if(searchId.equals(GoodsSearchBy.CATEGORYID.getSearchId())){
                    // 通过商品名查第一级分类id？
                    where.whereOrInSubQuery("category_id", db.clear().select("id").from("goods_category")
                            .where("name", "like", "%"+str+"%").getRawSql());
                }
                if (searchId.equals(GoodsSearchBy.CATEGORYID1.getSearchId())){
                    // 第二级分类的商品
                    where.whereOrInSubQuery("category_id_1",db.clear().select("id").from("goods_category")
                            .where("name","like","%"+str+"%").getRawSql());
                }
                if (searchId.equals(GoodsSearchBy.CATEGORYID2.getSearchId())){
                    // 第三级别分类的商品
                    where.whereOrInSubQuery("category_id_2",db.clear().select("id").from("goods_category")
                            .where("name","like","%"+str+"%").getRawSql());
                }
                if (searchId.equals(GoodsSearchBy.ISVIRTUAL.getSearchId())){
                    // 是否虚拟产品
                    where.whereOr("is_virtual",true);
                    where1.whereOr("goods_name","like","%"+str+"%");
                    where.subWhere(null,where1.buildWhere());
                }
                if (searchId.equals(GoodsSearchBy.BRANDID.getSearchId())){
                    // 根据品牌查询
                    where.whereOr("brand_id",db.clear().select("id").from("goods_brand")
                            .where("name","like","%"+str+"%").getRawSql());
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        // 其他更多形式的查询 TODO:
        ResponsePaginate res= db.clear().select().from("goods_info").subWhere(null, where.buildWhere()).paginate(page, 20);
        if(res.assertNullDatalist()){
            return  res;
        }

        // 时间格式转换
        for(int i=0;i<res.data.datalist.size();i++){
            Map<String, Object> onerow=res.data.datalist.get(i);
            onerow.put("created_at", Func.timemillis2datetime(Func.toLong(onerow.get("created_at")), null));
        }
        for(int i=0;i<res.data.datalist.size();i++){
            Map<String, Object> onerow=res.data.datalist.get(i);
            if(null != onerow.get("updated_at")) {break;}
            onerow.put("updated_at", Func.timemillis2datetime(Func.toLong(onerow.get("updated")), null));
        }
        return res;
    }

    @ApiOperation("查询商品详细信息")
    @RequestMapping(value = "/get_detail",method = RequestMethod.POST)
    @ResponseBody
    public Response getDetail(
            @RequestParam(required = true) @ApiParam("商品id") Long id
    ){
        MysqlDB db = DBResource.get();
        ResponsePaginate res = db.clear().select().from("goods_info").where("id",id).paginate(null,null);
        return Response.success(res.data.datalist);
    }

    @ApiOperation("添加购物车")
    @RequestMapping(value = "/add",method = RequestMethod.POST)
    @ResponseBody
    public Response addGoods(
            @RequestParam(required = false) @ApiParam("购物车id") Long user_cart_id,
            @RequestParam(required = true) @ApiParam("用户id") Long user_id,
            @RequestParam(required = true) @ApiParam("商品id") Long goods_id,
            @RequestParam(required = true) @ApiParam("供应商id") Long provider_id,
            @RequestParam(required = true) @ApiParam("商品名") String goods_name,
            @RequestParam(required = true) @ApiParam("商品价格") Double goods_price
    ){
        Map<String, Object> data = new HashMap<>();
//        data.put("id",user_cart_id);
        data.put("user_id",user_id);
        data.put("provider_id",provider_id);
        Map<String,Object> data1 = new HashMap<>();
//        data1.put("user_cart_id",user_cart_id);
        data1.put("user_id",user_id);
        data1.put("good_id",goods_id);
        data1.put("good_price",goods_price);
        data1.put("good_provider_id",provider_id);
        data1.put("good_name",goods_name);
        // 判断购物车是否存在
        MysqlDB db = DBResource.get();
        ResponsePaginate res = db.clear().select().from("user_cart").where("user_id",user_id).paginate(null,null);
        if(res.assertNullDatalist()){
            // 采用插入方式
            data.put("quantity",1);
            db.clear().insert("user_cart").data(data).save();
            data1.put("user_cart_id",db.clear().select("id").from("user_cart")
                    .where("user_id",user_id).paginate(null,null).data.datalist.get(0).get("id"));
            db.clear().insert("user_cart_goods").data(data1).save();
        }else {
            // 采用更新方式
            System.out.println("进入了");
            int quantity = (int)res.data.datalist.get(0).get("quantity") + 1;
            data.put("quantity",quantity);
            db.clear().update("user_cart").data(data).where("user_id",user_id).save();
            data1.put("user_cart_id",db.clear().select("id").from("user_cart")
                    .where("user_id",user_id).paginate(null,null).data.datalist.get(0).get("id"));
            db.clear().insert("user_cart_goods").data(data1).save();
        }
        DBResource.returnResource(db);
        return Response.success();
    }

    @ApiOperation("从购物车中选择购买商品,未加入订单")
    @RequestMapping(value = "/choose_goods",method = RequestMethod.POST)
    @ResponseBody
    public Response chooseGoods(
            @RequestParam(required = true) @ApiParam("购车内商品id") Long id
    ){
        MysqlDB db = DBResource.get();
        db.clear().update("user_cart_goods").data("is_chosen",1).where("id",id).save();
        DBResource.returnResource(db);
        return Response.success();
    }

    @ApiOperation("生成订单")
    @RequestMapping(value = "/create_order",method = RequestMethod.POST)
    @ResponseBody
    public Response createOrder(
        @RequestParam(required = true) @ApiParam("购物车id") Long id,
        @RequestParam(required = true) @ApiParam("订单收货地址") String address
    ){
        MysqlDB db = DBResource.get();
        // 查询购物车中被选择的商品
//        List<WhereClause> list = null;
//        WhereClause clause = WhereClause.getInsance("user_cart_id");
//        clause.filter = id;
//        WhereClause clause1 = WhereClause.getInsance("is_chosen");
//        clause1.filter = 1;
//        list.add(clause);
//        list.add(clause1);

        WhereBuilder wb = WhereBuilder.getInstance();
        WhereBuilder wb1 = WhereBuilder.getInstance();
        wb.where("user_cart_id",id);
        wb1.where("is_chosen",true);
        wb.subWhere(null,wb1.buildWhere());
        ResponsePaginate res = db.clear().select().from("user_cart_goods").subWhere(null,wb.buildWhere()).paginate(null,null);

//        ResponsePaginate res = db.clear().select().from("user_cart_goods").where(list).paginate(null,null);
        // 生成订单表
        Map<String,Object> data = new HashMap<>();
        long temp = OrderNoAutoCreate.CreateOrderNo().getOrderNo();
        double totalPrice = 0;
        data.put("order_no", temp);
        data.put("goods_num",res.data.datalist.size());
        data.put("address",address);
        data.put("user_id",res.data.datalist.get(0).get("user_id"));
        data.put("provider_id",res.data.datalist.get(0).get("good_provider_id"));
        data.put("create_time",Func.toLong(new Date()));

        List<Map<String,Object>> dataList = null;
        int num = (int)db.clear().select().from("user_cart").where("id",id).paginate(null,null).data.datalist.get(0).get("quantity");
        for(int i = 0; i < res.data.datalist.size(); i++){
            Map<String,Object> data1 = new HashMap<>();
            data1.put("order_no",temp);
            data1.put("user_id",res.data.datalist.get(i).get("user_id"));
            data1.put("provider_id",res.data.datalist.get(i).get("good_provider_id"));
            data1.put("good_id",res.data.datalist.get(i).get("good_id"));
            data1.put("good_price",res.data.datalist.get(i).get("good_price"));
            data1.put("good_name",res.data.datalist.get(i).get("good_name"));
            dataList.add(data1);
            totalPrice += (double)res.data.datalist.get(i).get("good_price");
            db.clear().delete("user_cart_goods").where("id",res.data.datalist.get(i).get("id"));
            if(num >= 1){
                num--;
            }
        }
        db.clear().update("user_cart").data("quantity",num).where("id",id).save();
        data.put("total_price",totalPrice);
        db.clear().insert("user_order").data(data).save();
        db.clear().insert("user_order_goods").data(dataList).save();
        return Response.success();
    }

    @ApiOperation("获取订单详情")
    @RequestMapping(value = "/get_order",method = RequestMethod.POST)
    @ResponseBody
    public ResponsePaginate getOrder(
            @RequestParam(required = true) @ApiParam("订单编号") Long order_no,
            @RequestParam(required = false) @ApiParam("订单所有者") Long user_id
    ){
        MysqlDB db = DBResource.get();
        WhereBuilder wb = WhereBuilder.getInstance();
        WhereBuilder wb1 = WhereBuilder.getInstance();
        wb.whereOr("order_no",order_no);
        if(null != user_id){
            wb1.whereOr("user_id",user_id);
            wb.subWhere(null,wb1.buildWhere());
        }
        ResponsePaginate res = db.clear().select().from("user_order_goods").subWhere(null,wb.buildWhere()).paginate(null,null);
        return res;
    }

    @ApiOperation("订单支付")
    @RequestMapping(value = "/pay",method = RequestMethod.POST)
    @ResponseBody
    public Response pay(
            @RequestParam(required = true) @ApiParam("订单编号") Long orderNo
    ){
        // TODO: 调用支付宝或者微信进行支付,要判断是否支付成功，不成功就返回

        // 修改订单信息
        MysqlDB db = DBResource.get();
        Map<String,Object> data = new HashMap<>();
        data.put("status",1);
        data.put("update_at",Func.toLong(new Date()));
        db.clear().update("user_order").data(data).where("order_no",orderNo).save();
        return Response.success();
    }

    @ApiOperation("订单发货")
    @RequestMapping(value = "/send",method = RequestMethod.POST)
    @ResponseBody
    public Response send(
            @RequestParam(required = true) @ApiParam("订单编号") Long orderNo
    ){
        // TODO: 通知发货
        MysqlDB db = DBResource.get();
        Map<String,Object> data = new HashMap<>();
        data.put("status",2);
        data.put("update_at",Func.toLong(new Date()));
        db.clear().update("user_order").data(data).where("order_no",orderNo).save();
        return Response.success();
    }

    @ApiOperation("订单签收")
    @RequestMapping(value = "/receipt",method = RequestMethod.POST)
    @ResponseBody
    public Response recepit(
            @RequestParam(required = true) @ApiParam("订单编号") Long orderNo,
            @RequestParam(required = true) @ApiParam("确认签收_快递端") boolean status
    ){
        if(!status){
            return Response.failed(300,300,"快递未确认签收");
        }
        MysqlDB db = DBResource.get();
        Map<String,Object> data = new HashMap<>();
        data.put("status",3);
        data.put("update_at",Func.toLong(new Date()));
        db.clear().update("user_order").data(data).where("order_no",orderNo).save();
        return Response.success();
    }

    @ApiOperation("订单完成")
    @RequestMapping(value = "/finish",method = RequestMethod.POST)
    @ResponseBody
    public Response finish(
            @RequestParam(required = true) @ApiParam("订单编号") Long orderNo,
            @RequestParam(required = true) @ApiParam("确认签收_客户端") boolean status
    ){
        if(!status){
            return Response.failed(300,300,"用户未确认收货");
        }
        MysqlDB db = DBResource.get();
        Map<String,Object> data = new HashMap<>();
        data.put("status",4);
        data.put("update_at",Func.toLong(new Date()));
        db.clear().update("user_order").data(data).where("order_no",orderNo).save();
        return Response.success();
    }
}
