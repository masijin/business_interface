package com.neuedu.business_interface.common;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class GuavaCacheUtils {
    private static final LoadingCache<String,String> loadingCache= CacheBuilder
            .newBuilder()
            .initialCapacity(1000)//初始缓存项
            .maximumSize(10000)//最大缓存项
            .expireAfterAccess(10, TimeUnit.MINUTES)
            .build(new CacheLoader<String, String>(){
                @Override
                public String load(String s) throws Exception {
                    return "null";
                }
            });
    public static void put(String key,String value){
        loadingCache.put(key, value);
    }
    public static String get(String key){
        try {
            String value=loadingCache.get(key);
            if(!value.equals("null")){
                return value;
            }
            return null;
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;

    }

    public static void main(String[] args) {

    }
}
