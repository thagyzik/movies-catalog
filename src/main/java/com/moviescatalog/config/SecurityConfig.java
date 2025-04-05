package com.moviescatalog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   CustomAuthenticationEntryPoint authenticationEntryPoint,
                                                   CustomAccessDeniedHandler accessDeniedHandler,
                                                   JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/auth/**", "/users/registerUser").permitAll()
                .antMatchers("/users/updateRole/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.POST, "/movies/addMovie").hasRole("ADMIN")
                .antMatchers(HttpMethod.PUT, "/movies/updateMovie/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.DELETE, "/movies/deleteMovie/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.POST, "/movies/search").hasAnyRole("USER", "ADMIN")
                .antMatchers(HttpMethod.POST, "/movies/ratings").hasAnyRole( "USER", "ADMIN")
                .antMatchers(HttpMethod.DELETE, "/movies/ratings/**").hasAnyRole( "USER", "ADMIN")
                .antMatchers(HttpMethod.GET, "/movies/ratings/byUser").hasAnyRole( "USER", "ADMIN")
                .antMatchers(
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/api-docs/**"
                ).permitAll()
                .anyRequest().authenticated()
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler);

        return http.build();
    }

}
