package com.can.store.shopping.controller;

import com.can.store.shopping.commons.GoodsSearchBy;
import com.can.store.shopping.commons.OrderNoAutoCreate;
import com.can.store.shopping.commons.kiss.db.DBResource;
import com.can.store.shopping.commons.kizz.db.DataObject;
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

import java.util.*;

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
//            @RequestParam(required = false) @ApiParam("购物车id") Long user_cart_id,
//            @RequestParam(required = true) @ApiParam("用户id") Long user_id,
            @RequestParam(required = true) @ApiParam("商品id") Long goods_id,
            @RequestParam(required = false) @ApiParam("商品数量") Integer goods_num
//            @RequestParam(required = true) @ApiParam("供应商id") Long provider_id,
//            @RequestParam(required = true) @ApiParam("商品名") String goods_name,
//            @RequestParam(required = true) @ApiParam("商品价格") Double goods_price
    ){
        Long user_id = 1L; // TODO：从session中获取
        Map<String, Object> data = new HashMap<>();
//        data.put("id",user_cart_id);
        data.put("user_id",user_id);
        data.put("goods_id",goods_id);
        if(null == goods_num){
            goods_num = 1;
        }
        data.put("quantity",goods_num);
        // 判断购物车内相同商品是否存在
        MysqlDB db = DBResource.get();
        WhereBuilder wb = WhereBuilder.getInstance();
        WhereBuilder wb1 = WhereBuilder.getInstance();
        wb.whereOr("user_id",user_id);
        wb1.whereOr("goods_id",goods_id);
        wb.subWhere(null,wb1.buildWhere());
        List<DataObject> existGoods = db.clear().select().from("user_cart").subWhere(null,wb.buildWhere()).get();
        if(null == existGoods){
            // 采用插入方式
            db.clear().insert("user_cart").data(data).save();
        }else {
            // 采用更新方式
//            System.out.println("进入了");
            int quantityInCart = existGoods.get(0).getInt("quantity")+goods_num;
            data.replace("quantity",goods_num,quantityInCart);
            db.clear().update("user_cart").data(data).where("id",existGoods.get(0).getString("id")).save();
        }
        if(db.queryIsFalse()){
            DBResource.returnResource(db);
            return Response.failed(501,501,"添加购物车失败");
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
        @RequestParam(required = false) @ApiParam("购物车id") Long id,
        @RequestParam(required = true) @ApiParam("订单收货地址") String address // 从数据库的地址表提取，这样不安全
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
        Long user_id=1l; // TODO:用户id从session中获取
        WhereBuilder wb = WhereBuilder.getInstance();
        WhereBuilder wb1 = WhereBuilder.getInstance();
        wb.where("user_id",user_id);
        wb1.where("is_chosen",1);
        wb.subWhere(null,wb1.buildWhere());
        List<DataObject> goodsInCart= db.clear().select()
                .from("user_cart")
                .subWhere(null,wb.buildWhere())
                .get();
        if(null==goodsInCart){
            DBResource.returnResource(db);
            return Response.failed(404, 0, "购物车无商品");
        }

        db.beginTransaction(); // 1 事务，设置setAutoCommit(false);
        List<DataObject> dataGoods=db.clear().select().from("goods_info")
                .whereIn("id", Func.array_field_obj("goods_id", goodsInCart))
                .forUpdate() // 配合1使用，数据具有互斥性时使用
                .get();
        if(null==dataGoods){
            boolean queryIsFalse=db.queryIsFalse();
            db.rollBack();
            DBResource.returnResource(db);
            return Response.failed(queryIsFalse?500:404, 0, "下单失败");
        }

        // 将从user_cart中取出来的goods_id 生成索引表
        Map<String, Integer> gid7index=Func.array_record_index_obj(dataGoods, "id");

        long temp = OrderNoAutoCreate.CreateOrderNo().getOrderNo();
        List<Map<String, Object>> insert2orderDetail=new ArrayList<>();
        int num=0;
        double totalPrice = 0.0;
        for(int i=0;i<goodsInCart.size();i++){
            String gid_t=goodsInCart.get(i).getString("goods_id");
            Integer index_t=gid7index.get(gid_t);
            if(null==index_t){
                db.rollBack();
                DBResource.returnResource(db);
                return Response.failed(501, 501, "exception");
            }
            int stockNum=dataGoods.get(index_t).getInteger("stock_num"); // 库存
            int quantity=goodsInCart.get(i).getInteger("quantity");
            if(quantity>stockNum){
                db.rollBack();
                DBResource.returnResource(db);
                return Response.failed(200, 0, "商品库存不足");
            }
            num+=quantity;

            Map<String, Object> od=new HashMap<>();
            od.put("goods_id", gid_t);
            od.put("user_id",user_id);
            od.put("order_no",temp);
            od.put("quantity", quantity);
            od.put("goods_name",dataGoods.get(index_t).getString("goods_name"));
            od.put("goods_price", dataGoods.get(index_t).getDouble("price"));
            od.put("provider_id",dataGoods.get(index_t).getString("provider_id"));
            insert2orderDetail.add(od);
            totalPrice = quantity*dataGoods.get(index_t).getDouble("price");
        }

        // 订单信息
        Map<String,Object> data = new HashMap<>();
        data.put("order_no", temp);
        data.put("goods_num",num);
        data.put("total_price",totalPrice);
        data.put("address",address);
        data.put("user_id",user_id);
        data.put("create_at",System.currentTimeMillis());

        db.clear().insert("user_order").data(data).save();
        if(db.queryIsFalse() || db.getLastAffectedRows()<1){
            db.rollBack();
            DBResource.returnResource(db);
            return Response.failed(502, 502, "下单失败");
        }

        Long order_id=db.getLastInsertId();
        for(int i=0;i<insert2orderDetail.size();i++){
            insert2orderDetail.get(i).put("order_id", order_id);

            db.clear().update("goods_info")
                    .putUpdateData("stock_num", "-", Func.toInteger(insert2orderDetail.get(i).get("quantity")))
                    .where("id", insert2orderDetail.get(i).get("goods_id"))
                    .save();
            if(db.queryIsFalse() || db.getLastAffectedRows()<1){
                db.rollBack();
                DBResource.returnResource(db);
                return Response.failed(504, 504, "下单失败-库存更新失败");
            }
        }
        db.clear().insert("user_order_goods").data(insert2orderDetail).save();
        if(db.queryIsFalse() || db.getLastAffectedRows()<1){
            db.rollBack();
            DBResource.returnResource(db);
            return Response.failed(503, 503, "下单失败");
        }

        db.clear().delete("user_cart").whereIn("id", Func.array_field_obj("id", goodsInCart)).save();
        if(db.queryIsFalse()){
            db.rollBack();
            DBResource.returnResource(db);
            return Response.failed(505, 505, "下单失败");
        }

        db.commit();
        DBResource.returnResource(db);
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
