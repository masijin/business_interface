package com.neuedu.business_interface.service;

import com.neuedu.business_interface.common.ServerResponse;

public interface IOrderService {
    /**
     * 创建订单
     */
    ServerResponse createOrder(Integer userId,Integer shippingId);

    /**
     * 取消订单
     */
    ServerResponse cancel(Integer userId,Long orderNo);
    /**
     * 获取购物车中订单详情
     */
    ServerResponse get_order_cart_product(Integer userid);
    /**
     * 订单列表
     */
    ServerResponse list(Integer userId,Integer pageNum,Integer pageSize);
    /**
     * 订单详情
     */
    ServerResponse detail(Long orderNo);

}
