package com.neuedu.business_interface.service.impl;


import com.neuedu.business_interface.common.Const;
import com.neuedu.business_interface.common.GuavaCacheUtils;
import com.neuedu.business_interface.common.ServerResponse;
import com.neuedu.business_interface.dao.UserInfoMapper;
import com.neuedu.business_interface.exception.MyException;
import com.neuedu.business_interface.pojo.UserInfo;
import com.neuedu.business_interface.service.IUserService;
import com.neuedu.business_interface.utils.MD5Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserServiceImpl implements IUserService {
    @Autowired
    UserInfoMapper userInfoMapper;



    @Override
    public ServerResponse login(UserInfo userInfo){



        //step1:参数非空判断
        if(userInfo==null){
            return ServerResponse.serverResponseByError("参数不能为空");

        }
        if(userInfo.getUsername()==null||userInfo.getUsername().equals("")){
            return ServerResponse.serverResponseByError("用户名不能为空");
        }
        if(userInfo.getPassword()==null||userInfo.getPassword().equals("")){
            return ServerResponse.serverResponseByError("密码不能为空");

        }

        //step2:用户名是否存在
        int username_result=userInfoMapper.exitsUsername(userInfo.getUsername());
        if(username_result==0){//用户名不存在
            return ServerResponse.serverResponseByError("用户名不存在");
        }


        //step3:根据用户名和密码登录


        UserInfo userInfo_result=userInfoMapper.findByUsernameAndPassword(userInfo.getUsername(),MD5Utils.getMD5Code(userInfo.getPassword()));
        if(userInfo_result==null){
            return ServerResponse.serverResponseByError("密码错误");
        }



//        //step4:判断权限
//        if(userInfo_result.getRole()!=0){//不是管理员
//            return ServerResponse.serverResponseByError("没有权限访问");
//        }

        userInfo.setPassword("");
        return ServerResponse.createServerResponseBySuccess(userInfo_result);
    }

    /***
     *注册接口
     */
    @Override
    public ServerResponse register(UserInfo userInfo) {

        //step1:参数非空判断
        if(userInfo==null){
            return ServerResponse.serverResponseByError("参数不能为空");

        }
        //判断用户
        int username_result=userInfoMapper.exitsUsername(userInfo.getUsername());
        if(username_result!=0){//用户名存在
            return ServerResponse.serverResponseByError("用户名已经存在");
        }
        //校验邮箱
        int email_result=userInfoMapper.exitsEmail(userInfo.getEmail());
        if(email_result!=0){//邮箱存在
            return ServerResponse.serverResponseByError("邮箱已经使用");
        }
        //注册
        userInfo.setRole(Const.RoleEnum.ROLE_ADMIN.getCode());
        userInfo.setPassword(MD5Utils.getMD5Code(userInfo.getPassword()));
        int count=userInfoMapper.insert(userInfo);
        if(count>0){

            return ServerResponse.createServerResponseBySuccess("注册成功",userInfo);
        }
        return ServerResponse.serverResponseByError("注册失败");
    }

    @Override
    public ServerResponse forget_get_question(String username) {

        //参数校验
        if(username==null||username.equals("")){
            return ServerResponse.serverResponseByError("用户名不能为空");
        }
        //校验username
        int result=userInfoMapper.exitsUsername(username);
        if(result==0){
            return ServerResponse.serverResponseByError("用户名不存在,请重新输入");
        }
        //查找密保问题
        String question=userInfoMapper.selectQuestionByUsername(username);
        if(question==null||question.equals("")){
            return ServerResponse.serverResponseByError("密保问题为空");
        }
        return ServerResponse.createServerResponseBySuccess(question);
    }

    @Override
    public ServerResponse forget_check_answer(String username, String question, String answer) {
        //参数校验
        if(username==null||username.equals("")){
            return ServerResponse.serverResponseByError("用户名不能为空");
        }

        if(question==null||question.equals("")){
            return ServerResponse.serverResponseByError("问题不能为空");
        }

        if(answer==null||answer.equals("")){
            return ServerResponse.serverResponseByError("答案不能为空");
        }
        //根据用户名和密保问题，答案查询
        int result=userInfoMapper.selectByUsernameAndQuestionAndAnswer(username,question,answer);
        if(result==0){
            return ServerResponse.serverResponseByError("答案错误");
        }
        //服务端生成一个token保存，并返回给客户端
        String forgetToken= UUID.randomUUID().toString();
        //guava cache
        GuavaCacheUtils.put(username,forgetToken);
        return ServerResponse.createServerResponseBySuccess(forgetToken);
    }

    @Override
    public ServerResponse forget_reset_password(String username, String passwordNew, String forgetToken) {

        //参数校验
        if(username==null||username.equals("")){
            return ServerResponse.serverResponseByError("用户名不能为空");
        }

        if(passwordNew==null||passwordNew.equals("")){
            return ServerResponse.serverResponseByError("密码不能为空");
        }

        if(forgetToken==null||forgetToken.equals("")){
            return ServerResponse.serverResponseByError("token不能为空");
        }
        //token校验
        String token=GuavaCacheUtils.get(username);
        if(token==null){
           return ServerResponse.serverResponseByError("token过期");
        }
        if(!token.equals(forgetToken)){
            return ServerResponse.serverResponseByError("无效的token");
        }
        //修改密码
        int result=userInfoMapper.updateUserPassword(username,MD5Utils.getMD5Code(passwordNew));
        if(result>0){
            return ServerResponse.createServerResponseBySuccess();
        }

        return ServerResponse.serverResponseByError("密码修改失败");
    }
}
