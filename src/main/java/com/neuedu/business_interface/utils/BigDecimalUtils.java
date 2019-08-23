package com.neuedu.business_interface.utils;

import java.math.BigDecimal;

/**
 * 价格计算工具类
 */
public class BigDecimalUtils {
    /**
     * 加法
     */
    public static BigDecimal add(double d1,double d2){
        BigDecimal bigDecimal1=new BigDecimal(String.valueOf(d1));
        BigDecimal bigDecimal2=new BigDecimal(String.valueOf(d2));
        return bigDecimal1.add(bigDecimal2);
    }
    /**
     * 减法
     */
    public static BigDecimal sub(double d1,double d2){
        BigDecimal bigDecimal1=new BigDecimal(String.valueOf(d1));
        BigDecimal bigDecimal2=new BigDecimal(String.valueOf(d2));
        return bigDecimal1.subtract(bigDecimal2);
    }
    /**
     * 乘法
     */
    public static BigDecimal mul(double d1,double d2){
        BigDecimal bigDecimal1=new BigDecimal(String.valueOf(d1));
        BigDecimal bigDecimal2=new BigDecimal(String.valueOf(d2));
        return bigDecimal1.multiply(bigDecimal2);
    }
    /**
     * 除法,保留两位小数，四舍五入
     */
    public static BigDecimal div(double d1,double d2){
        BigDecimal bigDecimal1=new BigDecimal(String.valueOf(d1));
        BigDecimal bigDecimal2=new BigDecimal(String.valueOf(d2));
        return bigDecimal1.divide(bigDecimal2,2,BigDecimal.ROUND_HALF_UP);
    }

    public static void main(String[] args) {

    }

}
