package com.neuedu.business_interface.service;


import com.neuedu.business_interface.common.ServerResponse;
import com.neuedu.business_interface.exception.MyException;
import com.neuedu.business_interface.pojo.Category;

import java.util.List;

public interface ICategoryService {
//    /**
//     * 添加类别
//     *
//     */
//     int addCategory(Category category)throws MyException;
//    /**
//     * 删除类别
//     *
//     */
//    int deleteCategory(int categoryId)throws MyException;
//    /**
//     * 修改类别
//     *
//     */
//    int updateCategory(Category category)throws MyException;
//    /**
//     * 查询类别
//     *
//     */
//    List<Category> findAll()throws MyException;
//    /**
//     * 根据类别ID查询类别信息
//     */
//    Category findCategoryById(int categoryId);
    /**
     * 根据类别ID查询子类
     */
    ServerResponse get_category(Integer categoryId);

    /**
     * 获取当前分类id及递归子节点categoryId
     */
    ServerResponse get_deep_category(Integer categoryId);
}
