package com.zicca.zcoupon.merchant.admin.common.aspect;

import com.zicca.zcoupon.merchant.admin.service.basics.chain.MerchantAdminChainContext;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
@RequiredArgsConstructor
public class ChainHandlerValidationAspect {

    private final MerchantAdminChainContext merchantAdminChainContext;

    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        ChainHandler chainHandler = getChainHandlerAnnotation(joinPoint);
        merchantAdminChainContext.handle(chainHandler.mark(), joinPoint.getArgs()[0]);
        return joinPoint.proceed();

    }

    /**
     * 获取当前方法上的注解
     */
    public static ChainHandler getChainHandlerAnnotation(ProceedingJoinPoint joinPoint) throws NoSuchMethodException {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = joinPoint.getTarget().getClass().getDeclaredMethod(methodSignature.getName(), methodSignature.getMethod().getParameterTypes());
        return method.getAnnotation(ChainHandler.class);
    }


}
