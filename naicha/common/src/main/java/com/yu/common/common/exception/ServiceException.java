package com.yu.common.common.exception;

import lombok.Data;

@Data
public class ServiceException extends Exception {
    private int code = 400;

    public ServiceException(String msg) {
        super(msg);
    }

    public ServiceException(String msg, int code) {
        super(msg);
        this.code = code;
    }

    // ******************************************************* //
    // ************************ 前台 服务异常 ****************** //
    // ******************************************************* //

    //*********************** user ***********************//
    public final static ServiceException CONST_user_not_exist =
            new ServiceException("用户不存在");
    public final static ServiceException CONST_user_is_forbidden =
            new ServiceException("用户已被冻结");
    public final static ServiceException CONST_user_not_login =
            new ServiceException("用户未登录", 10001);
    public final static ServiceException CONST_user_login_expired =
            new ServiceException("用户登录已过期", 10001);
    public final static ServiceException CONST_token_is_not_validate =
            new ServiceException("token无效或已过期", 10001);
    public final static ServiceException CONST_wx_login_failed =
            new ServiceException("通过微信登录失败");

    //*********************** 订单 ***********************//
    public final static ServiceException CONST_create_order_fail =
            new ServiceException("下单失败");
    public final static ServiceException CONST_order_not_belong_you =
            new ServiceException("该订单不属于你，无权查看");
    public final static ServiceException CONST_can_not_refund =
            new ServiceException("无法退款");
    public final static ServiceException CONST_can_not_cancel_order =
            new ServiceException("无法取消该订单");
    public final static ServiceException CONST_repeat_create_order =
            new ServiceException("请勿重复或频繁提交");
    public final static ServiceException CONST_user_has_an_order_not_pay =
            new ServiceException("您还有一单未支付");
    public final static ServiceException CONST_order_not_has_goods =
            new ServiceException("不能生成空订单，请选择商品");
    public final static ServiceException CONST_order_price_error =
            new ServiceException("");
    public final static ServiceException CONST_receiver_info_params_invalid =
            new ServiceException("收货信息错误, 请检查收获地址、手机和姓名");
    public final static ServiceException CONST_confirm_receive_failed =
            new ServiceException("确认收货失败");
    public final static ServiceException CONST_current_order_status_can_not_change =
            new ServiceException("当前的订单状态不可变");
    public final static ServiceException CONST_shopkeeper_is_at_rest =
            new ServiceException("商家休息中，下单失败");
    public final static ServiceException CONST_it_is_not_business_hours =
            new ServiceException("未到营业时间，下单失败");
    public final static ServiceException CONST_order_not_exist =
            new ServiceException("订单不存在");

    //*********************** address ***********************//
    public final static ServiceException CONST_user_is_not_belong_this_address =
            new ServiceException("用户不属于该地址");
    public final static ServiceException CONST_address_not_exist =
            new ServiceException("地址不存在");
    public final static ServiceException CONST_user_address_has_over_num =
            new ServiceException("收获地址超过数量");


    //********************* token ***************************//
    public final static ServiceException CONST_token_is_not_belongs_to_current_user =
            new ServiceException("token不是当前用户的");


    //************************ other *************************//
    public final static ServiceException CONST_parameter_error =
            new ServiceException("参数错误");

    public final static ServiceException CONST_parameter_value_error =
            new ServiceException("参数值不合法");

    public final static ServiceException CONST_fail_to_get_app_config =
            new ServiceException("获取应用配置失败");

    public final static ServiceException CONST_test_login_has_closed =
            new ServiceException("测试登录已经关闭");

    //*********************** 微信 weixin ********************//
    public final static ServiceException CONST_wx_validate_failed =
            new ServiceException("微信验证失败");
    public final static ServiceException CONST_user_not_bind_wei_xin =
            new ServiceException("用户未绑定微信");
    public final static ServiceException CONST_weixin_pay_exception =
            new ServiceException("微信支付异常");


    // ******************************************************* //
    // ************************ 后台 服务异常 ****************** //
    // ******************************************************* //
    public final static ServiceException CONST_verify_code_error_or_expire =
            new ServiceException("验证码错误或已失效");

    public final static ServiceException CONST_not_authorized =
            new ServiceException("权限不足", 10002);

    public final static ServiceException CONST_login_failed =
            new ServiceException("账号不存在或密码错误");

    public final static ServiceException CONST_can_not_change_role_of_super_system_admin_user =
            new ServiceException("无法修改超级管理员的权限");
    
    public final static ServiceException CONST_image_upload_failed =
            new ServiceException("图片上传失败，超过大小或格式不正确");
    
    //************ 修改密码
    public final static ServiceException CONST_password_pattern_error =
            new ServiceException("密码格式不合法");
    public final static ServiceException CONST_new_password_can_not_equals_old_password =
            new ServiceException("新密码不能和旧密码一样");
    public final static ServiceException CONST_old_password_error =
            new ServiceException("旧密码错误");

    public final static ServiceException CONST_don_not_has_permission =
            new ServiceException("没有权限");


    // ************ 商品 **************** //
    public final static ServiceException CONST_goods_image_upload_failed =
            new ServiceException("商品图片上传失败");

    public final static ServiceException CONST_jia_liao_can_not_set_default_property =
            new ServiceException("加料区不能设置默认属性");


}
