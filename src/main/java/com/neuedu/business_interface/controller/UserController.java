package com.neuedu.business_interface.controller;


import com.neuedu.business_interface.common.Const;
import com.neuedu.business_interface.common.ServerResponse;
import com.neuedu.business_interface.pojo.UserInfo;
import com.neuedu.business_interface.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;


@RestController
@RequestMapping(value = "/user")
public class UserController{

    @Autowired
    IUserService userService;

    @RequestMapping(value = "/login.do")
    public ServerResponse login(UserInfo userInfo, HttpSession session){

        ServerResponse serverResponse=userService.login(userInfo);
        if(serverResponse.isSuccess()){

            userInfo=(UserInfo)serverResponse.getData();
            session.setAttribute(Const.CURRENTUSER,userInfo);
        }

        return serverResponse;
    }

    @RequestMapping(value = "/register.do")
    public ServerResponse register(UserInfo userInfo){

        ServerResponse serverResponse=userService.register(userInfo);

        return serverResponse;
    }

    @RequestMapping(value = "/forget_get_question.do")
    public ServerResponse forget_get_question(String username){

        ServerResponse serverResponse=userService.forget_get_question(username);

        return serverResponse;
    }

    @RequestMapping(value = "/forget_check_answer.do")
    public ServerResponse forget_check_answer(String username,String question,String answer){

        ServerResponse serverResponse=userService.forget_check_answer(username,question,answer);

        return serverResponse;
    }

    /**
     * 忘记密码的重置密码
     */
    @RequestMapping(value = "/forget_reset_password.do")
    public ServerResponse forget_reset_password(String username,String passwordNew,String forgetToken){

        ServerResponse serverResponse=userService.forget_reset_password(username,passwordNew,forgetToken);

        return serverResponse;
    }

//
//
//
//    @RequestMapping("/login")
//    public UserInfo login(UserInfo userInfo){
//
//        UserInfo userInfo1=userService.login(userInfo);
//
//
//        return userInfo1;
//    }
//
//    @RequestMapping("/res")
//    public ServerResponse res(){
//        return ServerResponse.createServerResponseBySuccess();
//    }


}
