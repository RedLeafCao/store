package com.can.store.shopping.controller;

import com.can.store.shopping.commons.MyMD5Units;
import com.can.store.shopping.commons.UserIDDistribute;
import com.can.store.shopping.commons.ValidatorAutoCreate;
import com.can.store.shopping.commons.kiss.db.DBResource;
import com.can.store.shopping.commons.kiss.helper.session.AdminSession;
import com.can.store.shopping.commons.kiss.helper.session.Session;
import com.can.store.shopping.commons.kizz.db.DataObject;
import com.can.store.shopping.commons.kizz.db.mysql.MysqlDB;
import com.can.store.shopping.commons.kizz.db.mysql.WhereBuilder;
import com.can.store.shopping.commons.kizz.http.response.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import sun.security.pkcs11.Secmod;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@Api(tags = "用户管理",description = "用户登录注册，退出，修改信息等操作")
@RequestMapping("/store/my_auth")
public class MyAuthController {

    @ApiOperation("登录")
    @RequestMapping("/login")
    @ResponseBody
    public Response login(
            @RequestParam(required = true) @ApiParam("验证码") String validator,
            @RequestParam(required = true) @ApiParam("用户id") Long user_id,
            @RequestParam(required = true) @ApiParam("用户密码") String password
    ){
        MysqlDB db = DBResource.get();
        if(null == validator){
            DBResource.returnResource(db);
          return Response.failed(604,604,"验证码不能为空");
        }
        String sessionId = Session.getSessionID();
        WhereBuilder wb = WhereBuilder.getInstance();
        WhereBuilder wb1 = WhereBuilder.getInstance();
        wb.whereOr("session_id","like",sessionId);
        wb1.whereOr("validator_code",validator);
        wb.subWhere(null,wb1.buildWhere());
        List<DataObject> code = db.clear().select().from("validator").subWhere(null,wb.buildWhere()).get();
        if(code.size() < 1){
            DBResource.returnResource(db);
             return Response.failed(606,606,"验证码错误");
        }
        // 删除验证码
        File del = new File(code.get(0).getString("real_location"));
        del.delete();
        db.clear().delete("validator").subWhere(null,wb.buildWhere()).save();
        // 判断id 密码是否为空
        if(null == user_id || null == password)  {
            DBResource.returnResource(db);
          return Response.failed(601,601,"用户账号密码不能为空");
      }
      // 判断当前用户是否存在
        MyMD5Units md5 = MyMD5Units.getInstance(password);
      String[] fields = {"user_id","password"};
      List<DataObject> userInfo = db.clear().fields(fields).from("users_info").where("user_id",user_id).get();
      if(null == userInfo){
          DBResource.returnResource(db);
          return Response.failed(602,602,"不存在该用户或用户账号错误");
      }
      if(!(userInfo.get(0).getLong("user_id").equals(user_id) && userInfo.get(0).getString("password").equals(md5.getMd5Code()))){
          DBResource.returnResource(db);
          return Response.failed(603,603,"用户密码错误");
      }
      Session.setSessionClass("UserSession");
      Session.set("user_id",user_id);
      DBResource.returnResource(db);
      return Response.success();
    }

    @ApiOperation("验证码生成")
    @RequestMapping("/validate")
    @ResponseBody
    public Response registe_validate(
    ){
        try {
            ValidatorAutoCreate validCode = ValidatorAutoCreate.getInstance();
            Session.setSessionClass("UserSession");
            if(null == validCode.getCode()){
                return Response.failed(601,601,"获取验证码失败");
            }
            Session.set("validator_code",validCode.getCode());
            if(null == validCode.getCodeUrl()){
                return Response.failed(602,602,"获取验证码失败");
            }
            Map<String,Object> validator = new HashMap<>();
            validator.put("validator_code",validCode.getCode());
            validator.put("validator_url",validCode.getCodeUrl());
            validator.put("session_id",Session.getSessionID());
            validator.put("real_location",validCode.getRealLocation());
            MysqlDB db = DBResource.get();
            db.clear().insert("validator").data(validator).save();
            if(db.getLastAffectedRows()<1){
                return Response.failed(605,605,"获取验证码失败");
            }
            Map<String,Object> url = new HashMap<>();
            url.put("validator_code_url",validCode.getCodeUrl());
            return Response.success(url);
        } catch (IOException e){
            e.printStackTrace();
            return Response.failed(604,604,"获取验证码失败");
       }
    }

    @ApiOperation("注册")
    @RequestMapping("/registe")
    @ResponseBody
    public Response registe(
            @RequestParam(required = true) @ApiParam("验证码") String validator,
            @RequestParam(required = true) @ApiParam("用户手机号") Long phone,
            @RequestParam(required = true) @ApiParam("用户密码") String password,
            @RequestParam(required = true) @ApiParam("确认密码") String check_password
    ){
        MysqlDB db = DBResource.get();
        if(null == validator){
            DBResource.returnResource(db);
            return Response.failed(604,604,"验证码不能为空");
        }
        String sessionId = Session.getSessionID();
        WhereBuilder wb = WhereBuilder.getInstance();
        WhereBuilder wb1 = WhereBuilder.getInstance();
        wb.whereOr("session_id","like",sessionId);
        wb1.whereOr("validator_code",validator);
        wb.subWhere(null,wb1.buildWhere());
        List<DataObject> code = db.clear().select().from("validator").subWhere(null,wb.buildWhere()).get();
        if(code.size() < 1){
            DBResource.returnResource(db);
            return Response.failed(606,606,"验证码错误");
        }
        // 删除验证码
        File del = new File(code.get(0).getString("real_location"));
        del.delete();
        db.clear().delete("validator").subWhere(null,wb.buildWhere()).save();
        if(!password.equals(check_password)){
            DBResource.returnResource(db);
            return Response.failed(607,607,"两次密码不一致");
        }
        // 生成唯一的user_id
        UserIDDistribute udd = UserIDDistribute.getInstance();
        long uid = udd.getUser_id();
        // 密码进行MD5加密
        MyMD5Units md5 = MyMD5Units.getInstance(password);
        Map<String,Object> user = new HashMap<>();
        user.put("user_id",uid);
        user.put("password",md5.getMd5Code());
        user.put("phone",phone);
        db.clear().insert("users_info").data(user).save();
        if(db.queryIsFalse()){
            UserIDDistribute udd1 = UserIDDistribute.getInstance();
            long uid1 = udd1.getUser_id();
            user.replace("user_id",uid,uid1);
            db.clear().insert("users_info").data(user).save();
            if(db.queryIsFalse()){
                DBResource.returnResource(db);
                return Response.failed(608,608,"服务异常，请重新登录");
            }
            DBResource.returnResource(db);
            return Response.success();
        }
        DBResource.returnResource(db);
        return Response.success();
    }
}
