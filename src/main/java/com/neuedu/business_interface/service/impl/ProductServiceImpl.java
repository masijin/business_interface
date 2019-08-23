package com.neuedu.business_interface.service.impl;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.neuedu.business_interface.common.ServerResponse;
import com.neuedu.business_interface.dao.CategoryMapper;
import com.neuedu.business_interface.dao.ProductMapper;
import com.neuedu.business_interface.exception.MyException;
import com.neuedu.business_interface.pojo.Category;
import com.neuedu.business_interface.pojo.Product;
import com.neuedu.business_interface.service.IProductService;
import com.neuedu.business_interface.utils.DateUtils;
import com.neuedu.business_interface.utils.PropertiesUtils;
import com.neuedu.business_interface.vo.ProductDetailVO;
import com.neuedu.business_interface.vo.ProductListVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements IProductService {

    @Autowired
    ProductMapper productMapper;
    @Autowired
    CategoryMapper categoryMapper;

//    @Override
//    public int addProduct(Product product) throws MyException {
//        return productMapper.insert(product);
//    }
//
//    @Override
//    public int deleteProduct(int productId) throws MyException {
//        return productMapper.deleteByPrimaryKey(productId);
//    }
//
//    @Override
//    public int updateProduct(Product product) throws MyException {
//        return productMapper.updateByPrimaryKey(product);
//    }
//
//    @Override
//    public List<Product> findAllProduct() throws MyException {
//        List<Product> productList=productMapper.selectAll();
//        return productList;
//    }
//
//    @Override
//    public Product findProductById(int productId) throws MyException {
//        return productMapper.selectByPrimaryKey(productId);
//    }


    @Override
    public ServerResponse saveOrUpdate(Product product) {
        //参数非空校验
        if(product==null){
            return ServerResponse.serverResponseByError("参数为空");
        }

        //设置商品主图sub_image-->1.jpg,2.jpg,3.jpg
        String subImages=product.getSubImages();
        if(subImages!=null&&!subImages.equals("")){
            String[] subImageArr=subImages.split(",");
            if(subImageArr.length>0){
                product.setMainImage(subImageArr[0]);
            }

        }
        //商品save or update
        if(product.getId()==null){
            //添加
            int result=productMapper.insert(product);
            if(result>0){
                return ServerResponse.createServerResponseBySuccess();
            }else {
                return ServerResponse.serverResponseByError("添加失败");
            }
        }
        else {
            //更新
            int result=productMapper.updateByPrimaryKey(product);
            if(result>0){
                return ServerResponse.createServerResponseBySuccess();
            }else {
                return ServerResponse.serverResponseByError("更新失败");
            }
        }

    }

    @Override
    public ServerResponse set_sale_status(Integer productId,Integer status) {
        //参数非空校验
        if(productId==null){
            return ServerResponse.serverResponseByError("商品id不能为空");
        }
        if(productId==null){
            return ServerResponse.serverResponseByError("商品参数不能为空");
        }
        //更新商品状态
        Product product=new Product();
        product.setId(productId);
        product.setStatus(status);
        int result=productMapper.updateByPrimaryKey(product);

        //商品结果
        if(result>0){
            return ServerResponse.createServerResponseBySuccess();
        }
        return ServerResponse.serverResponseByError("更新商品状态失败");
    }

    @Override
    public ServerResponse detail(Integer productId) {
        //参数非空校验
        if(productId==null){
            return ServerResponse.serverResponseByError("商品id不能为空");
        }
        //查询商品

        Product product=productMapper.selectByPrimaryKey(productId);

        if(product==null){
            return ServerResponse.serverResponseByError("商品用户不存在");
        }
        //product-->productDetailVO
        ProductDetailVO productDetailVO=assembleProductDetailVO(product);
        //结果返回
        return ServerResponse.createServerResponseBySuccess(productDetailVO);
    }

    private ProductDetailVO assembleProductDetailVO(Product product){

        ProductDetailVO productDetailVO=new ProductDetailVO();
        productDetailVO.setCategoryId(product.getCategoryId());
        productDetailVO.setCreateTime(DateUtils.dateToStr(product.getCreateTime()));
        productDetailVO.setDetail(product.getDetail());
        productDetailVO.setImageHost(PropertiesUtils.readByKey("imageHost"));
        productDetailVO.setName(product.getName());
        productDetailVO.setMainImage(product.getMainImage());
        productDetailVO.setId(product.getId());
        productDetailVO.setPrice(product.getPrice());
        productDetailVO.setStatus(product.getStatus());
        productDetailVO.setStock(product.getStock());
        productDetailVO.setSubtitle(product.getSubtitle());
        productDetailVO.setSubImages(product.getSubImages());
        productDetailVO.setUpdateTime(DateUtils.dateToStr(product.getUpdateTime()));
        Category category=categoryMapper.selectByPrimaryKey(product.getCategoryId());
        if(category!=null){
            productDetailVO.setParentCategoryId(category.getParentId());
        }else {
            productDetailVO.setParentCategoryId(0);
        }


        return productDetailVO;
    }

    @Override
    public ServerResponse list(Integer pageNum, Integer pageSize) {

        PageHelper.startPage(pageNum,pageSize);
        //查询商品数据 select * from product limit(pagrNum-1)*pageSize.pageSize
        List<Product>productList= productMapper.selectAll();
        List<ProductListVO> productListVOList= Lists.newArrayList();
        if(productList!=null&&productList.size()>0){
            for(Product product:productList){
                ProductListVO productListVO=assmbleProductListVO(product);
                productListVOList.add(productListVO);

            }
        }
        PageInfo pageInfo=new PageInfo((productListVOList));

        return ServerResponse.createServerResponseBySuccess(pageInfo);
    }
    private ProductListVO assmbleProductListVO(Product product){
        ProductListVO productListVO=new ProductListVO();
        productListVO.setId(product.getId());
        productListVO.setCategoryId(product.getCategoryId());
        productListVO.setMainImage(product.getMainImage());
        productListVO.setName(product.getName());
        productListVO.setPrice(product.getPrice());
        productListVO.setStatus(product.getStatus());
        productListVO.setSubtitle(product.getSubtitle());
        return productListVO;
    }

    @Override
    public ServerResponse search(Integer productId, String  productName,
                                 Integer pageNum, Integer pageSize) {
        //select * from product where productId ? and productName like %name%
        PageHelper.startPage(pageNum,pageSize);
        if(productName!=null&&productName.equals("")){
            productName="%"+productName+"%";
        }
        List<Product> productList=productMapper.findProductByProductIdAndProductName(productId,productName);

        List<ProductListVO> productListVOList= Lists.newArrayList();
        if(productList!=null&&productList.size()>0){
            for(Product product:productList){
                ProductListVO productListVO=assmbleProductListVO(product);
                productListVOList.add(productListVO);

            }
        }
        PageInfo pageInfo=new PageInfo(productListVOList);

        return ServerResponse.createServerResponseBySuccess(pageInfo);
    }
}
