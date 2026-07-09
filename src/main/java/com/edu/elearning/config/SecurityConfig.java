package com.edu.elearning.config;


import com.edu.elearning.utility.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    @Order(2)
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth

                        // Public — no token needed
                        .requestMatchers(
                                "/user/create"

                        ).permitAll()

                        // Authenticated users
                        .requestMatchers(
                                "/user/logout",
                                "/user/refresh"
                        ).authenticated()

                        //ADMIN only
                        .requestMatchers(
                                "/user/delete/**"
                        ).hasRole("ADMIN")

                        // SUPER_ADMIN only
                        .requestMatchers(
                                "/admin/session/create",
                                "/admin/session/update",
                                "/admin/session/delete",
                                "/admin/session/bulk-create",
                                "/admin/session-mappings/**",
                                "/admin/pricing/**",
                                "/admin/closures/**",
                                "/admin/stats/**",
                                "/admin/bookings/**"
                        ).hasRole("SUPER_ADMIN")

                        // ADMIN — read-only on sessions
                        .requestMatchers("/admin/session/getAll").hasAnyRole("ADMIN", "SUPER_ADMIN")

                        .anyRequest().authenticated()
                )

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
