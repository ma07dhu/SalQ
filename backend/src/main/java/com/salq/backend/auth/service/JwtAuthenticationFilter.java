// package com.salq.backend.auth.service;

// import com.salq.backend.config.JwtUtil;
// import jakarta.servlet.FilterChain;
// import jakarta.servlet.ServletException;
// import jakarta.servlet.http.HttpServletRequest;
// import jakarta.servlet.http.HttpServletResponse;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.lang.NonNull;
// import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// // import org.springframework.security.core.authority.SimpleGrantedAuthority;
// import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.security.core.userdetails.*;
// import org.springframework.stereotype.Component;
// import org.springframework.web.filter.OncePerRequestFilter;

// import java.io.IOException;
// // import java.util.Collections;
// // import java.util.Locale;

// @Component
// public class JwtAuthenticationFilter extends OncePerRequestFilter {

//     @Autowired private JwtUtil jwtUtil;
//     @Autowired private UserDetailsService userDetailsService;

//     @Override
//     protected boolean shouldNotFilter(HttpServletRequest request) {
//         String path = request.getServletPath();
//         return "/api/auth/login".equals(path); // don't process login itself
//     }

//     @Override
//     protected void doFilterInternal(@NonNull HttpServletRequest request,
//                                     @NonNull HttpServletResponse response,
//                                     @NonNull FilterChain filterChain)
//             throws ServletException, IOException {

//         String authHeader = request.getHeader("Authorization");
//         if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//             filterChain.doFilter(request, response);
//             return;
//         }

//         String jwt = authHeader.substring(7);
//         String username;
//         try {
//             username = jwtUtil.extractUsername(jwt);
//         } catch (Exception e) {
//             filterChain.doFilter(request, response); // invalid token → continue so entry points can handle
//             return;
//         }

//         if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//             UserDetails userDetails;
//             try {
//                 userDetails = userDetailsService.loadUserByUsername(username);
//             } catch (UsernameNotFoundException ex) {
//                 filterChain.doFilter(request, response);
//                 return;
//             }
//             if (jwtUtil.validateToken(jwt, userDetails.getUsername())) {
//                 UsernamePasswordAuthenticationToken authToken =
//                         new UsernamePasswordAuthenticationToken(
//                                 userDetails,
//                                 null,
//                                 userDetails.getAuthorities() // DB roles
//                         );
//                 SecurityContextHolder.getContext().setAuthentication(authToken);
//             }
//         }
//         System.out.println("JWT extracted: {}"+ jwt);
//         System.out.println("Username: {}"+ username);

//         filterChain.doFilter(request, response);
//     }
// }


package com.salq.backend.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.salq.backend.config.JwtUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService; // You need to implement this for your User

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        System.out.println("=== JWT Filter Debug ===");
        System.out.println("Request URI: " + request.getRequestURI());
        System.out.println("Auth Header: " + authHeader);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            System.out.println("Token extracted: " + token.substring(0, Math.min(20, token.length())) + "...");
            try {
                username = jwtUtil.extractUsername(token);
                System.out.println("Username extracted: " + username);
            } catch (Exception e) {
                System.out.println("JWT extraction failed: " + e.getMessage());
            }
        } else {
            System.out.println("No valid Authorization header found");
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                System.out.println("User loaded: " + userDetails.getUsername() + " with authorities: " + userDetails.getAuthorities());

                if (jwtUtil.validateToken(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    System.out.println("✅ User authenticated successfully");
                } else {
                    System.out.println("❌ Token validation failed");
                }
            } catch (Exception e) {
                System.out.println("❌ Authentication failed: " + e.getMessage());
            }
        } else if (username != null) {
            // User already authenticated - validate the token is still valid
            try {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                if (!jwtUtil.validateToken(token, userDetails)) {
                    System.out.println("❌ Existing token is invalid - clearing context");
                    SecurityContextHolder.clearContext();
                }
            } catch (Exception e) {
                System.out.println("❌ Token validation failed - clearing context: " + e.getMessage());
                SecurityContextHolder.clearContext();
            }
        } else {
            System.out.println("No username found - request will be anonymous");
        }

        System.out.println("=== End JWT Filter Debug ===");
        filterChain.doFilter(request, response);
    }
}

