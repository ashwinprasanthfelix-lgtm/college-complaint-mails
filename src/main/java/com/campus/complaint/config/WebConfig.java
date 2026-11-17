package com.campus.complaint.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) throws Exception {
                HttpSession session = req.getSession(false);
                if (session == null || session.getAttribute("studentId") == null) {
                    res.sendRedirect(req.getContextPath() + "/login");
                    return false;
                }
                req.setAttribute("navName", session.getAttribute("studentName"));
                req.setAttribute("navRole", "student");
                return true;
            }
        }).addPathPatterns("/student/**");

        registry.addInterceptor(new HandlerInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) throws Exception {
                HttpSession session = req.getSession(false);
                if (session == null || session.getAttribute("adminId") == null) {
                    res.sendRedirect(req.getContextPath() + "/admin/login");
                    return false;
                }
                req.setAttribute("navName", session.getAttribute("adminName"));
                req.setAttribute("navRole", "admin");
                return true;
            }
        }).addPathPatterns("/admin/**").excludePathPatterns("/admin/login");
    }
}
