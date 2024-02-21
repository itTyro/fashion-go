package com.lzl.interceptor;

import com.lzl.constant.JwtClaimsConstant;
import com.lzl.context.BaseContext;
import com.lzl.properties.JwtProperties;
import com.lzl.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@Slf4j
public class JwtTokenUserInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtProperties jwtProperties;



    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("拦截的路径是：{}", request.getRequestURL().toString());
        //判断当前拦截到的是Controller的方法还是其他资源
        if (!(handler instanceof HandlerMethod)) {
            //当前拦截到的不是动态方法，直接放行
            log.info("当前拦截到的不是动态方法，直接放行");
            return true;
        }

        String jwt = request.getHeader(jwtProperties.getUserTokenName());
        if (!StringUtils.hasLength(jwt)) {
            // 令牌为空，响应401
            log.info("令牌为空，响应401");
            response.setStatus(HttpStatus.SC_UNAUTHORIZED);
            return false;
        }

        try {
            log.info("jwt令牌校验中：{}", jwt);
            Claims claims = JwtUtil.parseJWT(jwtProperties.getUserSecretKey(), jwt);
            Long userId = Long.valueOf(claims.get(JwtClaimsConstant.USER_ID).toString());
            log.info("当前操作用户id：{}", userId);
            BaseContext.setCurrentId(userId);
            return true;
        } catch (Exception e) {
            // jwt检验失败，返回401
            log.info("令牌检验失败，响应401");
            response.setStatus(HttpStatus.SC_UNAUTHORIZED);
            return false;
        }

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        BaseContext.removeCurrentId();
    }
}
