package com.neuedu.business_interface.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.neuedu.business_interface.common.ServerResponse;
import com.neuedu.business_interface.dao.ShippingMapper;
import com.neuedu.business_interface.pojo.Shipping;
import com.neuedu.business_interface.service.IShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ShippingServiceImpl implements IShippingService {
    @Autowired
    ShippingMapper shippingMapper;
    @Override
    public ServerResponse add(Integer userId, Shipping shipping) {
        //参数校验
        if(shipping==null){
            ServerResponse.serverResponseByError("参数错误");
        }
        //添加
        shipping.setUserId(userId);
        shippingMapper.insert(shipping);
        //返回结果
        Map<String,Integer> map= Maps.newHashMap();
        map.put("shippingId",shipping.getId());
        return ServerResponse.createServerResponseBySuccess(map);
    }

    @Override
    public ServerResponse del(Integer userId, Integer shippingId) {
        //参数
        //参数校验
        if(shippingId==null){
            ServerResponse.serverResponseByError("参数错误");
        }
        //删除
        int result=shippingMapper.deleteByUserIdAndShippingId(userId, shippingId);
        if(result>0){
            return ServerResponse.createServerResponseBySuccess();
        }

        return ServerResponse.serverResponseByError("删除失败");
    }

    @Override
    public ServerResponse update(Shipping shipping) {
        //参数校验
        if(shipping==null){
            ServerResponse.serverResponseByError("参数错误");
        }
        //更新
        int result=shippingMapper.updateBySelectKey(shipping);
        if(result>0){
            return ServerResponse.createServerResponseBySuccess();
        }
        //返回结果
        return ServerResponse.serverResponseByError("更新失败");
    }

    @Override
    public ServerResponse select(Integer shippingId) {
        //参数校验
        if(shippingId==null){
            ServerResponse.serverResponseByError("参数错误");
        }
        Shipping shipping=shippingMapper.selectByPrimaryKey(shippingId);

        return ServerResponse.createServerResponseBySuccess(shipping);
    }

    @Override
    public ServerResponse list(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<Shipping> shippingList=shippingMapper.selectAll();
        PageInfo pageInfo=new PageInfo(shippingList);

        return ServerResponse.createServerResponseBySuccess(shippingList);
    }
}
