package com.cqupt.art.interceptor;

import com.cqupt.art.entity.to.UserTo;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Component
public class LoginUserInterceptor implements HandlerInterceptor {
    public static ThreadLocal<UserTo> loginUser = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();
//        boolean match = new AntPathMatcher().match("/member/**", uri);
//        if(match){
//            return true;
//        }
        HttpSession session = request.getSession();
        UserTo user = (UserTo) session.getAttribute("user");
        if (user != null) {
            //登录
            loginUser.set(user);
            return true;
        } else {
            //去登录
//            HttpSession session1 = request.getSession();
//            session1.setAttribute("msg","请先进行登录");
//            response.sendRedirect("http://auth.gulimall.com/login.html");
            return false;
        }
    }
}
