package com.neuedu.business_interface.common;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * 服务端响应对象
 */

@JsonSerialize(include=JsonSerialize.Inclusion.NON_EMPTY)
public class ServerResponse<T> {
    private  int status;//状态
    private String msg;//接口的返回信息
    private T data;//接口返回给前端的数据

    private ServerResponse(){}
    private ServerResponse(int status){
        this.status=status;
    }
    private ServerResponse(int status,String msg){
        this.status=status;
        this.msg=msg;
    }

    private ServerResponse(int status,T data){
        this.status=status;
        this.data=data;
    }

    private ServerResponse(int status,String msg,T data){
        this.data=data;
        this.msg=msg;
        this.status=status;
    }

    /**
     * 判断接口是否调用成功
     */

    @JsonIgnore
    public boolean isSuccess(){
        return this.status==ResponseCode.SUCCESS;
    }
    public static<T> ServerResponse createServerResponseBySuccess(){

        return new ServerResponse(ResponseCode.SUCCESS);
    }
    public static<T> ServerResponse createServerResponseBySuccess(T data){

        return new ServerResponse(ResponseCode.SUCCESS,data);
    }
    public static<T> ServerResponse createServerResponseBySuccess(String msg){

        return new ServerResponse(ResponseCode.SUCCESS,msg);
    }
    public static<T> ServerResponse createServerResponseBySuccess(String msg,T data){

        return new ServerResponse(ResponseCode.SUCCESS,msg,data);
    }
    public static<T> ServerResponse createServerResponseByFail(int status,String msg){

        return new ServerResponse(status, msg);
    }

    /**
     * 接口调用失败的回调
     */

    public static ServerResponse serverResponseByError(){

        return new ServerResponse(ResponseCode.Error);
    }
    public static ServerResponse serverResponseByError(int status){

        return new ServerResponse(status);
    }
    public static ServerResponse serverResponseByError(int status,String msg){

        return new ServerResponse(status,msg);
    }
    public static ServerResponse serverResponseByError(String msg){

        return new ServerResponse(ResponseCode.Error,msg);
    }









    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }


}
