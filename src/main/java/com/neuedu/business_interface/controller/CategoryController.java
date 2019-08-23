package com.neuedu.business_interface.controller;
import com.neuedu.business_interface.common.Const;
import com.neuedu.business_interface.common.ServerResponse;
import com.neuedu.business_interface.pojo.UserInfo;
import com.neuedu.business_interface.service.ICategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/manage/category/")
public class CategoryController {
    @Autowired
    ICategoryService categoryService;

    /**
     * 获取品类子节点(平级）
     */
    @RequestMapping("get_category.do")
    public ServerResponse get_category(HttpSession session,Integer categoryId){
        UserInfo userInfo=(UserInfo)session.getAttribute(Const.CURRENTUSER);
        if(userInfo==null){
            return ServerResponse.serverResponseByError(Const.ResponseCodeEnum.NEED_LOGIN.getCode(),Const.ResponseCodeEnum.NEED_LOGIN.getDesc());
        }
        System.out.println(userInfo.getRole());
        System.out.println(Const.RoleEnum.ROLE_ADMIN.getCode());
        //判断用户权限
        if(userInfo.getRole()!=Const.RoleEnum.ROLE_ADMIN.getCode()){

            return ServerResponse.serverResponseByError(Const.ResponseCodeEnum.NO_PRIVILEGE.getCode(),Const.ResponseCodeEnum.NO_PRIVILEGE.getDesc());
        }


        return ServerResponse.createServerResponseBySuccess(categoryService.get_category(categoryId));
    }


    /**
     * 获取当前分类id及递归子节点categoryId
     */
    @RequestMapping("get_deep_category.do")
    public ServerResponse get_deep_category(HttpSession session,Integer categoryId){
        UserInfo userInfo=(UserInfo)session.getAttribute(Const.CURRENTUSER);
        if(userInfo==null){
            return ServerResponse.serverResponseByError(Const.ResponseCodeEnum.NEED_LOGIN.getCode(),Const.ResponseCodeEnum.NEED_LOGIN.getDesc());
        }
        //判断用户权限
        if(userInfo.getRole()!=Const.RoleEnum.ROLE_ADMIN.getCode()){

            return ServerResponse.serverResponseByError(Const.ResponseCodeEnum.NO_PRIVILEGE.getCode(),Const.ResponseCodeEnum.NO_PRIVILEGE.getDesc());
        }


        return categoryService.get_deep_category(categoryId);
    }


}
