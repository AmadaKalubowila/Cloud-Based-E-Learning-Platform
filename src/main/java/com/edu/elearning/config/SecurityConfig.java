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
                                "/user/create",
                                "/user/login",
                                "/user/resetPassword/**",
                                "/user/password",
                                "/user/getById",
                                "/module/getAll",
                                "/module/getById",
                                "/video/getAll",
                                "/video/getById",
                                "/course/getAll",
                                "/course/getById"

                        ).permitAll()

                        // Authenticated users
                        .requestMatchers(
                                "/user/logout",
                                "/user/update",
                                "/user/refresh"
                        ).authenticated()

                        //ADMIN only
                        .requestMatchers(
                                "/user/delete/**",
                                "/user/unlock-user/**",
                                "/user/getAll",
                                "/module/create",
                                "/module/update",
                                "/course/create",
                                "/course/update"
                        ).hasRole("ADMIN")

                        // SUPER_ADMIN only
                        .requestMatchers(
                                "/video/create/",
                                "/video/update"
                        ).hasRole("LECTURER")

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
