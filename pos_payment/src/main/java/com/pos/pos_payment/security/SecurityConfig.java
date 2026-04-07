package com.pos.pos_payment.security;

import com.pos.pos_payment.exception.security.CustomAccessDeniedHandler;
import com.pos.pos_payment.exception.security.CustomAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    private final SecurityFilter filter;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

                        // Process a sale – CASHIER, BRANCH_MANAGER, SUPER_ADMIN
                        .requestMatchers(HttpMethod.POST, "/payments")
                            .hasAnyRole("CASHIER", "BRANCH_MANAGER", "SUPER_ADMIN")

                        // Read payments/receipts – BRANCH_MANAGER, SUPER_ADMIN (+ CASHIER own)
                        .requestMatchers(HttpMethod.GET, "/payments/**")
                            .hasAnyRole("CASHIER", "BRANCH_MANAGER", "SUPER_ADMIN")
                        .requestMatchers(HttpMethod.GET, "/receipts/**")
                            .hasAnyRole("CASHIER", "BRANCH_MANAGER", "SUPER_ADMIN")

                        .anyRequest().authenticated()
                )

                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                .addFilterBefore(filter, BasicAuthenticationFilter.class)

                .build();
    }

    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of(
                "http://localhost:3000",
                "http://localhost:5173"
        ));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
