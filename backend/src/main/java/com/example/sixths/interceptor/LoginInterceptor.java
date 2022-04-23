package com.example.sixths.interceptor;

import com.example.sixths.utils.JWTUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    public static final String ID_KEY = "key_id";
    public static final String NAME_KEY = "key_name";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            // TODO?
            return true;
        }

        String token = request.getHeader("token");
        String openid = request.getParameter("openid");

        if (openid == null || token == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setHeader("content-type", "text/html;charset=UTF-8");
            PrintWriter out = response.getWriter();
            out.write("do not have one openid or token");
            out.flush();
            out.close();
            return false;
        }
        if (!JWTUtils.verifyUserToken(token, openid)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setHeader("content-type", "text/html;charset=UTF-8");
            PrintWriter out = response.getWriter();
            out.write("invalid token");
            out.flush();
            out.close();
            return false;
        }
        Integer id = JWTUtils.getClaim(token, "id").asInt();
        String name = JWTUtils.getClaim(token, "name").asString();
        request.setAttribute(NAME_KEY, name);
        request.setAttribute(ID_KEY, id);
        return true;
    }

}
