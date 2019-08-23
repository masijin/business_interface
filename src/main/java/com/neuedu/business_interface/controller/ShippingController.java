package com.neuedu.business_interface.controller;

import com.neuedu.business_interface.common.Const;
import com.neuedu.business_interface.common.ServerResponse;
import com.neuedu.business_interface.pojo.Shipping;
import com.neuedu.business_interface.pojo.UserInfo;
import com.neuedu.business_interface.service.IShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping(value ="/shipping" )
public class ShippingController {

    @Autowired
    IShippingService shippingService;
    /**
     * 添加地址
     */
    @RequestMapping("/add.do")
    public ServerResponse add(HttpSession session, Shipping shipping){

        UserInfo userInfo= (UserInfo) session.getAttribute(Const.CURRENTUSER);
        if(userInfo==null){
            return ServerResponse.serverResponseByError("需要登录");
        }
        return shippingService.add(userInfo.getId(),shipping);
    }


    /**
     *删除地址
     */
    @RequestMapping("/del.do")
    public ServerResponse del(HttpSession session, Integer shippingId){

        UserInfo userInfo= (UserInfo) session.getAttribute(Const.CURRENTUSER);
        if(userInfo==null){
            return ServerResponse.serverResponseByError("需要登录");
        }
        return shippingService.del(userInfo.getId(),shippingId);
    }
    /**
     *登录状态更新地址
     */
    @RequestMapping("/update.do")
    public ServerResponse update(HttpSession session, Shipping shipping){

        UserInfo userInfo= (UserInfo) session.getAttribute(Const.CURRENTUSER);
        if(userInfo==null){
            return ServerResponse.serverResponseByError("需要登录");
        }
        shipping.setUserId(userInfo.getId());
        return shippingService.update(shipping);
    }

    /**
     *选中查看的地址
     */
    @RequestMapping("/select.do")
    public ServerResponse select(HttpSession session, Integer shippingId){

        UserInfo userInfo= (UserInfo) session.getAttribute(Const.CURRENTUSER);
        if(userInfo==null){
            return ServerResponse.serverResponseByError("需要登录");
        }

        return shippingService.select(shippingId);
    }
    /**
     *地址列表
     */
    @RequestMapping("/list.do")
    public ServerResponse list(HttpSession session,
                               @RequestParam(required = false,defaultValue = "1") Integer pageNum,
                               @RequestParam(required = false,defaultValue = "10")Integer pageSize){

        UserInfo userInfo= (UserInfo) session.getAttribute(Const.CURRENTUSER);
        if(userInfo==null){
            return ServerResponse.serverResponseByError("需要登录");
        }

        return shippingService.list(pageNum,pageSize);
    }
}
