package com.baghaskara.kafka_redis_demo.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// @Component agar Spring mendeteksi class ini sebagai Bean dan bisa di-inject ke SecurityConfig
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    // Constructor Injection
    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        // 1. Ambil header Authorization
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        // 2. Validasi format header (Harus "Bearer <token>")
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // Kalau nggak ada atau format salah, lanjutkan ke filter berikutnya (tanpa
            // auth)
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Extract token (substring mulai dari index ke-7 untuk skip "Bearer ")
        jwt = authHeader.substring(7);

        try {
            // 4. Extract username (email) dari token
            userEmail = jwtService.extractUsername(jwt);

            // 5. Cek apakah user sudah terautentikasi di context saat ini
            // SecurityContextHolder itu ibarat "context" di Golang, tapi diikat ke Thread
            // (ThreadLocal)
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // Karena kita stateless, kita bikin UserDetails dummy berdasarkan info di
                // token.
                // Di production yang kompleks, lu mungkin load detail User dari DB/Redis pakai
                // email ini.
                UserDetails userDetails = User.builder()
                        .username(userEmail)
                        .password("") // Password tidak dibutuhkan lagi karena token udah valid
                        .authorities("ROLE_USER")
                        .build();

                // 6. Validasi signature dan expiration token
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null, // Credentials (password) udah nggak butuh
                            userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // 7. BAGIAN PALING KRUSIAL: Set Authentication ke Context!
                    // Ini yang bikin Spring Security tau kalau request ini "Authenticated" dan
                    // bukan "Anonymous".
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // Kalau token invalid/expired, biarkan SecurityContext kosong.
            // Nanti Spring Security yang akan handle dan return 403/401.
            System.out.println("JWT Validation failed: " + e.getMessage());
        }

        // 8. Lanjutkan request ke filter berikutnya atau ke Controller
        filterChain.doFilter(request, response);
    }
}