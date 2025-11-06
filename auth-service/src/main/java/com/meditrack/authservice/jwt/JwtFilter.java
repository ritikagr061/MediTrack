//package com.meditrack.authservice.jwt;
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.ApplicationContext;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//
//@Component
//public class JwtFilter extends OncePerRequestFilter {
//    @Autowired
//    private JwtService jwtService;
//    @Autowired
//    private ApplicationContext context;
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        String path = request.getServletPath();
//        if (path.equals("/auth/login") || path.equals("/auth/register-user")) {
//            filterChain.doFilter(request, response);
//            return;
//        }
//
//        String authHeader = request.getHeader("Authorization");
//        String jwtToken = null;
//        String userName = null;
//
//        //extracts userName from jwtToken
//        if(authHeader!=null && authHeader.startsWith("Bearer ")){
//            jwtToken = authHeader.substring(7);
//            userName = jwtService.extractUserName(jwtToken);
//        }
//
//        //checks if username valid plus checking only if the user isn't already authenticated for this request(might happen that its authenticated in some other filter or at OAuth2)
//        if(userName!=null && SecurityContextHolder.getContext().getAuthentication()==null){
//            //will fetch userDetail from the DB
//            UserDetails userDetail =  context.getBean(MyUserDetailService.class).loadUserByUsername(userName);
//            //while validating we just check if token is expired and does such user exists in db or not but where is signature validation?? todo
//            if(jwtService.validateToken(jwtToken,userDetail)){
//                UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userDetail,null,userDetail.getAuthorities());
//                token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                SecurityContextHolder.getContext().setAuthentication(token);
//            }
//        }
//
//        filterChain.doFilter(request,response);
//    }
//}
//
