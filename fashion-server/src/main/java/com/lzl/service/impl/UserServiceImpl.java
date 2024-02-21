package com.lzl.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lzl.dto.UserLoginDTO;
import com.lzl.entity.User;
import com.lzl.exception.LoginFailedException;
import com.lzl.mapper.UserMapper;
import com.lzl.properties.WeChatProperties;
import com.lzl.service.UserService;
import com.lzl.utils.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private static final String URL = "https://api.weixin.qq.com/sns/jscode2session";

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private WeChatProperties weChatProperties;

    @Override
    public User login(UserLoginDTO userLoginDTO) {
        // 1. 发送请求微信服务器获取openId
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("appid", weChatProperties.getAppid());
        paramMap.put("secret", weChatProperties.getSecret());
        paramMap.put("js_code", userLoginDTO.getCode());
        paramMap.put("grant_type", "authorization_code");
        String result = HttpClientUtil.doGet(URL, paramMap);
        log.info("微信登录：{}", result);
        if (!StringUtils.hasLength(result)) {
            log.info("调用接口返回的结果为null");
            throw new LoginFailedException("登陆失败");
        }

        // 获取openid, 将json转化为json对象
        JSONObject jsonObject = JSON.parseObject(result);
        String openid = jsonObject.getString("openid");
        if (!StringUtils.hasLength(openid)) {
            log.info("没有获取到openid");
            throw new LoginFailedException("登陆失败");
        }


        // 2. 查询用户是否存在
        User user = userMapper.selectByOpenid(openid);

        if (ObjectUtils.isEmpty(user)) {
            // 创建用户
             user = User.builder()
                    .openid(openid)
                    .createTime(LocalDateTime.now())
                    .build();
            userMapper.save(user);

        }


        // 3. 返回用户信息

        return user;
    }
}
