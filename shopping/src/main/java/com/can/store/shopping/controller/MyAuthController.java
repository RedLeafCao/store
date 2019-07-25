package com.can.store.shopping.controller;

import com.can.store.shopping.commons.kiss.db.DBResource;
import com.can.store.shopping.commons.kiss.helper.session.AdminSession;
import com.can.store.shopping.commons.kiss.helper.session.Session;
import com.can.store.shopping.commons.kizz.db.DataObject;
import com.can.store.shopping.commons.kizz.db.mysql.MysqlDB;
import com.can.store.shopping.commons.kizz.http.response.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@Api(tags = "用户管理",description = "用户登录注册，退出，修改信息等操作")
@RequestMapping("/store/my_auth")
public class MyAuthController {

    @ApiOperation("登录")
    @RequestMapping("/login")
    @ResponseBody
    public Response login(
            @RequestParam(required = true) @ApiParam("用户id") Long user_id,
            @RequestParam(required = true) @ApiParam("用户密码") String password // 经过加密?
    ){
      // 判断id 密码是否为空
      if(null == user_id || null == password)  {
          return Response.failed(601,601,"用户账号密码不能为空");
      }
      // 判断当前用户是否存在
      MysqlDB db = DBResource.get();
      String[] fields = {"user_id","password"};
      List<DataObject> userInfo = db.clear().fields(fields).from("users_info").where("user_id",user_id).get();
      if(null == userInfo){
          DBResource.returnResource(db);
          return Response.failed(602,602,"不存在该用户或用户账号错误");
      }
      // TODO:密码是否加密，需解码。
      if(!(userInfo.get(0).getLong("user_id").equals(user_id) && userInfo.get(0).getString("password").equals(password))){
          DBResource.returnResource(db);
          return Response.failed(603,603,"用户密码错误");
      }
      Session.setSessionClass("UserSession");
      Session.set("user_id",user_id);
      return Response.success();
    }
}
