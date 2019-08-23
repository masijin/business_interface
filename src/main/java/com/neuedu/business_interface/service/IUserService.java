package com.neuedu.business_interface.service;


import com.neuedu.business_interface.common.ServerResponse;
import com.neuedu.business_interface.exception.MyException;
import com.neuedu.business_interface.pojo.UserInfo;

public interface IUserService {

    /**
     * 登录接口
     */
    ServerResponse login(UserInfo userInfo);


    /**
     * 注册接口
     * @param userInfo
     * @return
     */
    ServerResponse register(UserInfo userInfo);

    /**
     * 根据用户名找回密保问题
     */
    ServerResponse forget_get_question(String username);

    /**
     * 提交问题答案
     */
    ServerResponse forget_check_answer(String username,String question,String answer);

    /**
     * 忘记密码的重置密码
     */
    ServerResponse forget_reset_password(String username,String passwordNew,String forgetToken);

//    public UserInfo login(UserInfo userInfo) throws MyException;
}
