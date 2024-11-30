package com.zuoxi.takeout.filter;


import com.alibaba.fastjson.JSON;
import com.zuoxi.takeout.common.BaseContext;
import com.zuoxi.takeout.common.R;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter
public class LoginCheckFilter implements Filter {
    // 路径过滤器
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        // a.获取本次请求的URI
        String requestUri = request.getRequestURI();

        // b.判断本次请求是否需要处理
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/user/login",
                "/user/sendMsg",
                "/doc.html",
                "/webjars/**",
                "/swagger-resources",
                "/v2/api-docs"
        };
        // c.如果不需要处理，则直接放行
        boolean isCheck = check(urls, requestUri);
        if (isCheck) {
            filterChain.doFilter(request, response);
            return;
        }

        // d.判断登录状态，如果已登录，则直接放行
        Object employee = request.getSession().getAttribute("employee");
        if (employee != null) {
            BaseContext.setCurrentUid((Long) employee);
            filterChain.doFilter(request, response);
            return;
        }
        // d2.判断移动端登录状态，如果已登录，则直接放行
        Object user = request.getSession().getAttribute("user");
        if (user != null) {
            BaseContext.setCurrentUid((Long) user);
            filterChain.doFilter(request, response);
            return;
        }
        // e.如果未登录则返回未登录结果，通过输出流方式向客户端页面响应数据
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
    }

    public boolean check(String[] urls, String uri) {
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, uri);
            if (match) {
                return true;
            }
        }
        return false;
    }
}
