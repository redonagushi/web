

//Konfigurimi kryesor i Spring Security.
//
//Vendos rregullat:
//
//cilat URL janë “public” (p.sh. /api/auth/**, static files)
//
//cilat kërkojnë login (token)
//
//cilat kërkojnë ADMIN
//
//Zakonisht çaktivizon session (STATELESS), aktivizon CORS/CSRF sipas nevojës dhe fut JwtAuthFilter në chain.

package com.example.platform.config;

import com.example.platform.security.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.*;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.http.HttpMethod;
import java.util.List;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> {}) // ✅ SHTO KETE
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))


.authorizeHttpRequests(auth -> auth
                // ✅ lejo preflight
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // ✅ faqet statike
                .requestMatchers(
                        "/", "/index.html", "/register.html", "/profile.html", "/admin.html",
                        "/error", "/favicon.ico"
                ).permitAll()

                // ✅ asset-et (shumë e rëndësishme!)
                .requestMatchers("/css/**", "/js/**").permitAll()

                // ✅ uploads
                .requestMatchers("/uploads/**").permitAll()

                // ✅ auth endpoints
                .requestMatchers("/api/auth/**").permitAll()

                // ✅ admin endpoints
                .requestMatchers("/api/admin/**").hasRole("ADMIN")

                // ✅ çdo gjë tjetër kërkon login
                .anyRequest().authenticated()
        )

                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(
                "http://localhost:3000",
                "http://localhost:5173",
                "http://localhost:4200"
        ));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
