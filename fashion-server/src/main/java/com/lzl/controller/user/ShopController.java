package com.lzl.controller.user;

import com.lzl.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController("userShopController")
@RequestMapping("/user/shop")
@Api(tags = "店铺状态")
public class ShopController {

    private final String KEY = "SHOP:STATUS";

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;


    @ApiOperation("获取店铺状态")
    @GetMapping("/status")
    public Result<Integer> getStatus() {
        Integer status = (Integer) redisTemplate.opsForValue().get(KEY);
        log.info("店铺的营业状态为：{}",  status == 1 ? "营业中" : "打样中");
        return Result.success(status);
    }
}
