package com.jjbin.mysite.api.config.interceptor;

import com.jjbin.mysite.api.SessionConst;
import com.jjbin.mysite.api.exception.Unauthorized;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * TODO 인터셉터와 아규먼트리졸버와 기능 중복 -> 리팩토링 할 것
 */
@Slf4j
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String requestURI = request.getRequestURI();

        log.info("인증 요청 체크 : {}",requestURI);
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute(SessionConst.LOGIN_MEMBER) == null) {
            throw new Unauthorized();
        }

        return true;
    }

}
