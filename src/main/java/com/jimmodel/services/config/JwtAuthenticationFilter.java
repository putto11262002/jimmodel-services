package com.jimmodel.services.config;

import com.jimmodel.services.exception.JwtException;
import com.jimmodel.services.service.BlackListUserService;
import com.jimmodel.services.service.UserDetailsImp;
import com.jimmodel.services.service.UserService;
import com.jimmodel.services.util.SecurityUtil;
import io.jsonwebtoken.ExpiredJwtException;

import io.jsonwebtoken.SignatureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Value("${jwt.header-string}")
    public String HEADER_STRING;

    @Value("${jwt.token-prefix}")
    public String TOKEN_PREFIX;

    @Autowired
    private UserService userService;

    @Autowired
    private SecurityUtil securityUtil;

    @Autowired
    private BlackListUserService blackListUserService;

    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver resolver;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        String header = req.getHeader(HEADER_STRING);
        if (header == null) {
          chain.doFilter(req, res);
          return;
        }

        if (!header.startsWith(TOKEN_PREFIX)){
            chain.doFilter(req, res);
            return;
        }

        String authToken = header.replace(TOKEN_PREFIX, "").trim();
        try{
            String username = securityUtil.validateAccessToken(authToken);
            UserDetailsImp userDetails = (UserDetailsImp) userService.loadUserByUsername(username);
            if (userDetails == null){
                chain.doFilter(req,res);
                return;
            }

            if (blackListUserService.isBlackListed(userDetails.getId())){
                System.out.println("ttt");
                chain.doFilter(req,res);
                return;
            }



            UsernamePasswordAuthenticationToken authentication = securityUtil.getAuthenticationToken(authToken, SecurityContextHolder.getContext().getAuthentication(), userDetails);
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            chain.doFilter(req, res);
        }catch (IllegalArgumentException | SignatureException | ExpiredJwtException e){
            resolver.resolveException(req, res, null, new JwtException(e.getMessage()));
        }catch (Exception e){
            resolver.resolveException(req, res, null, e);
        }

    }
}