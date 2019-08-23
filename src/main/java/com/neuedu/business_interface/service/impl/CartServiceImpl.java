package com.neuedu.business_interface.service.impl;

import com.google.common.collect.Lists;
import com.neuedu.business_interface.common.Const;
import com.neuedu.business_interface.common.ServerResponse;
import com.neuedu.business_interface.dao.CartMapper;
import com.neuedu.business_interface.dao.ProductMapper;
import com.neuedu.business_interface.pojo.Cart;
import com.neuedu.business_interface.pojo.Product;
import com.neuedu.business_interface.pojo.UserInfo;
import com.neuedu.business_interface.service.ICartService;
import com.neuedu.business_interface.utils.BigDecimalUtils;
import com.neuedu.business_interface.vo.CartProductVo;
import com.neuedu.business_interface.vo.CartVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.List;

@Service
public class CartServiceImpl implements ICartService {
    @Autowired
    CartMapper cartMapper;
    @Autowired
    ProductMapper productMapper;
    @Override
    public ServerResponse add(Integer userId,Integer productId, Integer count) {
        //参数校验
        if(productId==null||count==null){
            return ServerResponse.serverResponseByError("参数不能为空");
        }
        Product product=productMapper.selectByPrimaryKey(productId);
        if(product==null){
            ServerResponse.serverResponseByError("要添加的商品不存在");
        }
        //查询购物信息
        Cart cart=cartMapper.SelectCartByUserIdAndProductId(userId,productId);
        if(cart==null){
            //添加
            Cart cart1=new Cart();
            cart1.setUserId(userId);
            cart1.setProductId(productId);
            cart1.setQuantity(count);
            cart1.setChecked(Const.CartCheckedEnum.PRODUCT_CHECKED.getCode());
            cartMapper.insert(cart1);
        }else {
            //更新
            Cart cart1=new Cart();
            cart1.setId(cart.getId());
            cart1.setProductId(productId);
            cart1.setUserId(userId);
            cart1.setQuantity(count);
            cart1.setChecked(cart.getChecked());
            cartMapper.updateByPrimaryKey(cart1);
        }
        CartVO cartVO=getCartVOLimit(userId);


        return ServerResponse.createServerResponseBySuccess(cartVO);
    }

    private CartVO getCartVOLimit(Integer userId){
        CartVO cartVO=new CartVO();
        //根据用户ID查询用户信息->List<Caart>
        List<Cart> cartList=cartMapper.selectCartByUserId(userId);
        //List<Cart>->list<CartProductVO>
        List<CartProductVo> cartProductVoList= Lists.newArrayList();
        BigDecimal carttotalprice=new BigDecimal("0");
        if(cartList!=null&&cartList.size()>0){
            for(Cart cart:cartList){
                CartProductVo cartProductVo=new CartProductVo();
                cartProductVo.setId(cart.getId());
                cartProductVo.setQuantity(cart.getQuantity());
                cartProductVo.setUserId(cart.getUserId());
                cartProductVo.setProductChecked(cart.getChecked());
                //查询商品
                Product product=productMapper.selectByPrimaryKey(cart.getProductId());
                if(product!=null){
                    cartProductVo.setProductId(cart.getProductId());
                    cartProductVo.setProductMainImage(product.getMainImage());
                    cartProductVo.setProductName(product.getName());
                    cartProductVo.setProductPrice(product.getPrice());
                    cartProductVo.setProductStatus(product.getStatus());
                    cartProductVo.setProductStock(product.getStock());
                    cartProductVo.setProductSubtitle(product.getSubtitle());

                    int stock=product.getStock();
                    int limitProductCount=0;
                    if(stock>=cart.getQuantity()){
                        limitProductCount=cart.getQuantity();
                        cartProductVo.setLimitQuantity("LIMIT_NUM_SUCCESS");
                    }else {
                        limitProductCount=stock;
                        //更新购物车中商品的数量
                        Cart cart1=new Cart();
                        cart1.setId(cart.getId());
                        cart1.setQuantity(stock);
                        cart1.setProductId(cart.getProductId());
                        cart1.setChecked(cart.getChecked());
                        cart1.setUserId(userId);
                        cartMapper.updateByPrimaryKey(cart1);
                        cartProductVo.setLimitQuantity("LIMIT_NUM_FAIL");
                    }
                    cartProductVo.setQuantity(limitProductCount);
                    cartProductVo.setProductTotalPrice(BigDecimalUtils.mul(product.getPrice().doubleValue(),Double.valueOf(cartProductVo.getQuantity())));


                }

                carttotalprice=BigDecimalUtils.add(carttotalprice.doubleValue(),cartProductVo.getProductTotalPrice().doubleValue());
                cartProductVoList.add(cartProductVo);

            }
        }
        //计算总价格
        cartVO.setCartProductVoList(cartProductVoList);
        cartVO.setCarttotalprice(carttotalprice);

        //判断购物车是否全选
       int count= cartMapper.isCheckedAll(userId);
       if(count>0){
           cartVO.setIsallchecked(false);
       }else {
           cartVO.setIsallchecked(true);
       }

        //返回结果
        return cartVO;
    }

    @Override
    public ServerResponse list(Integer userId) {
        CartVO cartVO=getCartVOLimit(userId);

        return ServerResponse.createServerResponseBySuccess(cartVO);
    }

    @Override
    public ServerResponse update(Integer userId, Integer productId, Integer count) {
        //参数判断
        if(productId==null||count==null){
            return ServerResponse.serverResponseByError("参数不能为空");
        }
        //查询购物车中的产品
        Cart cart=cartMapper.SelectCartByUserIdAndProductId(userId,productId);
        if(cart==null){
            //更新数量
            cart.setQuantity(count);
            cartMapper.updateByPrimaryKey(cart);
        }
        //返回cartVO
        return ServerResponse.createServerResponseBySuccess(getCartVOLimit(userId));
    }

    @Override
    public ServerResponse delete_product(Integer userId, String productIds) {

        //参数非空校验
        if(productIds==null||productIds.equals("")){
            return ServerResponse.serverResponseByError("参数不能为空");
        }
        //productIDS装成集合
        List<Integer> productIdList=Lists.newArrayList();
        String[] productIdsArr=productIds.split(",");
        if(productIdsArr!=null&&productIdsArr.length>0){
            for(String productIdstr:productIdsArr){
                Integer productId=Integer.parseInt(productIdstr);
                productIdList.add(productId);
            }
        }
        //调用dao

        cartMapper.deleteByUseridAndProductIds(userId,productIdList);

        //返回结果
        return ServerResponse.createServerResponseBySuccess(getCartVOLimit(userId));
    }

    @Override
    public ServerResponse select(Integer userId, Integer productId,Integer check) {
        //非空校验
        if(productId==null){
            return ServerResponse.serverResponseByError("参数不能为空");
        }
        //dao接口
        cartMapper.selectOrUnselectProduct(userId,productId,check);

        //返回结果
        return ServerResponse.createServerResponseBySuccess(getCartVOLimit(userId));
    }

    @Override
    public ServerResponse get_cart_product_count(Integer userId) {

        int count=cartMapper.get_cart_product_count(userId);
        return ServerResponse.createServerResponseBySuccess(count);
    }
}
