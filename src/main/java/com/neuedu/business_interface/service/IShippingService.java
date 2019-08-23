package com.neuedu.business_interface.service;

import com.neuedu.business_interface.common.ServerResponse;
import com.neuedu.business_interface.pojo.Shipping;

public interface IShippingService {
    /**
     * 添加收货地址
     */
    ServerResponse add(Integer userId, Shipping shipping);

    /**
     * 删除收货地址
     */
    ServerResponse del(Integer userId,Integer shippingId);

    /**
     * 更新收货地址
     */
    ServerResponse update(Shipping shipping);
    /**
     * 选中查看的地址
     */
    ServerResponse select(Integer shippingId);
    /**
     * 地址列表
     */
    ServerResponse list(Integer pageNum,Integer pageSize);
}
