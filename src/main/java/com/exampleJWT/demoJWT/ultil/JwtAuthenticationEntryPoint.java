package com.exampleJWT.demoJWT.ultil;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
//Lớp này được triển khai từ interface AuthenticationEntryPoint,
// và nó được sử dụng để xử lý các yêu cầu khi người dùng cố gắng truy cập vào một tài nguyên
// yêu cầu xác thực nhưng không có token hợp lệ hoặc token bị lỗi.

    @Override
    public void commence(jakarta.servlet.http.HttpServletRequest request, jakarta.servlet.http.HttpServletResponse response, AuthenticationException authException) throws IOException, jakarta.servlet.ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("Invalid Token");
    }
}
