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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@Api(tags = "购物流程",description = "后台管理购物流程，加购物车，下单，生成订单")
@RequestMapping("/store/shop")
public class ShoppingController {
    @ApiOperation("查询展示商品")
    @PostMapping("/get")
    public ResponsePaginate getGoods(
            @RequestParam(required = false) @ApiParam("查询项") Integer searchId,
            @RequestParam(required = false) @ApiParam("查询id") Long id,
            @RequestParam(required = false) @ApiParam("查询字符") String str,
            @RequestParam(required = false) @ApiParam("页码") Integer page
    ){
        MysqlDB db = DBResource.get();

        WhereBuilder where=WhereBuilder.getInstance();
        //ResponsePaginate res = new ResponsePaginate();
        if(searchId == GoodsSearchBy.ALL.getSearchId() || searchId == null){
            where.whereOr("goods_name", "like", "%"+str+"%");
//            // 查询所有商品
//            res = db.clear().select().from("goods_name").paginate(page,null);
//            DBResource.returnResource(db);
//            return res;
        }
        if(searchId == GoodsSearchBy.ONE.getSearchId()){
            where.whereOr("id",id);
//            // 通过表ID查询单个商品
//            res = db.clear().select().from("goods_name").where("id","=",id).paginate(page,null);
//            DBResource.returnResource(db);
//            return res;
        }
        if(searchId == GoodsSearchBy.PROVIDER.getSearchId()){
            where.whereOrInSubQuery("provider_id", db.clear().select("id").from("provider")
                    .where("name", "like", "%"+str+"%").getRawSql());
            // 通过供货商provider查询商品
//            res = db.clear().select().from("goods_name").where("provider_id","=",id).paginate(page,null);
//            DBResource.returnResource(db);
//            return res;
        }
        if(searchId == GoodsSearchBy.CATEGORYID.getSearchId()){
            where.whereOrInSubQuery("category_id", db.clear().select("id").from("category")
                    .where("name", "like", "%"+str+"%").getRawSql());
//            // 第一级分类的商品
//            res = db.clear().select().from("goods_name").where("category_id","=",id).paginate(page,null);
//            DBResource.returnResource(db);
//            return res;
        } else if (searchId == GoodsSearchBy.CATEGORYID1.getSearchId()){
            // 第二级分类的商品
            res = db.clear().select().from("goods_name").where("category_id_1","=",id).paginate(page,null);
            DBResource.returnResource(db);
            return res;
        } else if (searchId == GoodsSearchBy.CATEGORYID2.getSearchId()){
            // 第三级别分类的商品
            res = db.clear().select().from("goods_name").where("category_id_2","=",id).paginate(page,null);
            DBResource.returnResource(db);
            return res;
        } else if (searchId == GoodsSearchBy.ISVIRTUAL.getSearchId()){
            // 是否虚拟产品
            res = db.clear().select().from("goods_name").where("is_virtual","=",id).paginate(page,null);
            DBResource.returnResource(db);
            return res;
        } else if (searchId == GoodsSearchBy.NAME.getSearchId()){
            // 根据商品名查询
            res = db.clear().select().from("goods_name").where("name","=",str).paginate(page,null);
            DBResource.returnResource(db);
            return res;
        } else if (searchId == GoodsSearchBy.BRANDID.getSearchId()){
            // 根据品牌查询
            res = db.clear().select().from("goods_name").where("brand_id","=",id).paginate(page,null);
            DBResource.returnResource(db);
            return res;
        }
        ResponsePaginate res= db.clear().select().from("goods_info").subWhere(null, where.buildWhere()).paginate(page, 20);
        if(res.assertNullDatalist()){
            return  res;
        }

        for(int i=0;i<res.data.datalist.size();i++){
            Map<String, Object> onerow=res.data.datalist.get(i);
            onerow.put("created_at", Func.timemillis2datetime(Func.toLong(onerow.get("created_at")), null));

        }
        return res;
    }

    @ApiOperation("查询商品详细信息")
    @PostMapping("/get_detail")
    public Response getDetail(
            @RequestParam(required = true) @ApiParam("商品id") Long id
    ){
        MysqlDB db = DBResource.get();
        ResponsePaginate res = db.clear().select().from("goods_info").where("id",id).paginate(null,null);
        return Response.success(res.data.datalist);
    }

    @ApiOperation("添加购物车")
    @PostMapping("/add")
    public Response addGoods(
            @RequestParam(required = true) @ApiParam("购物车id") Long user_cart_id,
            @RequestParam(required = true) @ApiParam("用户id") Long user_id,
            @RequestParam(required = true) @ApiParam("商品id") Long goods_id,
            @RequestParam(required = true) @ApiParam("供应商id") Long provider_id,
            @RequestParam(required = false) @ApiParam("商品名") String goods_name,
            @RequestParam(required = true) @ApiParam("商品价格") Double goods_price
    ){
        Map<String, Object> data = new HashMap<>();
        data.put("id",user_cart_id);
        data.put("user_id",user_id);
        data.put("good_provider_id",provider_id);
        Map<String,Object> data1 = new HashMap<>();
        data1.put("user_cart_id",user_cart_id);
        data1.put("user_id",user_id);
        data1.put("good_id",goods_id);
        data1.put("good_price",goods_price);
        data1.put("good_provider_id",provider_id);
        data1.put("good_name",goods_name);
        // 判断购物车是否存在
        MysqlDB db = DBResource.get();
        ResponsePaginate res = db.clear().select().from("user_cart").where("id",user_cart_id).paginate(null,null);
        if(res.assertNullData()){
            // 采用插入方式
            data.put("quantity",1);
            db.clear().insert("user_cart").data(data).save();
            db.clear().insert("user_cart_goods").data(data1).save();
        }else {
            // 采用更新方式
            int quantity = (int)res.data.datalist.get(0).get("quantity") + 1;
            data.put("quantity",quantity);
            db.clear().update("user_cart").data(data).where("id",user_cart_id).save();
            db.clear().insert("user_cart_goods").data(data1).save();
        }
        DBResource.returnResource(db);
        return Response.success();
    }

    @ApiOperation("从购物车中选择购买商品,未加入订单")
    @PostMapping("/choose_goods")
    public Response chooseGoods(
            @RequestParam(required = true) @ApiParam("购车内商品id") Long id
    ){
        MysqlDB db = DBResource.get();
        db.clear().update("user_cart_goods").data("is_chosen",1).where("id",id).save();
        DBResource.returnResource(db);
        return Response.success();
    }

    @ApiOperation("生成订单")
    @PostMapping("/create_order")
    public ResponsePaginate createOrder(
        @RequestParam(required = true) @ApiParam("购物车id") Long id
    ){
        MysqlDB db = DBResource.get();
        // 查询购物车中被选择的商品
        List<WhereClause> list = null;
        WhereClause clause = WhereClause.getInsance("user_cart_id");
        clause.filter = id;
        WhereClause clause1 = WhereClause.getInsance("is_chosen");
        clause1.filter = 1;
        list.add(clause);
        list.add(clause1);
        ResponsePaginate res = db.clear().select().from("user_cart_goods").where(list).paginate(null,null);
        Map<String,Object> data = new HashMap<>();
        long temp = OrderNoAutoCreate.CreateOrderNo().getOrderNo();
        data.put("order_no", temp);
        data.put("goods_num",res.data.datalist.size());
        data.put("user_id",res.data.datalist.get(0).get("user_id"));
        data.put("provider_id",res.data.datalist.get(0).get("provider_id"));
        data.put("create_time",new Date());
        db.clear().insert("user_order").data(data).save();

        return res;
    }

}
