package com.openvelog.openvelogbe.common.aop;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
@RequiredArgsConstructor
public class ControllerUseTime {
    private static final Logger log = LoggerFactory.getLogger(ControllerUseTime.class);

    @Around(value = "execution(public * com.openvelog.openvelogbe.*.controller..*(..))")
    public synchronized Object execute(ProceedingJoinPoint joinPoint) throws Throwable {
        // 측정 시작 시간
        long startTime = System.currentTimeMillis();

        try {
            // 핵심기능 수행
            Object output = joinPoint.proceed();
            return output;
        } finally {
            // 측정 종료 시간
            long endTime = System.currentTimeMillis();

            // 수행시간 = 종료 시간 - 시작 시간
            long runTime = endTime - startTime;

            // 사용된 controller class
            String controllerClassName = joinPoint.getTarget().getClass().getSimpleName();

            // 호출된 메소드
            String methodName = joinPoint.getSignature().getName();

            String requestUri = getRequestUrl();

            log.info("[API used time] Request URI: {}, Controller: {}, Method: {}, Total Time: {} ms",
                    requestUri, controllerClassName, methodName, runTime);
        }
    }

    private String getRequestUrl() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            HttpServletRequest request = requestAttributes.getRequest();
            return request.getRequestURI();
        }
        return null;
    }
}