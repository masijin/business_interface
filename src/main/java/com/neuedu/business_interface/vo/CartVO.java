package com.neuedu.business_interface.vo;

import java.math.BigDecimal;
import java.util.List;

/**
 * 购物车实体类
 */
public class CartVO {
    /**
     * 购物车信息集合CartVO
     */
    private List<CartProductVo> cartProductVoList;
    /**
     * 是否全选
     */
    private boolean isallchecked;
    /**
     * 总价格
     */
    private BigDecimal carttotalprice;

    public List<CartProductVo> getCartProductVoList() {
        return cartProductVoList;
    }

    public void setCartProductVoList(List<CartProductVo> cartProductVoList) {
        this.cartProductVoList = cartProductVoList;
    }

    public boolean isIsallchecked() {
        return isallchecked;
    }

    public void setIsallchecked(boolean isallchecked) {
        this.isallchecked = isallchecked;
    }

    public BigDecimal getCarttotalprice() {
        return carttotalprice;
    }

    public void setCarttotalprice(BigDecimal carttotalprice) {
        this.carttotalprice = carttotalprice;
    }
}
