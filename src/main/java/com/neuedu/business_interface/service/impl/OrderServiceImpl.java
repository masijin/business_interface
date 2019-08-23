package com.neuedu.business_interface.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.neuedu.business_interface.common.Const;
import com.neuedu.business_interface.common.ServerResponse;
import com.neuedu.business_interface.dao.*;
import com.neuedu.business_interface.pojo.*;
import com.neuedu.business_interface.service.IOrderService;
import com.neuedu.business_interface.utils.BigDecimalUtils;
import com.neuedu.business_interface.utils.DateUtils;
import com.neuedu.business_interface.utils.PropertiesUtils;
import com.neuedu.business_interface.vo.CartOrderItemVO;
import com.neuedu.business_interface.vo.OrderItemVO;
import com.neuedu.business_interface.vo.OrderVO;
import com.neuedu.business_interface.vo.ShippingVO;
import com.sun.org.apache.xpath.internal.operations.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
public class OrderServiceImpl implements IOrderService {
    @Autowired
    CartMapper cartMapper;
    @Autowired
    ProductMapper productMapper;
    @Autowired
    OrderMapper orderMapper;
    @Autowired
    OrderItemMapper orderItemMapper;
    @Autowired
    ShippingMapper shippingMapper;
    @Override
    public ServerResponse createOrder(Integer userId, Integer shippingId) {
        //参数校验
        if(shippingId==null){
            return ServerResponse.serverResponseByError("地质参数不能为空");

        }
        //userId查询购物车中已选中商品-》List《Cart》
        List<Cart> cartList=cartMapper.findCartListByUserIdAndChecked(userId);

        //List<Cart>->List<OrderItem>
        ServerResponse serverResponse=getCartOrderItem(userId,cartList);
        if(!serverResponse.isSuccess()){
            return serverResponse;
        }
        //创建订单,保存到数据库
        BigDecimal orderTotalPrice=new BigDecimal(0);
        List<OrderItem> orderItemList=(List<OrderItem>)serverResponse.getData();
        if(orderItemList==null||orderItemList.size()==0){
            return ServerResponse.serverResponseByError("购物车为空");
        }
        orderTotalPrice=getOrderPrice(orderItemList);
        Order order=create(userId,shippingId,orderTotalPrice);
        if(order==null){
            ServerResponse.serverResponseByError("订单创建失败");
        }
        //List<OrderItem>保存到数据库
        for(OrderItem orderItem:orderItemList){
            orderItem.setOrderNo(order.getOrderNo());
        }
          //批量插入
        orderItemMapper.insertBatch(orderItemList);
        //扣库存
        reduceProductStock(orderItemList);
        //清空已下单的商品
        cleanCart(cartList);
        //返回OrderVO
        OrderVO orderVO=assembleOrderVO(order,orderItemList,shippingId);

        return ServerResponse.createServerResponseBySuccess(orderVO);
    }
    /**
     * 扣库存
     */
    private void reduceProductStock(List<OrderItem> orderItemList){
        if(orderItemList!=null&&orderItemList.size()>0){
            for(OrderItem orderItem:orderItemList){
                Integer productId=orderItem.getProductId();
                Integer quantity=orderItem.getQuantity();
                Product product=productMapper.selectByPrimaryKey(productId);
                product.setStock(product.getStock()-quantity);
                System.out.println(product.getStock());
                productMapper.updateByPrimaryKey(product);
            }

        }
    }

    private OrderVO assembleOrderVO(Order order,List<OrderItem> orderItemList,Integer shippingId){

        OrderVO orderVO=new OrderVO();
        List<OrderItemVO> orderItemVOList=Lists.newArrayList();
        for(OrderItem orderItem:orderItemList){
            OrderItemVO orderItemVO=assembleOrderItemVO(orderItem);
            orderItemVOList.add(orderItemVO);
        }
        orderVO.setOrderItemVOList(orderItemVOList);
        orderVO.setImageHost(PropertiesUtils.readByKey("imageHost"));
        Shipping shipping=shippingMapper.selectByPrimaryKey(shippingId);
        if(shipping!=null){
            orderVO.setShippingId(shippingId);
            ShippingVO shippingVO=assmbleShippingVO(shipping);
            orderVO.setShippingVo(shippingVO);
            orderVO.setReceiverName(shipping.getReceiverName());
        }
        orderVO.setStatus(order.getStatus());
        Const.OrderStatusEnum orderStatusEnum=Const.OrderStatusEnum.codeOf(order.getStatus());
        if(orderStatusEnum!=null){
            orderVO.setStatusDesc(orderStatusEnum.getDesc());
        }
        orderVO.setPostage(0);
        orderVO.setPayment(order.getPayment());
        orderVO.setPaymentType(order.getPaymentType());
        Const.PaymentEnum paymentEnum=Const.PaymentEnum.codeOf(order.getPaymentType());
        if(paymentEnum!=null){
            orderVO.setPaymentTypeDesc(paymentEnum.getDesc());
        }
        orderVO.setOrderNo(order.getOrderNo());


        return orderVO;
    }

    private ShippingVO assmbleShippingVO(Shipping shipping){
        ShippingVO shippingVO=new ShippingVO();
        if(shipping!=null){
            shippingVO.setReceiverAddress(shipping.getReceiverAddress());
            shippingVO.setReceiverCity(shipping.getReceiverCity());
            shippingVO.setReceiverDistrict(shipping.getReceiverDistrict());
            shippingVO.setReceiverMobile(shipping.getReceiverMobile());
            shippingVO.setReceiverName(shipping.getReceiverName());
            shippingVO.setReceiverPhone(shipping.getReceiverPhone());
            shippingVO.setReceiverProvince(shipping.getReceiverProvince());
            shippingVO.setReceiverZip(shipping.getReceiverZip());
        }
        return shippingVO;
    }
    private OrderItemVO assembleOrderItemVO(OrderItem orderItem){
        OrderItemVO orderItemVO=new OrderItemVO();
        if(orderItem!=null){
            orderItemVO.setQuantity(orderItem.getQuantity());
            orderItemVO.setCreateTime(DateUtils.dateToStr(orderItem.getCreateTime()));
            orderItemVO.setCurrentUnitPrice(orderItem.getCurrentUnitPrice());
            orderItemVO.setOrderNo(orderItem.getOrderNo());
            orderItemVO.setProductId(orderItem.getProductId());
            orderItemVO.setProductImage(orderItem.getProductImage());
            orderItemVO.setProductName(orderItem.getProductName());
            orderItemVO.setTotalPrice(orderItem.getTotalPrice());
        }
        return orderItemVO;
    }

    /**
     * 清空购物车中已选择商品
     */
    private void cleanCart(List<Cart> cartList){
        if(cartList!=null&&cartList.size()>0){
            cartMapper.batchDelete(cartList);
        }

    }


    /**
     * 计算订单总价格
     */
    private BigDecimal getOrderPrice(List<OrderItem> orderItemList){
        BigDecimal bigDecimal=new BigDecimal(0);

        for(OrderItem orderItem:orderItemList){
            bigDecimal=BigDecimalUtils.add(bigDecimal.doubleValue(),orderItem.getTotalPrice().doubleValue());

        }
        return bigDecimal;
    }

    /**
     * 创建订单
     */
    private Order create(Integer userId,Integer shippingId,BigDecimal orderTotalPrice){
        Order order=new Order();
        order.setOrderNo(generateOrderNO());
        order.setUserId(userId);
        order.setShippingId(shippingId);
        order.setStatus(Const.OrderStatusEnum.ORDER_UN_PAY.getCode());
        //订单金额
        order.setPayment(orderTotalPrice);
        order.setPostage(0);
        order.setPaymentType(Const.PaymentEnum.ONLINE.getCode());
        //保存订单
        int result=orderMapper.insert(order);
        if(result>0){
            return order;
        }
        return null;
    }

    /**
     * 生成订单编号
     */
    private Long generateOrderNO(){
        return System.currentTimeMillis()+ new Random().nextInt(100);
    }

    private ServerResponse getCartOrderItem(Integer userId,List<Cart> cartList){
        if(cartList==null||cartList.size()==0){
            return ServerResponse.serverResponseByError("购物车为空");
        }
        List<OrderItem> orderItemList= Lists.newArrayList();
        for(Cart cart:cartList){
            OrderItem orderItem=new OrderItem();
            orderItem.setUserId(userId);
            Product product=productMapper.selectByPrimaryKey(cart.getProductId());
            if(product==null){
                return ServerResponse.serverResponseByError("id为"+cart.getProductId()+"的商品不存在");

            }
            if(product.getStatus()!= Const.ProductStatusEnum.PRODUCT_ONLINE.getCode()){
                return ServerResponse.serverResponseByError("id为"+product.getId()+"的商品已经下架");
            }
            if(product.getStock()<cart.getQuantity()){
                return ServerResponse.serverResponseByError("id为"+product.getId()+"的商品库存不足");
            }
            orderItem.setQuantity(cart.getQuantity());
            orderItem.setCurrentUnitPrice(product.getPrice());
            orderItem.setProductId(product.getId());
            orderItem.setProductImage(product.getMainImage());
            orderItem.setProductName(product.getName());
            orderItem.setTotalPrice(BigDecimalUtils.mul(product.getPrice().doubleValue(),cart.getQuantity().doubleValue()));



            orderItemList.add(orderItem);
        }
        return ServerResponse.createServerResponseBySuccess(orderItemList);
    }

    /**
     * 取消订单
     */
    @Override
    public ServerResponse cancel(Integer userId, Long orderNo) {
        //参数校验
        if(orderNo==null){
            return ServerResponse.serverResponseByError("参数不能为空");
        }

        //根据userId和orderNo查询订单
        Order order=orderMapper.findOrderByUserIdAndOrderNo(userId, orderNo);
        if(order==null){
            return ServerResponse.serverResponseByError("订单不存在");
        }
        //判断订单状态并取消
        if(order.getStatus()!=Const.OrderStatusEnum.ORDER_UN_PAY.getCode()){
            return ServerResponse.serverResponseByError("订单不可取消");
        }
        //返回结果
        order.setStatus(Const.OrderStatusEnum.ORDER_CANCLE.getCode());
        int result=orderMapper.updateByPrimaryKey(order);
        if (result>0) {
            return ServerResponse.createServerResponseBySuccess();
        }
        return ServerResponse.serverResponseByError("订单取消失败");
    }

    @Override
    public ServerResponse get_order_cart_product(Integer userId) {
        //查询购物车
        List<Cart> cartList=cartMapper.findCartListByUserIdAndChecked(userId);
        //List<Cart>->List<OrderItem>
        ServerResponse serverResponse=getCartOrderItem(userId,cartList);
        if(!serverResponse.isSuccess()){
            return serverResponse;
        }
        //组装VO
        CartOrderItemVO cartOrderItemVO=new CartOrderItemVO();
        cartOrderItemVO.setImageHost(PropertiesUtils.readByKey("imageHost"));
        List<OrderItem> orderItemList=(List<OrderItem>) serverResponse.getData();
        List<OrderItemVO> orderItemVOList=Lists.newArrayList();
        if(orderItemList==null||orderItemList.size()==0){
            return ServerResponse.serverResponseByError("购物车为空");
        }
        for(OrderItem orderItem:orderItemList){
            orderItemVOList.add(assembleOrderItemVO(orderItem));
        }
        cartOrderItemVO.setOrderItemVOList(orderItemVOList);
        cartOrderItemVO.setTotalPrice(getOrderPrice(orderItemList));

        //返回结果
        return ServerResponse.createServerResponseBySuccess(cartOrderItemVO);
    }

    @Override
    public ServerResponse list(Integer userId,Integer pageNum,Integer pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<Order> orderList = Lists.newArrayList();
        if(userId==null){//查询所有
           orderList=orderMapper.selectAll();
        }else{//查询当前用户
            orderList=orderMapper.findOrderByUserId(userId);
        }
        if(orderList==null||orderList.size()==0){
            return ServerResponse.serverResponseByError("未查询到订单信息");
        }
        List<OrderVO> orderVOList=Lists.newArrayList();
        for(Order order:orderList){
            List<OrderItem> orderItemList=orderItemMapper.findOrderItemByOrderNo(order.getOrderNo());
            OrderVO orderVO=assembleOrderVO(order,orderItemList,order.getShippingId());
            orderVOList.add(orderVO);
        }
        PageInfo pageInfo=new PageInfo(orderVOList);
        return ServerResponse.createServerResponseBySuccess(pageInfo);
    }

    @Override
    public ServerResponse detail(Long orderNo) {
        //参数校验
        if(orderNo==null){
            return ServerResponse.serverResponseByError("订单参数不能为空");
        }
        //根据订单编号查询订单
        Order order=orderMapper.findOrderByOrderNo(orderNo);
        if(order==null){
            return ServerResponse.serverResponseByError("订单不存在");
        }
        //获取OrderVO并返回结果
        List<OrderItem> orderItemList=orderItemMapper.findOrderItemByOrderNo(orderNo);
        OrderVO orderVO=assembleOrderVO(order,orderItemList,order.getShippingId());
        return ServerResponse.createServerResponseBySuccess(orderVO);
    }
}
