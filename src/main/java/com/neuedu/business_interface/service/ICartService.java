package com.neuedu.business_interface.service;

import com.neuedu.business_interface.common.ServerResponse;

import javax.servlet.http.HttpSession;

public interface ICartService {
    /**
     * 添加购物车
     */
    ServerResponse add(Integer userId,Integer productId, Integer count);

    ServerResponse list(Integer userId);

    ServerResponse update(Integer userId,Integer productId, Integer count);

    ServerResponse delete_product(Integer userId,String productIds);

    ServerResponse select(Integer userId,Integer productId,Integer check);

    ServerResponse get_cart_product_count(Integer userId);
}
