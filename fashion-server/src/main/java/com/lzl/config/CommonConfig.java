package com.lzl.config;

import com.lzl.properties.AliOssProperties;
import com.lzl.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class CommonConfig{

    /**
     * 声明AliOssUtil 的bean对象
     * @param aliOssProperties
     * @return
     */
    @Bean
    @ConditionalOnMissingBean  // 有这个bean的时候不注入，没有的时候才注入
    public AliOssUtil aliOssUtil(AliOssProperties aliOssProperties) {   // 使用bean注入的时候想用某个类，直接作为形参就可以使用
        return new AliOssUtil(
                aliOssProperties.getEndpoint(),
                aliOssProperties.getAccessKeyId(),
                aliOssProperties.getAccessKeySecret(),
                aliOssProperties.getBucketName()
        );
    }
}
