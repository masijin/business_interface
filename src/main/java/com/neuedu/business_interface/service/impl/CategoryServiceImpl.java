package com.neuedu.business_interface.service.impl;


import com.google.common.collect.Sets;
import com.neuedu.business_interface.common.ServerResponse;
import com.neuedu.business_interface.dao.CategoryMapper;
import com.neuedu.business_interface.exception.MyException;
import com.neuedu.business_interface.pojo.Category;
import com.neuedu.business_interface.service.ICategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;
import java.util.Set;


@Service
public class CategoryServiceImpl implements ICategoryService {


    @Autowired
    CategoryMapper categoryMapper;
//    @Override
//    public int addCategory(Category category) throws MyException {
//        return categoryMapper.insert(category);
//    }
//
//    @Override
//    public int deleteCategory(int categoryId) throws MyException {
//        return categoryMapper.deleteByPrimaryKey(categoryId);
//    }
//
//    @Override
//    public int updateCategory(Category category) throws MyException {
//        return categoryMapper.updateByPrimaryKey(category);
//    }
//
//    @Override
//    public List<Category> findAll() throws MyException {
//        List<Category> categoryList=categoryMapper.selectAll();
//
//
//        return categoryList;
//    }
//    @Override
//    public Category findCategoryById(int categoryId) {
//
//
//        return categoryMapper.selectByPrimaryKey(categoryId);
//    }

    @Override
    public ServerResponse get_category(Integer categoryId) {

        //非空校验
        if(categoryId==null){
            return ServerResponse.serverResponseByError("参数不能为空");
        }

        //根据categoryId查询类别
        Category category=categoryMapper.selectByPrimaryKey(categoryId);
        if(category==null){
            return  ServerResponse.serverResponseByError("查询的类别不存在");
        }
        //查询子类别
        List<Category> categoryList=categoryMapper.findChildCategory(categoryId);
        //返回结果
        return ServerResponse.createServerResponseBySuccess(categoryList);
    }

    @Override
    public ServerResponse get_deep_category(Integer categoryId) {

        //参数的非空校验
        if(categoryId==null){
            return ServerResponse.serverResponseByError("类别id不能为空");
        }
        //查询
        Set<Category> categorySet= Sets.newHashSet();
        categorySet= findAllChildrenCategory(categorySet,categoryId);
        Set<Integer> integerSet=Sets.newHashSet();
        Iterator<Category> categoryIterator=categorySet.iterator();
        while (categoryIterator.hasNext()){
            Category category=categoryIterator.next();
            integerSet.add(category.getId());
        }
        return ServerResponse.createServerResponseBySuccess(integerSet);
    }

    private Set<Category> findAllChildrenCategory(Set<Category> categorySet,Integer catergoryId){

        Category category=categoryMapper.selectByPrimaryKey(catergoryId);
        if(category!=null){
            categorySet.add(category);
        }
        //查找categoryID下的子节点（平级）
        List<Category> categoryList=categoryMapper.findChildCategory(catergoryId);
        if(categoryList!=null&&categoryList.size()>0){
            for(Category category1:categoryList){
                findAllChildrenCategory(categorySet,category1.getId());
            }

        }
        return categorySet;
    }

}
