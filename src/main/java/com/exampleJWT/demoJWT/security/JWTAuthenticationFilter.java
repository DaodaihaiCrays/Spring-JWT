package com.exampleJWT.demoJWT.security;

import com.exampleJWT.demoJWT.service.CustomUserDetailsService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter {
    private Logger logger = LoggerFactory.getLogger(OncePerRequestFilter.class);
    @Autowired
    private JWTHelper jwtHelper;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        // Authorization=Bearer <token>
//
//        String requestHeader = request.getHeader("Authorization");
//        logger.info(" Header :  {}", requestHeader);
//        String username = null;
//        String token = null;
//
//        if (requestHeader != null && requestHeader.startsWith("Bearer")) {
//            //looking good
//            token = requestHeader.substring(7);
//            try {
//                username = this.jwtHelper.getUsernameFromToken(token);
//            } catch (IllegalArgumentException e) {
//                logger.info("Illegal Argument while fetching the username !!");
//                e.printStackTrace();
//            } catch (ExpiredJwtException e) {
//                logger.info("Given jwt token is expired !!");
//                e.printStackTrace();
//            } catch (MalformedJwtException e) {
//                logger.info("Some changed has done in token !! Invalid Token");
//                e.printStackTrace();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        } else {
//            logger.info("Invalid Header Value !! ");
//        }
//
//        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//            UserDetails userDetails = this.customUserDetailsService.loadUserByUsername(username);
//
//            // Lấy userId từ token
//            Claims claims = jwtHelper.getAllClaimsFromToken(token);
//            Long userId = claims.get("userId", Long.class);  // Lấy ID người dùng từ token
//
//            Boolean validateToken = this.jwtHelper.validateToken(token, userDetails);
//            if (validateToken) {
//                // Sử dụng ID người dùng nếu cần
//                UsernamePasswordAuthenticationToken authentication =
//                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                SecurityContextHolder.getContext().setAuthentication(authentication);
//            }
//        }
//
//        filterChain.doFilter(request, response);
//    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Authorization=Bearer <token>
        String requestHeader = request.getHeader("Authorization");
        logger.info(" Header :  {}", requestHeader);
        String username = null;
        String token = null;

        if (requestHeader != null && requestHeader.startsWith("Bearer")) {
            // Token starts with Bearer
            token = requestHeader.substring(7);
            try {
                username = this.jwtHelper.getUsernameFromToken(token);
            } catch (IllegalArgumentException e) {
                logger.info("Illegal Argument while fetching the username !!");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);  // 400 Bad Request
                response.getWriter().write("Invalid token format or structure.");
                return;  // Stop processing the request
            } catch (ExpiredJwtException e) {
                logger.info("Access token has expired.");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);  // 401 Unauthorized
                response.getWriter().write("Access token is expired.");
                return;
            } catch (MalformedJwtException e) {
                logger.info("Malformed JWT token.");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);  // 400 Bad Request
                response.getWriter().write("Invalid JWT token.");
                return;
            } catch (Exception e) {
                logger.error("An error occurred while processing the token.", e);
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);  // 500 Internal Server Error
                response.getWriter().write("An internal error occurred while processing the token.");
                return;
            }
        } else {
            logger.info("Invalid Header Value !! ");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);  // 400 Bad Request
            response.getWriter().write("Authorization header must start with 'Bearer'.");
            return;
        }

        // Xác thực người dùng và thiết lập trong SecurityContext nếu username hợp lệ
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.customUserDetailsService.loadUserByUsername(username);
            Boolean validateToken = this.jwtHelper.validateToken(token, userDetails);
            if (validateToken) {
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                logger.info("Invalid token. Authentication failed.");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);  // 401 Unauthorized
                response.getWriter().write("Invalid or expired token.");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

}