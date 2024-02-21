package com.lzl.controller.user;

import com.lzl.constant.JwtClaimsConstant;
import com.lzl.dto.UserLoginDTO;
import com.lzl.entity.User;
import com.lzl.properties.JwtProperties;
import com.lzl.result.Result;
import com.lzl.service.UserService;
import com.lzl.utils.JwtUtil;
import com.lzl.vo.UserLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Api(tags = "用户管理")
@RestController
@RequestMapping("/user/user")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private JwtProperties jwtProperties;

    @ApiOperation("用户登录")
    @PostMapping("/login")
    public Result<UserLoginVO> login(@RequestBody UserLoginDTO userLoginDTO) {
        log.info("用户登录：{}", userLoginDTO);
        User user = userService.login(userLoginDTO);

        // 创建jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID, user.getId());
        String jwt = JwtUtil.createJWT(jwtProperties.getUserSecretKey(), jwtProperties.getUserTtl(), claims);

        UserLoginVO userLoginVO = UserLoginVO.builder()
                .token(jwt)
                .id(user.getId())
                .openid(user.getOpenid())
                .build();

        return Result.success(userLoginVO);
    }

    @ApiOperation("用户退出")
    @PostMapping("/logout")
    public Result<UserLoginVO> logout() {
        log.info("用户退出");

        return Result.success();
    }

}
