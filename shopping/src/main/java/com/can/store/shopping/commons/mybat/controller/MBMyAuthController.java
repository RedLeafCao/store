package com.can.store.shopping.commons.mybat.controller;

import com.can.store.shopping.commons.MyMD5Units;
import com.can.store.shopping.commons.OrderNoAutoCreate;
import com.can.store.shopping.commons.UserIDDistribute;
import com.can.store.shopping.commons.ValidatorAutoCreate;
import com.can.store.shopping.commons.kiss.helper.session.Session;
import com.can.store.shopping.commons.kiss.helper.session.UserSession;
import com.can.store.shopping.commons.kizz.http.response.Response;
import com.can.store.shopping.commons.kizz.lib.utils.Func;
import com.can.store.shopping.commons.mybat.model.User;
import com.can.store.shopping.commons.mybat.model.UserInfos;
import com.can.store.shopping.commons.mybat.model.Validator;
import com.can.store.shopping.commons.mybat.service.UserInfoService;
import com.can.store.shopping.commons.mybat.service.UserService;
import com.can.store.shopping.commons.mybat.service.ValidateService;
import com.can.store.shopping.dto.UserAdmin;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 使用Mybatis的用户管理controller
 * 2019.08.08
 */
@Controller
@Api(tags = "用户管理",description = "用户登录注册，基本信息修改，密码修改")
@RequestMapping("/mb/my_auth")
public class MBMyAuthController {

    @Autowired
    private UserService userService;
    @Autowired
    private ValidateService validateService;
    @Autowired
    private UserInfoService userInfoService;

    @ApiOperation("登录")
    @RequestMapping(value = "/login",method = RequestMethod.POST)
    @ResponseBody
    public Response login(
            @RequestParam(required = true) @ApiParam("验证码") String validator,
            @RequestParam(required = true) @ApiParam("用户id") Long user_id,
            @RequestParam(required = true) @ApiParam("用户密码") String password
    ){
        // 判断验证码是否正确
        List<Validator> validatorList = validateService.SelectBySessionIdAndValidatorCode(Session.getSessionID(),validator);
        if (validatorList.size() < 1 || validatorList.size() >1){
            return Response.failed(601,601,"验证码错误");
        }
        int raw = validateService.DeleteBySessionIdAndValidatorCode(Session.getSessionID(),validator);
        if (raw < 1){
            return Response.failed(601,601,"验证码更新失败");
        }

        // 查询用户是否存在
        User user = userService.selectUser(user_id);
        if(null == user){
            return Response.failed(602,602,"用户不存在，用户账号错误");
        }
        // 密码是否正确
        MyMD5Units md5 = MyMD5Units.getInstance(password);
        if(!(md5.getMd5Code().equals(user.getPassword()))){
            return Response.failed(603,603,"用户密码错误");
        }
        // session
        Session.set("user_id",user_id);
        return Response.success(user.getUserId().toString());
    }

    @ApiOperation("注册")
    @RequestMapping(value = "/registe",method = RequestMethod.POST)
    @ResponseBody
    public Response registe(
            @RequestBody UserAdmin userAdmin,
            @RequestParam(required = true) @ApiParam("验证码") String validator,
            @RequestParam(required = true) @ApiParam("确认密码") String check_password
    ){
        // 判断验证码是否正确
        List<Validator> validatorList = validateService.SelectBySessionIdAndValidatorCode(Session.getSessionID(),validator);
        if (validatorList.size() < 1 || validatorList.size() >1){
            return Response.failed(601,601,"验证码错误");
        }
        int raw = validateService.DeleteBySessionIdAndValidatorCode(Session.getSessionID(),validator);
        if (raw < 1){
            return Response.failed(601,601,"验证码更新失败");
        }

        // 判断两次密码是否正确
        if(!(userAdmin.getPassword().equals(check_password))){
            return Response.failed(602,602,"两次密码不一致");
        }
        // 生成用户id
        UserIDDistribute udd = UserIDDistribute.getInstance();
        Long user_id = udd.getUser_id();
        // MD5加密
        MyMD5Units md5 = MyMD5Units.getInstance(check_password);

        User user = new User();
        user.setUserId(user_id);
        user.setPassword(md5.getMd5Code());
        user.setCreateTime(System.currentTimeMillis());
        user.setLastTime(0L);
        // 插入数据库
        int row = userService.addUser(user);
        if(row < 1){
            return Response.failed(603,603,"用户注册失败");
        }
        return Response.success();
    }

    @ApiOperation("生成验证码")
    @RequestMapping(value = "/validate",method = RequestMethod.POST)
    @ResponseBody
    public Response validate(){
        try {
            ValidatorAutoCreate validator = ValidatorAutoCreate.getInstance();
            Session.setSessionClass("UserSession");

            if(null == validator.getCode()){
                return Response.failed(601,601,"验证码生成失败");
            }

            Session.set("validator_code",validator.getCode());
            if(null == validator.getCodeUrl()){
                return Response.failed(602,602,"获取验证码存放路径失败");
            }

            Validator model = new Validator();
            model.setValidatorCode(validator.getCode());
            model.setValidatorUrl(validator.getCodeUrl());
            model.setRealLocation(validator.getRealLocation());
            model.setSessionId(Session.getSessionID());

            int raw = validateService.insert(model);
            if(raw < 1){
                return Response.failed(603,603,"验证码更新异常");
            }

            return Response.success(validator.getCodeUrl());
        } catch (IOException e) {
            e.printStackTrace();
            return Response.failed(604,604,"获取验证码异常");
        }
    }

    @ApiOperation("查询所有用户")
    @RequestMapping(value = "/list_all",method = RequestMethod.POST)
    @ResponseBody
    public Response list_all(){
        // 应该验证为管理员账号方可查询所有用户的账号密码
        List<User> users = userService.selectAllUser(10,20);
        if(users.size() == 0){
            return Response.failed(601,601,"没有用户");
        }

        List<Map<String,Object>> uMaps = new ArrayList<>();
        Map<String,Object> map = new HashMap<>();
        int i = 0;
        for(User user : users){
            map.put("id",user.getId());
            map.put("user_id",user.getUserId());
            map.put("password",user.getPassword());
            map.put("create_time",user.getCreateTime());
            map.put("update_time",user.getLastTime());
            uMaps.set(i,map);
            map.clear();
            i++;
        }
        return Response.success(uMaps);
    }

    @ApiOperation("修改用户密码")
    @RequestMapping(value = "/alter_password",method = RequestMethod.POST)
    @ResponseBody
    public Response alter_password(
            @RequestParam(required = true) @ApiParam("原密码") String originPassword,
            @RequestParam(required = true) @ApiParam("新密码") String password,
            @RequestParam(required = true) @ApiParam("确认密码") String checkPassword
    ){
        UserSession us = UserSession.getInstance();
        Long user_id = us.getUserId();
        User user = userService.selectUser(user_id);
        if(user_id == null || user_id == 0L){
            return Response.failed(600,600,"登录状态异常，请重新登录");
        }
        if(user == null){
            return Response.failed(601,601,"当前用户异常，请重新登录");
        }
        if(!originPassword.equals(user.getPassword())){
            return Response.failed(602,602,"密码验证错误，请重新输入原密码验证");
        }
        if(!password.equals(checkPassword)){
            return Response.failed(603,603,"新密码不一致，请重新输入");
        }
        user.setPassword(password);
        int raw = userService.updateUser(user);
        if(raw < 1){
            return Response.failed(604,604,"密码修改失败");
        }
        return Response.success();
    }

    @ApiOperation("查询用户基本信息")
    @RequestMapping(value = "/select_basic",method = RequestMethod.POST)
    @ResponseBody
    public Response selectBasic()
    {
        UserSession us = UserSession.getInstance();
        Long user_id = us.getUserId();
        if(user_id == 0L){
            return Response.failed(601,601,"登录状态异常，请重新登录");
        }
        UserInfos userInfos = userInfoService.selectByUserId(user_id);
        if(null == userInfos){
            return Response.failed(602,602,"当前用户不存在");
        }
        Map<String,Object> uInfo = new HashMap<>();
        uInfo.put("id",userInfos.getId());
        uInfo.put("user_id",userInfos.getUserId());
        uInfo.put("password",userInfos.getPassword());
        uInfo.put("nick_name",userInfos.getNickName());
        uInfo.put("phone",userInfos.getPhone());
        uInfo.put("icon",userInfos.getIcon());
        uInfo.put("gender",userInfos.getGender());
        uInfo.put("birthday", Func.timemillis2datetime(userInfos.getBirthday(),null));
        uInfo.put("create_time",Func.timemillis2datetime(userInfos.getCreateTime(),null));
        uInfo.put("last_time",Func.timemillis2datetime(userInfos.getLastTime(),null));
        return Response.success(uInfo);
    }

    @ApiOperation("修改用户基本信息")
    @RequestMapping(value = "alter_basic",method = RequestMethod.POST)
    @ResponseBody
    public Response alterBasic(
            @RequestBody UserInfos userInfo
    ){
        UserSession us = UserSession.getInstance();
        Long user_id = us.getUserId();

        if(user_id == 0L){
            return Response.failed(602,602,"当前用户已退出登录");
        }
        if(userInfo.getNickName() == null && userInfo.getIcon() == null && userInfo.getPhone() == null
                && userInfo.getGender() == null && userInfo.getBirthday() == null){
            return Response.failed(601,601,"无修改项，修改无效");
        }

        userInfo.setUserId(user_id);
        int raw = userInfoService.updateByUserId(userInfo);
        if(raw < 1){
            return Response.failed(603,603,"修改信息失败");
        }
        return Response.success("修改基本信息成功");
    }

    @ApiOperation("退出登录")
    @RequestMapping(value = "logout",method = RequestMethod.POST)
    @ResponseBody
    public Response logout(){
        UserSession us = UserSession.getInstance();
        Long user_id = us.getUserId();
        int raw = userInfoService.updateLastTime(user_id,System.currentTimeMillis());
        if(raw < 1){
            return Response.failed(601,601,"退出失败");
        }
        Session.set("user_id",0L);
        return Response.success("已退出");
    }

    @ApiOperation("删除用户")
    @RequestMapping(value = "del",method = RequestMethod.POST)
    @ResponseBody
    public Response del(){
        UserSession us = UserSession.getInstance();
        Long user_id = us.getUserId();
        if(user_id == null){
            return Response.failed(601,601,"登录状态异常，请重新登录");
        }
        if(userService.selectUser(user_id) == null){
            return Response.failed(602,602,"当前用户不存在");
        }
        int raw = userService.deleteUser(user_id);
        if(raw < 1){
            return Response.failed(603,603,"当前用户删除失败");
        }
        Session.set("user_id",0L);
        // TODO:删除该用户的相关信息，如订单，购物车等
        return Response.success();
    }
}
