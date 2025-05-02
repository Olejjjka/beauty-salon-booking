package com.example.beauty_salon_booking.config;

import com.example.beauty_salon_booking.security.JwtAuthenticationFilter;
import com.example.beauty_salon_booking.security.JwtTokenProvider;
import com.example.beauty_salon_booking.security.MyUserDetailsService;
import com.example.beauty_salon_booking.services.RevokedTokenService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.client.RestTemplate;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final MyUserDetailsService userDetailsService;
    private final RevokedTokenService revokedTokenService;

    public SecurityConfig(JwtTokenProvider jwtTokenProvider, MyUserDetailsService userDetailsService, RevokedTokenService revokedTokenService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
        this.revokedTokenService = revokedTokenService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth
                        // Эндпоинты авторизации
                        .requestMatchers("/login").permitAll()
                                .requestMatchers("/register").permitAll()
                        .requestMatchers("/api/auth/register/**").permitAll()
                        .requestMatchers("/api/auth/login").permitAll()
                        .requestMatchers("/api/auth/logout").authenticated()

                        // CLIENT: доступ к просмотру мастеров и услуг
                        .requestMatchers(HttpMethod.GET, "/api/masters/*/appointments").hasRole("MASTER")
                        .requestMatchers(HttpMethod.GET, "/api/masters/**").hasAnyRole("CLIENT", "MASTER")
                        .requestMatchers(HttpMethod.GET, "/api/beauty-services/**").hasAnyRole("CLIENT", "MASTER")
                        .requestMatchers(HttpMethod.GET, "/api/appointments/**").hasAnyRole("CLIENT", "MASTER")

                        // CLIENT: доступ к своим данным
                        .requestMatchers("/api/clients/**").hasRole("CLIENT")

                        // CLIENT: доступ к созданию записи
                        .requestMatchers("/api/appointments/create").hasRole("CLIENT")

                        // MASTER: доступ к своим данным
                        .requestMatchers("/api/masters/**").hasRole("MASTER")

                        // MASTER: доступ к управлению услугами
                        .requestMatchers("/api/beauty-services/**").hasRole("MASTER")

                        // MASTER: доступ к управлению записями
                        .requestMatchers("/api/appointments/**").hasRole("MASTER")

                        // Запрет по умолчанию на всё остальное
                        //.anyRequest().authenticated()
                )

                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider, revokedTokenService), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}