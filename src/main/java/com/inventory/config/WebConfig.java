package com.inventory.config;

import com.inventory.entity.SysUser;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// 关键点：Spring Boot 3 必须使用 jakarta.servlet
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptor() {
                    @Override
                    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
                        HttpSession session = request.getSession();
                        SysUser user = (SysUser) session.getAttribute("currentUser");

                        // 如果 Session 里没用户，就跳转到登录页
                        if (user == null) {
                            response.sendRedirect("/login");
                            return false;
                        }
                        return true;
                    }
                })
                .addPathPatterns("/**") // 拦截所有路径
                .excludePathPatterns("/login", "/sysUser/login", "/css/**", "/js/**", "/images/**", "/error"); // 放行登录相关和静态资源
    }
}