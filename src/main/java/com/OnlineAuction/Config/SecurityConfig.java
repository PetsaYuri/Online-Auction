package com.OnlineAuction.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.ALWAYS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/signup").permitAll()
                        .requestMatchers("/login").permitAll()
                        .requestMatchers("/uploadImage").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/users/{id}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/auctions").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/auctions/{id}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/bets/{id}").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/bets").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/categories").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/lots").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/lots/{id}").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/lots").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/resultsOfAuctions/{id}").permitAll()
                        .anyRequest().hasAnyRole("admin", "owner"))
                .logout(logout -> logout.logoutUrl("/logout"))
                .httpBasic(Customizer.withDefaults())
                .build();
    }
}
