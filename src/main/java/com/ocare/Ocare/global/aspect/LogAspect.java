package com.ocare.Ocare.global.aspect;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;

@Slf4j
@Aspect
@Component
public class LogAspect {

    // 1. 모든 Web Adapter(컨트롤러)의 메서드를 타겟으로 잡음
    @Pointcut("execution(* com.ocare.Ocare.adapter.in.web..*Controller.*(..))")
    public void controllerPointcut() {
    }

    // 2. 메서드 실행 전후로 로그 기록
    @Around("controllerPointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getSignature().getDeclaringTypeName();
        Object[] args = joinPoint.getArgs();

        log.info("[Request] URL: {}, Method: {}, Controller: {}.{}, Args: {}",
                request.getRequestURI(), request.getMethod(), className, methodName, Arrays.toString(args));

        long start = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed(); // 실제 메서드 실행
            long executionTime = System.currentTimeMillis() - start;
            log.info("[Response] Execution Time: {}ms, Result: {}", executionTime, result);
            return result;
        } catch (Exception e) {
            log.error("[Error] Method: {}.{}, Message: {}", className, methodName, e.getMessage());
            throw e;
        }
    }
}
