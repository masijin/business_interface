package com.neuedu.business_interface.service;


import com.neuedu.business_interface.common.ServerResponse;
import com.neuedu.business_interface.exception.MyException;
import com.neuedu.business_interface.pojo.Product;

import java.util.List;

public interface IProductService {
//    /***
//     * 添加商品
//     */
//    public int addProduct(Product product)throws MyException;
//
//
//    /***
//     * 删除商品
//     */
//    public int deleteProduct(int productId) throws MyException;
//
//    /***
//     * 修改商品
//     */
//    public int updateProduct(Product product)throws MyException;
//
//    /***
//     * 查询所有商品
//     */
//    public List<Product> findAllProduct()throws MyException;
//
//    /***
//     * 根据id查询商品
//     */
//    public Product findProductById(int productId)throws MyException;


     ServerResponse saveOrUpdate(Product product);

     ServerResponse set_sale_status(Integer productId,Integer status);

     ServerResponse detail(Integer productId);

     ServerResponse list (Integer pageNum,Integer pageSize);

     ServerResponse search(Integer productId,String  productName,Integer pageNum,Integer pageSize);
}
