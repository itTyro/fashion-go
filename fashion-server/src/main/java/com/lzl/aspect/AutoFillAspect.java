package com.lzl.aspect;

import com.lzl.annotation.AutoFill;
import com.lzl.constant.AutoFillConstant;
import com.lzl.context.BaseContext;
import com.lzl.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 公共字段填充类
 */
@Slf4j
@Component
@Aspect
public class AutoFillAspect {

    /**
     * 为公共字段填充
     */
    @Before("execution(* com.lzl.mapper.*.*(..)) && @annotation(autoFill)")
    public void autoFillProperty(JoinPoint joinPoint, AutoFill autoFill) throws Exception { // 除了环绕通知的其他通知都用JoinPoint来获取

        // 1. 获取原始方法运行时传入的参数，获取第一个对象
        Object[] args = joinPoint.getArgs();
        if (ObjectUtils.isEmpty(args)) {
            return;
        }
        Object obj = args[0];

        // 2. 通过反射获取到对象的四个方法
        Method setUpdateTime = obj.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class); // 第一个参数为获取的方法名，第二个为要传递的参数类型
        Method setUpdateUser = obj.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
        Method setCreateTime = obj.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
        Method setCreateUser = obj.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);

        // 3. 获取注解对应的value值
        OperationType value = autoFill.value();


        // 4. 为属性赋值
        if (value.equals(OperationType.INSERT)) {
            setCreateTime.invoke(obj, LocalDateTime.now());
            setCreateUser.invoke(obj, BaseContext.getCurrentId());
        }

        setUpdateTime.invoke(obj, LocalDateTime.now());
        setUpdateUser.invoke(obj, BaseContext.getCurrentId());


    }
}
