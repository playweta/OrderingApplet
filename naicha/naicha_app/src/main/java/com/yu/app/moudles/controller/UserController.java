package com.yu.app.moudles.controller;

import com.yu.app.moudles.mapper.UserMapper;
import com.yu.common.common.util.weixin.WeixinUtil;
import com.yu.common.entity.app.form.LoginByWeixinForm;
import com.yu.common.entity.app.form.UpdateUserForm;
import com.yu.app.moudles.service.UserServiceImpl;
import com.yu.common.common.annotation.NeedLogin;
import com.yu.common.common.exception.ServiceException;
import com.yu.common.common.util.result.Result;
import com.yu.common.entity.app.User;
import com.yu.app.common.util.session.SessionUtil;
import com.yu.common.service.AppConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Api(tags = "用户服务")
@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserServiceImpl userService;
    @Resource
    private AppConfigService appConfigService;
    @Resource
    private UserMapper userMapper;

    @ApiOperation("开发测试时开发的登录接口")
    @PostMapping("/login/dev")
    public Result<User> login(String secretPassword) throws Exception {
        if (!"123".equals(secretPassword))
            throw ServiceException.CONST_login_failed;
        if (!appConfigService.getAppConfig().getTestUserLoginEnabled())
            throw ServiceException.CONST_test_login_has_closed;

        // 测试开发环境
        User user = userService.getUser("oK3r84teJdc7oStGn_hpRc0B2FcA"); // 陈亚茹的微信的openid
        SessionUtil.setUserSession(user);
        log.info("[开发环境，用户登录] {}", user);
        return Result.ok(user);
    }

    @ApiOperation("通过微信登录")
    @PostMapping("/login/weixin")
    public Result<User> loginByWx(@RequestBody LoginByWeixinForm form) throws Exception {
        return Result.ok(userService.loginByWeixin(form));
    }

    @ApiOperation("通过openid登录") // 这里要在加个验证不然不安全
    @PostMapping("/login/openid/{openid}")
    public Result<User> loginByWx(@PathVariable String openid) throws Exception {
        if (openid == null || openid.length() != 28)
            return Result.fail("非法微信openid");
        User user = userMapper.selectById(openid);
        if (user == null) { // 没有就注册
            user = new User();
            user.setWxOpenid(openid);
            user.setStatus(true);
            userMapper.insert(user);
        }
        if (!user.getStatus()) { // 检查账号是否冻结
            throw ServiceException.CONST_user_is_forbidden;
        }

        // user.setWxAvatar(); // 微信头像
        SessionUtil.setUserSession(user);
        // userMapper.updateWxAvatar(user.getWxAvatar(), openid);
        return Result.ok(user);
    }

    @ApiOperation("退出登录")
    @NeedLogin
    @DeleteMapping("/logout")
    public Result logout(HttpServletRequest request) {
        SessionUtil.logout(request);
        return Result.ok();
    }

    @ApiOperation("根据token获取用户数据, 并续期token")
    @NeedLogin
    @GetMapping("/info")
    public Result<User> getUserInfo(HttpServletRequest request) throws ServiceException {
        User user = userService.getUser(SessionUtil.getCurrentWxOpenidFromRequest(request));
        SessionUtil.setUserSession(user); // token已经更新
        return Result.ok(user);
    }

    @ApiOperation("更新信息")
    @NeedLogin
    @PutMapping
    public Result<Integer> updateUser(@ApiParam("更新学生信息表单") @RequestBody UpdateUserForm form, HttpServletRequest request) throws Exception {
        return Result.ok(userService.updateUser(form, SessionUtil.getCurrentWxOpenidFromRequest(request)));
    }
}
