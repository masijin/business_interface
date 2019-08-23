package com.neuedu.business_interface.controller;

import com.neuedu.business_interface.common.Const;
import com.neuedu.business_interface.common.ServerResponse;
import com.neuedu.business_interface.pojo.Product;
import com.neuedu.business_interface.pojo.UserInfo;
import com.neuedu.business_interface.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping(value ="/manage/product" )
public class ProductManageController {

    /**
     * 新增或更新产品
     */

    @Autowired
    IProductService productService;


    @RequestMapping(value = "/save.do")
    public ServerResponse saveOrUpdate(HttpSession session , Product product){

        UserInfo userInfo=(UserInfo)session.getAttribute(Const.CURRENTUSER);
        if(userInfo==null){
            return ServerResponse.serverResponseByError(Const.ResponseCodeEnum.NEED_LOGIN.getCode(),Const.ResponseCodeEnum.NEED_LOGIN.getDesc());
        }
        if(userInfo.getRole()!=Const.RoleEnum.ROLE_ADMIN.getCode()){
            return ServerResponse.serverResponseByError(Const.ResponseCodeEnum.NO_PRIVILEGE.getCode(),Const.ResponseCodeEnum.NO_PRIVILEGE.getDesc());
        }
        return productService.saveOrUpdate(product);
    }

    /**
     * 产品上下架
     */
    @RequestMapping(value = "/set_sale_status.do")
    public ServerResponse set_sale_status(HttpSession session , Integer productId,Integer status){

        UserInfo userInfo=(UserInfo)session.getAttribute(Const.CURRENTUSER);
        if(userInfo==null){
            return ServerResponse.serverResponseByError(Const.ResponseCodeEnum.NEED_LOGIN.getCode(),Const.ResponseCodeEnum.NEED_LOGIN.getDesc());
        }
        if(userInfo.getRole()!=Const.RoleEnum.ROLE_ADMIN.getCode()){
            return ServerResponse.serverResponseByError(Const.ResponseCodeEnum.NO_PRIVILEGE.getCode(),Const.ResponseCodeEnum.NO_PRIVILEGE.getDesc());
        }
        return productService.set_sale_status(productId,status);
    }
    /**
     * 查看商品详情
     */
    @RequestMapping(value = "/detail.do")
    public ServerResponse detail(HttpSession session , Integer productId){

        UserInfo userInfo=(UserInfo)session.getAttribute(Const.CURRENTUSER);
        if(userInfo==null){
            return ServerResponse.serverResponseByError(Const.ResponseCodeEnum.NEED_LOGIN.getCode(),Const.ResponseCodeEnum.NEED_LOGIN.getDesc());
        }
        if(userInfo.getRole()!=Const.RoleEnum.ROLE_ADMIN.getCode()){
            return ServerResponse.serverResponseByError(Const.ResponseCodeEnum.NO_PRIVILEGE.getCode(),Const.ResponseCodeEnum.NO_PRIVILEGE.getDesc());
        }
        return productService.detail(productId);
    }
    /**
     * 查看商品列表
     */
    @RequestMapping(value = "/list.do")
    public ServerResponse list(HttpSession session ,
                               @RequestParam(value = "pageNum",required = false,defaultValue = "1")Integer pageNum,
                               @RequestParam(value = "pageSize",required = false,defaultValue = "10")Integer pageSize){

        UserInfo userInfo=(UserInfo)session.getAttribute(Const.CURRENTUSER);
        if(userInfo==null){
            return ServerResponse.serverResponseByError(Const.ResponseCodeEnum.NEED_LOGIN.getCode(),Const.ResponseCodeEnum.NEED_LOGIN.getDesc());
        }
        if(userInfo.getRole()!=Const.RoleEnum.ROLE_ADMIN.getCode()){
            return ServerResponse.serverResponseByError(Const.ResponseCodeEnum.NO_PRIVILEGE.getCode(),Const.ResponseCodeEnum.NO_PRIVILEGE.getDesc());
        }
        return productService.list(pageNum,pageSize);
    }

    /**
     * 产品搜索
     */
    @RequestMapping(value = "/search.do")
    public ServerResponse search(HttpSession session ,
                                 @RequestParam(value = "productId",required = false)Integer productId,
                                 @RequestParam(value = "productName",required = false)String  productName,
                               @RequestParam(value = "pageNum",required = false,defaultValue = "1")Integer pageNum,
                               @RequestParam(value = "pageSize",required = false,defaultValue = "10")Integer pageSize){

        UserInfo userInfo=(UserInfo)session.getAttribute(Const.CURRENTUSER);
        if(userInfo==null){
            return ServerResponse.serverResponseByError(Const.ResponseCodeEnum.NEED_LOGIN.getCode(),Const.ResponseCodeEnum.NEED_LOGIN.getDesc());
        }
        if(userInfo.getRole()!=Const.RoleEnum.ROLE_ADMIN.getCode()){
            return ServerResponse.serverResponseByError(Const.ResponseCodeEnum.NO_PRIVILEGE.getCode(),Const.ResponseCodeEnum.NO_PRIVILEGE.getDesc());
        }
        return productService.search(productId,productName,pageNum,pageSize);
    }

}
